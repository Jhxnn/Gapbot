package com.Gapbot.Models;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "history")
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID historyId;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id", name = "duo_1")
    private Duo duo1;


    @ManyToOne
    @JoinColumn(referencedColumnName = "id", name = "duo_2")
    private Duo duo2;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id", name = "duo_winner")
    private Duo winnnerDuo;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id", name = "duo_loser")
    private Duo loserDuo;

    private LocalDate data;

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public UUID getHistoryId() {
        return historyId;
    }

    public void setHistoryId(UUID historyId) {
        this.historyId = historyId;
    }

    public Duo getDuo1() {
        return duo1;
    }

    public void setDuo1(Duo duo1) {
        this.duo1 = duo1;
    }

    public Duo getDuo2() {
        return duo2;
    }

    public void setDuo2(Duo duo2) {
        this.duo2 = duo2;
    }

    public Duo getWinnnerDuo() {
        return winnnerDuo;
    }

    public void setWinnnerDuo(Duo winnnerDuo) {
        this.winnnerDuo = winnnerDuo;
    }

    public Duo getLoserDuo() {
        return loserDuo;
    }

    public void setLoserDuo(Duo loserDuo) {
        this.loserDuo = loserDuo;
    }
}
