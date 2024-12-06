package com.summoner.lolhaeduo.client.dto;

import lombok.Data;

import java.util.List;

@Data
public class InfoResponse {
    private List<ParticipantsResponse> participants;
}
