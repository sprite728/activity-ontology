package io.mem0r1es.activitysubsumer.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LineByLineSplit {
    private BufferedReader reader;
    private String line;

    public LineByLineSplit(InputStream stream) {
        this.reader = new BufferedReader(new InputStreamReader(stream));
        try {
            line = reader.readLine();
        } catch (Exception e) {
            line = null;
        }
    }

    public boolean hasNext() {
        return line != null;
    }

    public String[] next() {
        String[] parts = line.split(" ");
        try {
            line = reader.readLine();
        } catch (Exception e) {
            line = null;
        }
        return parts;
    }

    public void close() {
        try {
            reader.close();
        } catch (Exception e) {
        }
    }
}