package ru.otus.hw.config;

import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.SimpleLocaleContext;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.i18n.LocaleContextResolver;

import java.util.Locale;

import static java.util.Locale.ENGLISH;
import static org.springframework.util.StringUtils.hasText;
import static org.springframework.web.server.adapter.WebHttpHandlerBuilder.LOCALE_CONTEXT_RESOLVER_BEAN_NAME;

@Component(LOCALE_CONTEXT_RESOLVER_BEAN_NAME)
public class RequestParamLocaleContextResolver implements LocaleContextResolver {

    public static final String COOKIE_NAME = "locale";

    private static final Locale DEFAULT_LOCALE = ENGLISH;

    @Override
    public LocaleContext resolveLocaleContext(ServerWebExchange exchange) {
        // Сначала проверяем параметр lang (для текущего запроса)
        var langParam = exchange.getRequest().getQueryParams().getFirst("lang");
        if (langParam != null && hasText(langParam)) {
            var locale = Locale.forLanguageTag(langParam);
            setLocaleToCookie(exchange, locale);
            return new SimpleLocaleContext(locale);
        }

        // Если параметра нет, берем из cookie
        var cookie = exchange.getRequest().getCookies().getFirst(COOKIE_NAME);
        var locale = DEFAULT_LOCALE;
        if (cookie != null && hasText(cookie.getValue())) {
            locale = Locale.forLanguageTag(cookie.getValue());
        }
        return new SimpleLocaleContext(locale);
    }

    @Override
    public void setLocaleContext(ServerWebExchange exchange, LocaleContext context) {
        var locale = (context != null && context.getLocale() != null) ? context.getLocale() : DEFAULT_LOCALE;
        setLocaleToCookie(exchange, locale);
    }

    private void setLocaleToCookie(ServerWebExchange exchange, Locale locale) {
        var responseCookie = ResponseCookie.from(COOKIE_NAME, locale.toLanguageTag())
            .path("/")
            .build();
        exchange.getResponse().addCookie(responseCookie);
    }
}
