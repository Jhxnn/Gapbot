package com.Gapbot.Services;

import com.Gapbot.Models.Participant;
import com.Gapbot.Models.Player;
import com.Gapbot.Repositories.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class ParticipantService {


    @Autowired
    ParticipantRepository participantRepository;




    public String sortearPersonagem(){

        List<String> campeoes = new ArrayList<>();

        try (InputStream inputStream = ParticipantService.class.getClassLoader().getResourceAsStream("champions.csv");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

                String linha;
             boolean skipHeader = true;

            while ((linha = reader.readLine()) != null) {
                if (skipHeader) {
                    skipHeader = false;
                    continue;
                }
                if (!linha.isBlank()) {
                    campeoes.add(linha.trim());
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo: " + e.getMessage());
            return null;
        }

        if (campeoes.isEmpty()) {
            System.out.println("Nenhum campeão encontrado.");
            return null;
        }

        String escolhido = campeoes.get(new Random().nextInt(campeoes.size()));

        return escolhido;
    }

    public List<Participant> createParticipants(Player player1, Player player2, Player player3, Player player4){

        List<Participant> participants = new ArrayList<>();

        Participant participant1 = new Participant();
        Participant participant2 = new Participant();
        Participant participant3 = new Participant();
        Participant participant4 = new Participant();

        participant1.setChampion(sortearPersonagem());
        participant2.setChampion(sortearPersonagem());
        participant3.setChampion(sortearPersonagem());
        participant4.setChampion(sortearPersonagem());

        participant1.setPlayer(player1);
        participant2.setPlayer(player2);
        participant3.setPlayer(player3);
        participant4.setPlayer(player4);

        participants.add(participant1);
        participants.add(participant2);
        participants.add(participant3);
        participants.add(participant4);

        participantRepository.saveAll(participants);

        return participants;

    }
}
