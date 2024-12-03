package com.summoner.lolhaeduo.domain.duo.entity;

import java.time.LocalDateTime;

import com.summoner.lolhaeduo.common.entity.Timestamped;
import com.summoner.lolhaeduo.domain.duo.enums.Lane;
import com.summoner.lolhaeduo.domain.duo.enums.QueueType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "duo")
public class Duo extends Timestamped {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

	@Enumerated(EnumType.STRING)
	private QueueType queueType;

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

	private String memberId;

	private String accountId;

	private LocalDateTime deletedAt;

	private Duo(QueueType queueType, Lane primaryRole, String primaryChamp, Lane secondaryRole, String secondaryChamp,
		Lane targetRole, String memo, Boolean mic, String memberId, String accountId) {
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

	private Duo(QueueType queueType, Lane primaryRole, Lane targetRole, String memo, Boolean mic, String memberId,
		String accountId) {
		this.queueType = queueType;
		this.primaryRole = primaryRole;
		this.targetRole = targetRole;
		this.memo = memo;
		this.mic = mic;
		this.memberId = memberId;
		this.accountId = accountId;
	}

	public static Duo quickOf(QueueType queueType, Lane primaryRole, String primaryChamp, Lane secondaryRole,
		String secondaryChamp, Lane targetRole, String memo, Boolean mic, String memberId, String accountId) {
		return new Duo(queueType, primaryRole, primaryChamp, secondaryRole, secondaryChamp, targetRole, memo, mic,
			memberId, accountId);
	}

	public static Duo rankOf(QueueType queueType, Lane primaryRole, Lane targetRole, String memo, Boolean mic,
		String memberId, String accountId) {
		return new Duo(queueType, primaryRole, targetRole, memo, mic, memberId, accountId);
	}

}
