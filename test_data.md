## 2020 Spring 各框架的测试结果

### dubbo

**服务端:**

```sh
ulimit -n 1000000
nohup java -jar dubbo-bench-provider-2.7.5.jar zookeeper://10.222.77.227:2181 &
```

**客户端:**
```sh
ulimit -n 1000000
nohup java -jar dubbo-bench-consumer-2.7.5.jar 100 10000000 zookeeper://10.222.77.227:2181 > result.log 2>&1 &
```


#### c=100, n=10000000

```yaml
throughput  (TPS)    : 10590
mean: 1.323284
median: 1.000000
max: 3105.000000
min: 0.000000
99P: 2.000000
```

#### c=1000, n=10000000

```yaml
throughput  (TPS)    : 9577
mean: 14.678306
median: 12.000000
max: 3249.000000
min: 0.000000
99P: 14.000000
```

### go 标准库

#### c=100,n=10000000

```sh
took 64733.274973 ms for 10000000 requests
2020/01/20 15:27:09 sent     requests    : 10000000
2020/01/20 15:27:09 received requests    : 10000000
2020/01/20 15:27:09 received requests_OK : 10000000
2020/01/20 15:27:09 throughput  (TPS)    : 154480
2020/01/20 15:27:09 mean: 611249 ns, median: 511101 ns, max: 8589768 ns, min: 75924 ns, p99.9: 2464773 ns
2020/01/20 15:27:09 mean: 0 ms, median: 0 ms, max: 8 ms, min: 0 ms, p99: 2 ms
```

#### c=1000,n=10000000

```
 took 55479.341019 ms for 10000000 requests
2020/01/20 15:30:50 sent     requests    : 10000000
2020/01/20 15:30:50 received requests    : 10000000
2020/01/20 15:30:50 received requests_OK : 10000000
2020/01/20 15:30:50 throughput  (TPS)    : 180247
2020/01/20 15:30:50 mean: 5206044 ns, median: 5163272 ns, max: 29196701 ns, min: 86510 ns, p99.9: 13032492 ns
2020/01/20 15:30:50 mean: 5 ms, median: 5 ms, max: 29 ms, min: 0 ms, p99: 13 ms
```

### grpc

#### c=100,n=10000000

```
took 91803 ms for 10000000 requests
2020/01/20 15:45:00 grpc_mclient.go:109: INFO : sent     requests    : 10000000
2020/01/20 15:45:00 grpc_mclient.go:110: INFO : received requests    : 10000000
2020/01/20 15:45:00 grpc_mclient.go:111: INFO : received requests_OK : 10000000
2020/01/20 15:45:00 grpc_mclient.go:112: INFO : throughput  (TPS)    : 108928
2020/01/20 15:45:00 grpc_mclient.go:113: INFO : mean: 857796 ns, median: 672012 ns, max: 43451467 ns, min: 125892 ns, p99: 6719222 ns
2020/01/20 15:45:00 grpc_mclient.go:114: INFO : mean: 0 ms, median: 0 ms, max: 43 ms, min: 0 ms, p99: 6 ms
```

#### c=1000,n=10000000

```
took 75736 ms for 10000000 requests
^@2020/01/20 15:46:44 grpc_mclient.go:109: INFO : sent     requests    : 10000000
2020/01/20 15:46:44 grpc_mclient.go:110: INFO : received requests    : 10000000
2020/01/20 15:46:44 grpc_mclient.go:111: INFO : received requests_OK : 10000000
2020/01/20 15:46:44 grpc_mclient.go:112: INFO : throughput  (TPS)    : 132037
2020/01/20 15:46:44 grpc_mclient.go:113: INFO : mean: 7148157 ns, median: 5975982 ns, max: 133300722 ns, min: 137300 ns, p99: 58021151 ns
2020/01/20 15:46:44 grpc_mclient.go:114: INFO : mean: 7 ms, median: 5 ms, max: 133 ms, min: 0 ms, p99: 58 ms
```


### rpcx

#### c=100,n=10000000

```
took 99082 ms for 10000000 requests
2020/01/20 16:02:14 rpcx_mclient.go:121: INFO : sent     requests    : 10000000
2020/01/20 16:02:14 rpcx_mclient.go:122: INFO : received requests    : 10000000
2020/01/20 16:02:14 rpcx_mclient.go:123: INFO : received requests_OK : 10000000
2020/01/20 16:02:14 rpcx_mclient.go:124: INFO : throughput  (TPS)    : 100926
2020/01/20 16:02:14 rpcx_mclient.go:125: INFO : mean: 954794 ns, median: 776113 ns, max: 13546797 ns, min: 80234 ns, p99: 4047876 ns
2020/01/20 16:02:14 rpcx_mclient.go:126: INFO : mean: 0 ms, median: 0 ms, max: 13 ms, min: 0 ms, p99: 4 ms
```

