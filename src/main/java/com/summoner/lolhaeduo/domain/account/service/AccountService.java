package com.summoner.lolhaeduo.domain.account.service;

import com.summoner.lolhaeduo.client.dto.PuuidResponse;
import com.summoner.lolhaeduo.client.dto.SummonerResponse;
import com.summoner.lolhaeduo.client.riot.RiotUtil;
import com.summoner.lolhaeduo.domain.account.dto.LinkAccountRequest;
import com.summoner.lolhaeduo.domain.account.entity.Account;
import com.summoner.lolhaeduo.domain.account.entity.AccountDetail;
import com.summoner.lolhaeduo.domain.account.enums.AccountType;
import com.summoner.lolhaeduo.domain.account.repository.AccountRepository;
import com.summoner.lolhaeduo.domain.member.entity.Member;
import com.summoner.lolhaeduo.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final MemberRepository memberRepository;
    private final AccountRepository accountRepository;
    private final RiotUtil riotUtil;

    /**
     * link account (for now, members can only link with RIOT account)
     * @param request accountType, accountId, accountPassword, summonerName, tagLine, server
     * @param memberId from token
     */
    public void linkAccount(LinkAccountRequest request, Long memberId) {
        memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("Not Found Member"));

        if (request.getAccountType().equals(AccountType.RIOT)) {

            PuuidResponse puuidResponse = riotUtil.extractPuuid(
                    request.getSummonerName(),
                    request.getTagLine(),
                    request.getServer().getRegion()
            );

            SummonerResponse summonerResponse = riotUtil.extractSummonerInfo(
                    puuidResponse.getPuuid(),
                    request.getServer()
            );

            AccountDetail newAccountDetail = AccountDetail.of(
                    puuidResponse.getPuuid(),
                    summonerResponse.getAccountId(),
                    summonerResponse.getId()
            );

            Account newAccount = Account.of(
                    request.getAccountId(),
                    request.getAccountPassword(),
                    AccountType.RIOT,
                    request.getSummonerName(),
                    request.getTagLine(),
                    request.getServer(),
                    newAccountDetail,
                    memberId
            );

            accountRepository.save(newAccount);
        }
    }
}
