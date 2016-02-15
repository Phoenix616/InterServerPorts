package de.themoep.interserverports;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

/**
 * InterServerPorts
 * Copyright (C) 2015 Max Lee (https://github.com/Phoenix616/)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

public class InterServerPorts extends JavaPlugin {

    private Map<String, WorldMapping> worldServerMap;
    private Map<String, TeleportRequest> requestCache = new HashMap<String, TeleportRequest>();

    public void onEnable() {
        saveDefaultConfig();
        loadConfig();
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getMessenger().registerIncomingPluginChannel(this, getName(), new TeleportRequestListener(this));
    }

    private boolean loadConfig() {
        reloadConfig();
        worldServerMap = new HashMap<String, WorldMapping>();
        ConfigurationSection worlds = getConfig().getConfigurationSection("worlds");
        if(worlds == null) {
            getLogger().warning("No worlds section in config found!");
            return false;
        }
        for(String world : worlds.getKeys(false)) {
            String serverName = worlds.getString(world + ".server");
            String worldName = worlds.getString(world + ".world");
            if(serverName == null) {
                getLogger().warning(world + " is missing a server name!");
                continue;
            } else if(worldName == null) {
                getLogger().warning(world + " is missing a world name!");
                continue;
            }
            worldServerMap.put(world.toLowerCase(), new WorldMapping(world, serverName, worldName));
        }
        return worldServerMap.size() > 0;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("InterServerPorts")) {
            if(args.length > 0) {
                if("reload".equalsIgnoreCase(args[0])) {
                    if(loadConfig()) {
                        sender.sendMessage(ChatColor.GREEN + getName() + " config reloaded!");
                    } else {
                        sender.sendMessage(ChatColor.RED + "Failed to reload " + getName() + " config! (Look at the console for exact error)");
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public WorldMapping getWorldServer(String world) {
        return worldServerMap.get(world.toLowerCase());
    }

    public void cacheTeleportRequest(String playerName, TeleportRequest request) {
        requestCache.put(playerName, request);
    }

    public void clearRequestCache(Player player) {
        requestCache.remove(player.getName());
    }

    public TeleportRequest getCachedRequest(String playerName) {
        return requestCache.get(playerName);
    }

    public void teleport(Player player, Location loc, PlayerTeleportEvent.TeleportCause cause) {
        player.teleport(loc, cause);
        getLogger().info("Teleported " + player.getName() + " to " + loc.getWorld().getName() + ", " + loc.getX() + "/" + loc.getY() + "/" + loc.getZ());
    }
}
