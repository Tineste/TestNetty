package orz.xuchao.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * Created by Administrator on 2017/7/6 0006.
 */
public class TimeClient {

    public SocketChannel socketChannel;
    private static final int MAX_FRAME_LENGTH = 1024 * 1024;
    private static final int LENGTH_FIELD_LENGTH = 2;
    private static final int LENGTH_FIELD_OFFSET = 2;
    private static final int LENGTH_ADJUSTMENT = 0;
    private static final int INITIAL_BYTES_TO_STRIP = 0;

    public void connect(int port ,String host)throws Exception{
        EventLoopGroup group=new NioEventLoopGroup();
        try {
            Bootstrap b=new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY,true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
//                            socketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024));
//                            socketChannel.pipeline().addLast(new StringDecoder());
                            socketChannel.pipeline().addLast(new CustomDecoder(MAX_FRAME_LENGTH,LENGTH_FIELD_LENGTH,LENGTH_FIELD_OFFSET,LENGTH_ADJUSTMENT,INITIAL_BYTES_TO_STRIP,false));
                            socketChannel.pipeline().addLast(new CustomEncoder());
                            socketChannel.pipeline().addLast(new TimeClientHandler());

                        }
                    });
            ChannelFuture f=b.connect(host,port).sync();
            if(f.isSuccess()){
                socketChannel=(SocketChannel)f.channel();
            }
//            f.channel().closeFuture();
        }finally {
//            group.shutdownGracefully();
        }
    }

    public static void main(String[] args)throws  Exception{
        int port=8080;
        TimeClient mTimeClient= new TimeClient();
        mTimeClient .connect(port,"127.0.0.1");

    }

}
