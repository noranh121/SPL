package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.objects.MessageEncoderDecoderImpl;
import bgu.spl.net.objects.BidiMessagingProtocolImpl;
import bgu.spl.net.srv.Server;

public class TPCMain {
    public static void main(String[] args) {
        int port = Integer.valueOf(args[0]);
        Server.threadPerClient(
                port, //port
                () -> new BidiMessagingProtocolImpl<>(), //protocol factory
                MessageEncoderDecoderImpl::new //message encoder decoder factory
        ).serve();
    }
}
