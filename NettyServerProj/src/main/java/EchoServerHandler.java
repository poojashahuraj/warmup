import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.*;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by parallels on 1/23/15.
 */
public class EchoServerHandler extends SimpleChannelUpstreamHandler {
    private final AtomicLong transferredBytes = new AtomicLong();
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        super.handleUpstream(ctx, e);
    }
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        transferredBytes.addAndGet(((ChannelBuffer) e.getMessage()).readableBytes());
        e.getChannel().write(e.getMessage());
    }
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        System.out.println("Exception caught");
        e.getChannel().close();
    }
}
