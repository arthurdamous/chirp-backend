package com.plcoding.chirp.domain.exception

class SamePasswordException: RuntimeException("The password can not be equal the current one") {
}