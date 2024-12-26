package com.summoner.lolhaeduo.domain.account.service;

import com.summoner.lolhaeduo.client.riot.dto.request.*;
import com.summoner.lolhaeduo.client.riot.dto.response.RiotApiMatchInfoResponse;
import com.summoner.lolhaeduo.client.riot.dto.response.RiotApiRankInfoResponse;
import com.summoner.lolhaeduo.client.riot.util.RiotClientUtil;
import com.summoner.lolhaeduo.common.event.AccountGameDataEvent;
import com.summoner.lolhaeduo.domain.account.entity.Account;
import com.summoner.lolhaeduo.domain.account.entity.AccountGameData;
import com.summoner.lolhaeduo.domain.account.entity.dataStorage.FlexRankData;
import com.summoner.lolhaeduo.domain.account.entity.dataStorage.QuickGameData;
import com.summoner.lolhaeduo.domain.account.entity.dataStorage.SoloRankData;
import com.summoner.lolhaeduo.domain.account.repository.AccountGameDataRepository;
import com.summoner.lolhaeduo.domain.account.repository.AccountRepository;
import com.summoner.lolhaeduo.domain.account.repository.dataStorage.FlexRankDataRepository;
import com.summoner.lolhaeduo.domain.account.repository.dataStorage.QuickGameDataRepository;
import com.summoner.lolhaeduo.domain.account.repository.dataStorage.SoloRankDataRepository;
import com.summoner.lolhaeduo.domain.duo.entity.Kda;
import com.summoner.lolhaeduo.domain.duo.enums.QueueType;
import com.summoner.lolhaeduo.domain.favorite.entity.Favorite;
import com.summoner.lolhaeduo.domain.favorite.repository.FavoriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.summoner.lolhaeduo.domain.duo.enums.QueueType.*;

@Service
@RequiredArgsConstructor
public class AccountGameDataService {

    private final RiotClientUtil riotClientUtil;
    private final AccountGameDataRepository accountGameDataRepository;
    private final QuickGameDataRepository quickGameDataRepository;
    private final SoloRankDataRepository soloRankDataRepository;
    private final FlexRankDataRepository flexRankDataRepository;
    private final AccountRepository accountRepository;
    private final FavoriteRepository favoriteRepository;

