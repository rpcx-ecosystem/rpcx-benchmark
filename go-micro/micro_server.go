package main

import (
	"flag"
	fmt "fmt"
	"runtime"
	"time"

	micro "github.com/micro/go-micro"
	"golang.org/x/net/context"
)

type HelloS struct{}

func (t *HelloS) Say(ctx context.Context, args *BenchmarkMessage, reply *BenchmarkMessage) error {
	s := "OK"
	var i int32 = 100
	args.Field1 = &s
	args.Field2 = &i
	*reply = *args
	if *delay > 0 {
		time.Sleep(*delay)
	} else {
		runtime.Gosched()
	}
	return nil
}

//var host = flag.String("s", "127.0.0.1:8972", "listened ip and port")

var delay = flag.Duration("delay", 0, "delay to mock business processing")

func main() {
	//flag.Parse()

	service := micro.NewService(
		micro.Name("hello"),
		micro.Version("latest"),
	)

	service.Init()

	RegisterHelloHandler(service.Server(), &HelloS{})

	// Run the server
	if err := service.Run(); err != nil {
		fmt.Println(err)
	}
}
