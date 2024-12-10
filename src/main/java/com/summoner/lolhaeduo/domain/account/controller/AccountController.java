package com.summoner.lolhaeduo.domain.account.controller;

import com.summoner.lolhaeduo.common.annotation.Auth;
import com.summoner.lolhaeduo.common.dto.AuthMember;
import com.summoner.lolhaeduo.domain.account.dto.LinkAccountRequest;
import com.summoner.lolhaeduo.domain.account.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    /**
     * 반환값은 따로 없고, 인수 값으로 계정 연동에 필요한 입력값을 받는다.
     */
    @PostMapping("/link/riot")
    public ResponseEntity<Void> linkAccount(@RequestBody @Valid LinkAccountRequest request, @Auth AuthMember authMember) {
        Long memberId = authMember.getMemberId();
        accountService.linkAccount(request, memberId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
