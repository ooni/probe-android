package org.openobservatory.applogger

interface ILogger {
    fun e(msg: String)
    fun w(msg: String)
    fun i(msg: String)
    fun api(msg: String)
}
