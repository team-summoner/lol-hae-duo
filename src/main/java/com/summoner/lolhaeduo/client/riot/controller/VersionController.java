package com.summoner.lolhaeduo.client.riot.controller;

import com.summoner.lolhaeduo.client.riot.util.RiotClientUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class VersionController {

    private final RiotClientUtil riotClientUtil;

    @PostMapping("/version")
    public ResponseEntity<Void> updateVersion() {
        riotClientUtil.updateVersion();
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
