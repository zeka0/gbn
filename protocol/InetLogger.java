package protocol;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Created by AlienX
 */
public class InetLogger {
    private Logger logger;

    public InetLogger(String filename, String logger_name){
        logger = Logger.getLogger(logger_name);
        try {
            InetLogger.setLoggingProperties(filename, logger);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public void log(Level level, String message){
        logger.log(level, message);
    }

    /**Find the path to store logs*/
    private static String getLogName(String file_name) {
        StringBuffer logPath = new StringBuffer();
        logPath.append("E:\\JetbrainsProjects\\Java\\JavaNet\\src\\protocol");
        logPath.append("\\" + file_name);
        File file = new File(logPath.toString());
        if (!file.exists())
            file.mkdir();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        logPath.append("\\" + sdf.format(new Date())+".log");

        return logPath.toString();
    }

    /**Set output file path*/
    private static void setLoggingProperties(String file_name, Logger logger){
        setLoggingProperties(file_name, logger, Level.ALL);
    }

    /**Set output file path*/
    private static void setLoggingProperties(String file_name, Logger logger,Level level) {
        FileHandler fh;
        try {
            String logName = getLogName(file_name);
            System.out.println("Created log: " + logName);
            fh = new FileHandler(logName,true);
            logger.addHandler(fh);//FileHandler
            fh.setFormatter(new SimpleFormatter());//Output Format
        } catch (SecurityException e) {
            logger.log(Level.SEVERE, "Security error", e);
        } catch (IOException e) {
            logger.log(Level.SEVERE,"FileHandler error", e);
        }
    }

    public static void main(String [] args) {
        InetLogger logger = new InetLogger("InetLog", "test");
        logger.log(Level.INFO, "d");
        logger.log(Level.INFO, "e");
        logger.log(Level.INFO, "f");
        logger.log(Level.INFO, "g");
        logger.log(Level.INFO, "h");
        System.out.print(1/(double)2);
    }
}
