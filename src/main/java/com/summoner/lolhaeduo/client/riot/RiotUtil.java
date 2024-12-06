package com.summoner.lolhaeduo.client.riot;

import com.summoner.lolhaeduo.client.dto.LeagueEntryResponse;
import com.summoner.lolhaeduo.client.dto.PuuidResponse;
import com.summoner.lolhaeduo.client.dto.SummonerResponse;
import com.summoner.lolhaeduo.domain.account.enums.AccountRegion;
import com.summoner.lolhaeduo.domain.account.enums.AccountServer;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class RiotUtil {

    private final RestTemplate restTemplate;
    private final Map<String, String> regionBaseUrls = Map.of(
            "ASIA", "https://asia.api.riotgames.com",
            "AMERICAS", "https://americas.api.riotgames.com",
            "EUROPE", "https://europe.api.riotgames.com"
    );

    @Value("${riot.api.key}")
    private String apiKey;

    public PuuidResponse extractPuuid(String summonerName, String tagLine, AccountRegion region) {
        // Choose regional URL
        String baseUrl = regionBaseUrls.getOrDefault(region.toString(), null);
        if (baseUrl == null) {
            throw new IllegalArgumentException("Invalid region specified: " + region);
        }

        // Set request URL
        String url = String.format(
                "%s/riot/account/v1/accounts/by-riot-id/%s/%s?api_key=%s",
                baseUrl, summonerName, tagLine, apiKey
        );

        // Execute request
        return restTemplate.getForObject(url, PuuidResponse.class);
    }

    public SummonerResponse extractSummonerInfo(String puuid, AccountServer server) {
        // Server code
        String serverDomain = server.name().toLowerCase();

        // Set request URL
        String url = String.format(
                "https://%s.api.riotgames.com/lol/summoner/v4/summoners/by-puuid/%s?api_key=%s",
                serverDomain, puuid, apiKey
        );

        // Execute request
        return restTemplate.getForObject(url, SummonerResponse.class);
    }

    public LeagueEntryResponse extractLeagueInfo(String summonerId, AccountServer server) {
        String serverDomain = server.name().toLowerCase();

        String url = String.format(
                "https://%s.api.riotgames.com/lol/league/v4/entries/by-summoner/%s?api_key=%s",
                serverDomain, summonerId, apiKey
        );

        return restTemplate.getForObject(url, LeagueEntryResponse.class);
    }

}
