package com.Gapbot.Services;

import com.Gapbot.Models.Duo;
import com.Gapbot.Models.History;
import com.Gapbot.Models.Player;
import com.Gapbot.Repositories.HistoryRepository;
import com.Gapbot.Repositories.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class HistoryService {


    @Autowired
    DuoService duoService;

    @Autowired
    HistoryRepository historyRepository;

    @Autowired
    PlayerRepository playerRepository;

    public History createMatch(List<Duo> duos){

        History history = new History();
        history.setDuo1(duos.get(0));
        history.setDuo2(duos.get(1));
        history.setData(LocalDate.now());
        historyRepository.save(history);
        return history;

    }
    public History updateMatch(UUID id, String vencedor) {
        History history = historyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Não foi possível encontrar"));

        Duo vencedorDuo, perdedorDuo;

        if ("duo1".equalsIgnoreCase(vencedor)) {
            vencedorDuo = history.getDuo1();
            perdedorDuo = history.getDuo2();
        } else {
            vencedorDuo = history.getDuo2();
            perdedorDuo = history.getDuo1();
        }

        history.setWinnnerDuo(vencedorDuo);
        history.setLoserDuo(perdedorDuo);
        historyRepository.save(history);

        List<Player> players = List.of(
                vencedorDuo.getParticipant1().getPlayer(),
                vencedorDuo.getParticipant2().getPlayer(),
                perdedorDuo.getParticipant1().getPlayer(),
                perdedorDuo.getParticipant2().getPlayer()
        );

        players.get(0).setWins(players.get(0).getWins() + 1);
        players.get(1).setWins(players.get(1).getWins() + 1);
        players.get(2).setLoses(players.get(2).getLoses() + 1);
        players.get(3).setLoses(players.get(3).getLoses() + 1);

        // Atualiza winrate como int
        for (Player p : players) {
            int wins = p.getWins();
            int losses = p.getLoses();
            int winrate = (wins + losses == 0) ? 0 : (int) ((wins * 100.0) / (wins + losses));
            p.setWinrate(winrate);
            playerRepository.save(p);
        }

        return history;
    }

    public History findById(UUID id){
        return  historyRepository.findById(id).orElseThrow(()-> new RuntimeException("Nao foi possivel encotnrar"));
    }

    public int cancelarPartida(History history){
        if(history.getWinnnerDuo() != null){
            return 1;
        }
        else {
            historyRepository.delete(history);
            return 2;
        }
    }

}
