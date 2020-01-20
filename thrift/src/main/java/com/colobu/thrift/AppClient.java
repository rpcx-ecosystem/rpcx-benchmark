package com.colobu.thrift;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.SynchronizedDescriptiveStatistics;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

public class AppClient {
    public static void main(String[] args) throws Exception {
        String host = args[0];

        final int threads = Integer.parseInt(args[1]);
        int n = Integer.parseInt(args[2]);
        BenchmarkMessage msg = prepareArgs();
        Greeter.Client[] client = createClients(host, threads, msg);


        final DescriptiveStatistics stats = new SynchronizedDescriptiveStatistics();
        ExecutorService es = Executors.newFixedThreadPool(threads);


        final AtomicInteger trans = new AtomicInteger(0);
        final AtomicInteger transOK = new AtomicInteger(0);


        final CountDownLatch latch = new CountDownLatch(n);


        final int count = n / threads; //count per client

        //warmup
        for (int i = 0; i < threads; i++) {
            client[i].say(msg);
        }

        long start = System.currentTimeMillis();
        for (int i = 0; i < threads; i++) {
            final int k = i;

            es.submit(() -> {
                for (int j = 0; j < count; j++) {
                    try {
                        long t = System.currentTimeMillis();
                        BenchmarkMessage m = client[k].say(msg);
                        t = System.currentTimeMillis() - t;
                        stats.addValue(t);

                        trans.incrementAndGet();

                        if (m != null && m.getField1().equals("OK")) {
                            transOK.incrementAndGet();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        latch.countDown();
                    }
                }
            });
        }


        latch.await();

        start = System.currentTimeMillis() - start;


        System.out.printf("sent     requests    : %d\n", n);
        System.out.printf("received requests    : %d\n", trans.get());
        System.out.printf("received requests_OK : %d\n", transOK.get());
        System.out.printf("throughput  (TPS)    : %d\n", n * 1000 / start);


        System.out.printf("mean: %f\n", stats.getMean());
        System.out.printf("median: %f\n", stats.getPercentile(50));
        System.out.printf("max: %f\n", stats.getMax());
        System.out.printf("min: %f\n", stats.getMin());

        System.out.printf("99P: %f\n", stats.getPercentile(99.9));

    }

    public static Greeter.Client[] createClients(String host, int n, BenchmarkMessage msg) throws TException {
        TTransport[] transport = new TTransport[n];
        Greeter.Client[] clients = new Greeter.Client[n];

        //warmup
        for (int i = 0; i < n; i++) {
            transport[i] = new TFramedTransport(new TSocket(host, 8972));
            transport[i].open();

            TProtocol protocol = new TBinaryProtocol(transport[i]);
            clients[i] =  new Greeter.Client(protocol);
            clients[i].say(msg);
        }

        return clients;
    }

    public static BenchmarkMessage prepareArgs() throws InvocationTargetException, IllegalAccessException {

        boolean b = true;
        int i = 100000;
        String s = "许多往事在眼前一幕一幕，变的那麼模糊";


        BenchmarkMessage msg = new BenchmarkMessage();


        Method[] methods = msg.getClass().getDeclaredMethods();
        for (Method m : methods) {
            if (m.getName().startsWith("setField") && ((m.getModifiers() & Modifier.PUBLIC) == Modifier.PUBLIC)) {
                Parameter[] params = m.getParameters();
                if (params.length == 1) {
                    String n = params[0].getParameterizedType().getTypeName();
                    m.setAccessible(true);
                    if (n.endsWith("java.lang.String")) {
                        m.invoke(msg, new Object[]{s});
                    } else if (n.endsWith("int")) {
                        m.invoke(msg, new Object[]{i});
                    } else if (n.equals("boolean")) {
                        m.invoke(msg, new Object[]{b});
                    }

                }
            }
        }

        return msg;

    }
}