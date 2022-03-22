import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Date;

/**
 * Class for Log.
 * To log the requests to a file.
 */
public class Log {
    private String logfile;
    private BufferedReader br;
    private BufferedWriter bw;
    private PrintWriter out;
    private Date date;

    /**
     * Constructor method for Log.
     * @param logfile the directory and the name of logfile
     */
    public Log(String logfile) {
        this.logfile = logfile;
    }

    /**
     * Method for log the request to file every time a request is made.
     * @param log the message that need to be logged to the file
     */
    public void writeLog(String log) {
        try {
            bw = new BufferedWriter(new FileWriter(logfile, true));
            out = new PrintWriter(bw, true);

            date = new Date();
            out.println(date + ": " + log);

            bw.close();
            out.close();
        } catch (Exception e) {
            System.out.println("Wrong with writing log : " + e.getMessage());
        }
    }
}
