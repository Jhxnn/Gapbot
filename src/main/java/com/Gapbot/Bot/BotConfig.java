package com.Gapbot.Bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotConfig {

    @Value("${discord.bot.token}")
    private String token;

    public String getToken() {
        return token;
    }
}
