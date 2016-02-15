package de.themoep.interserverports;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

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
public class PlayerListener implements Listener {

    private final InterServerPorts plugin;

    public PlayerListener(InterServerPorts plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerTeleportEvent event) {
        // Check if teleport is between worlds
        if(event.getFrom().getWorld() == event.getTo().getWorld()) {
            return;
        }
        // Get the name of the server the world is mapped to
        WorldMapping mapping = plugin.getWorldServer(event.getTo().getWorld().getName());
        // Return if world is not mapped to any server
        if(mapping == null) {
            return;
        }
        event.setCancelled(true);
        plugin.getLogger().info("Initiating teleport of " + event.getPlayer() + " from " + mapping.getMappedWorld() + " to " + mapping.getServer() + "/" + mapping.getWorld());

        // Forge and send plugin message to connect player to other server
        ByteArrayDataOutput cOut = ByteStreams.newDataOutput();
        cOut.writeUTF("Connect");
        cOut.writeUTF(mapping.getServer());
        event.getPlayer().sendPluginMessage(plugin, "BungeeCord", cOut.toByteArray());

        // Forge and send plugin message to InterServerPorts on the other server
        ByteArrayDataOutput tOut = ByteStreams.newDataOutput();
        tOut.writeUTF("Forward");
        tOut.writeUTF(mapping.getServer());
        tOut.writeUTF(plugin.getName());
        //ByteArrayOutputStream tStream = new ByteArrayOutputStream();
        //DataOutputStream tMsg = new DataOutputStream(tStream);
        ByteArrayDataOutput tMsg = ByteStreams.newDataOutput();
        tMsg.writeUTF(event.getPlayer().getName());
        tMsg.writeUTF(mapping.getWorld());
        tMsg.writeDouble(event.getTo().getX());
        tMsg.writeDouble(event.getTo().getY());
        tMsg.writeDouble(event.getTo().getZ());
        tMsg.writeFloat(event.getTo().getYaw());
        tMsg.writeFloat(event.getTo().getPitch());
        tMsg.writeUTF(event.getCause().toString());

        tOut.writeShort(tMsg.toByteArray().length);
        tOut.write(tMsg.toByteArray());
        event.getPlayer().sendPluginMessage(plugin, "BungeeCord", tOut.toByteArray());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        TeleportRequest request = plugin.getCachedRequest(event.getPlayer().getName());
        if(request == null) {
            return;
        }
        plugin.getLogger().info(event.getPlayer().getName() + " has a waiting teleport request!");
        Location destination = request.getLocation();
        if(destination == null) {
            plugin.getLogger().warning("No world " + request.getWorldName() + " found on this server!");
            return;
        }

        plugin.teleport(event.getPlayer(), destination, request.getTeleportCause());
        plugin.clearRequestCache(event.getPlayer());
    }
}
