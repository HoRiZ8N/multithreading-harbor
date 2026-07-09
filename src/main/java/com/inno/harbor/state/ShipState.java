package com.inno.harbor.state;

import com.inno.harbor.entity.Ship;

public interface ShipState {

    void handle(Ship ship);
}