package main

import (
	"context"
	"flag"
	"log"
	"net/http"
	_ "net/http/pprof"

	"github.com/smallnest/rpcx/server"
)

type Hello int

func (t *Hello) Say(ctx context.Context, args *BenchmarkMessage, reply *BenchmarkMessage) error {
	args.Field1 = "OK"
	args.Field2 = 100
	*reply = *args
	return nil
}

var host = flag.String("s", "127.0.0.1:8972", "listened ip and port")
var cpuprofile = flag.String("cpuprofile", "", "write cpu profile to file")

func main() {
	flag.Parse()

	go func() {
		log.Println(http.ListenAndServe("localhost:9981", nil))
	}()

	server := server.NewServer(nil)
	server.RegisterName("Hello", new(Hello), "")
	server.Serve("tcp", *host)
}
