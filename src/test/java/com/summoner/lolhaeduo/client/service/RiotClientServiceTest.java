package com.summoner.lolhaeduo.client.service;

import com.summoner.lolhaeduo.client.dto.RankStats;
import com.summoner.lolhaeduo.client.repository.FavoriteRepository;
import com.summoner.lolhaeduo.client.repository.VersionRepository;
import com.summoner.lolhaeduo.client.riot.RiotClient;
import com.summoner.lolhaeduo.common.util.TimeUtil;
import com.summoner.lolhaeduo.domain.account.entity.Account;
import com.summoner.lolhaeduo.domain.account.entity.AccountDetail;
import com.summoner.lolhaeduo.domain.account.enums.AccountServer;
import com.summoner.lolhaeduo.domain.account.enums.AccountType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class RiotClientServiceTest {

    @Autowired
    private RiotClientService riotClientService;

    @Autowired
    private RiotClient riotClient;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private VersionRepository versionRepository;

    @Autowired
    private TimeUtil timeUtil;

    private Account account;

    @BeforeEach
    void setUp() {
        AccountDetail accountDetail = AccountDetail.of("G35Q0FofPb45V1xvtES_lVcHlXvE7Hu8d1K_rBesTvPR0taAuKW6zRIHuFmr22zBhhwpk_s-pVpEbw",
                "RtIo7eNpSEbpw2Wp4V-C6i0Jez89ukfRZZbtO_kbqmmsMhLkgt0bLbwK",
                "nF894veNL_hEHiq--woe0ZKTVEQJYrzKtPYKuol2Mpur3m52Afsp84hjcQ"
                );
        account = Account.of("user1", "1234", AccountType.RIOT, "yokxim", "1323", AccountServer.KR, accountDetail, 1L);

    }

    @Test
    @DisplayName("getRankGameStats - 랭크 게임 통계 계산 테스트")
    void testGetRankGameStats() {
        // given
        AccountServer server = AccountServer.KR;

        // when
        RankStats rankStats = riotClientService.getRankGameStats(account, server);

        // then
        assertThat(rankStats).isNotNull();
        assertThat(rankStats.getSoloTotalGames()).isGreaterThanOrEqualTo(0);
        assertThat(rankStats.getFlexTotalGames()).isGreaterThanOrEqualTo(0);
        assertThat(rankStats.getSoloWinRate()).isBetween(0.0, 100.0);
        assertThat(rankStats.getFlexWinRate()).isBetween(0.0, 100.0);
    }
}