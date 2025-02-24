package org.captnced.configs;

import java.io.*;

public class Config {

    private final File configFile;

    public Config(String configName) {
        configFile = new File(System.getProperty("user.dir") + File.separator + configName + ".txt");
        if (!configFile.exists()) {
            try {
                boolean ignored = configFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String getValueFromKey(String key) {
        for (String line : readLines()) {
            String lineKey = line.replaceAll(" ", "").split("=")[0];
            String value = line.replaceAll(" ", "").split("=")[1];
            if (lineKey.equals(key)) {
                return value;
            }
        }
        return null;
    }

    private String[] readLines() {
        String[] lines;
        try {
            BufferedReader br = new BufferedReader(new FileReader(configFile));
            lines = br.lines().toArray(String[]::new);
        } catch (FileNotFoundException e) {
            return new String[0];
        }
        return lines;
    }
}
