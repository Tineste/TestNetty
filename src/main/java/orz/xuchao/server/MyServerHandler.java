package orz.xuchao.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;

/**
 * Created by Administrator on 2017/7/5 0005.
 */

//服务器的消息通道
public class MyServerHandler  extends ChannelInboundHandlerAdapter {


    @Override
    public void channelActive(final ChannelHandlerContext ctx) { // (1)
        final ByteBuf time = ctx.alloc().buffer(4); // (2)
        time.writeInt((int) (System.currentTimeMillis() / 1000L + 2208988800L));
        for (int i = 0; i < time.capacity(); i ++) {
            byte b = time.getByte(i);
            System.out.println("服务器端收到的数据===>"+(char) b);
        }


        final ChannelFuture f = ctx.writeAndFlush(time); // (3)
        f.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                assert f == channelFuture;
                ctx.close();
            }
        }); // (4)
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
