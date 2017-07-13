package orz.xuchao.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import orz.xuchao.server.bean.BasePackage;
import orz.xuchao.server.bean.CustomMsg;
import orz.xuchao.server.code.CRCCheck;
import orz.xuchao.server.utils.CRCUtil;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Administrator on 2017/7/4 0004.
 */
public class HelloWorld {



    public static void main(String[] args)throws  Exception {
        {

            BasePackage mBasePackage=new BasePackage();
            ByteBuf flag=Unpooled.buffer(2);
            flag.writeBytes(new byte[]{(byte)0xEF,0x3A});
            mBasePackage.setFlag(flag);
            mBasePackage.setChannel((byte) 0x01);
            mBasePackage.setProtocolVersion((byte) 0x01);
            byte[] bbody={0x01
                    ,0x05,0x01,0x00,0x01,0x02,0x03
                    ,0x59,0x3D,0x34,0x11};
            ByteBuf body=Unpooled.buffer(bbody.length);
            body.writeBytes(bbody);
            mBasePackage.setBody(body);
            CustomMsg c=mBasePackage.getCustomMsg();
            System.out.println(c.getEnd().array()[0]+"   "+c.getEnd().array()[1]);

        }

//            byte[] num=CRCUtil.short2Bytes((short) 15);
//
//
//            byte[] b = {
//                    (byte) 0xEF, 0x3A,
//                    0x00, 0x0F,
//                    0x01,
//                    0x01,
//                    0x03,
//                    0x05, 0x01, 0x00, 0x01, 0x02, 0x03,
//                    0x59, 0x3D, 0x34, 0x11
//
//            };
//            int n=MyCrc16Check(b);
//            System.out.println(n);
//            byte[] check=CRCUtil.int2Bytes(n);
//            byte[] check2 = new byte[2];
//            System.arraycopy(check, 2, check2, 0, 2);
//            System.out.println("CRC==>"+CRCUtil.bytesToHexString(check2));
//
//        }
//        System.out.println("-----------------------------------");
//        {
//            byte[] b = {0x01
//                    ,0x05,0x01,0x00,0x01,0x02,0x03
//                    ,0x59,0x3D,0x34,0x11};
//
//
//        int n=MyCrc16Check(b);
//            System.out.println(n);
//            byte[] check=CRCUtil.int2Bytes(n);
//            byte[] check2 = new byte[2];
//            System.arraycopy(check, 2, check2, 0, 2);
//            System.out.println("CRC==>"+CRCUtil.bytesToHexString(check2));
//
//
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

    public static byte[] crc16Check(byte[] data) {
        int high;
        int flag;

        // 16位寄存器，所有数位均为1
        int wcrc = 0xffff;
        for (int i = 0; i < data.length; i++) {
            // 16 位寄存器的高位字节
            high = wcrc >> 8;
            // 取被校验串的一个字节与 16 位寄存器的高位字节进行“异或”运算
            wcrc = high ^ data[i];

            for (int j = 0; j < 8; j++) {
                flag = wcrc & 0x0001;
                // 把这个 16 寄存器向右移一位
                wcrc = wcrc >> 1;
                // 若向右(标记位)移出的数位是 1,则生成多项式 1010 0000 0000 0001 和这个寄存器进行“异或”运算
                if (flag == 1)
                    wcrc ^= 0x4821;
            }
        }
        return CRCUtil.short2Bytes((short) wcrc);
    }


    public static void longToByte(byte[] bb, long x) {
        bb[ 0] = (byte) (x >> 56);
        bb[ 1] = (byte) (x >> 48);
        bb[ 2] = (byte) (x >> 40);
        bb[ 3] = (byte) (x >> 32);
        bb[ 4] = (byte) (x >> 24);
        bb[ 5] = (byte) (x >> 16);
        bb[ 6] = (byte) (x >> 8);
        bb[ 7] = (byte) (x >> 0);
    }

    public static long byteToLong(byte[] bb) {
        return ((((long) bb[ 0] & 0xff) << 56)
                | (((long) bb[ 1] & 0xff) << 48)
                | (((long) bb[ 2] & 0xff) << 40)
                | (((long) bb[ 3] & 0xff) << 32)
                | (((long) bb[ 4] & 0xff) << 24)
                | (((long) bb[ 5] & 0xff) << 16)
                | (((long) bb[ 6] & 0xff) << 8) | (((long) bb[ 7] & 0xff) << 0));
    }
    public static int A_VALUE=0xFFFF;
    public static int DIV_VALUE=0x4821;
//    public static int MyCrc16Check(int MyChar[]){
//        int I,J;
//        int Crc_Value=A_VALUE;
//        int My_Check;
//        for(I=0;I<MyChar.length;I++){
//            Crc_Value=Crc_Value^MyChar[I];
//            for (J=0;J<8;J++){
//                My_Check=Crc_Value&1;
//                Crc_Value=Crc_Value>>>1;
//                if(My_Check==1){
//                    Crc_Value=Crc_Value^DIV_VALUE;
//                }
//            }
//        }
//        return  Crc_Value;
//    }
    public static int MyCrc16Check(byte[] data){
        int i, j;
        int Crc_Value = A_VALUE;
        int My_Check;
        for (i = 0; i < data.length; i++) {
            //System.out.println(String.format("%x", data[i]));
            // System.out.println(Integer.toBinaryString(data[i] & 0xFF));
            Crc_Value = Crc_Value ^ (data[i] & 0xFF);
            for (j = 0; j < 8; j++) {
                My_Check = Crc_Value & 0x0001;
                Crc_Value = Crc_Value >>> 1;
                if (My_Check == 1) {
                    Crc_Value = Crc_Value ^ 0x4821;
                }
            }
        }
        return Crc_Value;
    }
}
