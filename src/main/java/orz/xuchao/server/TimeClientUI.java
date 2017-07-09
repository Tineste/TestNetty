package orz.xuchao.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Administrator on 2017/7/5 0005.
 */
public class TimeClientUI extends JFrame{

    private static TimeClient timeClient;
    private JTextField receivedTF;
    private JButton sendB;
    private JLabel jl1;

    public TimeClientUI() {
        this.setTitle("客户端");
        this.setBounds(0, 0, 355, 265);
        // 窗体大小不能改变
        this.setResizable(true);
        // 居中显示
        this.setLocationRelativeTo(null);
        // 窗体可见
        this.setVisible(true);
        // 创建一个容器
        Container con = this.getContentPane();
        receivedTF = new JTextField();
        receivedTF.setBounds(100, 100, 150, 20);

        // 按钮设定
        sendB = new JButton("发送消息");
        sendB.setBounds(280, 200, 65, 20);

        jl1 = new JLabel();
        jl1.setBounds(0, 0, 355, 265);



        // 给按钮添加1个事件
        sendB.addActionListener(new ActionListener() {


            public void actionPerformed(ActionEvent e) {
                String str = e.getActionCommand();
                if ("发送消息".equals(str)) {
                    System.out.println("你已经点击了按钮");
                    try {
                        //                        System.out.println("通道活动了并发了第2次消息");
//                        byte[]  req=("通道活动了并发了第2次消息").getBytes();
//                        ByteBuf message=Unpooled.buffer(req.length);
//                        message.writeBytes(req);
//                        timeClient.socketChannel.writeAndFlush(message);

                        //        System.out.println("通道活动了并发了第1次消息");



                        ByteBuf flag=Unpooled.buffer(2);
                        flag.writeBytes(new byte[]{(byte)0xEF,0x3A});
                        int len=15;
                        byte channel=0x01;
                        byte protocolVersion=0x01;
                        byte[] bbody={0x01
                                ,0x05,0x01,0x00,0x01,0x02,0x03
                                ,0x59,0x3D,0x34,0x11};
                        ByteBuf body=Unpooled.buffer(bbody.length);
                        body.writeBytes(bbody);
                        ByteBuf end=Unpooled.buffer(2);
                        end.writeBytes(new byte[]{0x51,(byte)0xC7});
                        CustomMsg customMsg = new CustomMsg(flag, len,channel,protocolVersion,body, end);
                        timeClient.socketChannel.writeAndFlush(customMsg);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        jl1.add(sendB);
        con.add(receivedTF);
        con.add(jl1);
    }
    public static void main(String[] args) {
        // 实例化对象
        TimeClientUI qq = new TimeClientUI();
//        启动客户端
        timeClient=new TimeClient();
        try {
            timeClient.connect(8080,"127.0.0.1");


        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
