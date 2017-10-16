package com.vodhanel.minecraft.va_postal.common;

import com.vodhanel.minecraft.va_postal.VA_postal;
import com.vodhanel.minecraft.va_postal.config.C_Citizens;
import com.vodhanel.minecraft.va_postal.config.GetConfig;
import com.vodhanel.minecraft.va_postal.mail.BookManip;
import com.vodhanel.minecraft.va_postal.mail.ID_Mail;
import com.vodhanel.minecraft.va_postal.navigation.RouteMngr;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class VA_Ping {
    public VA_Ping() {
    }

    public static synchronized void ping_route_watchdog() {
        if (!VA_Dispatcher.dispatcher_running) {
            return;
        }
        long elapsed_ms_since_last_route_ping = System.currentTimeMillis() - VA_postal.route_watchdog_ping_time;

        int npcs = VA_postal.wtr_npc.length;
        long interval = 30000 / npcs;
        if (elapsed_ms_since_last_route_ping < interval) {
            return;
        }

        if (VA_Dispatcher.dispatcher_async) {
            VA_postal.plugin.getServer().getScheduler().scheduleSyncDelayedTask(VA_postal.plugin, () -> {
            }, 20L);


        } else {

            ping_route_watchdog_worker();
        }
    }

    public static synchronized void ping_route_watchdog_worker() {
        if (!VA_Dispatcher.dispatcher_running) {
            return;
        }

        if (VA_postal.route_watchdog_pos < VA_postal.central_route_count - 1) {
            VA_postal.route_watchdog_pos += 1;
        } else {
            VA_postal.route_watchdog_pos = 0;
        }

        int id = VA_postal.route_watchdog_pos;
        if (!VA_postal.wtr_goal_active[id]) {
            return;
        }

        if (VA_postal.wtr_poffice[id] == null) {
            return;
        }
        String spostoffice = VA_postal.wtr_poffice[id];

        if (VA_postal.wtr_goal[id] == null) {
            return;
        }

        if ((VA_postal.wtr_npc[id] == null) || (!VA_postal.wtr_npc[id].isSpawned())) {
            if (VA_postal.wdtalk) {
                Util.cinform(AnsiColor.YELLOW + "[WATCHDOG External NPC] " + VA_postal.wtr_poffice[id] + ", " + VA_postal.wtr_address[id] + ", " + VA_postal.wtr_pos[id]);
            }
            try {
                VA_postal.wtr_npc[id].spawn(VA_postal.wtr_waypoint[id]);
            } catch (Exception e) {
                Util.cinform(AnsiColor.RED + "[WATCHDOG External NPC monitor] Problem Re-Spawning - Restart");
                VA_Dispatcher.restart(true);
                return;
            }
        }

        int age_seconds = Util.time_stamp() - VA_postal.wtr_watchdog_ext_npc_stamp[id];
        if ((age_seconds > 30) && (!VA_postal.wtr_cooling[id])) {
            VA_postal.wtr_ext_watchdog_reset[id] = true;
            try {
                VA_postal.wtr_goal[id].reset();
            } catch (Exception e) {
            }
        }

        double distanced_moved = 0.0D;
        try {
            distanced_moved = VA_postal.wtr_npc_player[id].getLocation().distanceSquared(VA_postal.wtr_watchdog_ext_last_location[id]);
        } catch (Exception e) {
            distanced_moved = 0.0D;
        }
        if (distanced_moved > 0.0D) {
            try {
                VA_postal.wtr_watchdog_ext_npc_stamp[id] = Util.time_stamp();
                VA_postal.wtr_watchdog_ext_last_location[id] = VA_postal.wtr_npc_player[id].getLocation();
            } catch (Exception e) {
            }
        }
        VA_postal.route_watchdog_ping_time = System.currentTimeMillis();
    }

    public static synchronized void ping_central_postmaster() {
        if ((!VA_Dispatcher.dispatcher_running) || (VA_postal.central_array_time == null)) {
            return;
        }

        int oldest_index = 0;

        for (int i = 0; i < VA_postal.central_route_count; i++) {
            if (VA_postal.central_array_name[i] != null) {
                oldest_index = i;
                break;
            }
        }
        int last_index = 0;

        for (int i = 0; i < VA_postal.central_route_count; i++) {
            if (VA_postal.central_array_name[i] != null) {
                last_index = i;
            }
        }

        if (!VA_Dispatcher.postmen_initialized) {
            for (int i = 0; i <= last_index; i++) {
                if ((VA_postal.central_array_name[i] != null) &&
                        (VA_postal.central_array_time[i] < 0L)) {
                    oldest_index = i;
                    break;
                }
            }

            if (oldest_index == last_index) {
                VA_Dispatcher.postmen_initialized = true;

                VA_postal.wtr_postman_cool = GetConfig.postman_cool_sec();
                VA_postal.central_cooldown = GetConfig.central_cool_sec();
            }
        } else {
            for (int i = 1; i <= last_index; i++) {
                if ((VA_postal.central_array_name[i] != null) &&
                        (VA_postal.central_array_time[i] < VA_postal.central_array_time[oldest_index])) {
                    oldest_index = i;
                }
            }


            if (!VA_postal.central_array_promoted[oldest_index]) {
                int elapsed_seconds_since_last_central_aciviy = Util.time_stamp() - VA_postal.central_route_time;

                if (elapsed_seconds_since_last_central_aciviy < VA_postal.central_cooldown) {
                    return;
                }
            }
        }


        VA_postal.central_array_time[oldest_index] = Util.time_stamp();

        VA_postal.central_array_promoted[oldest_index] = false;

        check_for_elapsed_timed_routines();


        int index = oldest_index;
        if (VA_Dispatcher.dispatcher_async) {
            VA_postal.plugin.getServer().getScheduler().scheduleSyncDelayedTask(VA_postal.plugin, new Runnable() {

                public void run() {
                    VA_Ping.ping_central_postmaster_worker(index);
                }
            }, 20L);

        } else {
            ping_central_postmaster_worker(index);
        }
    }

    public static synchronized void ping_central_postmaster_worker(int index) {
        if (!VA_Dispatcher.dispatcher_running) {
            return;
        }


        int id = find_local_id(index);
        if (id < 0) {
            return;
        }


        VA_postal.central_route_pos = index;

        String stown = VA_postal.wtr_poffice[id];

        com.vodhanel.minecraft.va_postal.config.C_Dispatcher.central_time_stamp(stown);


        VA_postal.central_po_slocation = com.vodhanel.minecraft.va_postal.config.C_Postoffice.get_central_po_location();
        if (VA_postal.central_route_npc == null) {
            RouteMngr.npc_create(VA_postal.central_po_slocation, false, "PostMaster");
        }


        P_Economy.verify_bank(stown);


        com.vodhanel.minecraft.va_postal.mail.ChestManip.set_central_chest_inv();
        if (VA_postal.central_po_inventory == null) {
            Util.cinform(AnsiColor.RED + "[Central PO] unable to set central chest inventory.");
            Util.cinform(AnsiColor.RED + "[Central PO] Stopping VA_Postal....");
            VA_Dispatcher.cancel_dispatcher(false);
            return;
        }


        if (VA_postal.wtr_count - 1 == id) {
            RouteMngr.initialize_npc(1000);
            VA_postal.central_po_log_book_check = false;
            P_Economy.ping_economy_schedule();
        }


        if (!VA_postal.central_po_log_book_check) {
            if (!BookManip.central_chest_contains_postal_log()) {
                BookManip.central_create_and_install_postal_log();
            }
            VA_postal.central_po_log_book_check = true;
        }


        if (VA_postal.central_route_npc == null) {
            Util.cinform(AnsiColor.RED + "[WATCHDOG] Detected invalid NPC reference (Central)");
            Util.cinform("\033[0;33m[WATCHDOG] " + Util.df(stown));
            VA_Dispatcher.restart(true);
            return;
        }


        ID_Mail.set_postoffice_chest_inv(id);
        if (VA_postal.wtr_inventory_postoffice[id] == null) {
            Util.cinform("\033[0;33m[Central PO] unable to set local chest inventory.");
            Util.cinform("\033[0;33m[Central PO] for post office: " + Util.df(stown));
            return;
        }


        if ((VA_postal.towny_configured) && (VA_postal.towny_opt_in)) {
            P_Towny.check_po_ownership(id, stown);
        }


        VA_postal.central_route_player = (Player) VA_postal.central_route_npc.getEntity();
        try {
            VA_postal.central_route_player.closeInventory();
        } catch (Exception e) {
        }


        tp_to_local_chest(id, stown);

        if (VA_postal.dynmap_configured) {
            P_Dynmap.update_central_pos(stown);
        }


        VA_postal.central_route_player.openInventory(VA_postal.wtr_inventory_postoffice[id]);
        ID_Mail.postmaster_service_postoffice(id, stown);


        VA_postal.central_route_time = Util.time_stamp();
    }

    public static synchronized int find_local_id(int index) {
        String found_name = "";
        if (VA_postal.central_array_name[index] != null) {
            found_name = VA_postal.central_array_name[index].toLowerCase().trim();
        } else {
            return -1;
        }

        int id = -1;
        for (int i = 0; i < VA_postal.central_route_count; i++) {
            if (VA_postal.wtr_poffice[i] != null) {
                String test_name = VA_postal.wtr_poffice[i].toLowerCase().trim();
                if (found_name.equals(test_name)) {
                    id = i;
                    break;
                }
            }
        }

        if ((id < 0) || (VA_postal.wtr_poffice[id] == null)) {
            return -1;
        }
        return id;
    }

    public static synchronized void check_for_elapsed_timed_routines() {
        int elapsed_seconds_since_last_admin_aciviy = Util.time_stamp() - VA_postal.admin_overide_stamp;
        if (elapsed_seconds_since_last_admin_aciviy > 60) {
            VA_postal.admin_overide = false;
        }


        int elapsed_seconds_since_last_bypass_aciviy = Util.time_stamp() - VA_postal.admin_bypass_stamp;
        if (elapsed_seconds_since_last_bypass_aciviy > 300) {
            VA_postal.admin_bypass = false;
        }


        VA_Timers.report_newmail_all();


        check_for_afk_in_route_editor();
    }

    private static synchronized void check_for_afk_in_route_editor() {
        if (VA_postal.plistener_player != null) {
            int interval = Util.time_stamp() - VA_postal.plistener_last_used;
            String splayer = VA_postal.plistener_player.getName();
            if (interval > VA_postal.allowed_reditor_afk) {
                Util.cinform("[Postal] Ending route editor session for " + splayer + ", AFK while editing.");
                com.vodhanel.minecraft.va_postal.listeners.RouteEditor.Exit_routeEditor(VA_postal.plistener_player);
            }
        }
    }

    public static synchronized void tp_to_local_chest(int id, String stown) {
        boolean tp_success = false;
        if (C_Citizens.alt_central_nav()) {
            Location target = Util.str2location(VA_postal.wtr_slocation_local_po_spawn[id]);
            VA_postal.central_route_npc.spawn(target);
            if (VA_postal.central_route_npc != null) {
                if (!VA_postal.central_route_npc.isSpawned()) {
                    VA_postal.central_route_npc.spawn(target);
                } else {
                    VA_postal.central_route_npc.getEntity().teleport(target);
                }
                tp_success = true;
            }
        } else if (VA_postal.central_route_npc != null) {
            VA_postal.central_route_npc.despawn();
            Location target = Util.str2location(VA_postal.wtr_slocation_local_po_spawn[id]);
            VA_postal.central_route_npc.spawn(target);
            VA_postal.central_route_player = (Player) VA_postal.central_route_npc.getEntity();
            tp_success = true;
        }

        if ((tp_success) && (VA_postal.centraltalk)) {
            Util.cinform("\033[1;35mCentral: \033[0;36m" + Util.df(stown));
        }
    }
}
