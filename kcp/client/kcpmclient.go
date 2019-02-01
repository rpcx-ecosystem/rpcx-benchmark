// go run -tags kcp kcpmclient.go
package main

import (
	"context"
	"crypto/sha1"
	"flag"
	"reflect"
	"strings"
	"sync"
	"sync/atomic"
	"time"

	"github.com/montanaflynn/stats"
	"github.com/rpcx-ecosystem/rpcx-benchmark/proto"
	"github.com/smallnest/rpcx/client"
	"github.com/smallnest/rpcx/log"
	"github.com/smallnest/rpcx/protocol"
	kcp "github.com/xtaci/kcp-go"
	"golang.org/x/crypto/pbkdf2"
)

var concurrency = flag.Int("c", 1, "concurrency")
var total = flag.Int("n", 1, "total requests for all clients")
var host = flag.String("s", "kcp@127.0.0.1:8972", "server ip and port")

const cryptKey = "rpcx-key"
const cryptSalt = "rpcx-salt"

func main() {
	flag.Parse()
	n := *concurrency
	m := *total / n

	servers := strings.Split(*host, ",")
	var serverPeers []*client.KVPair
	for _, server := range servers {
		serverPeers = append(serverPeers, &client.KVPair{Key: server})
	}

	log.Infof("Servers: %+v\n\n", serverPeers)

	log.Infof("concurrency: %d\nrequests per client: %d\n\n", n, m)

	servicePath := "Hello"
	serviceMethod := "Say"

	args := prepareArgs()

	b := make([]byte, 1024)
	i, _ := args.MarshalTo(b)
	log.Infof("message size: %d bytes\n\n", i)

	var wg sync.WaitGroup
	wg.Add(n * m)

	var startWg sync.WaitGroup
	startWg.Add(n)

	var trans uint64
	var transOK uint64

	d := make([][]int64, n, n)

	//it contains warmup time but we can ignore it
	totalT := time.Now().UnixNano()
	for i := 0; i < n; i++ {
		dt := make([]int64, 0, m)
		d = append(d, dt)

		go func(i int) {
			dis := client.NewMultipleServersDiscovery(serverPeers)

			pass := pbkdf2.Key([]byte(cryptKey), []byte(cryptSalt), 4096, 32, sha1.New)
			bc, _ := kcp.NewAESBlockCrypt(pass)
			option := client.DefaultOption
			option.Block = bc
			option.SerializeType = protocol.ProtoBuffer
			xclient := client.NewXClient(servicePath, client.Failtry, client.RoundRobin, dis, option)
			defer xclient.Close()

			var reply proto.BenchmarkMessage

			//warmup
			for j := 0; j < 5; j++ {
				xclient.Call(context.Background(), serviceMethod, args, &reply)
			}

			startWg.Done()
			startWg.Wait()

			for j := 0; j < m; j++ {
				t := time.Now().UnixNano()
				err := xclient.Call(context.Background(), serviceMethod, args, &reply)
				t = time.Now().UnixNano() - t

				d[i] = append(d[i], t)

				if err == nil && reply.Field1 == "OK" {
					atomic.AddUint64(&transOK, 1)
				}

				atomic.AddUint64(&trans, 1)
				wg.Done()
			}

		}(i)

	}

	wg.Wait()
	totalT = time.Now().UnixNano() - totalT
	totalT = totalT / 1000000
	log.Infof("took %d ms for %d requests", totalT, n*m)

	totalD := make([]int64, 0, n*m)
	for _, k := range d {
		totalD = append(totalD, k...)
	}
	totalD2 := make([]float64, 0, n*m)
	for _, k := range totalD {
		totalD2 = append(totalD2, float64(k))
	}

	mean, _ := stats.Mean(totalD2)
	median, _ := stats.Median(totalD2)
	max, _ := stats.Max(totalD2)
	min, _ := stats.Min(totalD2)
	p99, _ := stats.Percentile(totalD2, 99.9)

	log.Infof("sent     requests    : %d\n", n*m)
	log.Infof("received requests    : %d\n", atomic.LoadUint64(&trans))
	log.Infof("received requests_OK : %d\n", atomic.LoadUint64(&transOK))
	log.Infof("throughput  (TPS)    : %d\n", int64(n*m)*1000/totalT)
	log.Infof("mean: %.f ns, median: %.f ns, max: %.f ns, min: %.f ns, p99: %.f ns\n", mean, median, max, min, p99)
	log.Infof("mean: %d ms, median: %d ms, max: %d ms, min: %d ms, p99: %d ms\n", int64(mean/1000000), int64(median/1000000), int64(max/1000000), int64(min/1000000), int64(p99/1000000))

}

func prepareArgs() *proto.BenchmarkMessage {
	b := true
	var i int32 = 100000
	var s = "许多往事在眼前一幕一幕，变的那麼模糊"

	var args proto.BenchmarkMessage

	v := reflect.ValueOf(&args).Elem()
	num := v.NumField()
	for k := 0; k < num; k++ {
		field := v.Field(k)
		if field.Type().Kind() == reflect.Ptr {
			switch v.Field(k).Type().Elem().Kind() {
			case reflect.Int, reflect.Int32, reflect.Int64:
				field.Set(reflect.ValueOf(&i))
			case reflect.Bool:
				field.Set(reflect.ValueOf(&b))
			case reflect.String:
				field.Set(reflect.ValueOf(&s))
			}
		} else {
			switch field.Kind() {
			case reflect.Int, reflect.Int32, reflect.Int64:
				field.SetInt(100000)
			case reflect.Bool:
				field.SetBool(true)
			case reflect.String:
				field.SetString(s)
			}
		}

	}
	return &args
}
