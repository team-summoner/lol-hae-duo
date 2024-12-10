package com.summoner.lolhaeduo.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChampionPlayCount {

    private final String championName;
    private final int playCount;
}
