package com.hb712.gleak_android.message.net;

public class Location {

    private Double lon;
    private Double lat;

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public static class Builder {

        private Double lon;
        private Double lat;

        public Builder() {
        }

        public Builder lon(Double lon) {
            this.lon = lon;
            return this;
        }

        public Builder lat(Double lat) {
            this.lat = lat;
            return this;
        }

        public Location build() {
            return new Location(this);
        }
    }

    private Location(Builder builder) {
        lon = builder.lon;
        lat = builder.lat;
    }

    public Location() {
        super();
    }

}
