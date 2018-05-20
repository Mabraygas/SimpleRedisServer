package com.mabraygas.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

public class Tools {

    public static long UnixTimestamp() {
        return System.currentTimeMillis() / 1000L;
    }

    public static JsonNode JsonParse(InputStream stream) throws IOException {
        if(stream == null) {
            return null;
        }

        return Mapper.readTree(stream);
    }

    private static ObjectMapper Mapper = new ObjectMapper();
}
