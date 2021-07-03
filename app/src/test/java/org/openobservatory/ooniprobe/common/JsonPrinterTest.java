package org.openobservatory.ooniprobe.common;

import com.google.gson.GsonBuilder;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JsonPrinterTest {

    @Test
    public void testPrettyFormating() {
        // Arrange
        JsonPrinter printer = build();
        String input =  "{\"registration_server_status\":\"ok\",\"whatsapp_endpoints_status\":\"ok\",\"whatsapp_web_status\":\"ok\"}";
        String result = "{\n" +
                "  \"registration_server_status\": \"ok\",\n" +
                "  \"whatsapp_endpoints_status\": \"ok\",\n" +
                "  \"whatsapp_web_status\": \"ok\"\n" +
                "}";

        // Act
        String value = printer.prettyText(input);

        // Assert
        assertEquals(result, value);
    }


    private JsonPrinter build() {
        return new JsonPrinter(new GsonBuilder());
    }

}