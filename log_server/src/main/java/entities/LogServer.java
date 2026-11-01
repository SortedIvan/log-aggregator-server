package entities;

import java.net.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class LogServer implements AutoCloseable{
    private static final Logger serverLogger = Logger.getLogger(LogServer.class.getName());
    private ServerSocket serverSocket;
    private String ip;
    private int port;
    private AtomicBoolean isRunning = new AtomicBoolean(false);

    private static final int MAX_SERVER_BACKLOG = 1000;

    public LogServer(String ip, int port) {
        tryInitServerSocket(ip, port);
    }

    private void tryInitServerSocket(String ip, int port) {
        if (ip == null || ip.isEmpty()) {
            String errorMessage = "Server ip cannot be empty.";
            serverLogger.warning(errorMessage);
            throw new RuntimeException(errorMessage);
        }
        if (port <= 0) {
            String errorMessage = "Invalid server port.";
            serverLogger.warning(errorMessage);
            throw new RuntimeException(errorMessage);
        }

        try {
            InetSocketAddress serverAddress = new InetSocketAddress(ip, port);

            if (serverAddress.isUnresolved()) {
                throw new RuntimeException("Server ip is incorrect");
            }

            serverLogger.info(
                    String.format(
                            "Server bound to IP: %s and port: %s", ip,
                            String.valueOf(port)));

            this.serverSocket = new ServerSocket();
            this.serverSocket.bind(serverAddress, MAX_SERVER_BACKLOG);
        }
        catch (Exception ex) {
            String errorMessage = String.format(
                    "Caught exception while trying to bind server socket: %s", ex.getMessage());
            serverLogger.severe(errorMessage);
            throw new RuntimeException(errorMessage, ex);
        }
    }

    public void runServer() {
        isRunning.set(true);

        while (isRunning.get()) {
            try {
                Socket clientSocket = serverSocket.accept();
            }
            catch (SocketException socketException) {
                String message = String.format(
                        "Caught socket exception: %s", socketException.getMessage()
                );
                serverLogger.warning(message);
                if (!isRunning.get()) {
                    break;
                }
            }
            catch (Exception ex) {
                String message = String.format(
                        "Caught internal server exception: %s, shutting down log server", ex.getMessage()
                );
                serverLogger.severe(message);
                isRunning.set(false);
                break;
            }
        }
    }

    @Override
    public void close() throws Exception {
        if (isRunning.get()) {
            isRunning.set(false);
        }
    }
}
