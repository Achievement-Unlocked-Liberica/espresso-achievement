package espresso.achievement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Optional;

@Component
public class ApiMessageHelper {

    @Autowired
    private MessageSource messageSource;

    public String getMessage(String key, Object[] args) {

        return messageSource.getMessage(key, args, Locale.getDefault());
    }
}