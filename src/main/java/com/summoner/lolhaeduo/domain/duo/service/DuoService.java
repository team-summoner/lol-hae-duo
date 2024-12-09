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
        String encryptedSummonerId = linkedAccount.getAccountDetail().getEncryptedSummonerId();

        List<LeagueEntryResponse> rankInfo = riotClient.extractLeagueInfo(encryptedSummonerId, linkedAccount.getServer());

        QueueType queueType = request.getQueueType();
        LeagueEntryResponse selectedRankInfo = getSelectedRankInfoByQueueType(rankInfo, queueType);

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
    // todo 게임버전 api로 변경하면 로직 변경해야함
    private String getLatestVersion() {
        Version latestVersion = versionRepository.findLatestVersion();
        return latestVersion.getVersionNumber();
    }

    private LeagueEntryResponse getSelectedRankInfoByQueueType(List<LeagueEntryResponse> rankInfo, QueueType queueType) {
        return rankInfo.stream()
                .filter(info -> QueueType.fromRiotQueueType(info.getQueueType()) == queueType)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 큐 타입에 대한 랭크 정보를 찾을 수 없습니다."));
    }
}
