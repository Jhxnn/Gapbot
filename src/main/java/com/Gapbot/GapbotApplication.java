package com.Gapbot;

import com.Gapbot.Bot.BotListener;
import jakarta.annotation.PostConstruct;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GapbotApplication {

	private final BotListener discordListener;

	public GapbotApplication(BotListener discordListener) {
		this.discordListener = discordListener;
	}

	public static void main(String[] args) {
		SpringApplication.run(GapbotApplication.class, args);
	}

	@PostConstruct
	public void startBot() {
		JDABuilder.createDefault("SEU_TOKEN_AQUI")
				.setActivity(Activity.playing("com Java"))
				.addEventListeners(discordListener)
				.build();
	}

}
