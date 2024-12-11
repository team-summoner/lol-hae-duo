package com.summoner.lolhaeduo.domain.account.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
public class AccountDetail {

    private String puuid;
    private int profileIconId;
    private String encryptedAccountId;
    private String encryptedSummonerId;

    private AccountDetail(String puuid, int profileIconId, String encryptedAccountId, String encryptedSummonerId) {
        this.puuid = puuid;
        this.profileIconId = profileIconId;
        this.encryptedAccountId = encryptedAccountId;
        this.encryptedSummonerId = encryptedSummonerId;
    }

    public static AccountDetail of(String puuid, int profileIconId, String encryptedAccountId, String encryptedSummonerId) {
        return new AccountDetail(puuid, profileIconId, encryptedAccountId, encryptedSummonerId);
    }
}
