package orz.xuchao.server.LockServers;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import orz.xuchao.server.bean.BasePackage;
import orz.xuchao.server.bean.CustomMsg;
import orz.xuchao.server.utils.CRCUtil;

import java.util.logging.Logger;


/**
 * Created by Administrator on 2017/7/6 0006.
 */
public class LockToApiHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger=Logger.getLogger(LockToApiHandler.class.getName());
//    private UICallBack mUICallBack;
//
//
//    public LockToApiHandler(UICallBack mUICallBack){
//        this.mUICallBack = mUICallBack;
//
//    }

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

//        连接服务器后开始定时发送心跳包




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

        if(msg instanceof CustomMsg) {

            System.out.println("-------客户端收到命令-------");
            CustomMsg customMsg = (CustomMsg)msg;
//            ByteBuf buf=customMsg.getBody();

            ByteBuf customMsgFlag=customMsg.getFlag();
            Short customMsgLen=customMsg.getLen();
            byte customMsgChannel=customMsg.getChannel();
            byte customMsgProtocolVersion=customMsg.getProtocolVersion();
            ByteBuf customMsgEnd=customMsg.getEnd();
            ByteBuf customMsgBody=customMsg.getBody();


            byte[] req=new byte[customMsgBody.readableBytes()];
            customMsgBody.readBytes(req);


            switch (req[0]){
                case 0x22:
                {

                    System.out.println("==========================>所服务器去中心服务器注册门锁的mac地址   的操作完成");
                    StringBuffer sb=new StringBuffer();
//                    读取锁服务器和门锁的mac地址
                    BasePackage mBasePackage=new BasePackage();
                    ByteBuf customMsgBody2=Unpooled.buffer(req.length);
                    customMsgBody2.writeBytes(req);
                    byte[] ee=new byte[2];
                    customMsgEnd.getBytes(0,ee);
                    System.out.println("门锁服务器 收到的包0x21，包尾是--->"+ CRCUtil.bytesToHexString(ee));

                    sb.append("门锁服务器 收到的包0x21，包尾是--->"+ CRCUtil.bytesToHexString(ee));

                    mBasePackage.setCustomMsgAttribute(customMsgFlag,customMsgLen,customMsgChannel,customMsgProtocolVersion,customMsgBody2,customMsgEnd);
                    System.out.println("收到指令0x21");
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
                    byte[] result = new byte[1];
                    System.arraycopy(req, 17, result, 0, 1);




                    System.out.println("\r\n门锁的mac地址是："+CRCUtil.bytesToHexString(lockMac)+"\r\n");
                    System.out.println("\r\n门锁服务器的mac地址是："+CRCUtil.bytesToHexString(serverMac)+"\r\n");
                    System.out.println(req[req.length-1]);
                    System.out.println("服务器返回客户端0x21指令的结果  "+result[0]+"\r\n\r\n");
                    System.out.println();



                }
                break;
                case 0x26:{
                    System.out.println("客户端收到服务器请求0x06 ");
                    BasePackage mBasePackage=new BasePackage();
                    ByteBuf customMsgBody2=Unpooled.buffer(req.length);
                    customMsgBody2.writeBytes(req);
                    byte[] ee=new byte[2];
                    customMsgEnd.getBytes(0,ee);
                    System.out.println("客户端收到的包，包尾是--->"+ CRCUtil.bytesToHexString(ee));
                    mBasePackage.setCustomMsgAttribute(customMsgFlag,customMsgLen,customMsgChannel,customMsgProtocolVersion,customMsgBody2,customMsgEnd);
                    StringBuffer sb=new StringBuffer();
                    sb.append("客户端收到服务器请求0x06 \r\n ");
                    System.out.println("客户端收到服务器请求0x06");
                    if( mBasePackage.checkCRC()){
                        sb.append("CRC验证成功！\r\n");
                    }else {
                        sb.append("CRC验证失败！\r\n");
                    }

                    byte[] mac=new byte[6];
                    System.arraycopy(req, 1, mac, 0, 6);
                    byte[] id=new byte[8];
                    System.arraycopy(req, 7, id, 0, 8);
                    byte[] type=new byte[1];
                    System.arraycopy(req, 15, type, 0, 1);

                    byte[] time=new byte[4];
                    System.arraycopy(req, 16, time, 0, 4);


                    sb.append(CRCUtil.bytesToHexString(req)+".");
                    System.out.println();
                    System.out.println("  客户端应答服务器0x06请求并返回数据");
                    sb.append("\r\n客户端应答服务器0x06请求并返回数据 \r\n\r\n");
                    System.out.println();




                    BasePackage mBasePackage2=new BasePackage();
                    ByteBuf flag=Unpooled.buffer(2);
                    flag.writeBytes(new byte[]{(byte)0xEF,0x3A});
                    mBasePackage2.setFlag(flag);
                    mBasePackage2.setChannel((byte) 0x11);
                    mBasePackage2.setProtocolVersion((byte) 0x01);
                    byte[] resoult={0x01};
                    ByteBuf body=Unpooled.copiedBuffer(req,resoult);
                    mBasePackage2.setBody(body);
                    CustomMsg customMsgaa=mBasePackage2.getCustomMsg();
                    byte[] ee2=new byte[2];
                    customMsgaa.getEnd().getBytes(0,ee2);
                    System.out.println("客户端应答应服务器的包，包尾是--->"+ CRCUtil.bytesToHexString(ee2));
                    ctx.writeAndFlush(customMsgaa);



//                    api服务器的客户端（锁服务器）收到 api服务器的请求，获取到锁的通道，给锁发命令开门
//                    门锁服务器通知门锁开门
                    SocketChannel obj = TempTestChannelManagerService.getGatewayChannel(CRCUtil.bytesToHexString(mac));
                    BasePackage mBasePackageToLock=new BasePackage();
                    ByteBuf flagToLock=Unpooled.buffer(2);
                    flagToLock.writeBytes(new byte[]{(byte)0xEF,0x3A});
                    mBasePackageToLock.setFlag(flagToLock);
                    mBasePackageToLock.setChannel((byte) 0x01);
                    mBasePackageToLock.setProtocolVersion((byte) 0x01);

                    byte[] orlder={0x06};
                    ByteBuf body2=Unpooled.copiedBuffer(orlder,mac,id,type,time);



                    mBasePackageToLock.setBody(body2);
                    CustomMsg customMsgToLock=mBasePackageToLock.getCustomMsg();
                    byte[] eeToLock=new byte[2];
                    customMsgToLock.getEnd().getBytes(0,eeToLock);
                    System.out.println("客户端发出的包，包尾是--->"+ CRCUtil.bytesToHexString(eeToLock));
                    obj.writeAndFlush(customMsgToLock);


                }
                break;
                case 0x23:{
                    BasePackage mBasePackage=new BasePackage();
                    ByteBuf customMsgBody2=Unpooled.buffer(req.length);
                    customMsgBody2.writeBytes(req);
                    byte[] ee=new byte[2];
                    customMsgEnd.getBytes(0,ee);
                    System.out.println("客户端收到的包，包尾是--->"+ CRCUtil.bytesToHexString(ee));
                    mBasePackage.setCustomMsgAttribute(customMsgFlag,customMsgLen,customMsgChannel,customMsgProtocolVersion,customMsgBody2,customMsgEnd);
                    StringBuffer sb=new StringBuffer();
                    sb.append("客户端收到服务器请求0x23 \r\n ");
                    System.out.println("客户端收到服务器请求0x23");
                    if( mBasePackage.checkCRC()){
                        sb.append("CRC验证成功！\r\n");
                    }else {
                        sb.append("CRC验证失败！\r\n");
                    }

                    byte[] mac=new byte[6];
                    System.arraycopy(req, 1, mac, 0, 6);

                    byte[] time=new byte[4];
                    System.arraycopy(req, 7, time, 0, 4);


                    sb.append(CRCUtil.bytesToHexString(req)+".");
                    System.out.println();
                    System.out.println("  客户端应答服务器0x23请求并返回数据");
                    sb.append("\r\n客户端应答服务器0x23请求并返回数据 \r\n\r\n");
                    System.out.println();


//                    返回请求
                    BasePackage mBasePackage2=new BasePackage();
                    ByteBuf flag=Unpooled.buffer(2);
                    flag.writeBytes(new byte[]{(byte)0xEF,0x3A});
                    mBasePackage2.setFlag(flag);
                    mBasePackage2.setChannel((byte) 0x11);
                    mBasePackage2.setProtocolVersion((byte) 0x01);
                    byte[] resoult={0x01};
                    ByteBuf body=Unpooled.copiedBuffer(req,resoult);
                    mBasePackage2.setBody(body);
                    CustomMsg customMsgaa=mBasePackage2.getCustomMsg();
                    byte[] ee2=new byte[2];
                    customMsgaa.getEnd().getBytes(0,ee2);
                    System.out.println("客户端应答应服务器的包，包尾是--->"+ CRCUtil.bytesToHexString(ee2));
                    ctx.writeAndFlush(customMsgaa);



//                    api服务器的客户端（锁服务器）收到 api服务器的请求，获取到锁的通道，给锁发命令开门
//                    门锁服务器通知门锁开门
                    SocketChannel obj = TempTestChannelManagerService.getGatewayChannel(CRCUtil.bytesToHexString(mac));
                    BasePackage mBasePackageToLock=new BasePackage();
                    ByteBuf flagToLock=Unpooled.buffer(2);
                    flagToLock.writeBytes(new byte[]{(byte)0xEF,0x3A});
                    mBasePackageToLock.setFlag(flagToLock);
                    mBasePackageToLock.setChannel((byte) 0x01);
                    mBasePackageToLock.setProtocolVersion((byte) 0x01);
                    byte[] orlder={0x03};
                    ByteBuf body2=Unpooled.copiedBuffer(orlder,mac,time);
                    mBasePackageToLock.setBody(body2);
                    CustomMsg customMsgToLock=mBasePackageToLock.getCustomMsg();
                    byte[] eeToLock=new byte[2];
                    customMsgToLock.getEnd().getBytes(0,eeToLock);
                    System.out.println("客户端发出的包，包尾是--->"+ CRCUtil.bytesToHexString(eeToLock));
                    obj.writeAndFlush(customMsgToLock);


                }
                break;
                case 0x24:{

                    BasePackage mBasePackage=new BasePackage();
                    ByteBuf customMsgBody2=Unpooled.buffer(req.length);
                    customMsgBody2.writeBytes(req);
                    byte[] ee=new byte[2];
                    customMsgEnd.getBytes(0,ee);
                    System.out.println("客户端收到的包，包尾是--->"+ CRCUtil.bytesToHexString(ee));
                    mBasePackage.setCustomMsgAttribute(customMsgFlag,customMsgLen,customMsgChannel,customMsgProtocolVersion,customMsgBody2,customMsgEnd);
                    StringBuffer sb=new StringBuffer();
                    sb.append("客户端收到服务器请求0x24 \r\n ");
                    System.out.println("客户端收到服务器请求0x24");
                    if( mBasePackage.checkCRC()){
                        sb.append("CRC验证成功！\r\n");
                    }else {
                        sb.append("CRC验证失败！\r\n");
                    }

                    byte[] mac=new byte[6];
                    System.arraycopy(req, 1, mac, 0, 6);

                    sb.append(CRCUtil.bytesToHexString(req)+".");
                    System.out.println();
                    System.out.println("  客户端应答服务器0x24请求并返回数据");
                    sb.append("\r\n客户端应答服务器0x04请求并返回数据 \r\n\r\n");
                    System.out.println();


//                    返回请求
                    BasePackage mBasePackage2=new BasePackage();
                    ByteBuf flag=Unpooled.buffer(2);
                    flag.writeBytes(new byte[]{(byte)0xEF,0x3A});
                    mBasePackage2.setFlag(flag);
                    mBasePackage2.setChannel((byte) 0x11);
                    mBasePackage2.setProtocolVersion((byte) 0x01);
                    byte[] resoult={0x01};
                    ByteBuf body=Unpooled.copiedBuffer(req,resoult);
                    mBasePackage2.setBody(body);
                    CustomMsg customMsgaa=mBasePackage2.getCustomMsg();
                    byte[] ee2=new byte[2];
                    customMsgaa.getEnd().getBytes(0,ee2);
                    System.out.println("客户端应答应服务器的包，包尾是--->"+ CRCUtil.bytesToHexString(ee2));
                    ctx.writeAndFlush(customMsgaa);




//                    api服务器的客户端（锁服务器）收到 api服务器的请求，获取到锁的通道，给锁发命令
                    SocketChannel obj = TempTestChannelManagerService.getGatewayChannel(CRCUtil.bytesToHexString(mac));
                    BasePackage mBasePackageToLock=new BasePackage();
                    ByteBuf flagToLock=Unpooled.buffer(2);
                    flagToLock.writeBytes(new byte[]{(byte)0xEF,0x3A});
                    mBasePackageToLock.setFlag(flagToLock);
                    mBasePackageToLock.setChannel((byte) 0x01);
                    mBasePackageToLock.setProtocolVersion((byte) 0x01);

                    req[0]=0x04;
                    ByteBuf body3=Unpooled.copiedBuffer(req,resoult);
                    mBasePackageToLock.setBody(body3);

                    CustomMsg customMsgToLock=mBasePackageToLock.getCustomMsg();
                    byte[] eeToLock=new byte[2];
                    customMsgToLock.getEnd().getBytes(0,eeToLock);
                    System.out.println("客户端发出的包，包尾是--->"+ CRCUtil.bytesToHexString(eeToLock));
                    obj.writeAndFlush(customMsgToLock);


                }
                break;
                case 0x25:{

                    BasePackage mBasePackage=new BasePackage();
                    ByteBuf customMsgBody2=Unpooled.buffer(req.length);
                    customMsgBody2.writeBytes(req);
                    byte[] ee=new byte[2];
                    customMsgEnd.getBytes(0,ee);
                    System.out.println("客户端收到的包，包尾是--->"+ CRCUtil.bytesToHexString(ee));
                    mBasePackage.setCustomMsgAttribute(customMsgFlag,customMsgLen,customMsgChannel,customMsgProtocolVersion,customMsgBody2,customMsgEnd);
                    StringBuffer sb=new StringBuffer();
                    sb.append("客户端收到服务器请求0x25 \r\n ");
                    System.out.println("客户端收到服务器请求0x25");
                    if( mBasePackage.checkCRC()){
                        sb.append("CRC验证成功！\r\n");
                    }else {
                        sb.append("CRC验证失败！\r\n");
                    }
                    sb.append(CRCUtil.bytesToHexString(req)+".");

                    byte[] mac=new byte[6];
                    System.arraycopy(req, 1, mac, 0, 6);
                    System.out.println();
                    System.out.println("  客户端应答服务器0x05请求并返回数据");
                    sb.append("\r\n客户端应答服务器0x05请求并返回数据 \r\n\r\n");
                    System.out.println();

                    //                    返回请求
                    BasePackage mBasePackage2=new BasePackage();
                    ByteBuf flag=Unpooled.buffer(2);
                    flag.writeBytes(new byte[]{(byte)0xEF,0x3A});
                    mBasePackage2.setFlag(flag);
                    mBasePackage2.setChannel((byte) 0x11);
                    mBasePackage2.setProtocolVersion((byte) 0x01);
                    byte[] resoult={0x01};
                    ByteBuf body=Unpooled.copiedBuffer(req,resoult);
                    mBasePackage2.setBody(body);
                    CustomMsg customMsgaa=mBasePackage2.getCustomMsg();
                    byte[] ee2=new byte[2];
                    customMsgaa.getEnd().getBytes(0,ee2);
                    System.out.println("客户端应答应服务器的包，包尾是--->"+ CRCUtil.bytesToHexString(ee2));
                    ctx.writeAndFlush(customMsgaa);

                    //                    api服务器的客户端（锁服务器）收到 api服务器的请求，获取到锁的通道，给锁发命令
                    SocketChannel obj = TempTestChannelManagerService.getGatewayChannel(CRCUtil.bytesToHexString(mac));
                    BasePackage mBasePackageToLock=new BasePackage();
                    ByteBuf flagToLock=Unpooled.buffer(2);
                    flagToLock.writeBytes(new byte[]{(byte)0xEF,0x3A});
                    mBasePackageToLock.setFlag(flagToLock);
                    mBasePackageToLock.setChannel((byte) 0x01);
                    mBasePackageToLock.setProtocolVersion((byte) 0x01);

                    req[0]=0x05;
                    ByteBuf body3=Unpooled.copiedBuffer(req,resoult);
                    mBasePackageToLock.setBody(body3);

                    CustomMsg customMsgToLock=mBasePackageToLock.getCustomMsg();
                    byte[] eeToLock=new byte[2];
                    customMsgToLock.getEnd().getBytes(0,eeToLock);
                    System.out.println("客户端发出的包，包尾是--->"+ CRCUtil.bytesToHexString(eeToLock));
                    obj.writeAndFlush(customMsgToLock);


                }
                break;
                case 0x29:{

                    BasePackage mBasePackage=new BasePackage();
                    ByteBuf customMsgBody2=Unpooled.buffer(req.length);
                    customMsgBody2.writeBytes(req);
                    byte[] ee=new byte[2];
                    customMsgEnd.getBytes(0,ee);
                    System.out.println("客户端收到的包，包尾是--->"+ CRCUtil.bytesToHexString(ee));
                    mBasePackage.setCustomMsgAttribute(customMsgFlag,customMsgLen,customMsgChannel,customMsgProtocolVersion,customMsgBody2,customMsgEnd);
                    StringBuffer sb=new StringBuffer();
                    sb.append("客户端收到服务器请求0x29 \r\n ");
                    System.out.println("客户端收到服务器请求0x29");
                    if( mBasePackage.checkCRC()){
                        sb.append("CRC验证成功！\r\n");
                    }else {
                        sb.append("CRC验证失败！\r\n");
                    }
                    sb.append(CRCUtil.bytesToHexString(req)+".");

                    byte[] mac=new byte[6];
                    System.arraycopy(req, 1, mac, 0, 6);
                    System.out.println();
                    System.out.println("  客户端应答服务器0x05请求并返回数据");
                    sb.append("\r\n客户端应答服务器0x05请求并返回数据 \r\n\r\n");
                    System.out.println();

                    //                    返回请求
                    BasePackage mBasePackage2=new BasePackage();
                    ByteBuf flag=Unpooled.buffer(2);
                    flag.writeBytes(new byte[]{(byte)0xEF,0x3A});
                    mBasePackage2.setFlag(flag);
                    mBasePackage2.setChannel((byte) 0x11);
                    mBasePackage2.setProtocolVersion((byte) 0x01);
                    byte[] resoult={0x01};
                    ByteBuf body=Unpooled.copiedBuffer(req,resoult);
                    mBasePackage2.setBody(body);
                    CustomMsg customMsgaa=mBasePackage2.getCustomMsg();
                    byte[] ee2=new byte[2];
                    customMsgaa.getEnd().getBytes(0,ee2);
                    System.out.println("客户端应答应服务器的包，包尾是--->"+ CRCUtil.bytesToHexString(ee2));
                    ctx.writeAndFlush(customMsgaa);

                    //                    api服务器的客户端（锁服务器）收到 api服务器的请求，获取到锁的通道，给锁发命令
                    SocketChannel obj = TempTestChannelManagerService.getGatewayChannel(CRCUtil.bytesToHexString(mac));
                    BasePackage mBasePackageToLock=new BasePackage();
                    ByteBuf flagToLock=Unpooled.buffer(2);
                    flagToLock.writeBytes(new byte[]{(byte)0xEF,0x3A});
                    mBasePackageToLock.setFlag(flagToLock);
                    mBasePackageToLock.setChannel((byte) 0x01);
                    mBasePackageToLock.setProtocolVersion((byte) 0x01);

                    req[0]=0x09;
                    ByteBuf body3=Unpooled.copiedBuffer(req,resoult);
                    mBasePackageToLock.setBody(body3);

                    CustomMsg customMsgToLock=mBasePackageToLock.getCustomMsg();
                    byte[] eeToLock=new byte[2];
                    customMsgToLock.getEnd().getBytes(0,eeToLock);
                    System.out.println("客户端发出的包，包尾是--->"+ CRCUtil.bytesToHexString(eeToLock));
                    obj.writeAndFlush(customMsgToLock);


                }
                break;
                case 0x2C:{

                    BasePackage mBasePackage=new BasePackage();
                    ByteBuf customMsgBody2=Unpooled.buffer(req.length);
                    customMsgBody2.writeBytes(req);
                    byte[] ee=new byte[2];
                    customMsgEnd.getBytes(0,ee);
                    System.out.println("客户端收到的包，包尾是--->"+ CRCUtil.bytesToHexString(ee));
                    mBasePackage.setCustomMsgAttribute(customMsgFlag,customMsgLen,customMsgChannel,customMsgProtocolVersion,customMsgBody2,customMsgEnd);
                    StringBuffer sb=new StringBuffer();
                    sb.append("客户端收到服务器请求0x2C \r\n ");
                    System.out.println("客户端收到服务器请求0x2C");
                    if( mBasePackage.checkCRC()){
                        sb.append("CRC验证成功！\r\n");
                    }else {
                        sb.append("CRC验证失败！\r\n");
                    }
                    sb.append(CRCUtil.bytesToHexString(req)+".");

                    byte[] mac=new byte[6];
                    System.arraycopy(req, 1, mac, 0, 6);
                    System.out.println();
                    System.out.println("  客户端应答服务器0x2C请求并返回数据");
                    sb.append("\r\n客户端应答服务器0x2C请求并返回数据 \r\n\r\n");
                    System.out.println();

                    //                    返回请求
                    BasePackage mBasePackage2=new BasePackage();
                    ByteBuf flag=Unpooled.buffer(2);
                    flag.writeBytes(new byte[]{(byte)0xEF,0x3A});
                    mBasePackage2.setFlag(flag);
                    mBasePackage2.setChannel((byte) 0x11);
                    mBasePackage2.setProtocolVersion((byte) 0x01);
                    byte[] resoult={0x01};
                    ByteBuf body=Unpooled.copiedBuffer(req,resoult);
                    mBasePackage2.setBody(body);
                    CustomMsg customMsgaa=mBasePackage2.getCustomMsg();
                    byte[] ee2=new byte[2];
                    customMsgaa.getEnd().getBytes(0,ee2);
                    System.out.println("客户端应答应服务器的包，包尾是--->"+ CRCUtil.bytesToHexString(ee2));
                    ctx.writeAndFlush(customMsgaa);

                    //                    api服务器的客户端（锁服务器）收到 api服务器的请求，获取到锁的通道，给锁发命令
                    SocketChannel obj = TempTestChannelManagerService.getGatewayChannel(CRCUtil.bytesToHexString(mac));
                    BasePackage mBasePackageToLock=new BasePackage();
                    ByteBuf flagToLock=Unpooled.buffer(2);
                    flagToLock.writeBytes(new byte[]{(byte)0xEF,0x3A});
                    mBasePackageToLock.setFlag(flagToLock);
                    mBasePackageToLock.setChannel((byte) 0x01);
                    mBasePackageToLock.setProtocolVersion((byte) 0x01);

                    req[0]=0x0C;
                    ByteBuf body3=Unpooled.copiedBuffer(req,resoult);
                    mBasePackageToLock.setBody(body3);

                    CustomMsg customMsgToLock=mBasePackageToLock.getCustomMsg();
                    byte[] eeToLock=new byte[2];
                    customMsgToLock.getEnd().getBytes(0,eeToLock);
                    System.out.println("客户端发出的包，包尾是--->"+ CRCUtil.bytesToHexString(eeToLock));
                    obj.writeAndFlush(customMsgToLock);


                }
                break;
                case 0x2D:{

                    BasePackage mBasePackage=new BasePackage();
                    ByteBuf customMsgBody2=Unpooled.buffer(req.length);
                    customMsgBody2.writeBytes(req);
                    byte[] ee=new byte[2];
                    customMsgEnd.getBytes(0,ee);
                    System.out.println("客户端收到的包，包尾是--->"+ CRCUtil.bytesToHexString(ee));
                    mBasePackage.setCustomMsgAttribute(customMsgFlag,customMsgLen,customMsgChannel,customMsgProtocolVersion,customMsgBody2,customMsgEnd);
                    StringBuffer sb=new StringBuffer();
                    sb.append("客户端收到服务器请求0x2D \r\n ");
                    System.out.println("客户端收到服务器请求0x2D");
                    if( mBasePackage.checkCRC()){
                        sb.append("CRC验证成功！\r\n");
                    }else {
                        sb.append("CRC验证失败！\r\n");
                    }
                    sb.append(CRCUtil.bytesToHexString(req)+".");

                    byte[] mac=new byte[6];
                    System.arraycopy(req, 1, mac, 0, 6);
                    System.out.println();
                    System.out.println("  客户端应答服务器0x2C请求并返回数据");
                    sb.append("\r\n客户端应答服务器0x2C请求并返回数据 \r\n\r\n");
                    System.out.println();

                    //                    返回请求
                    BasePackage mBasePackage2=new BasePackage();
                    ByteBuf flag=Unpooled.buffer(2);
                    flag.writeBytes(new byte[]{(byte)0xEF,0x3A});
                    mBasePackage2.setFlag(flag);
                    mBasePackage2.setChannel((byte) 0x11);
                    mBasePackage2.setProtocolVersion((byte) 0x01);
                    byte[] resoult={0x01};
                    ByteBuf body=Unpooled.copiedBuffer(req,resoult);
                    mBasePackage2.setBody(body);
                    CustomMsg customMsgaa=mBasePackage2.getCustomMsg();
                    byte[] ee2=new byte[2];
                    customMsgaa.getEnd().getBytes(0,ee2);
                    System.out.println("客户端应答应服务器的包，包尾是--->"+ CRCUtil.bytesToHexString(ee2));
                    ctx.writeAndFlush(customMsgaa);

                    //                    api服务器的客户端（锁服务器）收到 api服务器的请求，获取到锁的通道，给锁发命令
                    SocketChannel obj = TempTestChannelManagerService.getGatewayChannel(CRCUtil.bytesToHexString(mac));
                    BasePackage mBasePackageToLock=new BasePackage();
                    ByteBuf flagToLock=Unpooled.buffer(2);
                    flagToLock.writeBytes(new byte[]{(byte)0xEF,0x3A});
                    mBasePackageToLock.setFlag(flagToLock);
                    mBasePackageToLock.setChannel((byte) 0x01);
                    mBasePackageToLock.setProtocolVersion((byte) 0x01);

                    req[0]=0x0D;
                    ByteBuf body3=Unpooled.copiedBuffer(req,resoult);
                    mBasePackageToLock.setBody(body3);

                    CustomMsg customMsgToLock=mBasePackageToLock.getCustomMsg();
                    byte[] eeToLock=new byte[2];
                    customMsgToLock.getEnd().getBytes(0,eeToLock);
                    System.out.println("客户端发出的包，包尾是--->"+ CRCUtil.bytesToHexString(eeToLock));
                    obj.writeAndFlush(customMsgToLock);


                }
                break;
                case 0x2E:{

                    BasePackage mBasePackage=new BasePackage();
                    ByteBuf customMsgBody2=Unpooled.buffer(req.length);
                    customMsgBody2.writeBytes(req);
                    byte[] ee=new byte[2];
                    customMsgEnd.getBytes(0,ee);
                    System.out.println("客户端收到的包，包尾是--->"+ CRCUtil.bytesToHexString(ee));
                    mBasePackage.setCustomMsgAttribute(customMsgFlag,customMsgLen,customMsgChannel,customMsgProtocolVersion,customMsgBody2,customMsgEnd);
                    StringBuffer sb=new StringBuffer();
                    sb.append("客户端收到服务器请求0x2E \r\n ");
                    System.out.println("客户端收到服务器请求0x2E");
                    if( mBasePackage.checkCRC()){
                        sb.append("CRC验证成功！\r\n");
                    }else {
                        sb.append("CRC验证失败！\r\n");
                    }
                    sb.append(CRCUtil.bytesToHexString(req)+".");

                    byte[] mac=new byte[6];
                    System.arraycopy(req, 1, mac, 0, 6);
                    System.out.println();
                    System.out.println("  客户端应答服务器0x2E请求并返回数据");
                    sb.append("\r\n客户端应答服务器0x2E请求并返回数据 \r\n\r\n");
                    System.out.println();

                    //                    返回请求
                    BasePackage mBasePackage2=new BasePackage();
                    ByteBuf flag=Unpooled.buffer(2);
                    flag.writeBytes(new byte[]{(byte)0xEF,0x3A});
                    mBasePackage2.setFlag(flag);
                    mBasePackage2.setChannel((byte) 0x11);
                    mBasePackage2.setProtocolVersion((byte) 0x01);
                    byte[] resoult={0x01};
                    ByteBuf body=Unpooled.copiedBuffer(req,resoult);
                    mBasePackage2.setBody(body);
                    CustomMsg customMsgaa=mBasePackage2.getCustomMsg();
                    byte[] ee2=new byte[2];
                    customMsgaa.getEnd().getBytes(0,ee2);
                    System.out.println("客户端应答应服务器的包，包尾是--->"+ CRCUtil.bytesToHexString(ee2));
                    ctx.writeAndFlush(customMsgaa);

                    //                    api服务器的客户端（锁服务器）收到 api服务器的请求，获取到锁的通道，给锁发命令
                    SocketChannel obj = TempTestChannelManagerService.getGatewayChannel(CRCUtil.bytesToHexString(mac));
                    BasePackage mBasePackageToLock=new BasePackage();
                    ByteBuf flagToLock=Unpooled.buffer(2);
                    flagToLock.writeBytes(new byte[]{(byte)0xEF,0x3A});
                    mBasePackageToLock.setFlag(flagToLock);
                    mBasePackageToLock.setChannel((byte) 0x01);
                    mBasePackageToLock.setProtocolVersion((byte) 0x01);

                    req[0]=0x0E;
                    ByteBuf body3=Unpooled.copiedBuffer(req,resoult);
                    mBasePackageToLock.setBody(body3);

                    CustomMsg customMsgToLock=mBasePackageToLock.getCustomMsg();
                    byte[] eeToLock=new byte[2];
                    customMsgToLock.getEnd().getBytes(0,eeToLock);
                    System.out.println("客户端发出的包，包尾是--->"+ CRCUtil.bytesToHexString(eeToLock));
                    obj.writeAndFlush(customMsgToLock);


                }
                break;

                case 0x36:{

                    byte[] lockMac = new byte[6];
                    System.arraycopy(req, 1, lockMac, 0, 6);
                    byte[] id = new byte[8];
                    System.arraycopy(req, 7, id, 0, 8);

                    byte[] type = new byte[1];
                    System.arraycopy(req, 15, type, 0, 1);

                    byte[] time = new byte[4];
                    System.arraycopy(req, 16, time, 0, 4);

                    byte[] resoult = new byte[1];
                    System.arraycopy(req, 20, time, 0, 1);
                    System.out.println("\r\n门锁的mac地址是："+CRCUtil.bytesToHexString(lockMac)+"\r\n");
                    System.out.println("\r\nid是："+CRCUtil.bytesToHexString(id)+"\r\n");
                    System.out.println("\r\ntype是："+CRCUtil.bytesToHexString(type)+"\r\n");
                    System.out.println("\r\n时间是："+CRCUtil.bytesToHexString(time)+"\r\n");
                    System.out.println("服务器返回客户端0x26指令的结果"+resoult+" \r\n\r\n");
                    System.out.println();
                }
                break;

                case 0x37:{

                    System.out.println("服务器返回客户端0x37指令的结果\r\n\r\n");
                    System.out.println();
                    byte[] mac=new byte[6];
                    System.arraycopy(req, 1, mac, 0, 6);
                    //                    门锁服务器收到api的指令，获取到锁的通道，给锁发命令
                    SocketChannel obj = TempTestChannelManagerService.getGatewayChannel(CRCUtil.bytesToHexString(mac));
                    BasePackage mBasePackageToLock=new BasePackage();
                    ByteBuf flagToLock=Unpooled.buffer(2);
                    flagToLock.writeBytes(new byte[]{(byte)0xEF,0x3A});
                    mBasePackageToLock.setFlag(flagToLock);
                    mBasePackageToLock.setChannel((byte) 0x01);
                    mBasePackageToLock.setProtocolVersion((byte) 0x01);

                    req[0]=0x27;
                    byte[] req2=new byte[req.length-1];
                    System.arraycopy(req, 0, req2, 0,req.length-1);
                    ByteBuf body = Unpooled.buffer(req2.length);
                    body.writeBytes(req2);
                    mBasePackageToLock.setBody(body);

                    CustomMsg customMsgToLock=mBasePackageToLock.getCustomMsg();
                    byte[] eeToLock=new byte[2];
                    customMsgToLock.getEnd().getBytes(0,eeToLock);
                    System.out.println("客户端发出的包，包尾是--->"+ CRCUtil.bytesToHexString(eeToLock));
                    obj.writeAndFlush(customMsgToLock);




                }
                break;
                case 0x3A:{

                    System.out.println("服务器返回客户端0x3A指令的结果\r\n\r\n");
                    System.out.println();
                    byte[] mac=new byte[6];
                    System.arraycopy(req, 1, mac, 0, 6);
                    //                    门锁服务器收到api的指令，获取到锁的通道，给锁发命令
                    SocketChannel obj = TempTestChannelManagerService.getGatewayChannel(CRCUtil.bytesToHexString(mac));
                    BasePackage mBasePackageToLock=new BasePackage();
                    ByteBuf flagToLock=Unpooled.buffer(2);
                    flagToLock.writeBytes(new byte[]{(byte)0xEF,0x3A});
                    mBasePackageToLock.setFlag(flagToLock);
                    mBasePackageToLock.setChannel((byte) 0x01);
                    mBasePackageToLock.setProtocolVersion((byte) 0x01);

                    req[0]=0x2A;
                    byte[] req2=new byte[req.length-1];
                    System.arraycopy(req, 0, req2, 0,req.length-1);
                    ByteBuf body = Unpooled.buffer(req2.length);
                    body.writeBytes(req2);
                    mBasePackageToLock.setBody(body);

                    CustomMsg customMsgToLock=mBasePackageToLock.getCustomMsg();
                    byte[] eeToLock=new byte[2];
                    customMsgToLock.getEnd().getBytes(0,eeToLock);
                    System.out.println("客户端发出的包，包尾是--->"+ CRCUtil.bytesToHexString(eeToLock));
                    obj.writeAndFlush(customMsgToLock);




                }
                break;
                case 0x3B:{

                    System.out.println("服务器返回客户端0x3B指令的结果\r\n\r\n");
                    System.out.println();
                    byte[] mac=new byte[6];
                    System.arraycopy(req, 1, mac, 0, 6);
                    //                    门锁服务器收到api的指令，获取到锁的通道，给锁发命令
                    SocketChannel obj = TempTestChannelManagerService.getGatewayChannel(CRCUtil.bytesToHexString(mac));
                    BasePackage mBasePackageToLock=new BasePackage();
                    ByteBuf flagToLock=Unpooled.buffer(2);
                    flagToLock.writeBytes(new byte[]{(byte)0xEF,0x3A});
                    mBasePackageToLock.setFlag(flagToLock);
                    mBasePackageToLock.setChannel((byte) 0x01);
                    mBasePackageToLock.setProtocolVersion((byte) 0x01);

                    req[0]=0x2B;
                    byte[] req2=new byte[req.length-1];
                    System.arraycopy(req, 0, req2, 0,req.length-1);
                    ByteBuf body = Unpooled.buffer(req2.length);
                    body.writeBytes(req2);
                    mBasePackageToLock.setBody(body);

                    CustomMsg customMsgToLock=mBasePackageToLock.getCustomMsg();
                    byte[] eeToLock=new byte[2];
                    customMsgToLock.getEnd().getBytes(0,eeToLock);
                    System.out.println("客户端发出的包，包尾是--->"+ CRCUtil.bytesToHexString(eeToLock));
                    obj.writeAndFlush(customMsgToLock);




                }
                break;

                case 0x34:{

                    System.out.println("服务器返回客户端0x34指令的结果\r\n\r\n");
                    System.out.println();
                }
                break;
                case 0x35:{

                    System.out.println("服务器返回客户端0x35指令的结果\r\n\r\n");
                    System.out.println();
                }
                break;
                case 0x39:{

                    System.out.println("服务器返回客户端0x39指令的结果\r\n\r\n");
                    System.out.println();
                }
                break;
                case 0x3C:{

                    System.out.println("服务器返回客户端0x3C指令的结果\r\n\r\n");
                    System.out.println();
                }
                break;
                case 0x3D:{

                    System.out.println("服务器返回客户端0x3C指令的结果\r\n\r\n");
                    System.out.println();
                }
                break;
                case 0x3E:{

                    System.out.println("服务器返回客户端0x3C指令的结果\r\n\r\n");
                    System.out.println();
                }
                break;




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
