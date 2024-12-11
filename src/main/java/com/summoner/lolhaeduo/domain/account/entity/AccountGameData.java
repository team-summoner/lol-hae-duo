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

    private String profileIconIdUrl;

    @OneToOne(fetch = FetchType.LAZY)
    private QuickGameData quickGameData;

    @OneToOne(fetch = FetchType.LAZY)
    private SoloRankData soloRankData;

    @OneToOne(fetch = FetchType.LAZY)
    private FlexRankData flexRankData;

    private LocalDateTime lastUpdated;

    private AccountGameData(String profileIconIdUrl, QuickGameData quickGameData, SoloRankData soloRankData, FlexRankData flexRankData) {
        this.profileIconIdUrl = profileIconIdUrl;
        this.quickGameData = quickGameData;
        this.soloRankData = soloRankData;
        this.flexRankData = flexRankData;
        this.lastUpdated = LocalDateTime.now();
    }

    public static AccountGameData of(String profileIconIdUrl, QuickGameData quickGameData, SoloRankData soloRankData, FlexRankData flexRankData) {
        return new AccountGameData(profileIconIdUrl, quickGameData, soloRankData, flexRankData);
    }

    public void update(String profileIconIdUrl, QuickGameData quickGameData, SoloRankData soloRankData, FlexRankData flexRankData) {
        this.profileIconIdUrl = profileIconIdUrl;
        this.quickGameData = quickGameData;
        this.soloRankData = soloRankData;
        this.flexRankData = flexRankData;
        this.lastUpdated = LocalDateTime.now();
    }
}
