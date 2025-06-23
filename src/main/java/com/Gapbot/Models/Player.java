package com.Gapbot.Models;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "players")
public class Player {

    @Id
    @Column(name = "id")
    private String playerId;

    private String nick;

    private int winrate;

    private int wins;

    private int loses;


    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public int getWinrate() {
        return winrate;
    }

    public void setWinrate(int winrate) {
        this.winrate = winrate;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLoses() {
        return loses;
    }

    public void setLoses(int loses) {
        this.loses = loses;
    }
}
