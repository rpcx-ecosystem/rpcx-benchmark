package main

import (
	"flag"
	"runtime"
	"time"

	"github.com/hprose/hprose-golang/rpc"
	"github.com/rpcx-ecosystem/rpcx-benchmark/proto"
)

func say(in []byte) ([]byte, error) {
	args := &proto.BenchmarkMessage{}
	args.Unmarshal(in)
	args.Field1 = "OK"
	args.Field2 = 100
	if *delay > 0 {
		time.Sleep(*delay)
	} else {
		runtime.Gosched()
	}
	return args.Marshal()
}

var (
	host  = flag.String("s", "127.0.0.1:8972", "listened ip and port")
	delay = flag.Duration("delay", 0, "delay to mock business processing")
)

func main() {
	flag.Parse()
	server := rpc.NewTCPServer("tcp://" + *host)
	server.AddFunction("say", say, rpc.Options{Simple: true})
	server.Start()
}
