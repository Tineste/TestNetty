package orz.xuchao.server.apiserverclient;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import orz.xuchao.server.bean.BasePackage;
import orz.xuchao.server.bean.CustomMsg;
import orz.xuchao.server.uicallback.UICallBack;
import orz.xuchao.server.utils.CRCUtil;

import java.util.Map;


/**
 * Created by xuchao on 2017/7/6 0006.
 * 中转服务器，就三个功能，一个是和门锁服务器服务器集群保持心跳包，二是给门锁端端分配服务器。
 */
public class ShuntServerHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LogManager.getLogger(ShuntServerHandler.class.getName());

    UICallBack mUICallBack;

    public ShuntServerHandler(UICallBack mUICallBack){
        this.mUICallBack = mUICallBack;
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
        String uuid=ctx.channel().id().asLongText();
        System.out.println("设备id为"+uuid+"的设备断开了连接");
        logger.info("设备id为"+uuid+"的设备断开了连接");
        System.out.println("目前有："+ShuntGatewayService.getChannels().size()+"个设备》》》》");


        Map<String, SocketChannel> map = ShuntGatewayService.getChannels();
        //遍历map中的值

        for (String key : map.keySet()) {

            if(map.get(key).equals(ctx.channel()))
                System.out.println("==========================>移除通道到通道列表"+key);
                ShuntGatewayService.removeGatewayChannel(key);
        }
    }



    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String uuid=ctx.channel().id().asLongText();

        System.out.println("一个客户端连接进来了："+uuid);
        System.out.println("《《《目前有："+ShuntGatewayService.getChannels().size()+"个设备");
        logger.info("一个客户端连接进来了："+uuid);
        logger.info("目前有："+ShuntGatewayService.getChannels().size()+"个设备");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {


        System.out.println("----------服务器收到命令----------------");
        if(msg instanceof CustomMsg) {
            CustomMsg customMsg = (CustomMsg)msg;
            ByteBuf customMsgFlag=customMsg.getFlag();
            Short customMsgLen=customMsg.getLen();
            byte customMsgChannel=customMsg.getChannel();
            byte customMsgProtocolVersion=customMsg.getProtocolVersion();
            ByteBuf customMsgEnd=customMsg.getEnd();
            ByteBuf customMsgBody=customMsg.getBody();
            byte[] req=new byte[customMsgBody.readableBytes()];
            customMsgBody.readBytes(req);
            String uuid=ctx.channel().id().asLongText();


            switch (req[0]){
                case 0x01: {

                    StringBuffer sb=new StringBuffer();

//                    收
                    BasePackage mBasePackage=new BasePackage();
                    ByteBuf customMsgBody2=Unpooled.buffer(req.length);
                    customMsgBody2.writeBytes(req);
                    byte[] ee=new byte[2];
                    customMsgEnd.getBytes(0,ee);
                    System.out.println("中转服务器收到的包0x01，包尾是--->"+ CRCUtil.bytesToHexString(ee));

                    sb.append("中转服务器收到的包0x01，包尾是--->"+ CRCUtil.bytesToHexString(ee));

                    mBasePackage.setCustomMsgAttribute(customMsgFlag,customMsgLen,customMsgChannel,customMsgProtocolVersion,customMsgBody2,customMsgEnd);
                    System.out.println("收到指令0x01");
                    if( mBasePackage.checkCRC()){
                        System.out.println("CRC验证成功！\r\n");
                    }else {
                        System.out.println("CRC验证失败！\r\n");
                    }
                    System.out.println(CRCUtil.bytesToHexString(req)+".");
                    System.out.println();
                    System.out.println("  服务器返回客户端0x01指令的结果");
                    byte[] mac = new byte[6];
                    System.arraycopy(req, 1, mac, 0, 6);
                    byte[] time = new byte[4];
                    System.arraycopy(req, 7, time, 0, 4);
                    CRCUtil.bytesToHexString(time);
                    System.out.println("\r\nmac地址是："+CRCUtil.bytesToHexString(mac)+"\r\n");
                    System.out.println("客户端发出的时间是："+CRCUtil.bytesToTime(time)+"\r\n");
                    System.out.println("服务器返回客户端0x01指令的结果 \r\n\r\n");
                    System.out.println();
                    BasePackage mBasePackage2=new BasePackage();
                    ByteBuf flag=Unpooled.buffer(2);
                    flag.writeBytes(new byte[]{(byte)0xEF,0x3A});
                    mBasePackage2.setFlag(flag);
                    mBasePackage2.setChannel((byte) 0x11);
                    mBasePackage2.setProtocolVersion((byte) 0x01);
                    byte[] order={0x01};
                    byte[] byte1=new byte[order.length+mac.length];
                    System.arraycopy(order, 0, byte1, 0, order.length);
                    System.arraycopy(mac, 0, byte1, order.length , mac.length);


//                    查看门锁服务器状态，分一个压力小的服务器给请求的门锁



//                    域名凑到200
                    String urls="127.0.0.1";
                    byte[] url1=urls.getBytes("utf-8");
                    if(url1.length>200){
                        logger.info("门锁服务器url大于200个字节，无法分配对应服务器");
                        return;
                    }
                    byte[] url2=new byte[200-url1.length];
                    byte[] url = new byte[200];
                    System.arraycopy(url1, 0, url, 0, url1.length);
                    System.arraycopy(url2, 0, url, url1.length , url2.length);
//                   port
                    byte[] port={0x59,0x52};
                    byte[] result={0x01};
                    ByteBuf buf=Unpooled.copiedBuffer(port,time,result);
                    byte[] byte2=new byte[buf.readableBytes()];
                    buf.readBytes(byte2);
                    byte[] data = new byte[byte1.length + url.length + byte2.length];
                    System.arraycopy(byte1, 0, data, 0, byte1.length);
                    System.arraycopy(url, 0, data, byte1.length, url.length);
                    System.arraycopy(byte2, 0, data, byte1.length + url.length, byte2.length);
                    ByteBuf body=Unpooled.buffer(data.length);
                    body.writeBytes(data);
                    mBasePackage2.setBody(body);
                    CustomMsg customMsgaa=mBasePackage2.getCustomMsg();
                    byte[] ee2=new byte[2];
                    customMsgaa.getEnd().getBytes(0,ee2);
                    System.out.println("中转服务器返回客户端的包，包尾是--->"+ CRCUtil.bytesToHexString(ee2));
                    ctx.writeAndFlush(customMsgaa);

                    sb.append("中转服务分配："+urls+"--->"+port[0]+port[1]);

                    mUICallBack.refreshText(sb.toString());
                }
                    break;

                case 0x21:{
                    StringBuffer sb=new StringBuffer();
//                    读取锁服务器和门锁的mac地址
                    BasePackage mBasePackage=new BasePackage();
                    ByteBuf customMsgBody2=Unpooled.buffer(req.length);
                    customMsgBody2.writeBytes(req);
                    byte[] ee=new byte[2];
                    customMsgEnd.getBytes(0,ee);
                    System.out.println("中转服务器/api 收到的包0x21，包尾是--->"+ CRCUtil.bytesToHexString(ee));

                    sb.append("中转服务器/api 收到的包0x21，包尾是--->"+ CRCUtil.bytesToHexString(ee));

                    mBasePackage.setCustomMsgAttribute(customMsgFlag,customMsgLen,customMsgChannel,customMsgProtocolVersion,customMsgBody2,customMsgEnd);
                    System.out.println("收到指令0x01");
                    if( mBasePackage.checkCRC()){
                        System.out.println("CRC验证成功！\r\n");
                    }else {
                        System.out.println("CRC验证失败！\r\n");
                    }
                    System.out.println(CRCUtil.bytesToHexString(req)+".");
                    System.out.println();
                    System.out.println("  服务器返回客户端0x21指令的结果");
                    byte[] lockMac = new byte[6];
                    System.arraycopy(req, 1, lockMac, 0, 6);
                    byte[] serverMac = new byte[6];
                    System.arraycopy(req, 7, serverMac, 0, 6);

                    System.out.println("\r\n门锁的mac地址是："+CRCUtil.bytesToHexString(lockMac)+"\r\n");
                    System.out.println("\r\n门锁服务器的mac地址是："+CRCUtil.bytesToHexString(serverMac)+"\r\n");
                    System.out.println("服务器返回客户端0x21指令的结果 \r\n\r\n");
                    System.out.println();

                    System.out.println("==========================>加入mac为"+CRCUtil.bytesToHexString(serverMac)+"到通道列表");
                    ShuntGatewayService.addGatewayChannel(CRCUtil.bytesToHexString(serverMac), (SocketChannel) ctx.channel());

//                    往数据库里面记录门锁和他所管理的门锁服务器
                    TempMacManagerService.lockMAC=CRCUtil.bytesToHexString(lockMac);
                    TempMacManagerService.serverMAC=CRCUtil.bytesToHexString(serverMac);
                    TempMacManagerService.lockMACB=lockMac;
                    TempMacManagerService.serverMACB=serverMac;


                    BasePackage mBasePackage2=new BasePackage();
                    ByteBuf flag=Unpooled.buffer(2);
                    flag.writeBytes(new byte[]{(byte)0xEF,0x3A});
                    mBasePackage2.setFlag(flag);
                    mBasePackage2.setChannel((byte) 0x11);
                    mBasePackage2.setProtocolVersion((byte) 0x01);
                    byte[] result={0x01};
                    ByteBuf returnByteBuf=Unpooled.copiedBuffer(req,result);
                    mBasePackage2.setBody(returnByteBuf);
                    CustomMsg customMsgaa=mBasePackage2.getCustomMsg();
                    byte[] ee2=new byte[2];
                    customMsgaa.getEnd().getBytes(0,ee2);
                    System.out.println("返回锁服务器0x21处理结果，包尾是--->"+ CRCUtil.bytesToHexString(ee2));
                    ctx.writeAndFlush(customMsgaa);


                }
                break;
                case 0x26:{
                    StringBuffer sb=new StringBuffer();
//                    读取锁服务器和门锁的mac地址
                    BasePackage mBasePackage=new BasePackage();
                    ByteBuf customMsgBody2=Unpooled.buffer(req.length);
                    customMsgBody2.writeBytes(req);
                    byte[] ee=new byte[2];
                    customMsgEnd.getBytes(0,ee);
                    System.out.println("中转服务器/api 收到的包0x21，包尾是--->"+ CRCUtil.bytesToHexString(ee));

                    sb.append("中转服务器/api 收到的包0x21，包尾是--->"+ CRCUtil.bytesToHexString(ee));

                    mBasePackage.setCustomMsgAttribute(customMsgFlag,customMsgLen,customMsgChannel,customMsgProtocolVersion,customMsgBody2,customMsgEnd);
                    System.out.println("收到指令0x01");
                    if( mBasePackage.checkCRC()){
                        System.out.println("CRC验证成功！\r\n");
                    }else {
                        System.out.println("CRC验证失败！\r\n");
                    }
                    System.out.println(CRCUtil.bytesToHexString(req)+".");
                    System.out.println();
                    System.out.println("  服务器返回客户端0x21指令的结果");
                    byte[] lockMac = new byte[6];
                    System.arraycopy(req, 1, lockMac, 0, 6);
                    byte[] serverMac = new byte[6];
                    System.arraycopy(req, 7, serverMac, 0, 6);

                    System.out.println("\r\n门锁的mac地址是："+CRCUtil.bytesToHexString(lockMac)+"\r\n");
                    System.out.println("\r\n门锁服务器的mac地址是："+CRCUtil.bytesToHexString(serverMac)+"\r\n");
                    System.out.println("服务器返回客户端0x21指令的结果 \r\n\r\n");
                    System.out.println();

                }
                break;

                case 0x27:{
                    StringBuffer sb=new StringBuffer();
//                    读取锁服务器和门锁的mac地址
                    BasePackage mBasePackage=new BasePackage();
                    ByteBuf customMsgBody2=Unpooled.buffer(req.length);
                    customMsgBody2.writeBytes(req);
                    byte[] ee=new byte[2];
                    customMsgEnd.getBytes(0,ee);
                    System.out.println("中转服务器/api 收到的包0x27，包尾是--->"+ CRCUtil.bytesToHexString(ee));

                    sb.append("中转服务器/api 收到的包0x27，包尾是--->"+ CRCUtil.bytesToHexString(ee));

                    mBasePackage.setCustomMsgAttribute(customMsgFlag,customMsgLen,customMsgChannel,customMsgProtocolVersion,customMsgBody2,customMsgEnd);
                    System.out.println("收到指令0x01");
                    if( mBasePackage.checkCRC()){
                        System.out.println("CRC验证成功！\r\n");
                    }else {
                        System.out.println("CRC验证失败！\r\n");
                    }
                    System.out.println(CRCUtil.bytesToHexString(req)+".");
                    System.out.println();
                    System.out.println("  服务器返回客户端0x21指令的结果");
                    byte[] lockMac = new byte[6];
                    System.arraycopy(req, 1, lockMac, 0, 6);
                    byte[] serverMac = new byte[6];
                    System.arraycopy(req, 7, serverMac, 0, 6);

                    System.out.println("\r\n门锁的mac地址是："+CRCUtil.bytesToHexString(lockMac)+"\r\n");
                    System.out.println("\r\n门锁服务器的mac地址是："+CRCUtil.bytesToHexString(serverMac)+"\r\n");
                    System.out.println("服务器返回客户端0x27指令的结果 \r\n\r\n");
                    System.out.println();






                }
                break;


               default:
                break;
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
