package com.inno.harbor.parser;

import com.inno.harbor.entity.Ship;
import com.inno.harbor.config.HarborConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class DataParser {

    private static final Logger LOGGER = LogManager.getLogger(DataParser.class);

    public HarborConfig parse(List<String> lines) {
        int berthCount = 0;
        int warehouseCapacity = 0;
        int warehouseInitial = 0;
        List<Ship> ships = new ArrayList<>();

        for (String line : lines) {
            if (line.startsWith("berths=")) {
                berthCount = Integer.parseInt(line.substring("berths=".length()));
            } else if (line.startsWith("warehouse_capacity=")) {
                warehouseCapacity = Integer.parseInt(line.substring("warehouse_capacity=".length()));
            } else if (line.startsWith("warehouse_initial=")) {
                warehouseInitial = Integer.parseInt(line.substring("warehouse_initial=".length()));
            } else if (line.startsWith("ship=")) {
                String[] parts = line.substring("ship=".length()).split(",");
                ships.add(new Ship(
                        parts[0],
                        Integer.parseInt(parts[1]),
                        Integer.parseInt(parts[2]),
                        Integer.parseInt(parts[3]),
                        Integer.parseInt(parts[4])
                ));
                LOGGER.debug("Parsed ship: {}", parts[0]);
            }
        }

        LOGGER.info("Data parsed: {} berths, warehouse {}/{}, {} ships",
                berthCount, warehouseInitial, warehouseCapacity, ships.size());
        return new HarborConfig(berthCount, warehouseCapacity, warehouseInitial, ships);
    }
}