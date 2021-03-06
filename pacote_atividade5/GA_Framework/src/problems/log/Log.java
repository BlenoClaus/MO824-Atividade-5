package problems.log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Log {
	
	private static final Logger logger = Logger.getLogger("Reporter"); 
	private static Map<String, Logger> logMap = new HashMap<>(); 
	
	public static synchronized Logger getLogger(String fileName) {
		Logger logger = logMap.get(fileName);
		if (logger == null) {
			FileHandler fh;
			try {
				logger = Logger.getLogger(fileName);
				fh = new FileHandler("instances/"+fileName);
				fh.setFormatter(new CustomFormatter());
				logger.addHandler(fh);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} 
			logMap.put(fileName, logger);
			return logger;
		}
		return logMap.get(fileName);
	}

	public static void info(String msg) {
		logger.info(msg);		
	}
	
	private static class CustomFormatter extends Formatter {
		 
        @Override
        public String format(LogRecord record) {
            StringBuffer sb = new StringBuffer();
            sb.append(record.getMessage());
            sb.append("\n");
            return sb.toString();
        }
         
    }

}

