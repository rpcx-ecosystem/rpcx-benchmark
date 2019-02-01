protoc -I.:${GOPATH}/src  --go_out=. --micro_out=. micro_benchmark.proto
