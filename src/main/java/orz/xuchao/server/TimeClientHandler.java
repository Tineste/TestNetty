package orz.xuchao.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import orz.xuchao.server.bean.BasePackage;
import orz.xuchao.server.bean.CustomMsg;
import orz.xuchao.server.uicallback.UICallBack;
import orz.xuchao.server.utils.CRCUtil;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;


/**
 * Created by Administrator on 2017/7/6 0006.
 */
public class TimeClientHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger=Logger.getLogger(TimeClientHandler.class.getName());
    private UICallBack mUICallBack;


    public TimeClientHandler(UICallBack mUICallBack){
        this.mUICallBack = mUICallBack;
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
            ByteBuf buf=customMsg.getBody();
            byte[] req=new byte[buf.readableBytes()];
            buf.readBytes(req);
            switch (req[0]){
                case 0x01: {
                    BasePackage mBasePackage=new BasePackage();
                    mBasePackage.setCustomMsg((CustomMsg)msg);
                    StringBuffer sb = new StringBuffer();
                    sb.append("客户收到端服务器命令0x01  \r\n");
                    System.out.println("客户收到端服务器命令0x01  ");

                    if( mBasePackage.checkCRC()){
                        sb.append("CRC验证成功！\r\n");
                    }else {
                        sb.append("CRC验证成功！\r\n");
                    }

                    sb.append(CRCUtil.bytesToHexString(req)+".");
                    System.out.println("...");

                    System.out.println("从中心服务器获取具体要链接的前置服务器域名或IP 完成");
                    sb.append("\r\n从中心服务器获取具体要链接的前置服务器域名或IP 完成\r\n\r\n");
                    System.out.println();
                    mUICallBack.refreshText(sb.toString());
                }













//                    System.out.println("----------------------------客户端应答服务器请求--------------------------");
//                    ByteBuf flag=Unpooled.buffer(2);
//                    flag.writeBytes(new byte[]{(byte)0xEF,0x3A});
//                    int len=24;
//                    byte channel=0x11;
//                    byte protocolVersion=0x01;
//                    byte[] bbody={0x01,
//                            0x05,0x01,0x00,0x01,0x02,0x03,
//                            0x23,(byte) 0xC9,0x3B,(byte) 0x89,0x4A,(byte) 0xED,(byte) 0xF0,0x65,
//                            0x59,0x3D,0x34,0x11,
//                            0x01
//                    };
//                    ByteBuf body=Unpooled.buffer(bbody.length);
//                    body.writeBytes(bbody);
//                    ByteBuf end=Unpooled.buffer(2);
//                    end.writeBytes(new byte[]{0x2D,(byte)0x6E});
//                    CustomMsg customMsg2 = new CustomMsg(flag, len,channel,protocolVersion,body, end);
//                    ctx.writeAndFlush(customMsg2);
                    break;
                case 0x02: {
                    StringBuffer sb = new StringBuffer();
                    sb.append("客户收到端服务器命令0x02 \r\n ");
                    System.out.println("客户收到端服务器命令0x02  ");
                    for (int i = 0; i < req.length; i++) {
                        System.out.print(req[i]);
                        sb.append(req[i]+".");
                    }

                    System.out.println("智能门禁向服务器申报MAC码并获取蓝牙密钥指令 完成");
                    sb.append("\r\n智能门禁向服务器申报MAC码并获取蓝牙密钥指令 完成\r\n\r\n");
                    System.out.println();
                    mUICallBack.refreshText(sb.toString());
                }
                break;
                case 0x03: {
                    StringBuffer sb = new StringBuffer();
                    sb.append("客户端收到服务器请求0x03 \r\n");
                    System.out.println("客户端收到服务器请求0x03 ");
                    for (int i = 0; i < req.length; i++) {
                        System.out.print(req[i]);
                        sb.append(req[i]+".");
                    }

                    System.out.println("客户端应答服务器0x03请求并返回数据");
                    sb.append("\r\n客户端应答服务器0x03请求并返回数据\r\n\r\n");
                    mUICallBack.refreshText(sb.toString());
                    System.out.println();
//                    应答服务器的请求
                    ByteBuf flag = Unpooled.buffer(2);
                    flag.writeBytes(new byte[]{(byte) 0xEF, 0x3A});

                    Short len = 0x18;
                    byte channel = 0x11;
                    byte protocolVersion = 0x01;
                    byte[] bbody = {0x03,
                            0x05, 0x01, 0x00, 0x01, 0x02, 0x03,
                            0x23, (byte) 0xC9, 0x3B, (byte) 0x89, 0x4A, (byte) 0xED, (byte) 0xF0, 0x65,
                            0x59, 0x3D, 0x34, 0x11,
                            0x01
                    };
                    ByteBuf body = Unpooled.buffer(bbody.length);
                    body.writeBytes(bbody);
                    ByteBuf end = Unpooled.buffer(2);
                    end.writeBytes(new byte[]{0x6F, (byte) 0x6B});
                    CustomMsg customMsg2 = new CustomMsg(flag, len, channel, protocolVersion, body, end);
                    ctx.writeAndFlush(customMsg2);
                }
                    break;
                case 0x04:{
                    System.out.println("客户端收到服务器请求0x04 ");
                    StringBuffer sb = new StringBuffer();
                    sb.append("客户端收到服务器请求0x04 \r\n");
                    for (int i = 0; i < req.length; i++) {
                        System.out.print(req[i]);
                        sb.append(req[i]+".");
                    }

                    System.out.println("客户端应答服务器0x04请求并返回数据");
                    sb.append("\r\n客户端应答服务器0x04请求并返回数据\r\n\r\n");
                    mUICallBack.refreshText(sb.toString());
                    System.out.println();
//                    应答服务器的请求
                    ByteBuf flag = Unpooled.buffer(2);
                    flag.writeBytes(new byte[]{(byte) 0xEF, 0x3A});
                    Short len = 0x1C;
                    byte channel = 0x11;
                    byte protocolVersion = 0x01;
                    byte[] bbody = {0x04,
                            0x01, 0x02, 0x03, 0x04, 0x05, 0x06,
                            0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
                            0x01,0x02,0x03,0x04,
                            0x01,0x02,0x03,0x04,
                            0x01
                    };
                    ByteBuf body = Unpooled.buffer(bbody.length);
                    body.writeBytes(bbody);
                    ByteBuf end = Unpooled.buffer(2);
                    end.writeBytes(new byte[]{0x6F, (byte) 0x6B});
                    CustomMsg customMsg2 = new CustomMsg(flag, len, channel, protocolVersion, body, end);
                    ctx.writeAndFlush(customMsg2);

                }
                break;
                case 0x05:{
                    System.out.println("客户端收到服务器请求0x05 ");
                    StringBuffer sb = new StringBuffer();
                    sb.append("客户端收到服务器请求0x05 \r\n");
                    for (int i = 0; i < req.length; i++) {
                        System.out.print(req[i]);
                        sb.append(req[i]+".");
                    }

                    System.out.println("客户端应答服务器0x05请求并返回数据");
                    sb.append("\r\n客户端应答服务器0x05请求并返回数据\r\n\r\n");
                    mUICallBack.refreshText(sb.toString());
                    System.out.println();
//                    应答服务器的请求
                    ByteBuf flag = Unpooled.buffer(2);
                    flag.writeBytes(new byte[]{(byte) 0xEF, 0x3A});
                    Short len = 0x18;
                    byte channel = 0x11;
                    byte protocolVersion = 0x01;
                    byte[] bbody = {0x05,
                            0x01, 0x02, 0x03, 0x04, 0x05, 0x06,
                            0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
                            0x01,0x02,0x03,0x04,
                            0x01
                    };
                    ByteBuf body = Unpooled.buffer(bbody.length);
                    body.writeBytes(bbody);
                    ByteBuf end = Unpooled.buffer(2);
                    end.writeBytes(new byte[]{0x6F, (byte) 0x6B});
                    CustomMsg customMsg2 = new CustomMsg(flag, len, channel, protocolVersion, body, end);
                    ctx.writeAndFlush(customMsg2);

                }
                break;
                case 0x06:{
                    System.out.println("客户端收到服务器请求0x06 ");
                    StringBuffer sb = new StringBuffer();
                    sb.append("客户端收到服务器请求0x06\r\n ");
                    for (int i = 0; i < req.length; i++) {
                        System.out.print(req[i]);
                        sb.append(req[i]+".");
                    }

                    System.out.println("客户端应答服务器0x06请求并返回数据");
                    sb.append("\r\n客户端应答服务器0x06请求并返回数据\r\n\r\n");
                    mUICallBack.refreshText(sb.toString());
                    System.out.println();
//                    应答服务器的请求
                    ByteBuf flag = Unpooled.buffer(2);
                    flag.writeBytes(new byte[]{(byte) 0xEF, 0x3A});
                    Short len = 0x19;
                    byte channel = 0x11;
                    byte protocolVersion = 0x01;
                    byte[] bbody = {0x06,
                            0x01, 0x02, 0x03, 0x04, 0x05, 0x06,
                            0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
                            0x01,
                            0x01,0x02,0x03,0x04,
                            0x01
                    };

                    ByteBuf body = Unpooled.buffer(bbody.length);
                    body.writeBytes(bbody);
                    ByteBuf end = Unpooled.buffer(2);
                    end.writeBytes(new byte[]{0x6F, (byte) 0x6B});
                    CustomMsg customMsg2 = new CustomMsg(flag, len, channel, protocolVersion, body, end);
                    ctx.writeAndFlush(customMsg2);
                }
                break;
                case 0x07:{
                    StringBuffer sb = new StringBuffer();
                    sb.append("客户端收到服务器命令0x07  \r\n");
                    System.out.println("客户收到端服务器命令0x07 ");
                    for (int i = 0; i < req.length; i++) {
                        System.out.print(req[i]);
                        sb.append(req[i]+".");
                    }

                    System.out.println("开门信息上报到服务器 完成");
                    sb.append("\r\n开门信息上报到服务器 完成\r\n\r\n");
                    mUICallBack.refreshText(sb.toString());
                    System.out.println();
                }
                break;
                case 0x08:{
                    StringBuffer sb = new StringBuffer();
                    sb.append("客户端收到服务器命令0x08  \r\n");
                    System.out.println("客户收到端服务器命令0x08  ");
                    for (int i = 0; i < req.length; i++) {
                        System.out.print(req[i]);
                        sb.append(req[i]+".");
                    }


                    System.out.println("心跳包给服务器  完成");
                    sb.append("\r\n心跳包给服务器  完成\r\n\r\n");
                    mUICallBack.refreshText(sb.toString());
                    System.out.println();
                }
                break;
                case 0x09:{
                    StringBuffer sb = new StringBuffer();
                    sb.append("客户端收到服务器命令0x09 \r\n");
                    System.out.println("客户收到端服务器命令0x09 ");
                    for (int i = 0; i < req.length; i++) {
                        System.out.print(req[i]);
                        sb.append(req[i]+".");
                    }
                    System.out.println("客户端应答服务器0x09请求并返回数据");
                    sb.append("\r\n客户端应答服务器0x09请求并返回数据\r\n\r\n");
                    mUICallBack.refreshText(sb.toString());
                    System.out.println();
//                    应答服务器的请求
                    ByteBuf flag = Unpooled.buffer(2);
                    flag.writeBytes(new byte[]{(byte) 0xEF, 0x3A});
                    Short len = 0x10;
                    byte channel = 0x11;
                    byte protocolVersion = 0x01;
                    byte[] bbody = {0x09,
                            0x01, 0x02, 0x03, 0x04, 0x05, 0x06,
                            0x01,0x02,0x03,0x04,
                            0x01
                    };
                    ByteBuf body = Unpooled.buffer(bbody.length);
                    body.writeBytes(bbody);
                    ByteBuf end = Unpooled.buffer(2);
                    end.writeBytes(new byte[]{0x6F, (byte) 0x6B});
                    CustomMsg customMsg2 = new CustomMsg(flag, len, channel, protocolVersion, body, end);
                    ctx.writeAndFlush(customMsg2);

                }
                break;
                case 0x0A:{
                    StringBuffer sb = new StringBuffer();
                    sb.append("客户端收到服务器命令0x0A  \r\n");
                    System.out.println("客户收到端服务器命令0x0A  ");
                    for (int i = 0; i < req.length; i++) {
                        System.out.print(req[i]);
                        sb.append(req[i]+".");
                    }
                    System.out.println(" 时间校准请求  完成");
                    sb.append(" \r\n时间校准请求  完成\r\n\r\n");
                    mUICallBack.refreshText(sb.toString());
                    System.out.println();

                }
                break;
                case 0x0B:{
                    StringBuffer sb = new StringBuffer();
                    sb.append("客户端收到服务器命令0x0B  \r\n");
                    System.out.println("客户收到端服务器命令0x0B  ");
                    for (int i = 0; i < req.length; i++) {
                        System.out.print(req[i]);
                        sb.append(req[i]+".");
                    }
                    System.out.println("报警信息上报到服务器  完成");
                    sb.append("\r\n报警信息上报到服务器  完成\r\n\r\n");
                    mUICallBack.refreshText(sb.toString());
                    System.out.println();
                }
                break;
                case 0x0C:{
                    StringBuffer sb = new StringBuffer();
                    sb.append("客户端收到服务器命令0x0C\r\n ");
                    System.out.println("客户收到端服务器命令0x0C ");
                    for (int i = 0; i < req.length; i++) {
                        System.out.print(req[i]);
                        sb.append(req[i]+".");
                    }
                    System.out.println("客户端应答服务器0x0C请求并返回数据");
                    sb.append("\r\n客户端应答服务器0x0C请求并返回数据\r\n\r\n");
                    mUICallBack.refreshText(sb.toString());
                    System.out.println();
//                    应答服务器的请求
                    ByteBuf flag = Unpooled.buffer(2);
                    flag.writeBytes(new byte[]{(byte) 0xEF, 0x3A});
                    Short len = 0x23;
                    byte channel = 0x11;
                    byte protocolVersion = 0x01;
                    byte[] bbody = {
                            0x0C,
                            0x01, 0x02, 0x03, 0x04, 0x05, 0x06,
                            0x01,0x02,
                            0x01,
                            0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,
                            0x01,0x02,0x03,0x04,
                            0x01,0x02,0x03,0x04,
                            0x01,0x02,0x03,0x04,
                            0x01
                    };
                    ByteBuf body = Unpooled.buffer(bbody.length);
                    body.writeBytes(bbody);
                    ByteBuf end = Unpooled.buffer(2);
                    end.writeBytes(new byte[]{0x6F, (byte) 0x6B});
                    CustomMsg customMsg2 = new CustomMsg(flag, len, channel, protocolVersion, body, end);
                    ctx.writeAndFlush(customMsg2);




                }
                break;
                case 0x0D: {
                    StringBuffer sb = new StringBuffer();
                    sb.append("客户端服务器命令0x0D的应答 \r\n ");
                    System.out.println("客户端服务器命令0x0D的应答  ");
                    for (int i = 0; i < 20; i++) {
                        System.out.print(req[i]);
                        sb.append(req[i]+".");
                    }
                    sb.append("...");
                    System.out.print("...");
                    System.out.println("客户端应答服务器0x0D请求并返回数据");
                    sb.append("\r\n客户端应答服务器0x0D请求并返回数据\r\n\r\n");
                    mUICallBack.refreshText(sb.toString());
                    System.out.println();
                    ByteBuf flag = Unpooled.buffer(2);
                    flag.writeBytes(new byte[]{(byte) 0xEF, 0x3A});
                    Short len=0xDC;
                    byte channel = 0x11;
                    byte protocolVersion = 0x01;
                    byte[] byte1 = {
                            0x0D,
                            0x01, 0x02, 0x03, 0x04, 0x05, 0x06};
//                    域名
                    byte[] url = new byte[200];
//                    端口号
                    byte[] byte2 = {
                            0x01, 0x02,
                            0x01, 0x02,
                            0x01, 0x02, 0x03,0x04,
                            0x01
                    };
                    byte[] data = new byte[byte1.length + url.length + byte2.length];
                    System.arraycopy(byte1, 0, data, 0, byte1.length);
                    System.arraycopy(url, 0, data, byte1.length, url.length);
                    System.arraycopy(byte2, 0, data, byte1.length + url.length, byte2.length);
                    ByteBuf body = Unpooled.buffer(data.length);
                    body.writeBytes(data);
                    ByteBuf end = Unpooled.buffer(2);
                    end.writeBytes(new byte[]{0x2D, (byte) 0x6E});
                    CustomMsg customMsg2= new CustomMsg(flag, len, channel, protocolVersion, body, end);
                    ctx.writeAndFlush(customMsg2);





                }
                break;
                case 0x0E:{
                    StringBuffer sb = new StringBuffer();
                    sb.append("客户端收到服务器命令0x0E的应答 \r\n ");
                    System.out.println("客户收到端服务器命令0x0E的应答  ");
                    for (int i = 0; i < req.length; i++) {
                        System.out.print(req[i]);
                        sb.append(req[i]+".");
                    }

                    System.out.println("客户端应答服务器0x0E请求并返回数据");
                    sb.append("\r\n客户端应答服务器0x0E请求并返回数据\r\n\r\n");
                    mUICallBack.refreshText(sb.toString());
                    System.out.println();
//                    应答服务器的请求
                    ByteBuf flag = Unpooled.buffer(2);
                    flag.writeBytes(new byte[]{(byte) 0xEF, 0x3A});
                    Short len = 0x10;
                    byte channel = 0x11;
                    byte protocolVersion = 0x01;
                    byte[] bbody = {
                            0x0E,
                            0x01, 0x02, 0x03, 0x04, 0x05, 0x06,
                            0x01,0x02,0x03,0x04,
                            0x01
                    };
                    ByteBuf body = Unpooled.buffer(bbody.length);
                    body.writeBytes(bbody);
                    ByteBuf end = Unpooled.buffer(2);
                    end.writeBytes(new byte[]{0x6F, (byte) 0x6B});
                    CustomMsg customMsg2 = new CustomMsg(flag, len, channel, protocolVersion, body, end);
                    ctx.writeAndFlush(customMsg2);




                }
                break;
                case 0x0F:{
                    StringBuffer sb = new StringBuffer();
                    sb.append("客户端收到服务器命令0x0F  \r\n");
                    System.out.println("客户收到端服务器命令0x0F  ");
                    for (int i = 0; i < req.length; i++) {
                        System.out.print(req[i]);
                        sb.append(req[i]+".");
                    }
                    System.out.println("客户端自定义消息发送完成");
                    sb.append("\r\n客户端自定义消息发送完成\r\n\r\n");
                    mUICallBack.refreshText(sb.toString());
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
