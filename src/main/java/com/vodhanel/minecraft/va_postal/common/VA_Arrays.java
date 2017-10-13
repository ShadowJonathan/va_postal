package com.vodhanel.minecraft.va_postal.common;

import com.vodhanel.minecraft.va_postal.VA_postal;
import com.vodhanel.minecraft.va_postal.navigation.Goal_WTR;
import com.vodhanel.minecraft.va_postal.navigation.ID_WTR;
import com.vodhanel.minecraft.va_postal.navigation.Stuck_NPC;
import net.citizensnpcs.api.ai.GoalController;
import net.citizensnpcs.api.ai.GoalSelector;
import net.citizensnpcs.api.ai.Navigator;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;
import org.dynmap.markers.CircleMarker;
import org.dynmap.markers.PolyLineMarker;

public class VA_Arrays {
    public VA_Arrays() {
    }

    public static synchronized void init_wtr_arrays(int al) {
        VA_postal.central_array_name = null;
        VA_postal.central_array_location = null;
        VA_postal.central_array_time = null;
        VA_postal.central_array_promoted = null;
        VA_postal.wtr_npc = null;
        VA_postal.wtr_inventory_postoffice = null;
        VA_postal.wtr_inventory_npc = null;
        VA_postal.wtr_inventory_address = null;
        VA_postal.wtr_npc_player = null;
        VA_postal.wtr_last_stuck_action = null;
        VA_postal.wtr_last_stuck_stamp = null;
        VA_postal.wtr_slocation_local_po = null;
        VA_postal.wtr_slocation_local_po_spawn = null;
        VA_postal.wtr_slocation_address = null;
        VA_postal.wtr_slocation_address_spawn = null;
        VA_postal.wtr_postal_route_start = null;
        VA_postal.wtr_not_postal_fired = null;
        VA_postal.wtr_waypoint_completed = null;
        VA_postal.wtr_watchdog_stuck_ms = null;
        VA_postal.wtr_watchdog_stuck_retry = null;
        VA_postal.wtr_watchdog_stuck_stamp = null;
        VA_postal.wtr_watchdog_ext_npc_stamp = null;
        VA_postal.wtr_watchdog_stuck_pos = null;
        VA_postal.wtr_watchdog_ext_last_location = null;
        VA_postal.wtr_last_location = null;
        VA_postal.wtr_done = null;
        VA_postal.wtr_Stuck_npc = null;
        VA_postal.wtr_goal = null;
        VA_postal.wtr_nav = null;
        VA_postal.wtr_goalselector = null;
        VA_postal.wtr_controller = null;
        VA_postal.wtr_swaypoint = null;
        VA_postal.wtr_swaypoint_last = null;
        VA_postal.wtr_swaypoint_next = null;
        VA_postal.wtr_waypoint = null;
        VA_postal.wtr_waypoint_last = null;
        VA_postal.wtr_waypoint_next = null;
        VA_postal.wtr_waypoint_dynamic = null;
        VA_postal.wtr_schest_location = null;
        VA_postal.wtr_schest_location_postoffice = null;
        VA_postal.wtr_chest_open = null;
        VA_postal.wtr_dist_next = null;
        VA_postal.wtr_range = null;
        VA_postal.wtr_pos = null;
        VA_postal.wtr_pos_next = null;
        VA_postal.wtr_pos_last = null;
        VA_postal.wtr_pos_final = null;
        VA_postal.wtr_forward = null;
        VA_postal.wtr_speed_factor = null;
        VA_postal.wtr_door_speed = null;
        VA_postal.wtr_poffice = null;
        VA_postal.wtr_address = null;
        VA_postal.wtr_qpair = null;
        VA_postal.wtr_trap_door = null;
        VA_postal.wtr_door = null;
        VA_postal.wtr_door_nav = null;
        VA_postal.wtr_door_nav_enter = null;
        VA_postal.wtr_sdoor_location = null;
        VA_postal.wtr_arriving = null;
        VA_postal.wtr_arrived = null;
        VA_postal.wtr_cooling = null;
        VA_postal.wtr_ext_watchdog_reset = null;
        VA_postal.wtr_goal_active = null;


        VA_postal.central_array_name = new String[al];
        VA_postal.central_array_location = new String[al];
        VA_postal.central_array_time = new long[al];
        VA_postal.central_array_promoted = new boolean[al];
        VA_postal.wtr_npc = new NPC[al];
        VA_postal.wtr_inventory_postoffice = new Inventory[al];
        VA_postal.wtr_inventory_npc = new PlayerInventory[al];
        VA_postal.wtr_inventory_address = new Inventory[al];
        VA_postal.wtr_npc_player = new Player[al];
        VA_postal.wtr_last_stuck_action = new String[al];
        VA_postal.wtr_last_stuck_stamp = new long[al];
        VA_postal.wtr_slocation_local_po = new String[al];
        VA_postal.wtr_slocation_local_po_spawn = new String[al];
        VA_postal.wtr_slocation_address = new String[al];
        VA_postal.wtr_slocation_address_spawn = new String[al];
        VA_postal.wtr_postal_route_start = new boolean[al];
        VA_postal.wtr_not_postal_fired = new boolean[al];
        VA_postal.wtr_waypoint_completed = new boolean[al];
        VA_postal.wtr_watchdog_stuck_ms = new int[al];
        VA_postal.wtr_watchdog_stuck_retry = new int[al];
        VA_postal.wtr_watchdog_stuck_stamp = new long[al];
        VA_postal.wtr_watchdog_ext_npc_stamp = new int[al];
        VA_postal.wtr_watchdog_stuck_pos = new int[al];
        VA_postal.wtr_watchdog_ext_last_location = new Location[al];
        VA_postal.wtr_last_location = new Location[al];
        VA_postal.wtr_done = new boolean[al];
        VA_postal.wtr_Stuck_npc = new Stuck_NPC[al];
        VA_postal.wtr_goal = new Goal_WTR[al];
        VA_postal.wtr_nav = new Navigator[al];
        VA_postal.wtr_goalselector = new GoalSelector[al];
        VA_postal.wtr_controller = new GoalController[al];
        VA_postal.wtr_swaypoint = new String[al];
        VA_postal.wtr_swaypoint_last = new String[al];
        VA_postal.wtr_swaypoint_next = new String[al];
        VA_postal.wtr_waypoint = new Location[al];
        VA_postal.wtr_waypoint_last = new Location[al];
        VA_postal.wtr_waypoint_next = new Location[al];
        VA_postal.wtr_waypoint_dynamic = new String[al];
        VA_postal.wtr_schest_location = new String[al];
        VA_postal.wtr_schest_location_postoffice = new String[al];
        VA_postal.wtr_chest_open = new boolean[al];
        VA_postal.wtr_dist_next = new double[al];
        VA_postal.wtr_range = new float[al];
        VA_postal.wtr_pos = new int[al];
        VA_postal.wtr_pos_next = new int[al];
        VA_postal.wtr_pos_last = new int[al];
        VA_postal.wtr_pos_final = new int[al];
        VA_postal.wtr_forward = new boolean[al];
        VA_postal.wtr_speed_factor = new float[al];
        VA_postal.wtr_door_speed = new float[al];
        VA_postal.wtr_poffice = new String[al];
        VA_postal.wtr_address = new String[al];
        VA_postal.wtr_qpair = new String[al];
        VA_postal.wtr_trap_door = new BlockState[al];
        VA_postal.wtr_door = new boolean[al];
        VA_postal.wtr_door_nav = new boolean[al];
        VA_postal.wtr_door_nav_enter = new boolean[al];
        VA_postal.wtr_sdoor_location = new String[al];
        VA_postal.wtr_arriving = new boolean[al];
        VA_postal.wtr_arrived = new boolean[al];
        VA_postal.wtr_cooling = new boolean[al];
        VA_postal.wtr_ext_watchdog_reset = new boolean[al];
        VA_postal.wtr_goal_active = new boolean[al];


        if (VA_postal.dynmap_configured) {
            VA_postal.dyn_postman = null;
            VA_postal.dyn_postman = new CircleMarker[al];
            VA_postal.dyn_postman_po = null;
            VA_postal.dyn_postman_po = new String[al];
            VA_postal.dyn_route = null;
            VA_postal.dyn_route = new PolyLineMarker[al];
        }
    }

    public static synchronized void clear_nav_and_goals() {
        for (int i = 0; i < VA_postal.wtr_controller.length; i++) {
            ID_WTR.clear_goal(i);
        }
    }
}
