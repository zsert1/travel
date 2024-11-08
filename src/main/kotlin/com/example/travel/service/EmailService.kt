package com.example.travel.service

import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import jakarta.mail.internet.MimeMessage
@Service
class EmailService(private val mailSender: JavaMailSender) {

    fun sendEmail(to: String, subject: String, text: String) {
      
        val message: MimeMessage = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true)
        
        helper.setTo(to)
        helper.setSubject(subject)
        helper.setText(text, true)
        
        mailSender.send(message)
    }
}
