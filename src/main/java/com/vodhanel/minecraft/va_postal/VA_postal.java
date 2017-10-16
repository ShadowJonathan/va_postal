package com.vodhanel.minecraft.va_postal;

import com.palmergames.bukkit.towny.Towny;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.vodhanel.minecraft.va_postal.commands.Cmdexecutor;
import com.vodhanel.minecraft.va_postal.common.Init_Towny;
import com.vodhanel.minecraft.va_postal.common.P_Economy;
import com.vodhanel.minecraft.va_postal.common.Util;
import com.vodhanel.minecraft.va_postal.common.VA_Dispatcher;
import com.vodhanel.minecraft.va_postal.config.Config;
import com.vodhanel.minecraft.va_postal.config.GetConfig;
import com.vodhanel.minecraft.va_postal.listeners.BukkitListener;
import com.vodhanel.minecraft.va_postal.listeners.CitizensListener;
import com.vodhanel.minecraft.va_postal.listeners.RouteEditor;
import com.vodhanel.minecraft.va_postal.navigation.Goal_WTR;
import com.vodhanel.minecraft.va_postal.navigation.RouteMngr;
import com.vodhanel.minecraft.va_postal.navigation.Stuck_NPC;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.ai.GoalController;
import net.citizensnpcs.api.ai.GoalSelector;
import net.citizensnpcs.api.ai.Navigator;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.dynmap.DynmapCommonAPI;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerIcon;
import org.dynmap.markers.MarkerSet;
import org.dynmap.markers.PolyLineMarker;

public class VA_postal extends JavaPlugin {
    public static VA_postal plugin;
    public static Configuration configsettings = null;

    public static Player plistener_player = null;
    public static int plistener_last_used = -1;
    public static String plistener_local_po = "";
    public static String plistener_address = "";
    public static String plistener_last_slocation = "null";
    public static String plistener_last_2d_location = "null";
    public static boolean plistener_cooling = false;
    public static boolean plistener_newroute = false;
    public static ItemStack[] plistener_quickbar = null;

