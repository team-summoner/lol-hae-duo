package com.summoner.lolhaeduo.client.riot.dto.response;

import com.summoner.lolhaeduo.domain.duo.enums.QueueType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class RiotApiMatchInfoResponse {

    private final int wins;
    private final int losses;
    private final int totalGames;
    private final QueueType queueType;

    private final double winRate;
    private final double averageKill;
    private final double averageDeath;
    private final double averageAssist;

    private final Map<String, Integer> champCountMap;
    private final Map<String, Integer> winCountMap;
}
