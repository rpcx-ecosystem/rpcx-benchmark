package main

import (
	context "context"
	"crypto/tls"
	"crypto/x509"
	"flag"
	"io/ioutil"
	"log"
	"net"
	"net/http"
	"runtime"
	"time"

	"github.com/rpcx-ecosystem/rpcx-benchmark/twirp/pb"
)

type hello struct{}

func (t *hello) Say(ctx context.Context, args *pb.BenchmarkMessage) (reply *pb.BenchmarkMessage, err error) {
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

var (
	host  = flag.String("s", "127.0.0.1:8972", "listened ip and port")
	delay = flag.Duration("delay", 0, "delay to mock business processing")
)

func main() {
	flag.Parse()

	ln, err := net.Listen("tcp", *host)
	if err != nil {
		log.Fatalf("failed to listen: %v", err)
	}

	caCert, err := ioutil.ReadFile("rootCA.pem")
	if err != nil {
		log.Fatal(err)
	}
	caCertPool := x509.NewCertPool()
	caCertPool.AppendCertsFromPEM(caCert)

	tlsConfig := &tls.Config{
		RootCAs: caCertPool,
	}
	tlsConfig.BuildNameToCertificate()

	s := pb.NewHelloServer(&hello{}, nil)
	server := &http.Server{
		Addr:      ":8443",
		Handler:   s,
		TLSConfig: tlsConfig,
	}

	log.Fatal(server.ServeTLS(ln, "server.crt", "server.key"))
}
