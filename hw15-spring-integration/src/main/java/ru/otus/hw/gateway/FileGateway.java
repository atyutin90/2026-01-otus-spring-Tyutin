package ru.otus.hw.gateway;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import ru.otus.hw.domain.Butterfly;

import java.util.Collection;

import static ru.otus.hw.config.AppChannelConfig.BUTTERFLY_CHANNEL;
import static ru.otus.hw.config.AppChannelConfig.FILE_CHANNEL;

@MessagingGateway
public interface FileGateway {

    @Gateway(requestChannel = FILE_CHANNEL, replyChannel = BUTTERFLY_CHANNEL)
    Collection<Butterfly> process(String fileName);
}
