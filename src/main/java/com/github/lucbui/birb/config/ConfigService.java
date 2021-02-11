package com.github.lucbui.birb.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;

public class ConfigService {
    private static FallapaloozaConfig config = null;

    public static FallapaloozaConfig getConfig() throws IOException {
        if(config == null) {
            config = readConfig();
        }
        return config;
    }

    private static FallapaloozaConfig readConfig() throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();
        return mapper.readValue(ConfigService.class.getResource("/config.yml"), FallapaloozaConfig.class);
    }
}
