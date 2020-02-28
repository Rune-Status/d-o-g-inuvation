package org.rspeer.io;

import org.rspeer.Configuration;
import org.rspeer.api.commons.ExecutionService;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public final class LogFileBuffer {

    private static final int MAX_BUFFER_SIZE = 15;
    private static final int REFRESH_RATE = 15;
    private static final int MAX_LOG_FILES = 10;

    private final String fileName;
    private final List<String> buffer;
    private final ScheduledFuture<?> future;

    public LogFileBuffer() {
        System.out.println("Starting file logger.");
        String instant = Instant.now().toString();
        String name = instant.split("\\.")[0] + ".txt";

        this.fileName = name.replaceAll(":", "");
        this.buffer = Collections.synchronizedList(new LinkedList<>());

        future = ExecutionService.schedule(this::clear, REFRESH_RATE, TimeUnit.SECONDS);
    }

    public boolean add(String string) {
        boolean result = buffer.add(string);
        if (result && buffer.size() >= MAX_BUFFER_SIZE) {
            clear();
        }

        return result;
    }

    public synchronized void clear() {
        if (buffer.size() == 0) {
            return;
        }

        File dir = new File(Configuration.LOGS);
        if (!dir.exists() && !dir.mkdirs()) {
            System.out.println("We were unable to create a logs directory");
            return;
        }

        File[] files = dir.listFiles();
        if (files != null && files.length > MAX_LOG_FILES && !clearOldLogFiles(files)) {
            System.out.println("Failed to delete old log files");
            return;
        }

        File logFile = new File(dir, fileName);
        try {
            if (!logFile.exists() && !logFile.createNewFile()) {
                System.out.println("We were unable to create the log file for the current session");
                return;
            }
        } catch (IOException e) {
            return;
        }

        try (FileWriter writer = new FileWriter(logFile, true)) {
            BufferedWriter bw = new BufferedWriter(writer);
            for (String element : buffer) {
                bw.append(element);
                bw.append("\n");
            }
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            buffer.clear();
        }
    }

    private boolean clearOldLogFiles(File[] files) {
        boolean result = true;
        for (int i = 0; i < files.length - MAX_LOG_FILES; i++) {
            File file = files[i];
            if (file != null) {
                result &= file.delete();
            }
        }

        return result;
    }

    public void dipose() {
        System.out.println("Stopping file logger.");
        clear();
        if (future != null) {
            future.cancel(false);
        }
    }
}
