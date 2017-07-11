package orz.xuchao.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import orz.xuchao.server.bean.CustomMsg;
import orz.xuchao.server.code.CRCCheck;

/**
 * Created by Administrator on 2017/7/4 0004.
 */
public class HelloWorld {
    public static void main(String[] args)throws  Exception {
//        byte[] a={(byte)0xEF,0x3A,
//                0x00,0x0F
//                ,0x01
//                ,0x01
//                ,0x01
//                ,0x05,0x01,0x00,0x01,0x02,0x03
//                ,0x59,0x3D,0x34,0x11
//                ,0x51,(byte)0xC7
//        };

        byte[] a={
                (byte)0xEF,0x3A,
                0x00,0x18,
                0x11,
                0x01,
                0x03,
                0x05,0x01,0x00,0x01,0x02,0x03,
                0x23,(byte) 0xC9,0x3B,(byte)0x89,0x4A,(byte)0xED,(byte)0xF0,0x65,
                0x59,0x3D,0x34,0x11,
                0x01,
                0x6F,0x6B
        };

//        CRCCheck.crc(a);

        byte[] byte1={(byte)0xEF,0x3A,(byte)0xDA,0x11,0x01,0x01,0x05,0x01,0x00,0x01,0x02,0x03};
//                    域名
        byte[] url=new byte[5];
//                    端口号
        byte[] byte2={0x50,0x50,0x59,0x3D,0x34,0x11,0x01,0x01,0x02};

        byte[] data = new byte[byte1.length+url.length+byte2.length];
        System.arraycopy(byte1,0,data,0,byte1.length);
        System.arraycopy(url,0,data,byte1.length,url.length);
        System.arraycopy(byte2,0,data,byte1.length+url.length,byte2.length);
        for (int i=0;i<data.length;i++){
            System.out.println(data[i]);
        }




    }
}
