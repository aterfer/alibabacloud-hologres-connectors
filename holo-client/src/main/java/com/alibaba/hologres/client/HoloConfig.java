/*
 * Copyright (c) 2020. Alibaba Group Holding Limited
 */

package com.alibaba.hologres.client;

import com.alibaba.hologres.client.model.WriteFailStrategy;
import com.alibaba.hologres.client.model.WriteMode;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * config class for holo-client.
 */
public class HoloConfig implements Serializable {

	public static final int DEFAULT_BATCH_SIZE = 512;
	public static final long DEFAULT_BATCH_BYTE_SIZE = 2L * 1024L * 1024L;
	public static final WriteMode DEFAULT_WRITE_MODE = WriteMode.INSERT_OR_REPLACE;

	public static final int DEFAULT_READ_BATCH_SIZE = 128;
	public static final int DEFAULT_READ_QUEUE = 256;

	//----------------------------write conf--------------------------------------------
	/**
	 * 在AsyncCommit为true，调用put方法时，当记录数>=writeBatchSize 或 总记录字节数数>=writeBatchByteSize.
	 * 调用flush进行批量提交.
	 * 默认为512
	 *
	 * @HasGetter
	 * @HasSetter
	 */
	int writeBatchSize = DEFAULT_BATCH_SIZE;

	/**
	 * 在AsyncCommit为true，调用put方法时，当记录数>=writeBatchSize 或 总记录字节数数>=writeBatchByteSize.
	 * 调用flush进行List批量提交.
	 * 默认为2MB
	 *
	 * @HasGetter
	 * @HasSetter
	 */
	long writeBatchByteSize = DEFAULT_BATCH_BYTE_SIZE;

	/**
	 * 所有表攒批总共的最大batchSize
	 * 调用flush进行List批量提交.
	 * 默认为20MB
	 *
	 * @HasGetter
	 * @HasSetter
	 */
	long writeBatchTotalByteSize = DEFAULT_BATCH_BYTE_SIZE * 10;

	/**
	 * 当INSERT目标表为有主键的表时采用不同策略.
	 * INSERT_OR_IGNORE 当主键冲突时，不写入
	 * INSERT_OR_UPDATE 当主键冲突时，更新相应列
	 * INSERT_OR_REPLACE当主键冲突时，更新所有列
	 *
	 * @HasGetter
	 * @HasSetter
	 */
	WriteMode writeMode = DEFAULT_WRITE_MODE;

	/**
	 * 启用后，当put分区表时，若分区不存在将自动创建，默认false.
	 *
	 * @HasGetter
	 * @HasSetter
	 */
	boolean dynamicPartition = false;

	/**
	 * 在AsyncCommit为true，当记录数>=writeBatchSize 或 总记录字节数数>=writeBatchByteSize 或距离上次flush超过writeMaxIntervalMs毫秒 调用flush进行提交     * 默认为100MB.
	 *
	 * @HasGetter
	 * @HasSetter
	 */
	long writeMaxIntervalMs = 10000L;

	/**
	 * 当INSERT失败采取的策略.
	 * TRY_ONE_BY_NE
	 * NONE
	 *
	 * @HasGetter
	 * @HasSetter
	 */
	WriteFailStrategy writeFailStrategy = WriteFailStrategy.TRY_ONE_BY_ONE;

	/**
	 * put操作的并发数.
	 *
	 * @HasGetter
	 * @HasSetter
	 */
	int writeThreadSize = 1;

	/**
	 * 每个write线程队列缓冲区大小.
	 *
	 * @HasGetter
	 * @HasSetter
	 */
	int writeBufferSize = DEFAULT_BATCH_SIZE * 5 / 4;

	/**
	 * 当将Number写入Date/timestamp/timestamptz列时，将number视作距离1970-01-01 00:00:00 +00:00的毫秒数.
	 *
	 * @HasGetter
	 * @HasSetter
	 */
	boolean inputNumberAsEpochMsForDatetimeColumn = false;

	/**
	 * 当将Number写入Date/timestamp/timestamptz列时，将number视作距离1970-01-01 00:00:00 +00:00的毫秒数.
	 *
	 * @HasGetter
	 * @HasSetter
	 */
	boolean inputStringAsEpochMsForDatetimeColumn = false;

	/**
	 * flush操作超时等待时间.
	 *
	 * @HasGetter
	 * @HasSetter
	 */
	long flushMaxWaitMs = 60000L;

	/**
	 * put操作超时等待时间.
	 *
	 * @HasGetter
	 * @HasSetter
	 */
	long putMaxWaitMs = 10000L;

