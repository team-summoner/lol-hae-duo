package com.summoner.lolhaeduo.domain.duo.dto;

import com.summoner.lolhaeduo.domain.duo.enums.Lane;
import com.summoner.lolhaeduo.domain.duo.enums.QueueType;
import lombok.Getter;

@Getter
public class DuoCreateRequest {

    private Long accountId;
    private QueueType queueType;
    private Lane primaryRole;
    private String primaryChamp;
    private Lane secondaryRole;
    private String secondaryChamp;
    private Lane targetRole;
    private String memo;
    private Boolean mic;
    private Long profileIcon;


    public void validate() {
        // 여기서 null 체크
        //
    }
}
