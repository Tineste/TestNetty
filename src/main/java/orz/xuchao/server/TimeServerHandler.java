package orz.xuchao.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;

/**
 * Created by Administrator on 2017/7/6 0006.
 */
public class TimeServerHandler extends ChannelInboundHandlerAdapter {

    private  int counter;


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String uuid=ctx.channel().id().asLongText();
        GatewayService.addGatewayChannel(uuid,(SocketChannel)ctx.channel());
        System.out.println("一个客户端连接进来了："+uuid);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {





//        System.out.println("服务器收到消息");
//        ByteBuf buf=(ByteBuf)msg;
//
//        byte[] req=new byte[buf.readableBytes()];
//        buf.readBytes(req);
//        for (int i=0;i<req.length;i++){
//            System.out.println(req[i]);
//        }
//
//        byte[] res={(byte) 0xEF,0x3A,
//                0x00,0x18,
//                0x11,
//                0x01,
//                0x01,
//                0x05,0x01,0x00,0x01,0x02,0x03,
//                0x23,(byte)0xC9,0x3B,(byte)0x89,0x4A,(byte)0xED,(byte)0xF0,0x65,
//                0x59,0x3D,0x34,0x11,
//                0x01,
//                0x2D,
//                0x6E
//        };
//        ByteBuf resp= Unpooled.copiedBuffer(res);
//        ctx.write(resp);

        System.out.println("------------------------------服务器接收到客户端的应答-----------------------");
        if(msg instanceof CustomMsg) {
            CustomMsg customMsg = (CustomMsg)msg;
            System.out.println(customMsg.getLen());
            ByteBuf buf=customMsg.getBody();
            byte[] req=new byte[buf.readableBytes()];
            buf.readBytes(req);
            for (int i=0;i<req.length;i++){
                System.out.println(req[i]);
            }
        }

//        System.out.println("----------------------------服务器返回命令给客户端--------------------------");
//        ByteBuf flag=Unpooled.buffer(2);
//        flag.writeBytes(new byte[]{(byte)0xEF,0x3A});
//        int len=24;
//        byte channel=0x11;
//        byte protocolVersion=0x01;
//        byte[] bbody={0x01,
//                0x05,0x01,0x00,0x01,0x02,0x03,
//                0x23,(byte) 0xC9,0x3B,(byte) 0x89,0x4A,(byte) 0xED,(byte) 0xF0,0x65,
//                0x59,0x3D,0x34,0x11,
//                0x01
//        };
//        ByteBuf body=Unpooled.buffer(bbody.length);
//        body.writeBytes(bbody);
//        ByteBuf end=Unpooled.buffer(2);
//        end.writeBytes(new byte[]{0x2D,(byte)0x6E});
//        CustomMsg customMsg = new CustomMsg(flag, len,channel,protocolVersion,body, end);
//        ctx.writeAndFlush(customMsg);



    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
