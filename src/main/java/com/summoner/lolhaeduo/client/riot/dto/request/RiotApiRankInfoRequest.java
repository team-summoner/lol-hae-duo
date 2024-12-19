package com.summoner.lolhaeduo.client.riot.dto.request;

import com.summoner.lolhaeduo.domain.account.enums.AccountServer;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RiotApiRankInfoRequest {

    @NotNull
    private String summonerId;

    @NotNull
    private String server;

    private RiotApiRankInfoRequest(String summonerId, AccountServer server) {
        this.summonerId = summonerId;
        this.server = server.toString();
    }

    public static RiotApiRankInfoRequest of(String summonerId, AccountServer server) {
        return new RiotApiRankInfoRequest(summonerId, server);
    }
}