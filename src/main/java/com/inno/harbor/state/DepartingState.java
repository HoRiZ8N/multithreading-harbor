package com.inno.harbor.state;

import com.inno.harbor.entity.Ship;
import com.inno.harbor.port.Port;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DepartingState implements ShipState {

    private static final Logger LOGGER = LogManager.getLogger(DepartingState.class);

    @Override
    public void handle(Ship ship) {
        LOGGER.info("Ship '{}' is departing from berth #{}", ship.getName(), ship.getBerth().getId());
        Port.getInstance().releaseBerth(ship.getBerth());
        ship.setBerth(null);
        ship.setDone();
        LOGGER.info("Ship '{}' has left the port. Final load: {}/{}", ship.getName(),
                ship.getCurrentContainers(), ship.getCapacity());
    }
}
