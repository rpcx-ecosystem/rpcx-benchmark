protoc -I.:${GOPATH}/src  --go_out=plugins=micro:. micro_benchmark.proto
