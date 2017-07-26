package orz.xuchao.server.lockservers;

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
import orz.xuchao.server.bean.BasePackage;
import orz.xuchao.server.bean.CustomMsg;
import orz.xuchao.server.code.CustomDecoder;
import orz.xuchao.server.code.CustomEncoder;
import orz.xuchao.server.utils.CRCUtil;

import java.util.Calendar;

/**
 * Created by Administrator on 2017/7/6 0006.
 */
public class LockToApiClient {

    public SocketChannel socketChannel;
    private static final int MAX_FRAME_LENGTH = 1024 * 1024;
    private static final int LENGTH_FIELD_LENGTH = 2;
    private static final int LENGTH_FIELD_OFFSET = 2;
    private static final int LENGTH_ADJUSTMENT = 0;
    private static final int INITIAL_BYTES_TO_STRIP = 0;
//    private UICallBack mUICallBack;


    Bootstrap b;
    EventLoopGroup group;
    ChannelFuture f;
//    public LockToApiClient(UICallBack mUICallBack){
//        this.mUICallBack = mUICallBack;
//    }



    public void connect(int port ,String host)throws Exception{
        group=new NioEventLoopGroup();
        try {
            b=new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY,true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
//                            socketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024));
//                            socketChannel.pipeline().addLast(new StringDecoder());
                            socketChannel.pipeline().addLast(new CustomDecoder(MAX_FRAME_LENGTH,LENGTH_FIELD_LENGTH,LENGTH_FIELD_OFFSET,LENGTH_ADJUSTMENT,INITIAL_BYTES_TO_STRIP,false));
                            socketChannel.pipeline().addLast(new CustomEncoder());
                            socketChannel.pipeline().addLast(new LockToApiHandler());

                        }
                    });
            f=b.connect(host,port).sync();
            if(f.isSuccess()){
                socketChannel=(SocketChannel)f.channel();


                BasePackage mBasePackage2=new BasePackage();
                ByteBuf flag=Unpooled.buffer(2);
                flag.writeBytes(new byte[]{(byte)0xEF,0x3A});
                mBasePackage2.setFlag(flag);
                mBasePackage2.setChannel((byte) 0x11);
                mBasePackage2.setProtocolVersion((byte) 0x01);
                byte[] orlder={0x21};
                byte[] serverMac={0x6,0x05,0x04,0x03,0x02,0x01};
                Calendar calendar = Calendar.getInstance();
                byte[] time= CRCUtil.timeToBytes(calendar);


                ByteBuf byteBuf=Unpooled.copiedBuffer(orlder,serverMac,time);

                mBasePackage2.setBody(byteBuf);
                CustomMsg customMsgaa=mBasePackage2.getCustomMsg();
                byte[] ee2=new byte[2];
                customMsgaa.getEnd().getBytes(0,ee2);
                System.out.println("服务器返回客户端的包，包尾是--->"+ CRCUtil.bytesToHexString(ee2));
                socketChannel.writeAndFlush(customMsgaa);


            }
//            f.channel().closeFuture();
        }finally {
//            group.shutdownGracefully();
        }
    }


    public static void main(String[] args) {
        int apiPort = 8981;
        String apiURL = "127.0.0.1";
        //        启动客户端
        LockToApiClient timeClient=new LockToApiClient();
        try {
            timeClient.connect(apiPort,apiURL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