	/**
	 * 启用时，not null且未在表上设置default的字段传入null时，将转为默认值.
	 * String
	 *
	 * @HasGetter
	 * @HasSetter
	 */
	boolean enableDefaultForNotNullColumn = true;

	/**
	 * defaultTimestamp.
	 * String
	 *
	 * @HasGetter
	 * @HasSetter
	 */
	String defaultTimestampText = null;

	/**
	 * 写入shard数重计算的间隔.
	 * long
	 *
	 * @HasGetter
	 * @HasSetter
	 */
	long writerShardCountResizeIntervalMs = 30000L;

	/**
	 * 开启将text列value中的\u0000替换为"".
	 * boolean
	 *
	 * @HasGetter
	 * @HasSetter
	 */
	boolean removeU0000InTextColumnValue = true;

	/**
	 * 批量delete时重写sql.
	 * boolean
	 *
	 * @HasGetter
	 * @HasSetter
	 */
	boolean reWriteBatchedDeletes = true;

	/**
	 * INSERT/DELETE rewrite模式下，单条sql的最大batch大小.
	 * boolean
	 *
	 * @HasGetter
	 * @HasSetter
	 */
	int rewriteSqlMaxBatchSize = 128;

	/**
	 * 全局flush的时间间隔.
	 * boolean
	 *
	 * @HasGetter
	 * @HasSetter
	 */
	long forceFlushInterval = -1L;

	/**
	 * 最大shard数.
	 * boolean
	 *
	 * @HasGetter
	 * @HasSetter
	 */
	int maxShardCount = -1;

	/**
	 * CopyIn时，InputStream和网络OutputStream之间的交互数据的buffer大小.
	 * int
	 *
	 * @HasGetter
	 * @HasSetter
	 */
	int copyInBufferSize = 65536;

	/**
	 * 多久打印一次写入数据采样.
	 * int
	 *
	 * @HasGetter
	 * @HasSetter
	 */
	long recordSampleInterval = -1L;

	//--------------------------read conf-------------------------------------------------
	/**
	 * 最多一次将readBatchSize条Get请求合并提交，默认128.
	 *
	 * @HasGetter
	 * @HasSetter
	 */
	int readBatchSize = DEFAULT_READ_BATCH_SIZE;
	/**
	 * get请求缓冲池大小，默认256.
	 *
	 * @HasGetter
	 * @HasSetter
	 */
	int readBatchQueueSize = DEFAULT_READ_QUEUE;

	/**
	 * get操作的并发数.
	 *
	 * @HasGetter
	 * @HasSetter
	 */
	int readThreadSize = 1;

	//--------------------------scan conf-------------------------------------------------
	/**
	 * scan每次fetch的大小.
	 *
	 * @HasGetter
	 * @HasSetter
	 */
	int scanFetchSize = 2000;
	/**
	 * scan的超时时间.
	 *
	 * @HasGetter
	 * @HasSetter
	 */
	int scanTimeoutSeconds = 60;

	//--------------------------binlog read conf-------------------------------------------------
	/**
	 * 一次读取 binlogReadBatchSize 条 Binlog 数据.
	 *
	 * @HasGetter
	 * @HasSetter
	 */
	int binlogReadBatchSize = 1024;

	/**
	 * 从 binlogReadStartLsn 开始消费 Binlog 数据.
	 *
	 * @HasGetter
	 * @HasSetter
	 */
	long binlogReadStartLsn = -1;

	/**
	 * 从 binlogReadStartTime 开始消费 Binlog 数据.
	 *
	 * @HasGetter
	 * @HasSetter
	 */
	String binlogReadStartTime = "1970-01-01 07:59:59+08";

	/**
	 * binlogRead 的超时时间.
	 *
	 * @HasGetter
	 * @HasSetter
	 */
	int binlogReadTimeoutSeconds = 60;

	/**
	 * 是否需要忽略Delete类型的Binlog.
	 *
	 * @HasGetter
	 * @HasSetter
	 */
	boolean binlogIgnoreDelete = false;

	/**
	 * 是否需要忽略BeforeUpdate类型的Binlog.
	 *
	 * @HasGetter
	 * @HasSetter
	 */
	boolean binlogIgnoreBeforeUpdate = false;

	//---------------------------conn conf------------------------------------------
	/**
	 * 请求重试次数，默认3.
	 * 这个名字应该叫做maxTryCount，而不是retryCount，设为1其实是不会retry的
	 *
	 * @HasGetter
	 * @HasSetter
	 */
	int retryCount = 3;

	/**
	 * 每次重试等待时间为  当前重试次数*retrySleepMs + retrySleepInitMs.
	 *
	 * @HasGetter
	 * @HasSetter
	 */
	long retrySleepStepMs = 10000L;

