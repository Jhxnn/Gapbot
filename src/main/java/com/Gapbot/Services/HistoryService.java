package com.Gapbot.Services;

import com.Gapbot.Models.Duo;
import com.Gapbot.Models.History;
import com.Gapbot.Repositories.HistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class HistoryService {


    @Autowired
    DuoService duoService;

    @Autowired
    HistoryRepository historyRepository;

    public History createMatch(List<Duo> duos){

        History history = new History();
        history.setDuo1(duos.get(0));
        history.setDuo2(duos.get(1));

        historyRepository.save(history);
        return history;

    }
    public History updateMatch(UUID id, String vencedor){
        History history = historyRepository.findById(id).orElseThrow(()-> new RuntimeException("Não foi possivel encontrar"));

        if("duo1".equals(vencedor)){
            history.setWinnnerDuo(history.getDuo1());
            history.setLoserDuo(history.getDuo2());
            historyRepository.save(history);
            return history;
        }
        history.setWinnnerDuo(history.getDuo2());
        history.setLoserDuo(history.getDuo1());
        historyRepository.save(history);
        return  history;

    }

}
