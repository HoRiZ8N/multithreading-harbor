package com.inno.harbor.state;

import com.inno.harbor.entity.Ship;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DockedState implements ShipState {

    private static final Logger LOGGER = LogManager.getLogger(DockedState.class);

    @Override
    public void handle(Ship ship) {
        LOGGER.info("Ship '{}' is docked. Planning: toUnload={}, toLoad={}",
                ship.getName(), ship.getContainersToUnload(), ship.getContainersToLoad());
        if (ship.getContainersToUnload() > 0) {
            ship.transitionTo(StateType.UNLOADING);
        } else if (ship.getContainersToLoad() > 0) {
            ship.transitionTo(StateType.LOADING);
        } else {
            ship.transitionTo(StateType.DEPARTING);
        }
    }
}
