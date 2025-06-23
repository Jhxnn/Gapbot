package com.Gapbot.Bot;


import com.Gapbot.Models.Participant;
import com.Gapbot.Models.Player;
import com.Gapbot.Repositories.PlayerRepository;
import com.Gapbot.Services.DuoService;
import com.Gapbot.Services.HistoryService;
import com.Gapbot.Services.ParticipantService;
import com.Gapbot.Services.PlayerService;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Component
public class BotListener extends ListenerAdapter {

    @Autowired
    PlayerService playerService;

    @Autowired
    DuoService duoService;

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    HistoryService historyService;

    @Autowired
    ParticipantService participantService;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        String msg = event.getMessage().getContentRaw();
        if (msg.equalsIgnoreCase("!registrar")) {
            if (playerRepository.findById(event.getAuthor().getId()) != null) {
                event.getChannel().sendMessage("❌ Jogador já cadastrado").queue();
                return;
            }
            playerService.createPlayer(event.getAuthor());
            event.getChannel().sendMessage("✅ Jogador registrado com sucesso!").queue();
        }

        if (msg.equalsIgnoreCase("!ranking")){
            List<Player> players = playerRepository.findAll();
            for(Player player : players){
                event.getChannel().sendMessage(player.getNick() + "   -    Winrate: " + player.getWinrate() + "%").queue();
            }
        }

        if (msg.equalsIgnoreCase("!comandos")){
            event.getChannel().sendMessage("!ranking - Lista ranking de jogadores \n!registrar - Registra jogador \n!jogador @nome - Lista dados do jogador mencionado").queue();
        }
        if (msg.startsWith("!jogador")) {
            List<User> mencionados = event.getMessage().getMentions().getUsers();

            if (mencionados.isEmpty()) {
                event.getChannel()
                        .sendMessage("❌ Você precisa mencionar um jogador. Ex: `!jogador @Jhon`")
                        .queue();
                return;
            }

            User playerMention = mencionados.get(0);
            Player player = playerService.findById(playerMention.getId());

            if (player == null) {
                event.getChannel()
                        .sendMessage("⚠️ Jogador ainda não está registrado no sistema.")
                        .queue();
                return;
            }

            String resposta = "**" + player.getNick() + "**\n"
                    + "🏆 Vitórias: " + player.getWins() + "\n"
                    + "❌ Derrotas: " + player.getLoses() + "\n"
                    + "📊 Winrate: " + player.getWinrate() + "%";

            event.getChannel().sendMessage(resposta).queue();
        }


    }
}
