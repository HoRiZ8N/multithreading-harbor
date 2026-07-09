package com.inno.harbor.shiphandler;

import com.inno.harbor.entity.Ship;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ShipHandler implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger(ShipHandler.class);

    private final Ship ship;

    public ShipHandler(Ship ship) {
        this.ship = ship;
    }

    @Override
    public void run() {
        LOGGER.info("Ship '{}' arrived at port: {}", ship.getName(), ship);
        while (!ship.isDone()) {
            ship.getState().handle(ship);
        }
        String result = String.format(
                "Ship '%s' fully serviced — containers on board: %d/%d",
                ship.getName(), ship.getCurrentContainers(), ship.getCapacity());
        LOGGER.info(result);
    }
}
