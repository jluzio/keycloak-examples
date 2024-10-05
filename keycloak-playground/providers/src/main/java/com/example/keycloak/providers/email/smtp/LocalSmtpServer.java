package com.example.keycloak.providers.email.smtp;

import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import org.keycloak.Config.Scope;
import org.keycloak.models.KeycloakSessionFactory;
import org.subethamail.smtp.MessageHandlerFactory;
import org.subethamail.smtp.server.SMTPServer;

@JBossLog
@RequiredArgsConstructor
public class LocalSmtpServer {

  public static final String DEFAULT_SMTP_SERVER_CONFIG_SCOPE = "smtp.server";
  private final Scope scopedConfig;
  private final KeycloakSessionFactory sessionFactory;
  private SMTPServer smtpServer;

  public void start() {
    log.infof("%s.init :: SMTP Server :: starting", getClass().getSimpleName());
    Integer port = scopedConfig.getInt("port", 25);
    boolean debug = scopedConfig.getBoolean("debug", false);
    log.infof("%s.init :: SMTP Server :: using port=%s | debug=%s",
        getClass().getSimpleName(), port, debug);

    MessageHandlerFactory handlerFactory = new LogMessageHandlerFactory();
//    MessageHandlerFactory handlerFactory = debug
//        ? new LogMessageHandlerFactory()
//        : new LogMessageHandlerFactory();
    smtpServer = SMTPServer.port(port)
        .messageHandlerFactory(handlerFactory)
        .build();
    smtpServer.start();
  }

  public void stop() {
    log.infof("%s.close :: SMTP Server :: stopping", getClass().getSimpleName());
    smtpServer.stop();
  }

}
