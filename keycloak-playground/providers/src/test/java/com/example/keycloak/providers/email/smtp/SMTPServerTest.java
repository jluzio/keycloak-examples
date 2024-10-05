package com.example.keycloak.providers.email.smtp;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.Maps;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Map;
import java.util.Properties;
import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;
import org.subethamail.smtp.MessageContext;
import org.subethamail.smtp.MessageHandler;
import org.subethamail.smtp.MessageHandlerFactory;
import org.subethamail.smtp.RejectException;
import org.subethamail.smtp.TooMuchDataException;
import org.subethamail.smtp.server.SMTPServer;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@JBossLog
class SMTPServerTest {

  @Test
  void test() {
    Map<String, String> mailsContextData = Maps.newConcurrentMap();
    TestMessageHandlerFactory messageHandlerFactory = new TestMessageHandlerFactory(
        mailsContextData);
    SMTPServer smtpServer = SMTPServer.port(2500)
        .messageHandlerFactory(messageHandlerFactory)
        .build();
    smtpServer.start();

    Properties mailProperties = new Properties();
    mailProperties.put("mail.smtp.host", "localhost");
    mailProperties.put("mail.smtp.port", "2500");

    int messageCount = 1000;
    Flux.range(0, messageCount)
        .publishOn(Schedulers.boundedElastic())
        .log()
        .flatMap(this::processValue)
        .publishOn(Schedulers.boundedElastic())
        .log()
        .doOnNext(id -> {
          try {
            sendMail(id, mailProperties);
          } catch (Exception e) {
            throw Exceptions.propagate(e);
          }
        })
        .collectList()
        .block();

    smtpServer.stop();

    assertThat(mailsContextData)
        .hasSize(messageCount);
    log.debugf("data: %s", mailsContextData);
  }

  private Mono<Integer> processValue(Integer value) {
    return Mono.just(value)
        .delayElement(Duration.ofMillis(RandomUtils.nextInt(0, 50)));
  }

  private void sendMail(Integer id, Properties mailProperties) throws MessagingException {
    Session session = Session.getInstance(mailProperties);

    Message message = new MimeMessage(session);
    message.setFrom(new InternetAddress("from@gmail.com"));
    message.setRecipients(
        Message.RecipientType.TO, InternetAddress.parse("to@gmail.com"));
    message.setSubject(id.toString());

    String msg = "This is my first email using JavaMailer";

    MimeBodyPart mimeBodyPart = new MimeBodyPart();
    mimeBodyPart.setContent(msg, "text/html");

    Multipart multipart = new MimeMultipart();
    multipart.addBodyPart(mimeBodyPart);

    message.setContent(multipart);

    Transport.send(message);
  }

  @RequiredArgsConstructor
  static class TestMessageHandlerFactory implements MessageHandlerFactory {

    private final Map<String, String> contextData;

    public MessageHandler create(MessageContext ctx) {
      return new TestMessageHandler();
    }

    class TestMessageHandler implements MessageHandler {

      @Override
      public void from(String from) throws RejectException {
        // not required
      }

      @Override
      public void recipient(String recipient) throws RejectException {
        // not required
      }

      @Override
      public String data(InputStream inputStream) throws RejectException, TooMuchDataException, IOException {
        try {
          Session session = Session.getInstance(new Properties());
          MimeMessage mimeMessage = new MimeMessage(session, inputStream);
          contextData.put(mimeMessage.getSubject(), mimeMessage.getMessageID());
          return null;
        } catch (MessagingException e) {
          throw new IOException(e);
        }
      }

      @Override
      public void done() {
        // not required
      }
    }
  }

}