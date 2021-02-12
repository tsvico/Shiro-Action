package im.zhaojun.system.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import javax.mail.MessagingException

@Service
class MailService {
    @Value("\${spring.mail.username}")
    private lateinit var form: String

    @Autowired
    private lateinit var mailSender: JavaMailSender
    fun sendHTMLMail(to: String, subject: String, content: String) {
        val mimeMessage = mailSender.createMimeMessage()
        val helper: MimeMessageHelper
        try {
            helper = MimeMessageHelper(mimeMessage, true)
            helper.setTo(to)
            helper.setSubject(subject)
            helper.setText(content, true)
            helper.setFrom(form)
        } catch (e: MessagingException) {
            e.printStackTrace()
        }
        mailSender.send(mimeMessage)
    }
}