package com.summoner.lolhaeduo.client.riot.dto.request;

import com.summoner.lolhaeduo.domain.account.enums.AccountRegion;
import com.summoner.lolhaeduo.domain.duo.enums.QueueType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class RiotApiUpdateMatchIdRequest {

    @NotNull
    private QueueType queueType;

    @NotNull
    private LocalDateTime lastUpdatedAt;

    @NotNull
    private String region;

    @NotNull
    private String puuid;

    private RiotApiUpdateMatchIdRequest(QueueType queueType, LocalDateTime lastUpdatedAt, AccountRegion region, String puuid) {
        this.queueType = queueType;
        this.lastUpdatedAt = lastUpdatedAt;
        this.region = region.toString();
        this.puuid = puuid;
    }

    public static RiotApiUpdateMatchIdRequest of(QueueType queueType, LocalDateTime lastUpdatedAt, AccountRegion region, String puuid) {
        return new RiotApiUpdateMatchIdRequest(queueType, lastUpdatedAt, region, puuid);
    }
}
