package orz.xuchao.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.socket.SocketChannel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Administrator on 2017/7/5 0005.
 */
public class TimeServerUI extends JFrame{

    private static  TimeServer mTimeServer= new TimeServer();
    private JButton sendB;
    private JLabel jl1;

    public TimeServerUI() {
        this.setTitle("服务器端");
        this.setBounds(0, 0, 355, 265);
        // 窗体大小不能改变
        this.setResizable(true);
        // 居中显示
        this.setLocationRelativeTo(null);
        // 窗体可见
        this.setVisible(true);
        // 创建一个容器
        Container con = this.getContentPane();

        // 按钮设定
        sendB = new JButton("从智能门禁读取一条UID指令");
        sendB.setBounds(10, 10, 300, 20);

        jl1 = new JLabel();
        jl1.setBounds(0, 0, 355, 265);


        // 给按钮添加1个事件
        sendB.addActionListener(new ActionListener() {


            public void actionPerformed(ActionEvent e) {
                String str = e.getActionCommand();

                    System.out.println("你已经点击了按钮");
                    try {

                        Map<String, SocketChannel> map = GatewayService.getChannels();

                        System.out.println("-------------目前有"+map.size()+"个客户端连进来了--------------");

                        Iterator<String> it = map.keySet().iterator();
                        while (it.hasNext()) {
                            String key = it.next();
                            SocketChannel obj = map.get(key);
                            System.out.println("channel id is: " + key);
                            System.out.println("channel: " + obj.isActive());

                            System.out.println("从智能门禁读取一条 UID 指令");
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
                            obj.writeAndFlush(customMsg);



                        }



                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }

            }
        });
        jl1.add(sendB);
        con.add(jl1);

    }
    public static void main(String[] args) {
        // 实例化对象
        TimeServerUI qq = new TimeServerUI();
//        启动客户端
        try {
            mTimeServer.bind(8080);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
