package com.summoner.lolhaeduo.domain.duo.repository;

import com.summoner.lolhaeduo.domain.duo.entity.Duo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DuoRepository extends JpaRepository<Duo, Long> {
}