    @Async
    @EventListener
    @Transactional
    public void createAccountGameDataEvent(AccountGameDataEvent event) {
        Account account = accountRepository.findById(event.getAccountId()).orElseThrow(
                () -> new IllegalArgumentException("Account not found")
        );

        // 랭크 정보 가져오기
        RiotApiRankInfoResponse rankStats = riotClientUtil.getRankInfos(
                RiotApiRankInfoRequest.of(account.getAccountDetail().getEncryptedSummonerId(), account.getServer())
        );

        // 매치 아이디 가져오기
        // 일반 게임의 경우 매치 수 정보를 얻을 수 없으므로 입력값을 받지 않습니다.
        List<String> quickMatchIds = riotClientUtil.getMatchIds(
                RiotApiMatchIdRequest.of(QUICK, 0, account.getRegion(), account.getAccountDetail().getPuuid())
        );
        List<String> soloMatchIds = riotClientUtil.getMatchIds(
                RiotApiMatchIdRequest.of(SOLO, rankStats.getSoloTotalGames(), account.getRegion(), account.getAccountDetail().getPuuid())
        );
        List<String> flexMatchIds = riotClientUtil.getMatchIds(
                RiotApiMatchIdRequest.of(FLEX, rankStats.getFlexTotalGames(), account.getRegion(), account.getAccountDetail().getPuuid())
        );

        // 매치 상세 정보 가져오기
        QuickGameData quickGameData = null;
        if (!quickMatchIds.isEmpty()) {
            RiotApiMatchInfoResponse quickStats = riotClientUtil.getMatchInfos(
                    RiotApiMatchInfoRequest.of(account.getId(), quickMatchIds, QUICK, account.getSummonerName(), account.getTagLine(), account.getRegion())
            );

            updateFavorite(account.getId(), QUICK, quickStats.getChampCountMap(), quickStats.getWinCountMap());

            quickGameData = QuickGameData.of(
                    quickStats.getWins(), quickStats.getTotalGames(),
                    Kda.of(quickStats.getAverageKill(), quickStats.getAverageAssist(), quickStats.getAverageDeath())
            );
        }

        SoloRankData soloRankData = null;
        if (!soloMatchIds.isEmpty()) {
            RiotApiMatchInfoResponse soloStats = riotClientUtil.getMatchInfos(
                    RiotApiMatchInfoRequest.of(account.getId(), soloMatchIds, SOLO, account.getSummonerName(), account.getTagLine(), account.getRegion())
            );

            updateFavorite(account.getId(), SOLO, soloStats.getChampCountMap(), soloStats.getWinCountMap());

            soloRankData = SoloRankData.of(
                    rankStats.getSoloTier(), rankStats.getSoloRank(),
                    soloStats.getWins(), soloStats.getTotalGames(),
                    Kda.of(soloStats.getAverageKill(), soloStats.getAverageAssist(), soloStats.getAverageDeath())
            );
        }

        FlexRankData flexRankData = null;
        if (!flexMatchIds.isEmpty()) {
            RiotApiMatchInfoResponse flexStats = riotClientUtil.getMatchInfos(
                    RiotApiMatchInfoRequest.of(account.getId(), flexMatchIds, FLEX, account.getSummonerName(), account.getTagLine(), account.getRegion())
            );

            updateFavorite(account.getId(), FLEX, flexStats.getChampCountMap(), flexStats.getWinCountMap());

            flexRankData = FlexRankData.of(
                    rankStats.getFlexTier(), rankStats.getFlexRank(),
                    flexStats.getWins(), flexStats.getTotalGames(),
                    Kda.of(flexStats.getAverageKill(), flexStats.getAverageAssist(), flexStats.getAverageDeath())
            );
        }

        if (quickGameData == null && soloRankData == null && flexRankData == null) {
            return;
        }

        String iconUrl = riotClientUtil.getProfileIcon(
                RiotApiUpdateProfileRequest.of(account.getAccountDetail().getPuuid(), account.getServer())
        );

        AccountGameData newAccountGameData = AccountGameData.of(iconUrl, quickGameData, soloRankData, flexRankData);
        account.linkAccountGameData(newAccountGameData);

        accountGameDataRepository.save(newAccountGameData);
    }

    // update를 하기 위해서는 updatedAt을 사용해서 추가 로직이 필요함

