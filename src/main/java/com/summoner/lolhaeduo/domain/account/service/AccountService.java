package com.summoner.lolhaeduo.domain.account.service;

import com.summoner.lolhaeduo.client.dto.PuuidResponse;
import com.summoner.lolhaeduo.client.dto.SummonerResponse;
import com.summoner.lolhaeduo.client.riot.RiotClient;
import com.summoner.lolhaeduo.client.service.RiotClientService;
import com.summoner.lolhaeduo.domain.account.dto.LinkAccountRequest;
import com.summoner.lolhaeduo.domain.account.entity.Account;
import com.summoner.lolhaeduo.domain.account.entity.AccountDetail;
import com.summoner.lolhaeduo.domain.account.entity.AccountGameData;
import com.summoner.lolhaeduo.domain.account.enums.AccountType;
import com.summoner.lolhaeduo.domain.account.repository.AccountRepository;
import com.summoner.lolhaeduo.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final MemberRepository memberRepository;
    private final AccountRepository accountRepository;
    private final RiotClient riotClient;

    /**
     * link account (for now, members can only link with RIOT account)
     * @param request accountType, accountId, accountPassword, summonerName, tagLine, server
     * @param memberId from token
     */
    public void linkAccount(LinkAccountRequest request, Long memberId) {
        memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("Not Found Member"));

        if (accountRepository.isMemberAccountLimitExceeded(memberId)) {
            throw new IllegalArgumentException("Account limit(3) exceeded");
        }

        if (accountRepository.existsByUsername(request.getAccountId())) {
            throw new IllegalArgumentException("Account already exists");
        }

        if (request.getAccountType().equals(AccountType.RIOT)) {

            PuuidResponse puuidResponse = riotClient.extractPuuid(
                    request.getSummonerName(),
                    request.getTagLine(),
                    request.getServer().getRegion()
            );

            SummonerResponse summonerResponse = riotClient.extractSummonerInfo(
                    puuidResponse.getPuuid(),
                    request.getServer()
            );

            AccountDetail newAccountDetail = AccountDetail.of(
                    puuidResponse.getPuuid(),
                    summonerResponse.getProfileIconId(),
                    summonerResponse.getAccountId(),
                    summonerResponse.getId()
            );

            AccountGameData accountGameData = retrieveData();

            Account newAccount = Account.of(
                    request.getAccountId(),
                    request.getAccountPassword(),
                    AccountType.RIOT,
                    request.getSummonerName(),
                    request.getTagLine(),
                    request.getServer(),
                    newAccountDetail,
                    accountGameData,
                    memberId
            );

            accountRepository.save(newAccount);
        }
    }

    // 초기 데이터 불러오기 (추후 비동기 처리로 개발 예정)
    private AccountGameData retrieveData() {
        return null;
    }
}
