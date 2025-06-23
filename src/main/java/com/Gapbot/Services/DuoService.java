package com.Gapbot.Services;

import com.Gapbot.Models.Duo;
import com.Gapbot.Models.Participant;
import com.Gapbot.Repositories.DuoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DuoService {

    @Autowired
    ParticipantService participantService;


    @Autowired
    DuoRepository duoRepository;


    public List<Duo> createDuos(List<Participant> participants){
        Collections.shuffle(participants);

        Duo duo1 = new Duo();
        duo1.setParticipant1(participants.get(0));
        duo1.setParticipant2(participants.get(1));

        Duo duo2 = new Duo();
        duo2.setParticipant1(participants.get(2));
        duo2.setParticipant2(participants.get(3));

        List<Duo> duos = List.of(duo1, duo2);
        duoRepository.saveAll(duos);

        return duos;

    }
}
