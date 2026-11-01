package uk.co.scattercode.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

/**
 * The Java equivalent of what was {@code iban-web-servlet.xml}. Spring Boot
 * configures the rest: the dispatcher servlet, the Thymeleaf view resolver
 * in place of the JSP one, static resources under {@code /resources/**} (see
 * {@code application.properties}) and UTF-8 request and response encoding.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Saves a locale change in a cookie. The bean name is the one Spring MVC
     * looks up, which makes Spring Boot's own locale resolver back off.
     */
    @Bean
    public LocaleResolver localeResolver() {
        return new CookieLocaleResolver();
    }

    /**
     * Changes the locale when a 'locale' request parameter is sent; e.g.
     * {@code /?locale=de}.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LocaleChangeInterceptor());
    }

    /**
     * Forwards requests for "/" to the "welcome" view.
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("welcome");
    }
}
