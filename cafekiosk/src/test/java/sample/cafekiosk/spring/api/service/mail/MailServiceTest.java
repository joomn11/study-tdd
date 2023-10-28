package sample.cafekiosk.spring.api.service.mail;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import sample.cafekiosk.spring.api.domain.history.mail.MailSendHistory;
import sample.cafekiosk.spring.api.domain.history.mail.MailSendHistoryRepository;
import sample.cafekiosk.spring.client.mail.MailSendClient;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @Mock
//    @Spy
    private MailSendClient mailSendClient;

    @Mock
    private MailSendHistoryRepository mailSendHistoryRepository;

    @InjectMocks
    private MailService mailService;

    @Test
    void sendMail() {
        // given

//        Mockito.when(mailSendClient.sendEmail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
//               .thenReturn(true);

        // for @Spy
//        Mockito.doReturn(true)
//               .when(mailSendClient)
//               .sendEmail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

        BDDMockito.given(mailSendClient.sendEmail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                  .willReturn(true);

        // when
        boolean result = mailService.sendMail("", "", "", "");

        // then
        Assertions.assertThat(result).isTrue();
        Mockito.verify(mailSendHistoryRepository, Mockito.times(1)).save(Mockito.any(MailSendHistory.class));
    }
}