package com.plcoding.chirp.domain.exception

class RateLimiteException(
    val resetsInSeconds: Long,
): RuntimeException("Rate limit exceeded. Please try again in $resetsInSeconds seconds") {
}