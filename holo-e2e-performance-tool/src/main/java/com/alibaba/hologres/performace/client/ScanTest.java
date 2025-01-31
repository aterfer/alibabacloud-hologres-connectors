package com.alibaba.hologres.performace.client;


import com.alibaba.hologres.client.*;
import com.alibaba.hologres.client.exception.HoloClientException;
import com.alibaba.hologres.client.impl.ConnectionHolder;
import com.alibaba.hologres.client.impl.ExecutionPool;
import com.alibaba.hologres.client.impl.util.ConnectionUtil;
import com.alibaba.hologres.client.model.RecordScanner;
import com.alibaba.hologres.client.model.TableName;
import com.alibaba.hologres.client.model.TableSchema;
import com.alibaba.hologres.client.utils.ConfLoader;
import com.alibaba.hologres.client.utils.Metrics;
import com.alibaba.hologres.com.codahale.metrics.Histogram;
import com.alibaba.hologres.com.codahale.metrics.Meter;
import com.alibaba.hologres.performace.params.ParamsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ScanTest {
  public static final Logger LOG = LoggerFactory.getLogger(ScanTest.class);
  public static final String METRICS_SCAN_PERF_QPS = "scan_perf_qps";
  public static final String METRICS_SCAN_PERF_LATENCY = "scan_perf_latency";

  private String confName;
  private long targetTime;
  private AtomicInteger singleExecutionPoolJobSize;
  ScanTestConf conf = new ScanTestConf();

  ParamsProvider provider;

  public void run(String confName) throws Exception {
    LOG.info("confName:{}", confName);
    this.confName = confName;
    ConfLoader.load(confName, "scan.", conf);

    HoloConfig config = new HoloConfig();
    ConfLoader.load(confName, "holoClient.", config);
    provider = new ParamsProvider(conf.keyRangeParams);
    Reporter reporter = new Reporter(confName);
    ConnectionHolder.addPreSql("set hg_experimental_enable_fixed_dispatcher_for_scan = on");
    try (HoloClient client = new HoloClient(config)) {
      TableSchema schema = client.sql(conn -> {
        if (conf.vacuumTableBeforeRun) {
          SqlUtil.vaccumTable(conn, conf.tableName);
        }
        reporter.start(ConnectionUtil.getHoloVersion(conn));
        return ConnectionUtil.getTableSchema(conn, TableName.valueOf(conf.tableName));
      }).get();
      if (schema == null) {
        throw new Exception("table not found");
      } else if (schema.getDistributionKeys().length != provider.size()) {
        throw new Exception(
            "table has " + schema.getDistributionKeys().length + " distribution keys, but test.params only has "
                + provider.size() + " columns");
      }
    }
    if (conf.singleExecutionPool) {
      singleExecutionPoolJobSize = new AtomicInteger(conf.threadSize);
    } else {
      singleExecutionPoolJobSize = new AtomicInteger(0);
    }
    targetTime = System.currentTimeMillis() + conf.testTime;
    Thread[] threads = new Thread[conf.threadSize];
    Metrics.startSlf4jReporter(60L, TimeUnit.SECONDS);
    for (int i = 0; i < threads.length; ++i) {
      threads[i] = new Thread(new Job(i));
      threads[i].start();
    }

    for (int i = 0; i < threads.length; ++i) {
      threads[i].join();
    }

    Metrics.reporter().report();
    {
      Meter meter = Metrics.registry().meter(METRICS_SCAN_PERF_QPS);
      Histogram hist = Metrics.registry().histogram(METRICS_SCAN_PERF_LATENCY);
      reporter.report(meter.getCount(), meter.getOneMinuteRate(), meter.getFiveMinuteRate(),
          meter.getFifteenMinuteRate(), hist.getSnapshot().getMean(),
          hist.getSnapshot().get99thPercentile(), hist.getSnapshot().get999thPercentile());
    }

    if (conf.deleteTableAfterDone) {
      SqlUtil.dropTableByHoloClient(config, conf.tableName);
    }
  }


  class Job implements Runnable {
    int id;

    public Job(int id) {
      this.id = id;
    }

    @Override
    public void run() {
      HoloConfig poolConf = new HoloConfig();
      HoloConfig clientConf = new HoloConfig();
      try {
        ConfLoader.load(confName, "holoClient.", poolConf);
        ConfLoader.load(confName, "holoClient.", clientConf);
        ConfLoader.load(confName, "pool.", poolConf);

        String executionPoolName = "hello";
        if (!conf.singleExecutionPool) {
          executionPoolName += "_" + id;
        }
        Meter meter = Metrics.registry().meter(METRICS_SCAN_PERF_QPS);
        Histogram hist = Metrics.registry().histogram(METRICS_SCAN_PERF_LATENCY);
        ExecutionPool pool = ExecutionPool.buildOrGet(executionPoolName, poolConf, true, poolConf.isUseFixedFe());
        try (HoloClient client = new HoloClient(clientConf)) {
          if (poolConf.isUseFixedFe()) {
            client.setFixedPool(pool);
          } else {
            client.setPool(pool);
          }
          int i = 0;
          CompletableFuture<Void> future = null;
          while (true) {
            if (++i % 1000 == 0) {
              if (System.currentTimeMillis() > targetTime) {
                break;
              }
            }
            TableSchema schema = client.getTableSchema(conf.tableName);
            Scan.Builder scanBuilder = Scan.newBuilder(schema).setSortKeys(SortKeys.NONE);
            for (int j = 0; j < schema.getDistributionKeys().length; ++j) {
              scanBuilder.addEqualFilter(schema.getDistributionKeys()[j], provider.get(j));
            }
            Scan scan = scanBuilder.build();

            long startNano = System.nanoTime();
            if (conf.async) {
              future = client.asyncScan(scan).thenAccept(rs -> {
                meter.mark();
                long endNano = System.nanoTime();
                hist.update((endNano - startNano) / 1000000L);
              });
            } else {
              try (RecordScanner rs =client.scan(scan)){}
              meter.mark();
              long endNano = System.nanoTime();
              hist.update((endNano - startNano) / 1000000L);
            }
          }
          if (conf.async && future != null) {
            future.get();
          }
        } finally {
          if (conf.singleExecutionPool && singleExecutionPoolJobSize.decrementAndGet() == 0 ||(!conf.singleExecutionPool)) {
            pool.close();
          }
        }

      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}

class ScanTestConf {
  public int threadSize = 10;
  public long testTime = 600000;
  public String tableName = "holo_perf";
  public boolean singleExecutionPool = true;
  public boolean vacuumTableBeforeRun = true;
  public String keyRangeParams;
  public boolean async;
  public boolean deleteTableAfterDone = false;
}