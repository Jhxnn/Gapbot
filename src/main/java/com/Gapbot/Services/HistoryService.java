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
    public History updateMatch(UUID id, String vencedor){
        History history = historyRepository.findById(id).orElseThrow(()-> new RuntimeException("Não foi possivel encontrar"));

        if("duo1".equals(vencedor)){
            history.setWinnnerDuo(history.getDuo1());
            history.setLoserDuo(history.getDuo2());
            historyRepository.save(history);
            Player player1 = history.getDuo1().getParticipant1().getPlayer();
            Player player2 = history.getDuo1().getParticipant2().getPlayer();
            Player player3 = history.getDuo2().getParticipant1().getPlayer();
            Player player4 = history.getDuo2().getParticipant2().getPlayer();
            player1.setWins(player1.getWins() + 1);
            player2.setWins(player2.getWins() + 1);
            player3.setLoses(player3.getLoses() + 1);
            player4.setLoses(player4.getLoses() + 1);

            playerRepository.save(player1);
            playerRepository.save(player2);
            playerRepository.save(player3);
            playerRepository.save(player4);


            return history;
        }
        history.setWinnnerDuo(history.getDuo2());
        history.setLoserDuo(history.getDuo1());
        historyRepository.save(history);
        Player player1 = history.getDuo1().getParticipant1().getPlayer();
        Player player2 = history.getDuo1().getParticipant2().getPlayer();
        Player player3 = history.getDuo2().getParticipant1().getPlayer();
        Player player4 = history.getDuo2().getParticipant2().getPlayer();
        player1.setLoses(player1.getLoses() + 1);
        player2.setLoses(player2.getLoses() + 1);
        player3.setWins(player3.getWins() + 1);
        player4.setWins(player4.getWins() + 1);

        player1.setWinrate((player1.getWins() + player1.getLoses() == 0) ? 0 :
                player1.getWins() / (player1.getWins() + player1.getLoses()) * 100);

        player2.setWinrate((player2.getWins() + player2.getLoses() == 0) ? 0 :
             player2.getWins() / (player2.getWins() + player2.getLoses()) * 100);

        player3.setWinrate((player3.getWins() + player3.getLoses() == 0) ? 0 :
                 player3.getWins() / (player3.getWins() + player3.getLoses()) * 100);

        player4.setWinrate((player4.getWins() + player4.getLoses() == 0) ? 0 :
                ( player4.getWins() / (player4.getWins() + player4.getLoses()) * 100));

        playerRepository.save(player1);
        playerRepository.save(player2);
        playerRepository.save(player3);
        playerRepository.save(player4);



        return  history;

    }

}
