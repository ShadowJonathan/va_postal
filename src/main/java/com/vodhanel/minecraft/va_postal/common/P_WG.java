package com.vodhanel.minecraft.va_postal.common;

import com.vodhanel.minecraft.va_postal.VA_postal;
import com.vodhanel.minecraft.va_postal.commands.Cmdexecutor;
import org.bukkit.entity.Player;

public class P_WG {
    VA_postal plugin;

    public P_WG(VA_postal instance) {
        plugin = instance;
    }

    public static boolean ok_to_build_wg(Player player) {
        if (VA_postal.wg_configured) {
            if (Cmdexecutor.hasPermission(player, "postal.accept.bypass")) {
                return true;
            }
            org.bukkit.Location location = player.getLocation();

            if (VA_postal.worldguard.canBuild(player, location)) {
                return true;
            }
            return false;
        }

        return true;
    }
}
