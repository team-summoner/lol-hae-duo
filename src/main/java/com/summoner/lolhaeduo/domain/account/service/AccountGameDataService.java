package com.summoner.lolhaeduo.domain.account.service;

import com.summoner.lolhaeduo.client.dto.MatchStats;
import com.summoner.lolhaeduo.client.dto.RankStats;
import com.summoner.lolhaeduo.client.service.RiotClientService;
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
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.summoner.lolhaeduo.domain.duo.enums.QueueType.*;

@Service
@RequiredArgsConstructor
public class AccountGameDataService {

    private final RiotClientService riotClientService;
    private final AccountGameDataRepository accountGameDataRepository;
    private final QuickGameDataRepository quickGameDataRepository;
    private final SoloRankDataRepository soloRankDataRepository;
    private final FlexRankDataRepository flexRankDataRepository;
    private final AccountRepository accountRepository;

    private static final int NUMBER_OF_RECENT_MATCH = 20;

    @Async
    @EventListener
    @Transactional
    public void createAccountGameDataEvent(AccountGameDataEvent event) {
        Account account = accountRepository.findById(event.getAccountId()).orElseThrow(
                () -> new IllegalArgumentException("Account not found")
        );

        // 랭크 정보 가져오기
        RankStats rankStats = riotClientService.getRankGameStats(account.getAccountDetail().getEncryptedSummonerId(), account.getServer());

        // 매치 아이디 가져오기
        List<String> quickMatchIds = riotClientService.getMatchIds(QUICK, NUMBER_OF_RECENT_MATCH, account.getRegion(), account.getAccountDetail().getPuuid());
        List<String> soloMatchIds = riotClientService.getMatchIds(SOLO, rankStats.getSoloTotalGames(), account.getRegion(), account.getAccountDetail().getPuuid());
        List<String> flexMatchIds = riotClientService.getMatchIds(FLEX, rankStats.getFlexTotalGames(), account.getRegion(), account.getAccountDetail().getPuuid());

        // 매치 상세 정보 가져오기(승/패, KDA, 최근 선호 챔피언 통계 정보)
        MatchStats quickStats = riotClientService.getMatchStats(account.getId(), quickMatchIds, QUICK, account.getSummonerName(), account.getTagLine(), account.getRegion());
        MatchStats soloStats = riotClientService.getMatchStats(account.getId(), soloMatchIds, SOLO, account.getSummonerName(), account.getTagLine(), account.getRegion());
        MatchStats flexStats = riotClientService.getMatchStats(account.getId(), flexMatchIds, FLEX, account.getSummonerName(), account.getTagLine(), account.getRegion());

        // 각각 데이터 객체 생성
        String iconUrl = riotClientService.updateProfileIconUrl(account);
        QuickGameData quickGameData = QuickGameData.of(
                quickStats.getWins(), quickStats.getTotalGames(),
                Kda.of(quickStats.getAverageKill(), quickStats.getAverageAssist(), quickStats.getAverageDeath())
        );
        SoloRankData soloRankData = SoloRankData.of(
                rankStats.getSoloTier(), rankStats.getSoloRank(),
                soloStats.getWins(), soloStats.getTotalGames(),
                Kda.of(soloStats.getAverageKill(), soloStats.getAverageAssist(), soloStats.getAverageDeath())
        );
        FlexRankData flexRankData = FlexRankData.of(
                rankStats.getFlexTier(), rankStats.getFlexRank(),
                flexStats.getWins(), flexStats.getTotalGames(),
                Kda.of(flexStats.getAverageKill(), flexStats.getAverageAssist(), flexStats.getAverageDeath())
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
        SoloRankData soloRankData = recentData.getSoloRankData();
        FlexRankData flexRankData = recentData.getFlexRankData();

        // 2. 가장 최근 업데이트 날짜를 확인한다.
        LocalDateTime modifiedAt = recentData.getModifiedAt();

        // 3. 프로필 아이콘을 업데이트한다.
        String updatedProfileIconUrl = riotClientService.updateProfileIconUrl(account);

        // 4. 반영해야할 매치 ID가 있는지 조회한다.
        RankStats rankStats = riotClientService.getRankGameStats(account.getAccountDetail().getEncryptedSummonerId(), account.getServer());

        List<String> quickMatchIds = riotClientService.updateMatchIds(QUICK, modifiedAt, account.getRegion(), account.getAccountDetail().getPuuid());
        List<String> soloMatchIds = riotClientService.updateMatchIds(SOLO, modifiedAt, account.getRegion(), account.getAccountDetail().getPuuid());
        List<String> flexMatchIds = riotClientService.updateMatchIds(FLEX, modifiedAt, account.getRegion(), account.getAccountDetail().getPuuid());

        MatchStats quickStats = riotClientService.getMatchStats(account.getId(), quickMatchIds, QUICK, account.getSummonerName(), account.getTagLine(), account.getRegion());
        MatchStats soloStats = riotClientService.getMatchStats(account.getId(), soloMatchIds, SOLO, account.getSummonerName(), account.getTagLine(), account.getRegion());
        MatchStats flexStats = riotClientService.getMatchStats(account.getId(), flexMatchIds, FLEX, account.getSummonerName(), account.getTagLine(), account.getRegion());

        QuickGameData updatedQuickGameData = QuickGameData.of(
                quickStats.getWins(), quickStats.getTotalGames(),
                Kda.of(quickStats.getAverageKill(), quickStats.getAverageAssist(), quickStats.getAverageDeath())
        );
        soloRankData.update(
                rankStats.getSoloTier(), rankStats.getSoloRank(),
                soloRankData.getWins() + soloStats.getWins(),
                soloRankData.getTotalGames() + soloStats.getTotalGames(),
                updateKda(soloRankData.getKda(), soloRankData.getTotalGames(), soloStats)
        );
        flexRankData.update(
                rankStats.getFlexTier(), rankStats.getFlexRank(),
                flexRankData.getWins() + flexStats.getWins(),
                flexRankData.getTotalGames() + flexStats.getTotalGames(),
                updateKda(flexRankData.getKda(), flexRankData.getTotalGames(), flexStats)
        );

        // 5. AccountGameData 값을 업데이트한다.
        recentData.update(
                updatedProfileIconUrl,
                quickGameDataRepository.save(updatedQuickGameData),
                soloRankDataRepository.save(soloRankData),
                flexRankDataRepository.save(flexRankData)
        );
        accountGameDataRepository.save(recentData);
    }

    private Kda updateKda(Kda previousKda, int totalGames, MatchStats matchStats) {
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
