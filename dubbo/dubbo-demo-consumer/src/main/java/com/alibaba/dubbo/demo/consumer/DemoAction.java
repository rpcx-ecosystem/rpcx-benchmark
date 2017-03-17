/*
 * Copyright 1999-2011 Alibaba Group.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.dubbo.demo.consumer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import com.codahale.metrics.*;

import com.alibaba.dubbo.demo.DemoService;
import com.alibaba.dubbo.demo.DubboBenchmark;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.SynchronizedDescriptiveStatistics;
import org.springframework.beans.factory.annotation.Value;

public class DemoAction {

    private DemoService demoService;



    public void setDemoService(DemoService demoService) {
        this.demoService = demoService;
    }

    public void testSayHello() throws InterruptedException {
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            try {
                String hello = demoService.sayHello("world" + i);
                System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] " + hello);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Thread.sleep(2000);
        }
    }

    public DubboBenchmark.BenchmarkMessage testSay(byte[] msgBytes) throws InterruptedException {
        try {
            byte[] reply = demoService.say(msgBytes);
            DubboBenchmark.BenchmarkMessage replyMsg = DubboBenchmark.BenchmarkMessage.parseFrom(reply);
            return replyMsg;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public void start() throws Exception {
        int threads = 100;

        final DescriptiveStatistics stats = new SynchronizedDescriptiveStatistics();


        DubboBenchmark.BenchmarkMessage msg = prepareArgs();
        final byte[] msgBytes = msg.toByteArray();

        int n = 1000000;
        final CountDownLatch latch = new CountDownLatch(n);

        ExecutorService es = Executors.newFixedThreadPool(threads);


        final AtomicInteger trans = new AtomicInteger(0);
        final AtomicInteger transOK = new AtomicInteger(0);


        long start = System.currentTimeMillis();
        for (int i = 0; i < n; i++) {
            es.submit(() -> {
                try {

                    long t = System.currentTimeMillis();
                    DubboBenchmark.BenchmarkMessage m = testSay(msgBytes);
                    t = System.currentTimeMillis() - t;
                    stats.addValue(t);

                    trans.incrementAndGet();

                    if (m != null && m.getField1().equals("OK")) {
                        transOK.incrementAndGet();
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
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

        System.out.printf("99P: %f\n", stats.getPercentile(90));
    }

    public DubboBenchmark.BenchmarkMessage prepareArgs() throws InvocationTargetException, IllegalAccessException {

        boolean b = true;
        int i = 100000;
        String s = "许多往事在眼前一幕一幕，变的那麼模糊";


        DubboBenchmark.BenchmarkMessage.Builder builder = DubboBenchmark.BenchmarkMessage.newBuilder();

        Method[] methods = builder.getClass().getDeclaredMethods();
        for (Method m : methods) {
            if (m.getName().startsWith("setField") && ((m.getModifiers() & Modifier.PUBLIC) == Modifier.PUBLIC)) {
                Parameter[] params = m.getParameters();
                if (params.length == 1) {
                    String n = params[0].getParameterizedType().getTypeName();
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