package com.summoner.lolhaeduo.domain.duo.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Embeddable
@Data
@NoArgsConstructor
public class Kda {

    private BigDecimal averageKills;
    private BigDecimal averageAssists;
    private BigDecimal averageDeaths;

    private Kda(BigDecimal averageKills, BigDecimal averageAssists, BigDecimal averageDeaths) {
        this.averageKills = averageKills;
        this.averageAssists = averageAssists;
        this.averageDeaths = averageDeaths;
    }

    public static Kda of(BigDecimal averageKills, BigDecimal averageAssists, BigDecimal averageDeaths) {
        return new Kda(averageKills, averageAssists, averageDeaths);
    }
}
