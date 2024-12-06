package com.summoner.lolhaeduo.client.dto;

import lombok.Data;

@Data
public class MatchResponse {
    private MetadataResponse metadata;
    private InfoResponse info;
}
