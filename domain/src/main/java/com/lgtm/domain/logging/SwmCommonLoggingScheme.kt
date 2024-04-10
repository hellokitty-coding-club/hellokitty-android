package com.lgtm.domain.logging

import com.swm.logging.android.logging_scheme.SWMLoggingScheme
import java.lang.reflect.Type

@Suppress("unused")
class SwmCommonLoggingScheme(
    eventLogName: String,
    screenName: String,
    logVersion: String,
    logData: Map<String, Any>,
) : SWMLoggingScheme() {

    init {
        setLoggingScheme(
            eventLogName = eventLogName,
            screenName = screenName,
            logVersion = logVersion,
            logData = logData.toMutableMap()
        )
    }

    class Builder {
        private lateinit var eventLogName: String
        private lateinit var screenName: String
        private var logVersion: String = "1"
        private var map = mapOf<String, Any>()

        fun setEventLogName(eventLogName: String): Builder {
            this.eventLogName = eventLogName
            return this
        }

        fun setScreenName(screenName: String): Builder {
            this.screenName = screenName
            return this
        }

        fun setScreenName(type: Type): Builder {
            check(type is Class<*>)
            this.screenName = type.simpleName
            return this
        }


        fun setLogData(map: Map<String, Any>): Builder {
            this.map = map.mapValues { handleLocalDateTime(it.value) }
            return this
        }

        private fun handleLocalDateTime(value: Any): Any {
            return if (value is java.time.LocalDateTime) {
                value.toString()
            } else {
                value
            }
        }

        fun setLogVersion(logVersion: Int): Builder {
            this.logVersion = logVersion.toString()
            return this
        }

        fun build(): SwmCommonLoggingScheme {
            check(::eventLogName.isInitialized) { "eventLogName is not initialized" }
            check(::screenName.isInitialized) { "screenName is not initialized" }

            return SwmCommonLoggingScheme(
                eventLogName,
                screenName,
                logVersion,
                map
            )
        }
    }
}