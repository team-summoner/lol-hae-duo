package com.summoner.lolhaeduo.client.service;

import com.summoner.lolhaeduo.client.dto.*;
import com.summoner.lolhaeduo.client.entity.Version;
import com.summoner.lolhaeduo.client.repository.VersionRepository;
import com.summoner.lolhaeduo.client.riot.RiotClient;
import com.summoner.lolhaeduo.common.util.TimeUtil;
import com.summoner.lolhaeduo.domain.account.entity.Account;
import com.summoner.lolhaeduo.domain.account.enums.AccountRegion;
import com.summoner.lolhaeduo.domain.account.enums.AccountServer;
import com.summoner.lolhaeduo.domain.duo.enums.QueueType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RiotClientService {

    private final RiotClient riotClient;
    private final TimeUtil timeUtil;
    private final VersionRepository versionRepository;

    public RankStats getRankGameStats(Account account, AccountServer server) {
        List<LeagueEntryResponse> leagueInfoList = riotClient.extractLeagueInfo(account.getAccountDetail().getEncryptedSummonerId(), server);

        int soloTotalGames = 0, flexTotalGames = 0;
        int soloWins = 0, soloLosses = 0;
        int flexWins = 0, flexLosses = 0;
        String soloTier = "", soloRank = "";
        String flexTier = "", flexRank = "";

        for (LeagueEntryResponse leagueInfo : leagueInfoList) {
            int wins = leagueInfo.getWins();
            int losses = leagueInfo.getLosses();

            if (leagueInfo.getQueueType().equals("RANKED_SOLO_5x5")) {
                soloWins += wins;
                soloLosses += losses;
                soloTotalGames += wins + losses;
                soloTier = leagueInfo.getTier();
                soloRank = leagueInfo.getRank();
            }
            else if (leagueInfo.getQueueType().equals("RANKED_FLEX_SR")) {
                flexWins += wins;
                flexLosses += losses;
                flexTotalGames += wins + losses;
                flexTier = leagueInfo.getTier();
                flexRank = leagueInfo.getRank();
            }
        }

        double soloWinRate = (soloWins + soloLosses) > 0 ? (double) soloWins / (soloWins + soloLosses) * 100: 0;
        double flexWinRate = (flexWins + flexLosses) > 0 ? (double) flexWins / (flexWins + flexLosses) * 100: 0;

        return new RankStats(soloTotalGames, flexTotalGames, soloWins, soloLosses, soloWinRate, soloTier, soloRank, flexWins, flexLosses, flexWinRate, flexTier, flexRank);
    }

    public List<String> getMatchIds(QueueType queueType, int playCount, AccountRegion region, String puuid) {
        List<String> matchIds = new ArrayList<>();

        if (queueType == QueueType.QUICK) {
            // 일반 게임의 경우 최근 30일간의 최대 20판을 조회
            long startTime = timeUtil.startTimeInEpoch(30);
            matchIds = riotClient.extractMatchIds(startTime, null, 490, null, 0, 20, region, puuid);

        } else {
            // 랭크 게임의 경우 playCount에 따라 100판 단위로 API 호출
            int totalRetrieved = 0;

            while (totalRetrieved < playCount) {
                int count = Math.min(100, playCount - totalRetrieved);
                List<String> partialMatchIds = riotClient.extractMatchIds(
                        null, null,
                        queueType == QueueType.SOLO ? 420 : 440,
                        null, totalRetrieved,
                        count, region, puuid
                );

                if (partialMatchIds == null || partialMatchIds.isEmpty()) {
                    break;
                }

                matchIds.addAll(partialMatchIds);
                totalRetrieved += partialMatchIds.size();

                // 만약 가져온 판 수가 예상보다 적다면 더 이상 호출할 필요가 없음
                if (partialMatchIds.size() < count) {
                    break;
                }
            }
        }

        return matchIds;
    }

    public MatchStats getMatchStats(List<String> matchIds, QueueType queueType, String summonerName, String tagLine, AccountRegion region) {
        // 초기화
        int totalKills = 0, totalDeath = 0, totalAssists = 0;
        int winCount = 0;
        int totalGames = matchIds.size();
        Map<String, Integer> champCount = new HashMap<>();

        // 20 게임 제한을 위한 서브 리스트
        List<String> limitedMatchIds = matchIds.size() > 20 ? matchIds.subList(0, 20) : matchIds;

        for (String matchId : matchIds) {
            FormattedMatchResponse matchResponse = riotClient.getMatchDetails(matchId, summonerName, tagLine, region);

            if (matchResponse != null) {
                champCount.put(matchResponse.getChampionName(), champCount.getOrDefault(matchResponse.getChampionName(), 0) + 1);

                // 일반 게임일 경우 승리 카운트
                if (queueType == QueueType.QUICK && matchResponse.isWin()) {
                    winCount++;
                }
            }
        }

        // 승률 계산 (일반 only)
        double winRate = (queueType == QueueType.QUICK && totalGames > 0) ? (double) winCount / totalGames * 100 : 0;

        // 최근 20경기에 대해 KDA 계산
        for (String matchId : limitedMatchIds) {
            FormattedMatchResponse matchResponse = riotClient.getMatchDetails(matchId, summonerName, tagLine, region);

            if (matchResponse != null) {
                totalKills += matchResponse.getKills();
                totalDeath += matchResponse.getDeaths();
                totalAssists += matchResponse.getAssists();
            }
        }

        double averageKill = !limitedMatchIds.isEmpty() ? (double) totalKills / limitedMatchIds.size() : 0;
        double averageDeath = !limitedMatchIds.isEmpty() ? (double) totalDeath / limitedMatchIds.size() : 0;
        double averageAssist = !limitedMatchIds.isEmpty() ? (double) totalAssists / limitedMatchIds.size() : 0;

        // 챔피언 플레이 횟수 기준으로 정렬
        List<ChampionPlayCount> mostPlayedChamps = champCount.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(3)
                .map(entry -> new ChampionPlayCount(entry.getKey(), entry.getValue()))
                .toList();

        return new MatchStats(winRate, averageKill, averageDeath, averageAssist, mostPlayedChamps, matchIds.size());
    }

    public String getProfileIconUrl(Account linkedAccount) {

        SummonerResponse summonerResponse = riotClient.extractSummonerInfo(
                linkedAccount.getAccountDetail().getPuuid(),
                linkedAccount.getServer()
        );
        int accountProfileIconId = summonerResponse.getProfileIconId();

        String latestVersion = getLatestVersion();

        return String.format(
                "https://ddragon.leagueoflegends.com/cdn/%s/img/profileicon/%d.png",
                latestVersion, accountProfileIconId
        );
    }

    public String getLatestVersion() {
        Version latestVersion = versionRepository.findLatestVersion();
        return latestVersion.getVersionNumber();
    }
}
