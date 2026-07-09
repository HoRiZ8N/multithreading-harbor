package com.inno.harbor.state;

import com.inno.harbor.entity.Ship;
import com.inno.harbor.entity.Storage;
import com.inno.harbor.port.Port;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoadingState implements ShipState {

    private static final Logger LOGGER = LogManager.getLogger(LoadingState.class);

    @Override
    public void handle(Ship ship) {
        LOGGER.info("Ship '{}' started loading ({} containers)", ship.getName(), ship.getContainersToLoad());
        Storage storage = Port.getInstance().getStorage();

        while (ship.getContainersToLoad() > 0) {
            int freeSpace = ship.getCapacity() - ship.getCurrentContainers();
            int requested = Math.min(ship.getContainersToLoad(), freeSpace);
            if (requested <= 0) {
                LOGGER.warn("Ship '{}' is full, cannot load more containers", ship.getName());
                break;
            }
            int loaded = storage.retrieve(requested);
            ship.addContainers(loaded);
            ship.setContainersToLoad(ship.getContainersToLoad() - loaded);
        }

        LOGGER.info("Ship '{}' finished loading. On board: {}/{}", ship.getName(),
                ship.getCurrentContainers(), ship.getCapacity());
        ship.transitionTo(StateType.DEPARTING);
    }
}
