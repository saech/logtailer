package gd.mocks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ServerMock {
    private static final Logger log = LogManager.getLogger(ServerMock.class);

    public static void main(String[] args) {
        new Server(ConfigMock.getPort()).run();
    }

    private static class Server extends Thread {
        private final int port;

        Server(int port) {
            this.port = port;
        }

        @Override
        public void run() {
            try (ServerSocket ss = new ServerSocket(port)) {
                log.info("listening to: {}", port);
                while (true) {
                    Socket s = ss.accept();
                    InputStream is = s.getInputStream();
                    DataInputStream dis = new DataInputStream(is);
                    long globalOffset;
                    while (true) {
                        try {
                            globalOffset = dis.readLong();
                            log.info("global offset: {}", globalOffset);
                            break;
                        } catch (EOFException eof) {
                            //partial read occurred
                        }
                        try {
                            log.info("nothing read from socket");
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            throw new IllegalStateException("unexpected interruption");
                        }
                    }

                    OutputStream os = s.getOutputStream();
                    DataOutputStream dos = new DataOutputStream(os);
                    byte[] bytes = new byte[2048];
                    while (true) {
                        if (!s.isConnected()) {
                            break;
                        }
                        int read = is.read(bytes, 0, bytes.length);
                        if (read != -1) {
                            log.info("incoming data: {}", new String(bytes, 0, read, StandardCharsets.UTF_8));
                            globalOffset += read;
                            dos.writeLong(globalOffset);
                            log.info("acked globalOffset: {}", globalOffset);
                        }
                    }
                }
            } catch (IOException e) {
                log.error("ioexception occurred", e);
            }
        }
    }
}
