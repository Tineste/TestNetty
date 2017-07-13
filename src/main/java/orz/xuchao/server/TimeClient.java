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
import orz.xuchao.server.bean.CustomMsg;
import orz.xuchao.server.code.CustomDecoder;
import orz.xuchao.server.code.CustomEncoder;
import orz.xuchao.server.uicallback.UICallBack;

import java.util.Timer;
import java.util.TimerTask;

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
    private UICallBack mUICallBack;



    public TimeClient(UICallBack mUICallBack){
        this.mUICallBack = mUICallBack;
    }



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
                            socketChannel.pipeline().addLast(new TimeClientHandler(mUICallBack));

                        }
                    });
            ChannelFuture f=b.connect(host,port).sync();
            if(f.isSuccess()){
                socketChannel=(SocketChannel)f.channel();
//                建立通道后就开始持续向服务器发送心跳包
//                Timer tme =new Timer("心跳包");
//                tme.schedule(new TimerTask() {
//                    @Override
//                    public void run() {
//                        ByteBuf flag = Unpooled.buffer(2);
//                        flag.writeBytes(new byte[]{(byte) 0xEF, 0x3A});
//                        Short len = 0x10;
//                        byte channel = 0x11;
//                        byte protocolVersion = 0x01;
//                        byte[] bbody = {
//                                0x08,
//                                0x01, 0x02, 0x03, 0x04, 0x05, 0x06,
//                                0x01,
//                                0x01, 0x02, 0x03, 0x04,
//                        };
//                        ByteBuf body = Unpooled.buffer(bbody.length);
//                        body.writeBytes(bbody);
//                        ByteBuf end = Unpooled.buffer(2);
//                        end.writeBytes(new byte[]{0x6F, (byte) 0x6B});
//                        CustomMsg customMsg2 = new CustomMsg(flag, len, channel, protocolVersion, body, end);
//                        socketChannel.writeAndFlush(customMsg2);
//                    }
//                },1000,5*1000*60);
            }
//            f.channel().closeFuture();
        }finally {
//            group.shutdownGracefully();
        }
    }


}
