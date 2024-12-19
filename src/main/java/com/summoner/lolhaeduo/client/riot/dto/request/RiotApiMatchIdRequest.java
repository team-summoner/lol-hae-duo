package com.summoner.lolhaeduo.client.riot.dto.request;

import com.summoner.lolhaeduo.domain.account.enums.AccountRegion;
import com.summoner.lolhaeduo.domain.duo.enums.QueueType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RiotApiMatchIdRequest {

    @NotNull
    private QueueType queueType;

    private int playCount;

    @NotNull
    private String region;

    @NotNull
    private String puuid;

    private RiotApiMatchIdRequest(QueueType queueType, int playCount, AccountRegion region, String puuid) {
        this.queueType = queueType;
        this.playCount = playCount;
        this.region = region.toString();
        this.puuid = puuid;
    }

    public static RiotApiMatchIdRequest of(QueueType queueType, int playCount, AccountRegion region, String puuid) {
        return new RiotApiMatchIdRequest(queueType, playCount, region, puuid);
    }
}