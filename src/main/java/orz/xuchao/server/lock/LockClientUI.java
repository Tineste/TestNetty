package orz.xuchao.server.lock;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import orz.xuchao.server.bean.BasePackage;
import orz.xuchao.server.bean.CustomMsg;
import orz.xuchao.server.uicallback.ChanageUserverCallBack;
import orz.xuchao.server.uicallback.UICallBack;
import orz.xuchao.server.utils.CRCUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Calendar;
import java.util.Properties;

/**
 * Created by Administrator on 2017/7/5 0005.
 */
public class LockClientUI extends JFrame{
    //    private final JButton sendB0;
    private  JButton sendB5;
    private  JButton sendB6;
    public LockTimeClient timeClient;
    private JTextArea receivedTF;
    private JButton sendB1,sendB2,sendB3,sendB4;
    private JLabel jl1;

//    private static String url="183.131.66.171";
    private static String url="127.0.0.1";
    private static int port=8984;



//    private JTextField tv;
    UICallBack mUICallBack;


    ChanageUserverCallBack mChanageUserverCallBack;
    public LockClientUI() {
        this.setTitle("客户端");
        this.setBounds(0, 0, 600, 800);
        // 窗体大小不能改变
        this.setResizable(true);
        // 居中显示
        this.setLocationRelativeTo(null);
        // 窗体可见
        this.setVisible(true);
        // 创建一个容器
        Container con = this.getContentPane();
//        tv=new JTextField();
//        tv.setBounds(50,10,250,20);
//        // 按钮设定
//        sendB0 = new JButton("发送自定义消息");
//        sendB0.setBounds(310, 10, 200, 20);
//        sendB0.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                System.out.println("发送自定义消息");
////                建立通道后就开始持续向服务器发送心跳包
//                ByteBuf flag = Unpooled.buffer(2);
//                flag.writeBytes(new byte[]{(byte) 0xEF, 0x3A});
//                byte channel = 0x11;
//                byte protocolVersion = 0x01;
//                byte[] order={0x0F};
//                short len;
//                try {
////                    bbody = tv.getText().getBytes("UTF-8");
//                    byte[] bbody = {0x01,0x02};
//                    byte[] data = new byte[order.length + bbody.length ];
//                    byte[] time={0x01,0x02,0x03,0x04};
//                    System.arraycopy(order, 0, data, 0, order.length);
//                    System.arraycopy(bbody, 0, data, order.length, bbody.length);
//                    byte[] data1 = new byte[data.length + time.length ];
//                    System.arraycopy(data, 0, data1, 0, data.length);
//                    System.arraycopy(time, 0, data1, data.length, time.length);
//                    int num=(3+data1.length+4+2);
//                     len = (short)num;
//                    ByteBuf body = Unpooled.buffer(data1.length);
//                    body.writeBytes(data1);
//                    ByteBuf end = Unpooled.buffer(2);
//                    end.writeBytes(new byte[]{0x6F, (byte) 0x6B});
//                    CustomMsg customMsg2 = new CustomMsg(flag, len, channel, protocolVersion, body, end);
//                    timeClient.socketChannel.writeAndFlush(customMsg2);
//                } catch (Exception e1) {
//                    e1.printStackTrace();
//                }
//            }
//        });




        receivedTF= new JTextArea();
        //在文本框上添加滚动条
        JScrollPane jsp = new JScrollPane(receivedTF);
        //设置矩形大小.参数依次为(矩形左上角横坐标x,矩形左上角纵坐标y，矩形长度，矩形宽度)
        jsp.setBounds(50, 220, 500, 500);
        //默认的设置是超过文本框才会显示滚动条，以下设置让滚动条一直显示
        jsp.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        // 按钮设定
        sendB1 = new JButton("从中心服务器获取具体要链接的前置服务器域名或ip 0x01");
        sendB1.setBounds(50, 40, 500, 20);
        sendB1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
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
                byte[] mac ={0x08,0x06, 0x00,0x00, 0x00,0x01};
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
                timeClient.socketChannel.writeAndFlush(customMsg);

            }
        });
        // 按钮设定
        sendB2 = new JButton("智能门禁向服务器申报MAC码并获取蓝牙密钥 0x02");
        sendB2.setBounds(50, 70, 500, 20);
        sendB2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
