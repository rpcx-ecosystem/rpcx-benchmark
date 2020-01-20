/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.bench.consumer;

import org.apache.dubbo.bench.DemoService;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;

public class Consumer {

    public static void main(String[] args) throws Exception {
        ReferenceConfig<DemoService> reference = new ReferenceConfig<>();
        reference.setApplication(new ApplicationConfig("dubbo-demo-api-consumer"));
        String zk = "zookeeper://127.0.0.1:2181";
        if (args.length > 2) {
            zk = args[2];
        }
        reference.setRegistry(new RegistryConfig(zk));
        reference.setInterface(DemoService.class);
        reference.setTimeout(10000);
        DemoService service = reference.get();

        DemoAction demoAction = new DemoAction();
        demoAction.setDemoService(service);

        if (args.length > 0) {
            demoAction.threads = Integer.parseInt(args[0]);
        }
        if (args.length > 1) {
            demoAction.count = Integer.parseInt(args[1]);
        }

        demoAction.warmup();

        demoAction.start();

    }
}
