package com.summoner.lolhaeduo.client.riot.util;

import com.summoner.lolhaeduo.client.riot.dto.request.*;
import com.summoner.lolhaeduo.client.riot.dto.response.RiotApiAccountInfoResponse;
import com.summoner.lolhaeduo.client.riot.dto.response.RiotApiMatchInfoResponse;
import com.summoner.lolhaeduo.client.riot.dto.response.RiotApiRankInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RiotClientUtil {

    private final RestTemplate restTemplate;
    private final String riotClientApiBaseUrl = "http://riot-client-server-url/riot-client";

    // RiotClientController - /account-infos [POST]
    public RiotApiAccountInfoResponse getAccountInfos(RiotApiAccountInfoRequest request) {
        String url = riotClientApiBaseUrl + "/account-infos";
        ResponseEntity<RiotApiAccountInfoResponse> response = restTemplate.postForEntity(
                url, request, RiotApiAccountInfoResponse.class
        );
        return response.getBody();
    }

    // RiotClientController - /update-profile-icon [POST]
    public String getProfileIcon(RiotApiUpdateProfileRequest request) {
        String url = riotClientApiBaseUrl + "/update-profile-icon";
        ResponseEntity<String> response = restTemplate.postForEntity(
                url, request, String.class
        );
        return response.getBody();
    }

    // RiotClientController - /rank-infos [POST]
    public RiotApiRankInfoResponse getRankInfos(RiotApiRankInfoRequest request) {
        String url = riotClientApiBaseUrl + "/rank-infos";
        ResponseEntity<RiotApiRankInfoResponse> response = restTemplate.postForEntity(
                url, request, RiotApiRankInfoResponse.class
        );
        return response.getBody();
    }

    // RiotClientController - /match-ids [POST]
    public List<String> getMatchIds(RiotApiMatchIdRequest request) {
        String url = riotClientApiBaseUrl + "/match-ids";
        HttpEntity<RiotApiMatchIdRequest> requestEntity = new HttpEntity<>(request);
        ResponseEntity<List<String>> response = restTemplate.exchange(
                url, HttpMethod.POST, requestEntity, new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }

    // RiotClientController - /match-ids [PUT]
    public List<String> updateMatchIds(RiotApiUpdateMatchIdRequest request) {
        String url = riotClientApiBaseUrl + "/match-ids";
        HttpEntity<RiotApiUpdateMatchIdRequest> requestEntity = new HttpEntity<>(request);
        ResponseEntity<List<String>> response = restTemplate.exchange(
                url, HttpMethod.PUT, requestEntity, new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }

    // RiotClientController - /match-infos [POST]
    public RiotApiMatchInfoResponse getMatchInfos(RiotApiMatchInfoRequest request) {
        String url = riotClientApiBaseUrl + "/match-infos";
        ResponseEntity<RiotApiMatchInfoResponse> response = restTemplate.postForEntity(
                url, request, RiotApiMatchInfoResponse.class
        );
        return response.getBody();
    }
}
