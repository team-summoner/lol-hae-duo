package com.summoner.lolhaeduo.domain.account.service;

import com.summoner.lolhaeduo.client.service.RiotClientService;
import com.summoner.lolhaeduo.common.event.AccountGameDataEvent;
import com.summoner.lolhaeduo.domain.account.dto.LinkAccountRequest;
import com.summoner.lolhaeduo.domain.account.entity.Account;
import com.summoner.lolhaeduo.domain.account.entity.AccountDetail;
import com.summoner.lolhaeduo.domain.account.repository.AccountRepository;
import com.summoner.lolhaeduo.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import static com.summoner.lolhaeduo.domain.account.enums.AccountType.RIOT;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final MemberRepository memberRepository;
    private final AccountRepository accountRepository;
    private final RiotClientService riotClientService;
    private final ApplicationEventPublisher eventPublisher;

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

        if (request.getAccountType().equals(RIOT)) {
            AccountDetail newAccountDetail = riotClientService.createAccountDetail(request);

            Account newAccount = Account.of(
                    request.getAccountId(),
                    request.getAccountPassword(),
                    RIOT,
                    request.getSummonerName(),
                    request.getTagLine(),
                    request.getServer(),
                    newAccountDetail,
                    memberId
            );

            // 이벤트 발생
            eventPublisher.publishEvent(new AccountGameDataEvent(newAccount.getId()));
            accountRepository.save(newAccount);
        }
    }
}
