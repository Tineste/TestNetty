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
                case 0x21:
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




//                case 0x01: {
//                    BasePackage mBasePackage=new BasePackage();
//
//
//                    byte[] ee2=new byte[2];
//                    customMsgEnd.getBytes(0,ee2);
//                    System.out.println("客户端收到服务器返回的包尾是--->"+ CRCUtil.bytesToHexString(ee2));
//
//                    ByteBuf customMsgBody2=Unpooled.buffer(req.length);
//                    customMsgBody2.writeBytes(req);
//                    mBasePackage.setCustomMsgAttribute(customMsgFlag,customMsgLen,customMsgChannel,customMsgProtocolVersion,customMsgBody2,customMsgEnd);
//                    StringBuffer sb = new StringBuffer();
//                    sb.append("客户收到端服务器命令0x01  \r\n");
//                    System.out.println("客户收到端服务器命令0x01  ");
//
//                    if( mBasePackage.checkCRC()){
//                        sb.append("CRC验证成功！\r\n");
//                    }else {
//                        sb.append("CRC验证失败！\r\n");
//                    }
//
//                    sb.append(CRCUtil.bytesToHexString(req)+".");
//                    System.out.println("...");
//
//
//
//                    byte[] mac = new byte[6];
//                    System.arraycopy(req, 1, mac, 0, 6);
//                    byte[] time = new byte[4];
//                    System.arraycopy(req, 209, time, 0, 4);
//                    CRCUtil.bytesToHexString(time);
//
//                    byte[] url=new byte[200];
//                    System.arraycopy(req, 7, url, 0, 200);
//                    byte[] port=new byte[2];
//                    System.arraycopy(req, 207, port, 0, 2);
//                    String newPort=""+port[0]+port[1];
//
//
//                    sb.append("\r\n"+"mac地址是："+CRCUtil.bytesToHexString(mac)+"\r\n");
//                    sb.append("服务器端返回的时间是："+CRCUtil.bytesToTime(time)+"\r\n");
//
//                    sb.append("服务器端返回url是："+ new String(url)+"   端口是"+newPort+"\r\n");
//                    System.out.println("服务器端返回url是："+ new String(url)+"   端口是"+newPort+"\r\n");
//                    System.out.println("从中心服务器获取具体要链接的前置服务器域名或IP 完成");
//                    sb.append("从中心服务器获取具体要链接的前置服务器域名或IP 完成\r\n\r\n");
//                    System.out.println();
//                    mUICallBack.refreshText(sb.toString());
//                }
//
//
//                    break;
//                case 0x02: {
//
//                    BasePackage mBasePackage=new BasePackage();
//                    byte[] ee2=new byte[2];
//                    customMsgEnd.getBytes(0,ee2);
//                    System.out.println("客户端收到服务器返回的包尾是--->"+ CRCUtil.bytesToHexString(ee2));
//                    ByteBuf customMsgBody2=Unpooled.buffer(req.length);
//                    customMsgBody2.writeBytes(req);
//                    mBasePackage.setCustomMsgAttribute(customMsgFlag,customMsgLen,customMsgChannel,customMsgProtocolVersion,customMsgBody2,customMsgEnd);
//                    StringBuffer sb = new StringBuffer();
//                    sb.append("客户收到端服务器命令0x02  \r\n");
//                    System.out.println("客户收到端服务器命令0x02  ");
//
//                    if( mBasePackage.checkCRC()){
//                        sb.append("CRC验证成功！\r\n");
//                    }else {
//                        sb.append("CRC验证失败！\r\n");
//                    }
//
//                    sb.append(CRCUtil.bytesToHexString(req));
//                    System.out.println("智能门禁向服务器申报MAC码并获取蓝牙密钥指令完成");
//                    sb.append("\r\n智能门禁向服务器申报MAC码并获取蓝牙密钥指令完成\r\n\r\n");
//                    System.out.println();
//                    mUICallBack.refreshText(sb.toString());
//
//                }
//                break;
//                case 0x03: {
////                    StringBuffer sb = new StringBuffer();
////                    sb.append("客户端收到服务器请求0x03 \r\n");
////                    System.out.println("客户端收到服务器请求0x03 ");
////                    for (int i = 0; i < req.length; i++) {
////                        System.out.print(req[i]);
////                        sb.append(req[i]+".");
////                    }
////
////                    System.out.println("客户端应答服务器0x03请求并返回数据");
////                    sb.append("\r\n客户端应答服务器0x03请求并返回数据\r\n\r\n");
////                    mUICallBack.refreshText(sb.toString());
////                    System.out.println();
//
//
//                    BasePackage mBasePackage=new BasePackage();
//                    ByteBuf customMsgBody2=Unpooled.buffer(req.length);
//                    customMsgBody2.writeBytes(req);
//                    byte[] ee=new byte[2];
//                    customMsgEnd.getBytes(0,ee);
//                    System.out.println("客户端收到的包，包尾是--->"+ CRCUtil.bytesToHexString(ee));
//                    mBasePackage.setCustomMsgAttribute(customMsgFlag,customMsgLen,customMsgChannel,customMsgProtocolVersion,customMsgBody2,customMsgEnd);
//                    StringBuffer sb=new StringBuffer();
//                    sb.append("客户端收到服务器请求0x03 \r\n ");
//                    System.out.println("客户端收到服务器请求0x03");
//                    if( mBasePackage.checkCRC()){
//                        sb.append("CRC验证成功！\r\n");
//                    }else {
//                        sb.append("CRC验证失败！\r\n");
//                    }
//                    sb.append(CRCUtil.bytesToHexString(req)+".");
//                    System.out.println();
//                    System.out.println("  客户端应答服务器0x03请求并返回数据");
//                    sb.append("\r\n客户端应答服务器0x03请求并返回数据 \r\n\r\n");
//                    System.out.println();
//
//
//
////                    应答服务器的请求
////                    ByteBuf flag = Unpooled.buffer(2);
////                    flag.writeBytes(new byte[]{(byte) 0xEF, 0x3A});
////
////                    Short len = 0x18;
////                    byte channel = 0x11;
////                    byte protocolVersion = 0x01;
////                    byte[] bbody = {0x03,
////                            0x05, 0x01, 0x00, 0x01, 0x02, 0x03,
////                            0x23, (byte) 0xC9, 0x3B, (byte) 0x89, 0x4A, (byte) 0xED, (byte) 0xF0, 0x65,
////                            0x59, 0x3D, 0x34, 0x11,
////                            0x01
////                    };
////                    ByteBuf body = Unpooled.buffer(bbody.length);
////                    body.writeBytes(bbody);
////                    ByteBuf end = Unpooled.buffer(2);
////                    end.writeBytes(new byte[]{0x6F, (byte) 0x6B});
////                    CustomMsg customMsg2 = new CustomMsg(flag, len, channel, protocolVersion, body, end);
////                    ctx.writeAndFlush(customMsg2);
//
//
//                    BasePackage mBasePackage2=new BasePackage();
//                    ByteBuf flag=Unpooled.buffer(2);
//                    flag.writeBytes(new byte[]{(byte)0xEF,0x3A});
//                    mBasePackage2.setFlag(flag);
//                    mBasePackage2.setChannel((byte) 0x11);
//                    mBasePackage2.setProtocolVersion((byte) 0x01);
//
//                    byte[] data = {0x03,
//                            0x05, 0x01, 0x00, 0x01, 0x02, 0x03,
//                            0x23, (byte) 0xC9, 0x3B, (byte) 0x89, 0x4A, (byte) 0xED, (byte) 0xF0, 0x65,
//                            0x59, 0x3D, 0x34, 0x11,
//                            0x01
//                    };
//
//                    ByteBuf body=Unpooled.buffer(data.length);
//                    body.writeBytes(data);
//                    mBasePackage2.setBody(body);
//                    CustomMsg customMsgaa=mBasePackage2.getCustomMsg();
//                    byte[] ee2=new byte[2];
//                    customMsgaa.getEnd().getBytes(0,ee2);
//                    System.out.println("客户端应答应服务器的包，包尾是--->"+ CRCUtil.bytesToHexString(ee2));
//                    ctx.writeAndFlush(customMsgaa);
//                    mUICallBack.refreshText(sb.toString());
//
//
//
//                }
//                    break;
//                case 0x04:{
//
//
//
//
//
//                    BasePackage mBasePackage=new BasePackage();
//                    ByteBuf customMsgBody2=Unpooled.buffer(req.length);
//                    customMsgBody2.writeBytes(req);
//                    byte[] ee=new byte[2];
//                    customMsgEnd.getBytes(0,ee);
//                    System.out.println("客户端收到的包，包尾是--->"+ CRCUtil.bytesToHexString(ee));
//                    mBasePackage.setCustomMsgAttribute(customMsgFlag,customMsgLen,customMsgChannel,customMsgProtocolVersion,customMsgBody2,customMsgEnd);
//                    StringBuffer sb=new StringBuffer();
//                    sb.append("客户端收到服务器请求0x04 \r\n ");
//                    System.out.println("客户端收到服务器请求0x04");
//                    if( mBasePackage.checkCRC()){
//                        sb.append("CRC验证成功！\r\n");
//                    }else {
//                        sb.append("CRC验证失败！\r\n");
//                    }
//                    sb.append(CRCUtil.bytesToHexString(req)+".");
//                    System.out.println();
//                    System.out.println("  客户端应答服务器0x04请求并返回数据");
//                    sb.append("\r\n客户端应答服务器0x04请求并返回数据 \r\n\r\n");
//                    System.out.println();
//
//
//
//
//
//                    BasePackage mBasePackage2=new BasePackage();
//                    ByteBuf flag=Unpooled.buffer(2);
//                    flag.writeBytes(new byte[]{(byte)0xEF,0x3A});
//                    mBasePackage2.setFlag(flag);
//                    mBasePackage2.setChannel((byte) 0x11);
//                    mBasePackage2.setProtocolVersion((byte) 0x01);
//                    byte[] data = {0x04,
//                            0x01, 0x02, 0x03, 0x04, 0x05, 0x06,
//                            0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
//                            0x01,0x02,0x03,0x04,
//                            0x01,0x02,0x03,0x04,
//                            0x01
//                    };
//                    ByteBuf body=Unpooled.buffer(data.length);
//                    body.writeBytes(data);
//                    mBasePackage2.setBody(body);
//                    CustomMsg customMsgaa=mBasePackage2.getCustomMsg();
//                    byte[] ee2=new byte[2];
//                    customMsgaa.getEnd().getBytes(0,ee2);
//                    System.out.println("客户端应答应服务器的包，包尾是--->"+ CRCUtil.bytesToHexString(ee2));
//                    ctx.writeAndFlush(customMsgaa);
//                    mUICallBack.refreshText(sb.toString());
//
//                }
//                break;
//                case 0x05:{
//                    BasePackage mBasePackage=new BasePackage();
//                    ByteBuf customMsgBody2=Unpooled.buffer(req.length);
//                    customMsgBody2.writeBytes(req);
//                    byte[] ee=new byte[2];
//                    customMsgEnd.getBytes(0,ee);
//                    System.out.println("客户端收到的包，包尾是--->"+ CRCUtil.bytesToHexString(ee));
//                    mBasePackage.setCustomMsgAttribute(customMsgFlag,customMsgLen,customMsgChannel,customMsgProtocolVersion,customMsgBody2,customMsgEnd);
//                    StringBuffer sb=new StringBuffer();
//                    sb.append("客户端收到服务器请求0x05 \r\n ");
//                    System.out.println("客户端收到服务器请求0x05");
//                    if( mBasePackage.checkCRC()){
//                        sb.append("CRC验证成功！\r\n");
//                    }else {
//                        sb.append("CRC验证失败！\r\n");
//                    }
//                    sb.append(CRCUtil.bytesToHexString(req)+".");
//                    System.out.println();
//                    System.out.println("  客户端应答服务器0x05请求并返回数据");
//                    sb.append("\r\n客户端应答服务器0x05请求并返回数据 \r\n\r\n");
//                    System.out.println();
//
//
//                    BasePackage mBasePackage2=new BasePackage();
//                    ByteBuf flag=Unpooled.buffer(2);
//                    flag.writeBytes(new byte[]{(byte)0xEF,0x3A});
//                    mBasePackage2.setFlag(flag);
//                    mBasePackage2.setChannel((byte) 0x11);
//                    mBasePackage2.setProtocolVersion((byte) 0x01);
//                    byte[] data = {0x05,
//                            0x01, 0x02, 0x03, 0x04, 0x05, 0x06,
//                            0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
//                            0x01,0x02,0x03,0x04,
//                            0x01
//                    };
//                    ByteBuf body=Unpooled.buffer(data.length);
//                    body.writeBytes(data);
//                    mBasePackage2.setBody(body);
//                    CustomMsg customMsgaa=mBasePackage2.getCustomMsg();
//                    byte[] ee2=new byte[2];
//                    customMsgaa.getEnd().getBytes(0,ee2);
//                    System.out.println("客户端应答应服务器的包，包尾是--->"+ CRCUtil.bytesToHexString(ee2));
//                    ctx.writeAndFlush(customMsgaa);
//                    mUICallBack.refreshText(sb.toString());
//
//                }
//                break;
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
//                case 0x07:{
//
//
//                    BasePackage mBasePackage=new BasePackage();
//                    byte[] ee2=new byte[2];
//                    customMsgEnd.getBytes(0,ee2);
//                    System.out.println("客户端收到服务器返回的包尾是--->"+ CRCUtil.bytesToHexString(ee2));
//                    ByteBuf customMsgBody2=Unpooled.buffer(req.length);
//                    customMsgBody2.writeBytes(req);
//                    mBasePackage.setCustomMsgAttribute(customMsgFlag,customMsgLen,customMsgChannel,customMsgProtocolVersion,customMsgBody2,customMsgEnd);
//                    StringBuffer sb = new StringBuffer();
//                    sb.append("客户收到端服务器命令0x07  \r\n");
//                    System.out.println("客户收到端服务器命令0x07  ");
//
//                    if( mBasePackage.checkCRC()){
//                        sb.append("CRC验证成功！\r\n");
//                    }else {
//                        sb.append("CRC验证失败！\r\n");
//                    }
//                    sb.append(CRCUtil.bytesToHexString(req));
//                    System.out.println("开门信息上报到服务器完成");
//                    sb.append("\r\n开门信息上报到服务器完成\r\n\r\n");
//                    System.out.println();
//                    mUICallBack.refreshText(sb.toString());
//
//
//                }
//                break;
//                case 0x08:{
//                    BasePackage mBasePackage=new BasePackage();
//                    byte[] ee2=new byte[2];
//                    customMsgEnd.getBytes(0,ee2);
//                    System.out.println("客户端收到服务器返回的包尾是--->"+ CRCUtil.bytesToHexString(ee2));
//                    ByteBuf customMsgBody2=Unpooled.buffer(req.length);
//                    customMsgBody2.writeBytes(req);
//                    mBasePackage.setCustomMsgAttribute(customMsgFlag,customMsgLen,customMsgChannel,customMsgProtocolVersion,customMsgBody2,customMsgEnd);
//                    StringBuffer sb = new StringBuffer();
//                    sb.append("客户收到端服务器命令0x08  \r\n");
//                    System.out.println("客户收到端服务器命令0x08  ");
//
//                    if( mBasePackage.checkCRC()){
//                        sb.append("CRC验证成功！\r\n");
//                    }else {
//                        sb.append("CRC验证失败！\r\n");
//                    }
//                    sb.append(CRCUtil.bytesToHexString(req));
//                    System.out.println("心跳包给服务器完成");
//                    sb.append("\r\n心跳包给服务器完成\r\n\r\n");
//                    System.out.println();
//                    mUICallBack.refreshText(sb.toString());
//
//                }
//                break;
//                case 0x09:{
//                    System.out.println("客户端收到服务器请求0x09 ");
//                    BasePackage mBasePackage=new BasePackage();
//                    ByteBuf customMsgBody2=Unpooled.buffer(req.length);
//                    customMsgBody2.writeBytes(req);
//                    byte[] ee=new byte[2];
//                    customMsgEnd.getBytes(0,ee);
//                    System.out.println("客户端收到的包，包尾是--->"+ CRCUtil.bytesToHexString(ee));
//                    mBasePackage.setCustomMsgAttribute(customMsgFlag,customMsgLen,customMsgChannel,customMsgProtocolVersion,customMsgBody2,customMsgEnd);
//                    StringBuffer sb=new StringBuffer();
//                    sb.append("客户端收到服务器请求0x09 \r\n ");
//                    System.out.println("客户端收到服务器请求0x09");
//                    if( mBasePackage.checkCRC()){
//                        sb.append("CRC验证成功！\r\n");
//                    }else {
//                        sb.append("CRC验证失败！\r\n");
//                    }
//                    sb.append(CRCUtil.bytesToHexString(req)+".");
//                    System.out.println();
//                    System.out.println("  客户端应答服务器0x09请求并返回数据");
//                    sb.append("\r\n客户端应答服务器0x09请求并返回数据 \r\n\r\n");
//                    System.out.println();
//
//
//
//
//
//
//                    BasePackage mBasePackage2=new BasePackage();
//                    ByteBuf flag=Unpooled.buffer(2);
//                    flag.writeBytes(new byte[]{(byte)0xEF,0x3A});
//                    mBasePackage2.setFlag(flag);
//                    mBasePackage2.setChannel((byte) 0x11);
//                    mBasePackage2.setProtocolVersion((byte) 0x01);
//                    byte[] data = {0x09,
//                            0x01, 0x02, 0x03, 0x04, 0x05, 0x06,
//                            0x01,0x02,0x03,0x04,
//                            0x01
//                    };
//                    ByteBuf body=Unpooled.buffer(data.length);
//                    body.writeBytes(data);
//                    mBasePackage2.setBody(body);
//                    CustomMsg customMsgaa=mBasePackage2.getCustomMsg();
//                    byte[] ee2=new byte[2];
//                    customMsgaa.getEnd().getBytes(0,ee2);
//                    System.out.println("客户端应答应服务器的包，包尾是--->"+ CRCUtil.bytesToHexString(ee2));
//                    ctx.writeAndFlush(customMsgaa);
//                    mUICallBack.refreshText(sb.toString());
//
//
//
//                }
//                break;
//                case 0x0A:{
//
//
//                    BasePackage mBasePackage=new BasePackage();
//                    byte[] ee2=new byte[2];
//                    customMsgEnd.getBytes(0,ee2);
//                    System.out.println("客户端收到服务器返回的包尾是--->"+ CRCUtil.bytesToHexString(ee2));
//                    ByteBuf customMsgBody2=Unpooled.buffer(req.length);
//                    customMsgBody2.writeBytes(req);
//                    mBasePackage.setCustomMsgAttribute(customMsgFlag,customMsgLen,customMsgChannel,customMsgProtocolVersion,customMsgBody2,customMsgEnd);
//                    StringBuffer sb = new StringBuffer();
//                    sb.append("客户收到端服务器命令0x0A  \r\n");
//                    System.out.println("客户收到端服务器命令0x0A  ");
//
//                    if( mBasePackage.checkCRC()){
//                        sb.append("CRC验证成功！\r\n");
//                    }else {
//                        sb.append("CRC验证失败！\r\n");
//                    }
//                    sb.append(CRCUtil.bytesToHexString(req));
//                    System.out.println("时间校准请求完成");
//                    sb.append("\r\n时间校准请求完成\r\n\r\n");
//                    System.out.println();
//                    mUICallBack.refreshText(sb.toString());
//
//                }
//                break;
//                case 0x0B:{
////                    StringBuffer sb = new StringBuffer();
////                    sb.append("客户端收到服务器命令0x0B  \r\n");
////                    System.out.println("客户收到端服务器命令0x0B  ");
////                    for (int i = 0; i < req.length; i++) {
////                        System.out.print(req[i]);
////                        sb.append(req[i]+".");
////                    }
////                    System.out.println("报警信息上报到服务器  完成");
////                    sb.append("\r\n报警信息上报到服务器  完成\r\n\r\n");
////                    mUICallBack.refreshText(sb.toString());
////                    System.out.println();
////
//
//                    BasePackage mBasePackage=new BasePackage();
//                    byte[] ee2=new byte[2];
//                    customMsgEnd.getBytes(0,ee2);
//                    System.out.println("客户端收到服务器返回的包尾是--->"+ CRCUtil.bytesToHexString(ee2));
//                    ByteBuf customMsgBody2=Unpooled.buffer(req.length);
//                    customMsgBody2.writeBytes(req);
//                    mBasePackage.setCustomMsgAttribute(customMsgFlag,customMsgLen,customMsgChannel,customMsgProtocolVersion,customMsgBody2,customMsgEnd);
//                    StringBuffer sb = new StringBuffer();
//                    sb.append("客户收到端服务器命令0x0B  \r\n");
//                    System.out.println("客户收到端服务器命令0x0B  ");
//
//                    if( mBasePackage.checkCRC()){
//                        sb.append("CRC验证成功！\r\n");
//                    }else {
//                        sb.append("CRC验证失败！\r\n");
//                    }
//                    sb.append(CRCUtil.bytesToHexString(req));
//                    System.out.println("报警信息上报到服务器完成");
//                    sb.append("\r\n报警信息上报到服务器完成\r\n\r\n");
//                    System.out.println();
//                    mUICallBack.refreshText(sb.toString());
//
//
//
//                }
//                break;
//                case 0x0C:{
//
//
//
//
//                    System.out.println("客户端收到服务器请求0x0C ");
//                    BasePackage mBasePackage=new BasePackage();
//                    ByteBuf customMsgBody2=Unpooled.buffer(req.length);
//                    customMsgBody2.writeBytes(req);
//                    byte[] ee=new byte[2];
//                    customMsgEnd.getBytes(0,ee);
//                    System.out.println("客户端收到的包，包尾是--->"+ CRCUtil.bytesToHexString(ee));
//                    mBasePackage.setCustomMsgAttribute(customMsgFlag,customMsgLen,customMsgChannel,customMsgProtocolVersion,customMsgBody2,customMsgEnd);
//                    StringBuffer sb=new StringBuffer();
//                    sb.append("客户端收到服务器请求0x0C\r\n ");
//                    System.out.println("客户端收到服务器请求0x0C");
//                    if( mBasePackage.checkCRC()){
//                        sb.append("CRC验证成功！\r\n");
//                    }else {
//                        sb.append("CRC验证失败！\r\n");
//                    }
//                    sb.append(CRCUtil.bytesToHexString(req)+".");
//                    System.out.println();
//                    System.out.println("  客户端应答服务器0x0C请求并返回数据");
//                    sb.append("\r\n客户端应答服务器0x0C请求并返回数据 \r\n\r\n");
//                    System.out.println();
//
//
//
//
//
////                    应答服务器的请求
//
//
//
//                    BasePackage mBasePackage2=new BasePackage();
//                    ByteBuf flag=Unpooled.buffer(2);
//                    flag.writeBytes(new byte[]{(byte)0xEF,0x3A});
//                    mBasePackage2.setFlag(flag);
//                    mBasePackage2.setChannel((byte) 0x11);
//                    mBasePackage2.setProtocolVersion((byte) 0x01);
//                    byte[] data = {
//                            0x0C,
//                            0x01, 0x02, 0x03, 0x04, 0x05, 0x06,
//                            0x01,0x02,
//                            0x01,
//                            0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,
//                            0x01,0x02,0x03,0x04,
//                            0x01,0x02,0x03,0x04,
//                            0x01,0x02,0x03,0x04,
//                            0x01
//                    };
//                    ByteBuf body=Unpooled.buffer(data.length);
//                    body.writeBytes(data);
//                    mBasePackage2.setBody(body);
//                    CustomMsg customMsgaa=mBasePackage2.getCustomMsg();
//                    byte[] ee2=new byte[2];
//                    customMsgaa.getEnd().getBytes(0,ee2);
//                    System.out.println("客户端应答应服务器的包，包尾是--->"+ CRCUtil.bytesToHexString(ee2));
//                    ctx.writeAndFlush(customMsgaa);
//                    mUICallBack.refreshText(sb.toString());
//
//
//
//                }
//                break;
//                case 0x0D: {
//
//
//                    System.out.println("客户端收到服务器请求0x0D ");
//                    BasePackage mBasePackage=new BasePackage();
//                    ByteBuf customMsgBody2=Unpooled.buffer(req.length);
//                    customMsgBody2.writeBytes(req);
//                    byte[] ee=new byte[2];
//                    customMsgEnd.getBytes(0,ee);
//                    System.out.println("客户端收到的包，包尾是--->"+ CRCUtil.bytesToHexString(ee));
//                    mBasePackage.setCustomMsgAttribute(customMsgFlag,customMsgLen,customMsgChannel,customMsgProtocolVersion,customMsgBody2,customMsgEnd);
//                    StringBuffer sb=new StringBuffer();
//                    sb.append("客户端收到服务器请求0x0D\r\n ");
//                    System.out.println("客户端收到服务器请求0x0D");
//                    if( mBasePackage.checkCRC()){
//                        sb.append("CRC验证成功！\r\n");
//                    }else {
//                        sb.append("CRC验证失败！\r\n");
//                    }
//                    sb.append(CRCUtil.bytesToHexString(req)+".");
//                    System.out.println();
//                    System.out.println("  客户端应答服务器0x0D请求并返回数据");
//                    sb.append("\r\n客户端应答服务器0x0D请求并返回数据 \r\n\r\n");
//                    System.out.println();
//
//
//
//
//                    BasePackage mBasePackage2=new BasePackage();
//                    ByteBuf flag=Unpooled.buffer(2);
//                    flag.writeBytes(new byte[]{(byte)0xEF,0x3A});
//                    mBasePackage2.setFlag(flag);
//                    mBasePackage2.setChannel((byte) 0x11);
//                    mBasePackage2.setProtocolVersion((byte) 0x01);
//                    byte[] byte1 = {
//                            0x0D,
//                            0x01, 0x02, 0x03, 0x04, 0x05, 0x06};
////                    域名
//                    byte[] url = new byte[200];
////                    端口号
//                    byte[] byte2 = {
//                            0x01, 0x02,
//                            0x01, 0x02,
//                            0x01, 0x02, 0x03,0x04,
//                            0x01
//                    };
//                    byte[] data = new byte[byte1.length + url.length + byte2.length];
//                    System.arraycopy(byte1, 0, data, 0, byte1.length);
//                    System.arraycopy(url, 0, data, byte1.length, url.length);
//                    System.arraycopy(byte2, 0, data, byte1.length + url.length, byte2.length);
//                    ByteBuf body=Unpooled.buffer(data.length);
//                    body.writeBytes(data);
//                    mBasePackage2.setBody(body);
//                    CustomMsg customMsgaa=mBasePackage2.getCustomMsg();
//                    byte[] ee2=new byte[2];
//                    customMsgaa.getEnd().getBytes(0,ee2);
//                    System.out.println("客户端应答应服务器的包，包尾是--->"+ CRCUtil.bytesToHexString(ee2));
//                    ctx.writeAndFlush(customMsgaa);
//                    mUICallBack.refreshText(sb.toString());
//
//
//
//                }
//                break;
//                case 0x0E:{
//
//
//
//
//                    System.out.println("客户端收到服务器请求0x0E ");
//                    BasePackage mBasePackage=new BasePackage();
//                    ByteBuf customMsgBody2=Unpooled.buffer(req.length);
//                    customMsgBody2.writeBytes(req);
//                    byte[] ee=new byte[2];
//                    customMsgEnd.getBytes(0,ee);
//                    System.out.println("客户端收到的包，包尾是--->"+ CRCUtil.bytesToHexString(ee));
//                    mBasePackage.setCustomMsgAttribute(customMsgFlag,customMsgLen,customMsgChannel,customMsgProtocolVersion,customMsgBody2,customMsgEnd);
//                    StringBuffer sb=new StringBuffer();
//                    sb.append("客户端收到服务器请求0x0E\r\n ");
//                    System.out.println("客户端收到服务器请求0x0E");
//                    if( mBasePackage.checkCRC()){
//                        sb.append("CRC验证成功！\r\n");
//                    }else {
//                        sb.append("CRC验证失败！\r\n");
//                    }
//                    sb.append(CRCUtil.bytesToHexString(req)+".");
//                    System.out.println();
//                    System.out.println("  客户端应答服务器0x0E请求并返回数据");
//                    sb.append("\r\n客户端应答服务器0x0E请求并返回数据 \r\n\r\n");
//                    System.out.println();
//
////                    应答服务器的请求
//                    BasePackage mBasePackage2=new BasePackage();
//                    ByteBuf flag=Unpooled.buffer(2);
//                    flag.writeBytes(new byte[]{(byte)0xEF,0x3A});
//                    mBasePackage2.setFlag(flag);
//                    mBasePackage2.setChannel((byte) 0x11);
//                    mBasePackage2.setProtocolVersion((byte) 0x01);
//                    byte[] data = {
//                            0x0E,
//                            0x01, 0x02, 0x03, 0x04, 0x05, 0x06,
//                            0x01,0x02,0x03,0x04,
//                            0x01
//                    };
//                    ByteBuf body=Unpooled.buffer(data.length);
//                    body.writeBytes(data);
//                    mBasePackage2.setBody(body);
//                    CustomMsg customMsgaa=mBasePackage2.getCustomMsg();
//                    byte[] ee2=new byte[2];
//                    customMsgaa.getEnd().getBytes(0,ee2);
//                    System.out.println("客户端应答应服务器的包，包尾是--->"+ CRCUtil.bytesToHexString(ee2));
//                    ctx.writeAndFlush(customMsgaa);
//                    mUICallBack.refreshText(sb.toString());
//
//
//                }
//                break;

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
