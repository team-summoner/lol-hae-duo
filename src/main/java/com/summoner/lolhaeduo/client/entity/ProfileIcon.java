package com.summoner.lolhaeduo.client.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class ProfileIcon {

    @Id
    private Integer id;

    private ProfileIcon(Integer id) {
        this.id = id;
    }

    public static ProfileIcon of(Integer id) {
        return new ProfileIcon(id);
    }
}
