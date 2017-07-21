package orz.xuchao.server.channelmanager;

import io.netty.channel.socket.SocketChannel;

/**
 * Created by Administrator on 2017/7/19 0019.
 */
public class MyChannelTemp {
    private String id;
    private String mac;
    private String channel;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
