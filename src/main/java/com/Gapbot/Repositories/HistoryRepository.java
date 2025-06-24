package com.Gapbot.Repositories;

import com.Gapbot.Models.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface HistoryRepository extends JpaRepository<History, UUID> {

    @Query("SELECT h FROM History h " +
            "WHERE h.duo1.participant1.player.id = :playerId " +
            "   OR h.duo1.participant2.player.id = :playerId " +
            "   OR h.duo2.participant1.player.id = :playerId " +
            "   OR h.duo2.participant2.player.id = :playerId")
    List<History> findByPlayerId(String playerId);

}
