package co.casterlabs.tcp_proxy;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import co.casterlabs.commons.io.streams.ForceFlushedOutputStream;
import co.casterlabs.commons.io.streams.StreamUtil;
import xyz.e3ndr.fastloggingframework.logging.FastLogger;

public class ProxyConnection implements Closeable {
    private FastLogger logger;

    private Socket trueSocket;
    private Socket proxySocket;

    private boolean isClosed = false;

    public ProxyConnection(Socket trueSocket, int soTimeout) {
        try {
            this.logger = new FastLogger(String.format("%s (%d)", trueSocket.getRemoteSocketAddress(), this.hashCode()));
            this.logger.debug("Incoming connection!");

            this.trueSocket = trueSocket;
            this.proxySocket = new Socket();

            this.proxySocket.setSoTimeout(soTimeout);
            this.trueSocket.setSoTimeout(soTimeout);

            this.proxySocket.setTcpNoDelay(true);
            this.trueSocket.setTcpNoDelay(true);
            this.proxySocket.setTrafficClass(0x10 | 0x08); // LOWDELAY | THROUGHPUT
            this.trueSocket.setTrafficClass(0x10 | 0x08); // LOWDELAY | THROUGHPUT
        } catch (IOException e) {
            this.logger.fatal("An error occurred whilst initializing the proxy:\n%s", e);
            this.close();
        }
    }

    public void connect(String targetAddress, int targetPort) {
        try {
            this.logger.debug("Connecting to %s:%d", targetAddress, targetPort);
            this.proxySocket.connect(new InetSocketAddress(targetAddress, targetPort), this.proxySocket.getSoTimeout());
            this.logger.debug("Connected! Proxying...");

            int mtu = Math.min(NetworkUtil.guessMtu(this.trueSocket), NetworkUtil.guessMtu(this.proxySocket));

            Thread.ofVirtual().start(() -> {
                try {
                    StreamUtil.streamTransfer(
                        this.proxySocket.getInputStream(),
                        new ForceFlushedOutputStream(this.trueSocket.getOutputStream()),
                        mtu
                    );
                } catch (IOException e) {
                    this.logger.trace(e);
                } finally {
                    this.close();
                }
            });
            Thread.ofVirtual().start(() -> {
                try {
                    StreamUtil.streamTransfer(
                        this.trueSocket.getInputStream(),
                        new ForceFlushedOutputStream(this.proxySocket.getOutputStream()),
                        mtu
                    );
                } catch (IOException e) {
                    this.logger.trace(e);
                } finally {
                    this.close();
                }
            });
        } catch (IOException e) {
            this.logger.fatal("An error occurred whilst connecting the proxy:\n%s", e);
            this.close();
        }
    }

    @Override
    public void close() {
        if (this.isClosed) {
            return;
        }

        this.isClosed = true;
        this.logger.debug("Disconnected.");

        try {
            this.trueSocket.close();
        } catch (IOException e) {
            this.logger.warn("An error occurred whilst closing the trueSocket:\n%s", e);
        }
        try {
            this.proxySocket.close();
        } catch (IOException e) {
            this.logger.warn("An error occurred whilst closing the proxySocket:\n%s", e);
        }

        this.proxySocket = null;
        this.trueSocket = null;
    }

}
