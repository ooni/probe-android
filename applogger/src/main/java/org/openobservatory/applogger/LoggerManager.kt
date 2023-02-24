package org.openobservatory.applogger

import android.content.Context
import android.util.Log
import java.io.*


abstract class LoggerManager(private val context: Context) : ILogger, Subject {

    //https://www.codemetrix.in/2019/09/write-logs-into-log-file-in-android.html
    private var observers = mutableListOf<MessageObserver>()
    private val logFileDir: File by lazy {
        File("${context.filesDir}/Log")
    }

    val logFile: File by lazy {
        File("$logFileDir/logger.txt")
    }

    companion object {
        @JvmStatic
        fun getTag(line: String): String {
            return line.substring(24).trim().split(":").first().trim()
        }
    }

    protected fun saveLog(msg: String, tag: String) {
        try {
            if (!logFileDir.exists()) {
                logFileDir.mkdir()
            }

            if (!logFile.exists()) {
                logFile.createNewFile()
            }

            val buf = BufferedWriter(FileWriter(logFile, true))
            buf.append(msg.replace("\r\n", ""))
            buf.append("\n")
            buf.flush()
            buf.close()

        } catch (ex: Exception) {
            Log.e("Logger", ex.toString())

        }
        notifyMessage(msg, tag)
    }

    override fun register(observer: MessageObserver) {
        observers.add(observer)
    }

    override fun unRegister(observer: MessageObserver) {
        observers.remove(observer)
    }

    override fun notifyMessage(msg: String, tag: String?) {
        observers.forEach {
            it.update(tag)
        }
    }


    fun getLog(tag: String? = null): StringBuilder {

        //Read text from file
        val text = StringBuilder()

        try {
            val br = BufferedReader(FileReader(logFile))
            var line: String?
            while (br.readLine().also { line = it } != null) {
                tag?.let {
                    when {
                        tag == getTag(line ?: "ALL") -> {
                            text.append(line)
                            text.append('\n')
                        }

                        tag == LogType.ALL.name -> {
                            text.append(line)
                            text.append('\n')
                        }

                        else -> {}
                    }
                } ?: kotlin.run {

                }
            }
            br.close()
        } catch (e: IOException) {
            Log.e("Logger", e.toString())
        }
        return text
    }

    fun deleteOldLog() {
        if (logFile.exists()) {
            logFile.delete()
        }
    }

}
