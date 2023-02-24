package org.openobservatory.applogger

import android.content.Context
import java.io.File

class FileUtils(val context: Context) {

    private val logFileDir: File by lazy {
        File("${context.filesDir}/Log")
    }

    private val logFile: File by lazy {
        File("$logFileDir/logger.txt")
    }

    fun handleFileCreation() {
        if (!logFileDir.exists()) {
            logFileDir.mkdir()
        }

        if (!logFile.exists()) {
            logFile.createNewFile()
        }
    }

}
