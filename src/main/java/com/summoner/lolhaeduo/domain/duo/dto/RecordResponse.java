package com.summoner.lolhaeduo.domain.duo.dto;

import com.summoner.lolhaeduo.domain.duo.entity.Kda;
import lombok.Getter;

@Getter
public class RecordResponse {

    private int wins;       // 승리 횟수
    private int losses;     // 패배 횟수
    private int kills;      // 총 kill 횟수
    private int deaths;     // 총 death 횟수
    private int assists;    // 총 assist 횟수
    private int playCounts; // 게임 플레이 횟수
    private Kda kda;        // 각각의 평균 K/D/A를 담고 있는 Kda

    private RecordResponse(int wins, int losses, int kills, int deaths, int assists, int playCounts, Kda kda) {
        this.wins = wins;
        this.losses = losses;
        this.kills = kills;
        this.deaths = deaths;
        this.assists = assists;
        this.playCounts = playCounts;
        this.kda = kda;
    }

    public static RecordResponse of(int wins, int losses, int kills, int deaths, int assists, int playCounts, Kda kda) {
        return new RecordResponse(wins, losses, kills, deaths, assists, playCounts, kda);
    }
}
