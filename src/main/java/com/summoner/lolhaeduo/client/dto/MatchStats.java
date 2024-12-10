package com.summoner.lolhaeduo.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MatchStats {

    private final double winRate;
    private final double averageKill;
    private final double averageDeath;
    private final double averageAssist;
    private final int totalGames;
}
