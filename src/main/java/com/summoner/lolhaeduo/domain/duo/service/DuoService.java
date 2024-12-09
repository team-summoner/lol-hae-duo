package com.summoner.lolhaeduo.domain.duo.service;

import com.summoner.lolhaeduo.client.dto.FormattedMatchResponse;
import com.summoner.lolhaeduo.client.dto.LeagueEntryResponse;
import com.summoner.lolhaeduo.client.dto.SummonerResponse;
import com.summoner.lolhaeduo.client.entity.Version;
import com.summoner.lolhaeduo.client.repository.VersionRepository;
import com.summoner.lolhaeduo.client.riot.RiotClient;
import com.summoner.lolhaeduo.common.dto.AuthMember;
import com.summoner.lolhaeduo.domain.account.entity.Account;
import com.summoner.lolhaeduo.domain.account.enums.AccountRegion;
import com.summoner.lolhaeduo.domain.account.repository.AccountRepository;
import com.summoner.lolhaeduo.domain.duo.dto.*;
import com.summoner.lolhaeduo.domain.duo.dto.DuoUpdateRequest;
import com.summoner.lolhaeduo.domain.duo.entity.Duo;
import com.summoner.lolhaeduo.domain.duo.entity.Kda;
import com.summoner.lolhaeduo.domain.duo.enums.QueueType;
import com.summoner.lolhaeduo.domain.duo.repository.DuoRepository;
import com.summoner.lolhaeduo.domain.member.enums.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DuoService {

    private final DuoRepository duoRepository;
    private final AccountRepository accountRepository;
    private final VersionRepository versionRepository;
    private final RiotClient riotClient;

    public DuoCreateResponse createDuo(DuoCreateRequest request, Long memberId) {

        Account linkedAccount = accountRepository.findByMemberId(memberId);
        Long linkedAccountId = linkedAccount.getId();

        String profileIconUrl = getProfileIconUrl(linkedAccount);
        String encryptedSummonerId = linkedAccount.getAccountDetail().getEncryptedSummonerId();

        List<LeagueEntryResponse> rankInfo = riotClient.extractLeagueInfo(encryptedSummonerId, linkedAccount.getServer());

        QueueType queueType = request.getQueueType();
        LeagueEntryResponse selectedSoloRankInfo = getSelectedRankInfoByQueueType(rankInfo, QueueType.SOLO);
        LeagueEntryResponse selectedFlexRankInfo = getSelectedRankInfoByQueueType(rankInfo, QueueType.FLEX);

        Duo duo;
        int winRate = 0;
        switch (queueType) {
            case QUICK -> duo = Duo.quickOf(

                    request.getQueueType(),
                    request.getPrimaryRole(),
                    request.getPrimaryChamp(),
                    request.getSecondaryRole(),
                    request.getSecondaryChamp(),
                    request.getTargetRole(),
                    request.getMemo(),
                    request.getMic(),
                    selectedSoloRankInfo.getTier(),
                    selectedSoloRankInfo.getRank(),
                    wins,
                    losses,
                    profileIconUrl,
                    memberId,
                    linkedAccountId
            );
            case SOLO -> {
                duo = Duo.soloOf(
                        request.getQueueType(),
                        request.getPrimaryRole(),
                        request.getTargetRole(),
                        request.getMemo(),
                        request.getMic(),
                        selectedSoloRankInfo.getTier(),
                        selectedSoloRankInfo.getRank(),
                        selectedSoloRankInfo.getWins(),
                        selectedSoloRankInfo.getLosses(),
                        profileIconUrl,
                        memberId,
                        linkedAccountId
                );
                calculateRankWinRate(selectedSoloRankInfo.getWins(), selectedSoloRankInfo.getLosses());
            }

            case FLEX -> {

                duo = Duo.flexOf(
                        request.getQueueType(),
                        request.getPrimaryRole(),
                        request.getTargetRole(),
                        request.getMemo(),
                        request.getMic(),
                        selectedFlexRankInfo.getTier(),
                        selectedFlexRankInfo.getRank(),
                        selectedFlexRankInfo.getWins(),
                        selectedFlexRankInfo.getLosses(),
                        profileIconUrl,
                        memberId,
                        linkedAccountId
                );
                calculateRankWinRate(selectedFlexRankInfo.getWins(), selectedFlexRankInfo.getLosses());
            }

            default -> throw new IllegalArgumentException("Queue Type 잘못됨");
        }
        duoRepository.save(duo);

        return new DuoCreateResponse(duo, winRate);
    }



    @Transactional
    public DuoUpdateResponse update(Long memberId, Long duoId, DuoUpdateRequest updateRequest) {
        Duo duo = duoRepository.findById(duoId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 듀오 찾기 글입니다."));
        if (duo.getDeletedAt() != null) {
            throw new IllegalStateException("삭제된 듀오 찾기 글 입니다.");
        }
        if (!duo.getMemberId().equals(memberId)) {
            throw new IllegalStateException("듀오 신청자가 아닙니다.");
        }

        Long duoAccountId = duo.getAccountId();
        Account duoLinkedAccount = accountRepository.findById(duoAccountId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 롤 계정입니다."));

        // todo
        //  - 삭제된 롤 계정인지 확인 -> RSO 연동 후 진행

        QueueType queueType = updateRequest.getQueueType();
        RecordResponse recordResponse = null;

        // 빠른 대전일 때
        if (queueType == QueueType.QUICK) {
            recordResponse = callQuickRecord(duoLinkedAccount);
        }

        duo.update(
                queueType,
                updateRequest.getPrimaryRole(),
                updateRequest.getPrimaryChamp(),
                updateRequest.getSecondaryRole(),
                updateRequest.getSecondaryChamp(),
                updateRequest.getTargetRole(),
                updateRequest.getMemo(),
                updateRequest.getMic(),
                recordResponse.getWins(),
                recordResponse.getLosses(),
                recordResponse.getKda()
        );

        return DuoUpdateResponse.from(duo);
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


    // ===================================================================
    // ===================================================================
    // ===================================================================

    public RecordResponse callQuickRecord(Account duoLinkedAccount) {
        int wins = 0;
        int losses = 0;
        int kills = 0;
        int deaths = 0;
        int assists = 0;
        int daysBefore = 30;

        long startTime = calculateStartTime(daysBefore);    // 빠른 대전일 때, 조회할 기간의 시작 시점; 한 달(30일) 전
        Long endTime = null;        // 빠른 대전일 때, 조회할 기간의 종료 시점 (현재라 설정 안 함)
        int queue = 490;        // 빠른 대전의 specific queue id; 490
        String type = "normal";     // 빠른 대전의 type; normal

        int start = 0;              // 시작 인덱스
        int count = 20;             // 한 번에 읽어 올 match id의 개수

        AccountRegion region = duoLinkedAccount.getRegion();
        String puuid = duoLinkedAccount.getAccountDetail().getPuuid();

        // 한 달간 플레이한 최대 20판의 빠른 대전 id
        List<String> matchIds = riotClient.extractMatchIds(startTime, endTime, queue, type, start, count, region, puuid);

        String summonerName = duoLinkedAccount.getSummonerName();
        String tagLine = duoLinkedAccount.getTagLine();

        // 한 달간 최대 20판의 플레이한 빠른 대전의
        // 챔피언 플레이 수
        // <championName, playingCounts>
        // <챔피언 이름, 챔피언 플레이 수>
        Map<String, Integer> playingChampionCount = new HashMap<>();

        List<FormattedMatchResponse> matchDetails = new ArrayList<>();
        for (String matchId : matchIds) {
            FormattedMatchResponse matchDetail = riotClient.getMatchDetails(matchId, summonerName, tagLine, region);

            // 승패 계산
            if (matchDetail.isWin()) {
                wins++;
            } else {
                losses++;
            }

            // K/D/A
            kills += matchDetail.getKills();
            deaths += matchDetail.getDeaths();
            assists += matchDetail.getAssists();

            // 챔피언별 플레이 수
            playingChampionCount.put(matchDetail.getChampionName(),
                    playingChampionCount.getOrDefault(matchDetail.getChampionName(), 0) + 1);
            // TOP 3 챔피언
            List<Map.Entry<String, Integer>> top3Champions = playingChampionCount.entrySet().stream()
                    .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                    .limit(3)
                    .toList();

            matchDetails.add(matchDetail);
        }
        // todo
        //  - TOP 3 챔피언 어떻게 반환할 건지 정한 후, return

        int playCounts = matchDetails.size();
        // KDA 평균 계산
        BigDecimal totalPlayCounts = BigDecimal.valueOf(playCounts);
        BigDecimal averageKills = matchDetails.isEmpty()
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(kills).divide(totalPlayCounts, 2, RoundingMode.HALF_UP);
        BigDecimal averageDeaths = matchDetails.isEmpty()
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(deaths).divide(totalPlayCounts, 2, RoundingMode.HALF_UP);
        BigDecimal averageAssists = matchDetails.isEmpty()
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(assists).divide(totalPlayCounts, 2, RoundingMode.HALF_UP);
        Kda kda = Kda.of(averageKills, averageDeaths, averageAssists);

        return RecordResponse.of(wins, losses, kills, deaths, assists, playCounts, kda);
    }

    // 현재로부터 days일 전 Epoch timestamp 계산 메서드
    public long calculateStartTime(int days) {
        // 현재 시간
        Instant now = Instant.now();

        // 30일 전 계산
        Instant thirtyDaysAgo = now.minus(days, ChronoUnit.DAYS);

        // Epoch Time 으로 변환
        long epochTime = thirtyDaysAgo.getEpochSecond();
        log.info("30일 전 Epoch Time {}", epochTime);

        return epochTime;
    }



    // ===================================================================

    public int calculateRankWinRate(int wins, int losses) {
        if (wins + losses == 0) {
            return 0;
        }
        return ((wins * 100) / (wins + losses));
    }
    // 승률 계산하는 메서드
    public int calculateWinRate() {
        return 0;
    }

    // ===================================================================
    // KDA 계산하는 메서드
    public BigDecimal calculateKda(Account linkedAccount, int queue) {
        int kills = 0;
        int deaths = 0;
        int assists = 0;
        int daysBefore = 30;

        long startTime = calculateStartTime(daysBefore);    // 빠른 대전일 때, 조회할 기간의 시작 시점; 한 달(30일) 전
//        queue = 490;        // 빠른 대전의 specific queue id; 490

        AccountRegion region = linkedAccount.getRegion();
        String puuid = linkedAccount.getAccountDetail().getPuuid();
        String summonerName = linkedAccount.getSummonerName();
        String tagLine = linkedAccount.getTagLine();

        // 한 달간 플레이한 최대 20판의 queue 타입 대전 id
        List<String> matchIds = riotClient.extractMatchIds(startTime, null, queue, null, 0, 20, region, puuid);

        List<FormattedMatchResponse> matchDetails = new ArrayList<>();
        for (String matchId : matchIds) {
            FormattedMatchResponse matchDetail = riotClient.getMatchDetails(matchId, summonerName, tagLine, region);

            // K/D/A
            kills += matchDetail.getKills();
            deaths += matchDetail.getDeaths();
            assists += matchDetail.getAssists();

        }
        BigDecimal totalKills = BigDecimal.valueOf(kills);
        BigDecimal totalDeaths = BigDecimal.valueOf(deaths);
        BigDecimal totalAssists = BigDecimal.valueOf(assists);

        Kda.of(
                 BigDecimal.valueOf(kills / matchIds.size()),
                BigDecimal.valueOf(assists / matchIds.size()),
                BigDecimal.valueOf(deaths / matchIds.size())
        );

        BigDecimal kda;
        if (totalDeaths.compareTo(BigDecimal.ZERO) == 0) {
            kda = totalKills.add(totalAssists);
        }

        kda = totalKills.add(totalAssists)  // kill + assist
                .divide(totalDeaths, 2, RoundingMode.HALF_UP);  // 나누기
        return kda;
    }

    // ===================================================================
    // 최근 챔피언 계산하는 메서드


    // ===================================================================
    // ===================================================================

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
