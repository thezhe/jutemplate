package localhost.suppliers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.function.Supplier;

public final class Udp implements Supplier<byte[]>, AutoCloseable {

    /**
     * UDP socket.
     */
    private final DatagramSocket socket; // TODO

    /**
     * @param port
     * @param host
     * @throws SocketException
     * @throws UnknownHostException
     */
    public Udp(final int port, final String host)
            throws SocketException, UnknownHostException { // TODO nullcheck
        this.socket = new DatagramSocket(port, InetAddress.getByName(host));
    }

    @Override
    public byte[] get() {
        try {
            socket.receive(new DatagramPacket(new byte[1], 0)); // TODO
            return new byte[0];
        } catch (IOException e) {
            return new byte[0];
        }
    }

    @Override
    public void close() {
        socket.close();
    }
}
