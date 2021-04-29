package org.openobservatory.ooniprobe.common;

import androidx.test.filters.SmallTest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

@SmallTest
public class DateAdapterTest {
    private static final String STRING = "\"1970-01-01 00:00:00\"";
    private static final Date DATE = new Date(0);

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Date.class, new DateAdapter())
            .create();

    @Test
    public void write() {
        assertEquals(STRING, gson.toJson(DATE));
    }

    @Test
    public void read() {
        assertEquals(DATE, gson.fromJson(STRING, Date.class));
    }
}
