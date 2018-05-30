package sample;


import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

class CustomProxySelector extends ProxySelector {
    @Override
    public List<Proxy> select(URI uri) {
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("localhost",8111));
        ArrayList list = new ArrayList();
        list.add(proxy);
        return list;
    }

    @Override
    public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
        System.out.println("Connection to: "+uri+ "failed");
    }
}
