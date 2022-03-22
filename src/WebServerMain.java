/**
 * CS5001-p3-networking.
 * @author 210005313
 * @version 1.0
 */
public class WebServerMain {
    /**
     * The main method for running the server.
     * @param args command line arguments
     *             args[0] the root directory where the web pages are stored
     *             args[1] port
     */
    public static void main(String[] args) {
        try {
            if (args.length < 2) {
              System.out.println("Usage: java WebServerMain <document_root> <port>");
              System.exit(1);
            }
            String directory = args[0];
            int port = Integer.parseInt(args[1]);
            Server server = new Server(directory, port);
        } catch (Exception e) {
            System.out.println("Ooops: " + e.getMessage());
        }
    }
}
