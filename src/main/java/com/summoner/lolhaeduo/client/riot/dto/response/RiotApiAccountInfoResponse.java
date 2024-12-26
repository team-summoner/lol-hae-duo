package com.summoner.lolhaeduo.client.riot.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RiotApiAccountInfoResponse {

    private String encryptedPuuid;

    private String encryptedAccountId;

    private String encryptedSummonerId;

    private RiotApiAccountInfoResponse(String encryptedPuuid, String encryptedAccountId, String encryptedSummonerId) {
        this.encryptedPuuid = encryptedPuuid;
        this.encryptedAccountId = encryptedAccountId;
        this.encryptedSummonerId = encryptedSummonerId;
    }

    public static RiotApiAccountInfoResponse of(String encryptedPuuid, String encryptedAccountId, String encryptedSummonerId) {
        return new RiotApiAccountInfoResponse(encryptedPuuid, encryptedAccountId, encryptedSummonerId);
    }
}