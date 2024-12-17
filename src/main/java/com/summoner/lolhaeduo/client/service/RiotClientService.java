package com.summoner.lolhaeduo.client.service;

import com.summoner.lolhaeduo.client.dto.*;
import com.summoner.lolhaeduo.client.entity.Favorite;
import com.summoner.lolhaeduo.client.repository.FavoriteRepository;
import com.summoner.lolhaeduo.client.repository.VersionRepository;
import com.summoner.lolhaeduo.client.riot.RiotClient;
import com.summoner.lolhaeduo.common.util.TimeUtil;
import com.summoner.lolhaeduo.domain.account.dto.LinkAccountRequest;
import com.summoner.lolhaeduo.domain.account.entity.Account;
import com.summoner.lolhaeduo.domain.account.entity.AccountDetail;
import com.summoner.lolhaeduo.domain.account.enums.AccountRegion;
import com.summoner.lolhaeduo.domain.account.enums.AccountServer;
import com.summoner.lolhaeduo.domain.duo.enums.QueueType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.summoner.lolhaeduo.domain.duo.enums.QueueType.*;

@Service
@RequiredArgsConstructor
public class RiotClientService implements RiotDataProvider {

    private final RiotClient riotClient;
    private final TimeUtil timeUtil;
    private final VersionRepository versionRepository;
    private final FavoriteRepository favoriteRepository;

    private static final int NUMBER_OF_RECENT_MATCH = 20;
    private static final int PERIOD_OF_RECENT_MATCH = 30;
    private static final int MAX_METHOD_CALL = 100;

    public AccountDetail createAccountDetail(LinkAccountRequest request) {
        PuuidResponse puuidResponse = riotClient.extractPuuid(
                request.getSummonerName(),
                request.getTagLine(),
                request.getServer().getRegion()
        );

        SummonerResponse summonerResponse = riotClient.extractSummonerInfo(
                puuidResponse.getPuuid(),
                request.getServer()
        );

        return AccountDetail.of(
                puuidResponse.getPuuid(),
                summonerResponse.getAccountId(),
                summonerResponse.getId()
        );
    }

    public String updateProfileIconUrl(Account account) {
        SummonerResponse response = riotClient.extractSummonerInfo(account.getAccountDetail().getPuuid(), account.getServer());
        int accountProfileIconId = response.getProfileIconId();

        String latestVersion = versionRepository.findLatestVersion().getVersionNumber();

        return String.format(
                "https://ddragon.leagueoflegends.com/cdn/%s/img/profileicon/%d.png",
                latestVersion, accountProfileIconId
        );
    }

    public RankStats getRankGameStats(String summonerId, AccountServer server) {
        List<LeagueEntryResponse> leagueInfoList = riotClient.extractLeagueInfo(summonerId, server);

        int soloTotalGames = 0, flexTotalGames = 0;
        int soloWins = 0, soloLosses = 0;
        int flexWins = 0, flexLosses = 0;
        String soloTier = "", soloRank = "";
        String flexTier = "", flexRank = "";

        for (LeagueEntryResponse leagueInfo : leagueInfoList) {
            int wins = leagueInfo.getWins();
            int losses = leagueInfo.getLosses();

            if (leagueInfo.getQueueType().equals(SOLO.getQueueType())) {
                soloWins = wins;
                soloLosses = losses;
                soloTotalGames = wins + losses;
                soloTier = leagueInfo.getTier();
                soloRank = leagueInfo.getRank();
            }
            else if (leagueInfo.getQueueType().equals(FLEX.getQueueType())) {
                flexWins = wins;
                flexLosses = losses;
                flexTotalGames = wins + losses;
                flexTier = leagueInfo.getTier();
                flexRank = leagueInfo.getRank();
            }
        }

        double soloWinRate = 0;
        double flexWinRate = 0;
        if ((soloWins + soloLosses) > 0) {
            soloWinRate = (double) soloWins / (soloWins + soloLosses) * 100;
        }
        if ((flexWins + flexLosses) > 0) {
            flexWinRate = (double) flexWins / (flexWins + flexLosses) * 100;
        }

        return new RankStats(soloTotalGames, flexTotalGames, soloWins, soloLosses, soloWinRate, soloTier, soloRank, flexWins, flexLosses, flexWinRate, flexTier, flexRank);
    }

