package com.summoner.lolhaeduo.domain.duo.controller;

import com.summoner.lolhaeduo.common.annotation.Auth;
import com.summoner.lolhaeduo.common.dto.AuthMember;
import com.summoner.lolhaeduo.domain.duo.dto.DuoCreateRequest;
import com.summoner.lolhaeduo.domain.duo.dto.DuoCreateResponse;
import com.summoner.lolhaeduo.domain.duo.dto.DuoUpdateRequest;
import com.summoner.lolhaeduo.domain.duo.dto.DuoUpdateResponse;
import com.summoner.lolhaeduo.domain.duo.service.DuoService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/duo")
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

    @PutMapping("/{duoId}")
    public ResponseEntity<DuoUpdateResponse> update(
            @Auth AuthMember authMember,
            @PathVariable Long duoId,
            @Valid @RequestBody DuoUpdateRequest duoUpdateRequest) {
        if (!duoUpdateRequest.isFlexQueueTypeValid()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        Long memberId = authMember.getMemberId();
        DuoUpdateResponse response = duoService.update(memberId, duoId, duoUpdateRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    private final DuoService duoService;

    @DeleteMapping("/duo/{duoId}")
    public void deleteDuo(@PathVariable Long duoId,
                          @Auth AuthMember authMember
                          ) {
        // Duo 삭제 로직
        duoService.deleteDuoById(duoId,authMember);
    }
}
