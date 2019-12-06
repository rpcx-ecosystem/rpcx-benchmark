package main

import (
	"encoding/binary"
	"flag"
	"io"
	"log"
	"net"
	"net/http"
	_ "net/http/pprof"
	"runtime"
	"strings"
	"time"

	"github.com/rpcx-ecosystem/rpcx-benchmark/proto"
	"github.com/smallnest/goframe"
)

var (
	host      = flag.String("s", "127.0.0.1:8972", "listened ip and port")
	delay     = flag.Duration("delay", 0, "delay to mock business processing")
	debugAddr = flag.String("d", "127.0.0.1:9981", "server ip and port")
)

func main() {
	flag.Parse()

	go func() {
		log.Println(http.ListenAndServe(*debugAddr, nil))
	}()

	l, err := net.Listen("tcp", *host)
	if err != nil {
		panic(err)
	}
	defer l.Close()

	encoderConfig, decoderConfig := config()

	for {
		conn, err := l.Accept()
		if err != nil {
			panic(err)
		}

		c := goframe.NewLengthFieldBasedFrameConn(encoderConfig, decoderConfig, conn)
		go func(conn goframe.FrameConn) {
			for {
				b, err := c.ReadFrame()
				if err != nil {
					if err == io.EOF || strings.Contains(err.Error(), "connection reset by peer") {
						return
					}
					panic(err)
				}
				b, _ = say(b)
				c.WriteFrame(b)
			}
		}(c)
	}
}

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

func config() (goframe.EncoderConfig, goframe.DecoderConfig) {
	encoderConfig := goframe.EncoderConfig{
		ByteOrder:                       binary.BigEndian,
		LengthFieldLength:               4,
		LengthAdjustment:                0,
		LengthIncludesLengthFieldLength: false,
	}

	decoderConfig := goframe.DecoderConfig{
		ByteOrder:           binary.BigEndian,
		LengthFieldOffset:   0,
		LengthFieldLength:   4,
		LengthAdjustment:    0,
		InitialBytesToStrip: 4,
	}

	return encoderConfig, decoderConfig
}