//                BasePackage mBasePackage=new BasePackage();
//                ByteBuf flag=Unpooled.buffer(2);
//                flag.writeBytes(new byte[]{(byte)0xEF,0x3A});
//                mBasePackage.setFlag(flag);
//                mBasePackage.setChannel((byte) 0x01);
//                mBasePackage.setProtocolVersion((byte) 0x01);
//                byte[] bbody={0x02
//                        ,0x01,0x02,0x03,0x04,0x05,0x06
//                        ,0x59,0x3D,0x34,0x11};
//                ByteBuf body=Unpooled.buffer(bbody.length);
//                body.writeBytes(bbody);
//                mBasePackage.setBody(body);
//                CustomMsg customMsg=mBasePackage.getCustomMsg();
//                byte[] ee=new byte[2];
//                customMsg.getEnd().getBytes(0,ee);
//                System.out.println("客户端发出的包，包尾是--->"+ CRCUtil.bytesToHexString(ee));
//                timeClient.socketChannel.writeAndFlush(customMsg);


            }
        });

        sendB3 = new JButton("开门信息上报到服务器指令 0x07");
        sendB3.setBounds(50, 100, 500, 20);
        sendB3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                BasePackage mBasePackage=new BasePackage();
                ByteBuf flag=Unpooled.buffer(2);
                flag.writeBytes(new byte[]{(byte)0xEF,0x3A});
                mBasePackage.setFlag(flag);
                mBasePackage.setChannel((byte) 0x01);
                mBasePackage.setProtocolVersion((byte) 0x01);



                byte[] orlder={0x07};
                byte[] mac=LockConfig.mac;
                byte[] id={0x04,0x04,0x03,0x03,0x02,0x02,0x01,0x01};
                byte[] type={0x01};
                Calendar calendar1 = Calendar.getInstance();
                byte[] time= CRCUtil.timeToBytes(calendar1);
                ByteBuf body=Unpooled.copiedBuffer(orlder,mac,id,type,time);

                mBasePackage.setBody(body);
                CustomMsg customMsg=mBasePackage.getCustomMsg();
                byte[] ee=new byte[2];
                customMsg.getEnd().getBytes(0,ee);
                System.out.println("客户端发出的包，包尾是--->"+ CRCUtil.bytesToHexString(ee));
                timeClient.socketChannel.writeAndFlush(customMsg);
            }
        });



        sendB4 = new JButton("时间校准请求 0x0A");
        sendB4.setBounds(50, 130, 500, 20);
        sendB4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("时间校准指令上报到服务器");

                BasePackage mBasePackage=new BasePackage();
                ByteBuf flag=Unpooled.buffer(2);
                flag.writeBytes(new byte[]{(byte)0xEF,0x3A});
                mBasePackage.setFlag(flag);
                mBasePackage.setChannel((byte) 0x01);
                mBasePackage.setProtocolVersion((byte) 0x01);
                byte[] bbody={
                        0x0A,
                        0x01, 0x02, 0x03, 0x04, 0x05, 0x06,
                        0x01, 0x02, 0x03, 0x04
                };
                ByteBuf body=Unpooled.buffer(bbody.length);
                body.writeBytes(bbody);
                mBasePackage.setBody(body);
                CustomMsg customMsg=mBasePackage.getCustomMsg();
                byte[] ee=new byte[2];
                customMsg.getEnd().getBytes(0,ee);
                System.out.println("客户端发出的包，包尾是--->"+ CRCUtil.bytesToHexString(ee));
                timeClient.socketChannel.writeAndFlush(customMsg);



            }
        });

        sendB5 = new JButton("手动心跳包指令上报到服务器 0x08");
        sendB5.setBounds(50, 160, 500, 20);
        sendB5.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {


                System.out.println("手动心跳包指令上报到服务器");
                BasePackage mBasePackage=new BasePackage();
                ByteBuf flag=Unpooled.buffer(2);
                flag.writeBytes(new byte[]{(byte)0xEF,0x3A});
                mBasePackage.setFlag(flag);
                mBasePackage.setChannel((byte) 0x01);
                mBasePackage.setProtocolVersion((byte) 0x01);
                byte[] bbody={
                        0x08,
                        0x01, 0x02, 0x03, 0x04, 0x05, 0x06,
                        0x01,
                        0x01, 0x02, 0x03, 0x04
                };
                ByteBuf body=Unpooled.buffer(bbody.length);
                body.writeBytes(bbody);
                mBasePackage.setBody(body);
                CustomMsg customMsg=mBasePackage.getCustomMsg();
                byte[] ee=new byte[2];
                customMsg.getEnd().getBytes(0,ee);
                System.out.println("客户端发出的包，包尾是--->"+ CRCUtil.bytesToHexString(ee));
                timeClient.socketChannel.writeAndFlush(customMsg);



            }
        });

        sendB6 = new JButton("报警信息上报到服务器指令 0x0B");
        sendB6.setBounds(50, 190, 500, 20);
        sendB6.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {



                System.out.println("报警信息上报到服务器指令");
                BasePackage mBasePackage=new BasePackage();
                ByteBuf flag=Unpooled.buffer(2);
                flag.writeBytes(new byte[]{(byte)0xEF,0x3A});
                mBasePackage.setFlag(flag);
                mBasePackage.setChannel((byte) 0x01);
                mBasePackage.setProtocolVersion((byte) 0x01);
                byte[] bbody={
                        0x0B,
                        0x01, 0x02, 0x03, 0x04, 0x05, 0x06,
                        0x01,
                        0x01, 0x02, 0x03, 0x04,
                };
                ByteBuf body=Unpooled.buffer(bbody.length);
                body.writeBytes(bbody);
                mBasePackage.setBody(body);
                CustomMsg customMsg=mBasePackage.getCustomMsg();
                byte[] ee=new byte[2];
                customMsg.getEnd().getBytes(0,ee);
                System.out.println("客户端发出的包，包尾是--->"+ CRCUtil.bytesToHexString(ee));
                timeClient.socketChannel.writeAndFlush(customMsg);



            }
        });


        
        jl1 = new JLabel();
        jl1.setBounds(0, 0, 355, 265);

