package com.summoner.lolhaeduo.domain.duo.entity;

import com.summoner.lolhaeduo.common.entity.Timestamped;
import com.summoner.lolhaeduo.domain.duo.enums.Lane;
import com.summoner.lolhaeduo.domain.duo.enums.QueueType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "duo")
public class Duo extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private QueueType queueType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Lane primaryRole;

    private String primaryChamp;

    @Enumerated(EnumType.STRING)
    private Lane secondaryRole;

    private String secondaryChamp;

    @Enumerated(EnumType.STRING)
    private Lane targetRole;

    private String memo;

    private Boolean mic;

    @Column(nullable = false)
    private String tier;

    @Column(nullable = false)
    private String ranks;

    @Column(nullable = false)
    private int wins;

    @Column(nullable = false)
    private int losses;

    private String favoritesChamp;

    private Long profileIcon;

    @Embedded
    private Kda kda;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private Long accountId;

    private LocalDateTime deletedAt;

    private Duo(QueueType queueType, Lane primaryRole, String primaryChamp, Lane secondaryRole, String secondaryChamp,
                Lane targetRole, String memo, Boolean mic, String tier, String ranks, int wins, int losses, Long memberId, Long accountId) {
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
        this.memberId = memberId;
        this.accountId = accountId;
    }

    public static Duo quickOf(QueueType queueType,
                              Lane primaryRole, String primaryChamp,
                              Lane secondaryRole, String secondaryChamp,
                              Lane targetRole,
                              String memo, Boolean mic,
                              String tier, String rank,
                              int wins, int losses, // 최근 20게임 승패
                              Long memberId, Long accountId) {
        return new Duo(
                queueType,
                primaryRole,
                primaryChamp,
                secondaryRole,
                secondaryChamp,
                targetRole,
                memo,
                mic,
                tier,
                rank,
                wins,
                losses,
                memberId,
                accountId
        );
    }

    public static Duo soloOf(QueueType queueType,
                             Lane primaryRole, Lane targetRole,
                             String memo, Boolean mic,
                             String tier, String rank,
                             int wins, int losses,  // League API 에서 호출한 시즌 승률 (솔로 랭크 = 개인 게임)
                             Long memberId, Long accountId) {
        return new Duo(
                queueType,
                primaryRole,
                null,
                null,
                null,
                targetRole,
                memo,
                mic,
                tier,
                rank,
                wins,
                losses,
                memberId,
                accountId
        );
    }


    public static Duo flexOf(QueueType queueType,
                             Lane primaryRole, Lane targetRole,
                             String memo, Boolean mic,
                             String tier, String rank,
                             int wins, int losses,  // League API 에서 호출한 시즌 승률 (자유 랭크 = 팀 게임)
                             Long memberId, Long accountId) {
        return new Duo(
                queueType,
                primaryRole,
                null,
                null,
                null,
                targetRole,
                memo,
                mic,
                tier,
                rank,
                wins,
                losses,
                memberId,
                accountId
        );
    }

    /*
    // 듀오 찾기 수정이 진행되면, 변경될 수 있는 부분입니다.
    */
    public void update(QueueType queueType,
                       Lane primaryRole, String primaryChamp,
                       Lane secondaryRole, String secondaryChamp,
                       Lane targetRole,
                       String memo, Boolean mic
    ) {
        this.queueType = queueType;
        this.primaryRole = primaryRole;
        this.primaryChamp = primaryChamp;
        this.secondaryRole = secondaryRole;
        this.secondaryChamp = secondaryChamp;
        this.targetRole = targetRole;
        this.memo = memo;
        this.mic = mic;
    }
}
