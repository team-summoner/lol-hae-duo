package com.summoner.lolhaeduo.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FormattedMatchResponse {
    private String championName;
    private int kills;
    private int deaths;
    private int assists;
    private boolean win;
}
