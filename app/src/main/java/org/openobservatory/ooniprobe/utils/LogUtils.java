package org.openobservatory.ooniprobe.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import android.content.Context;

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

    public static String getLogParts(Context c, String jsonfile, int position){
        final String jsonContent = LogUtils.readLogFile(c, jsonfile);
        final String[] parts = jsonContent.split("\n");
        return parts[position];
    }

    public static int getNumLogParts(Context c, String jsonfile){
        final String jsonContent = LogUtils.readLogFile(c, jsonfile);
        final String[] parts = jsonContent.split("\n");
        if (parts.length > 1)
            return 2;
        else if (parts.length == 1 && parts[0].length() > 0)
            return 1;
        return 0;
    }

}
