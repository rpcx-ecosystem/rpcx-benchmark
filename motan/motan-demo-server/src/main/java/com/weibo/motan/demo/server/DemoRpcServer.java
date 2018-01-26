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

package com.weibo.motan.demo.server;

import com.weibo.api.motan.common.MotanConstants;
import com.weibo.api.motan.util.MotanSwitcherUtil;
import com.weibo.motan.demo.service.MotanDemoService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class DemoRpcServer {

    public static void main(String[] args) throws InterruptedException, IOException {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(new String[] {"classpath*:motan_demo_server.xml"});
        MotanSwitcherUtil.setSwitcherValue(MotanConstants.REGISTRY_HEARTBEAT_SWITCHER, false);
        System.out.println("server start...");

        if (args.length > 0 ) {
            long sleep = Long.parseLong(args[0]);
            MotanDemoService motanDemoServiceImpl = (MotanDemoService)applicationContext.getBean("motanDemoServiceImpl");
            motanDemoServiceImpl.setSleep(sleep);
        }

        System.in.read();
    }

}
