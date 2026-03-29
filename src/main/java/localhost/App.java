package localhost;

import java.io.File;
import java.io.IOException;
import interfaces.ksy.Msg;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.common.flogger.FluentLogger;

import interfaces.schema.Config;
import io.kaitai.struct.ByteBufferKaitaiStream;
import localhost.suppliers.Udp;
import tools.jackson.databind.json.JsonMapper;

/**
 * Main-Class.
 */
public final class App {

    /**
     * Flogger.
     */
    private static final FluentLogger LOG = FluentLogger.forEnclosingClass();

    /**
     * @param args
     * @see https://github.com/checkstyle/checkstyle/issues/17810
     */
    public static void main(final String... args) {
        final var configPath = Path.of("./config.json");
        final var config = Files.exists(configPath) ? JsonMapper.shared()
                .readValue(new File("config.json"), Config.class)
                : new Config();
        final var uri = config.getUri().get();
        try (var udp = new Udp(uri.getPort(), uri.getHost())) {
            try (var stream = new ByteBufferKaitaiStream(udp.get())) {
                final var msg = new Msg(stream);
                IO.println(JsonMapper.shared().writeValueAsString(msg));
            }
        } catch (IOException e) {
            LOG.atSevere().withCause(e);
        }
    }

    private App() {
        throw new UnsupportedOperationException("Cannot construct Main-Class");
    }
}
