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
import com.summoner.lolhaeduo.domain.duo.entity.Kda;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.summoner.lolhaeduo.domain.duo.enums.QueueType.*;

@Service
@RequiredArgsConstructor
public class AccountGameDataService {

    private final RiotClientService riotClientService;
    private final AccountGameDataRepository accountGameDataRepository;
    private final AccountRepository accountRepository;

    private static final int NUMBER_OF_RECENT_MATCH = 20;

    @EventListener
    @Transactional
    public void createAccountGameDataEvent(AccountGameDataEvent event) {
        Account account = accountRepository.findById(event.getAccountId()).orElseThrow(
                () -> new IllegalArgumentException("Account not found")
        );

        // 랭크 정보 가져오기
        RankStats rankStats = riotClientService.getRankGameStats(account.getAccountDetail().getEncryptedAccountId(), account.getServer());

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
    public AccountGameData updateAccountGameData(Account account) {
        return null;
    }
}
