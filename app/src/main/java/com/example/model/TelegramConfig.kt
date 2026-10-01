package com.example.model

data class TelegramConfig(
    val botToken: String = "8632489493:AAGVkQCVRq4D9mU2NpxrPP-MMx0LOebaMpc",
    val chatId: String = "6130389200",
    val isSending: Boolean = false,
    val lastSentTime: Long? = null,
    val lastStatusMessage: String? = null,
    val sendSuccess: Boolean? = null
)
