package com.summoner.lolhaeduo.domain.duo.controller;

import com.summoner.lolhaeduo.common.annotation.Auth;
import com.summoner.lolhaeduo.common.dto.AuthMember;
import com.summoner.lolhaeduo.domain.duo.dto.DuoCreateRequest;
import com.summoner.lolhaeduo.domain.duo.dto.DuoCreateResponse;
import com.summoner.lolhaeduo.domain.duo.service.DuoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DuoController {

    private final DuoService duoService;

    @PostMapping("/duo")
    public ResponseEntity<DuoCreateResponse> createDuo(@RequestBody DuoCreateRequest createRequest, @Auth AuthMember authMember) {

        DuoCreateResponse duoCreateResponse = duoService.createDuo(createRequest,authMember.getMemberId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(duoCreateResponse);
    }
}
