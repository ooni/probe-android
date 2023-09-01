package org.openobservatory.applogger

enum class LogType {
    ALL, DEBUG, ERROR, WARNING, INFO, API;

    companion object {
        fun getAllTypes() = listOf(ALL.name, DEBUG.name, ERROR.name, WARNING.name, INFO.name, API.name)
    }
}
