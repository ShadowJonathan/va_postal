package com.vodhanel.minecraft.va_postal.common;

import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.vodhanel.minecraft.va_postal.VA_postal;

public class Init_Towny implements Runnable {
    VA_postal plugin;
    Towny towny;

    public Init_Towny(VA_postal plugin) {
        this.plugin = plugin;
        towny = plugin.getTowny();
    }

    public void run() {
        int hits = 0;
        plugin.getLogger().info("================================================");
        for (Resident resident : TownyUniverse.getDataSource().getResidents()) {

            hits++;
        }
        plugin.getLogger().info("Postal registered " + hits + " Towny residents");
        plugin.getLogger().info("================================================");
    }
}
