package com.summoner.lolhaeduo.client.riot.dto.request;

import com.summoner.lolhaeduo.domain.account.enums.AccountRegion;
import com.summoner.lolhaeduo.domain.duo.enums.QueueType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class RiotApiMatchInfoRequest {

    @NotNull
    private Long accountId;

    private List<String> matchIds;

    @NotNull
    private QueueType queueType;

    @NotNull
    private String summonerName;

    @NotNull
    private String tagLine;

    @NotNull
    private String region;

    private RiotApiMatchInfoRequest(Long accountId, List<String> matchIds, QueueType queueType, String summonerName, String tagLine, AccountRegion region) {
        this.accountId = accountId;
        this.matchIds = matchIds;
        this.queueType = queueType;
        this.summonerName = summonerName;
        this.tagLine = tagLine;
        this.region = region.toString();
    }

    public static RiotApiMatchInfoRequest of(Long accountId, List<String> matchIds, QueueType queueType, String summonerName, String tagLine, AccountRegion region) {
        return new RiotApiMatchInfoRequest(accountId, matchIds, queueType, summonerName, tagLine, region);
    }
}
