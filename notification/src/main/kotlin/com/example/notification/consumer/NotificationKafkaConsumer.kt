package com.example.notification.consumer

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender

class NotificationKafkaConsumer(private val javaMailSender: JavaMailSender) {

    @KafkaListener(topics = ["register"])
    fun receive(to: String) {
        val message = SimpleMailMessage()
        message.setFrom("noreply@baeldung.com")
        message.setTo(to)
        message.setSubject("Welcome")
        message.setText("Hello from Spring microservice")
        javaMailSender.send(message)
    }
}