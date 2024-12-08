package com.summoner.lolhaeduo.domain.duo.service;

import com.summoner.lolhaeduo.client.dto.LeagueEntryResponse;
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
    private final RiotClient riotClient;

    public DuoCreateResponse createDuo(DuoCreateRequest request, Long memberId) {
        Account linkedAccount  = accountRepository.findByMemberId(memberId);
        Long linkedAccountId = linkedAccount.getId();

        List<LeagueEntryResponse> rankInfo = getRankInfo(linkedAccount);

        QueueType queueType = QueueType.valueOf(request.getQueueType().toString());
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
                LeagueEntryResponse selectedRankInfo = rankInfo.stream()
                        .filter(info -> QueueType.fromRiotQueueType(info.getQueueType()) == queueType)
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("해당 큐 타입에 대한 랭크 정보를 찾을 수 없습니다."));

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
                        memberId,
                        linkedAccountId
                );
            }

            case FLEX -> {
                LeagueEntryResponse selectedRankInfo = rankInfo.stream()
                        .filter(info -> QueueType.fromRiotQueueType(info.getQueueType()) == queueType)
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("해당 큐 타입에 대한 랭크 정보를 찾을 수 없습니다."));

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
                        memberId,
                        linkedAccountId
                );
            }

            default -> throw new IllegalArgumentException("Queue Type 잘못됨");
        }
        duoRepository.save(duo);
        return new DuoCreateResponse(duo);
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
