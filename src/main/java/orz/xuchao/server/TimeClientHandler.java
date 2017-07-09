package orz.xuchao.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.*;
import java.util.logging.Logger;


/**
 * Created by Administrator on 2017/7/6 0006.
 */
public class TimeClientHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger=Logger.getLogger(TimeClientHandler.class.getName());

    public TimeClientHandler(){

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端发送了连接请求");

//        System.out.println("通道活动了并发了第1次消息");
//        byte[] req={(byte)0xEF,0x3A,
//                0x00,0x0F
//                ,0x01
//                ,0x01
//                ,0x01
//                ,0x05,0x01,0x00,0x01,0x02,0x03
//                ,0x59,0x3D,0x34,0x11
//                ,0x51,(byte)0xC7
//        };
//        System.out.println("===>"+req[0]);
//        ByteBuf message=Unpooled.buffer(req.length);
//        message.writeBytes(req);
//        ctx.writeAndFlush(message);


//        System.out.println("通道活动了并发了第1次消息");
//        ByteBuf flag=Unpooled.buffer(2);
//        flag.writeBytes(new byte[]{(byte)0xEF,0x3A});
//        int len=15;
//        byte channel=0x01;
//        byte protocolVersion=0x01;
//        byte[] bbody={0x01
//                ,0x05,0x01,0x00,0x01,0x02,0x03
//                ,0x59,0x3D,0x34,0x11};
//        ByteBuf body=Unpooled.buffer(bbody.length);
//        body.writeBytes(bbody);
//        ByteBuf end=Unpooled.buffer(2);
//        end.writeBytes(new byte[]{0x51,(byte)0xC7});
//        CustomMsg customMsg = new CustomMsg(flag, len,channel,protocolVersion,body, end);
//        ctx.writeAndFlush(customMsg);

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

//        ByteBuf buf=(ByteBuf)msg;
//        byte[] req=new byte[buf.readableBytes()];
//        buf.readBytes(req);
//        System.out.println("客户端收到了服务器端的回复");
//        for (int i=0;i<req.length;i++){
//            System.out.println(req[i]);
//        }



        System.out.println("------------------------------客户端接收到服务器的命令-----------------------");
        if(msg instanceof CustomMsg) {
            CustomMsg customMsg = (CustomMsg)msg;
            System.out.println(customMsg.getLen());
            ByteBuf buf=customMsg.getBody();
            byte[] req=new byte[buf.readableBytes()];
            buf.readBytes(req);
            for (int i=0;i<req.length;i++){
                System.out.println(req[i]);
            }

            switch (req[0]){
                case 1:
                    System.out.println("收到 从智能门禁读UID  命令");
                    System.out.println("----------------------------客户端应答服务器请求--------------------------");
                    ByteBuf flag=Unpooled.buffer(2);
                    flag.writeBytes(new byte[]{(byte)0xEF,0x3A});
                    int len=24;
                    byte channel=0x11;
                    byte protocolVersion=0x01;
                    byte[] bbody={0x01,
                            0x05,0x01,0x00,0x01,0x02,0x03,
                            0x23,(byte) 0xC9,0x3B,(byte) 0x89,0x4A,(byte) 0xED,(byte) 0xF0,0x65,
                            0x59,0x3D,0x34,0x11,
                            0x01
                    };
                    ByteBuf body=Unpooled.buffer(bbody.length);
                    body.writeBytes(bbody);
                    ByteBuf end=Unpooled.buffer(2);
                    end.writeBytes(new byte[]{0x2D,(byte)0x6E});
                    CustomMsg customMsg2 = new CustomMsg(flag, len,channel,protocolVersion,body, end);
                    ctx.writeAndFlush(customMsg2);
                    break;
                case 2:
                    System.out.println("收到 写一条UID和有效期到智能门禁指令  命令");
                case 3:
                    System.out.println("收到 从智能门禁删除一条UID指令  命令");
                case 4:
                    System.out.println("收到  开门指令  命令");
                default:
                    break;
            }
        }




    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warning("Unexpected exception form downstream:"+cause.getMessage());
        ctx.close();
    }



}