    @Transactional
    public void updateAccountGameData(Account account) {
        // 1. AccountGameData를 불러온다.
        AccountGameData recentData = account.getAccountGameData();
        QuickGameData quickGameData = recentData.getQuickGameData();
        SoloRankData soloRankData = recentData.getSoloRankData();
        FlexRankData flexRankData = recentData.getFlexRankData();

        // 2. 가장 최근 업데이트 날짜를 확인한다.
        LocalDateTime modifiedAt = recentData.getModifiedAt();

        // 3. 프로필 아이콘을 업데이트한다.
        String updatedProfileIconUrl = riotClientUtil.getProfileIcon(
                RiotApiUpdateProfileRequest.of(account.getAccountDetail().getPuuid(), account.getServer())
        );

        // 4. 반영해야할 매치 ID가 있는지 조회한다.
        RiotApiRankInfoResponse rankStats = riotClientUtil.getRankInfos(
                RiotApiRankInfoRequest.of(account.getAccountDetail().getEncryptedSummonerId(), account.getServer())
        );

        List<String> quickMatchIds = riotClientUtil.updateMatchIds(
                RiotApiUpdateMatchIdRequest.of(QUICK, modifiedAt, account.getRegion(), account.getAccountDetail().getPuuid())
        );
        List<String> soloMatchIds = riotClientUtil.updateMatchIds(
                RiotApiUpdateMatchIdRequest.of(SOLO, modifiedAt, account.getRegion(), account.getAccountDetail().getPuuid())
        );
        List<String> flexMatchIds = riotClientUtil.updateMatchIds(
                RiotApiUpdateMatchIdRequest.of(FLEX, modifiedAt, account.getRegion(), account.getAccountDetail().getPuuid())
        );


        if (!quickMatchIds.isEmpty()) {
            RiotApiMatchInfoResponse quickStats = riotClientUtil.getMatchInfos(
                    RiotApiMatchInfoRequest.of(account.getId(), quickMatchIds, QUICK, account.getSummonerName(), account.getTagLine(), account.getRegion())
            );

            updateFavorite(account.getId(), QUICK, quickStats.getChampCountMap(), quickStats.getWinCountMap());

            quickGameData.update(
                    quickStats.getWins(), quickStats.getTotalGames(),
                    Kda.of(quickStats.getAverageKill(), quickStats.getAverageAssist(), quickStats.getAverageDeath())
            );
        }

        if (!soloMatchIds.isEmpty()) {
            RiotApiMatchInfoResponse soloStats = riotClientUtil.getMatchInfos(
                    RiotApiMatchInfoRequest.of(account.getId(), soloMatchIds, SOLO, account.getSummonerName(), account.getTagLine(), account.getRegion())
            );

            updateFavorite(account.getId(), SOLO, soloStats.getChampCountMap(), soloStats.getWinCountMap());

            soloRankData.update(
                    rankStats.getSoloTier(), rankStats.getSoloRank(),
                    soloRankData.getWins() + soloStats.getWins(),
                    soloRankData.getTotalGames() + soloStats.getTotalGames(),
                    updateKda(soloRankData.getKda(), soloRankData.getTotalGames(), soloStats)
            );
        }

        if (!flexMatchIds.isEmpty()) {
            RiotApiMatchInfoResponse flexStats = riotClientUtil.getMatchInfos(
                    RiotApiMatchInfoRequest.of(account.getId(), flexMatchIds, FLEX, account.getSummonerName(), account.getTagLine(), account.getRegion())
            );

            updateFavorite(account.getId(), FLEX, flexStats.getChampCountMap(), flexStats.getWinCountMap());

            flexRankData.update(
                    rankStats.getFlexTier(), rankStats.getFlexRank(),
                    flexRankData.getWins() + flexStats.getWins(),
                    flexRankData.getTotalGames() + flexStats.getTotalGames(),
                    updateKda(flexRankData.getKda(), flexRankData.getTotalGames(), flexStats)
            );
        }

        // 5. AccountGameData 값을 업데이트한다.
        recentData.update(
                updatedProfileIconUrl,
                quickGameDataRepository.save(quickGameData),
                soloRankDataRepository.save(soloRankData),
                flexRankDataRepository.save(flexRankData)
        );

        accountGameDataRepository.save(recentData);
    }

    private void updateFavorite(Long accountId, QueueType queueType, Map<String, Integer> champCount, Map<String, Integer> winCount) {
        // accountId, queueType, championName으로 생성된 Favorite이 있으면 업데이트하고, 없으면 새로 만든다.
        for (Map.Entry<String, Integer> entry : champCount.entrySet()) {
            String championName = entry.getKey();
            int playCount = entry.getValue();
            int championWinCount = winCount.getOrDefault(championName, 0);

            Favorite existingFavorite = favoriteRepository.findByAccountIdAndQueueTypeAndChampionName(accountId, queueType, championName);

            if (existingFavorite != null) {
                existingFavorite.update(playCount, championWinCount);
            } else {
                Favorite newFavorite = Favorite.of(accountId, queueType, championName, playCount, championWinCount);
                favoriteRepository.save(newFavorite);
            }
        }
    }

    private Kda updateKda(Kda previousKda, int totalGames, RiotApiMatchInfoResponse matchStats) {
        double totalKill = previousKda.getAverageKills() * totalGames;
        double totalAssist = previousKda.getAverageAssists() * totalGames;
        double totalDeath = previousKda.getAverageDeaths() * totalGames;

        return Kda.of(
                (totalKill + (matchStats.getAverageKill() * matchStats.getTotalGames())) / (totalGames + matchStats.getTotalGames()),
                (totalAssist + (matchStats.getAverageAssist() * matchStats.getTotalGames())) / (totalGames + matchStats.getTotalGames()),
                (totalDeath + (matchStats.getAverageDeath() * matchStats.getTotalGames())) / (totalGames + matchStats.getTotalGames())
        );
    }
}
