package orz.xuchao.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.Charset;

/**
 * Created by Administrator on 2017/7/7 0007.
 */
public class CustomEncoder  extends MessageToByteEncoder<CustomMsg> {
    @Override
    protected void encode(ChannelHandlerContext ctx, CustomMsg msg, ByteBuf out) throws Exception {
        if(null == msg){
            throw new Exception("msg is null");
        }

        ByteBuf flag = msg.getFlag();
        int len = msg.getLen();
        byte channel= msg.getChannel();
        byte protocolVersion = msg.getProtocolVersion();
        ByteBuf body = msg.getBody();
        ByteBuf end = msg.getEnd();

        out.writeBytes(flag);
        out.writeInt(len);
        out.writeByte(channel);
        out.writeByte(protocolVersion);
        out.writeBytes(body);
        out.writeBytes(end);

    }
}
