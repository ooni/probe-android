package org.openobservatory.ooniprobe.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import android.content.Context;

/**
 * Created by lorenzo on 27/04/16.
 */
public class LogUtils {
    public static String readLogFile(Context c, String filename) {
        String logPath = c.getFilesDir() + filename;
        File file = new File(logPath);

        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            //Need to add proper error handling here
        }
        return text.toString();
    }

    public static String[] getLogParts(Context c, String jsonfile){
        final String jsonContent = LogUtils.readLogFile(c, jsonfile);
        final String[] parts = jsonContent.split("\n");
        return parts;
    }

}
