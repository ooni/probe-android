package org.openobservatory.applogger

import android.content.Context
import android.text.format.DateFormat
import java.util.*

class InAppLogger(context: Context) : LoggerManager(context) {


    companion object {

        var loggerInstance: InAppLogger? = null

        @Synchronized
        fun initialize(context: Context): InAppLogger {
            if (loggerInstance == null) {
                loggerInstance = InAppLogger(context)
            }
            return loggerInstance!!
        }
    }

    override fun e(msg: String) {
        saveLog("${DateFormat.format("yyyy-MM-dd hh:mm:ss a", Date())} : ${LogType.ERROR.name} : $msg", LogType.ERROR.name)
    }

    override fun w(msg: String) {
        saveLog("${DateFormat.format("yyyy-MM-dd hh:mm:ss a", Date())} : ${LogType.WARNING.name} : $msg", LogType.WARNING.name)
    }

    override fun i(msg: String) {
        saveLog("${DateFormat.format("yyyy-MM-dd hh:mm:ss a", Date())} : ${LogType.INFO.name} : $msg", LogType.INFO.name)
    }

    override fun d(msg: String) {
        saveLog("${DateFormat.format("yyyy-MM-dd hh:mm:ss a", Date())} : ${LogType.DEBUG.name} : $msg", LogType.DEBUG.name)
    }

    override fun api(msg: String) {
        saveLog("${DateFormat.format("yyyy-MM-dd hh:mm:ss a", Date())} : ${LogType.API.name} : $msg", LogType.API.name)
    }


}
