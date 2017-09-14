package sender;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.Socket;

public class TcpSender implements AutoCloseable {
    private final String host;
    private final int port;

    private Socket socket;

    public TcpSender(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public boolean open(long offset) {
        IOUtils.closeQuietly(socket);
        try {
            socket = new Socket(host, port);
            try (OutputStream os = socket.getOutputStream()) {
                try (DataOutputStream dos = new DataOutputStream(os)) {
                    dos.writeLong(offset);
                }
            }
            return true;
        } catch (IOException e) {
            IOUtils.closeQuietly(socket);
            return false;
        }
    }

    public void write(byte[] bytes, int num) throws IOException {
        try (OutputStream os = socket.getOutputStream()) {
            os.write(bytes, 0, num);
        }
    }

    public long read() throws IOException {
        try (InputStream is = socket.getInputStream()) {
            try (DataInputStream dis = new DataInputStream(is)) {
                return dis.readLong();
            }
        } catch (EOFException eof) {
            //partial read occurred
            return -1;
        }
    }

    public void close() {
        IOUtils.closeQuietly(socket);
    }
}