    public static ScoreboardManager plistener_sb_manager = null;
    public static boolean plistener_using_scoreboard = false;
    public static Scoreboard plistener_hud_board;
    public static org.bukkit.scoreboard.Objective plistener_hud_objective;
    public static Score plistener_hud_po;
    public static Score plistener_hud_addr;
    public static Score plistener_hud_addr_elev;
    public static Score plistener_hud_lwp;
    public static Score plistener_hud_tot;
    public static NPC[] wtr_npc;
    public static String[] wtr_qpair;
    public static Inventory[] wtr_inventory_postoffice;
    public static PlayerInventory[] wtr_inventory_npc;
    public static Inventory[] wtr_inventory_address;
    public static Player[] wtr_npc_player;
    public static long[] wtr_last_stuck_stamp;
    public static String[] wtr_last_stuck_action;
    public static String[] wtr_slocation_local_po;
    public static String[] wtr_slocation_local_po_spawn;
    public static String[] wtr_slocation_address;
    public static String[] wtr_slocation_address_spawn;
    public static boolean[] wtr_postal_route_start;
    public static boolean[] wtr_not_postal_fired;
    public static boolean[] wtr_waypoint_completed;
    public static int[] wtr_watchdog_stuck_ms;
    public static int[] wtr_watchdog_stuck_retry;
    public static long[] wtr_watchdog_stuck_stamp;
    public static int[] wtr_watchdog_ext_npc_stamp;
    public static int[] wtr_watchdog_stuck_pos;
    public static Location[] wtr_watchdog_ext_last_location;
    public static Location[] wtr_last_location;
    public static boolean[] wtr_done;
    public static Stuck_NPC[] wtr_Stuck_npc;
    public static Goal_WTR[] wtr_goal;
    public static Navigator[] wtr_nav;
    public static GoalSelector[] wtr_goalselector;
    public static GoalController[] wtr_controller;
    public static String[] wtr_swaypoint;
    public static String[] wtr_swaypoint_last;
    public static String[] wtr_swaypoint_next;
    public static Location[] wtr_waypoint;
    public static Location[] wtr_waypoint_last;
    public static Location[] wtr_waypoint_next;
    public static String[] wtr_waypoint_dynamic;
    public static String[] wtr_schest_location;
    public static String[] wtr_schest_location_postoffice;
    public static boolean[] wtr_chest_open;
    public static double[] wtr_dist_next;
    public static float[] wtr_range;
    public static int[] wtr_pos;
    public static int[] wtr_pos_next;
    public static int[] wtr_pos_last;
    public static int[] wtr_pos_final;
    public static boolean[] wtr_forward;
    public static float[] wtr_speed_factor;
    public static float[] wtr_door_speed;
    public static String[] wtr_poffice;
    public static String[] wtr_address;
    public static BlockState[] wtr_trap_door;
    public static boolean[] wtr_door;
    public static boolean[] wtr_door_nav;
    public static boolean[] wtr_door_nav_enter;
    public static String[] wtr_sdoor_location;
    public static boolean[] wtr_arriving;
    public static boolean[] wtr_arrived;
    public static boolean[] wtr_cooling;
    public static boolean[] wtr_ext_watchdog_reset;
    public static boolean[] wtr_goal_active;
    public static Stuck_NPC wtr_stuck_npc = null;
    public static int wtr_postman_cool = -1;
    public static int wtr_watchdog_sys_ext_stamp = -1;
    public static int wtr_id = -1;
    public static int wtr_count = 0;
    public static boolean needs_configuration = false;
    public static boolean admin_overide = false;
    public static int admin_overide_stamp = 0;
    public static boolean admin_bypass = false;
    public static int admin_bypass_stamp = 0;
    public static int wtr_routechat_time_stamp = -1;
    public static int wtr_collide_time_stamp = -1;
    public static boolean wtr_concurrent = false;
    public static boolean lookclose_on_route = false;
    public static float wtr_speed = 1.0F;
    public static String[] central_array_name;
    public static String[] central_array_location;
    public static long[] central_array_time;
    public static boolean[] central_array_promoted;
    public static int central_route_count = -1;
    public static int central_route_pos = 0;
    public static int central_route_time = -1;
    public static int central_cooldown = -1;
    public static NPCRegistry central_route_npc_reg = null;
    public static NPC central_route_npc = null;
    public static Player central_route_player = null;
    public static String central_po_slocation = null;
    public static String central_po_slocation_spawn = null;
    public static String central_schest_location = null;
    public static Inventory central_po_inventory = null;
    public static boolean central_po_log_book_check = false;
    public static int check_new_mail_secs = 0;
    public static Permission perms = null;
    public static boolean private_mailboxes = false;
    public static boolean strict_door_nav = true;
    public static boolean debug = false;
    public static boolean lossy_pathfinding = false;
    public static boolean quiet = false;
    public static boolean routetalk = false;
    public static boolean permtalk = false;
    public static boolean centraltalk = false;
    public static boolean cstalk = false;
    public static int mailtalk = 1;
    public static boolean queuetalk = false;
    public static boolean wdtalk = false;
    public static boolean postal_route_start = false;
    public static boolean postal_route_stopping = false;
    public static boolean postal_route_stopped = false;
    public static long route_watchdog_ping_time = System.currentTimeMillis();
    public static int route_watchdog_pos = 0;
    public static int chunks_loaded = 0;
    public static int chunks_requested = 0;
    public static NPCRegistry npcRegistry;
    public static int search_distance = 5;
    public static int allowed_geo_proximity = 15;
    public static int allowed_reditor_afk = 180;
    public static boolean economy_configured = false;
    public static Economy econ = null;
    public static Plugin dynmap;
    public static boolean dynmap_configured = false;
    public static boolean dynmap_active = false;
    public static DynmapCommonAPI apiDynmap;
    public static org.dynmap.markers.MarkerAPI markerapi;
    public static MarkerSet markerset;
    public static org.dynmap.markers.CircleMarker[] dyn_postman;
    public static String[] dyn_postman_po;
    public static PolyLineMarker[] dyn_route;
    public static Marker dyn_postmaster;
    public static MarkerIcon dyn_postman_ico;
    public static MarkerIcon dyn_postmaster_ico;
    public static MarkerIcon dyn_central_ico;
    public static MarkerIcon dyn_postoffice_ico;
    public static MarkerIcon dyn_address_ico;
    public static Marker[] dyn_po_mrkr;
    public static String[] dyn_po_str;
    public static Marker[] dyn_addr_mrkr;
    public static String[] dyn_addr_str_po;
    public static String[] dyn_addr_str_addr;
    public static boolean towny_configured = false;
    public static boolean towny_opt_in = false;
    public static Towny towny;
    public static boolean wg_configured = false;
    public static WorldGuardPlugin worldguard;
    private static boolean Postal_Started = false;

    public VA_postal() {
    }

    public static boolean using_towny() {
        return (towny_configured) && (towny_opt_in);
    }

