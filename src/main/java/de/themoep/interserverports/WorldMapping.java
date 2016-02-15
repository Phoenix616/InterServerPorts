package de.themoep.interserverports;

/**
 * InterServerPorts
 * Copyright (C) 2016 Max Lee (https://github.com/Phoenix616/)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Mozilla Public License as published by
 * the Mozilla Foundation, version 2.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * Mozilla Public License v2.0 for more details.
 * <p/>
 * You should have received a copy of the Mozilla Public License v2.0
 * along with this program. If not, see <http://mozilla.org/MPL/2.0/>.
 */
public class WorldMapping {
    private final String mappedWorld;
    private final String server;
    private final String world;

    public WorldMapping(String mappedWorld, String server, String world) {
        this.mappedWorld = mappedWorld;
        this.server = server;
        this.world = world;
    }

    public String getMappedWorld() {
        return mappedWorld;
    }

    public String getServer() {
        return server;
    }

    public String getWorld() {
        return world;
    }
}
