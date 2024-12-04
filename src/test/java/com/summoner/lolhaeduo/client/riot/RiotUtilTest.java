package com.summoner.lolhaeduo.client.riot;

import com.summoner.lolhaeduo.client.dto.PuuidResponse;
import com.summoner.lolhaeduo.client.dto.SummonerResponse;
import com.summoner.lolhaeduo.domain.account.enums.AccountRegion;
import com.summoner.lolhaeduo.domain.account.enums.AccountServer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RiotUtilTest {

    @Autowired
    private RiotUtil riotUtil;

    @Test
    @DisplayName("Extract PUUID using a riot API: SUCCESS")
    void test1() {
        // given
        String summonerName = "진주 갈리오";
        String tagLine = "KR1";
        AccountRegion region = AccountRegion.ASIA;

        // when
        PuuidResponse response = riotUtil.extractPuuid(summonerName, tagLine, region);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getPuuid()).isNotEmpty();
        assertThat(response.getGameName()).isEqualTo(summonerName);
        assertThat(response.getTagLine()).isEqualTo(tagLine);
    }

    @Test
    @DisplayName("Extract summonerInfo using PUUID: SUCCESS")
    void test2() {
        // given
        String puuid = "CY8LOxe0cw7yiD6h2wK5ef4iezjXM4e1ZQyU2-EoF17L5YrWisnjeO4WxOEY3C_4tRa3AP1M3rdsfw";
        AccountServer server = AccountServer.KR;

        // when
        SummonerResponse response = riotUtil.extractSummonerInfo(puuid, server);

        // then
        assertThat(response).isNotNull();
    }
}