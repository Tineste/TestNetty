package orz.xuchao.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import orz.xuchao.server.bean.BasePackage;
import orz.xuchao.server.bean.CustomMsg;
import orz.xuchao.server.channelmanager.GatewayService;
import orz.xuchao.server.uicallback.UICallBack;
import orz.xuchao.server.utils.CRCUtil;

import java.util.Calendar;

import static com.oracle.jrockit.jfr.ContentType.Bytes;


/**
 * Created by Administrator on 2017/7/6 0006.
 */
public class TimeServerHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
        String uuid=ctx.channel().id().asLongText();
        GatewayService.removeGatewayChannel(uuid);
        System.out.println("设备id为"+ctx.channel().id()+"的设备断开了连接");
        mUICallBack.refreshText("\r\n设备id为"+ctx.channel().id()+"的设备断开了连接\r\n\r\n");


    }

    private UICallBack mUICallBack;


    public TimeServerHandler(UICallBack mUICallBack){
        this.mUICallBack = mUICallBack;
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String uuid=ctx.channel().id().asLongText();
        GatewayService.addGatewayChannel(uuid,(SocketChannel)ctx.channel());



        System.out.println("一个客户端连接进来了："+uuid+"  目前有"+GatewayService.getChannels().size()+"个设备连入服务器 ");
        mUICallBack.refreshText("\r\n一个客户端连接进来了："+uuid+"  目前有"+GatewayService.getChannels().size()+"个设备连入服务器 \r\n\r\n");
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
                case 0x01: {
//                    收
                    BasePackage mBasePackage=new BasePackage();
                    ByteBuf customMsgBody2=Unpooled.buffer(req.length);
                    customMsgBody2.writeBytes(req);
                    byte[] ee=new byte[2];
                    customMsgEnd.getBytes(0,ee);
                    System.out.println("服务器收到的的包，包尾是--->"+ CRCUtil.bytesToHexString(ee));
                    mBasePackage.setCustomMsgAttribute(customMsgFlag,customMsgLen,customMsgChannel,customMsgProtocolVersion,customMsgBody2,customMsgEnd);
                    StringBuffer sb=new StringBuffer();
                    sb.append("收到"+uuid+"指令0x01 \r\n ");
                    System.out.println("收到指令0x01");
                    if( mBasePackage.checkCRC()){
                        sb.append("CRC验证成功！\r\n");
                    }else {
                        sb.append("CRC验证失败！\r\n");
                    }
                    sb.append(CRCUtil.bytesToHexString(req)+".");
                    System.out.println();
                    System.out.println("  服务器返回客户端0x01指令的结果");
                    byte[] mac = new byte[6];
                    System.arraycopy(req, 1, mac, 0, 6);
                    byte[] time = new byte[4];
                    System.arraycopy(req, 7, time, 0, 4);
                    CRCUtil.bytesToHexString(time);

                    sb.append("\r\nmac地址是："+CRCUtil.bytesToHexString(mac)+"\r\n");
                    sb.append("客户端发出的时间是："+CRCUtil.bytesToTime(time)+"\r\n");

                    sb.append("服务器返回客户端0x01指令的结果 \r\n\r\n");
                    System.out.println();

//                    发
                    BasePackage mBasePackage2=new BasePackage();
                    ByteBuf flag=Unpooled.buffer(2);
                    flag.writeBytes(new byte[]{(byte)0xEF,0x3A});
                    mBasePackage2.setFlag(flag);
                    mBasePackage2.setChannel((byte) 0x11);
                    mBasePackage2.setProtocolVersion((byte) 0x01);

//                    byte[] byte1 = {0x01, 0x05, 0x01, 0x00, 0x01, 0x02, 0x03};

                    byte[] order={0x01};
                    byte[] byte1=new byte[order.length+mac.length];
                    System.arraycopy(order, 0, byte1, 0, order.length);
                    System.arraycopy(mac, 0, byte1, order.length , mac.length);



//                    域名
                    byte[] url = new byte[200];

//                    端口号+时间戳+结果码
//                    byte[] byte2 = {0x50,0x50,
//                            0x59, 0x3D, 0x34, 0x11,
//                            0x01};

                    byte[] port={0x50,0x50};



                    Calendar calendar = Calendar.getInstance();
                    byte[] time2=CRCUtil.timeToBytes(calendar);
                    byte[] result={0x01};

                    ByteBuf buf=Unpooled.copiedBuffer(port,time2,result);
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
                    System.out.println("服务器返回客户端的包，包尾是--->"+ CRCUtil.bytesToHexString(ee2));
                    ctx.writeAndFlush(customMsgaa);
                    mUICallBack.refreshText(sb.toString());
                }
                    break;
                case  0x02: {

                    BasePackage mBasePackage=new BasePackage();
                    ByteBuf customMsgBody2=Unpooled.buffer(req.length);
                    customMsgBody2.writeBytes(req);
                    byte[] ee=new byte[2];
                    customMsgEnd.getBytes(0,ee);
                    System.out.println("服务器收到的的包，包尾是--->"+ CRCUtil.bytesToHexString(ee));
                    mBasePackage.setCustomMsgAttribute(customMsgFlag,customMsgLen,customMsgChannel,customMsgProtocolVersion,customMsgBody2,customMsgEnd);
                    StringBuffer sb=new StringBuffer();
                    sb.append("收到"+uuid+"指令0x02 \r\n ");
                    System.out.println("收到指令0x02");
                    if( mBasePackage.checkCRC()){
                        sb.append("CRC验证成功！\r\n");
                    }else {
                        sb.append("CRC验证成功！\r\n");
                    }
                    sb.append(CRCUtil.bytesToHexString(req)+".");
                    System.out.println();
                    System.out.println("  服务器返回客户端0x02指令的结果");
                    sb.append("服务器返回客户端0x02指令的结果 \r\n\r\n");
                    System.out.println();


                    BasePackage mBasePackage2=new BasePackage();
                    ByteBuf flag=Unpooled.buffer(2);
                    flag.writeBytes(new byte[]{(byte)0xEF,0x3A});
                    mBasePackage2.setFlag(flag);
                    mBasePackage2.setChannel((byte) 0x11);
                    mBasePackage2.setProtocolVersion((byte) 0x01);

                    byte[] data = {0x02,
                            0x01, 0x02, 0x03, 0x04, 0x05, 0x06,
                            0x01,0x02,0x03,0x04,
                            0x01, 0x02, 0x03, 0x04, 0x05, 0x06,
                            0x01
                    };

                    ByteBuf body=Unpooled.buffer(data.length);
                    body.writeBytes(data);
                    mBasePackage2.setBody(body);
                    CustomMsg customMsgaa=mBasePackage2.getCustomMsg();
                    byte[] ee2=new byte[2];
                    customMsgaa.getEnd().getBytes(0,ee2);
                    System.out.println("服务器返回客户端的包，包尾是--->"+ CRCUtil.bytesToHexString(ee2));
                    ctx.writeAndFlush(customMsgaa);
                    mUICallBack.refreshText(sb.toString());

                }
                break;
                case 0x03:{
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
                    mUICallBack.refreshText(sb.toString());
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
                    mUICallBack.refreshText(sb.toString());

                }
                break;
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
                    mUICallBack.refreshText(sb.toString());

                }
                break;
                case 0x06:{



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
                    }else {
                        sb.append("CRC验证失败！\r\n");
                    }
                    sb.append(CRCUtil.bytesToHexString(req)+".");
                    System.out.println("服务器开门指令完成");
                    sb.append("\r\n服务器开门指令完成\r\n\r\n");
                    System.out.println();
                    mUICallBack.refreshText(sb.toString());



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



                    BasePackage mBasePackage2=new BasePackage();
                    ByteBuf flag=Unpooled.buffer(2);
                    flag.writeBytes(new byte[]{(byte)0xEF,0x3A});
                    mBasePackage2.setFlag(flag);
                    mBasePackage2.setChannel((byte) 0x11);
                    mBasePackage2.setProtocolVersion((byte) 0x01);

                    byte[] data ={0x07,
                            0x01, 0x02, 0x03, 0x04, 0x05, 0x06,
                            0x01, 0x02, 0x03, 0x04, 0x05, 0x06,0x07,0x08,
                            0x01,
                            0x01,0x02,0x03,0x04,
                            0x01
                    };

                    ByteBuf body=Unpooled.buffer(data.length);
                    body.writeBytes(data);
                    mBasePackage2.setBody(body);
                    CustomMsg customMsgaa=mBasePackage2.getCustomMsg();
                    byte[] ee2=new byte[2];
                    customMsgaa.getEnd().getBytes(0,ee2);
                    System.out.println("服务器返回客户端的包，包尾是--->"+ CRCUtil.bytesToHexString(ee2));
                    ctx.writeAndFlush(customMsgaa);
                    mUICallBack.refreshText(sb.toString());
                }
                break;
                case 0x08:{
//                    StringBuffer sb=new StringBuffer();
//                    sb.append("服务器端收到客户端"+uuid+"命令0x08的请求 \r\n ");
//                    System.out.println("服务器收到客户端命令0x08的请求  ");
//                    for (int i = 0; i < req.length; i++) {
//                        System.out.print(req[i]);
//                        sb.append(req[i]+".");
//                    }
//                    System.out.println();
//                    System.out.println("服务器返回客户端0x08指令的结果");
//                    sb.append("服务器返回客户端0x08指令的结果\r\n\r\n");
//
//                    System.out.println();
//                    mUICallBack.refreshText(sb.toString());



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



//                    ByteBuf flag = Unpooled.buffer(2);
//                    flag.writeBytes(new byte[]{(byte) 0xEF, 0x3A});
//                    Short len=0x14;
//                    byte channel = 0x11;
//                    byte protocolVersion = 0x01;
//                    byte[] data= {
//                            0x08,
//                            0x01, 0x02, 0x03, 0x04, 0x05, 0x06,
//                            0x01,0x02,0x03,0x04,
//                            0x01,0x02,0x03,0x04,
//                            0x01
//                    };
//                    ByteBuf body = Unpooled.buffer(data.length);
//                    body.writeBytes(data);
//                    ByteBuf end = Unpooled.buffer(2);
//                    end.writeBytes(new byte[]{0x2D, (byte) 0x6E});
//                    CustomMsg customMsg2 = new CustomMsg(flag, len, channel, protocolVersion, body, end);
//                    ctx.writeAndFlush(customMsg2);

                    BasePackage mBasePackage2=new BasePackage();
                    ByteBuf flag=Unpooled.buffer(2);
                    flag.writeBytes(new byte[]{(byte)0xEF,0x3A});
                    mBasePackage2.setFlag(flag);
                    mBasePackage2.setChannel((byte) 0x11);
                    mBasePackage2.setProtocolVersion((byte) 0x01);

                    byte[] data ={
                            0x08,
                            0x01, 0x02, 0x03, 0x04, 0x05, 0x06,
                            0x01,0x02,0x03,0x04,
                            0x01,0x02,0x03,0x04,
                            0x01
                    };

                    ByteBuf body=Unpooled.buffer(data.length);
                    body.writeBytes(data);
                    mBasePackage2.setBody(body);
                    CustomMsg customMsgaa=mBasePackage2.getCustomMsg();
                    byte[] ee2=new byte[2];
                    customMsgaa.getEnd().getBytes(0,ee2);
                    System.out.println("服务器返回客户端的包，包尾是--->"+ CRCUtil.bytesToHexString(ee2));
                    ctx.writeAndFlush(customMsgaa);
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
                    mUICallBack.refreshText(sb.toString());


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






                    BasePackage mBasePackage2=new BasePackage();
                    ByteBuf flag=Unpooled.buffer(2);
                    flag.writeBytes(new byte[]{(byte)0xEF,0x3A});
                    mBasePackage2.setFlag(flag);
                    mBasePackage2.setChannel((byte) 0x11);
                    mBasePackage2.setProtocolVersion((byte) 0x01);
                    byte[] data ={
                            0x0A,
                            0x01, 0x02, 0x03, 0x04, 0x05, 0x06,
                            0x01,0x02,0x03,0x04,
                            0x01,0x02,0x03,0x04,
                            0x01
                    };
                    ByteBuf body=Unpooled.buffer(data.length);
                    body.writeBytes(data);
                    mBasePackage2.setBody(body);
                    CustomMsg customMsgaa=mBasePackage2.getCustomMsg();
                    byte[] ee2=new byte[2];
                    customMsgaa.getEnd().getBytes(0,ee2);
                    System.out.println("服务器返回客户端的包，包尾是--->"+ CRCUtil.bytesToHexString(ee2));
                    ctx.writeAndFlush(customMsgaa);
                    mUICallBack.refreshText(sb.toString());

                }
                break;
                case 0x0B:{
//                    StringBuffer sb=new StringBuffer();
//                    sb.append("服务器端收到客户端"+uuid+"命令0x0B的请求  \r\n");
//                    System.out.println("服务器收到客户端命令0x0B的请求  ");
//                    for (int i = 0; i < req.length; i++) {
//                        System.out.print(req[i]);
//                        sb.append(req[i]+".");
//                    }
//                    System.out.println();
//                    System.out.println("服务器返回客户端0x0B指令的结果");
//                    sb.append("服务器返回客户端0x0B指令的结果\r\n\r\n");
//
//                    System.out.println();
//                    mUICallBack.refreshText(sb.toString());


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



//                    ByteBuf flag = Unpooled.buffer(2);
//                    flag.writeBytes(new byte[]{(byte) 0xEF, 0x3A});
//                    Short len=0x11;
//                    byte channel = 0x11;
//                    byte protocolVersion = 0x01;
//                    byte[] data= {
//                            0x0B,
//                            0x01, 0x02, 0x03, 0x04, 0x05, 0x06,
//                            0x01,
//                            0x01,0x02,0x03,0x04,
//                            0x01
//                    };
//                    ByteBuf body = Unpooled.buffer(data.length);
//                    body.writeBytes(data);
//                    ByteBuf end = Unpooled.buffer(2);
//                    end.writeBytes(new byte[]{0x2D, (byte) 0x6E});
//                    CustomMsg customMsg2 = new CustomMsg(flag, len, channel, protocolVersion, body, end);
//                    ctx.writeAndFlush(customMsg2);

                    BasePackage mBasePackage2=new BasePackage();
                    ByteBuf flag=Unpooled.buffer(2);
                    flag.writeBytes(new byte[]{(byte)0xEF,0x3A});
                    mBasePackage2.setFlag(flag);
                    mBasePackage2.setChannel((byte) 0x11);
                    mBasePackage2.setProtocolVersion((byte) 0x01);
                    byte[] data ={
                            0x0B,
                            0x01, 0x02, 0x03, 0x04, 0x05, 0x06,
                            0x01,
                            0x01,0x02,0x03,0x04,
                            0x01
                    };
                    ByteBuf body=Unpooled.buffer(data.length);
                    body.writeBytes(data);
                    mBasePackage2.setBody(body);
                    CustomMsg customMsgaa=mBasePackage2.getCustomMsg();
                    byte[] ee2=new byte[2];
                    customMsgaa.getEnd().getBytes(0,ee2);
                    System.out.println("服务器返回客户端的包，包尾是--->"+ CRCUtil.bytesToHexString(ee2));
                    ctx.writeAndFlush(customMsgaa);
                    mUICallBack.refreshText(sb.toString());


                }
                break;
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
                    mUICallBack.refreshText(sb.toString());

                }
                break;
                case 0x0D:{
//                    StringBuffer sb=new StringBuffer();
//                    sb.append("服务器端收到客户端"+uuid+"命令0x0D的应答 \r\n ");
//                    System.out.println("服务器收到客户端命令0x0D的应答  ");
//                    for (int i = 0; i < req.length; i++) {
//                        System.out.print(req[i]);
//                        sb.append(req[i]+".");
//                    }
//                    System.out.println();
//
//
//                    System.out.println("服务器读取给定时间范围内开门日志指令完成");
//                    sb.append("\r\n服务器 读取给定时间范围内开门日志指令完成\r\n\r\n");
//                    System.out.println();
//                    mUICallBack.refreshText(sb.toString());





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
                    mUICallBack.refreshText(sb.toString());

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
                    mUICallBack.refreshText(sb.toString());

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
