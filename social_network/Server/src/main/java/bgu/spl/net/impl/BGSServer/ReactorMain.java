package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.objects.MessageEncoderDecoderImpl;
import bgu.spl.net.objects.BidiMessagingProtocolImpl;
import bgu.spl.net.srv.Server;

public class ReactorMain {
    public static void main(String[] args) {
        int port = Integer.valueOf(args[0]);
        int numberOfThreads = Integer.valueOf(args[1]);
        Server.reactor(
                numberOfThreads,
                port, //port
                () -> new BidiMessagingProtocolImpl<>(), //protocol factory
                MessageEncoderDecoderImpl::new //message encoder decoder factory
        ).serve();
    }
}