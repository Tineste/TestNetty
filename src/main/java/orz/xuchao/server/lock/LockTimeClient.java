package orz.xuchao.server.lock;

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
import orz.xuchao.server.uicallback.ChanageUserverCallBack;
import orz.xuchao.server.uicallback.UICallBack;
import orz.xuchao.server.utils.CRCUtil;

import java.util.Calendar;

/**
 * Created by Administrator on 2017/7/6 0006.
 */
public class LockTimeClient {

    public SocketChannel socketChannel;
    private static final int MAX_FRAME_LENGTH = 1024 * 1024;
    private static final int LENGTH_FIELD_LENGTH = 2;
    private static final int LENGTH_FIELD_OFFSET = 2;
    private static final int LENGTH_ADJUSTMENT = 0;
    private static final int INITIAL_BYTES_TO_STRIP = 0;
    private UICallBack mUICallBack;
    private ChanageUserverCallBack mChanageUserverCallBack;


    Bootstrap b;
    EventLoopGroup group;
    ChannelFuture f;
    public LockTimeClient(UICallBack mUICallBack, ChanageUserverCallBack mChanageUserverCallBack){
        this.mUICallBack = mUICallBack;
        this.mChanageUserverCallBack=mChanageUserverCallBack;
    }



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
                            socketChannel.pipeline().addLast(new LockClientHandler(mUICallBack,mChanageUserverCallBack));

                        }
                    });
            f=b.connect(host,port).sync();
            if(f.isSuccess()){
                socketChannel=(SocketChannel)f.channel();
                System.out.println("==========================>从api服务器中获取ip和mac");
//                一上来就访问中转服务器，来获取压力小的门锁服务器
                BasePackage mBasePackage=new BasePackage();
                ByteBuf flag=Unpooled.buffer(2);
                flag.writeBytes(new byte[]{(byte)0xEF,0x3A});
                mBasePackage.setFlag(flag);
                mBasePackage.setChannel((byte) 0x01);
                mBasePackage.setProtocolVersion((byte) 0x01);
//                byte[] bbody={0x01
//                        ,0x06,0x05, 0x04,0x03, 0x02,0x01
//                        ,0x59,0x6C,0x3F, (byte) 0xD7};

                byte[] orlder={0x01};
                byte[] mac = LockConfig.mac;
                Calendar calendar = Calendar.getInstance();
                byte[] time=CRCUtil.timeToBytes(calendar);
                byte[] bbody=new byte[orlder.length+mac.length+time.length];

                System.arraycopy(orlder, 0, bbody, 0, orlder.length);
                System.arraycopy(mac, 0, bbody, orlder.length, mac.length);
                System.arraycopy(time, 0, bbody, orlder.length + mac.length, time.length);


                ByteBuf body=Unpooled.buffer(bbody.length);
                body.writeBytes(bbody);
                mBasePackage.setBody(body);
                CustomMsg customMsg=mBasePackage.getCustomMsg();
                byte[] ee=new byte[2];
                customMsg.getEnd().getBytes(0,ee);
                System.out.println("客户端发出的包，包尾是--->"+ CRCUtil.bytesToHexString(ee));
                socketChannel.writeAndFlush(customMsg);

            }
//            f.channel().closeFuture();
        }finally {
//            group.shutdownGracefully();
        }
    }


    public void reConnect(int port, String url) throws InterruptedException {
        if(null!=f){
            f.channel().closeFuture();
        }
        if(null!=group){
            group.shutdownGracefully();
        }
//        重新连接到新的服务器
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
                            socketChannel.pipeline().addLast(new LockClientHandler(mUICallBack,mChanageUserverCallBack));

                        }
                    });
            f=b.connect(url,port).sync();
            if(f.isSuccess()){

                 System.out.println("==========================>完成服务器转接，注册mac地址0x02");
                socketChannel=(SocketChannel)f.channel();
//                一上来就向服务器申报mac用于区分通道
                BasePackage mBasePackage=new BasePackage();
                ByteBuf flag=Unpooled.buffer(2);
                flag.writeBytes(new byte[]{(byte)0xEF,0x3A});
                mBasePackage.setFlag(flag);
                mBasePackage.setChannel((byte) 0x01);
                mBasePackage.setProtocolVersion((byte) 0x01);
                byte[] orlder={0x02};
                byte[] mac = LockConfig.mac;
                Calendar calendar = Calendar.getInstance();
                byte[] time=CRCUtil.timeToBytes(calendar);
                byte[] bbody=new byte[orlder.length+mac.length+time.length];

                System.arraycopy(orlder, 0, bbody, 0, orlder.length);
                System.arraycopy(mac, 0, bbody, orlder.length, mac.length);
                System.arraycopy(time, 0, bbody, orlder.length + mac.length, time.length);


                ByteBuf body=Unpooled.buffer(bbody.length);
                body.writeBytes(bbody);
                mBasePackage.setBody(body);
                CustomMsg customMsg=mBasePackage.getCustomMsg();
                byte[] ee=new byte[2];
                customMsg.getEnd().getBytes(0,ee);
                System.out.println("客户端发出的包，包尾是=====>"+ CRCUtil.bytesToHexString(ee));
                socketChannel.writeAndFlush(customMsg);

            }
//            f.channel().closeFuture();
        }finally {
//            group.shutdownGracefully();
        }


    }
}