	/**
	 * 每次重试等待时间为  当前重试次数*retrySleepMs + retrySleepInitMs.
	 *
	 * @HasGetter
	 * @HasSetter
	 */
	long retrySleepInitMs = 1000L;

	/**
	 * 是否在初始化HoloClient时就检测连接是否可用（正常连接是lazy的，只有在get和put是才会初始化连接）.
	 *
	 * @HasGetter
	 * @HasSetter
	 */
	boolean failFastWhenInit = true;

	/**
	 * 每个get和put的后台连接在空闲超过connectionMaxIdleMs后将被释放(再次使用时会自动重新连接).
	 *
	 * @HasGetter
	 * @HasSetter
	 */
	long connectionMaxIdleMs = 60000L;

	/**
	 * meta信息缓存时间(ms).
	 *
	 * @HasGetter
	 * @HasSetter
	 */
	long metaCacheTTL = 60000L;

	/**
	 * meta缓存剩余时间低于 metaCacheTTL/metaAutoRefreshFactor 将被自动刷新.
	 *
	 * @HasGetter
	 * @HasSetter
	 */
	int metaAutoRefreshFactor = 4;

	/**
	 * 使用在client端实现的动态分区，替代jdbc driver动态分区实现.
	 *
	 * @HasGetter
	 * @HasSetter
	 */
	boolean enableClientDynamicPartition = true;

	/**
	 * 执行hg_internal_refresh_meta的默认超时时间(单位为秒).
	 * 若<=0则不执行
	 *
	 * @HasGetter
	 * @HasSetter
	 */
	int refreshMetaTimeout = 10;

	/**
	 * connection建立后是否执行hg_internal_refresh_meta.
	 *
	 * @HasGetter
	 * @HasSetter
	 */
	boolean refreshMetaAfterConnectionCreated = true;

	/**
	 * 获取tableSchema前是否执行hg_internal_refresh_meta.
	 *
	 * @HasGetter
	 * @HasSetter
	 */
	boolean refreshMetaBeforeGetTableSchema = true;

	//------------------------endpoint conf--------------------------------------------
	/**
	 * 顾名思义，jdbcUrl.
	 *
	 * @HasGetter
	 * @HasSetter
	 */
	String jdbcUrl;

	/**
	 * jdbcUrl，必填.
	 *
	 * @HasGetter
	 * @HasSetter
	 */
	String username;
	/**
	 * jdbcUrl，必填.
	 *
	 * @HasGetter
	 * @HasSetter
	 */
	String password;

	String appName = "holo-client";

	public WriteMode getWriteMode() {
		return writeMode;
	}

	public void setWriteMode(WriteMode writeMode) {
		this.writeMode = writeMode;
	}

	public String getJdbcUrl() {
		return jdbcUrl;
	}

	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getWriteBatchSize() {
		return writeBatchSize;
	}

	public void setWriteBatchSize(int batchSize) {
		this.writeBatchSize = batchSize;
	}

	public long getWriteBatchByteSize() {
		return writeBatchByteSize;
	}

	public void setWriteBatchByteSize(long batchByteSize) {
		this.writeBatchByteSize = batchByteSize;
	}

	public int getReadBatchSize() {
		return readBatchSize;
	}

	public void setReadBatchSize(int readBatchSize) {
		this.readBatchSize = readBatchSize;
	}

	public int getReadBatchQueueSize() {
		return readBatchQueueSize;
	}

	public void setReadBatchQueueSize(int readBatchQueueSize) {
		this.readBatchQueueSize = readBatchQueueSize;
	}

	public boolean isFailFastWhenInit() {
		return failFastWhenInit;
	}

	public void setFailFastWhenInit(boolean failFastWhenInit) {
		this.failFastWhenInit = failFastWhenInit;
	}

	public int getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	public long getRetrySleepStepMs() {
		return retrySleepStepMs;
	}

	public void setRetrySleepStepMs(long retrySleepStepMs) {
		this.retrySleepStepMs = retrySleepStepMs;
	}

	public long getRetrySleepInitMs() {
		return retrySleepInitMs;
	}

	public void setRetrySleepInitMs(long retrySleepInitMs) {
		this.retrySleepInitMs = retrySleepInitMs;
	}

	public boolean isDynamicPartition() {
		return dynamicPartition;
	}

	public void setDynamicPartition(boolean dynamicPartition) {
		this.dynamicPartition = dynamicPartition;
	}

	public long getWriteMaxIntervalMs() {
		return writeMaxIntervalMs;
	}

