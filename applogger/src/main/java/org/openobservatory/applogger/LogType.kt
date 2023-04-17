package org.openobservatory.applogger

enum class LogType {
    ALL, ERROR, WARNING, INFO, API;

    companion object {
        fun getAllTypes() = listOf(ALL.name, ERROR.name, WARNING.name, INFO.name, API.name)
    }
}
