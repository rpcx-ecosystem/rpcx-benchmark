package main

import (
	context "context"
	"flag"
	"log"
	"net"
	"net/http"
)

type hello struct{}

func (t *hello) Say(ctx context.Context, args *BenchmarkMessage) (reply *BenchmarkMessage, err error) {
	s := "OK"
	var i int32 = 100
	args.Field1 = &s
	args.Field2 = &i
	return args, nil
}

var host = flag.String("s", "127.0.0.1:8972", "listened ip and port")

func main() {
	flag.Parse()

	ln, err := net.Listen("tcp", *host)
	if err != nil {
		log.Fatalf("failed to listen: %v", err)
	}

	server := NewHelloServer(&hello{}, nil)
	log.Fatal(http.Serve(ln, server))
}
