package com.summoner.lolhaeduo.domain.duo.service;

import com.summoner.lolhaeduo.client.dto.LeagueEntryResponse;
import com.summoner.lolhaeduo.client.dto.PuuidResponse;
import com.summoner.lolhaeduo.client.dto.SummonerResponse;
import com.summoner.lolhaeduo.client.entity.Version;
import com.summoner.lolhaeduo.client.repository.VersionRepository;
import com.summoner.lolhaeduo.client.riot.RiotClient;
import com.summoner.lolhaeduo.domain.account.entity.Account;
import com.summoner.lolhaeduo.domain.account.repository.AccountRepository;
import com.summoner.lolhaeduo.domain.duo.dto.DuoCreateRequest;
import com.summoner.lolhaeduo.domain.duo.dto.DuoCreateResponse;
import com.summoner.lolhaeduo.domain.duo.entity.Duo;
import com.summoner.lolhaeduo.domain.duo.enums.QueueType;
import com.summoner.lolhaeduo.domain.duo.repository.DuoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DuoService {

    private final DuoRepository duoRepository;
    private final AccountRepository accountRepository;
    private final VersionRepository versionRepository;
    private final RiotClient riotClient;

    public DuoCreateResponse createDuo(DuoCreateRequest request, Long memberId) {

        Account linkedAccount  = accountRepository.findByMemberId(memberId);
        Long linkedAccountId = linkedAccount.getId();

        String profileIconUrl = getProfileIconUrl(linkedAccount);

        List<LeagueEntryResponse> rankInfo = getRankInfo(linkedAccount);

        QueueType queueType = QueueType.valueOf(request.getQueueType().toString());
        LeagueEntryResponse selectedRankInfo = getSelectedRankInfo(rankInfo, queueType);

        Duo duo;
        switch (queueType) {
//            case QUICK -> duo = Duo.quickOf(
//                    request.getQueueType(),
//                    request.getPrimaryRole(),
//                    request.getPrimaryChamp(),
//                    request.getSecondaryRole(),
//                    request.getSecondaryChamp(),
//                    targetRoles,
//                    request.getMemo(),
//                    request.getMic(),
//                    memberId,
//                    linkedAccountId
//            );
            case SOLO -> {
                duo = Duo.soloOf(
                        request.getQueueType(),
                        request.getPrimaryRole(),
                        request.getTargetRole(),
                        request.getMemo(),
                        request.getMic(),
                        selectedRankInfo.getTier(),
                        selectedRankInfo.getRank(),
                        selectedRankInfo.getWins(),
                        selectedRankInfo.getLosses(),
                        profileIconUrl,
                        memberId,
                        linkedAccountId
                );

            }
            case FLEX -> {
                duo = Duo.flexOf(
                        request.getQueueType(),
                        request.getPrimaryRole(),
                        request.getTargetRole(),
                        request.getMemo(),
                        request.getMic(),
                        selectedRankInfo.getTier(),
                        selectedRankInfo.getRank(),
                        selectedRankInfo.getWins(),
                        selectedRankInfo.getLosses(),
                        profileIconUrl,
                        memberId,
                        linkedAccountId
                );
            }
            default -> throw new IllegalArgumentException("Queue Type 잘못됨");
        }
        duoRepository.save(duo);
        int winRate = duo.calculateWinRate(selectedRankInfo.getWins(), selectedRankInfo.getLosses());
        return new DuoCreateResponse(duo, winRate);
    }

    private String getProfileIconUrl(Account linkedAccount) {
        PuuidResponse puuidResponse = riotClient.extractPuuid(
                linkedAccount.getSummonerName(),
                linkedAccount.getTagLine(),
                linkedAccount.getRegion()
        );
        SummonerResponse summonerResponse = riotClient.extractSummonerInfo(
                puuidResponse.getPuuid(),
                linkedAccount.getServer()
        );
        int accountProfileIconId = summonerResponse.getProfileIconId();

        String latestVersion = getLatestVersion();

        return String.format(
                "https://ddragon.leagueoflegends.com/cdn/%s/img/profileicon/%d.png",
                latestVersion, accountProfileIconId
        );
    }

    private String getLatestVersion() {
        Version latestVersion = versionRepository.findLatestVersion();
        if (latestVersion == null) {
            throw new IllegalArgumentException("최신버전이 없습니다");
        }
        return latestVersion.getVersionNumber();
        // latestVersion != null일때,  return latestVersion.getVersionNumber();
        // 아닐땐  String latestVersionFromApi = DataDragonScheduler.fetchLatestVersion();의 값을 리턴?
    }

    private static LeagueEntryResponse getSelectedRankInfo(List<LeagueEntryResponse> rankInfo, QueueType queueType) {
        return rankInfo.stream()
                .filter(info -> QueueType.fromRiotQueueType(info.getQueueType()) == queueType)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 큐 타입에 대한 랭크 정보를 찾을 수 없습니다."));
    }

    // 랭크 티어 가져오기
    private List<LeagueEntryResponse> getRankInfo(Account linkedAccount) {
        String encryptedSummonerId = linkedAccount.getAccountDetail().getEncryptedSummonerId();

        return riotClient.extractLeagueInfo(
                encryptedSummonerId,
                linkedAccount.getServer()
        );
    }
}
