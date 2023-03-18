package me.zombie_striker.mcs;


import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.translation.Translator;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.extensions.Extension;
import net.minestom.server.scoreboard.Sidebar;

import java.util.HashMap;
import java.util.UUID;

public class Main extends Extension{

    private HashMap<UUID, Sidebar> playerSidebars = new HashMap<>();

    @Override
    public void initialize() {
        //Update the sidebar whenever a player leaves.
        GlobalEventHandler globalEventHandler =  MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addListener(PlayerDisconnectEvent.class, event -> {
            Sidebar sidebar = playerSidebars.get(event.getPlayer());
            if(sidebar!=null) {
                sidebar.updateLineContent("onlineplayers", getOnlinePlayerComponent());
                sidebar.removeViewer(event.getPlayer());
            }
            for(Player player : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
                updateSidebars(player);
            }
        });
        //Create the sidebar when a player joins/spawns, and update all the sidebars with the new player count.
        globalEventHandler.addListener(PlayerSpawnEvent.class, event -> {
            Sidebar sidebar = new Sidebar("--xx[HypeJet]xx--");
            sidebar.createLine(new Sidebar.ScoreboardLine("onlineplayers", getOnlinePlayerComponent(),0));
            sidebar.createLine(new Sidebar.ScoreboardLine("hypepoints", getPlayerHypePointsComponent(event.getPlayer()),0));
            sidebar.addViewer(event.getPlayer());
            playerSidebars.put(event.getPlayer().getUuid(),sidebar);

            for(Player player : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
                updateSidebars(player);
            }
        });
    }

    /**
     * Updates the sidebar for the player
     * @param player The player
     */
    private void updateSidebars(Player player) {
        Sidebar sidebar = playerSidebars.get(player.getUuid());
        sidebar.updateLineContent("onlineplayers",getOnlinePlayerComponent());
    }

    /**
     * Returns a Component of the online player count.
     * @return the Component
     */
    private Component getOnlinePlayerComponent() {
        int playersOnline = MinecraftServer.getConnectionManager().getOnlinePlayers().size();
        return Component.translatable("onlineplayers",Component.text("Online Players: "+playersOnline , NamedTextColor.WHITE));
    }

    /**
     * Returns a Component of the player's hype points
     * @return the Component
     */
    private Component getPlayerHypePointsComponent(Player player) {
        int hypepoints = 100;
        //TODO: Fetch the hypepoints from somewhere
        return Component.translatable("hypepoints",Component.text("HypePoints: "+hypepoints , NamedTextColor.WHITE));
    }
    @Override
    public void terminate() {

    }
}