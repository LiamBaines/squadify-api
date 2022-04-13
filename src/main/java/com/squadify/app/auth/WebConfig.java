package com.squadify.app.auth;

import com.squadify.app.squad.SquadDao;
import com.squadify.app.user.SquadifyUserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@RequiredArgsConstructor
public class WebConfig extends WebMvcConfigurerAdapter {

    @Value("${squadify.dev.client.url}")
    private String clientUrl;

    @Value("${squadify.dev.api.url}")
    private String apiUrl;

    private final SquadDao squadDao;
    private final SquadifyUserDao squadifyUserDao;

    @Bean
    AuthenticationInterceptor getAuthenticationInterceptor() {
        return new AuthenticationInterceptor();
//        return new AuthenticationInterceptor(squadDao, squadifyUserDao);
    }

    @Override
    public void addInterceptors (InterceptorRegistry registry) {
        registry.addInterceptor(getAuthenticationInterceptor());

    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins(clientUrl).allowCredentials(true).allowedMethods("GET", "PUT", "POST", "DELETE");
        registry.addMapping("/auth/callback").allowedOriginPatterns("https://accounts.spotify.com/");
    }

    @Bean
    CookieSerializer cookieSerializer() {
        DefaultCookieSerializer defaultCookieSerializer = new DefaultCookieSerializer();
        defaultCookieSerializer.setUseHttpOnlyCookie(false);
        return defaultCookieSerializer;
    }
}
