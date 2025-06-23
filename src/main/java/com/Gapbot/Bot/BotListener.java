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

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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
                event.getChannel().sendMessage("❌ Jogador já cadastrado! Deve ser o asafe que ta usando né..").queue();
                return;
            }
            playerService.createPlayer(event.getAuthor());
            event.getChannel().sendMessage("✅ Jogador registrado com sucesso!").queue();
        }

        if (msg.equalsIgnoreCase("!ranking")) {
            List<Player> players = playerRepository.findAll();
            List<Player> ordenados = players.stream()
                    .sorted(Comparator.comparingDouble(Player::getWinrate).reversed())
                    .toList();

            for (Player player : ordenados) {
                event.getChannel().sendMessage(player.getNick() + "   -    Winrate: " + player.getWinrate() + "%").queue();
            }
        }

        if (msg.equalsIgnoreCase("!comandos")) {
            event.getChannel().sendMessage("!ranking - Lista ranking de jogadores \n!registrar - Registra jogador  \n!match @jogador1 @jogador2 @jogador3 @jogador4 - Cria partida \n!jogador @nome - Lista dados do jogador mencionado").queue();
        }
        if (msg.startsWith("!jogador")) {
            List<User> mencionados = event.getMessage().getMentions().getUsers();

            if (mencionados.isEmpty()) {
                event.getChannel()
                        .sendMessage("❌ Você precisa mencionar um jogador. Ex: `!jogador @Jhon`, Meio obvio né...")
                        .queue();
                return;
            }

            User playerMention = mencionados.get(0);

            Optional<Player> optionalPlayer = playerRepository.findById(playerMention.getId());

            if (optionalPlayer.isEmpty()) {
                event.getChannel()
                        .sendMessage("⚠️ Jogador ainda não está registrado no sistema. Muito burro pprt")
                        .queue();
                return;
            }

            Player player = optionalPlayer.get();

            String resposta = "**" + player.getNick() + "**\n"
                    + "🏆 Vitórias: " + player.getWins() + "\n"
                    + "❌ Derrotas: " + player.getLoses() + "\n"
                    + "📊 Winrate: " + player.getWinrate() + "%";

            event.getChannel().sendMessage(resposta).queue();
        }

        if (msg.startsWith("!match")) {
            List<User> mencionados = event.getMessage().getMentions().getUsers();
            if (mencionados.size() < 3) {
                event.getChannel()
                        .sendMessage("❌ Você precisa mencionar os quatro jogdaores... PQP EM`")
                        .queue();
                return;
            }
        }
    }
}
