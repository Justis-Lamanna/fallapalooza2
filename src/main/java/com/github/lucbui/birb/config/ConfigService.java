package com.github.lucbui.birb.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;

public class ConfigService {
    private static FallapaloozaConfig config = null;

    public static FallapaloozaConfig getConfig() {
        if(config == null) {
            try {
                config = readConfig();
                if(System.getProperty("sheet") != null) {
                    config.setSpreadsheetId(System.getProperty("sheet"));
                }
            } catch (IOException ex) {
                throw new IllegalStateException("Unable to read config", ex);
            }
        }
        return config;
    }

    private static FallapaloozaConfig readConfig() throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();
        return mapper.readValue(ConfigService.class.getResource("/config.yml"), FallapaloozaConfig.class);
    }
}