	public void setWriteMaxIntervalMs(long writeMaxIntervalMs) {
		this.writeMaxIntervalMs = writeMaxIntervalMs;
	}

	public WriteFailStrategy getWriteFailStrategy() {
		return writeFailStrategy;
	}

	public void setWriteFailStrategy(WriteFailStrategy writeFailStrategy) {
		this.writeFailStrategy = writeFailStrategy;
	}

	public int getReadThreadSize() {
		return readThreadSize;
	}

	public void setReadThreadSize(int readThreadSize) {
		this.readThreadSize = readThreadSize;
	}

	public long getConnectionMaxIdleMs() {
		return connectionMaxIdleMs;
	}

	public void setConnectionMaxIdleMs(long connectionMaxIdleMs) {
		this.connectionMaxIdleMs = connectionMaxIdleMs;
	}

	public int getWriteThreadSize() {
		return writeThreadSize;
	}

	public void setWriteThreadSize(int writeThreadSize) {
		this.writeThreadSize = writeThreadSize;
	}

	@Deprecated
	public int getWriteBufferSize() {
		return writeBufferSize;
	}

	@Deprecated
	public void setWriteBufferSize(int writeBufferSize) {
		this.writeBufferSize = writeBufferSize;
	}

	public boolean isInputNumberAsEpochMsForDatetimeColumn() {
		return inputNumberAsEpochMsForDatetimeColumn;
	}

	public void setInputNumberAsEpochMsForDatetimeColumn(boolean inputNumberAsEpochMsForDatetimeColumn) {
		this.inputNumberAsEpochMsForDatetimeColumn = inputNumberAsEpochMsForDatetimeColumn;
	}

	public long getFlushMaxWaitMs() {
		return flushMaxWaitMs;
	}

	public void setFlushMaxWaitMs(long flushMaxWaitMs) {
		this.flushMaxWaitMs = flushMaxWaitMs;
	}

	public long getMetaCacheTTL() {
		return metaCacheTTL;
	}

	public void setMetaCacheTTL(long metaCacheTTL) {
		this.metaCacheTTL = metaCacheTTL;
	}

	public boolean isInputStringAsEpochMsForDatetimeColumn() {
		return inputStringAsEpochMsForDatetimeColumn;
	}

	public void setInputStringAsEpochMsForDatetimeColumn(boolean inputStringAsEpochMsForDatetimeColumn) {
		this.inputStringAsEpochMsForDatetimeColumn = inputStringAsEpochMsForDatetimeColumn;
	}

	public boolean isEnableDefaultForNotNullColumn() {
		return enableDefaultForNotNullColumn;
	}

	public void setEnableDefaultForNotNullColumn(boolean enableDefaultForNotNullColumn) {
		this.enableDefaultForNotNullColumn = enableDefaultForNotNullColumn;
	}

	public String getDefaultTimestampText() {
		return defaultTimestampText;
	}

	public void setDefaultTimestampText(String defaultTimestampText) {
		this.defaultTimestampText = defaultTimestampText;
	}

	public long getPutMaxWaitMs() {
		return putMaxWaitMs;
	}

	public void setPutMaxWaitMs(long putMaxWaitMs) {
		this.putMaxWaitMs = putMaxWaitMs;
	}

	public long getWriteBatchTotalByteSize() {
		return writeBatchTotalByteSize;
	}

