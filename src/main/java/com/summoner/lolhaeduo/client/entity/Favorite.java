package com.summoner.lolhaeduo.client.entity;

import com.summoner.lolhaeduo.domain.duo.enums.QueueType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@RequiredArgsConstructor
public class Favorite {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long accountId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QueueType queueType;

    @Column(nullable = false)
    private String championName;

    private int playCount;

    private int winCount;

    public Favorite(Long accountId, QueueType queueType, String key, int playCount, int winCount) {
        this.accountId = accountId;
        this.queueType = queueType;
        this.championName = key;
        this.playCount = playCount;
        this.winCount = winCount;
    }
}
