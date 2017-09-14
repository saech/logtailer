package sender;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.Socket;

public class TcpSender implements AutoCloseable {
    private final String host;
    private final int port;

    private Socket socket;

    public TcpSender(String host, int port) throws IOException {
        this.host = host;
        this.port = port;

        socket = new Socket(host, port);
    }

    public void open(long offset) throws IOException {
        OutputStream os = socket.getOutputStream();
        DataOutputStream dos = new DataOutputStream(os);
        dos.writeLong(offset);
    }

    public void write(byte[] bytes, int num) throws IOException {
        OutputStream os = socket.getOutputStream();
        os.write(bytes, 0, num);
    }

    public long read() throws IOException {
        InputStream is = socket.getInputStream();
        DataInputStream dis = new DataInputStream(is);
        try {
            return dis.readLong();
        } catch (EOFException eof) {
            //partial read occurred
            return -1;
        }
    }

    public void close() {
        IOUtils.closeQuietly(socket);
    }
}
