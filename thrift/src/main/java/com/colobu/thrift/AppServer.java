package com.colobu.thrift;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

public class AppServer
{
    public static GreeterHandler handler;

    public static Greeter.Processor processor;

    public static void main( String[] args )
    {
        long delay = 0L;
        if (args.length >0) {
            delay = Long.parseLong(args[0]);
        }
        try { 
            handler = new GreeterHandler(delay);
            processor = new Greeter.Processor(handler);

            simple(processor);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void simple(com.colobu.thrift.Greeter.Processor processor) {
        try {
            TNonblockingServerSocket serverTransport = new TNonblockingServerSocket(8972);
            //TServer server = new TSimpleServer(new TServer.Args(serverTransport).processor(processor));

            // Use this for a multithreaded server
            //TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));

            //https://github.com/rpcx-ecosystem/rpcx-benchmark/issues/1
//            TThreadedSelectorServer server = new TThreadedSelectorServer(
//                new TThreadedSelectorServer.Args(serverTransport).processor(processor).
//                selectorThreads(2).workerThreads(512));

            TThreadedSelectorServer server = new TThreadedSelectorServer(
                    new TThreadedSelectorServer.Args(serverTransport).processor(processor).
                            selectorThreads(2).workerThreads(512));

            System.out.println("Starting the simple server...");
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
