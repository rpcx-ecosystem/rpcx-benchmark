package com.colobu.thrift;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

public class AppServer
{
    public static GreeterHandler handler;

    public static Greeter.Processor processor;

    public static void main( String[] args )
    {
        try {
            handler = new GreeterHandler();
            processor = new Greeter.Processor(handler);

            simple(processor);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void simple(com.colobu.thrift.Greeter.Processor processor) {
        try {
            TServerTransport serverTransport = new TServerSocket(8972);
            //TServer server = new TSimpleServer(new TServer.Args(serverTransport).processor(processor));

            // Use this for a multithreaded server
            TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));

            System.out.println("Starting the simple server...");
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
