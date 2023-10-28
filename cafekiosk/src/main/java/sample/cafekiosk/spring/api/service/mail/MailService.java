package sample.cafekiosk.spring.api.service.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sample.cafekiosk.spring.api.domain.history.mail.MailSendHistory;
import sample.cafekiosk.spring.api.domain.history.mail.MailSendHistoryRepository;
import sample.cafekiosk.spring.client.mail.MailSendClient;

@RequiredArgsConstructor
@Service
public class MailService {

    private final MailSendClient mailSendClient;
    private final MailSendHistoryRepository mailSendHistoryRepository;

    public boolean sendMail(String from, String to, String title, String content) {
        boolean result = mailSendClient.sendEmail(from, to, title, content);

        if (result) {
            mailSendHistoryRepository.save(MailSendHistory.builder()
                                                          .fromEmail(from)
                                                          .toEmail(to)
                                                          .subject(title)
                                                          .content(content)
                                                          .build()
            );
            mailSendClient.a();
            mailSendClient.b();
            mailSendClient.c();

            return true;
        }
        return false;

    }
}
