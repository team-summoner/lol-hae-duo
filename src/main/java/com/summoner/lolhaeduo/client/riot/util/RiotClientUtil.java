package com.summoner.lolhaeduo.client.riot.util;

import com.summoner.lolhaeduo.client.riot.dto.request.*;
import com.summoner.lolhaeduo.client.riot.dto.response.RiotApiAccountInfoResponse;
import com.summoner.lolhaeduo.client.riot.dto.response.RiotApiMatchInfoResponse;
import com.summoner.lolhaeduo.client.riot.dto.response.RiotApiRankInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RiotClientUtil {

    private final RestTemplate restTemplate;
    private final String riotClientApiBaseUrl = "http://localhost:8081/riot-client";

    private HttpHeaders headers = new HttpHeaders();

    // RiotClientController - /account-infos [POST]
    public RiotApiAccountInfoResponse getAccountInfos(RiotApiAccountInfoRequest request) {
        String url = riotClientApiBaseUrl + "/account-infos";

        headers.set("Content-Type", "application/json");

        HttpEntity<RiotApiAccountInfoRequest> requestEntity = new HttpEntity<>(request, headers);

        ResponseEntity<RiotApiAccountInfoResponse> response = restTemplate.exchange(
                url, HttpMethod.POST, requestEntity, RiotApiAccountInfoResponse.class
        );

        return response.getBody();
    }

    // RiotClientController - /update-profile-icon [POST]
    public String getProfileIcon(RiotApiUpdateProfileRequest request) {
        String url = riotClientApiBaseUrl + "/update-profile-icon";

        headers.set("Content-Type", "application/json");

        HttpEntity<RiotApiUpdateProfileRequest> requestEntity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.POST, requestEntity, String.class
        );

        return response.getBody();
    }

    // RiotClientController - /rank-infos [POST]
    public RiotApiRankInfoResponse getRankInfos(RiotApiRankInfoRequest request) {
        String url = riotClientApiBaseUrl + "/rank-infos";

        headers.set("Content-Type", "application/json");

        HttpEntity<RiotApiRankInfoRequest> requestEntity = new HttpEntity<>(request, headers);

        ResponseEntity<RiotApiRankInfoResponse> response = restTemplate.exchange(
                url, HttpMethod.POST, requestEntity, RiotApiRankInfoResponse.class
        );

        return response.getBody();
    }

    // RiotClientController - /match-ids [POST]
    public List<String> getMatchIds(RiotApiMatchIdRequest request) {
        String url = riotClientApiBaseUrl + "/match-ids";

        headers.set("Content-Type", "application/json");

        HttpEntity<RiotApiMatchIdRequest> requestEntity = new HttpEntity<>(request, headers);

        ResponseEntity<List<String>> response = restTemplate.exchange(
                url, HttpMethod.POST, requestEntity, new ParameterizedTypeReference<>() {}
        );

        return response.getBody();
    }

    // RiotClientController - /match-ids [PUT]
    public List<String> updateMatchIds(RiotApiUpdateMatchIdRequest request) {
        String url = riotClientApiBaseUrl + "/match-ids";

        headers.set("Content-Type", "application/json");

        HttpEntity<RiotApiUpdateMatchIdRequest> requestEntity = new HttpEntity<>(request, headers);

        ResponseEntity<List<String>> response = restTemplate.exchange(
                url, HttpMethod.PUT, requestEntity, new ParameterizedTypeReference<>() {}
        );

        return response.getBody();
    }

    // RiotClientController - /match-infos [POST]
    public RiotApiMatchInfoResponse getMatchInfos(RiotApiMatchInfoRequest request) {
        String url = riotClientApiBaseUrl + "/match-infos";

        headers.set("Content-Type", "application/json");

        HttpEntity<RiotApiMatchInfoRequest> requestEntity = new HttpEntity<>(request, headers);

        ResponseEntity<RiotApiMatchInfoResponse> response = restTemplate.exchange(
                url, HttpMethod.POST, requestEntity, RiotApiMatchInfoResponse.class
        );

        return response.getBody();
    }

    // DataDragonController - /ddragon
    public void updateVersion() {
        String url = riotClientApiBaseUrl + "/ddragon";
        restTemplate.exchange(url, HttpMethod.GET, null, Void.class);
    }
}
