package com.Gapbot.Bot;


import com.Gapbot.Models.Participant;
import com.Gapbot.Services.DuoService;
import com.Gapbot.Services.HistoryService;
import com.Gapbot.Services.ParticipantService;
import com.Gapbot.Services.PlayerService;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class BotListener extends ListenerAdapter {

    @Autowired
    PlayerService playerService;

    @Autowired
    DuoService duoService;

    @Autowired
    HistoryService historyService;

    @Autowired
    ParticipantService participantService;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        String msg = event.getMessage().getContentRaw();
        if (msg.equalsIgnoreCase("!registrar")) {
            if (playerService.findById(event.getAuthor().getId()) != null) {
                event.getChannel().sendMessage("❌ Jogador já cadastrado").queue();
                return;
            }

            playerService.createPlayer(event.getAuthor());
            event.getChannel().sendMessage("✅ Jogador registrado com sucesso!").queue();
        }



    }
}
