package main

import (
	"flag"
	"fmt"
	"runtime"
	"time"

	micro "github.com/micro/go-micro"
	"github.com/micro/go-plugins/transport/tcp"
	pb "github.com/rpcx-ecosystem/rpcx-benchmark/go-micro/pb"
	"golang.org/x/net/context"
)

var (
	host  = flag.String("s", "127.0.0.1:8972", "listened ip and port")
	delay = flag.Duration("delay", 0, "delay to mock business processing")
)

type Hello struct{}

func (t *Hello) Say(ctx context.Context, args *pb.BenchmarkMessage, reply *pb.BenchmarkMessage) (err error) {
	s := "OK"
	var i int32 = 100
	args.Field1 = &s
	args.Field2 = &i
	if *delay > 0 {
		time.Sleep(*delay)
	} else {
		runtime.Gosched()
	}
	return nil
}

func main() {
	flag.Parse()

	// Create a new service. Optionally include some options here.
	service := micro.NewService(
		micro.Name("hello"),
		micro.Transport(tcp.NewTransport()),
	)

	// Init will parse the command line flags.
	service.Init(server.Address(*host))

	// Register handler
	pb.RegisterHelloHandler(service.Server(), new(Hello))

	// Run the server
	if err := service.Run(); err != nil {
		fmt.Println(err)
	}
}
