package com.inno.harbor.entity;

import com.inno.harbor.exception.PortException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Storage {

    private static final Logger LOGGER = LogManager.getLogger(Storage.class);

    private final int capacity;
    private int currentContainers;
    private final ReentrantLock lock;
    private final Condition notFull;
    private final Condition notEmpty;

    public Storage(int capacity, int initialContainers) {
        this.capacity = capacity;
        this.currentContainers = initialContainers;
        this.lock = new ReentrantLock();
        this.notFull = lock.newCondition();
        this.notEmpty = lock.newCondition();
    }

    public int store(int count) {
        lock.lock();
        try {
            while (currentContainers >= capacity) {
                LOGGER.debug("Storage full ({}/{}), waiting for free space…", currentContainers, capacity);
                try {
                    notFull.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new PortException("Interrupted while waiting for storage space", e);
                }
            }
            int freeSpace = capacity - currentContainers;
            int stored = Math.min(count, freeSpace);
            for (int i = 0; i < stored; i++) {
                currentContainers++;
                try {
                    TimeUnit.MILLISECONDS.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new PortException("Interrupted while storing containers", e);
                }
            }
            LOGGER.info("Stored {} containers in storage. Current: {}/{}", stored, currentContainers, capacity);
            notEmpty.signalAll();
            return stored;
        } finally {
            lock.unlock();
        }
    }

    public int retrieve(int count) {
        lock.lock();
        try {
            while (currentContainers <= 0) {
                LOGGER.debug("Storage empty, waiting for containers…");
                try {
                    notEmpty.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new PortException("Interrupted while waiting for containers", e);
                }
            }
            int retrieved = Math.min(count, currentContainers);
            for (int i = 0; i < retrieved; i++) {
                currentContainers--;
                try {
                    TimeUnit.MILLISECONDS.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new PortException("Interrupted while retrieving containers", e);
                }
            }
            LOGGER.info("Retrieved {} containers from storage. Current: {}/{}", retrieved, currentContainers, capacity);
            notFull.signalAll();
            return retrieved;
        } finally {
            lock.unlock();
        }
    }

    public int getCurrentContainers() {
        lock.lock();
        try {
            return currentContainers;
        } finally {
            lock.unlock();
        }
    }

    public int getCapacity() {
        return capacity;
    }
}
