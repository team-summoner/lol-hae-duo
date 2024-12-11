package com.summoner.lolhaeduo.domain.account.entity;

import com.summoner.lolhaeduo.domain.account.entity.dataStorage.FlexRankData;
import com.summoner.lolhaeduo.domain.account.entity.dataStorage.QuickGameData;
import com.summoner.lolhaeduo.domain.account.entity.dataStorage.SoloRankData;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@RequiredArgsConstructor
public class AccountGameData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private Account account;

    @OneToOne(fetch = FetchType.LAZY)
    private QuickGameData quickGameData;

    @OneToOne(fetch = FetchType.LAZY)
    private SoloRankData soloRankData;

    @OneToOne(fetch = FetchType.LAZY)
    private FlexRankData flexRankData;

    private LocalDateTime lastUpdated;
}
