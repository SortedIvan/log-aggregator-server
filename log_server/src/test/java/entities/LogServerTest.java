package entities;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LogServerTest {
    private LogServer logServer;

    @Test
    public void test_LogServerInitializedWithWrongIp() {
        String badIp = "-100.-100.-100.-100";

        var thrown = Assertions.assertThrows(
                RuntimeException.class,
                () -> new LogServer(badIp, 3333)
        );

        Assertions.assertTrue(thrown.getMessage().contains("Server ip is incorrect"));
    }

    @Test
    public void test_LogServerInitializedWithWrongPort() {
        int badPort = -1000;

        var thrown = Assertions.assertThrows(
                RuntimeException.class,
                () -> new LogServer("127.0.0.1", badPort)
        );

        Assertions.assertTrue(thrown.getMessage().contains("Invalid server port"));
    }
}
