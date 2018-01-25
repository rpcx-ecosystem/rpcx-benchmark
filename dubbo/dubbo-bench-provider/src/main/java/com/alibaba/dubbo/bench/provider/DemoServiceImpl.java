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
package com.alibaba.dubbo.bench.provider;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.alibaba.dubbo.bench.DemoService;
import com.alibaba.dubbo.bench.DubboBenchmark;
import com.alibaba.dubbo.rpc.RpcContext;
import com.google.protobuf.InvalidProtocolBufferException;

public class DemoServiceImpl implements DemoService {

    long sleep;

    public String sayHello(String name) {
        System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] Hello " + name + ", request from consumer: " + RpcContext.getContext().getRemoteAddress());
        return "Hello " + name + ", response form provider: " + RpcContext.getContext().getLocalAddress();
    }

    public byte[] say(byte[] msg) {
        try {
            if (sleep > 0 ) {
                Thread.sleep(sleep);
            }


            DubboBenchmark.BenchmarkMessage data = DubboBenchmark.BenchmarkMessage.newBuilder().mergeFrom(msg)
                    .setField1("OK").setField2(100).build();
            return data.toByteArray();

        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return new byte[0];
    }


    public void setSleep(long s) {
        sleep = s;
    }




}