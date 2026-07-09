package com.inno.harbor.config;

import com.inno.harbor.entity.Ship;

import java.util.List;

public record HarborConfig(
        int berthCount,
        int warehouseCapacity,
        int warehouseInitial,
        List<Ship> ships
) {}
