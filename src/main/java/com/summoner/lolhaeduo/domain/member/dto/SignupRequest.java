package com.summoner.lolhaeduo.domain.member.dto;

import lombok.Getter;

@Getter
public class SignupRequest {
    private String username;
    private String password;
    private String passwordConfirmation;
}
