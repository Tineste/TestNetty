package orz.xuchao.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import orz.xuchao.server.utils.CRCUtil;

import java.util.Calendar;

/**
 * Created by Administrator on 2017/7/4 0004.
 */
public class HelloWorld {



    public static void main(String[] args)throws  Exception {

        {


            byte[] bbb={(byte) 0xEF,0x3A
        };

            System.out.println("===>"+CRCUtil.bytesToHexString(CRCUtil.int2Bytes(CRCUtil.MyCrc16Check(bbb))));




//        byte[]转ByteBF
            byte[] b = {0x01, 0x02, 0x03};
            ByteBuf bb = Unpooled.buffer(b.length);
            bb.writeBytes(b);

//        ByteBuf转byte[]
            byte[] b1 = new byte[bb.readableBytes()];
            bb.getBytes(0,b1);

            byte[] b2 = new byte[bb.readableBytes()];
            bb.readBytes(b2);

            System.out.println("-------" + b1[0]);
            System.out.println("-------" + b2[0]);


        }



        System.out.println("----------------时间转byte-------------------");
        {
            Calendar calendar = Calendar.getInstance();
            calendar.clear();
            calendar.set(2017, 5, 11, 20, 14, 10); //重载函数，参考java jdk doc.
            System.out.println(calendar.getTime());
            System.out.println(calendar.getTimeInMillis());
            System.out.println(calendar.getTimeInMillis() / 1000);
            System.out.println(CRCUtil.bytesToHexString(CRCUtil.getTimestampBytes(calendar.getTimeInMillis() / 1000)));
        }
        System.out.println("----------------byte转时间-------------------");
        {

            byte[] b={0x59,0x3D,0x34,0x12};
            System.out.println(CRCUtil.bytesToInt(b));
            System.out.println(((long) CRCUtil.bytesToInt(b))*1000);
            Calendar calendar = Calendar.getInstance();
            calendar.clear();
            calendar.setTimeInMillis(((long) CRCUtil.bytesToInt(b))*1000);
            System.out.println(calendar.getTime());

        }
    }

}