    public static synchronized void SHUTDOWN() {
        VA_Dispatcher.dispatcher_running = false;
        if (Postal_Started) {
            Postal_Started = false;
            if (!needs_configuration) {
                Util.dinform("NPC DELETE ALL...");
                RouteMngr.npc_delete_all(true);
                Util.dinform("ALL NPC DELETED");
                if ((dynmap_active) &&
                        (dynmap.isEnabled()) &&
                        (markerset != null)) {
                    markerset.deleteMarkerSet();
                    markerset = null;
                }
            }
        }
    }

    public static synchronized String proper(String string) {
        try {
            if (string.length() > 0) {
                return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase().trim();
            }
        } catch (Exception e) {
        }

        return "";
    }

    public static synchronized String fixed_len(String input, int len, String filler) {
        try {
            input = input.trim();

            if (input.length() >= len) {
                return input.substring(0, len);
            }

            StringBuilder inputBuilder = new StringBuilder(input);
            while (inputBuilder.length() < len) {
                inputBuilder.append(filler);
            }
            input = inputBuilder.toString();
            return input;
        } catch (Exception e) {
            StringBuilder blank = new StringBuilder();
            for (int i = 0; i < len; i++) {
                blank.append(filler);
            }
            return blank.toString();
        }
    }

    public void onEnable() {
        Postal_Started = false;
        try {
            Class.forName("net.citizensnpcs.api.CitizensAPI");
        } catch (ClassNotFoundException e) {
            Util.cinform("[Postal] Citizens2 was not found, aborting....");
            return;
        }

        plugin = this;
        admin_overide = false;
        admin_bypass = false;
        Config.LoadConfiguration();
        configsettings = getConfig();
        GetConfig.update_config_settings();
        Cmdexecutor.init_confirm_queue();
        mailtalk = 1;
        debug = GetConfig.debug();
        lossy_pathfinding = GetConfig.lossy_pathfinding();
        check_new_mail_secs = GetConfig.new_mail_secs();
        private_mailboxes = GetConfig.private_mailboxes();
        strict_door_nav = GetConfig.strict_door_nav();
        wtr_concurrent = GetConfig.is_wtr_concurrent();
        lookclose_on_route = GetConfig.lookclose_on_route();
        search_distance = GetConfig.search_distance();
        allowed_geo_proximity = GetConfig.allowed_geo_proximity();
        allowed_reditor_afk = GetConfig.allowed_reditor_afk();
        towny_opt_in = GetConfig.towny_opt_in();
        npcRegistry = CitizensAPI.getNPCRegistry();
        RouteMngr.npc_delete_all(true);
        wtr_stuck_npc = new Stuck_NPC();
        setupScoreboard();
        setupPermissions();
        setupEconomy();
        setupDynmap();
        setupTowny();
        setupWorldGuard();
        if (GetConfig.auto_start()) {
            VA_Dispatcher.restart(false);
        }

        plugin.getServer().getPluginManager().registerEvents(new BukkitListener(plugin), plugin);
        plugin.getServer().getPluginManager().registerEvents(new CitizensListener(plugin), plugin);
        plugin.getServer().getPluginManager().registerEvents(new RouteEditor(plugin), plugin);

        Cmdexecutor executor = new Cmdexecutor(this);
        getCommand("postal").setExecutor(executor);
        getCommand("addr").setExecutor(executor);
        getCommand("distr").setExecutor(executor);
        getCommand("att").setExecutor(executor);
        getCommand("go").setExecutor(executor);
        getCommand("tlist").setExecutor(executor);
        getCommand("alist").setExecutor(executor);
        getCommand("plist").setExecutor(executor);
        getCommand("gps").setExecutor(executor);
        getCommand("gpsp").setExecutor(executor);
        getCommand("expedite").setExecutor(executor);
        getCommand("setcentral").setExecutor(executor);
        getCommand("setlocal").setExecutor(executor);
        getCommand("setaddr").setExecutor(executor);
        getCommand("setroute").setExecutor(executor);
        getCommand("setowner").setExecutor(executor);
        getCommand("gotocentral").setExecutor(executor);
        getCommand("gotolocal").setExecutor(executor);
        getCommand("gotoaddr").setExecutor(executor);
        getCommand("owneraddr").setExecutor(executor);
        getCommand("ownerlocal").setExecutor(executor);
        getCommand("showroute").setExecutor(executor);
        getCommand("deletelocal").setExecutor(executor);
        getCommand("deleteaddr").setExecutor(executor);
        getCommand("openlocal").setExecutor(executor);
        getCommand("openaddr").setExecutor(executor);
        getCommand("closelocal").setExecutor(executor);
        getCommand("closeaddr").setExecutor(executor);
        getCommand("package").setExecutor(executor);
        getCommand("cod").setExecutor(executor);
        getCommand("accept").setExecutor(executor);
        getCommand("refuse").setExecutor(executor);

        Postal_Started = true;
    }

