package com.summoner.lolhaeduo.client.riot.dto.request;

import com.summoner.lolhaeduo.domain.account.enums.AccountRegion;
import com.summoner.lolhaeduo.domain.account.enums.AccountServer;
import com.summoner.lolhaeduo.domain.account.enums.AccountType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class RiotApiAccountInfoRequest {

    @NotNull
    private String accountType;

    @NotNull
    private String accountId;

    @NotNull
    private String accountPassword;

    @NotNull
    private String summonerName;

    @NotNull
    private String tagLine;

    @NotNull
    private String server;

    @NotNull
    private String region;

    private RiotApiAccountInfoRequest(AccountType accountType, String accountId, String accountPassword, String summonerName, String tagLine, AccountServer server, AccountRegion region) {
        this.accountType = accountType.toString();
        this.accountId = accountId;
        this.accountPassword = accountPassword;
        this.summonerName = summonerName;
        this.tagLine = tagLine;
        this.server = server.toString();
        this.region = region.toString();
    }

    public static RiotApiAccountInfoRequest of(AccountType accountType, String accountId, String accountPassword, String summonerName, String tagLine, AccountServer server, AccountRegion region) {
        return new RiotApiAccountInfoRequest(accountType, accountId, accountPassword, summonerName, tagLine, server, region);
    }
}
