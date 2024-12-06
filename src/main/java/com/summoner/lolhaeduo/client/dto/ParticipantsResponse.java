package com.summoner.lolhaeduo.client.dto;

import lombok.Data;

@Data
public class ParticipantsResponse {
    private String riotIdGameName;
    private String riotIdTagline;
    private int assists;
    private String championName;
    private int deaths;
    private int kills;
    private boolean win;
}
