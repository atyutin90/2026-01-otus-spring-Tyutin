package ru.otus.hw.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;

@Configuration
public class AppChannelConfig {

    public static final String FILE_CHANNEL = "fileChannel";

    public static final String CATERPILLAR_CHANNEL = "caterpillarChannel";

    public static final String CHRYSALIS_CHANNEL = "chrysalisChannel";

    public static final String BUTTERFLY_CHANNEL = "butterflyChannel";

    @Bean
    public MessageChannel caterpillarChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel chrysalisChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel butterflyChannel() {
        return new DirectChannel();
    }
}
