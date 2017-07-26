package orz.xuchao.server.test;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.socket.SocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import orz.xuchao.server.utils.CRCUtil;

import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2017/7/4 0004.
 */
public class HelloWorld {
    private static final Logger logger = LogManager.getLogger(HelloWorld.class.getName());

    private void logout(){
        logger.debug("输出debug级别的日志.....");
        logger.info("输出info级别的日志.....");
        logger.error("输出error级别的日志.....");

    }


    public static void main(String[] args)throws  Exception {


         Map<String, String> map = new ConcurrentHashMap();
        String a="dadfadsf";

        map.put("aa",a);
        map.put("aa",a);







        short n=8985;
        byte[] b11=CRCUtil.short2Bytes(n);
        short n1=CRCUtil.bytesToShort(b11);

        System.out.println();




        byte[] serverMac={0x06,0x05,0x04,0x03,0x02,0x01};
        System.out.println(CRCUtil.bytesToHexString(serverMac));
        byte[] serverMac2=CRCUtil.bytesToHexString(serverMac).getBytes("utf-8");




        Calendar calendar = Calendar.getInstance();
        calendar.set(2020,2,22);
        byte[] time1= CRCUtil.timeToBytes(calendar);
        Calendar calendar2= Calendar.getInstance();
        byte[] time2= CRCUtil.timeToBytes(calendar2);

        System.out.println(CRCUtil.bytesToTime(time1));
        System.out.println(CRCUtil.bytesToTime(time2));




//        HelloWorld mHelloWorld=new HelloWorld();
//        mHelloWorld.logout();

//        byte[] orlder={0x02};
//        byte[] result={0x01};
//        byte[] blueSecret={0x01, 0x01, 0x02, 0x02, 0x03, 0x03};
//        ByteBuf body=Unpooled.copiedBuffer(orlder,mac,time,blueSecret,result);

        String s="{\n" +
                "\"a\": \"哈哈\", \n" +
                "\"b\": \"阿达是广大\"}";

//        System.out.println(s);
        byte[] b=s.getBytes("utf-8");
        byte[] b1={0x00,0x00,0x00};

        byte[] b2=new byte[b.length+b1.length];

        System.arraycopy(b, 0, b2, 0, b.length);
        System.arraycopy(b1, 0, b2, b.length, b1.length);
        String str=new String(b2);
        System.out.println(">"+str);


//        System.out.println(CRCUtil.bytesToHexString(b));

        String s2 = new String(b);//bytep[]转换为String
//        System.out.println(s2);





//        MyChannelTemp myChannel1=new MyChannelTemp();
//        myChannel1.setId("01");
//        myChannel1.setMac("11111");
//        myChannel1.setChannel("AAAAAAA");
//
//        MyChannelTemp myChannel2=new MyChannelTemp();
//        myChannel2.setId("02");
//        myChannel2.setMac("22222");
//        myChannel2.setChannel("BBBBB");
//
//        MyChannelTemp myChannel3=new MyChannelTemp();
//        myChannel3.setId("03");
//        myChannel3.setMac("33333");
//        myChannel3.setChannel("CCCCC");
//        ArrayList<MyChannelTemp> arrayList=new ArrayList<MyChannelTemp>();
//        arrayList.add(myChannel1);
//        arrayList.add(myChannel2);
//        arrayList.add(myChannel3);
//
//        for (MyChannelTemp m:arrayList) {
//            if (m.getId().equals("01"))
//                m.setMac("1");
//        }
//        System.out.println(arrayList.get(0).getMac());
//        System.out.println(arrayList.get(1).getMac());
//        System.out.println(arrayList.get(2).getMac());
//
//
//        for (MyChannelTemp m:arrayList) {
//            if (m.getId().equals("02"))
//                arrayList.remove(m);
//        }
//        for (MyChannelTemp m:arrayList) {
//            System.out.println(m.getId());
//        }

//        {
//            byte[] bbb={
//                    (byte) 0xEF,0x3A,0x00, (byte) 0xDA,0x11,0x01,0x01,0x05,0x01,0x00,
//                    0x01,0x02,0x03,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
//                    0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
//                    0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
//                    0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
//                    0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
//                    0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
//                    0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
//                    0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
//                    0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
//                    0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
//                    0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
//                    0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
//                    0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
//                    0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
//                    0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
//                    0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
//                    0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
//                    0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
//                    0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
//                    0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
//                    0x00,0x00,0x00,0x50,0x50,0x59,0x3D,0x34,0x11,0x01};
//            System.out.println("===>"+CRCUtil.bytesToHexString(CRCUtil.int2Bytes(CRCUtil.MyCrc16Check(bbb))));
////        byte[]转ByteBF
//            byte[] b = {0x01, 0x02, 0x03};
//            ByteBuf bb = Unpooled.buffer(b.length);
//            bb.writeBytes(b);
////        ByteBuf转byte[]
//            byte[] b1 = new byte[bb.readableBytes()];
//            bb.getBytes(0,b1);
//
//            byte[] b2 = new byte[bb.readableBytes()];
//            bb.readBytes(b2);
//
//            System.out.println("-------" + b1[0]);
//            System.out.println("-------" + b2[0]);
//        }
//        System.out.println("----------------时间转byte-------------------");
//        {
//            Calendar calendar = Calendar.getInstance();
//            calendar.clear();
//            calendar.set(2017, 5, 11, 20, 14, 10); //重载函数，参考java jdk doc.
//            System.out.println(calendar.getTime());
//            System.out.println(calendar.getTimeInMillis());
//            System.out.println(calendar.getTimeInMillis() / 1000);
//            System.out.println(CRCUtil.bytesToHexString(CRCUtil.getTimestampBytes(calendar.getTimeInMillis() / 1000)));
//        }
//        System.out.println("----------------byte转时间-------------------");
//        {
//
//            byte[] b={0x59,0x3D,0x34,0x12};
//            System.out.println(CRCUtil.bytesToInt(b));
//            System.out.println(((long) CRCUtil.bytesToInt(b))*1000);
//            Calendar calendar = Calendar.getInstance();
//            calendar.clear();
//            calendar.setTimeInMillis(((long) CRCUtil.bytesToInt(b))*1000);
//            System.out.println(calendar.getTime());
//
//        }
    }

}
