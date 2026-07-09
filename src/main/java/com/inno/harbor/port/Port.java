package com.inno.harbor.port;

import com.inno.harbor.entity.Berth;
import com.inno.harbor.entity.Storage;
import com.inno.harbor.exception.PortException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

public class Port {

    private static final Logger LOGGER = LogManager.getLogger(Port.class);

    private static final AtomicReference<Port> INSTANCE = new AtomicReference<>();
    private static final ReentrantLock lock = new ReentrantLock();

    private final List<Berth> berths;
    private final Storage warehouse;
    private final Semaphore berthSemaphore;
    private final ReentrantLock berthAccessLock;

    private static int berthCount;
    private static int warehouseCapacity;
    private static int warehouseInitial;

    private Port(int berthCount, int warehouseCapacity, int warehouseInitial) {
        this.berths = new ArrayList<>();
        for (int i = 1; i <= berthCount; i++) {
            berths.add(new Berth(i));
        }
        this.warehouse = new Storage(warehouseCapacity, warehouseInitial);
        this.berthSemaphore = new Semaphore(berthCount, true);
        this.berthAccessLock = new ReentrantLock();
        LOGGER.info("Port created: {} berths, warehouse {}/{}", berthCount, warehouseInitial, warehouseCapacity);
    }

    public static void setBerthCount(int value)       { berthCount = value; }
    public static void setWarehouseCapacity(int value) { warehouseCapacity = value; }
    public static void setWarehouseInitial(int value)  { warehouseInitial = value; }

    public static Port getInstance() {
        Port port = INSTANCE.get();
        if (port == null) {
            lock.lock();
            try {
                port = INSTANCE.get();
                if (port == null) {
                    port = new Port(berthCount, warehouseCapacity, warehouseInitial);
                    INSTANCE.set(port);
                }
            } finally {
                lock.unlock();
            }
        }
        return port;
    }

    public Berth acquireBerth() {
        try {
            berthSemaphore.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PortException("Interrupted while waiting for a berth", e);
        }
        berthAccessLock.lock();
        try {
            for (Berth berth : berths) {
                if (berth.isAvailable()) {
                    berth.occupy();
                    return berth;
                }
            }
        } finally {
            berthAccessLock.unlock();
        }
        throw new PortException("No free berth found after semaphore acquisition");
    }

    public void releaseBerth(Berth berth) {
        berthAccessLock.lock();
        try {
            berth.free();
        } finally {
            berthAccessLock.unlock();
        }
        berthSemaphore.release();
        LOGGER.info("Berth #{} released", berth.getId());
    }

    public Storage getStorage() {
        return warehouse;
    }
}
