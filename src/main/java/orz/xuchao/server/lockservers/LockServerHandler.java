package orz.xuchao.server.lockservers;

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

import java.util.Calendar;
import java.util.Map;


/**
 * Created by Administrator on 2017/7/6 0006.
 */
public class LockServerHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LogManager.getLogger(LockServerHandler.class.getName());



    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
        String uuid=ctx.channel().id().asLongText();
        System.out.println("设备id为"+uuid+"的设备断开了连接");
        mUICallBack.refreshText("\r\n设备id为"+uuid+"的设备断开了连接\r\n\r\n");
        logger.info("设备id为"+uuid+"的设备断开了连接");


        Map<String, SocketChannel>  map =TempTestChannelManagerService.getChannels();
        //遍历map中的值

        for (String key : map.keySet()) {

            if(map.get(key).equals(ctx.channel()))
                TempTestChannelManagerService.removeGatewayChannel(key);
        }



    }

    private UICallBack mUICallBack;


    public LockServerHandler(UICallBack mUICallBack){
        this.mUICallBack = mUICallBack;
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String uuid=ctx.channel().id().asLongText();
        System.out.println("一个客户端连接进来了："+uuid);
        mUICallBack.refreshText("\r\n一个客户端连接进来了："+uuid+"  \r\n\r\n");
        logger.info("一个客户端连接进来了："+uuid);
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
        System.out.println("----------服务器收到命令----------------");
        if(msg instanceof CustomMsg) {
            CustomMsg customMsg = (CustomMsg)msg;



            ByteBuf customMsgFlag=customMsg.getFlag();
            Short customMsgLen=customMsg.getLen();
            byte customMsgChannel=customMsg.getChannel();
            byte customMsgProtocolVersion=customMsg.getProtocolVersion();
            ByteBuf customMsgEnd=customMsg.getEnd();
            ByteBuf customMsgBody=customMsg.getBody();

//            byte[] byteFlag=new byte[customMsgFlag.readableBytes()];
//            customMsgFlag.readBytes(byteFlag);
//
//            byte[] byteEnd=new byte[customMsgEnd.readableBytes()];
//            customMsgEnd.readBytes(byteEnd);
//
//
//            System.out.println("1--->"+byteFlag[0]);
//            System.out.println("2--->"+customMsgLen);
//            System.out.println("3--->"+customMsgChannel);
//            System.out.println("4--->"+customMsgProtocolVersion);
//            System.out.println("5--->"+byteEnd[0]);




            byte[] req=new byte[customMsgBody.readableBytes()];
            customMsgBody.readBytes(req);
            String uuid=ctx.channel().id().asLongText();


            switch (req[0]){
                case  0x02: {
                    BasePackage mBasePackage=new BasePackage();
                    ByteBuf customMsgBody2=Unpooled.buffer(req.length);
                    customMsgBody2.writeBytes(req);
                    byte[] ee=new byte[2];
                    customMsgEnd.getBytes(0,ee);
                    mBasePackage.setCustomMsgAttribute(customMsgFlag,customMsgLen,customMsgChannel,customMsgProtocolVersion,customMsgBody2,customMsgEnd);
                    StringBuffer sb=new StringBuffer();
                    sb.append("锁服务器收到 门锁  注册mac地址 命令0x02 \r\n ");
                    System.out.println("收到指令0x02");
                    if( mBasePackage.checkCRC()){
                        sb.append("CRC验证成功！\r\n");
                    }else {
                        sb.append("CRC验证成功！\r\n");
                    }
                    sb.append(CRCUtil.bytesToHexString(req)+".");
                    byte[] time=new byte[4];
                    System.arraycopy(req, 7, time, 0, 4);
                    byte[] lockMac = new byte[6];
                    System.arraycopy(req, 1, lockMac, 0, 6);
                    sb.append("mac地址是"+CRCUtil.bytesToHexString(lockMac)+" \r\n\r\n");
//                    以mac为key，通道为value，放入map管理
                    TempTestChannelManagerService.addGatewayChannel(CRCUtil.bytesToHexString(lockMac),(SocketChannel)ctx.channel());
                    mUICallBack.refreshText(sb.toString());
//                    去中心服务器注册门锁的mac地址
                    byte[] serverMac={0x06,0x05,0x04,0x03,0x02,0x01};
                    BasePackage mBasePackage2=new BasePackage();
                    ByteBuf flag=Unpooled.buffer(2);
                    flag.writeBytes(new byte[]{(byte)0xEF,0x3A});
                    mBasePackage2.setFlag(flag);
                    mBasePackage2.setChannel( customMsgChannel);
                    mBasePackage2.setProtocolVersion(customMsgProtocolVersion);
                    byte[] orlder={0x02};
                    ByteBuf byteBuf=Unpooled.copiedBuffer(orlder,lockMac,serverMac,time);
                    mBasePackage2.setBody(byteBuf);
                    CustomMsg customMsgaa=mBasePackage2.getCustomMsg();
                    byte[] ee2=new byte[2];
                    customMsgaa.getEnd().getBytes(0,ee2);
                    System.out.println("服务器返回客户端的包，包尾是--->"+ CRCUtil.bytesToHexString(ee2));
                    LockServerUI.timeClient.socketChannel.writeAndFlush(customMsgaa);


                }
                break;
//                case 0x13:{
//                    try {
//                        SocketChannel obj = TempTestChannelManagerService.getGatewayChannel("010203040506");
//
//                        BasePackage mBasePackage=new BasePackage();
//                        ByteBuf flag=Unpooled.buffer(2);
//                        flag.writeBytes(new byte[]{(byte)0xEF,0x3A});
//                        mBasePackage.setFlag(flag);
//                        mBasePackage.setChannel((byte) 0x01);
//                        mBasePackage.setProtocolVersion((byte) 0x01);
//                        byte[] bbody={
//                                0x03
//                                ,0x05,0x01,0x00,0x01,0x02,0x03
//                                ,0x59,0x3D,0x34,0x11};
//                        ByteBuf body=Unpooled.buffer(bbody.length);
//                        body.writeBytes(bbody);
//                        mBasePackage.setBody(body);
//                        CustomMsg customMsg0=mBasePackage.getCustomMsg();
//                        byte[] ee=new byte[2];
//                        customMsg0.getEnd().getBytes(0,ee);
//                        System.out.println("客户端发出的包，包尾是--->"+ CRCUtil.bytesToHexString(ee));
//                        obj.writeAndFlush(customMsg0);
//
//                    } catch (Exception e1) {
//                        e1.printStackTrace();
//                    }
//
//
//                }
//                break;
                case 0x03:{
                    System.out.println("==LockServerHandler==>指令0x03的反馈 ");
                    BasePackage mBasePackage=new BasePackage();
                    byte[] ee2=new byte[2];
                    customMsgEnd.getBytes(0,ee2);
                    System.out.println("服务器端收到客户端返回的包尾是--->"+ CRCUtil.bytesToHexString(ee2));
                    ByteBuf customMsgBody2=Unpooled.buffer(req.length);
                    customMsgBody2.writeBytes(req);
                    mBasePackage.setCustomMsgAttribute(customMsgFlag,customMsgLen,customMsgChannel,customMsgProtocolVersion,customMsgBody2,customMsgEnd);
                    StringBuffer sb = new StringBuffer();
                    sb.append("服务器端收到客户端"+uuid+"命令0x03  \r\n");
                    System.out.println("服务器端收到客户端命令0x03  ");
                    if( mBasePackage.checkCRC()){
                        sb.append("CRC验证成功！\r\n");
                    }else {
                        sb.append("CRC验证失败！\r\n");
                    }
                    sb.append(CRCUtil.bytesToHexString(req)+".");
                    System.out.println("服务器从智能门禁读取一条UID完成");
                    sb.append("\r\n服务器从智能门禁读取一条UID完成\r\n\r\n");
                    System.out.println();
                    logger.info(sb.toString());
                    mUICallBack.refreshText(sb.toString());




                    //                    调用api服务器通知开门完成
                    BasePackage mBasePackage2 = new BasePackage();
                    ByteBuf flag = Unpooled.buffer(2);
                    flag.writeBytes(new byte[]{(byte) 0xEF, 0x3A});
                    mBasePackage2.setFlag(flag);
                    mBasePackage2.setChannel(customMsgChannel);
                    mBasePackage2.setProtocolVersion(customMsgProtocolVersion);
                    ByteBuf body = Unpooled.buffer(req.length);
                    body.writeBytes(req);
                    mBasePackage2.setBody(body);
                    CustomMsg customMsgaa = mBasePackage2.getCustomMsg();
                    byte[] ee22 = new byte[2];
                    customMsgaa.getEnd().getBytes(0, ee22);
                    System.out.println("客户端应答应服务器的包，包尾是--->" + CRCUtil.bytesToHexString(ee22));
                    LockServerUI.timeClient.socketChannel.writeAndFlush(customMsgaa);

                    System.out.println("5====>指令0x03的反馈到api服务器 ");
                }
                break;
                case 0x04:{

                    BasePackage mBasePackage=new BasePackage();
                    byte[] ee2=new byte[2];
                    customMsgEnd.getBytes(0,ee2);
                    System.out.println("服务器端收到客户端返回的包尾是--->"+ CRCUtil.bytesToHexString(ee2));
                    ByteBuf customMsgBody2=Unpooled.buffer(req.length);
                    customMsgBody2.writeBytes(req);
                    mBasePackage.setCustomMsgAttribute(customMsgFlag,customMsgLen,customMsgChannel,customMsgProtocolVersion,customMsgBody2,customMsgEnd);
                    StringBuffer sb = new StringBuffer();
                    sb.append("服务器端收到客户端"+uuid+"命令0x04的应答   \r\n");
                    System.out.println("服务器收到客户端命令0x04的应答  ");
                    if( mBasePackage.checkCRC()){
                        sb.append("CRC验证成功！\r\n");
                    }else {
                        sb.append("CRC验证失败！\r\n");
                    }
                    sb.append(CRCUtil.bytesToHexString(req)+".");
                    System.out.println("服务器写了一条UID和有效期到智能门禁指令完成");
                    sb.append("\r\n服务器写了一条UID和有效期到智能门禁指令完成\r\n\r\n");
                    System.out.println();
                    logger.info(sb.toString());
                    mUICallBack.refreshText(sb.toString());

                    //                    调用api服务器通知开门完成
                    BasePackage mBasePackage2 = new BasePackage();
                    ByteBuf flag = Unpooled.buffer(2);
                    flag.writeBytes(new byte[]{(byte) 0xEF, 0x3A});
                    mBasePackage2.setFlag(flag);
                    mBasePackage2.setChannel(customMsgChannel);
                    mBasePackage2.setProtocolVersion(customMsgProtocolVersion);
                    ByteBuf body = Unpooled.buffer(req.length);
                    body.writeBytes(req);
                    mBasePackage2.setBody(body);
                    CustomMsg customMsgaa = mBasePackage2.getCustomMsg();
                    byte[] ee22 = new byte[2];
                    customMsgaa.getEnd().getBytes(0, ee22);
                    System.out.println("客户端应答应服务器的包，包尾是--->" + CRCUtil.bytesToHexString(ee22));
                    LockServerUI.timeClient.socketChannel.writeAndFlush(customMsgaa);

//                    SocketChannel obj =  TempTestChannelManagerService.getGatewayChannel("050100000001");
//
//                    if(null!=obj) {
//                        BasePackage mBasePackage2 = new BasePackage();
//                        ByteBuf flag = Unpooled.buffer(2);
//                        flag.writeBytes(new byte[]{(byte) 0xEF, 0x3A});
//                        mBasePackage2.setFlag(flag);
//                        mBasePackage2.setChannel((byte) 0x11);
//                        mBasePackage2.setProtocolVersion((byte) 0x01);
//                        byte[] data = {0x14,
//                                0x01, 0x02, 0x03, 0x04, 0x05, 0x06,
//                                0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
//                                0x01, 0x02, 0x03, 0x04,
//                                0x01, 0x02, 0x03, 0x04,
//                                0x01
//                        };
//                        ByteBuf body = Unpooled.buffer(data.length);
//                        body.writeBytes(data);
//                        mBasePackage2.setBody(body);
//                        CustomMsg customMsgaa = mBasePackage2.getCustomMsg();
//                        byte[] ee22 = new byte[2];
//                        customMsgaa.getEnd().getBytes(0, ee22);
//                        System.out.println("客户端应答应服务器的包，包尾是--->" + CRCUtil.bytesToHexString(ee22));
//                        obj.writeAndFlush(customMsgaa);
//
//                    }

                }
                break;
//                case 0x14:{
//                    try {
//                        SocketChannel obj =  TempTestChannelManagerService.getGatewayChannel("010203040506");
//
//                            BasePackage mBasePackage=new BasePackage();
//                            ByteBuf flag=Unpooled.buffer(2);
//                            flag.writeBytes(new byte[]{(byte)0xEF,0x3A});
//                            mBasePackage.setFlag(flag);
//                            mBasePackage.setChannel((byte) 0x01);
//                            mBasePackage.setProtocolVersion((byte) 0x01);
//                            byte[] bbody={0x04
//                                    ,0x01,0x02,0x03,0x04,0x05,0x06
//                                    ,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08
//                                    ,0x01,0x02,0x03,0x04
//                                    ,0x01,0x02,0x03,0x04};
//                            ByteBuf body=Unpooled.buffer(bbody.length);
//                            body.writeBytes(bbody);
//                            mBasePackage.setBody(body);
//                            CustomMsg customMsg14=mBasePackage.getCustomMsg();
//                            byte[] ee=new byte[2];
//                            customMsg14.getEnd().getBytes(0,ee);
//                            System.out.println("客户端发出的包，包尾是--->"+ CRCUtil.bytesToHexString(ee));
//                            obj.writeAndFlush(customMsg14);
//
//
//
//                    } catch (Exception e1) {
//                        e1.printStackTrace();
//                    }
//
//
//                }
//                break;
                case 0x05:{



                    BasePackage mBasePackage=new BasePackage();
                    byte[] ee2=new byte[2];
                    customMsgEnd.getBytes(0,ee2);
                    System.out.println("服务器端收到客户端返回的包尾是--->"+ CRCUtil.bytesToHexString(ee2));
                    ByteBuf customMsgBody2=Unpooled.buffer(req.length);
                    customMsgBody2.writeBytes(req);
                    mBasePackage.setCustomMsgAttribute(customMsgFlag,customMsgLen,customMsgChannel,customMsgProtocolVersion,customMsgBody2,customMsgEnd);
                    StringBuffer sb = new StringBuffer();
                    sb.append("服务器端收到客户端"+uuid+"命令0x05的应答   \r\n");
                    System.out.println("服务器收到客户端命令0x05的应答  ");
                    if( mBasePackage.checkCRC()){
                        sb.append("CRC验证成功！\r\n");
                    }else {
                        sb.append("CRC验证失败！\r\n");
                    }
                    sb.append(CRCUtil.bytesToHexString(req)+".");
                    System.out.println("服务器从智能门禁删除一条UID指令完成");
                    sb.append("\r\n服务器从智能门禁删除一条UID指令完成\r\n\r\n");
                    System.out.println();
                    logger.info(sb.toString());
                    mUICallBack.refreshText(sb.toString());


                    //                    调用api服务器通知开门完成
                    BasePackage mBasePackage2 = new BasePackage();
                    ByteBuf flag = Unpooled.buffer(2);
                    flag.writeBytes(new byte[]{(byte) 0xEF, 0x3A});
                    mBasePackage2.setFlag(flag);
                    mBasePackage2.setChannel(customMsgChannel);
                    mBasePackage2.setProtocolVersion(customMsgProtocolVersion);
                    ByteBuf body = Unpooled.buffer(req.length);
                    body.writeBytes(req);
                    mBasePackage2.setBody(body);
                    CustomMsg customMsgaa = mBasePackage2.getCustomMsg();
                    byte[] ee22 = new byte[2];
                    customMsgaa.getEnd().getBytes(0, ee22);
                    System.out.println("客户端应答应服务器的包，包尾是--->" + CRCUtil.bytesToHexString(ee22));
                    LockServerUI.timeClient.socketChannel.writeAndFlush(customMsgaa);


                }
                break;
//                case 0x16:{
//
//                    try {
//                        SocketChannel obj =  TempTestChannelManagerService.getGatewayChannel("010203040506");
//
//
//                            BasePackage mBasePackage=new BasePackage();
//                            ByteBuf flag=Unpooled.buffer(2);
//                            flag.writeBytes(new byte[]{(byte)0xEF,0x3A});
//                            mBasePackage.setFlag(flag);
//                            mBasePackage.setChannel((byte) 0x01);
//                            mBasePackage.setProtocolVersion((byte) 0x01);
//                            byte[] bbody={0x06
//                                    ,0x01,0x02,0x03,0x04,0x05,0x06
//                                    ,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08
//                                    ,0x01
//                                    ,0x01,0x02,0x03,0x04};
//                            ByteBuf body=Unpooled.buffer(bbody.length);
//                            body.writeBytes(bbody);
//                            mBasePackage.setBody(body);
//                            CustomMsg customMsg16=mBasePackage.getCustomMsg();
//                            byte[] ee=new byte[2];
//                            customMsg16.getEnd().getBytes(0,ee);
//                            System.out.println("客户端发出的包，包尾是--->"+ CRCUtil.bytesToHexString(ee));
//                            obj.writeAndFlush(customMsg16);
//
//
//                    } catch (Exception e1) {
//                        e1.printStackTrace();
//                    }
//
//                }
//                break;


                case 0x06:{


                    System.out.println("==LockServerHandler==>门锁服务器 收到门锁开门指令0x06的反馈 ");

                    BasePackage mBasePackage=new BasePackage();
                    byte[] ee2=new byte[2];
                    customMsgEnd.getBytes(0,ee2);
                    System.out.println("服务器端收到客户端返回的包尾是--->"+ CRCUtil.bytesToHexString(ee2));
                    ByteBuf customMsgBody2=Unpooled.buffer(req.length);
                    customMsgBody2.writeBytes(req);
                    mBasePackage.setCustomMsgAttribute(customMsgFlag,customMsgLen,customMsgChannel,customMsgProtocolVersion,customMsgBody2,customMsgEnd);
                    StringBuffer sb = new StringBuffer();
                    sb.append("服务器端收到客户端"+uuid+"命令0x06的应答   \r\n");
                    System.out.println("服务器收到客户端命令0x06的应答  ");
                    if( mBasePackage.checkCRC()){
                        sb.append("CRC验证成功！\r\n");
                        System.out.println("CRC验证成功");
                    }else {
                        sb.append("CRC验证失败！\r\n");
                        System.out.println("CRC验证失败！");
                    }
                    sb.append(CRCUtil.bytesToHexString(req)+".");
                    logger.info(sb.toString());
                    mUICallBack.refreshText(sb.toString());


                    BasePackage mBasePackage2 = new BasePackage();
                    ByteBuf flag = Unpooled.buffer(2);
                    flag.writeBytes(new byte[]{(byte) 0xEF, 0x3A});
                    mBasePackage2.setFlag(flag);
                    mBasePackage2.setChannel(customMsgChannel);
                    mBasePackage2.setProtocolVersion(customMsgProtocolVersion);



                    ByteBuf body = Unpooled.buffer(req.length);
                    body.writeBytes(req);

                    mBasePackage2.setBody(body);
                    CustomMsg customMsgaa = mBasePackage2.getCustomMsg();
                    byte[] ee22 = new byte[2];
                    customMsgaa.getEnd().getBytes(0, ee22);
                    System.out.println("锁服务器中转到锁的包，包尾是--->" + CRCUtil.bytesToHexString(ee22));
                    LockServerUI.timeClient.socketChannel.writeAndFlush(customMsgaa);
                    System.out.println("5====>门锁服务器 将门锁开门指令0x06的反馈到api服务器 ");




                }
                break;
                case 0x07:{



                    BasePackage mBasePackage=new BasePackage();
                    ByteBuf customMsgBody2=Unpooled.buffer(req.length);
                    customMsgBody2.writeBytes(req);
                    byte[] ee=new byte[2];
                    customMsgEnd.getBytes(0,ee);
                    System.out.println("服务器收到的的包，包尾是--->"+ CRCUtil.bytesToHexString(ee));
                    mBasePackage.setCustomMsgAttribute(customMsgFlag,customMsgLen,customMsgChannel,customMsgProtocolVersion,customMsgBody2,customMsgEnd);
                    StringBuffer sb=new StringBuffer();
                    sb.append("收到"+uuid+"指令0x07 \r\n ");
                    System.out.println("收到指令0x07");
                    if( mBasePackage.checkCRC()){
                        sb.append("CRC验证成功！\r\n");
                    }else {
                        sb.append("CRC验证成功！\r\n");
                    }
                    sb.append(CRCUtil.bytesToHexString(req)+".");
                    System.out.println();
                    System.out.println("  服务器返回客户端0x07指令的结果");
                    sb.append("服务器返回客户端0x07指令的结果 \r\n\r\n");
                    System.out.println();

                    mUICallBack.refreshText(sb.toString());




                    //                    调用api服务器
                    BasePackage mBasePackage3 = new BasePackage();
                    ByteBuf flag3 = Unpooled.buffer(2);
                    flag3.writeBytes(new byte[]{(byte) 0xEF, 0x3A});
                    mBasePackage3.setFlag(flag3);
                    mBasePackage3.setChannel(customMsgChannel);
                    mBasePackage3.setProtocolVersion(customMsgProtocolVersion);



                    ByteBuf body = Unpooled.buffer(req.length);
                    body.writeBytes(req);
                    mBasePackage3.setBody(body);
                    CustomMsg customMsg3 = mBasePackage3.getCustomMsg();
                    byte[] ee22 = new byte[2];
                    customMsg3.getEnd().getBytes(0, ee22);
                    System.out.println("客户端应答应服务器的包，包尾是--->" + CRCUtil.bytesToHexString(ee22));
                    System.out.println(customMsg3.getLen()+"《《《《《《《《《《《《《《《《《《《《《《");
                    LockServerUI.timeClient.socketChannel.writeAndFlush(customMsg3);


                }
                break;
                case 0x08:{


                    BasePackage mBasePackage=new BasePackage();
                    ByteBuf customMsgBody2=Unpooled.buffer(req.length);
                    customMsgBody2.writeBytes(req);
                    byte[] ee=new byte[2];
                    customMsgEnd.getBytes(0,ee);
                    System.out.println("服务器收到的的包，包尾是--->"+ CRCUtil.bytesToHexString(ee));
                    mBasePackage.setCustomMsgAttribute(customMsgFlag,customMsgLen,customMsgChannel,customMsgProtocolVersion,customMsgBody2,customMsgEnd);
                    StringBuffer sb=new StringBuffer();
                    sb.append("收到"+uuid+"指令0x08 \r\n ");
                    System.out.println("收到指令0x08");
                    if( mBasePackage.checkCRC()){
                        sb.append("CRC验证成功！\r\n");
                    }else {
                        sb.append("CRC验证成功！\r\n");
                    }
                    sb.append(CRCUtil.bytesToHexString(req)+".");
                    System.out.println();
                    System.out.println("  服务器返回客户端0x08指令的结果");
                    sb.append("服务器返回客户端0x08指令的结果 \r\n\r\n");
                    System.out.println();


                    //                    调用api服务器
                    BasePackage mBasePackage3 = new BasePackage();
                    ByteBuf flag3 = Unpooled.buffer(2);
                    flag3.writeBytes(new byte[]{(byte) 0xEF, 0x3A});
                    mBasePackage3.setFlag(flag3);
                    mBasePackage3.setChannel(customMsgChannel);
                    mBasePackage3.setProtocolVersion(customMsgProtocolVersion);
                    ByteBuf body = Unpooled.buffer(req.length);
                    body.writeBytes(req);
                    mBasePackage3.setBody(body);
                    CustomMsg customMsg3 = mBasePackage3.getCustomMsg();
                    byte[] ee22 = new byte[2];
                    customMsg3.getEnd().getBytes(0, ee22);
                    System.out.println("客户端应答应服务器的包，包尾是--->" + CRCUtil.bytesToHexString(ee22));
                    System.out.println(customMsg3.getLen()+"《《《《《《《《《《《《《《《《《《《《《《");
                    LockServerUI.timeClient.socketChannel.writeAndFlush(customMsg3);



//                    byte[] order={0x08};
//                    byte[] mac=new byte[6];
//                    System.arraycopy(req, 1, mac, 0, 6);
//                    byte[] time=new byte[4];
//                    System.arraycopy(req, 8, time, 0, 4);
//                    Calendar calendar= Calendar.getInstance();
//                    byte[] rightTime= CRCUtil.timeToBytes(calendar);
//                    byte[] resoult={0x01};
//
//                    BasePackage mBasePackage2=new BasePackage();
//                    ByteBuf flag=Unpooled.buffer(2);
//                    flag.writeBytes(new byte[]{(byte)0xEF,0x3A});
//                    mBasePackage2.setFlag(flag);
//                    mBasePackage2.setChannel((byte) 0x11);
//                    mBasePackage2.setProtocolVersion((byte) 0x01);
//
//                    ByteBuf body2=Unpooled.copiedBuffer(order,mac,time,rightTime,resoult);
//                    mBasePackage2.setBody(body2);
//                    CustomMsg customMsgaa=mBasePackage2.getCustomMsg();
//                    byte[] ee2=new byte[2];
//                    customMsgaa.getEnd().getBytes(0,ee2);
//                    System.out.println("服务器返回客户端的包，包尾是--->"+ CRCUtil.bytesToHexString(ee2));
//                    ctx.writeAndFlush(customMsgaa);
                    logger.info(sb.toString());
                    mUICallBack.refreshText(sb.toString());


                }
                break;
                case 0x09:{

                    BasePackage mBasePackage=new BasePackage();
                    byte[] ee2=new byte[2];
                    customMsgEnd.getBytes(0,ee2);
                    System.out.println("服务器端收到客户端返回的包尾是--->"+ CRCUtil.bytesToHexString(ee2));
                    ByteBuf customMsgBody2=Unpooled.buffer(req.length);
                    customMsgBody2.writeBytes(req);
                    mBasePackage.setCustomMsgAttribute(customMsgFlag,customMsgLen,customMsgChannel,customMsgProtocolVersion,customMsgBody2,customMsgEnd);
                    StringBuffer sb = new StringBuffer();
                    sb.append("服务器端收到客户端"+uuid+"命令0x09的应答   \r\n");
                    System.out.println("服务器收到客户端命令0x09的应答  ");
                    if( mBasePackage.checkCRC()){
                        sb.append("CRC验证成功！\r\n");
                    }else {
                        sb.append("CRC验证失败！\r\n");
                    }
                    sb.append(CRCUtil.bytesToHexString(req)+".");
                    System.out.println("服务器清空智能门禁所有UID和有效期指令完成");
                    sb.append("\r\n服务器清空智能门禁所有UID和有效期指令完成\r\n\r\n");
                    System.out.println();
                    logger.info(sb.toString());
                    mUICallBack.refreshText(sb.toString());



//                    通知api服务器完成
                    BasePackage mBasePackage2 = new BasePackage();
                    ByteBuf flag = Unpooled.buffer(2);
                    flag.writeBytes(new byte[]{(byte) 0xEF, 0x3A});
                    mBasePackage2.setFlag(flag);
                    mBasePackage2.setChannel(customMsgChannel);
                    mBasePackage2.setProtocolVersion(customMsgProtocolVersion);



                    ByteBuf body = Unpooled.buffer(req.length);
                    body.writeBytes(req);

                    mBasePackage2.setBody(body);
                    CustomMsg customMsgaa = mBasePackage2.getCustomMsg();
                    byte[] ee22 = new byte[2];
                    customMsgaa.getEnd().getBytes(0, ee22);
                    System.out.println("客户端应答应服务器的包，包尾是--->" + CRCUtil.bytesToHexString(ee22));
                    LockServerUI.timeClient.socketChannel.writeAndFlush(customMsgaa);




                }
                break;
                case 0x0A:{

                    BasePackage mBasePackage=new BasePackage();
                    ByteBuf customMsgBody2=Unpooled.buffer(req.length);
                    customMsgBody2.writeBytes(req);
                    byte[] ee=new byte[2];
                    customMsgEnd.getBytes(0,ee);
                    System.out.println("服务器收到的的包，包尾是--->"+ CRCUtil.bytesToHexString(ee));
                    mBasePackage.setCustomMsgAttribute(customMsgFlag,customMsgLen,customMsgChannel,customMsgProtocolVersion,customMsgBody2,customMsgEnd);
                    StringBuffer sb=new StringBuffer();
                    sb.append("收到"+uuid+"指令0x0A \r\n ");
                    System.out.println("收到指令0x0A");
                    if( mBasePackage.checkCRC()){
                        sb.append("CRC验证成功！\r\n");
                    }else {
                        sb.append("CRC验证成功！\r\n");
                    }
                    sb.append(CRCUtil.bytesToHexString(req)+".");
                    System.out.println();
                    System.out.println("  服务器返回客户端0x0A指令的结果");
                    sb.append("服务器返回客户端0x0A指令的结果 \r\n\r\n");
                    System.out.println();









                    //                    调用api服务器
                    BasePackage mBasePackage3 = new BasePackage();
                    ByteBuf flag3 = Unpooled.buffer(2);
                    flag3.writeBytes(new byte[]{(byte) 0xEF, 0x3A});
                    mBasePackage3.setFlag(flag3);
                    mBasePackage3.setChannel(customMsgChannel);
                    mBasePackage3.setProtocolVersion(customMsgProtocolVersion);
                    ByteBuf body = Unpooled.buffer(req.length);
                    body.writeBytes(req);
                    mBasePackage3.setBody(body);
                    CustomMsg customMsg3 = mBasePackage3.getCustomMsg();
                    byte[] ee22 = new byte[2];
                    customMsg3.getEnd().getBytes(0, ee22);
                    System.out.println("客户端应答应服务器的包，包尾是--->" + CRCUtil.bytesToHexString(ee22));
                    System.out.println(customMsg3.getLen()+"《《《《《《《《《《《《《《《《《《《《《《");
                    LockServerUI.timeClient.socketChannel.writeAndFlush(customMsg3);

                }
                break;
                case 0x0B:{


                    BasePackage mBasePackage=new BasePackage();
                    ByteBuf customMsgBody2=Unpooled.buffer(req.length);
                    customMsgBody2.writeBytes(req);
                    byte[] ee=new byte[2];
                    customMsgEnd.getBytes(0,ee);
                    System.out.println("服务器收到的的包，包尾是--->"+ CRCUtil.bytesToHexString(ee));
                    mBasePackage.setCustomMsgAttribute(customMsgFlag,customMsgLen,customMsgChannel,customMsgProtocolVersion,customMsgBody2,customMsgEnd);
                    StringBuffer sb=new StringBuffer();
                    sb.append("收到"+uuid+"指令0x0B \r\n ");
                    System.out.println("收到指令0x0B");
                    if( mBasePackage.checkCRC()){
                        sb.append("CRC验证成功！\r\n");
                    }else {
                        sb.append("CRC验证成功！\r\n");
                    }
                    sb.append(CRCUtil.bytesToHexString(req)+".");
                    System.out.println();
                    System.out.println("  服务器返回客户端0x0B指令的结果");
                    sb.append("服务器返回客户端0x0B指令的结果 \r\n\r\n");
                    System.out.println();




                    //                    调用api服务器
                    BasePackage mBasePackage3 = new BasePackage();
                    ByteBuf flag3 = Unpooled.buffer(2);
                    flag3.writeBytes(new byte[]{(byte) 0xEF, 0x3A});
                    mBasePackage3.setFlag(flag3);
                    mBasePackage3.setChannel(customMsgChannel);
                    mBasePackage3.setProtocolVersion(customMsgProtocolVersion);
                    ByteBuf body = Unpooled.buffer(req.length);
                    body.writeBytes(req);
                    mBasePackage3.setBody(body);
                    CustomMsg customMsg3 = mBasePackage3.getCustomMsg();
                    byte[] ee22 = new byte[2];
                    customMsg3.getEnd().getBytes(0, ee22);
                    System.out.println("客户端应答应服务器的包，包尾是--->" + CRCUtil.bytesToHexString(ee22));
                    System.out.println(customMsg3.getLen()+"《《《《《《《《《《《《《《《《《《《《《《");
                    LockServerUI.timeClient.socketChannel.writeAndFlush(customMsg3);


                }
                break;
//                case 0x1C:{
//                    try {
//
//                        SocketChannel obj =  TempTestChannelManagerService.getGatewayChannel("010203040506");
//
//
//
//                            BasePackage mBasePackage=new BasePackage();
//                            ByteBuf flag=Unpooled.buffer(2);
//                            flag.writeBytes(new byte[]{(byte)0xEF,0x3A});
//                            mBasePackage.setFlag(flag);
//                            mBasePackage.setChannel((byte) 0x01);
//                            mBasePackage.setProtocolVersion((byte) 0x01);
//                            byte[] bbody={  0x0C,
//                                    0x01, 0x02, 0x03, 0x04, 0x05, 0x06,
//                                    0x01, 0x02, 0x03, 0x04,
//                                    0x01, 0x02, 0x03, 0x04,
//                                    0x01, 0x02,
//                                    0x01, 0x02, 0x03, 0x04};
//                            ByteBuf body=Unpooled.buffer(bbody.length);
//                            body.writeBytes(bbody);
//                            mBasePackage.setBody(body);
//                            CustomMsg customMsg1c=mBasePackage.getCustomMsg();
//                            byte[] ee=new byte[2];
//                            customMsg1c.getEnd().getBytes(0,ee);
//                            System.out.println("客户端发出的包，包尾是--->"+ CRCUtil.bytesToHexString(ee));
//                            obj.writeAndFlush(customMsg1c);
//
//                    } catch (Exception e1) {
//                        e1.printStackTrace();
//                    }
//
//
//                }
//                break;


                case 0x0C:{


                    BasePackage mBasePackage=new BasePackage();
                    byte[] ee2=new byte[2];
                    customMsgEnd.getBytes(0,ee2);
                    System.out.println("服务器端收到客户端返回的包尾是--->"+ CRCUtil.bytesToHexString(ee2));
                    ByteBuf customMsgBody2=Unpooled.buffer(req.length);
                    customMsgBody2.writeBytes(req);
                    mBasePackage.setCustomMsgAttribute(customMsgFlag,customMsgLen,customMsgChannel,customMsgProtocolVersion,customMsgBody2,customMsgEnd);
                    StringBuffer sb = new StringBuffer();
                    sb.append("服务器端收到客户端"+uuid+"命令0x0C的应答   \r\n");
                    System.out.println("服务器收到客户端命令0x0C的应答  ");
                    if( mBasePackage.checkCRC()){
                        sb.append("CRC验证成功！\r\n");
                    }else {
                        sb.append("CRC验证失败！\r\n");
                    }
                    sb.append(CRCUtil.bytesToHexString(req)+".");
                    System.out.println("服务器读取给定时间范围内开门日志指令完成");
                    sb.append("\r\n服务器读取给定时间范围内开门日志指令完成\r\n\r\n");
                    System.out.println();
                    logger.info(sb.toString());
                    mUICallBack.refreshText(sb.toString());



//                    通知api服务器完成
                    BasePackage mBasePackage2 = new BasePackage();
                    ByteBuf flag = Unpooled.buffer(2);
                    flag.writeBytes(new byte[]{(byte) 0xEF, 0x3A});
                    mBasePackage2.setFlag(flag);
                    mBasePackage2.setChannel(customMsgChannel);
                    mBasePackage2.setProtocolVersion(customMsgProtocolVersion);

                    ByteBuf body = Unpooled.buffer(req.length);
                    body.writeBytes(req);
                    mBasePackage2.setBody(body);
                    CustomMsg customMsgaa = mBasePackage2.getCustomMsg();
                    byte[] ee22 = new byte[2];
                    customMsgaa.getEnd().getBytes(0, ee22);
                    System.out.println("客户端应答应服务器的包，包尾是--->" + CRCUtil.bytesToHexString(ee22));
                    LockServerUI.timeClient.socketChannel.writeAndFlush(customMsgaa);

                }
                break;
                case 0x0D:{
                    BasePackage mBasePackage=new BasePackage();
                    byte[] ee2=new byte[2];
                    customMsgEnd.getBytes(0,ee2);
                    System.out.println("服务器端收到客户端返回的包尾是--->"+ CRCUtil.bytesToHexString(ee2));
                    ByteBuf customMsgBody2=Unpooled.buffer(req.length);
                    customMsgBody2.writeBytes(req);
                    mBasePackage.setCustomMsgAttribute(customMsgFlag,customMsgLen,customMsgChannel,customMsgProtocolVersion,customMsgBody2,customMsgEnd);
                    StringBuffer sb = new StringBuffer();
                    sb.append("服务器端收到客户端"+uuid+"命令0x0D的应答   \r\n");
                    System.out.println("服务器收到客户端命令0x0D的应答  ");
                    if( mBasePackage.checkCRC()){
                        sb.append("CRC验证成功！\r\n");
                    }else {
                        sb.append("CRC验证失败！\r\n");
                    }
                    sb.append(CRCUtil.bytesToHexString(req)+".");
                    System.out.println("服务器向智能门禁发送远程升级指令完成");
                    sb.append("\r\n服务器向智能门禁发送远程升级指令完成\r\n\r\n");
                    System.out.println();
                    logger.info(sb.toString());
                    mUICallBack.refreshText(sb.toString());


//                    通知api服务器完成
                    BasePackage mBasePackage2 = new BasePackage();
                    ByteBuf flag = Unpooled.buffer(2);
                    flag.writeBytes(new byte[]{(byte) 0xEF, 0x3A});
                    mBasePackage2.setFlag(flag);
                    mBasePackage2.setChannel((byte) 0x11);
                    mBasePackage2.setProtocolVersion((byte) 0x01);

                    ByteBuf body = Unpooled.buffer(req.length);
                    body.writeBytes(req);
                    mBasePackage2.setBody(body);
                    CustomMsg customMsgaa = mBasePackage2.getCustomMsg();
                    byte[] ee22 = new byte[2];
                    customMsgaa.getEnd().getBytes(0, ee22);
                    System.out.println("客户端应答应服务器的包，包尾是--->" + CRCUtil.bytesToHexString(ee22));
                    LockServerUI.timeClient.socketChannel.writeAndFlush(customMsgaa);


                }
                break;
                case 0x0E:{

                    BasePackage mBasePackage=new BasePackage();
                    byte[] ee2=new byte[2];
                    customMsgEnd.getBytes(0,ee2);
                    System.out.println("服务器端收到客户端返回的包尾是--->"+ CRCUtil.bytesToHexString(ee2));
                    ByteBuf customMsgBody2=Unpooled.buffer(req.length);
                    customMsgBody2.writeBytes(req);
                    mBasePackage.setCustomMsgAttribute(customMsgFlag,customMsgLen,customMsgChannel,customMsgProtocolVersion,customMsgBody2,customMsgEnd);
                    StringBuffer sb = new StringBuffer();
                    sb.append("服务器端收到客户端"+uuid+"命令0x0E的应答   \r\n");
                    System.out.println("服务器收到客户端命令0x0E的应答  ");
                    if( mBasePackage.checkCRC()){
                        sb.append("CRC验证成功！\r\n");
                    }else {
                        sb.append("CRC验证失败！\r\n");
                    }
                    sb.append(CRCUtil.bytesToHexString(req)+".");
                    System.out.println("服务器向智能门禁发送远程重启指令完成");
                    sb.append("\r\n服务器向智能门禁发送远程重启指令完成\r\n\r\n");
                    System.out.println();
                    logger.info(sb.toString());
                    mUICallBack.refreshText(sb.toString());



//                    通知api服务器完成
                    BasePackage mBasePackage2 = new BasePackage();
                    ByteBuf flag = Unpooled.buffer(2);
                    flag.writeBytes(new byte[]{(byte) 0xEF, 0x3A});
                    mBasePackage2.setFlag(flag);
                    mBasePackage2.setChannel(customMsgChannel);
                    mBasePackage2.setProtocolVersion(customMsgProtocolVersion);

                    ByteBuf body = Unpooled.buffer(req.length);
                    body.writeBytes(req);
                    mBasePackage2.setBody(body);
                    CustomMsg customMsgaa = mBasePackage2.getCustomMsg();
                    byte[] ee22 = new byte[2];
                    customMsgaa.getEnd().getBytes(0, ee22);
                    System.out.println("客户端应答应服务器的包，包尾是--->" + CRCUtil.bytesToHexString(ee22));
                    LockServerUI.timeClient.socketChannel.writeAndFlush(customMsgaa);


                }
                break;

//                case 0x27:{
//                    BasePackage mBasePackage=new BasePackage();
//                    byte[] ee2=new byte[2];
//                    customMsgEnd.getBytes(0,ee2);
//                    System.out.println("服务器端收到客户端返回的包尾是--->"+ CRCUtil.bytesToHexString(ee2));
//                    ByteBuf customMsgBody2=Unpooled.buffer(req.length);
//                    customMsgBody2.writeBytes(req);
//                    mBasePackage.setCustomMsgAttribute(customMsgFlag,customMsgLen,customMsgChannel,customMsgProtocolVersion,customMsgBody2,customMsgEnd);
//                    StringBuffer sb = new StringBuffer();
//                    sb.append("服务器端收到客户端"+uuid+"命令0x27的应答   \r\n");
//                    System.out.println("服务器收到客户端命令0x27的应答  ");
//                    if( mBasePackage.checkCRC()){
//                        sb.append("CRC验证成功！\r\n");
//                    }else {
//                        sb.append("CRC验证失败！\r\n");
//                    }
//                    sb.append(CRCUtil.bytesToHexString(req)+".");
//                    System.out.println(" 、 开门信息 上报到服务器 指令");
//                    sb.append("\r\n 、 开门信息 上报到服务器 指令\r\n\r\n");
//                    System.out.println();
//                    logger.info(sb.toString());
//                    mUICallBack.refreshText(sb.toString());
//
//                }
//                break;
//                case 0x2A:{
//                    BasePackage mBasePackage=new BasePackage();
//                    byte[] ee2=new byte[2];
//                    customMsgEnd.getBytes(0,ee2);
//                    System.out.println("服务器端收到客户端返回的包尾是--->"+ CRCUtil.bytesToHexString(ee2));
//                    ByteBuf customMsgBody2=Unpooled.buffer(req.length);
//                    customMsgBody2.writeBytes(req);
//                    mBasePackage.setCustomMsgAttribute(customMsgFlag,customMsgLen,customMsgChannel,customMsgProtocolVersion,customMsgBody2,customMsgEnd);
//                    StringBuffer sb = new StringBuffer();
//
//
//
//                    byte[] mac=new byte[6];
//                    System.arraycopy(req, 1, mac, 0, 6);
//                    System.out.println("~~~~~~~~~33333~~~~~~~~" + CRCUtil.bytesToHexString(mac));
//
//
//                    sb.append("服务器端收到客户端"+uuid+"命令0x2A的应答   \r\n");
//                    System.out.println("服务器收到客户端命令0x2A的应答  ");
//                    if( mBasePackage.checkCRC()){
//                        sb.append("CRC验证成功！\r\n");
//                    }else {
//                        sb.append("CRC验证失败！\r\n");
//                    }
//                    sb.append(CRCUtil.bytesToHexString(req)+".");
//                    System.out.println(" 、 校对时间 指令");
//                    sb.append("\r\n 、 校对时间 指令\r\n\r\n");
//                    System.out.println();
//                    logger.info(sb.toString());
//                    mUICallBack.refreshText(sb.toString());
//
//                }
//                break;
//                case 0x2B:{
//                    BasePackage mBasePackage=new BasePackage();
//                    byte[] ee2=new byte[2];
//                    customMsgEnd.getBytes(0,ee2);
//                    System.out.println("服务器端收到客户端返回的包尾是--->"+ CRCUtil.bytesToHexString(ee2));
//                    ByteBuf customMsgBody2=Unpooled.buffer(req.length);
//                    customMsgBody2.writeBytes(req);
//                    mBasePackage.setCustomMsgAttribute(customMsgFlag,customMsgLen,customMsgChannel,customMsgProtocolVersion,customMsgBody2,customMsgEnd);
//                    StringBuffer sb = new StringBuffer();
//                    sb.append("服务器端收到客户端"+uuid+"命令0x2B的应答   \r\n");
//                    System.out.println("服务器收到客户端命令0x2B的应答  ");
//                    if( mBasePackage.checkCRC()){
//                        sb.append("CRC验证成功！\r\n");
//                    }else {
//                        sb.append("CRC验证失败！\r\n");
//                    }
//                    sb.append(CRCUtil.bytesToHexString(req)+".");
//                    System.out.println(" 报警 指令");
//                    sb.append("\r\n 、 报警 指令\r\n\r\n");
//                    System.out.println();
//                    logger.info(sb.toString());
//                    mUICallBack.refreshText(sb.toString());
//
//                }
//                break;


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
        System.err.println(cause.getMessage());
//        ctx.close();
    }


    private void registerMac(byte[] lockMac,byte[] serverMac,byte[] time){


    }

    private void  openCompleteToApiServer(){


    }
}
