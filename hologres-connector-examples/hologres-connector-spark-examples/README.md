# Spark-connector-Examples
在Examples模块下，有如下几个示例：

* 1.SparkDataFrameToHoloExample

  一个使用java实现的通过Holo Spark connector将数据写入至Hologres的应用
  使用scala脚本实现的例子可以参考 hologres-connector-spark-2.x/README.md

运行Example 1

### 编译
运行```mvn package -DskipTests```

### 创建Hologres结果表用于接收数据
在自己的Hologres实例，创建结果表:

```create table sink_table(user_id bigint, user_name text, price decimal(38,2), sale_timestamp timestamptz);```

### 提交Spark作业
当前的Spark example默认使用Spark 2.4版本进行编译，测试的时候请使用Spark 2.x版本实例

```
spark-submit  --class com.alibaba.hologres.spark.example.SparkDataFrameToHoloExample --jars target/hologres-connector-spark-examples-1.0-SNAPSHOT-jar-with-dependencies.jar target/hologres-connector-spark-examples-1.0-SNAPSHOT-jar-with-dependencies.jar --endpoint ${ip:port} --username ${user_name} --password ${password} --database {database} --tablename sink_table
```