package orz.xuchao.server.LockServers;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import orz.xuchao.server.code.CustomDecoder;
import orz.xuchao.server.code.CustomEncoder;
import orz.xuchao.server.uicallback.UICallBack;


/**
 * Created by Administrator on 2017/7/6 0006.
 */
public class LockServer
{

    private static final int MAX_FRAME_LENGTH = 1024 * 1024;
    private static final int LENGTH_FIELD_LENGTH = 2;
    private static final int LENGTH_FIELD_OFFSET = 2;
    private static final int LENGTH_ADJUSTMENT = 0;
    private static final int INITIAL_BYTES_TO_STRIP = 0;

    private UICallBack mUICallBack;



    public LockServer(UICallBack mUICallBack){
        this.mUICallBack = mUICallBack;
    }



    public void bind(int port) throws Exception{
        EventLoopGroup bossGrop=new NioEventLoopGroup();
        EventLoopGroup workerGroup=new NioEventLoopGroup();
        try{
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGrop,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
//                            ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
//                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new CustomDecoder(MAX_FRAME_LENGTH,LENGTH_FIELD_LENGTH,LENGTH_FIELD_OFFSET,LENGTH_ADJUSTMENT,INITIAL_BYTES_TO_STRIP,false));
                            ch.pipeline().addLast(new CustomEncoder());
                            ch.pipeline().addLast(new LockServerHandler(mUICallBack));
                        }
                    })
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture f=b.bind(port).sync();
            f.channel().closeFuture().sync();
        }finally {
            bossGrop.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

   
}
