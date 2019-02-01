package main

import (
	"context"
	"crypto/sha1"
	"flag"
	"time"

	"github.com/rpcx-ecosystem/rpcx-benchmark/proto"
	"github.com/smallnest/rpcx/server"
	kcp "github.com/xtaci/kcp-go"
	"golang.org/x/crypto/pbkdf2"
)

type Hello int

func (t *Hello) Say(ctx context.Context, args *proto.BenchmarkMessage, reply *proto.BenchmarkMessage) error {
	args.Field1 = "OK"
	args.Field2 = 100
	*reply = *args
	if *delay > 0 {
		time.Sleep(*delay)
	}
	return nil
}

var (
	host  = flag.String("s", "localhost:8972", "listened ip and port")
	delay = flag.Duration("delay", 0, "delay to mock business processing")
)

const cryptKey = "rpcx-key"
const cryptSalt = "rpcx-salt"

func main() {
	flag.Parse()

	pass := pbkdf2.Key([]byte(cryptKey), []byte(cryptSalt), 4096, 32, sha1.New)
	bc, err := kcp.NewAESBlockCrypt(pass)
	if err != nil {
		panic(err)
	}
	s := server.NewServer(server.WithBlockCrypt(bc))
	s.RegisterName("Hello", new(Hello), "")
	err = s.Serve("kcp", *host)
	if err != nil {
		panic(err)
	}

}
