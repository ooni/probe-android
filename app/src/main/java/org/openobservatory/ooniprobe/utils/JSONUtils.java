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
        private String filePath;

        public JSONL(String filePath) throws IOException {
            this.filePath = filePath;
            File file = new File(filePath);
            try {
                this.fr = new FileReader(file);
            } catch (IOException e) {
                Log.e(DEBUG_TAG, "Failed to open file " + this.filePath);
                throw e;
            }
        }

        public Iterator<JSONObject> iterator(){
            BufferedReader br = new BufferedReader(this.fr);
            return new JSONIterator(br, this.filePath);
        }

        private class JSONIterator  implements  Iterator<JSONObject>{
            private BufferedReader br;
            private Boolean eof;
            private String filePath;

            public JSONIterator(BufferedReader br, String filePath) {
                this.br = br;
                this.filePath = filePath;
                this.eof = false;
            }

            public boolean hasNext() {
                return !this.eof;
            }

            public JSONObject next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                String line;
                try {
                    line = this.br.readLine();
                    if (line == null) {
                        this.eof = true;
                        this.br.close();
                        throw new NoSuchElementException();
                    }
                } catch (IOException e) {
                    Log.e(DEBUG_TAG, "Failed to read file " + this.filePath);
                    this.eof = true;
                    try { this.br.close(); } catch (IOException f) {}
                    throw new NoSuchElementException();
                }

                try {
                    return new JSONObject(line);
                } catch (JSONException e) {
                    Log.e(DEBUG_TAG, "Failed to read line " + this.filePath);
                    // We skip invalid lines
                    //return next();
                    throw new NoSuchElementException();
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
