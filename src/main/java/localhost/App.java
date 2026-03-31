package localhost;

import java.io.IOException;
import interfaces.ksy.Msg;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.common.flogger.FluentLogger;

import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import interfaces.schema.Config;
import io.kaitai.struct.ByteBufferKaitaiStream;
import io.lettuce.core.RedisClient;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.pubsub.RedisPubSubAdapter;
import tools.jackson.databind.json.JsonMapper;

/**
 * Main-Class.
 */
@DefaultAnnotation(NonNull.class)
public final class App {
    /**
     * Flogger.
     */
    private static final FluentLogger LOG = FluentLogger.forEnclosingClass();

    public static final class Listener
            extends RedisPubSubAdapter<byte[], byte[]> {
        @Override
        public void message(final byte[] channel, final byte[] message) {
            try (var stream = new ByteBufferKaitaiStream(message)) {
                final var msg = new Msg(stream);
                IO.println(JsonMapper.shared().writeValueAsString(msg));
            } catch (IOException e) {
                LOG.atSevere().withCause(e);
            }
        }
    }

    /**
     * @param args
     * @see https://github.com/checkstyle/checkstyle/issues/17810
     */
    public static void main(final String... args) {
        final var configPath = Path.of("config.json");
        final var config = Files.exists(configPath)
                ? JsonMapper.shared().readValue(configPath, Config.class)
                : new Config();
        try (var client = RedisClient.create(config.getUri().orElseThrow());
                var pubSubConnection = client
                        .connectPubSub(ByteArrayCodec.INSTANCE)) {
            pubSubConnection.addListener(new Listener());
            final var pubSub = pubSubConnection.sync();
            pubSub.subscribe(config
                    .getChannel()
                    .orElseThrow()
                    .getBytes(StandardCharsets.UTF_8));
        }
    }

    private App() {
        throw new UnsupportedOperationException("Cannot construct Main-Class");
    }
}
