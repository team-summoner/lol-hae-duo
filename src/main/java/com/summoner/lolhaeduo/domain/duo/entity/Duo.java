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

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Lane targetRole;

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
               Lane targetRole, String memo, Boolean mic, Long memberId, Long accountId) {
        this.queueType = queueType;
        this.primaryRole = primaryRole;
        this.primaryChamp = primaryChamp;
        this.secondaryRole = secondaryRole;
        this.secondaryChamp = secondaryChamp;
        this.targetRole = targetRole;
        this.memo = memo;
        this.mic = mic;
        this.memberId = memberId;
        this.accountId = accountId;
    }

    private Duo(QueueType queueType, Lane primaryRole, String primaryChamp, Lane secondaryRole, String secondaryChamp,
               Lane targetRole, String memo, Boolean mic, String tier, String rank, int wins, int losses, Long memberId, Long accountId) {
        this.queueType = queueType;
        this.primaryRole = primaryRole;
        this.primaryChamp = primaryChamp;
        this.secondaryRole = secondaryRole;
        this.secondaryChamp = secondaryChamp;
        this.targetRole = targetRole;
        this.memo = memo;
        this.mic = mic;
        this.tier = tier;
        this.rank = rank;
        this.wins = wins;
        this.losses = losses;
        this.memberId = memberId;
        this.accountId = accountId;
    }

    public static Duo quickOf(QueueType queueType, Lane primaryRole, String primaryChamp, Lane secondaryRole, String secondaryChamp, Lane targetRole, String memo, Boolean mic, Long memberId, Long accountId) {
        return new Duo(
                queueType,
                primaryRole,
                primaryChamp,
                secondaryRole,
                secondaryChamp,
                targetRole,
                memo,
                mic,
                memberId,
                accountId
        );
    }

    public static Duo rankOf(QueueType queueType, Lane primaryRole,Lane targetRole, String memo, Boolean mic, String tier, String rank, int wins, int losses, Long memberId, Long accountId) {
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




}
