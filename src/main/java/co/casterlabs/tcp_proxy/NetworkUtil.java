package co.casterlabs.tcp_proxy;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.Socket;

public class NetworkUtil {

    public static int guessMtu(Socket clientSocket) {
        InetAddress address = clientSocket.getInetAddress();

        if (address.isLoopbackAddress()) {
            // In practice, loopback MTU is usually ~2^64. Because we use this MTU value to
            // determine our buffer size, we want to keep it small to avoid memory issues.
            return 8192; // Arbitrary.
        }

        if (address instanceof Inet6Address) {
            /*
             * ipv6 min. MTU = 1280
             * ipv6 header size = 40
             */
            return 1280 - 40;
        } else {
            /*
             * ipv4 min. MTU = 576 (though usually around 1500)
             * ipv4 header size = 20-60
             */
            return 1500 - 60;
        }
    }

}
