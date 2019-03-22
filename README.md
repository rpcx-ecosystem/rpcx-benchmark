## Benchmark

**测试环境**
* CPU:    Intel(R) Xeon(R) CPU E5-2620 v2 @ 2.10GHz, 24 cores
* Memory: 16G
* OS:     Linux Server-3 2.6.32-358.el6.x86_64, CentOS 6.4
* Go:     1.7

测试代码client是通过protobuf编解码和server通讯的。
请求发送给server, server解码、更新两个字段、编码再发送给client，所以整个测试会包含客户端的编解码和服务器端的编解码。
消息的内容大约为581 byte, 在传输的过程中会增加少许的头信息，所以完整的消息大小在600字节左右。

测试用的proto文件如下：

```proto
syntax = "proto2";

package main;

option optimize_for = SPEED;


message BenchmarkMessage {
  required string field1 = 1;
  optional string field9 = 9;
  optional string field18 = 18;
  optional bool field80 = 80 [default=false];
  optional bool field81 = 81 [default=true];
  required int32 field2 = 2;
  required int32 field3 = 3;
  optional int32 field280 = 280;
  optional int32 field6 = 6 [default=0];
  optional int64 field22 = 22;
  optional string field4 = 4;
  repeated fixed64 field5 = 5;
  optional bool field59 = 59 [default=false];
  optional string field7 = 7;
  optional int32 field16 = 16;
  optional int32 field130 = 130 [default=0];
  optional bool field12 = 12 [default=true];
  optional bool field17 = 17 [default=true];
  optional bool field13 = 13 [default=true];
  optional bool field14 = 14 [default=true];
  optional int32 field104 = 104 [default=0];
  optional int32 field100 = 100 [default=0];
  optional int32 field101 = 101 [default=0];
  optional string field102 = 102;
  optional string field103 = 103;
  optional int32 field29 = 29 [default=0];
  optional bool field30 = 30 [default=false];
  optional int32 field60 = 60 [default=-1];
  optional int32 field271 = 271 [default=-1];
  optional int32 field272 = 272 [default=-1];
  optional int32 field150 = 150;
  optional int32 field23 = 23 [default=0];
  optional bool field24 = 24 [default=false];
  optional int32 field25 = 25 [default=0];
  optional bool field78 = 78;
  optional int32 field67 = 67 [default=0];
  optional int32 field68 = 68;
  optional int32 field128 = 128 [default=0];
  optional string field129 = 129 [default="xxxxxxxxxxxxxxxxxxxxx"];
  optional int32 field131 = 131 [default=0];
}
```



测试的并发client是 100, 1000,2000 and 5000。总请求数一百万。

**测试结果**

### 一个服务器和一个客户端，在同一台机器上

并发client|平均值(ms)|中位数(ms)|最大值(ms)|最小值(ms)|吞吐率(TPS)
-------------|-------------|-------------|-------------|-------------|-------------
100|0|0|17|0|164338
500|2|1|40|0|181126
1000|4|3|56|0|186219
2000|9|7|105|0|182815
5000|25|22|200|0|178858


可以看出平均值和中位数值相差不大，说明没有太多的离谱的延迟。

随着并发数的增大，服务器延迟也越长，这是正常的。

### 客户端在一台机器上，服务器在另外一台机器上

如果我们把客户端和服务器端的程序放在两台独立机器上，这两台机器的配置和上面的测试相同。测试结果如下：

并发client|平均值(ms)|中位数(ms)|最大值(ms)|最小值(ms)|吞吐率(TPS)
-------------|-------------|-------------|-------------|-------------|-------------
100|1|1|20|0|127975
500|5|1|4350|0|136407
1000|10|2|3233|0|155255
2000|17|2|9735|0|159438
5000|44|2|12788|0|161917 

因为与实际的网络传输，所以吞吐量没有上面的使用loopback的结果好，但是吞吐量已经不错了，接近每秒10万个事务处理。



### 客户端在一台机器上，两个服务器在另外两台机器上

如果部署成集群的模式，一个客户端，两个服务器端，测试结果如下：

并发client|平均值(ms)|中位数(ms)|最大值(ms)|最小值(ms)|吞吐率(TPS)
-------------|-------------|-------------|-------------|-------------|-------------
100|0|0|41|0|128932
500|3|2|273|0|150285
1000|5|5|621|0|150152
2000|10|7|288|0|159974
5000|23|12|629|0|155279


