package co.casterlabs.tcp_proxy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

import xyz.e3ndr.fastloggingframework.FastLoggingFramework;
import xyz.e3ndr.fastloggingframework.logging.FastLogger;

public class Main {
    private static final FastLogger LOGGER = new FastLogger("TCP-Proxy");

    public static void main(String[] args) throws Exception {
        System.setProperty("fastloggingframework.wrapsystem", "true");
        FastLoggingFramework.setColorEnabled(false);

        int bindPort = Integer.parseInt(System.getProperty("tp.bindport", "8080"));
        int soTimeout = Integer.parseInt(System.getProperty("tp.sotimeout", "30000"));
        String targetAddress = System.getProperty("tp.targetaddr", "example.com");
        int targetPort = Integer.parseInt(System.getProperty("tp.targetport", "80"));

        try (
            ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress("::", bindPort));

            LOGGER.info("Listening on [::]:%d", bindPort);

            while (serverSocket.isBound()) {
                @SuppressWarnings("resource")
                ProxyConnection proxy = new ProxyConnection(serverSocket.accept(), soTimeout);
                proxy.connect(targetAddress, targetPort);
            }
        } catch (IOException e) {
            LOGGER.severe("Unable to handle connections:\n%s", e);
        }
    }

}
