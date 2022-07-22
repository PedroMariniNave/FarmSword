package com.zpedroo.farmsword.objects;

import lombok.Data;
import org.bukkit.entity.EntityType;

import java.math.BigInteger;

@Data
public class FarmMob {

    private final EntityType entityType;
    private final double expAmount;
    private final BigInteger pointsAmount;
}