package com.rstlss.app

enum class Actions
{
    PLAY,
    STOP,
    PAUSE,
    VOLUME,
    KILL,
    NOTIFY,
    PLAY_OR_FALLBACK,
    FADE_OUT,
    CANCEL_FADE_OUT,
    SNOOZE
}

enum class ActionOnError {
    RESET,
    NOTIFY
}

enum class ActionOpenParam {
    SLEEP,
    ALARM,
    CUSTOMIZE,
}