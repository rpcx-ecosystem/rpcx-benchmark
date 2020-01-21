package main

import (
	"fmt"
	"os"
	"runtime"
	"strconv"
	"time"

	micro "github.com/micro/go-micro"
	pb "github.com/rpcx-ecosystem/rpcx-benchmark/go-micro/pb"
	"golang.org/x/net/context"
)

var (
	host  string
	delay int
)

type Hello struct{}

func (t *Hello) Say(ctx context.Context, args *pb.BenchmarkMessage, reply *pb.BenchmarkMessage) (err error) {
	s := "OK"
	var i int32 = 100
	args.Field1 = &s
	args.Field2 = &i
	if delay > 0 {
		time.Sleep(time.Duration(delay))
	} else {
		runtime.Gosched()
	}
	return nil
}

func main() {
	host, _ = os.LookupEnv("HOST")
	if host == "" {
		host = "127.0.0.1:8972"
	}

	d, _ := os.LookupEnv("DELAY")
	if d != "" {
		delay, _ = strconv.Atoi(d)
	}

	// Create a new service. Optionally include some options here.
	service := micro.NewService(
		micro.Name("hello"),
	)

	// Init will parse the command line flags.
	service.Init(micro.Address(host))

	// Register handler
	pb.RegisterHelloHandler(service.Server(), new(Hello))

	// Run the server
	if err := service.Run(); err != nil {
		fmt.Println(err)
	}
}
