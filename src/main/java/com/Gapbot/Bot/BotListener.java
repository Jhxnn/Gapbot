package com.Gapbot.Bot;


import com.Gapbot.Models.Duo;
import com.Gapbot.Models.History;
import com.Gapbot.Models.Participant;
import com.Gapbot.Models.Player;
import com.Gapbot.Repositories.HistoryRepository;
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

import java.util.*;

@Component
public class BotListener extends ListenerAdapter {

    @Autowired
    PlayerService playerService;

    @Autowired
    DuoService duoService;

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    HistoryRepository historyRepository;

    @Autowired
    HistoryService historyService;

    @Autowired
    ParticipantService participantService;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        String msg = event.getMessage().getContentRaw();
        if (msg.equalsIgnoreCase("!registrar")) {
            if (playerRepository.findById(event.getAuthor().getId()).isPresent()) {
                event.getChannel().sendMessage("❌ Jogador já cadastrado! Deve ser o Asafe que tá usando né...").queue();
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

            StringBuilder mensagem = new StringBuilder("🏆 **Ranking por Winrate**\n\n");

            for (int i = 0; i < ordenados.size(); i++) {
                Player player = ordenados.get(i);
                mensagem.append(i + 1).append(". ")
                        .append(player.getNick())
                        .append(" - ")
                        .append("Winrate: ")
                        .append(player.getWinrate())
                        .append("%\n");
            }

            event.getChannel().sendMessage(mensagem.toString()).queue();
        }


        if (msg.equalsIgnoreCase("!comandos")) {
            event.getChannel().sendMessage("!ranking - Lista ranking de jogadores \n!registrar - Registra jogador  \n!iniciar @jogador1 @jogador2 @jogador3 @jogador4 - Cria partida \n!jogador @nome - Lista dados do jogador mencionado").queue();
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

            String resposta = "** " + player.getNick() + "**\n"
                    + "🏆 Vitórias: " + player.getWins() + "\n"
                    + "❌ Derrotas: " + player.getLoses() + "\n"
                    + "📊 Winrate: " + player.getWinrate() + "%";

            event.getChannel().sendMessage(resposta).queue();
        }

        if (msg.startsWith("!iniciar")) {
            List<User> mencionados = event.getMessage().getMentions().getUsers();

            if (mencionados.size() < 4) {
                event.getChannel()
                        .sendMessage("❌ Você precisa mencionar os quatro jogadores... PQP EM")
                        .queue();
                return;
            }

            List<Player> players = new ArrayList<>();
            for (User mencionado : mencionados.subList(0, 4)) {
                Optional<Player> optionalPlayer = playerRepository.findById(mencionado.getId());

                if (optionalPlayer.isEmpty()) {
                    event.getChannel()
                            .sendMessage("❌ O Jogador " + mencionado.getName() + " não está cadastrado... BURRO")
                            .queue();
                    return;
                }

                players.add(optionalPlayer.get());
            }

            List<Participant> participantes = participantService.createParticipants(
                    players.get(0), players.get(1), players.get(2), players.get(3)
            );
            List<Duo> duplas = duoService.createDuos(participantes);
            History history = historyService.createMatch(duplas);
            String mensagem = "**📌 ID da Partida:** `" + history.getHistoryId() + "`\n\n" +
                    "🥇 **Duo 1**:\n" +
                    "- **" + history.getDuo1().getParticipant1().getPlayer().getNick() + "** : " + history.getDuo1().getParticipant1().getChampion() + "\n" +
                    "- **" + history.getDuo1().getParticipant2().getPlayer().getNick() + "** : " + history.getDuo1().getParticipant2().getChampion() + "\n\n" +
                    "🥈 **Duo 2**:\n" +
                    "- **" + history.getDuo2().getParticipant1().getPlayer().getNick() + "** : " + history.getDuo2().getParticipant1().getChampion() + "\n" +
                    "- **" + history.getDuo2().getParticipant2().getPlayer().getNick() + "** : " + history.getDuo2().getParticipant2().getChampion();

            event.getChannel().sendMessage(mensagem).queue();
        }

        if (msg.startsWith("!finalizar")) {
            String[] partes = msg.split(" ");

            if (partes.length < 2) {
                event.getChannel().sendMessage("❌ Você precisa informar o ID da partida. Ex: `!finalizar <ID>`").queue();
                return;
            }

            String idPartidaStr = partes[1];

            try {
                UUID idPartida = UUID.fromString(idPartidaStr);

                Optional<History> optionalHistory = historyRepository.findById(idPartida);
                if (optionalHistory.isEmpty()) {
                    event.getChannel().sendMessage("⚠️ Partida não encontrada com esse ID.").queue();
                    return;
                }

                event.getChannel().sendMessage("✅ Partida encontrada! Agora diga quem venceu com: `!ganhador duo1` ou `!ganhador duo2`").queue();


            } catch (IllegalArgumentException e) {
                event.getChannel().sendMessage("❌ ID inválido. Certifique-se de usar um UUID válido.").queue();
            }
        }
        if (msg.startsWith("!historico")) {
            List<History> historicos = historyRepository.findAll();

            if (historicos.isEmpty()) {
                event.getChannel().sendMessage("⚠️ Nenhuma partida encontrada.").queue();
                return;
            }

            StringBuilder mensagem = new StringBuilder("📜 **Histórico de Partidas**\n\n");

            for (History h : historicos) {
                String vencedor = h.getWinnnerDuo() != null
                        ? h.getWinnnerDuo().getParticipant1().getPlayer().getNick() + " & " +
                        h.getWinnnerDuo().getParticipant2().getPlayer().getNick()
                        : "❓ Ainda não definido";

                String perdedor = h.getLoserDuo() != null
                        ? h.getLoserDuo().getParticipant1().getPlayer().getNick() + " & " +
                        h.getLoserDuo().getParticipant2().getPlayer().getNick()
                        : "❓ Ainda não definido";

                mensagem.append("🆔 **ID:** `").append(h.getHistoryId()).append("`\n")
                        .append("🥇 **Vencedor:** ").append(vencedor).append("\n")
                        .append("🥈 **Perdedor:** ").append(perdedor).append("\n\n");
            }

            event.getChannel().sendMessage(mensagem.toString()).queue();
        }

    }
}
