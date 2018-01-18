package com.colobu.thrift;

import org.apache.thrift.TException;

public class GreeterHandler implements com.colobu.thrift.Greeter.Iface {
    long delay;

    public GreeterHandler(long delay) {
        this.delay = delay;
    }

    public BenchmarkMessage say(BenchmarkMessage msg) throws TException {
        msg.setField1("OK");
        msg.setField2(100);

        if (delay > 0) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
            }
        }

        return msg;
    }

}