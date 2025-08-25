package ch.teko.transportapp.entity;

import java.util.ArrayList;

public class Place {
    ArrayList<Station> stations;

    public ArrayList<Station> getStations() {
        return stations;
    }

    public void setStations(ArrayList<Station> stations) {
        this.stations = stations;
    }
}
