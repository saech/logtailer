package gd.sender;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class TcpSender implements AutoCloseable {
    private final Socket socket;
    private final DataOutputStream dos;
    private final DataInputStream dis;

    public TcpSender(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        socket.setSoTimeout(1);
        dos = new DataOutputStream(socket.getOutputStream());
        dis = new DataInputStream(socket.getInputStream());
    }

    public void open(long offset) throws IOException {
        dos.writeLong(offset);
    }

    public void write(byte[] bytes, int num) throws IOException {
        dos.write(bytes, 0, num);
    }

    public long read() throws IOException {
        try {
            return dis.readLong();
        } catch (SocketTimeoutException eof) {
            return -1;
        }
    }

    public void close() {
        IOUtils.closeQuietly(socket);
    }
}
