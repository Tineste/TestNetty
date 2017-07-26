package orz.xuchao.server.test;

import orz.xuchao.server.utils.CRCUtil;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2017/7/10 0010.
 */
public class Test {
    private static int n;
    public static void main(String[] args){
        System.out.println("eee" +
                "");


        Calendar calendar = Calendar.getInstance();
        System.out.println("1=="+calendar.getTimeInMillis());


        calendar.setTimeInMillis(calendar.getTimeInMillis()+120*1000);

        System.out.println("2=="+calendar.getTimeInMillis());

        long curren = System.currentTimeMillis();
        curren += 30 * 60 * 1000;
        Date da = new Date(curren);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(da);
        System.out.println("3=="+calendar2.getTimeInMillis());

    }

}
