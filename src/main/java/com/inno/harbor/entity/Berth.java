package com.inno.harbor.entity;

import java.util.concurrent.locks.ReentrantLock;

public class Berth {

    private final int id;
    private boolean available;
    private final ReentrantLock lock;

    public Berth(int id) {
        this.id = id;
        this.available = true;
        this.lock = new ReentrantLock();
    }

    public int getId() {
        return id;
    }

    public boolean isAvailable() {
        return available;
    }

    public void occupy() {
        available = false;
    }

    public void free() {
        available = true;
    }

    public ReentrantLock getLock() {
        return lock;
    }
}
