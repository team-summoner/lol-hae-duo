package com.summoner.lolhaeduo.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MatchStats {

    private final double winRate;
    private final double averageKill;
    private final double averageDeath;
    private final double averageAssist;
    private final List<ChampionPlayCount> mostPlayedChamps;
    private final int totalGames;
}
