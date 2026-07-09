package com.inno.harbor.state;

import com.inno.harbor.entity.Berth;
import com.inno.harbor.entity.Ship;
import com.inno.harbor.port.Port;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class WaitingState implements ShipState {

    private static final Logger LOGGER = LogManager.getLogger(WaitingState.class);

    @Override
    public void handle(Ship ship) {
        LOGGER.info("Ship '{}' is waiting for a free berth", ship.getName());
        Berth berth = Port.getInstance().acquireBerth();
        ship.setBerth(berth);
        LOGGER.info("Ship '{}' docked at berth #{}", ship.getName(), berth.getId());
        ship.transitionTo(StateType.DOCKED);
    }
}
