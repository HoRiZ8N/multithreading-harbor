package com.inno.harbor.entity;

import com.inno.harbor.state.ShipState;
import com.inno.harbor.state.StateType;
import com.inno.harbor.state.WaitingState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicInteger;

public class Ship implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger(Ship.class);

    private static final AtomicInteger ID_COUNTER = new AtomicInteger(1);

    private final int id;
    private final String name;
    private int currentContainers;
    private final int capacity;
    private int containersToLoad;
    private int containersToUnload;
    private ShipState state;
    private Berth berth;
    private boolean done;

    public Ship(String name, int currentContainers, int capacity,
                int containersToLoad, int containersToUnload) {
        this.id = ID_COUNTER.getAndIncrement();
        this.name = name;
        this.currentContainers = currentContainers;
        this.capacity = capacity;
        this.containersToLoad = containersToLoad;
        this.containersToUnload = containersToUnload;
        this.state = new WaitingState();
    }

    public int getId()                     { return id; }
    public String getName()               { return name; }
    public int getCurrentContainers()     { return currentContainers; }
    public int getCapacity()              { return capacity; }
    public int getContainersToLoad()      { return containersToLoad; }
    public int getContainersToUnload()    { return containersToUnload; }
    public ShipState getState()           { return state; }
    public Berth getBerth()              { return berth; }
    public boolean isDone()              { return done; }

    public void setState(ShipState state)             { this.state = state; }
    public void transitionTo(StateType type)          { this.state = type.createState(); }
    public void setBerth(Berth berth)                { this.berth = berth; }
    public void setDone()                            { this.done = true; }
    public void setContainersToLoad(int n)            { this.containersToLoad = n; }
    public void setContainersToUnload(int n)          { this.containersToUnload = n; }

    public void addContainers(int count)    { currentContainers += count; }
    public void removeContainers(int count) { currentContainers -= count; }

    @Override
    public void run() {
        LOGGER.info("Ship '{}' arrived at port: {}", name, this);
        while (!done) {
            state.handle(this);
        }
        LOGGER.info("Ship '{}' fully serviced — containers on board: {}/{}",
                name, currentContainers, capacity);
    }

    @Override
    public String toString() {
        return String.format("Ship{id=%d, name='%s', containers=%d/%d, toLoad=%d, toUnload=%d}",
                id, name, currentContainers, capacity, containersToLoad, containersToUnload);
    }
}
