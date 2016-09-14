package com.springboot.api.tools;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.TimeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.TriggeringPolicy;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.lookup.StrLookup;

import java.io.File;
import java.nio.charset.Charset;

public class LogContext {

    private static final String SEPARATOR = " - ";
    private static final String DEBUG_LEVEL = "debug";
    private static final String INFO_LEVEL = "info";
    private static final String WARN_LEVEL = "warn";
    private static final String ERROR_LEVEL = "error";
    private static final String FATAL_LEVEL = "fatal";
    private static final String TRACE_LEVEL = "trace";

    private static final ThreadLocal<LogContext> LOCAL = new ThreadLocal<LogContext>();

    private String loggerName;
    private String requestUUID;

    private LogContext() {
    }

    public static LogContext instance() {
        LogContext context = LOCAL.get();
        if (context == null) {
            context = new LogContext();
            LOCAL.set(context);
        }
        return context;
    }

    public void clear() {
        clearLoggerName();
        clearRequestUUID();
    }

    public void clearRequestUUID() {
        requestUUID = null;
    }

    public void clearLoggerName() {
        loggerName = null;
    }

    public void debug(Object... messages) {
        writeLog(DEBUG_LEVEL, null, messages);
    }

    public void debug(Throwable t, Object... messages) {
        writeLog(DEBUG_LEVEL, t, messages);
    }

    public void info(Object... messages) {
        writeLog(INFO_LEVEL, null, messages);
    }

    public void info(Throwable t, Object... messages) {
        writeLog(INFO_LEVEL, t, messages);
    }

    public void warn(Object... messages) {
        writeLog(WARN_LEVEL, null, messages);
    }

    public void warn(Throwable t, Object... messages) {
        writeLog(WARN_LEVEL, t, messages);
    }

    public void error(Object... messages) {
        writeLog(ERROR_LEVEL, null, messages);
    }

    public void error(Throwable t, Object... messages) {
        writeLog(ERROR_LEVEL, t, messages);
    }

    public void fatal(Object... messages) {
        writeLog(FATAL_LEVEL, null, messages);
    }

    public void fatal(Throwable t, Object... messages) {
        writeLog(FATAL_LEVEL, t, messages);
    }

    public void trace(Object... messages) {
        writeLog(TRACE_LEVEL, null, messages);
    }

    public void trace(Throwable t, Object... messages) {
        writeLog(TRACE_LEVEL, t, messages);
    }

    private void writeLog(String level, Throwable t, Object... messages) {
        Logger logger = getLogger();
        if (DEBUG_LEVEL.equals(level)) {
            if (t == null)
                logger.debug(formatMessage(messages));
            else
                logger.debug(formatMessage(messages), t);
        } else if (INFO_LEVEL.equals(level)) {
            if (t == null)
                logger.info(formatMessage(messages));
            else
                logger.info(formatMessage(messages), t);
        } else if (WARN_LEVEL.equals(level)) {
            if (t == null)
                logger.warn(formatMessage(messages));
            else
                logger.warn(formatMessage(messages), t);
        } else if (ERROR_LEVEL.equals(level)) {
            if (t == null)
                logger.error(formatMessage(messages));
            else
                logger.error(formatMessage(messages), t);
        } else if (FATAL_LEVEL.equals(level)) {
            if (t == null)
                logger.fatal(formatMessage(messages));
            else
                logger.fatal(formatMessage(messages), t);
        } else if (TRACE_LEVEL.equals(level)) {
            if (t == null)
                logger.trace(formatMessage(messages));
            else
                logger.trace(formatMessage(messages), t);
        }
    }

    private String formatMessage(Object... messages) {
        StringBuilder ftMessage = new StringBuilder();
        ftMessage.append(requestUUID);
        if (messages == null || messages.length < 1)
            return ftMessage.toString();
        int count = 0;
        Exception ex = new Exception();
        StackTraceElement[] elements = ex.getStackTrace();
        ftMessage.append(SEPARATOR).append(elements[3].getFileName()).append(":")
                .append(elements[3].getLineNumber()).append(SEPARATOR);
        for (Object msg : messages) {
            ftMessage.append(msg);
            if (count < messages.length - 1)
                ftMessage.append(SEPARATOR);
            count++;
        }
        return ftMessage.toString();
    }

    private Logger getLogger() {
        if (StringUtils.isEmpty(loggerName)) {
            loggerName = CommonLoggerNameConstants.DEFAULT_LOG_NAME;
        }
        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        if (!ctx.hasLogger(loggerName)) {
            createLogger(loggerName, ctx);
        }
        return LogManager.getLogger(loggerName);
    }

    private void createLogger(String loggerName, LoggerContext ctx) {
        final Configuration config = ctx.getConfiguration();
        StrLookup variableResolver = config.getStrSubstitutor().getVariableResolver();
        String baseLogPath = variableResolver.lookup("baseLogPath");
        String fileName = baseLogPath + File.separator + "business" + File.separator + loggerName + ".log";
        String filePattern = fileName + "." + variableResolver.lookup("dailyRollingFilePattern");
        String appenderName = loggerName + "_LogAppender";
        Level bizLoggerLevel = Level.valueOf(variableResolver.lookup("bizLoggerLevel"));
        Layout layout = PatternLayout.createLayout(variableResolver.lookup("logPattern"), null, config,
                null, Charset.forName("UTF-8"), true, false, null, null);
        TriggeringPolicy triggeringPolicy = TimeBasedTriggeringPolicy.createPolicy("1", "true");
        Appender appender = RollingFileAppender.createAppender(fileName, filePattern, "true",
                appenderName, "true", "8192", "true", triggeringPolicy, null, layout, null,
                "true", null, null, config);
        appender.start();
        config.addAppender(appender);
        AppenderRef businessAppenderRef = AppenderRef.createAppenderRef(appenderName, null, null);
        AppenderRef exceptionAppenderRef = AppenderRef.createAppenderRef("exception_LogAppender",
                null, null);
        AppenderRef[] refs = new AppenderRef[]{businessAppenderRef, exceptionAppenderRef};
        LoggerConfig loggerConfig = LoggerConfig.createLogger("false", bizLoggerLevel, loggerName,
                "true", refs, null, config, null);
        loggerConfig.addAppender(appender, null, null);
        loggerConfig.addAppender(config.getAppender("exception_LogAppender"), null, null);
        config.addLogger(loggerName, loggerConfig);
        ctx.updateLoggers();
    }

    public String getLoggerName() {
        return loggerName;
    }

    public void setLoggerName(String loggerName) {
        this.loggerName = loggerName;
    }

    public String getRequestUUID() {
        return requestUUID;
    }

    public void setRequestUUID(String requestUUID) {
        this.requestUUID = requestUUID;
    }

}
