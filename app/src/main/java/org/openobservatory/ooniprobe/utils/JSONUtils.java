package org.openobservatory.ooniprobe.utils;

import android.util.Log;
import android.webkit.JavascriptInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class JSONUtils {
    public static final class JSONL implements Iterable<JSONObject> {

        private static final String DEBUG_TAG = "JSONLIterator";

        private FileReader fr;
        private File file;

        public JSONL(File file) throws IOException {
            try {
                this.file = file;
                this.fr = new FileReader(file);
            } catch (IOException e) {
                Log.e(DEBUG_TAG, "Failed to open file " + file.getPath());
                throw e;
            }
        }

        public String getLineN(int n) throws RuntimeException {
            BufferedReader br = new BufferedReader(this.fr);
            String line;
            int idx = 0;
            try {
                while ((line = br.readLine()) != null) {
                    if (idx == n) {
                        return line;
                    }
                    idx++;
                }
            } catch (IOException e) {
                throw new RuntimeException("IOError in reading line");
            }
            throw new RuntimeException("Could not find line at offset");
        }

        public Iterator<JSONObject> iterator(){
            BufferedReader br = new BufferedReader(this.fr);
            return new JSONIterator(br, this.file);
        }

        private class JSONIterator  implements  Iterator<JSONObject>{
            private BufferedReader br;
            private File file;

            public JSONIterator(BufferedReader br, File file) {
                this.br = br;
                this.file = file;
            }

            private void closeReader() {
                try {
                    this.br.close();
                } catch (IOException f) {
                    Log.e(DEBUG_TAG, "Failed to close file " + this.file.getPath());
                }
            }

            public boolean hasNext() {
                try {
                    // Will return true when there is still data to read.
                    if (!this.br.ready()) {
                        this.closeReader();
                        return false;
                    }
                    return true;
                } catch (IOException e) {
                    Log.e(DEBUG_TAG, "Failed to read file " + this.file.getPath());
                    this.closeReader();
                    return false;
                }
            }

            public JSONObject next() throws NoSuchElementException {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                String line;
                try {
                    line = this.br.readLine();
                    return new JSONObject(line);
                } catch (IOException e) {
                    Log.e(DEBUG_TAG, "Failed to read line " + this.file.getPath());
                    return next();
                } catch (JSONException e) {
                    Log.e(DEBUG_TAG, "Failed to parse JSON in line " + this.file.getPath());
                    return next();
                }
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        }
    }

    public static class InjectedJSON {
        private String jsonData;

        public InjectedJSON(String json) {
            jsonData = json;
        }

        @JavascriptInterface
        public String get() {
            return jsonData;
        }
    }
}
