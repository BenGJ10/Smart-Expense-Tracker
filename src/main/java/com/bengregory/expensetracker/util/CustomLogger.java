package com.bengregory.expensetracker.util;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class CustomLogger {
    private static final Logger LOGGER = Logger.getLogger(CustomLogger.class.getName());
    private static final String LOG_DIR = "logs";
    private static final String LOG_FILE = LOG_DIR + File.separator + "log";
    private static CustomLogger instance;

    private CustomLogger() {
        try {
            // Ensure log directory exists
            File logDir = new File(LOG_DIR);
            if (!logDir.exists()) {
                logDir.mkdirs();
            }
            // This will create a custom logger
            FileHandler fileHandler = new FileHandler(LOG_FILE, true); // Append mode
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
            LOGGER.setLevel(Level.ALL);
        } catch (IOException e) {
            System.err.println("Failed to initialize logger: " + e.getMessage());
        }
    }

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