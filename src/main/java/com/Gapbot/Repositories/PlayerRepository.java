package com.Gapbot.Repositories;

import com.Gapbot.Models.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PlayerRepository extends JpaRepository<Player, String> {
}
