package com.summoner.lolhaeduo.domain.member.repository;

import com.summoner.lolhaeduo.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