### 以下的代码是测试rpcx使用的序列化框架的性能。
```
[root@localhost rpcx]# go test -bench . -test.benchmem
PASS
BenchmarkNetRPC_gob-16            100000             18742 ns/op             321 B/op          9 allocs/op
BenchmarkNetRPC_jsonrpc-16        100000             21360 ns/op            1170 B/op         31 allocs/op
BenchmarkNetRPC_msgp-16           100000             18617 ns/op             776 B/op         35 allocs/op
BenchmarkRPCX_gob-16              100000             18718 ns/op             320 B/op          9 allocs/op
BenchmarkRPCX_json-16             100000             21238 ns/op            1170 B/op         31 allocs/op
BenchmarkRPCX_msgp-16             100000             18635 ns/op             776 B/op         35 allocs/op
BenchmarkRPCX_gencodec-16         100000             18454 ns/op            4485 B/op         17 allocs/op
BenchmarkRPCX_protobuf-16         100000             17234 ns/op             733 B/op         13 allocs/op
```


## 和gRPC比较
[gRPC](https://github.com/grpc/grpc-go) 是Google开发的一个RPC框架，支持多种编程语言。

我对gRPC和rpcx进行了相同的测试，得到了相应的测试结果。结果显示rpcx的性能要远远好于gRPC。
gRPC的优势之一就是随着并发数的增大，吞吐率比较稳定，而rpcx随着并发数的增加性能有所下降，但总体吞吐率还是要高于gRPC的。

rpcx的测试结果如上，下面事gRPC的测试结果。

### 一个服务器和一个客户端，在同一台机器上

并发client|平均值(ms)|中位数(ms)|最大值(ms)|最小值(ms)|吞吐率(TPS)
-------------|-------------|-------------|-------------|-------------|-------------
100|0|0|20|0|55561
500|7|6|59|0|62593
1000|14|12|103|0|65329
2000|28|24|163|0|67033
5000|71|64|380|0|63803

![](_documents/images/rpcx-grpc-1.png)

### 客户端在一台机器上，服务器在另外一台机器上

并发client|平均值(ms)|中位数(ms)|最大值(ms)|最小值(ms)|吞吐率(TPS)
-------------|-------------|-------------|-------------|-------------|-------------
100|1|0|21|0|68250
500|5|1|3059|0|78486
1000|10|1|6274|0|79980
2000|19|1|9736|0|58129
5000|43|2|14224|0|44724

![](_documents/images/rpcx-grpc-2.png)

### 客户端在一台机器上，两个服务器在另外两台机器上

并发client|平均值(ms)|中位数(ms)|最大值(ms)|最小值(ms)|吞吐率(TPS)
-------------|-------------|-------------|-------------|-------------|-------------
100|1|0|19|0|88082
500|4|1|1461|0|90334
1000|9|1|6315|0|62305
2000|17|1|9736|0|44487
5000|38|1|25087|0|33198

![](_documents/images/rpcx-grpc-2.png)


### !! Latest Benchmark !!

**updated**:  2019-03-02


`TPS`:吞吐率
`mean`: 单个请求平均耗时
`max`: 单个请求的最大耗时
`min`: 单个请求的最小耗时
`p99`: 99%的请求单个耗时

####  并发数 256

tarsgo client启动命令： `CONC=256 TOTAL=1000000 ./mclient --config=benchmark.conf`

|         |TPS    |   mean  |   max  |  min   | p99 |
|---|---|---|---|---|---|
|tarsgo |  42985  |   5ms  |    600ms | 0ms  |  36ms|
|rpcx   |  122166  |  2ms   |   31ms |  0ms  |  18ms|
|grpc   |  130642  |  1ms  |    16ms  | 0ms   | 9ms|

##  并发数 512
tarsgo client启动命令： `CONC=512 TOTAL=1000000 ./mclient --config=benchmark.conf`

|         |TPS    |   mean  |   max  |  min   | p99 |
|---|---|---|---|---|---|
|tarsgo |  39485  |   12ms   |  591ms | 0ms  |  507ms|
|rpcx   |  142177  |  3ms    |  63ms  | 0ms  |  28ms|
|grpc  |   123099 |   4ms    |  46ms  | 0ms  |  16ms|

## 并发数 1024
tarsgo client启动命令： `CONC=1024 TOTAL=1000000 ./mclient --config=benchmark.conf`

|         |TPS    |   mean  |   max  |  min   | p99 |
|---|---|---|---|---|---|
|tarsgo |  40944 |    24ms  |   809m |  0ms  |  612ms|
|rpcx  |   134439  |  6ms   |   79ms  | 0ms  |  49ms|
|grpc |    115607 |   8ms   |   115ms | 0ms  |  33ms|