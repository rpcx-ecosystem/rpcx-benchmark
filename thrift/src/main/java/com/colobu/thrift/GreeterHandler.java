package com.colobu.thrift;

import org.apache.thrift.TException;

public class GreeterHandler implements com.colobu.thrift.Greeter.Iface {

    public BenchmarkMessage say(BenchmarkMessage msg) throws TException {
        msg.setField1("OK");
        msg.setField2(100);
        return msg;
    }

}