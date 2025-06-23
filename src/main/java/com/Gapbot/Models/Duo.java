package com.Gapbot.Models;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "duos")
public class Duo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID pairId;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id",name = "participante_1")
    private Participant participant1;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id",name = "participante_2")
    private Participant participant2;

    public UUID getPairId() {
        return pairId;
    }

    public void setPairId(UUID pairId) {
        this.pairId = pairId;
    }

    public Participant getParticipant1() {
        return participant1;
    }

    public void setParticipant1(Participant participant1) {
        this.participant1 = participant1;
    }

    public Participant getParticipant2() {
        return participant2;
    }

    public void setParticipant2(Participant participant2) {
        this.participant2 = participant2;
    }
}
