package com.summoner.lolhaeduo.domain.duo.controller;

import com.summoner.lolhaeduo.common.annotation.Auth;
import com.summoner.lolhaeduo.common.dto.AuthMember;
import com.summoner.lolhaeduo.domain.duo.dto.*;
import com.summoner.lolhaeduo.domain.duo.enums.Lane;
import com.summoner.lolhaeduo.domain.duo.enums.QueueType;
import com.summoner.lolhaeduo.domain.duo.service.DuoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/duo")
@RequiredArgsConstructor
public class DuoController {

    private final DuoService duoService;
    @GetMapping()
    public ResponseEntity<Page<DuoListResponse>> getDuoList(
        @RequestParam(required = false) QueueType queueType,
        @RequestParam(required = false) Lane lane,
        @RequestParam(required = false) String tier,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size

    ){
        Pageable pageable = PageRequest.of(page, size);
        Page<DuoListResponse> duoList = duoService.getPagedDuoList(queueType, lane, tier,pageable);
        return ResponseEntity.ok(duoList);
    }

    @PostMapping("")
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
        if (!duoUpdateRequest.isQuickQueueTypeValid()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        Long memberId = authMember.getMemberId();
        DuoUpdateResponse response = duoService.update(memberId, duoId, duoUpdateRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @DeleteMapping("/duo/{duoId}")
    public ResponseEntity<Void> deleteDuo(@PathVariable Long duoId,
                                          @Auth AuthMember authMember) {
        // Duo 삭제 로직
        duoService.deleteDuoById(duoId, authMember);

        // HTTP 상태 코드 204 반환
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
