package com.plcoding.chirp.domain.exception

class InvalidChatSizeException : RuntimeException(
    "There must be at least 2 participants in a chat"
)