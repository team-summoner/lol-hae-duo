package com.summoner.lolhaeduo.client.service;

import com.summoner.lolhaeduo.client.dto.MatchStats;
import com.summoner.lolhaeduo.client.dto.RankStats;
import com.summoner.lolhaeduo.domain.account.entity.Account;
import com.summoner.lolhaeduo.domain.account.enums.AccountRegion;
import com.summoner.lolhaeduo.domain.account.enums.AccountServer;
import com.summoner.lolhaeduo.domain.duo.enums.QueueType;

import java.util.List;

public interface RiotDataProvider {
    RankStats getRankGameStats(Account account, AccountServer server);
    List<String> getMatchIds(QueueType queueType, int playCount, AccountRegion region, String puuid);
    MatchStats getMatchStats(Long accountId, List<String> matchIds, QueueType queueType, String summonerName, String tagLine, AccountRegion region);
    String getProfileIconUrl(String puuid, AccountServer server);
}
