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

import java.time.format.DateTimeFormatter;
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

    private final Map<String, UUID> partidasEmFinalizacao = new HashMap<>();


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
            String comandos = "**📋 Lista de Comandos Disponíveis:**\n\n" +
                    "🧾 `!comandos` — Lista todos os comandos disponíveis\n" +
                    "📝 `!registrar` — Registra o jogador no sistema\n" +
                    "🎮 `!iniciar @j1 @j2 @j3 @j4` — Inicia uma partida com 4 jogadores mencionados\n" +
                    "🏁 `!finalizar <ID>` — Finaliza uma partida informando o ID\n" +
                    "❌ `!cancelar <ID>` — Cancela uma partida informando o ID\n" +
                    "📊 `!ranking` — Mostra o ranking dos jogadores por Winrate\n" +
                    "🔍 `!jogador @user` — Exibe os dados do jogador mencionado\n" +
                    "📜 `!historico` — Lista todo o histórico de partidas\n" +
                    "📉 `!historico @user` — Mostra o histórico do jogador atual";

            event.getChannel().sendMessage(comandos).queue();
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

            if (mencionados.size() > 4) {
                event.getChannel()
                        .sendMessage("❌ O bgl é 2x2 po, quer me fuder me beija")
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
                event.getChannel().sendMessage("❌ Você precisa informar o ID da partida. Ex: `!finalizar <ID>`. Chega dar vontade de se autodetonar aqui pqp").queue();
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

                partidasEmFinalizacao.put(event.getAuthor().getId(), idPartida);

                event.getChannel().sendMessage("✅ Partida encontrada! Agora diga quem venceu com: `!ganhador duo1` ou `!ganhador duo2`").queue();


            } catch (IllegalArgumentException e) {
                event.getChannel().sendMessage("❌ ID inválido. Certifique-se de usar um UUID válido.").queue();
            }
        }
        if (msg.startsWith("!historico")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            List<User> mencionados = event.getMessage().getMentions().getUsers();

            List<History> historicos;

            if (mencionados.isEmpty()) {
                historicos = historyRepository.findAll();
            } else {
                String playerId = mencionados.get(0).getId();
                historicos = historyRepository.findByPlayerId(playerId);
            }

            if (historicos.isEmpty()) {
                event.getChannel().sendMessage("⚠️ Nenhuma partida encontrada para esse jogador.").queue();
                return;
            }

            StringBuilder mensagem = new StringBuilder("📜 **Histórico de Partidas**\n\n");

            for (History h : historicos) {
                String vencedor = h.getWinnnerDuo() != null
                        ? "**" + h.getWinnnerDuo().getParticipant1().getPlayer().getNick() + "** (" + h.getWinnnerDuo().getParticipant1().getChampion() + ") & " +
                        "**" + h.getWinnnerDuo().getParticipant2().getPlayer().getNick() + "** (" + h.getWinnnerDuo().getParticipant2().getChampion() + ")"
                        : "❓ Ainda não definido";

                String perdedor = h.getLoserDuo() != null
                        ? "**" + h.getLoserDuo().getParticipant1().getPlayer().getNick() + "** (" + h.getLoserDuo().getParticipant1().getChampion() + ") & " +
                        "**" + h.getLoserDuo().getParticipant2().getPlayer().getNick() + "** (" + h.getLoserDuo().getParticipant2().getChampion() + ")"
                        : "❓ Ainda não definido";

                String dataFormatada = h.getData() != null
                        ? h.getData().format(formatter)
                        : "Data não disponível";

                mensagem.append("🆔 **ID:** `").append(h.getHistoryId()).append("`\n")
                        .append("🗓️ **Data:** ").append(dataFormatada).append("\n")
                        .append("🥇 **Vencedor:** ").append(vencedor).append("\n")
                        .append("🥈 **Perdedor:** ").append(perdedor).append("\n\n");

            }

            event.getChannel().sendMessage(mensagem.toString()).queue();
        }


        if(msg.contains("asafe")){
            event.getChannel().sendMessage("Por favor não diga este nome novamente, sujeito a banimento!").queue();
        }

        if (msg.startsWith("!ganhador")) {
            if (!partidasEmFinalizacao.containsKey(event.getAuthor().getId())) {
                event.getChannel().sendMessage("⚠️ Você precisa usar `!finalizar <ID>` antes de informar o ganhador. Precoce").queue();
                return;
            }

            UUID partidaId = partidasEmFinalizacao.get(event.getAuthor().getId());
            String[] partes = msg.split(" ");
            if (partes.length < 2 || (!partes[1].equalsIgnoreCase("duo1") && !partes[1].equalsIgnoreCase("duo2"))) {
                event.getChannel().sendMessage("❌ Formato inválido. Use: `!ganhador duo1` ou `!ganhador duo2`. AAAAAAAAAAAA").queue();
                return;
            }

            String vencedor = partes[1].toLowerCase();

            try {
                History updatedHistory = historyService.updateMatch(partidaId, vencedor);
                partidasEmFinalizacao.remove(event.getAuthor().getId());

                event.getChannel().sendMessage("✅ Vencedor registrado com sucesso! 🏆").queue();

            } catch (Exception e) {
                event.getChannel().sendMessage("❌ Erro ao atualizar o resultado da partida: " + e.getMessage()).queue();
            }
        }
        if (msg.startsWith("!cancelar")) {
            String[] partes = msg.split(" ");

            if (partes.length < 2) {
                event.getChannel().sendMessage("❌ Você precisa informar o ID da partida. Ex: `!cancelar <ID>. ta se fazendo de pão pra ganhar linguiça`").queue();
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
                int verifica = historyService.cancelarPartida(historyService.findById(idPartida));
                if(verifica == 1){
                    event.getChannel().sendMessage("❌ A partida já foi finalizada. Espertinho, ta querendo farmar winrate!").queue();
                }
                if(verifica == 2){
                    event.getChannel().sendMessage("✅ Partida cancelada!").queue();
                }


            } catch (IllegalArgumentException e) {
                event.getChannel().sendMessage("❌ ID inválido. Certifique-se de usar um UUID válido.").queue();
            }
        }



    }

}
