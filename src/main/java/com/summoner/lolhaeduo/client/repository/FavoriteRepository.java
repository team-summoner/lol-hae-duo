package com.summoner.lolhaeduo.client.repository;

import com.summoner.lolhaeduo.client.entity.Favorite;
import com.summoner.lolhaeduo.domain.duo.enums.QueueType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    void deleteByAccountIdAndQueueType(Long accountId, QueueType queueType);
    List<Favorite> findTop3ByAccountIdAndQueueTypeOrderByPlayCountDesc(Long id, QueueType queueType);
}
