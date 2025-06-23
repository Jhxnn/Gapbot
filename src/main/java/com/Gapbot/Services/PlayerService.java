package com.Gapbot.Services;


import com.Gapbot.Models.Player;
import com.Gapbot.Repositories.PlayerRepository;
import net.dv8tion.jda.api.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {

    @Autowired
    PlayerRepository playerRepository;

    public Player createPlayer(User user){
        Player player = new Player();
        player.setPlayerId(user.getId());
        player.setNick(user.getName());
        player.setLoses(0);
        player.setWins(0);
        player.setWinrate(0);
        return playerRepository.save(player);
    }

    public Player findById(String id){
        return playerRepository.findById(id).orElseThrow(()-> new RuntimeException("Não foi possivel achar o usuario"));
    }
}
