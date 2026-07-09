package com.inno.harbor.state;

public enum StateType {

    WAITING {
        @Override
        public ShipState createState() { return new WaitingState(); }
    },
    DOCKED {
        @Override
        public ShipState createState() { return new DockedState(); }
    },
    UNLOADING {
        @Override
        public ShipState createState() { return new UnloadingState(); }
    },
    LOADING {
        @Override
        public ShipState createState() { return new LoadingState(); }
    },
    DEPARTING {
        @Override
        public ShipState createState() { return new DepartingState(); }
    };

    public abstract ShipState createState();
}