package com.summoner.lolhaeduo.domain.duo.dto;

import com.summoner.lolhaeduo.domain.duo.entity.Duo;
import com.summoner.lolhaeduo.domain.duo.entity.Kda;
import com.summoner.lolhaeduo.domain.duo.enums.Lane;
import com.summoner.lolhaeduo.domain.duo.enums.QueueType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class DuoUpdateResponse {
    private Long duoId;                 // 듀오 찾기의 ID
    private QueueType queueType;        // 큐 타입
    private Lane primaryRole;           // 주 역할군
    private String primaryChamp;        // 주 역할군의 선호 챔피언
    private Lane secondaryRole;         // 부 역할군
    private String secondaryChamp;      // 부 역할군의 선호 챔피언
    private Lane targetRole;            // 선호하는 매칭 역할군
    private String memo;                // 메모
    private Boolean mic;                // 마이크
    private String tier;                // 신청한 유저의 티어
    private String ranks;               // 신청한 유저의 랭크
    private int wins;
    private int losses;
    private String favoritesChamp;
    private String profileIcon;
    private Kda kda;
    private Long memberId;              // 신청한 유저의 ID
    private Long accountId;             // 신청한 유저의 연동 계정 ID
    private LocalDateTime createAt;     // 듀오 찾기 생성 일자
    private LocalDateTime modifiedAt;   // 듀오 찾기 수정 일자

    public DuoUpdateResponse(Long duoId, QueueType queueType, Lane primaryRole, String primaryChamp,
                             Lane secondaryRole, String secondaryChamp, Lane targetRole, String memo, Boolean mic,
                             String tier, String ranks, int wins, int losses, String favoritesChamp, String profileIcon,
                             Kda kda, Long memberId, Long accountId, LocalDateTime createAt, LocalDateTime modifiedAt) {
        this.duoId = duoId;
        this.queueType = queueType;
        this.primaryRole = primaryRole;
        this.primaryChamp = primaryChamp;
        this.secondaryRole = secondaryRole;
        this.secondaryChamp = secondaryChamp;
        this.targetRole = targetRole;
        this.memo = memo;
        this.mic = mic;
        this.tier = tier;
        this.ranks = ranks;
        this.wins = wins;
        this.losses = losses;
        this.favoritesChamp = favoritesChamp;
        this.profileIcon = profileIcon;
        this.kda = kda;
        this.memberId = memberId;
        this.accountId = accountId;
        this.createAt = createAt;
        this.modifiedAt = modifiedAt;
    }

    public static DuoUpdateResponse from(Duo duo) {
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
                duo.getMemberId(),
                duo.getAccountId(),
                duo.getCreatedAt(),
                duo.getModifiedAt()
        );
    }
}
