package com.zpedroo.farmsword.objects;

import lombok.Data;
import org.bukkit.entity.EntityType;

@Data
public class FarmMob {

    private final EntityType entityType;
    private final double expAmount;
}