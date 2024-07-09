package org.openobservatory.ooniprobe.kt

import java.io.File
import java.io.FileOutputStream
import java.nio.charset.Charset;

class FileUtils {
    companion object {
        fun writeStringToFile(file: File, s: String, charsetName: Charset, append: Boolean) {
            FileOutputStream(file, append).bufferedWriter(charsetName).use {
                it.write(s, 0, s.length)
            }
        }
        fun readFileToString(file: File, charsetName: Charset): String {
            return file.readText(charsetName)
        }
    }
}