package com.springboot.api.tools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RecordLogger {

    private static final String SEPARATOR = " - ";
    private static final String HADOOP_SEPARATOR = "\t";

    private RecordLogger() {
    }

    public static void timeLog(String methodName, long time) {
        try {
            Logger logger = LogManager.getLogger(CommonLoggerNameConstants.TIME_CALCULATOR_LOGGER);
            logger.info(format(false, methodName, time));
        } catch (Exception e) {
            LogContext.instance().error(e, "请求耗时日志记录失败");
        }
    }

    private static String format(boolean isHadoopLog, Object... messages) {
        String formatSeparator = SEPARATOR;
        if (isHadoopLog)
            formatSeparator = HADOOP_SEPARATOR;
        if (messages == null || messages.length < 1)
            return "";
        StringBuilder ftMessage = new StringBuilder();
        int count = 0;
        for (Object msg : messages) {
            ftMessage.append(msg == null ? "" : msg);
            if (count < messages.length - 1)
                ftMessage.append(formatSeparator);
            count++;
        }
        return ftMessage.toString();
    }

}
