package com.summoner.lolhaeduo.client.repository;

import com.summoner.lolhaeduo.client.entity.Version;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VersionRepository extends JpaRepository<Version, Long> {
}
