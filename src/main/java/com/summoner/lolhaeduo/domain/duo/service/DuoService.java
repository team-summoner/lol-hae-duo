package com.summoner.lolhaeduo.domain.duo.service;

import com.summoner.lolhaeduo.client.dto.MatchStats;
import com.summoner.lolhaeduo.client.dto.RankStats;
import com.summoner.lolhaeduo.client.entity.Favorite;
import com.summoner.lolhaeduo.client.repository.FavoriteRepository;
import com.summoner.lolhaeduo.client.service.RiotClientService;
import com.summoner.lolhaeduo.common.dto.AuthMember;
import com.summoner.lolhaeduo.domain.account.entity.Account;
import com.summoner.lolhaeduo.domain.account.repository.AccountRepository;
import com.summoner.lolhaeduo.domain.duo.dto.DuoCreateRequest;
import com.summoner.lolhaeduo.domain.duo.dto.DuoCreateResponse;
import com.summoner.lolhaeduo.domain.duo.dto.DuoUpdateRequest;
import com.summoner.lolhaeduo.domain.duo.dto.DuoUpdateResponse;
import com.summoner.lolhaeduo.domain.duo.entity.Duo;
import com.summoner.lolhaeduo.domain.duo.entity.Kda;
import com.summoner.lolhaeduo.domain.duo.enums.QueueType;
import com.summoner.lolhaeduo.domain.duo.repository.DuoRepository;
import com.summoner.lolhaeduo.domain.member.enums.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DuoService {

    private final DuoRepository duoRepository;
    private final AccountRepository accountRepository;
    private final RiotClientService riotClientService;
    private final FavoriteRepository favoriteRepository;

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
        List<String> favoritesChamp;
        Kda kda = Kda.of(0.0, 0.0, 0.0);

        Duo duo = null;

        if (request.getQueueType() == QueueType.QUICK) {
            MatchStats matchStats = riotClientService.getMatchStats(
                    linkedAccount.getId(),
                    riotClientService.getMatchIds(QueueType.QUICK, 20, linkedAccount.getRegion(), linkedAccount.getAccountDetail().getPuuid()),
                    QueueType.QUICK,
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

            favoritesChamp = favoriteRepository.findTop3ByAccountIdAndQueueTypeOrderByPlayCountDesc(linkedAccount.getId(), QueueType.QUICK)
                    .stream()
                    .map(Favorite::getChampionName)
                    .toList();

            duo = Duo.quickOf(
                    QueueType.QUICK,
                    request.getPrimaryRole(), request.getPrimaryChamp(),
                    request.getSecondaryRole(), request.getSecondaryChamp(),
                    request.getTargetRole(), request.getMemo(),
                    request.getMic(),
                    tier, ranks, wins, losses, profileIconUrl, kda, favoritesChamp,
                    linkedAccount.getMemberId(), linkedAccount.getId()
            );

        } else if (request.getQueueType() == QueueType.SOLO) {
            RankStats rankStats = riotClientService.getRankGameStats(linkedAccount, linkedAccount.getServer());
            wins = rankStats.getSoloWins();
            losses = rankStats.getSoloLosses();
            tier = rankStats.getSoloTier();
            ranks = rankStats.getSoloRank();

            MatchStats matchStats = riotClientService.getMatchStats(
                    linkedAccount.getId(),
                    riotClientService.getMatchIds(QueueType.SOLO, rankStats.getSoloTotalGames(), linkedAccount.getRegion(), linkedAccount.getAccountDetail().getPuuid()),
                    QueueType.SOLO,
                    linkedAccount.getSummonerName(),
                    linkedAccount.getTagLine(),
                    linkedAccount.getRegion()
            );

            kda = Kda.of(
                    matchStats.getAverageKill(),
                    matchStats.getAverageDeath(),
                    matchStats.getAverageAssist()
            );

            favoritesChamp = favoriteRepository.findTop3ByAccountIdAndQueueTypeOrderByPlayCountDesc(linkedAccount.getId(), QueueType.SOLO)
                    .stream()
                    .map(Favorite::getChampionName)
                    .toList();

            duo = Duo.soloOf(
                    QueueType.SOLO,
                    request.getPrimaryRole(), request.getTargetRole(),
                    request.getMemo(), request.getMic(),
                    tier, ranks, wins, losses, profileIconUrl, kda, favoritesChamp,
                    linkedAccount.getMemberId(), linkedAccount.getId()
            );

        } else if (request.getQueueType() == QueueType.FLEX) {
            RankStats rankStats = riotClientService.getRankGameStats(linkedAccount, linkedAccount.getServer());
            wins = rankStats.getFlexWins();
            losses = rankStats.getFlexLosses();
            tier = rankStats.getFlexTier();
            ranks = rankStats.getFlexRank();

            MatchStats matchStats = riotClientService.getMatchStats(
                    linkedAccount.getId(),
                    riotClientService.getMatchIds(QueueType.FLEX, rankStats.getFlexTotalGames(), linkedAccount.getRegion(), linkedAccount.getAccountDetail().getPuuid()),
                    QueueType.FLEX,
                    linkedAccount.getSummonerName(),
                    linkedAccount.getTagLine(),
                    linkedAccount.getRegion()
            );

            kda = Kda.of(
                    matchStats.getAverageKill(),
                    matchStats.getAverageDeath(),
                    matchStats.getAverageAssist()
            );

            favoritesChamp = favoriteRepository.findTop3ByAccountIdAndQueueTypeOrderByPlayCountDesc(linkedAccount.getId(), QueueType.FLEX)
                    .stream()
                    .map(Favorite::getChampionName)
                    .toList();

            duo = Duo.flexOf(
                    QueueType.FLEX,
                    request.getPrimaryRole(), request.getTargetRole(),
                    request.getMemo(), request.getMic(),
                    tier, ranks, wins, losses, profileIconUrl, kda, favoritesChamp,
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
        List<String> favoritesChamp = List.of();
        Kda kda = Kda.of(0.0, 0.0, 0.0);

        if (!duo.getQueueType().equals(request.getQueueType())) {
            if (request.getQueueType() == QueueType.QUICK) {
                MatchStats matchStats = riotClientService.getMatchStats(
                        linkedAccount.getId(),
                        riotClientService.getMatchIds(QueueType.QUICK, 20, linkedAccount.getRegion(), linkedAccount.getAccountDetail().getPuuid()),
                        QueueType.QUICK,
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

                favoritesChamp = favoriteRepository.findTop3ByAccountIdAndQueueTypeOrderByPlayCountDesc(linkedAccount.getId(), QueueType.QUICK)
                        .stream()
                        .map(Favorite::getChampionName)
                        .toList();

            } else if (request.getQueueType() == QueueType.SOLO) {
                RankStats rankStats = riotClientService.getRankGameStats(linkedAccount, linkedAccount.getServer());
                wins = rankStats.getSoloWins();
                losses = rankStats.getSoloLosses();
                tier = rankStats.getSoloTier();
                ranks = rankStats.getSoloRank();

                MatchStats matchStats = riotClientService.getMatchStats(
                        linkedAccount.getId(),
                        riotClientService.getMatchIds(QueueType.SOLO, rankStats.getSoloTotalGames(), linkedAccount.getRegion(), linkedAccount.getAccountDetail().getPuuid()),
                        QueueType.SOLO,
                        linkedAccount.getSummonerName(),
                        linkedAccount.getTagLine(),
                        linkedAccount.getRegion()
                );

                kda = Kda.of(
                        matchStats.getAverageKill(),
                        matchStats.getAverageDeath(),
                        matchStats.getAverageAssist()
                );

                favoritesChamp = favoriteRepository.findTop3ByAccountIdAndQueueTypeOrderByPlayCountDesc(linkedAccount.getId(), QueueType.SOLO)
                        .stream()
                        .map(Favorite::getChampionName)
                        .toList();

            } else if (request.getQueueType() == QueueType.FLEX) {
                RankStats rankStats = riotClientService.getRankGameStats(linkedAccount, linkedAccount.getServer());
                wins = rankStats.getFlexWins();
                losses = rankStats.getFlexLosses();
                tier = rankStats.getFlexTier();
                ranks = rankStats.getFlexRank();

                MatchStats matchStats = riotClientService.getMatchStats(
                        linkedAccount.getId(),
                        riotClientService.getMatchIds(QueueType.FLEX, rankStats.getFlexTotalGames(), linkedAccount.getRegion(), linkedAccount.getAccountDetail().getPuuid()),
                        QueueType.FLEX,
                        linkedAccount.getSummonerName(),
                        linkedAccount.getTagLine(),
                        linkedAccount.getRegion()
                );

                kda = Kda.of(
                        matchStats.getAverageKill(),
                        matchStats.getAverageDeath(),
                        matchStats.getAverageAssist()
                );

                favoritesChamp = favoriteRepository.findTop3ByAccountIdAndQueueTypeOrderByPlayCountDesc(linkedAccount.getId(), QueueType.FLEX)
                        .stream()
                        .map(Favorite::getChampionName)
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
        } else {
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
                    duo.getFavoritesChamp()
            );
        }

        return new DuoUpdateResponse(
                duo.getId(),
                duo.getQueueType(),
                duo.getPrimaryRole(),
                duo.getPrimaryChamp(),
                duo.getSecondaryRole(),
                duo.getSecondaryChamp(),
                duo.getTargetRole(),
                duo.getMemo(),
                duo.getMic(),
                duo.getTier(),
                duo.getRanks(),
                duo.getWins(),
                duo.getLosses(),
                duo.getFavoritesChamp(),
                duo.getProfileIcon(),
                duo.getKda(),
                linkedAccount.getMemberId(),
                linkedAccount.getId(),
                duo.getCreatedAt(),
                duo.getModifiedAt()
        );
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
