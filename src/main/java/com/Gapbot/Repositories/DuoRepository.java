package com.Gapbot.Repositories;

import com.Gapbot.Models.Duo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DuoRepository extends JpaRepository<Duo, UUID> {
}