	public void setWriteBatchTotalByteSize(long writeBatchTotalByteSize) {
		this.writeBatchTotalByteSize = writeBatchTotalByteSize;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public int getScanFetchSize() {
		return scanFetchSize;
	}

	public int getScanTimeoutSeconds() {
		return scanTimeoutSeconds;
	}

	public void setScanFetchSize(int scanFetchSize) {
		this.scanFetchSize = scanFetchSize;
	}

	public void setScanTimeoutSeconds(int scanTimeoutSeconds) {
		this.scanTimeoutSeconds = scanTimeoutSeconds;
	}

	public int getMetaAutoRefreshFactor() {
		return metaAutoRefreshFactor;
	}

	public void setMetaAutoRefreshFactor(int metaAutoRefreshFactor) {
		this.metaAutoRefreshFactor = metaAutoRefreshFactor;
	}

	public long getWriterShardCountResizeIntervalMs() {
		return writerShardCountResizeIntervalMs;
	}

	public void setWriterShardCountResizeIntervalMs(long writerShardCountResizeIntervalMs) {
		this.writerShardCountResizeIntervalMs = writerShardCountResizeIntervalMs;
	}

	public boolean isRemoveU0000InTextColumnValue() {
		return removeU0000InTextColumnValue;
	}

	public void setRemoveU0000InTextColumnValue(boolean removeU0000InTextColumnValue) {
		this.removeU0000InTextColumnValue = removeU0000InTextColumnValue;
	}

	public boolean isReWriteBatchedDeletes() {
		return reWriteBatchedDeletes;
	}

	public void setReWriteBatchedDeletes(boolean reWriteBatchedDeletes) {
		this.reWriteBatchedDeletes = reWriteBatchedDeletes;
	}

	public boolean isEnableClientDynamicPartition() {
		return enableClientDynamicPartition;
	}

	public void setEnableClientDynamicPartition(boolean enableClientDynamicPartition) {
		this.enableClientDynamicPartition = enableClientDynamicPartition;
	}

	public int getRefreshMetaTimeout() {
		return refreshMetaTimeout;
	}

	public void setRefreshMetaTimeout(int refreshMetaTimeout) {
		this.refreshMetaTimeout = refreshMetaTimeout;
	}

	public boolean isRefreshMetaAfterConnectionCreated() {
		return refreshMetaAfterConnectionCreated;
	}

	public void setRefreshMetaAfterConnectionCreated(boolean refreshMetaAfterConnectionCreated) {
		this.refreshMetaAfterConnectionCreated = refreshMetaAfterConnectionCreated;
	}

	public boolean isRefreshMetaBeforeGetTableSchema() {
		return refreshMetaBeforeGetTableSchema;
	}

	public void setRefreshMetaBeforeGetTableSchema(boolean refreshMetaBeforeGetTableSchema) {
		this.refreshMetaBeforeGetTableSchema = refreshMetaBeforeGetTableSchema;
	}

	public int getRewriteSqlMaxBatchSize() {
		return rewriteSqlMaxBatchSize;
	}

	public void setRewriteSqlMaxBatchSize(int rewriteSqlMaxBatchSize) {
		this.rewriteSqlMaxBatchSize = rewriteSqlMaxBatchSize;
	}

	public long getForceFlushInterval() {
		return forceFlushInterval;
	}

	public void setForceFlushInterval(long forceFlushInterval) {
		this.forceFlushInterval = forceFlushInterval;
	}

	public int getMaxShardCount() {
		return maxShardCount;
	}

	public void setMaxShardCount(int maxShardCount) {
		this.maxShardCount = maxShardCount;
	}

	public int getCopyInBufferSize() {
		return copyInBufferSize;
	}

	public void setCopyInBufferSize(int copyInBufferSize) {
		this.copyInBufferSize = copyInBufferSize;
	}

	public long getRecordSampleInterval() {
		return recordSampleInterval;
	}

	public void setRecordSampleInterval(long recordSampleInterval) {
		this.recordSampleInterval = recordSampleInterval;
	}

	public int getBinlogReadBatchSize() {
		return binlogReadBatchSize;
	}

	public void setBinlogReadBatchSize(int binlogReadBatchSize) {
		this.binlogReadBatchSize = binlogReadBatchSize;
	}

	public long getBinlogReadStartLsn() {
		return binlogReadStartLsn;
	}

	public void setBinlogReadStartLsn(long binlogReadStartLsn) {
		this.binlogReadStartLsn = binlogReadStartLsn;
	}

	public String getBinlogReadStartTime() {
		return binlogReadStartTime;
	}

	public void setBinlogReadStartTime(String binlogReadStartTime) {
		this.binlogReadStartTime = binlogReadStartTime;
	}

	public int getBinlogReadTimeoutSeconds() {
		return binlogReadTimeoutSeconds;
	}

	public void setBinlogReadTimeoutSeconds(int binlogReadTimeoutSeconds) {
		this.binlogReadTimeoutSeconds = binlogReadTimeoutSeconds;
	}

	public boolean getBinlogIgnoreDelete() {
		return binlogIgnoreDelete;
	}

	public void setBinlogIgnoreDelete(boolean binlogIgnoreDelete) {
		this.binlogIgnoreDelete = binlogIgnoreDelete;
	}

	public boolean getBinlogIgnoreBeforeUpdate() {
		return binlogIgnoreBeforeUpdate;
	}

	public void setBinlogIgnoreBeforeUpdate(boolean binlogIgnoreBeforeUpdate) {
		this.binlogIgnoreBeforeUpdate = binlogIgnoreBeforeUpdate;
	}

	public static String[] getPropertyKeys() {
		Field[] fields = HoloConfig.class.getDeclaredFields();
		String[] propertyKeys = new String[fields.length];
		int index = 0;
		for (Field field : fields) {
			propertyKeys[index++] = field.getName();
		}
		return propertyKeys;
	}
}