package com.vodhanel.minecraft.va_postal.listeners;

import com.vodhanel.minecraft.va_postal.VA_postal;
import com.vodhanel.minecraft.va_postal.common.Util;
import com.vodhanel.minecraft.va_postal.common.VA_Dispatcher;
import com.vodhanel.minecraft.va_postal.config.C_Citizens;
import net.citizensnpcs.api.event.*;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class CitizensListener implements Listener {
    VA_postal plugin;

    public CitizensListener(VA_postal instance) {
        plugin = instance;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public static void onNPCDespawn(NPCDespawnEvent event) {
        if (event.isCancelled()) {
            return;
        }

        boolean npc_despawned = false;
        NPC npc = null;
        Location location = null;
        try {
            DespawnReason chunkunload = DespawnReason.CHUNK_UNLOAD;
            DespawnReason reason = event.getReason();
            if (reason == chunkunload) {
                if ((event.getNPC().getEntity() instanceof Player)) {
                    npc = event.getNPC();
                }
            } else {
                return;
            }
        } catch (Exception e) {
            return;
        }

        int npc_id_event;

        try {
            npc_id_event = npc.getId();
        } catch (Exception e) {
            return;
        }

        if (VA_postal.central_route_npc != null) {
            try {
                if (VA_postal.central_route_npc.getId() == npc_id_event) {
                    event.setCancelled(true);
                    return;
                }
            } catch (Exception e) {
            }
        }

        for (int i = 0; i < VA_postal.wtr_count; i++) {
            if (VA_postal.wtr_npc[i] != null) {
                try {
                    if (VA_postal.wtr_npc[i].getId() == npc_id_event) {
                        event.setCancelled(true);
                        return;
                    }
                } catch (Exception e) {
                }
            }
            return;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public static void onNPCRightClick(NPCRightClickEvent event) {
        if ((!VA_Dispatcher.dispatcher_running) || (event.isCancelled())) {
            return;
        }

        NPC npc = event.getNPC();


        boolean hit = false;
        for (int i = 0; i < VA_postal.wtr_count; i++) {
            if (npc == VA_postal.wtr_npc[i]) {
                hit = true;
                break;
            }
        }
        if (!hit) {
            return;
        }


        int elapsed_seconds_since_last_npc_chat = Util.time_stamp() - VA_postal.wtr_routechat_time_stamp;
        if (elapsed_seconds_since_last_npc_chat < 10) {
            return;
        }

        Player clicker = event.getClicker();
        if (npc == VA_postal.wtr_npc[VA_postal.wtr_id]) {
            chat_onroute(clicker);
        } else {
            chat_postoffice(clicker);
        }

        VA_postal.wtr_routechat_time_stamp = Util.time_stamp();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public static void onNPCLeftClick(NPCLeftClickEvent event) {
        if ((!VA_Dispatcher.dispatcher_running) || (event.isCancelled())) {
            return;
        }

        NPC npc = event.getNPC();


        boolean hit = false;
        for (int i = 0; i < VA_postal.wtr_count; i++) {
            if (npc == VA_postal.wtr_npc[i]) {
                hit = true;
                break;
            }
        }
        if (!hit) {
            return;
        }


        int elapsed_seconds_since_last_npc_chat = Util.time_stamp() - VA_postal.wtr_routechat_time_stamp;
        if (elapsed_seconds_since_last_npc_chat < 10) {
            return;
        }

        Player clicker = event.getClicker();
        if (npc == VA_postal.wtr_npc[VA_postal.wtr_id]) {
            chat_onroute(clicker);
        } else {
            chat_postoffice(clicker);
        }

        VA_postal.wtr_routechat_time_stamp = Util.time_stamp();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public static void onNPCCollision(NPCCollisionEvent event) {
        if (!VA_Dispatcher.dispatcher_running) {
            return;
        }

        NPC npc = event.getNPC();
        Entity entity = event.getCollidedWith();
        if (!(entity instanceof Player)) {
            return;
        }


        boolean hit = false;
        for (int i = 0; i < VA_postal.wtr_count; i++) {
            if (npc == VA_postal.wtr_npc[i]) {
                hit = true;
                break;
            }
        }
        if (!hit) {
            return;
        }


        int elapsed_seconds_since_last_npc_chat = Util.time_stamp() - VA_postal.wtr_collide_time_stamp;
        if (elapsed_seconds_since_last_npc_chat < 10) {
            return;
        }

        Player collider = (Player) event.getCollidedWith();
        chat_collision(collider);

        VA_postal.wtr_collide_time_stamp = Util.time_stamp();
    }

    public static void chat_onroute(Player player) {
        String splayer = proper(player.getName());
        String srespond = C_Citizens.chat_onroute();
        srespond = srespond.replace("%player%", splayer);
        Util.pinform(player, srespond);
    }

    public static void chat_postoffice(Player player) {
        String splayer = proper(player.getName());
        String srespond = C_Citizens.chat_postoffice();
        srespond = srespond.replace("%player%", splayer);
        Util.pinform(player, srespond);
    }

    public static void chat_collision(Player player) {
        String splayer = proper(player.getName());
        String srespond = C_Citizens.chat_collision();
        srespond = srespond.replace("%player%", splayer);
        Util.pinform(player, srespond);
    }

    public static String proper(String string) {
        String result = "null";
        if (string.length() > 0) {
            string = string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase().trim();
            result = string;
        }
        return result;
    }

    public static String chat_address_format(String string) {
        String[] parts = string.split("_");
        String name = proper(parts[0]);
        if (parts.length > 1) {
            name = name + " " + Util.proper(parts[1]);
        }
        if (parts.length > 2) {
            name = name + " " + Util.proper(parts[2]);
        }
        if (parts.length > 3) {
            name = name + " " + Util.proper(parts[3]);
        }
        return name;
    }
}
