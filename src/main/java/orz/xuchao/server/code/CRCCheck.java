package orz.xuchao.server.code;

/**
 * Created by Administrator on 2017/7/10 0010.
 */
public class CRCCheck {
    public static int[] crc(int[] data) {
        int[] temdata = new int[data.length + 2];
        // unsigned char alen = *aStr – 2; //CRC16只计算前两部分
        int xda, xdapoly;
        int i, j, xdabit;
        xda = 0xFFFF;
        xdapoly = 0xA001; // (X**16 + X**15 + X**2 + 1)
        for (i = 0; i < data.length; i++) {
            xda ^= data[i];
            for (j = 0; j < 8; j++) {
                xdabit = (int) (xda & 0x01);
                xda >>= 1;
                if (xdabit == 1)
                    xda ^= xdapoly;
            }
        }
        System.arraycopy(data, 0, temdata, 0, data.length);
        temdata[temdata.length - 2] = (int) (xda & 0xFF);
        temdata[temdata.length - 1] = (int) (xda >> 8);
        return temdata;
    }
}