    public void onDisable() {
        SHUTDOWN();
    }

    private synchronized boolean setupPermissions() {
        try {
            Class.forName("net.milkbowl.vault.permission.Permission");
        } catch (ClassNotFoundException e) {
            perms = null;
            Util.cinform("[Postal] Using Bukkit for permissions.");
            return false;
        }
        RegisteredServiceProvider<Permission> rsp = null;
        rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = (Permission) rsp.getProvider();
        Util.cinform("[Postal] Using Vault for permissions hook.");
        return true;
    }

    private boolean setupEconomy() {
        economy_configured = GetConfig.economy_use();
        if (economy_configured) {
            economy_configured = false;
            try {
                Class.forName("net.milkbowl.vault.economy.Economy");
            } catch (ClassNotFoundException e) {
                perms = null;
                Util.cinform("[Postal] Could not find Vault class for economy.");
                return false;
            }
            if (getServer().getPluginManager().getPlugin("Vault") == null) {
                Util.cinform("[Postal] Could not find Vault plugin for economy.");
                return false;
            }
            RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp == null) {
                Util.cinform("[Postal] Could not register Vault for economy.");
                return false;
            }
            econ = (Economy) rsp.getProvider();
            if (econ == null) {
                Util.cinform("[Postal] No economy service providor via Vault.");
                return false;
            }
            if (econ.hasBankSupport()) {
                String e_name = Util.df(econ.getName());
                Util.cinform("[Postal] Bank equiped economy verified via Vault.");
                Util.cinform("[Postal] Using " + e_name + " for economy.");
                economy_configured = true;
                P_Economy.init_economy();
                return true;
            }
            Util.cinform("\033[0;33m[Postal] Vault enabled economy detected, but Postal");
            Util.cinform("\033[0;33m[Postal] requires bank support. Disabling economy.");
            return false;
        }


        Util.cinform("[Postal] Economy disabled in config.yml");
        return false;
    }

    private synchronized void setupScoreboard() {
        plistener_using_scoreboard = GetConfig.use_scoreboard();
        if (plistener_using_scoreboard) {
            try {
                Class.forName("org.bukkit.scoreboard.ScoreboardManager");
            } catch (ClassNotFoundException e) {
                plistener_using_scoreboard = false;
                Util.cinform("[Postal] Not using ScoreBoard");
            }
            if (plistener_using_scoreboard) {
                plistener_sb_manager = org.bukkit.Bukkit.getScoreboardManager();
                Util.cinform("[Postal] Using ScoreBoard for HUD display");
            }
        } else {
            Util.cinform("[Postal] Not using ScoreBoard");
        }
    }

    private synchronized void setupDynmap() {
        dynmap_configured = false;
        dynmap_configured = GetConfig.dynmap();
        if (dynmap_configured) {
            PluginManager pm = getServer().getPluginManager();

            dynmap = pm.getPlugin("dynmap");
            if (dynmap == null) {
                Util.cinform("[Postal] Cannot find dynmap, api not hooked.");
                dynmap_configured = false;
                return;
            }
            Util.cinform("[Postal] Using Dynmap.");

            apiDynmap = (DynmapCommonAPI) dynmap;
        } else {
            Util.cinform("[Postal] Dynmap disabled in 'config.yml'.");
        }
    }

    private synchronized void setupTowny() {
        towny_configured = false;
        if (towny_opt_in) {
            check_towny();
            if ((towny == null) || (getServer().getScheduler().scheduleSyncDelayedTask(this, new Init_Towny(this), 1L) == -1)) {
                Util.cinform("[Postal] Cannot find Towny, api not hooked.");
                towny_configured = false;
            } else {
                Util.cinform("[Postal] Using Towny.");
                towny_configured = true;
            }
        } else {
            Util.cinform("[Postal] Towny disabled in 'config.yml'.");
        }
    }

    public synchronized void check_towny() {
        PluginManager pm = getServer().getPluginManager();
        Plugin p = pm.getPlugin("Towny");
        if ((p != null) || ((p instanceof Towny))) {
            towny = (Towny) p;
        }
    }

    public Towny getTowny() {
        return towny;
    }

    private synchronized void setupWorldGuard() {
        wg_configured = false;
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
        if ((plugin == null) || (!(plugin instanceof WorldGuardPlugin))) {
            Util.cinform("[Postal] Cannot find WorldGuard, api not hooked.");
            wg_configured = false;
        } else {
            Util.cinform("[Postal] Using WorldGuard.");
            worldguard = (WorldGuardPlugin) plugin;
            wg_configured = true;
        }
    }
}
