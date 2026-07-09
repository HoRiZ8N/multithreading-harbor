package com.inno.harbor.main;

import com.inno.harbor.entity.Ship;
import com.inno.harbor.port.Port;
import com.inno.harbor.parser.DataParser;
import com.inno.harbor.reader.DataReader;
import com.inno.harbor.validator.DataValidator;
import com.inno.harbor.config.HarborConfig;
import com.inno.harbor.shiphandler.ShipHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    public static void main(String[] args) throws IOException, InterruptedException {
        List<String> lines = new DataReader().read("data.txt");
        new DataValidator().validate(lines);
        HarborConfig config = new DataParser().parse(lines);

        Port.setBerthCount(config.berthCount());
        Port.setWarehouseCapacity(config.warehouseCapacity());
        Port.setWarehouseInitial(config.warehouseInitial());
        Port.getInstance();

        List<Ship> ships = config.ships();
        ExecutorService executor = Executors.newFixedThreadPool(ships.size());

        for (Ship ship : ships) {
            executor.execute(new ShipHandler(ship));
        }

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, java.util.concurrent.TimeUnit.NANOSECONDS);
        LOGGER.info("All ships have been serviced. Port operations complete.");
    }
}