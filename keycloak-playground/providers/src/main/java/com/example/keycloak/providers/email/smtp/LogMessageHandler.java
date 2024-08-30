package com.example.keycloak.providers.email.smtp;

import static java.util.Optional.ofNullable;

import com.google.common.io.CharStreams;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import org.subethamail.smtp.MessageContext;
import org.subethamail.smtp.MessageHandler;
import org.subethamail.smtp.RejectException;

@RequiredArgsConstructor
@JBossLog
class LogMessageHandler implements MessageHandler {

  private final MessageContext messageContext;


  @Override
  public void from(String from) throws RejectException {
    log.infof("FROM: %s", from);
  }

  @Override
  public void recipient(String recipient) throws RejectException {
    log.infof("RECIPIENT: %s", recipient);
  }

  @Override
  public void data(InputStream data) throws IOException {
    try {
      String lineSeparator = System.lineSeparator();
      String separator = "= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =";
      String message = convertDataToString(data);
//      MimeMessage mimeMessage = getMimeMessage(data);
      MimeMessage mimeMessage = getMimeMessage(new ByteArrayInputStream(message.getBytes()));
      String emailBody = getEmailBody(mimeMessage);

      Function<Address[], String> toStringAddresses = addresses ->
          Arrays.stream(addresses).map(Address::toString).collect(Collectors.joining());

      log.infof("MAIL DATA%s%s%s%s%s%s",
          lineSeparator,
          separator, lineSeparator,
          message, lineSeparator,
          separator);
      log.infof("mimeMessage :: subject:%s | from:%s | to:%s | body:%s",
          mimeMessage.getSubject(),
          toStringAddresses.apply(mimeMessage.getFrom()),
          toStringAddresses.apply(mimeMessage.getRecipients(RecipientType.TO)),
          emailBody
      );
    } catch (IOException e) {
      throw e;
    } catch (Exception e) {
      throw new IOException(new IllegalArgumentException(e));
    }
  }

  @Override
  public void done() {
    log.info("Finished");
  }

  private MimeMessage getMimeMessage(InputStream inputStream) throws MessagingException {
    Session session = Session.getInstance(new Properties());
    return new MimeMessage(session, inputStream);
  }

  private String convertDataToString(InputStream data) throws IOException {
    try (Reader reader = new InputStreamReader(data)) {
      return CharStreams.toString(reader);
    }
  }

  private String getEmailBody(MimeMessage message) throws MessagingException, IOException {
    String htmlBody = null;
    String textBody = null;
    if (message.getContent() instanceof String) {
      textBody = (String) message.getContent();
    } else if (message.getContent() instanceof Multipart) {
      Multipart multipart = (Multipart) message.getContent();
      for (int i = 0; i < multipart.getCount(); i++) {
        BodyPart bodyPart = multipart.getBodyPart(i);
        if (bodyPart.isMimeType(MediaType.TEXT_HTML)) {
          htmlBody = (String) bodyPart.getContent();
        } else if (bodyPart.isMimeType(MediaType.TEXT_PLAIN)) {
          textBody = (String) bodyPart.getContent();
        }
      }
    }
    log.debugf("emailBody :: html=%s | textBody=%s", htmlBody, textBody);
    return ofNullable(htmlBody).orElse(textBody);
  }

}
