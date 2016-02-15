package de.themoep.interserverports;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

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
public class TeleportRequestListener implements PluginMessageListener {
    private final InterServerPorts plugin;

    public TeleportRequestListener(InterServerPorts plugin) {
        this.plugin = plugin;
    }

    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subChannel = in.readUTF();
        if(!subChannel.equals(plugin.getName())) {
            return;
        }
        short len = in.readShort();
        byte[] msgbytes = new byte[len];
        in.readFully(msgbytes);

        DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
        try {
            String playerName = msgin.readUTF();
            String worldName = msgin.readUTF();
            double x = msgin.readDouble();
            double y = msgin.readDouble();
            double z = msgin.readDouble();
            float yaw = msgin.readFloat();
            float pitch = msgin.readFloat();
            String causeStr = msgin.readUTF();

            PlayerTeleportEvent.TeleportCause cause = PlayerTeleportEvent.TeleportCause.UNKNOWN;
            try {
                cause = PlayerTeleportEvent.TeleportCause.valueOf(causeStr.toUpperCase());
            } catch(IllegalArgumentException e) {
                plugin.getLogger().warning("Unknown teleport cause " + causeStr + "! Using " + cause);
            }

            World world = plugin.getServer().getWorld(worldName);
            if(world == null) {
                plugin.getLogger().warning("No world " + worldName + " found on this server! Please check the configuration on the server the player " + playerName + " teleported from!");
                return;
            }
            Location destination = new Location(world, x, y, z, yaw, pitch);
            Player toTeleport = plugin.getServer().getPlayer(playerName);
            if(toTeleport != null && toTeleport.isOnline()) {
                plugin.teleport(toTeleport, destination, cause);
            } else {
                plugin.cacheTeleportRequest(playerName, new TeleportRequest(destination, cause));
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
