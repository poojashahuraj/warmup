import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
/**
 * Created by parallels on 1/23/15.
 */
public class ServerClass {
    public static void mcdain(String[] args) {
        ServerClass sc = new ServerClass();
        sc.execute();
    }
    private void execute() {
        ServerBootstrap bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory
                (Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()));
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            @Override
    public ChannelPipeline getPipeline() throws Exception {
                return Channels.pipeline(new EchoServerHandler());
    }
        });
        bootstrap.bind(new InetSocketAddress(8080));
    }
}