    public List<String> getMatchIds(QueueType queueType, int playCount, AccountRegion region, String puuid) {
        List<String> matchIds = new ArrayList<>();
        int totalRetrieved = 0;

        if (queueType == QUICK) {
            int start = 0;
            while (true) {
                List<String> partialMatchIds = riotClient.extractMatchIds(null, null, QUICK.getQueueId(), null, start, 100, region, puuid);
                if (partialMatchIds == null || partialMatchIds.isEmpty()) {
                    break;
                }

                matchIds.addAll(partialMatchIds);
                totalRetrieved += partialMatchIds.size();
                start += totalRetrieved;
            }
        } else {
            // 랭크 게임의 경우 playCount에 따라 100판 단위로 API 호출
            while (totalRetrieved < playCount) {
                int count = Math.min(MAX_METHOD_CALL, playCount - totalRetrieved);
                List<String> partialMatchIds = riotClient.extractMatchIds(
                        null, null,
                        queueType == SOLO ? SOLO.getQueueId() : FLEX.getQueueId(),

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

    // 수동으로 전적 정보 갱신 시 하루 안에 100번 게임을 할 가능성이 없다고 판단했습니다.
    // 따라서 RiotClient의 getmatchIds를 한번만 호출해도 필요한 모든 정보를 다 조회할 수 있다고 생각해서, 1번만 호출하게 되었습니다.
    public List<String> updateMatchIds(QueueType queueType, LocalDateTime lastUpdatedAt, AccountRegion region, String puuid) {
        long startTime = timeUtil.convertToEpochSeconds(lastUpdatedAt);
        return riotClient.extractMatchIds(
                startTime, null,
                queueType.getQueueId(),
                null, 0, 100, region, puuid
        );
    }

    @Transactional
    public MatchStats getMatchStats(Long accountId, List<String> matchIds, QueueType queueType, String summonerName, String tagLine, AccountRegion region) {
        // 초기화
        int totalKills = 0, totalDeath = 0, totalAssists = 0;
        int winCount = 0;
        int totalGames = matchIds.size();
        Map<String, Integer> champCount = new HashMap<>();
        Map<String, Integer> winCountMap = new HashMap<>();

        for (String matchId : matchIds) {
            FormattedMatchResponse matchResponse = riotClient.getMatchDetails(matchId, summonerName, tagLine, region);

            if (matchResponse != null) {
                String championName = matchResponse.getChampionName();
                champCount.put(championName, champCount.getOrDefault(championName, 0) + 1);

                if (matchResponse.isWin()) {
                    winCount++;
                    winCountMap.put(championName, winCountMap.getOrDefault(championName, 0) + 1);
                }
            }
        }
        double winRate = 0;
        if (queueType == QUICK && totalGames > 0) {
            winRate = (double) winCount / totalGames * 100;
        }

        // 모든 경기에 대한 KDA 계산
        for (String matchId : matchIds) {
            FormattedMatchResponse matchResponse = riotClient.getMatchDetails(matchId, summonerName, tagLine, region);

            if (matchResponse != null) {
                totalKills += matchResponse.getKills();
                totalDeath += matchResponse.getDeaths();
                totalAssists += matchResponse.getAssists();
            }
        }

        double averageKill = (double) totalKills / totalGames;
        double averageDeath = (double) totalDeath / totalGames;
        double averageAssist = (double) totalAssists / totalGames;

        // 동일한 accountId와 queueType에 대한 중복 데이터 발생 방지 : 항상 최신 데이터로 덮어씌움
        favoriteRepository.deleteByAccountIdAndQueueType(accountId, queueType);

        List<Favorite> favorites = champCount.entrySet().stream()
                .map(entry -> new Favorite(accountId, queueType, entry.getKey(), entry.getValue(), winCountMap.getOrDefault(entry.getKey(), 0)))
                .toList();

        favoriteRepository.saveAll(favorites);

        return new MatchStats(
                winCount, totalGames - winCount, totalGames, queueType,
                winRate, averageKill, averageDeath, averageAssist
        );
    }
}
