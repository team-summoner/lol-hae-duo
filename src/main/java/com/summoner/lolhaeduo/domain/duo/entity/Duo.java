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

    @Enumerated(EnumType.STRING)
    private QueueType queueType;

    @Enumerated(EnumType.STRING)
    private Lane primaryRole;

    @Column(nullable = false)
    private String primaryChamp;

    @Enumerated(EnumType.STRING)
    private Lane secondaryRole;

    @Column(nullable = false)
    private String secondaryChamp;

    @Enumerated(EnumType.STRING)
    private Lane targetRole;

    private String memo;

    private Boolean mic;

    private String tier;

    private String rank;

    private int wins;

    private int losses;

    private String favoritesChamp;

    @Embedded
    private Kda kda;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private Long accountId;

    private LocalDateTime deletedAt;




}