#### c=1000,n=10000000

```
took 58275 ms for 10000000 requests
2020/01/20 16:03:45 rpcx_mclient.go:121: INFO : sent     requests    : 10000000
2020/01/20 16:03:45 rpcx_mclient.go:122: INFO : received requests    : 10000000
2020/01/20 16:03:45 rpcx_mclient.go:123: INFO : received requests_OK : 10000000
2020/01/20 16:03:45 rpcx_mclient.go:124: INFO : throughput  (TPS)    : 171600
2020/01/20 16:03:45 rpcx_mclient.go:125: INFO : mean: 5474568 ns, median: 5369917 ns, max: 33885454 ns, min: 78216 ns, p99: 16806552 ns
2020/01/20 16:03:45 rpcx_mclient.go:126: INFO : mean: 5 ms, median: 5 ms, max: 33 ms, min: 0 ms, p99: 16 ms
```

### async-rpcx

#### c=100,n=10000000

```
took 54582 ms for 10000000 requests
2020/01/20 16:10:14 rpcx_mclient.go:145: INFO : sent     requests    : 10000000
2020/01/20 16:10:14 rpcx_mclient.go:146: INFO : received requests    : 10000000
2020/01/20 16:10:14 rpcx_mclient.go:147: INFO : received requests_OK : 10000000
2020/01/20 16:10:14 rpcx_mclient.go:148: INFO : throughput  (TPS)    : 183210
2020/01/20 16:10:14 rpcx_mclient.go:149: INFO : mean: 235075061 ns, median: 238628422 ns, max: 713519584 ns, min: 1853214 ns, p99: 591043364 ns
2020/01/20 16:10:14 rpcx_mclient.go:150: INFO : mean: 235 ms, median: 238 ms, max: 713 ms, min: 1 ms, p99: 591 ms
```

#### c=1000,n=10000000

```
took 55263 ms for 10000000 requests
2020/01/20 16:07:21 rpcx_mclient.go:145: INFO : sent     requests    : 10000000
2020/01/20 16:07:21 rpcx_mclient.go:146: INFO : received requests    : 10000000
2020/01/20 16:07:21 rpcx_mclient.go:147: INFO : received requests_OK : 10000000
2020/01/20 16:07:21 rpcx_mclient.go:148: INFO : throughput  (TPS)    : 180952
2020/01/20 16:07:21 rpcx_mclient.go:149: INFO : mean: 843985176 ns, median: 825286868 ns, max: 1645398520 ns, min: 15688776 ns, p99: 1532695207 ns
2020/01/20 16:07:21 rpcx_mclient.go:150: INFO : mean: 843 ms, median: 825 ms, max: 1645 ms, min: 15 ms, p99: 1532 ms
```

### thrift

#### c=100,n=10000000

```
java -cp thrift-1.0-SNAPSHOT.jar com.colobu.thrift.AppClient 192.168.1.226 100 10000000
sent     requests    : 10000000
received requests    : 10000000
received requests_OK : 10000000
throughput  (TPS)    : 18798
mean: 0.674689
median: 1.000000
max: 19.000000
min: 0.000000
99P: 4.000000
```

#### c=1000,n=10000000

```
java -cp thrift-1.0-SNAPSHOT.jar com.colobu.thrift.AppClient 192.168.1.226 1000 10000000
sent     requests    : 10000000
received requests    : 10000000
received requests_OK : 10000000
throughput  (TPS)    : 19151
mean: 7.158192
median: 7.000000
max: 181.000000
min: 0.000000
99P: 19.000000
```

### tarsgo

#### c=100,n=10000000

```
tarsgo_mclient.go:96: INFO : took 146657 ms for 10000000 requests
^@2020/01/20 16:40:37 tarsgo_mclient.go:113: INFO : sent     requests    : 10000000
2020/01/20 16:40:37 tarsgo_mclient.go:114: INFO : received requests    : 10000000
2020/01/20 16:40:37 tarsgo_mclient.go:115: INFO : received requests_OK : 0
2020/01/20 16:40:37 tarsgo_mclient.go:116: INFO : throughput  (TPS)    : 68186
2020/01/20 16:40:37 tarsgo_mclient.go:117: INFO : mean: 1463218 ns, median: 897658 ns, max: 833883541 ns, min: 40970 ns, p99: 24386698 ns
2020/01/20 16:40:37 tarsgo_mclient.go:118: INFO : mean: 1 ms, median: 0 ms, max: 833 ms, min: 0 ms, p99: 24 ms
```

#### c=1000,n=10000000

