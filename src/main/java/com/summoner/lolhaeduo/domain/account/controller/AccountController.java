package com.summoner.lolhaeduo.domain.account.controller;


import com.summoner.lolhaeduo.domain.account.dto.LinkAccountRequest;
import com.summoner.lolhaeduo.domain.account.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
     * 토큰이 어떤 방식으로 전달되는지는 필터가 만들어지고 난 후에 수정해야 할것 같다.
     * 토큰에 멤버 id가 저장되어 있다는 가정으로 하려고 한다.
     */
    @PostMapping("/link/riot")
    public ResponseEntity<Void> linkAccount(@RequestBody @Valid LinkAccountRequest request, Long memberId) {
        accountService.linkAccount(request, memberId);
        return ResponseEntity.ok().build();
    }
}
