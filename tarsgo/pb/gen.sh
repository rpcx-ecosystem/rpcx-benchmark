GOGO_ROOT=${GOPATH}/src/github.com/gogo/protobuf

protoc -I.:${GOPATH}/src --go_out=plugins=tarsrpc:. tarsgo_benchmark.proto
