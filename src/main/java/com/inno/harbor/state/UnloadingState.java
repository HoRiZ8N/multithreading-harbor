package com.inno.harbor.state;

import com.inno.harbor.entity.Ship;
import com.inno.harbor.entity.Storage;
import com.inno.harbor.port.Port;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UnloadingState implements ShipState {

    private static final Logger LOGGER = LogManager.getLogger(UnloadingState.class);

    @Override
    public void handle(Ship ship) {
        LOGGER.info("Ship '{}' started unloading ({} containers)", ship.getName(), ship.getContainersToUnload());
        Storage storage = Port.getInstance().getStorage();

        while (ship.getContainersToUnload() > 0) {
            int stored = storage.store(ship.getContainersToUnload());
            ship.removeContainers(stored);
            ship.setContainersToUnload(ship.getContainersToUnload() - stored);
        }

        LOGGER.info("Ship '{}' finished unloading. On board: {}/{}", ship.getName(),
                ship.getCurrentContainers(), ship.getCapacity());

        if (ship.getContainersToLoad() > 0) {
            ship.transitionTo(StateType.LOADING);
        } else {
            ship.transitionTo(StateType.DEPARTING);
        }
    }
}
