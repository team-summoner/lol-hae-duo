package com.summoner.lolhaeduo.domain.account.entity;

import java.util.UUID;

import com.summoner.lolhaeduo.common.entity.Timestamped;
import com.summoner.lolhaeduo.domain.account.enums.AccountType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "account")
public class Account extends Timestamped {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

	@Column(unique = true, nullable = false)
	private String username;

	@Column(nullable = false)
	private String password;

	@Enumerated(EnumType.STRING)
	private AccountType accountType;

	@Column(nullable = false)
	private String summonerName;

	@Column(nullable = false)
	private String memberId;

	private Account(String username, String password, AccountType accountType, String summonerName, String memberId) {
		this.username = username;
		this.password = password;
		this.accountType = accountType;
		this.summonerName = summonerName;
		this.memberId = memberId;
	}

	public static Account of(String username, String password, AccountType accountType, String summonerName, String memberId) {
		return new Account(username, password, accountType, summonerName, memberId);
	}
}
