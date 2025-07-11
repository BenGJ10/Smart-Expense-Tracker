package com.bengregory.expensetracker.util;

import java.io.File;
import java.util.Date;
import java.io.IOException;
import java.util.logging.*;
import java.text.SimpleDateFormat;

public class CustomLogger {
    private static final Logger LOGGER = Logger.getLogger(CustomLogger.class.getName());
    private static final String LOG_DIR = "logs";
    private static CustomLogger instance; // only instance of CustomLogger

    private CustomLogger() {
        try {
            // Ensure log directory exists
            File logDir = new File(LOG_DIR);
            if (!logDir.exists()) {
                logDir.mkdirs();
            }
            // Reset logging
            LogManager.getLogManager().reset();
            // This will create a custom logger
            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            String logFileName = LOG_DIR + File.separator + "log-" + timestamp + ".log";
            FileHandler fileHandler = new FileHandler(logFileName);
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.ALL);

            LOGGER.setLevel(Level.ALL);
            LOGGER.addHandler(fileHandler); // Nothing will be shown in the console
        } catch (IOException e) {
            System.err.println("Failed to initialize logger: " + e.getMessage());
        }
    }

    // Ensure that only one instance of CustomLogger is ever created
    public static CustomLogger getInstance() {
        if (instance == null) {
            synchronized (CustomLogger.class) {
                if (instance == null) {
                    instance = new CustomLogger();
                }
            }
        }
        return instance;
    }

    public void info(String message) {
        LOGGER.info(message);
    }

    public void warning(String message) {
        LOGGER.warning(message);
    }

    public void error(String message, Throwable throwable) {
        LOGGER.log(Level.SEVERE, message, throwable);
    }
}