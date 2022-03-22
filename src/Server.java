import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class for Server.
 * Offer common methods for all Server
 */
public class Server {
    /**
     * The maximum number of threads.
     */
    private static final int MAX_THREADS = 10;
    /**
     * The thread pool that reuses a fixed number(MAX_THREADS) of threads operating off a shared unbounded queue.
     */
    private final ExecutorService pool = Executors.newFixedThreadPool(MAX_THREADS);
    private ServerSocket ss;

    /**
     * Constructor method for class Server.
     * To allow socket to connect to the server on the port
     * @param directory the directory where the web pages are stored
     * @param port      the port which the server socket will listen on
     */
    public Server(String directory, int port) {
        try {
            Log logfile = new Log("../log.txt");
            System.out.println("___log.txt is created, waiting for logging___");

            ss = new ServerSocket(port);
            System.out.println("___Server start to listen on port " + port + "___");

            while (true) {
                Socket conn = ss.accept();
                System.out.println("___Server got new connection request from client" + conn.getInetAddress() + "___");

                pool.execute(new ConnectionHandler(directory, conn, logfile));
            }
        } catch (IOException ioe) {
            System.out.println("Ooops: " + ioe.getMessage());
            pool.shutdown();
        }
    }
}
