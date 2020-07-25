package com.example.notification.service

import org.springframework.kafka.annotation.KafkaListener

class NotificationKafkaConsumer {

    @KafkaListener(topics = ["register"])
    fun receive(payload: String) {
        println("Received payload='$payload'")
    }
}