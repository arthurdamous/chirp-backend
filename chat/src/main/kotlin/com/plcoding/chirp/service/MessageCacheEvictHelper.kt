package com.plcoding.chirp.service

import com.plcoding.chirp.domain.type.ChatId
import org.springframework.cache.annotation.CacheEvict
import org.springframework.stereotype.Component

@Component
class MessageCacheEvictHelper {

    @CacheEvict(cacheNames = ["messages"], key = "#chatId")
    fun evictMessagesCache(chatId: ChatId) {
        //NO-OP: Let spring handle cache evict
    }
}