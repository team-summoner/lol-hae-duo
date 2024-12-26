package com.summoner.lolhaeduo.client.riot.dto.request;

import com.summoner.lolhaeduo.domain.account.enums.AccountServer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RiotApiUpdateProfileRequest {

    private String puuid;

    private String server;

    private RiotApiUpdateProfileRequest(String puuid, AccountServer server) {
        this.puuid = puuid;
        this.server = server.toString();
    }

    public static RiotApiUpdateProfileRequest of(String puuid, AccountServer server) {
        return new RiotApiUpdateProfileRequest(puuid, server);
    }
}