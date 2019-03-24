package uk.ac.warwick.dcs.sherlock.module.web.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import uk.ac.warwick.dcs.sherlock.module.web.configuration.properties.SecurityProperties;
import uk.ac.warwick.dcs.sherlock.module.web.configuration.properties.SetupProperties;
import uk.ac.warwick.dcs.sherlock.module.web.configuration.properties.WebmasterProperties;

import java.util.Locale;

/**
 * Enables localisation support and loads the extra properties for Spring's
 * application.properties files
 */
@Configuration
@EnableConfigurationProperties({
        SecurityProperties.class,
        SetupProperties.class,
        WebmasterProperties.class
})
public class MvcConfig implements WebMvcConfigurer {
    /**
     * Used to determine which locale is currently being used and sets the default
     *
     * @return the session based locale resolver
     */
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver resolver = new SessionLocaleResolver();
        resolver.setDefaultLocale(Locale.ENGLISH);
        return resolver;
    }

    /**
     * An interceptor bean which allows for changing the current locale on every
     * request based on the value of the "lang" parameter appended to a request
     *
     * @return the interceptor object
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        return interceptor;
    }

    /**
     * Adds the above interceptor bean to the application’s interceptor registry
     *
     * @param registry which helps with configuring a list of mapped interceptors
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

    /**
     * Spring delegates the message resolution to this bean and ReloadableResourceBundleMessageSource
     * resolves messages from the resource bundle (messages.properties) for each locale
     *
     * @return the implementation of the MessageSource interface
     */
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    /**
     * To use the validation messages from the properties file during model validation
     * we need to register the message source above to the local validator bean
     *
     * @return the local validator bean with the validation message source set
     */
    @Bean
    @Override
    public Validator getValidator() {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource());
        return bean;
    }
}
