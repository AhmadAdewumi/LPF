package com.ahmad.ProductFinder.mailing;

import com.sun.xml.messaging.saaj.packaging.mime.MessagingException;

public interface IEmailService {
    void sendEmail(final AbstractEmailContext email) throws MessagingException, jakarta.mail.MessagingException;
}
