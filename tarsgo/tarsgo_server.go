package main

import (
	"flag"
	"fmt"
	"runtime"
	"time"

	"github.com/TarsCloud/TarsGo/tars"
	"github.com/rpcx-ecosystem/rpcx-benchmark/tarsgo/pb"
)

var (
	host  = flag.String("s", "127.0.0.1:8972", "listened ip and port")
	delay = flag.Duration("delay", 0, "delay to mock business processing")
)

type HelloImpl struct{}

func (t *HelloImpl) Say(args pb.BenchmarkMessage) (output pb.BenchmarkMessage, err error) {
	s := "OK"
	var i int32 = 100
	args.Field1 = &s
	args.Field2 = &i
	if *delay > 0 {
		time.Sleep(*delay)
	} else {
		runtime.Gosched()
	}
	return args, nil
}

func main() {
	flag.Parse()

	impl := new(HelloImpl)
	app := new(pb.Hello)
	cfg := tars.GetServerConfig()

	fmt.Println("start ", cfg.App+"."+cfg.Server+".HelloTestObj")
	app.AddServant(impl, cfg.App+"."+cfg.Server+".HelloTestObj")
	tars.Run()
}
