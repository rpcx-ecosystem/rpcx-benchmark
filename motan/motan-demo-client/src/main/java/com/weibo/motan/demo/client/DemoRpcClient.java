/*
 *  Copyright 2009-2016 Weibo, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.weibo.motan.demo.client;

import com.weibo.motan.demo.service.DubboBenchmark;
import com.weibo.motan.demo.service.MotanDemoService;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.SynchronizedDescriptiveStatistics;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class DemoRpcClient {

    public static DubboBenchmark.BenchmarkMessage  testSay(MotanDemoService service, byte[] msgBytes) {
        try {
            byte[] reply = service.say(msgBytes);
            DubboBenchmark.BenchmarkMessage replyMsg = DubboBenchmark.BenchmarkMessage.parseFrom(reply);
            return replyMsg;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    public static void main(String[] args) throws Exception {
        final DescriptiveStatistics stats = new SynchronizedDescriptiveStatistics();


        int threads = 1;

        if (args.length >0) {
            threads = Integer.parseInt(args[0]);
        }


        DubboBenchmark.BenchmarkMessage msg = prepareArgs();
        final byte[] msgBytes = msg.toByteArray();

        int n = 10;
        if (args.length >1) {
            n = Integer.parseInt(args[1]);
        }

        final CountDownLatch latch = new CountDownLatch(n);

        ExecutorService es = Executors.newFixedThreadPool(threads);


        final AtomicInteger trans = new AtomicInteger(0);
        final AtomicInteger transOK = new AtomicInteger(0);


        ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"classpath:motan_demo_client.xml"});

        final MotanDemoService service = (MotanDemoService) ctx.getBean("motanDemoReferer");

        //warmup
        for (int i = 0; i < 10; i++) {
            testSay(service, msgBytes);
        }

        long start = System.currentTimeMillis();
        for (int i = 0; i < n; i++) {
            es.submit(new Runnable() {
                @Override
                public void run() {
                    try {

                        long t = System.currentTimeMillis();
                        DubboBenchmark.BenchmarkMessage m = testSay(service, msgBytes);
                        t = System.currentTimeMillis() - t;
                        stats.addValue(t);

                        trans.incrementAndGet();

                        if (m != null && m.getField1().equals("OK")) {
                            transOK.incrementAndGet();
                        }

                    } finally {
                        latch.countDown();
                    }
                }
            } );
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

        System.out.printf("99P: %f\n", stats.getPercentile(90));

    }


    public static DubboBenchmark.BenchmarkMessage prepareArgs() throws InvocationTargetException, IllegalAccessException {

        boolean b = true;
        int i = 100000;
        String s = "许多往事在眼前一幕一幕，变的那麼模糊";


        DubboBenchmark.BenchmarkMessage.Builder builder = DubboBenchmark.BenchmarkMessage.newBuilder();

        Method[] methods = builder.getClass().getDeclaredMethods();
        for (Method m : methods) {
            if (m.getName().startsWith("setField") && ((m.getModifiers() & Modifier.PUBLIC) == Modifier.PUBLIC)) {
                Class[] params = m.getParameterTypes();
                if (params.length == 1) {
                    String n = params[0].getName();
                    m.setAccessible(true);
                    if (n.endsWith("java.lang.String")) {
                        m.invoke(builder, new Object[]{s});
                    } else if (n.endsWith("int")) {
                        m.invoke(builder, new Object[]{i});
                    } else if (n.equals("boolean")) {
                        m.invoke(builder, new Object[]{b});
                    }

                }
            }
        }

        return builder.build();

    }

}
