import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Class for ConnectionHandler.
 * Multi-threaded processing of the interaction among server and clients
 * some code of the methods run(), cleanup() come from Micheal Young(mct25@st-andrews.ac.uk)
 */
public class ConnectionHandler implements Runnable {
    private Socket conn;
    private String root;
    private Log logfile;
    private InputStream is;
    private OutputStream os;    // the stream send from the service to the client
    private BufferedReader br;

    /**
     * Constructor method for class Server.
     * To initialize parameters when a new connection (thread) is opened
     * @param root    root directory where the web pages are stored
     * @param conn    the port which the server socket will listen on
     * @param logfile file that records request info
     */
    public ConnectionHandler(String root, Socket conn, Log logfile) {
        this.conn = conn;
        this.root = root;
        this.logfile = logfile;

        try {
            is = conn.getInputStream();
            os = conn.getOutputStream();
            br = new BufferedReader(new InputStreamReader(is));
        } catch (IOException ioe) {
            System.out.println("ConnectionHandler: " + ioe.getMessage());
            cleanup();
        }
    }

    /**
     * Method for recording what is required to do.
     */
    @Override
    public void run() {
        System.out.println("___new ConnectionHandler thread started___ ");
        System.out.println("___THREAD ID: " + Thread.currentThread().getId() + "___");
        try {
            handleRequest();
        } catch (Exception e) {
            System.out.println("ConnectionHandler:run() " + e.getMessage());
            cleanup();
        }
    }

    /**
     * Method for dealing with different HTTP request types(HEAD, GET).
     * @throws IOException deals with  I/O exception
     */
    private void handleRequest() throws IOException {
        String line = br.readLine();
        System.out.println("____ConnectionHandler: " + line + "____");

        // such as: GET /another/page3.html HTTP/1.1 -> "GET" + "/another/page3.html" + "HTTP/1.1"
        String[] words = line.split(" ");
        String requestType = words[0];
        String fileDirectory = words[1];

        System.out.println("____Start to handle request____");
        RequestHandler rh = new RequestHandler(requestType, root, fileDirectory);

        byte[] response = rh.getResponse();
        os.write(response);

        logfile.writeLog(rh.getRequestInfo());
    }

    /**
     * Method for cleaning up data when exiting the system.
     */
    private void cleanup() {
        System.out.println("___ConnectionHandler: cleaning up and exiting___");
        try {
            br.close();
            is.close();
            conn.close();
        } catch (IOException ioe) {
            System.out.println("ConnectionHandler:cleanup " + ioe.getMessage());
        }
    }
}