```
./tars_client -config benchmark.conf
2020/01/20 16:42:19 tarsgo_mclient.go:42: INFO : concurrency: 1000
requests per client: 10000
2020/01/20 16:42:19 tarsgo_mclient.go:47: INFO : message size: 581 bytes
2020/01/20 16:44:33 tarsgo_mclient.go:96: INFO : took 133438 ms for 10000000 requests
2020/01/20 16:44:40 tarsgo_mclient.go:113: INFO : sent     requests    : 10000000
2020/01/20 16:44:40 tarsgo_mclient.go:114: INFO : received requests    : 10000000
2020/01/20 16:44:40 tarsgo_mclient.go:115: INFO : received requests_OK : 0
2020/01/20 16:44:40 tarsgo_mclient.go:116: INFO : throughput  (TPS)    : 74941
2020/01/20 16:44:40 tarsgo_mclient.go:117: INFO : mean: 13189350 ns, median: 255591 ns, max: 1020977156 ns, min: 41305 ns, p99: 808153981 ns
2020/01/20 16:44:40 tarsgo_mclient.go:118: INFO : mean: 13 ms, median: 0 ms, max: 1020 ms, min: 0 ms, p99: 808 ms
```

### hprose

#### c=100,n=10000000

```
info took 85780 ms for 10000000 requests
2020/01/20 16:53:47  info sent     requests    : 10000000
2020/01/20 16:53:47  info received requests    : 10000000
2020/01/20 16:53:47  info received requests_OK : 10000000
2020/01/20 16:53:47  info throughput  (TPS)    : 116577
2020/01/20 16:53:47  info mean: 855396 ns, median: 611286 ns, max: 43307064 ns, min: 74772 ns, p99: 8799176 ns
2020/01/20 16:53:47  info mean: 0 ms, median: 0 ms, max: 43 ms, min: 0 ms, p99: 8 ms
```

#### c=1000,n=10000000

```
info took 55890 ms for 10000000 requests
2020/01/20 16:55:23  info sent     requests    : 10000000
2020/01/20 16:55:23  info received requests    : 10000000
2020/01/20 16:55:23  info received requests_OK : 10000000
2020/01/20 16:55:23  info throughput  (TPS)    : 178922
2020/01/20 16:55:23  info mean: 5574522 ns, median: 5450258 ns, max: 112631418 ns, min: 86944 ns, p99: 15302910 ns
2020/01/20 16:55:23  info mean: 5 ms, median: 5 ms, max: 112 ms, min: 0 ms, p99: 15 ms
```

### go-micro

#### c=100,n=10000000

```sh
./micro_client
2020/01/21 16:08:32 gomicro_client.go:43: INFO : 192.168.1.226:8972 1000000 100
2020/01/21 16:08:32 gomicro_client.go:47: INFO : concurrency: 100
requests per client: 10000

2020/01/21 16:08:32 gomicro_client.go:52: INFO : message size: 581 bytes

2020/01/21 16:10:30 gomicro_client.go:102: INFO : took 117938 ms for 1000000 requests
2020/01/21 16:10:31 gomicro_client.go:119: INFO : sent     requests    : 1000000
2020/01/21 16:10:31 gomicro_client.go:120: INFO : received requests    : 1000000
2020/01/21 16:10:31 gomicro_client.go:121: INFO : received requests_OK : 0
2020/01/21 16:10:31 gomicro_client.go:122: INFO : throughput  (TPS)    : 8479
2020/01/21 16:10:31 gomicro_client.go:123: INFO : mean: 11462696 ns, median: 10945519 ns, max: 1015949740 ns, min: 10513193 ns, p99: 17612504 ns
2020/01/21 16:10:31 gomicro_client.go:124: INFO : mean: 11 ms, median: 10 ms, max: 1015 ms, min: 10 ms, p99: 17 ms
```


#### c=1000,n=10000000

```go
2020/01/21 16:11:09 gomicro_client.go:43: INFO : 192.168.1.226:8972 1000000 1000
2020/01/21 16:11:09 gomicro_client.go:47: INFO : concurrency: 1000
requests per client: 1000

2020/01/21 16:11:09 gomicro_client.go:52: INFO : message size: 581 bytes

2020/01/21 16:12:01 gomicro_client.go:102: INFO : took 51922 ms for 1000000 requests
2020/01/21 16:12:01 gomicro_client.go:119: INFO : sent     requests    : 1000000
2020/01/21 16:12:01 gomicro_client.go:120: INFO : received requests    : 1000000
2020/01/21 16:12:01 gomicro_client.go:121: INFO : received requests_OK : 0
2020/01/21 16:12:01 gomicro_client.go:122: INFO : throughput  (TPS)    : 19259
2020/01/21 16:12:01 gomicro_client.go:123: INFO : mean: 46115145 ns, median: 22666560 ns, max: 3060928934 ns, min: 10578529 ns, p99: 1050307726 ns
2020/01/21 16:12:01 gomicro_client.go:124: INFO : mean: 46 ms, median: 22 ms, max: 3060 ms, min: 10 ms, p99: 1050 ms
```