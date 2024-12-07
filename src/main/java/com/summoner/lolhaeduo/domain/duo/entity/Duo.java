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
import java.util.ArrayList;
import java.util.List;

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

    @ElementCollection  // <duo_id, Lane>
    @CollectionTable(name = "TARGET_ROLE", joinColumns = @JoinColumn(name = "DUO_ID"))
    @Column
    @Enumerated(EnumType.STRING)
    private List<Lane> targetRoles = new ArrayList<>();

    private String memo;

    private Boolean mic;

    @Column(nullable = false)
    private String tier;

    @Column(nullable = false)
    private String rank;

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
                List<Lane> targetRoles, String memo, Boolean mic, Long memberId, Long accountId) {
        this.queueType = queueType;
        this.primaryRole = primaryRole;
        this.primaryChamp = primaryChamp;
        this.secondaryRole = secondaryRole;
        this.secondaryChamp = secondaryChamp;
        this.targetRoles = targetRoles;
        this.memo = memo;
        this.mic = mic;
        this.memberId = memberId;
        this.accountId = accountId;
    }

    private Duo(QueueType queueType, Lane primaryRole, String primaryChamp, Lane secondaryRole, String secondaryChamp,
                List<Lane> targetRoles, String memo, Boolean mic, String tier, String rank, int wins, int losses, Long memberId, Long accountId) {
        this.queueType = queueType;
        this.primaryRole = primaryRole;
        this.primaryChamp = primaryChamp;
        this.secondaryRole = secondaryRole;
        this.secondaryChamp = secondaryChamp;
        this.targetRoles = targetRoles;
        this.memo = memo;
        this.mic = mic;
        this.tier = tier;
        this.rank = rank;
        this.wins = wins;
        this.losses = losses;
        this.memberId = memberId;
        this.accountId = accountId;
    }

    public static Duo quickOf(QueueType queueType,
                              Lane primaryRole, String primaryChamp,
                              Lane secondaryRole, String secondaryChamp,
                              List<Lane> targetRoles,
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
                targetRoles,
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
                             Lane primaryRole, List<Lane> targetRoles,
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
                targetRoles,
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
                             Lane primaryRole, List<Lane> targetRoles,
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
                targetRoles,
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
                       List<Lane> targetRoles,
                       String memo, Boolean mic
    ) {
        this.queueType = queueType;
        this.primaryRole = primaryRole;
        this.primaryChamp = primaryChamp;
        this.secondaryRole = secondaryRole;
        this.secondaryChamp = secondaryChamp;
        this.targetRoles = targetRoles;
        this.memo = memo;
        this.mic = mic;
    }
}
