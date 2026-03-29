package localhost.suppliers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.function.Supplier;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public final class Udp implements Supplier<ByteBuffer>, AutoCloseable {

    /**
     * UDP socket.
     */
    private final DatagramSocket socket; // TODO
    /**
     * UDP buffer.
     */
    private final byte[] buffer = new byte[Short.MAX_VALUE * 2];

    /**
     * @param port
     * @param host
     * @throws SocketException
     * @throws UnknownHostException
     * @apiNote Not thread-safe
     */
    public Udp(final int port, final String host)
            throws SocketException, UnknownHostException { // TODO nullcheck
        this.socket = new DatagramSocket(port,
                InetAddress.getByName(host));
    }

    @SuppressFBWarnings(value = "EI_EXPOSE_BUF")
    @Override
    public ByteBuffer get() {
        try {
            socket.receive(new DatagramPacket(buffer, buffer.length)); // TODO
            return ByteBuffer.wrap(buffer);
        } catch (IOException e) {
            return ByteBuffer.wrap(new byte[0]);
        }
    }

    @Override
    public void close() {
        socket.close();
    }
}
