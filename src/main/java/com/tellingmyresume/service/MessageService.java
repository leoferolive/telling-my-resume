package com.tellingmyresume.service;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class MessageService {

    private final MessageSource messageSource;

    public MessageService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(String code) {
        return getMessage(code, null);
    }

    public String getMessage(String code, Object[] args) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(code, args, code, locale);
    }

    public String getMessage(String code, Object[] args, Locale locale) {
        return messageSource.getMessage(code, args, code, locale);
    }

    public String getMessageWithFallback(String code, String fallback) {
        return getMessageWithFallback(code, null, fallback);
    }

    public String getMessageWithFallback(String code, Object[] args, String fallback) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(code, args, fallback, locale);
    }
}