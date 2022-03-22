import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;

/**
 * Class for Request handler.
 * To handle request from the client
 */
public class RequestHandler {
    private static final String PROTOCOL_VERSION = "HTTP/1.1";
    private static final String WORKING_OK = "200 OK";
    private static final String RESOURCE_NOT_FOUND = "404 Not Found";
    private static final String REQUEST_NOT_IMPLEMENT = "501 Not Implemented";

    private String requestType;
    private String root;
    private String fileDirectory;
    private String fileName;
    private String fileFormat;

    private String responseCode;
    private byte[] content;
    private String contentType = "text/html"; // the default content type is text/html
    private byte[] responses;

    /**
     * Constructor method for class RequestHandler.
     * @param requestType   the request type
     * @param root          the root directory to find a file, such as ../www
     * @param fileDirectory the directory where the file is stored, such as /another/page3.html
     */
    public RequestHandler(String requestType, String root, String fileDirectory) {
        this.requestType = requestType;
        this.root = root;
        this.fileDirectory = fileDirectory;
    }

    /**
     * Method for getting response according to different types of requests.
     * @return  a byte array that contains response information
     */
    public byte[] getResponse() {
        String path = root + fileDirectory;

        File file = new File(path);

        System.out.println("____Start to judge request_____");
        if (!isValidRequest(requestType)) { // judge whether the request is valid
            System.out.println("_____REQUEST NOT IMPLEMENT_____");
            responseCode = PROTOCOL_VERSION + " " + REQUEST_NOT_IMPLEMENT;
            content = responseCode.getBytes();
            responses = headResponse();

        } else if (!file.exists()) { // judge whether the file is existing
            System.out.println("_____RESOURCE NOT FOUND_____");
            responseCode = PROTOCOL_VERSION + " " + RESOURCE_NOT_FOUND;
            content =  responseCode.getBytes();
            responses = headResponse();

        } else { // process the valid request
            System.out.println("_____VALID REQUEST_____");
            responseCode = PROTOCOL_VERSION + " " + WORKING_OK;

            System.out.println("_____Start to judge file format_____");
            String[] fileDirectories = fileDirectory.split("/"); // such as split "/another/page3.html"

            if (fileDirectories.length > 1) {
                fileName = fileDirectories[fileDirectories.length - 1];
            } else {
                fileName = fileDirectories[0];
            }

            // get the format of the file. such as "page3.html" --> "html"
            fileFormat = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

            // determine whether the file is an image
            if (getImageFormat(fileFormat) != null) {
                contentType = "image/" + getImageFormat(fileFormat);
            }

            content = getContent(path);
            System.out.println("_____Get the content for valid request_____");

            if (requestType.equals("HEAD")) {
                responses = headResponse();
                System.out.println("_____Get HEAD response_____");
            } else if (requestType.equals("GET")) {
                // since all types of response contain the Header
                // it is more convenient to get header and body separately and then combine them together when it is needed
                responses = combine(headResponse(), getContent(path));
                System.out.println("_____Get GET response_____");
            }
        }
        return responses;
    }

    /**
     * Method for judging whether the request is valid.
     * @param request the request type that is input by the client
     * @return a boolean to tell whether the request is valid
     */
    public boolean isValidRequest(String request) {
        boolean isValid = false;
        if (request.equals("HEAD") || request.equals("GET")) {
            isValid = true;
        }
        return isValid;
    }

    /**
     * Method for determing whether the file is an image.
     * @param fileFormat the format of the file
     * @return a string that indicates the image format if the file is an image
     *         otherwise, return null
     */
    public String getImageFormat(String fileFormat) {
        String imageFormat;
        switch (fileFormat) {
            case "gif":
                imageFormat = "gif";
                break;
            case "jpeg":
                imageFormat = "jpeg";
                break;
            case "jpg":
                imageFormat = "jpg";
                break;
            case "png":
                imageFormat = "png";
                break;
            default:
                imageFormat = null;
        }
        return imageFormat;
    }

    /**
     * Method for getting response for HEAD request.
     * @return a byte array that contains HEAD response information
     */
    public byte[] headResponse() {
        String head = responseCode + "\r\n";
        head += "Server: Simple Java Http Server\r\n";
        head += "Content-Type: " + contentType + "\r\n";
        head += "Content-Length: " + content.length + "\r\n";
        head += "\r\n";
        return head.getBytes();
    }

    /**
     * Method for getting the content of the corresponding file.
     * @param path where the file is stored
     * @return a byte array that contains the content of the corresponding file
     */
    public byte[] getContent(String path) {
       try {
            if (getImageFormat(fileFormat) != null) {
                System.out.println("___try to get content of image___");
                FileInputStream in = new FileInputStream(path);
                ByteArrayOutputStream out = new ByteArrayOutputStream();

                int c;
                while ((c = in.read()) != -1) {
                    out.write(c);
                    content = out.toByteArray();
                }
                in.close();
                out.close();
                System.out.println("___Successfully get content of image___");

            } else if (fileFormat.contains("html")) {
                System.out.println("___try to get content of html___");
                BufferedReader br = new BufferedReader(new FileReader(path));

                String line;
                String contentInString = "";
                while ((line = br.readLine()) != null) {
                    contentInString += line + "\r\n";
                }
                br.close();
                content = contentInString.getBytes();
                System.out.println("___Successfully get content of html___");
            }
        } catch (IOException ioe) {
            System.out.println("Something wrong with getting content: " + ioe.getMessage());
        }
        return content;
    }

    /**
     * Method for combining two byte arrays into a new one.
     * @param byteAry1 the first needed byte array
     * @param byteAry2 the second needed byte array
     * @return a byte array that contains all needed information
     */
    public byte[] combine(byte[] byteAry1, byte[] byteAry2) {
        byte[] newArray = new byte[byteAry1.length + byteAry2.length];

        for (int i = 0; i < newArray.length; i++) {
            if (i < byteAry1.length) {
                newArray[i] = byteAry1[i];
            } else {
                newArray[i] = byteAry2[i - byteAry1.length];
            }
        }
        return newArray;
    }

    /**
     * Method for offering information to the log file.
     * @return a string that shows the detail of request
     */
    public String getRequestInfo() {
        return "Thread ID:" + Thread.currentThread().getId() + ", Request Type:" + requestType + ", Response code:" + responseCode;
    }
}
