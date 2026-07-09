package com.inno.harbor.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DataReader {

    public List<String> read(String resourceName) throws IOException {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(resourceName);
        if (stream == null) {
            throw new IOException("Resource not found on classpath: " + resourceName);
        }
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    lines.add(line);
                }
            }
        }
        return lines;
    }
}
