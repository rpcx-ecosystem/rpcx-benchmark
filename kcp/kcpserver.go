package main

import (
	"context"
	"crypto/sha1"
	"flag"

	"github.com/smallnest/rpcx/server"
	kcp "github.com/xtaci/kcp-go"
	"golang.org/x/crypto/pbkdf2"
)

type Hello int

func (t *Hello) Say(ctx context.Context, args *BenchmarkMessage, reply *BenchmarkMessage) error {
	args.Field1 = "OK"
	args.Field2 = 100
	*reply = *args
	return nil
}

var host = flag.String("s", "127.0.0.1:8972", "listened ip and port")

const cryptKey = "rpcx-key"
const cryptSalt = "rpcx-salt"

func main() {
	flag.Parse()

	pass := pbkdf2.Key([]byte(cryptKey), []byte(cryptSalt), 4096, 32, sha1.New)
	bc, err := kcp.NewAESBlockCrypt(pass)
	if err != nil {
		panic(err)
	}
	options := map[string]interface{}{"BlockCrypt": bc}

	server := server.NewServer(options)
	server.RegisterName("Hello", new(Hello), "")
	err = server.Serve("kcp", *host)
	if err != nil {
		panic(err)
	}

}
