package com.summoner.lolhaeduo.domain.duo.service;

import com.summoner.lolhaeduo.client.dto.MatchStats;
import com.summoner.lolhaeduo.client.dto.RankStats;
import com.summoner.lolhaeduo.client.entity.Favorite;
import com.summoner.lolhaeduo.client.repository.FavoriteRepository;
import com.summoner.lolhaeduo.client.service.RiotClientService;
import com.summoner.lolhaeduo.common.dto.AuthMember;
import com.summoner.lolhaeduo.common.dto.PageResponse;
import com.summoner.lolhaeduo.domain.account.entity.Account;
import com.summoner.lolhaeduo.domain.account.repository.AccountRepository;
import com.summoner.lolhaeduo.domain.duo.dto.*;
import com.summoner.lolhaeduo.domain.duo.entity.Duo;
import com.summoner.lolhaeduo.domain.duo.entity.Kda;
import com.summoner.lolhaeduo.domain.duo.enums.Lane;
import com.summoner.lolhaeduo.domain.duo.enums.QueueType;
import com.summoner.lolhaeduo.domain.duo.repository.DuoRepository;
import com.summoner.lolhaeduo.domain.member.enums.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.summoner.lolhaeduo.domain.duo.enums.QueueType.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DuoService {

    private final DuoRepository duoRepository;
    private final AccountRepository accountRepository;
    private final RiotClientService riotClientService;
    private final FavoriteRepository favoriteRepository;


    // 페이징 처리된 듀오 리스트 조회
    public PageResponse<DuoListResponse> getPagedDuoList(QueueType queueType, Lane lane, String tier, int page, int size) {
        // 1. 조건에 맞는 듀오 리스트를 페이징 처리해서 가져오기
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Duo> duoPage = duoRepository.findDuosByCondition(queueType, lane, tier, pageable);

        // 2. 가져온 듀오 리스트를 map()을 사용하여 변환
        Page<DuoListResponse> response = duoPage.map(duo -> {
            // Account 정보 조회
            Account account = accountRepository.findById(duo.getAccountId())
                .orElseThrow(() -> new IllegalArgumentException("해당 계정을 찾을 수 없습니다. accountId=" + duo.getAccountId()));


            // DuoListResponse 생성
            return DuoListResponse.of(
                duo,
                account.getSummonerName(),
                account.getTagLine()

            );
        });

        return  PageResponse.of(response.toList(), pageable , response.getTotalPages());
    }
    @Transactional
    public DuoCreateResponse createDuo(DuoCreateRequest request, Long memberId) {

        Account linkedAccount = accountRepository.findById(request.getAccountId()).orElseThrow(
                () -> new IllegalArgumentException("Not Found Account")
        );

        if (linkedAccount.getMemberId() != memberId) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        // profileIcon
        String profileIconUrl = riotClientService.getProfileIconUrl(linkedAccount);

        // 승리, 패배 정보 및 승률 계산
        int wins = 0, losses = 0, winRate = 0;
        String tier = "", ranks = "";
        List<Long> favoriteId;
        Kda kda = Kda.of(0.0, 0.0, 0.0);

        Duo duo = null;

        if (request.getQueueType() == QUICK) {
            MatchStats matchStats = riotClientService.getMatchStats(
                    linkedAccount.getId(),
                    riotClientService.getMatchIds(QUICK, 20, linkedAccount.getRegion(), linkedAccount.getAccountDetail().getPuuid()),
                    QUICK,
                    linkedAccount.getSummonerName(),
                    linkedAccount.getTagLine(),
                    linkedAccount.getRegion()
            );

            int totalGames = matchStats.getTotalGames();
            wins = (int) ((matchStats.getWinRate() / 100) * totalGames);
            losses = totalGames - wins;

            // 솔로랭크 기준 티어, 랭크 가져오기
            RankStats rankStats = riotClientService.getRankGameStats(linkedAccount, linkedAccount.getServer());
            tier = rankStats.getSoloTier();
            ranks = rankStats.getSoloRank();

            kda = Kda.of(
                    matchStats.getAverageKill(),
                    matchStats.getAverageDeath(),
                    matchStats.getAverageAssist()
            );

            favoriteId = favoriteRepository.findTop3ByAccountIdAndQueueTypeOrderByPlayCountDesc(linkedAccount.getId(), QUICK)
                    .stream()
                    .map(Favorite::getId)
                    .toList();

            duo = Duo.quickOf(
                    QUICK,
                    request.getPrimaryRole(), request.getPrimaryChamp(),
                    request.getSecondaryRole(), request.getSecondaryChamp(),
                    request.getTargetRole(), request.getMemo(),
                    request.getMic(),
                    tier, ranks, wins, losses, profileIconUrl, kda, favoriteId,
                    linkedAccount.getMemberId(), linkedAccount.getId()
            );

        }
        if (request.getQueueType() == SOLO) {
            RankStats rankStats = riotClientService.getRankGameStats(linkedAccount, linkedAccount.getServer());
            wins = rankStats.getSoloWins();
            losses = rankStats.getSoloLosses();
            tier = rankStats.getSoloTier();
            ranks = rankStats.getSoloRank();

            MatchStats matchStats = riotClientService.getMatchStats(
                    linkedAccount.getId(),
                    riotClientService.getMatchIds(SOLO, rankStats.getSoloTotalGames(), linkedAccount.getRegion(), linkedAccount.getAccountDetail().getPuuid()),
                    SOLO,
                    linkedAccount.getSummonerName(),
                    linkedAccount.getTagLine(),
                    linkedAccount.getRegion()
            );

            kda = Kda.of(
                    matchStats.getAverageKill(),
                    matchStats.getAverageDeath(),
                    matchStats.getAverageAssist()
            );

            favoriteId = favoriteRepository.findTop3ByAccountIdAndQueueTypeOrderByPlayCountDesc(linkedAccount.getId(), SOLO)
                    .stream()
                    .map(Favorite::getId)
                    .toList();

            duo = Duo.soloOf(
                    SOLO,
                    request.getPrimaryRole(), request.getTargetRole(),
                    request.getMemo(), request.getMic(),
                    tier, ranks, wins, losses, profileIconUrl, kda, favoriteId,
                    linkedAccount.getMemberId(), linkedAccount.getId()
            );

        }
        if (request.getQueueType() == FLEX) {
            RankStats rankStats = riotClientService.getRankGameStats(linkedAccount, linkedAccount.getServer());
            wins = rankStats.getFlexWins();
            losses = rankStats.getFlexLosses();
            tier = rankStats.getFlexTier();
            ranks = rankStats.getFlexRank();

            MatchStats matchStats = riotClientService.getMatchStats(
                    linkedAccount.getId(),
                    riotClientService.getMatchIds(FLEX, rankStats.getFlexTotalGames(), linkedAccount.getRegion(), linkedAccount.getAccountDetail().getPuuid()),
                    FLEX,
                    linkedAccount.getSummonerName(),
                    linkedAccount.getTagLine(),
                    linkedAccount.getRegion()
            );

            kda = Kda.of(
                    matchStats.getAverageKill(),
                    matchStats.getAverageDeath(),
                    matchStats.getAverageAssist()
            );

            favoriteId = favoriteRepository.findTop3ByAccountIdAndQueueTypeOrderByPlayCountDesc(linkedAccount.getId(), FLEX)
                    .stream()
                    .map(Favorite::getId)
                    .toList();

            duo = Duo.flexOf(
                    FLEX,
                    request.getPrimaryRole(), request.getTargetRole(),
                    request.getMemo(), request.getMic(),
                    tier, ranks, wins, losses, profileIconUrl, kda, favoriteId,
                    linkedAccount.getMemberId(), linkedAccount.getId()
            );
        }

        Duo savedDuo = duoRepository.save(duo);
        if (wins + losses > 0) {
            winRate = (int) ((double) wins / (wins + losses) * 100);
        }

        return new DuoCreateResponse(savedDuo, winRate);
    }


    @Transactional
    public DuoUpdateResponse update(Long memberId, Long duoId, DuoUpdateRequest request) {
        Duo duo = duoRepository.findById(duoId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 듀오 찾기 글입니다."));
        if (duo.getDeletedAt() != null) {
            throw new IllegalStateException("삭제된 듀오 찾기 글 입니다.");
        }
        if (!duo.getMemberId().equals(memberId)) {
            throw new IllegalStateException("듀오 신청자가 아닙니다.");
        }
        Account linkedAccount = accountRepository.findById(duo.getAccountId()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 롤 계정입니다."));

        int wins = 0, losses = 0, winRate = 0;
        String tier = "", ranks = "";
        List<Long> favoritesChamp = List.of();
        Kda kda = Kda.of(0.0, 0.0, 0.0);

        if (duo.getQueueType().equals(request.getQueueType())) {
            duo.update(
                    request.getQueueType(),
                    request.getPrimaryRole(),
                    request.getPrimaryChamp(),
                    request.getSecondaryRole(),
                    request.getSecondaryChamp(),
                    request.getTargetRole(),
                    request.getMemo(),
                    request.getMic(),
                    duo.getTier(),
                    duo.getRanks(),
                    duo.getWins(),
                    duo.getLosses(),
                    duo.getKda(),
                    duo.getFavoriteId()
            );
        } else {
            if (request.getQueueType() == QUICK) {
                MatchStats matchStats = riotClientService.getMatchStats(
                        linkedAccount.getId(),
                        riotClientService.getMatchIds(QUICK, 20, linkedAccount.getRegion(), linkedAccount.getAccountDetail().getPuuid()),
                        QUICK,
                        linkedAccount.getSummonerName(),
                        linkedAccount.getTagLine(),
                        linkedAccount.getRegion()
                );

                int totalGames = matchStats.getTotalGames();
                wins = (int) ((matchStats.getWinRate() / 100) * totalGames);
                losses = totalGames - wins;

                RankStats rankStats = riotClientService.getRankGameStats(linkedAccount, linkedAccount.getServer());
                tier = rankStats.getSoloTier();
                ranks = rankStats.getSoloRank();

                kda = Kda.of(
                        matchStats.getAverageKill(),
                        matchStats.getAverageDeath(),
                        matchStats.getAverageAssist()
                );

                favoritesChamp = favoriteRepository.findTop3ByAccountIdAndQueueTypeOrderByPlayCountDesc(linkedAccount.getId(), QUICK)
                        .stream()
                        .map(Favorite::getId)
                        .toList();

            }
            if (request.getQueueType() == SOLO) {
                RankStats rankStats = riotClientService.getRankGameStats(linkedAccount, linkedAccount.getServer());
                wins = rankStats.getSoloWins();
                losses = rankStats.getSoloLosses();
                tier = rankStats.getSoloTier();
                ranks = rankStats.getSoloRank();

                MatchStats matchStats = riotClientService.getMatchStats(
                        linkedAccount.getId(),
                        riotClientService.getMatchIds(SOLO, rankStats.getSoloTotalGames(), linkedAccount.getRegion(), linkedAccount.getAccountDetail().getPuuid()),
                        SOLO,
                        linkedAccount.getSummonerName(),
                        linkedAccount.getTagLine(),
                        linkedAccount.getRegion()
                );

                kda = Kda.of(
                        matchStats.getAverageKill(),
                        matchStats.getAverageDeath(),
                        matchStats.getAverageAssist()
                );

                favoritesChamp = favoriteRepository.findTop3ByAccountIdAndQueueTypeOrderByPlayCountDesc(linkedAccount.getId(), SOLO)
                        .stream()
                        .map(Favorite::getId)
                        .toList();

            }
            if (request.getQueueType() == FLEX) {
                RankStats rankStats = riotClientService.getRankGameStats(linkedAccount, linkedAccount.getServer());
                wins = rankStats.getFlexWins();
                losses = rankStats.getFlexLosses();
                tier = rankStats.getFlexTier();
                ranks = rankStats.getFlexRank();

                MatchStats matchStats = riotClientService.getMatchStats(
                        linkedAccount.getId(),
                        riotClientService.getMatchIds(FLEX, rankStats.getFlexTotalGames(), linkedAccount.getRegion(), linkedAccount.getAccountDetail().getPuuid()),
                        FLEX,
                        linkedAccount.getSummonerName(),
                        linkedAccount.getTagLine(),
                        linkedAccount.getRegion()
                );

                kda = Kda.of(
                        matchStats.getAverageKill(),
                        matchStats.getAverageDeath(),
                        matchStats.getAverageAssist()
                );

                favoritesChamp = favoriteRepository.findTop3ByAccountIdAndQueueTypeOrderByPlayCountDesc(linkedAccount.getId(), FLEX)
                        .stream()
                        .map(Favorite::getId)
                        .toList();
            }

            duo.update(
                    request.getQueueType(),
                    request.getPrimaryRole(),
                    request.getPrimaryChamp(),
                    request.getSecondaryRole(),
                    request.getSecondaryChamp(),
                    request.getTargetRole(),
                    request.getMemo(),
                    request.getMic(),
                    tier,
                    ranks,
                    wins,
                    losses,
                    kda,
                    favoritesChamp
            );
        }

        return DuoUpdateResponse.of(duo);
    }


    public void deleteDuoById(Long duoId, AuthMember authMember) {
        Duo existDuo = duoRepository.findById(duoId).orElseThrow(
                () -> new IllegalArgumentException("듀오가 존재하지 않습니다.")
        );

        if (!authMember.getMemberId().equals(existDuo.getMemberId())) {
            if (!authMember.getRole().equals(UserRole.ADMIN)) {
                throw new IllegalArgumentException("삭제 권한이 없습니다.");
            }
        }
        // Soft Delete 처리: 삭제 시간 기록
        existDuo.delete();
        duoRepository.save(existDuo);  // 엔티티를 업데이트하여 삭제 시간 저장

        duoRepository.deleteById(duoId);
    }
}