//        jl1.add(sendB1);
//        jl1.add(tv);
//        jl1.add(sendB0);
        jl1.add(sendB2);
        jl1.add(sendB3);
        jl1.add(sendB4);
        jl1.add(sendB5);
        jl1.add(sendB6);
        jl1.add(jsp);



        con.add(jl1);

        mUICallBack= new UICallBack() {

            public void refreshText(String s) {
                receivedTF.setText(receivedTF.getText()+s);
                receivedTF.selectAll();
            }
        };


        mChanageUserverCallBack= new ChanageUserverCallBack() {


            public void chanageServer(String url, int port) {
                try {
                    System.out.println("准备重新连接新服务器---"+url+":"+port);
                    timeClient.reConnect(port,url);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };


        //        启动客户端
         timeClient=new LockTimeClient(mUICallBack,mChanageUserverCallBack);
        try {
            timeClient.connect(port,url);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    public static void main(String[] args) {

        try{
//            Properties prop1 = new Properties();
//            ///保存属性到b.properties文件
//            FileOutputStream oFile = new FileOutputStream("c.properties", true);//true表示追加打开
//            prop1.setProperty("port", "8981");
//            prop1.setProperty("url", "127.0.0.1");
//            prop1.store(oFile, "The New properties file");
//            oFile.close();

//            Properties prop2 = new Properties();
//            //读取属性文件a.properties
//            InputStream in = new BufferedInputStream(new FileInputStream("c.properties"));
//            prop2.load(in);     ///加载属性列表
//
//            if(null!=prop2.getProperty("port")){
//                port = Integer.valueOf(prop2.getProperty("port"));
//                url=prop2.getProperty("url");
//
//            }



        } catch (Exception e) {
            e.printStackTrace();
        }






        // 实例化对象
        LockClientUI qq = new LockClientUI();





    }
}
