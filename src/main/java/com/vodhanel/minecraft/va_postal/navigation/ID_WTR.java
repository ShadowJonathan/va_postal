package com.vodhanel.minecraft.va_postal.navigation;

import com.vodhanel.minecraft.va_postal.VA_postal;
import com.vodhanel.minecraft.va_postal.common.AnsiColor;
import com.vodhanel.minecraft.va_postal.common.P_Towny;
import com.vodhanel.minecraft.va_postal.common.Util;
import com.vodhanel.minecraft.va_postal.common.VA_Dispatcher;
import com.vodhanel.minecraft.va_postal.config.C_Citizens;
import com.vodhanel.minecraft.va_postal.config.C_Owner;
import com.vodhanel.minecraft.va_postal.config.C_Queue;
import com.vodhanel.minecraft.va_postal.config.C_Route;
import com.vodhanel.minecraft.va_postal.mail.ID_Mail;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.LookClose;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.material.Gate;

import java.util.Map;

import static com.vodhanel.minecraft.va_postal.common.Util.df;
import static net.citizensnpcs.util.Util.faceLocation;

public class ID_WTR {
    public ID_WTR() {
    }

    public static void start_postal_route(int id) {
        String spostoffice = VA_postal.wtr_poffice[id];
        String saddress = VA_postal.wtr_address[id];

        ID_Mail.set_postoffice_chest_inv(id);
        if (VA_postal.wtr_inventory_postoffice[id] != null) {
            ID_Mail.npc_start_route(id);

            if (!ID_Mail.po_chest_contains_postal_log(id)) {
                ID_Mail.po_create_and_install_postal_log(id);
            }
            try {
                VA_postal.wtr_npc_player[id] = ((Player) VA_postal.wtr_npc[id].getEntity());
                VA_postal.wtr_npc_player[id].closeInventory();
            } catch (Exception e) {
            }
        } else {
            Util.binform("&f&o" + df(spostoffice) + " &c&odoes not have a mail chest in the post office.");
        }

        ID_Mail.set_address_chest_inv(id);
        if (VA_postal.wtr_inventory_address[id] == null) {
            Util.binform("&f&o" + df(spostoffice) + ", " + saddress + " &c&odoes not have a mail chest.");
        } else {
            ID_Mail.route_housekeeping(id);
        }


        if ((VA_postal.towny_configured) && (VA_postal.towny_opt_in) &&
                (!"null".equals(VA_postal.wtr_schest_location[id]))) {
            String sloc = VA_postal.wtr_schest_location[id];
            Location loc = Util.str2location(sloc);
            Map.Entry<String, Player> addr_owner = P_Towny.towny_addr_owner_by_loc(loc);
            if (!addr_owner.getKey().equals("not_towny")) {
                if (addr_owner.getKey().equals("un_owned_plot")) {
                    if (C_Owner.is_address_owner_defined(spostoffice, saddress)) {
                        C_Owner.del_owner_address(spostoffice, saddress);
                        Util.cinform("Towny override: Owner removed from: " + df(spostoffice) + ", " + df(saddress));
                    }

                } else if (C_Owner.is_address_owner_defined(spostoffice, saddress)) {
                    Player tname = C_Owner.get_owner_address(spostoffice, saddress);
                    if (tname == addr_owner.getValue()) {
                        C_Owner.set_owner_address(spostoffice, saddress, addr_owner.getValue());
                        Util.cinform("Towny override: New owner: " + df(spostoffice) + ", " + df(saddress));
                    }
                } else {
                    C_Owner.set_owner_address(spostoffice, saddress, addr_owner.getValue());
                    Util.cinform("Towny override: New owner: " + df(spostoffice) + ", " + df(saddress));
                }
            }
        }
    }

    public static void arrived_at_address(int id) {
        String spostoffice = VA_postal.wtr_poffice[id];
        String saddress = VA_postal.wtr_address[id];

        ID_Mail.set_address_chest_inv(id);


        String slocation = Util.put_point_on_ground(VA_postal.wtr_slocation_address_spawn[id], false);
        Location target = Util.str2location(slocation);
        if (VA_postal.wtr_npc[id].getEntity().getLocation() != target) {
            VA_postal.wtr_nav[id].getDefaultParameters().speedModifier(0.5F);
            Util.dinform(AnsiColor.GREEN + "AAA: New target for " + id + " " + target);
            VA_postal.wtr_nav[id].setTarget(target);
            if ((VA_postal.wtr_controller[id] != null) &&
                    (VA_postal.wtr_controller[id].isPaused())) {
                VA_postal.wtr_controller[id].setPaused(false);
            }
        }


        ID_Mail.set_postoffice_chest_inv(id);
        if (VA_postal.wtr_inventory_postoffice[id] == null) {
            return;
        }

        if (VA_postal.wtr_inventory_address[id] != null) {
            try {
                VA_postal.wtr_npc_player[id].openInventory(VA_postal.wtr_inventory_address[id]);
                VA_postal.wtr_chest_open[id] = true;
            } catch (Exception e) {
                VA_postal.wtr_chest_open[id] = false;
            }


            ID_Mail.npc_deliver_mail(id);


            ID_Mail.npc_pickup_mail(id);


            if (ID_Mail.chest_contains_postal_log(id)) {
                Util.dinform(AnsiColor.CYAN + "Postal log exists");
                ID_Mail.npc_update_postal_log(id);
            } else {
                Util.dinform(AnsiColor.CYAN + "Installing new postal log");
                ID_Mail.npc_create_and_install_postal_log(id);
            }
        }
    }

    public static void returned_to_postoffice(int id) {
        String spostoffice = VA_postal.wtr_poffice[id];
        String saddress = VA_postal.wtr_address[id];


        VA_postal.wtr_npc_player[id] = ((Player) VA_postal.wtr_npc[id].getEntity());
        VA_postal.wtr_npc_player[id].setItemOnCursor(null);


        ID_Mail.set_postoffice_chest_inv(id);

        if (VA_postal.wtr_inventory_postoffice[id] != null) {
            try {
                VA_postal.wtr_npc_player[id].openInventory(VA_postal.wtr_inventory_postoffice[id]);
            } catch (Exception e) {
            }
            ID_Mail.npc_post_office_return_from_route(id);
        }
    }

    public static void tp_npc(NPC npc, Location target) {
        if ((npc != null) && (target != null)) {
            if (!npc.isSpawned()) {
                npc.spawn(target);
            } else {
                npc.getEntity().teleport(target);
            }
        }
    }

    public static synchronized void report_recovery(int id, String msg) {
        if (C_Citizens.report_nav_probs()) {
            Util.cinform("\033[0;33m[Postal] \033[1;32m" + msg);
            Util.cinform("\033[0;33m[Postal] Nav recovery for : \033[0;37m" + VA_postal.wtr_poffice[id] + "\033[0;33m" + " postman ");
            Util.cinform("\033[0;33m[Postal] While servicing  : \033[0;37m" + VA_postal.wtr_address[id]);
            Util.cinform("\033[0;33m[Postal] Waypoint sequence: \033[0;37m" + VA_postal.wtr_pos[id]);
            Util.cinform("\033[1;32m-------------------------------------------------------");
        }
    }

    public static synchronized boolean npc_should_run(int id) {
        if (!VA_Dispatcher.dispatcher_running) {
            Util.dinform(AnsiColor.RED + "SNR: DISPATCHER IS NOT RUNNING");
            return false;
        }
        if (!VA_postal.wtr_goal_active[id]) {
            Util.dinform(AnsiColor.RED + "SNR: GOAL IS NOT ACTIVE");
            return false;
        }
        if (VA_postal.wtr_qpair[id] == null) {
            Util.dinform(AnsiColor.RED + "SNR: QPAIR IS NULL");
            return false;
        }
        if (VA_postal.wtr_npc_player[id] == null) {
            Util.dinform(AnsiColor.RED + "SNR: NPC PLAYER IS NULL");
            return false;
        }
        if (VA_postal.wtr_npc[id] == null) {
            Util.dinform(AnsiColor.RED + "SNR: NPC IS NULL");
            return false;
        }
        if (!VA_postal.wtr_npc_player[id].isValid()) {
            Util.dinform(AnsiColor.RED + "SNR: NPC PLAYER IS INVALID " + id);
            Util.dinform(AnsiColor.RED + "SNR: " + VA_postal.wtr_npc_player[id].getLocation() + " " + VA_postal.wtr_npc_player[id].getDisplayName());
            //return false;
        }
        if (VA_postal.wtr_nav[id] == null) {
            Util.dinform(AnsiColor.RED + "SNR: NAV IS NULL");
            return false;
        }
        if (VA_postal.wtr_goal[id] == null) {
            Util.dinform(AnsiColor.RED + "SNR: GOAL IS NULL");
            return false;
        }
        if (VA_postal.wtr_goalselector[id] == null) {
            Util.dinform(AnsiColor.RED + "SNR: GOAL SELECTOR IS NULL");
            return false;
        }
        if (VA_postal.wtr_controller[id] == null) {
            Util.dinform(AnsiColor.RED + "SNR: GOAL CONTROLLER IS NULL");
            return false;
        }
        if (VA_postal.wtr_poffice[id] == null) {
            Util.dinform(AnsiColor.RED + "SNR: POST OFFICE IS NULL");
            return false;
        }
        if (VA_postal.wtr_address[id] == null) {
            Util.dinform(AnsiColor.RED + "SNR: ADRESS IS NULL");
            return false;
        }
        return true;
    }

    public static void clear_goal(int id) {
        VA_postal.wtr_goal_active[id] = false;
        VA_postal.wtr_qpair[id] = null;
        VA_postal.wtr_poffice[id] = null;
        VA_postal.wtr_address[id] = null;

        if (VA_postal.wtr_nav[id] != null) {
            VA_postal.wtr_nav[id] = null;
        }
        if ((VA_postal.wtr_goal[id] != null) && (VA_postal.wtr_goalselector[id] != null) && (VA_postal.wtr_controller[id] != null)) {
            VA_postal.wtr_goalselector[id].finishAndRemove();
            if (VA_postal.wtr_controller[id] != null) {
                VA_postal.wtr_controller[id].clear();
            }
        }
        if (VA_postal.wtr_goal[id] != null) {
            VA_postal.wtr_goal[id] = null;
        }
        if (VA_postal.wtr_goalselector[id] != null) {
            VA_postal.wtr_goalselector[id] = null;
        }
        if (VA_postal.wtr_controller[id] != null) {
            VA_postal.wtr_controller[id] = null;
        }
    }

    public static synchronized boolean watchdog_check(int id) {
        if ((!VA_Dispatcher.dispatcher_running) || (VA_postal.wtr_goalselector[id] == null)) {
            return true;
        }
        if ((!VA_postal.wtr_goal_active[id]) || (VA_postal.wtr_cooling[id])) {
            return true;
        }
        if (VA_postal.wtr_npc[id].getNavigator().isNavigating()) {
            VA_postal.wtr_watchdog_sys_ext_stamp = Util.time_stamp();
            VA_postal.wtr_watchdog_ext_npc_stamp[id] = Util.time_stamp();
        }
        long elapsed_seconds_since_last_npc_aciviy = System.currentTimeMillis() - VA_postal.wtr_watchdog_stuck_stamp[id];
        if (elapsed_seconds_since_last_npc_aciviy > VA_postal.wtr_watchdog_stuck_ms[id]) {
            VA_postal.wtr_watchdog_stuck_stamp[id] = System.currentTimeMillis();
            VA_postal.wtr_watchdog_stuck_retry[id] += 1;
            return true;
        }
        return false;
    }

    public static synchronized void cancel_route(int id) {
        if ((!VA_Dispatcher.dispatcher_running) || (!VA_postal.wtr_goal_active[id])) {
            return;
        }
        VA_postal.wtr_done[id] = true;

        String slocation = VA_postal.wtr_slocation_local_po_spawn[id];
        Location target = Util.str2location(slocation);
        try {
            VA_postal.wtr_npc[id].despawn();
            VA_postal.wtr_npc[id].spawn(target);
            VA_postal.wtr_npc_player[id] = ((Player) VA_postal.wtr_npc[id].getEntity());
            VA_postal.wtr_inventory_npc[id] = VA_postal.wtr_npc_player[id].getInventory();
        } catch (Exception e) {
            Util.cinform(AnsiColor.RED + "Problem teleporting (cancel route)");
        }
        if (VA_postal.wtr_controller[id] != null) {
            VA_postal.wtr_controller[id].clear();
        }
    }

    public static void safe_re_target(int id) {
        if ((VA_postal.wtr_controller[id] != null) &&
                (VA_postal.wtr_controller[id].isPaused())) {
            VA_postal.wtr_controller[id].setPaused(false);
        }

        if ((VA_postal.wtr_nav[id] != null) && (VA_postal.wtr_waypoint[id] != null) && (!VA_postal.wtr_nav[id].isNavigating())) {
            Util.dinform(AnsiColor.GREEN + "SRT: New target for " + id + " " + VA_postal.wtr_waypoint[id] + " " + VA_postal.wtr_dist_next[id]);
            //if (VA_postal.wtr_dist_next[id] == 0.0)
            //    VA_postal.wtr_npc[id].getEntity().teleport(VA_postal.wtr_waypoint[id]);
            //else
            VA_postal.wtr_nav[id].setTarget(VA_postal.wtr_waypoint[id]);
        }
    }

    public static void safe_stop(int id) {
        if ((VA_postal.wtr_controller[id] != null) &&
                (!VA_postal.wtr_controller[id].isPaused())) {
            VA_postal.wtr_controller[id].setPaused(true);
        }

        if ((VA_postal.wtr_nav[id] != null) && (!VA_postal.wtr_nav[id].isNavigating())) {
            VA_postal.wtr_nav[id].cancelNavigation();
        }
    }

    public static boolean at_waypoint(int id) { // FIXME IS BUGGED
        //Util.dinform("at_waypoint called for " + id);
        if (!VA_postal.wtr_waypoint_completed[id]) {
            Location npc_loc = VA_postal.wtr_npc[id].getEntity().getLocation();
            Location target = VA_postal.wtr_waypoint[id];


            int nx = (int) npc_loc.getX();
            int nz = (int) npc_loc.getZ();
            int tx = (int) Math.floor(target.getX());
            int tz = (int) Math.floor(target.getZ());


            int navx = 0;// = (int) VA_postal.wtr_nav[id].getTargetAsLocation().getX();
            int navz = 0;// = (int) VA_postal.wtr_nav[id].getTargetAsLocation().getZ();

            //Util.dinform(AnsiColor.L_GREEN + "at_waypoint for " + id + ": npc: " + nx + "," + nz + " target: " + tx + "," + tz + AnsiColor.YELLOW + " navigation location: " + navx + "," + navz + " " + ((navx == tx && navz == tz) ? AnsiColor.L_GREEN + "MATCH" : AnsiColor.RED + "NO MATCH"));
            if ((nx == tx) && (nz == tz)) {
                return true;
            }

            //Util.dinform(AnsiColor.CYAN + "DISTANCE: "+npc_loc.distance(target));
            if (VA_postal.lossy_pathfinding && npc_loc.distance(target) <= 2D) {
                return true;
            }

            //Util.dinform(AnsiColor.GREEN + "at_waypoint distance for " + id + " is " + npc_loc.distance(target));
            if (((VA_postal.wtr_pos[id] == 0) || (VA_postal.wtr_pos[id] == VA_postal.wtr_pos_final[id])) &&
                    (npc_loc.distance(target) <= 3.5D)) {
                return true;
            }
        } else
            Util.dinform("wtr_waypoint_completed is true for " + id);

        return false;
    }

    public static void invoke_next_waypoint(int id) {
        if (!VA_postal.wtr_waypoint_completed[id]) {
            VA_postal.wtr_waypoint_completed[id] = true;
            VA_postal.wtr_watchdog_stuck_retry[id] = 0;
            Util.dinform(AnsiColor.GREEN + "INW: New target for " + id + " " + null);
            VA_postal.wtr_nav[id].setTarget(null);

            if (VA_postal.wtr_door[id]) {
                Util.dinform("THERE IS DOOR FOR " + id);
                door_sequencer(id);
            }

            increment_to_next_waypoint(id);

            if (VA_Dispatcher.dispatcher_running) {
                VA_postal.wtr_goalselector[id].finishAndRemove();
                RouteMngr.npc_next_waypoint(id);
            } else {
                clear_goal(id);
            }
        }
    }

    public static String location_2_XZ(Location loc) {
        String result;
        try {
            result = loc.getWorld().getName() + ",";
            double x = (int) Math.floor(loc.getX());
            result = result + Double.toString(x) + ",";
            double z = (int) Math.floor(loc.getZ());
            return result + Double.toString(z);
        } catch (Exception e) {
        }


        return "null";
    }

    public static void door_sequencer(int id) {
        Util.dinform("DOOR SEQUENCER CALLED");
        if (!VA_postal.wtr_door[id]) {
            VA_postal.wtr_door[id] = true;
            VA_postal.wtr_door_nav[id] = false;
            VA_postal.wtr_door_nav_enter[id] = false;
        }
        if (!VA_postal.wtr_door_nav[id]) {
            VA_postal.wtr_door_nav[id] = true;
            return;
        }
        if (!VA_postal.wtr_door_nav_enter[id]) {
            open_door(id, true, false);
            VA_postal.wtr_door_nav_enter[id] = true;
            return;
        }
        open_door(id, false, false);
        VA_postal.wtr_door[id] = false;
        VA_postal.wtr_door_nav[id] = false;
        VA_postal.wtr_door_nav_enter[id] = false;
    }

    public static void open_door(int id, boolean open, boolean quiet) {
        Util.dinform("open_door: " + AnsiColor.GREEN + "id " + AnsiColor.WHITE + "= [" + AnsiColor.YELLOW + id + AnsiColor.WHITE + "], " + AnsiColor.GREEN + "open " + AnsiColor.WHITE + "= [" + AnsiColor.YELLOW + open + AnsiColor.WHITE + "], " + AnsiColor.GREEN + "quiet " + AnsiColor.WHITE + "= [" + AnsiColor.YELLOW + quiet + AnsiColor.WHITE + "]");
        int m_id = -1;
        boolean twodoors = false;
        Location reference_loc = Util.str2location(VA_postal.wtr_sdoor_location[id]);
        m_id = reference_loc.add(1.0D, 0.0D, 0.0D).getBlock().getTypeId();
        if ((m_id == 64) || (m_id == 71)) {
            twodoors = true;
        }
        if (!twodoors) {
            reference_loc = Util.str2location(VA_postal.wtr_sdoor_location[id]);
            m_id = reference_loc.subtract(1.0D, 0.0D, 0.0D).getBlock().getTypeId();
            if ((m_id == 64) || (m_id == 71)) {
                twodoors = true;
            }
        }
        if (!twodoors) {
            reference_loc = Util.str2location(VA_postal.wtr_sdoor_location[id]);
            m_id = reference_loc.add(0.0D, 0.0D, 1.0D).getBlock().getTypeId();
            if ((m_id == 64) || (m_id == 71)) {
                twodoors = true;
            }
        }
        if (!twodoors) {
            reference_loc = Util.str2location(VA_postal.wtr_sdoor_location[id]);
            m_id = reference_loc.subtract(0.0D, 0.0D, 1.0D).getBlock().getTypeId();
            if ((m_id == 64) || (m_id == 71)) {
                twodoors = true;
            }
        }

        Block door1 = Util.str2location(VA_postal.wtr_sdoor_location[id]).getBlock();
        if (door1 != null) {
            if (open) {
                openDoor(id, door1, quiet);
                if (twodoors) {
                    Block door2 = reference_loc.getBlock();
                    if (door2 != null) {
                        openDoor(id, door2, quiet);
                    } else {
                        Util.cinform(AnsiColor.CYAN + "Double door found, but came up null (open)");
                    }
                }
            } else {
                closeDoor(id, door1, quiet);
                if (twodoors) {
                    Block door2 = reference_loc.getBlock();
                    if (door2 != null) {
                        closeDoor(id, door2, quiet);
                    } else {
                        Util.cinform(AnsiColor.CYAN + "Double door found, but came up null (close)");
                    }
                }
            }
        } else {
            Util.cinform(AnsiColor.CYAN + "Reference door came up null");
        }
    }

    private static void openDoor(int id, Block block, boolean quiet) {
        Util.dinform("openDoor: " + AnsiColor.GREEN + "id " + AnsiColor.WHITE + "= [" + AnsiColor.YELLOW + id + AnsiColor.WHITE + "], " + AnsiColor.GREEN + "block " + AnsiColor.WHITE + "= [" + AnsiColor.YELLOW + block + AnsiColor.WHITE + "], " + AnsiColor.GREEN + "quiet " + AnsiColor.WHITE + "= [" + AnsiColor.YELLOW + quiet + AnsiColor.WHITE + "]");
        if (block != null) {
            if (block.getType() == Material.FENCE_GATE) {
                BlockState state = block.getState();
                Gate gate = (Gate) state.getData();
                gate.setOpen(true);
                state.update();
                if (!quiet) {
                    block.getWorld().playSound(block.getLocation(), Sound.BLOCK_FENCE_GATE_OPEN, 1.0F, 0.0F);
                }
            } else if ((block.getType() == Material.IRON_DOOR_BLOCK) || (block.getType() == Material.WOODEN_DOOR)) {
                BlockState state = block.getState();
                byte the_byte = block.getData();

                int bit = 2;
                the_byte = (byte) (the_byte | 1 << bit);
                state.setRawData(the_byte);
                state.update();
                if (!quiet) {
                    block.getWorld().playSound(block.getLocation(), Sound.BLOCK_WOODEN_DOOR_OPEN, 1.0F, 0.0F);
                }
            }
        }
    }

    private static void closeDoor(int id, Block block, boolean quiet) {
        if (block != null) {
            if (block.getType() == Material.FENCE_GATE) {
                BlockState state = block.getState();
                Gate gate = (Gate) state.getData();
                gate.setOpen(false);
                state.update();
                if (!quiet) {
                    block.getWorld().playSound(block.getLocation(), Sound.BLOCK_FENCE_GATE_CLOSE, 1.0F, 0.0F);
                }
            } else if ((block.getType() == Material.IRON_DOOR_BLOCK) || (block.getType() == Material.WOODEN_DOOR)) {
                BlockState state = block.getState();
                byte the_byte = block.getData();

                int bit = 2;
                the_byte = (byte) (the_byte & (~1 << bit));
                state.setRawData(the_byte);
                state.update();
                if (!quiet) {
                    block.getWorld().playSound(block.getLocation(), Sound.BLOCK_WOODEN_DOOR_CLOSE, 1.0F, 0.0F);
                }
            }
        }
    }

    public static Location pre_and_post_door_block(int id, boolean open) {
        Location wpnt_base = Util.simplified_copy(VA_postal.wtr_swaypoint[id]);

        Location door_base = Util.simplified_copy(VA_postal.wtr_sdoor_location[id]);

        if ((door_base == null) || (wpnt_base == null)) {
            return null;
        }


        wpnt_base.setY(door_base.getY());


        Location test_loc = Util.offset_from_ref(door_base, 1, 0, 0);
        Location save_loc = null;
        if (open) {
            save_loc = Util.simplified_copy(test_loc);
        } else {
            save_loc = Util.offset_from_ref(door_base, 2, 0, 0);
        }
        double save_dist = wpnt_base.distance(test_loc);


        test_loc = Util.offset_from_ref(door_base, -1, 0, 0);
        double test_dist = wpnt_base.distance(test_loc);
        if (test_dist < save_dist) {
            if (open) {
                save_loc = Util.simplified_copy(test_loc);
            } else {
                save_loc = Util.offset_from_ref(door_base, -2, 0, 0);
            }
            save_dist = test_dist;
        }


        test_loc = Util.offset_from_ref(door_base, 0, 0, 1);
        test_dist = wpnt_base.distance(test_loc);
        if (test_dist < save_dist) {
            if (open) {
                save_loc = Util.simplified_copy(test_loc);
            } else {
                save_loc = Util.offset_from_ref(door_base, 0, 0, 2);
            }
            save_dist = test_dist;
        }


        test_loc = Util.offset_from_ref(door_base, 0, 0, -1);
        test_dist = wpnt_base.distance(test_loc);
        if (test_dist < save_dist) {
            if (open) {
                save_loc = Util.simplified_copy(test_loc);
            } else {
                save_loc = Util.offset_from_ref(door_base, 0, 0, -2);
            }
        }

        return save_loc;
    }

    public static void door_navigator(int id, boolean open) {
        Util.dinform("DOOR NAV CALLED");
        Location target = null;
        if (open) {
            target = Util.str2location(VA_postal.wtr_swaypoint_next[id]);
        } else {
            target = Util.str2location(VA_postal.wtr_swaypoint_last[id]);
        }
        if (target == null) {
            return;
        }
        Location door_loc = pre_and_post_door_block(id, open);
        if (door_loc == null) {
            return;
        }
        if ((VA_postal.lookclose_on_route) && (open)) {
            VA_postal.wtr_npc[id].getTrait(LookClose.class).lookClose(false);
        }
        target.setY(door_loc.getY());
        final Location f_target = target;
        final Location f_door_loc = door_loc;
        if (open) {
            double dist_to_door = Util.get_2d_distance(VA_postal.wtr_sdoor_location[id], VA_postal.wtr_waypoint[id]);

            if (dist_to_door > 1.0D) {
                walk_to(id, f_door_loc, 0.5F);
            }
        } else {
            VA_postal.plugin.getServer().getScheduler().runTaskLater(VA_postal.plugin, new Runnable() {
                public void run() {
                    ID_WTR.walk_to(id, f_door_loc, 0.5F);
                    VA_postal.plugin.getServer().getScheduler().runTaskLater(VA_postal.plugin, new Runnable() {
                        public void run() {
                            faceLocation(VA_postal.wtr_npc[id].getEntity(), f_target);
                            if (VA_postal.lookclose_on_route) {
                                VA_postal.plugin.getServer().getScheduler().runTaskLater(VA_postal.plugin, new Runnable() {

                                    public void run() {
                                        VA_postal.wtr_npc[id].getTrait(LookClose.class).lookClose(true);
                                    }
                                }, 40L);
                            }
                        }
                    }, 20L);
                }
            }, 60L);
        }
    }

    public static void walk_to(int id, Location target, float speed) {
        if (target == null) {
            return;
        }
        int dist_to_target = (int) Util.get_2d_distance(VA_postal.wtr_npc[id].getEntity().getLocation(), target);
        if (dist_to_target > 0) {
            if ((VA_postal.wtr_controller[id] != null) &&
                    (VA_postal.wtr_controller[id].isPaused())) {
                VA_postal.wtr_controller[id].setPaused(false);
            }

            if (VA_postal.wtr_nav[id] != null) {
                if (VA_postal.wtr_nav[id].isNavigating()) {
                    VA_postal.wtr_nav[id].cancelNavigation();
                }
                VA_postal.wtr_nav[id].getDefaultParameters().speedModifier(speed);

                Util.dinform(AnsiColor.GREEN + "WT: New target for " + id + " " + AnsiColor.L_YELLOW + target);
                VA_postal.wtr_nav[id].setTarget(target);
            }
        }
    }

    public static void show_location(Location target, int h_typ, boolean carpet) {
        if (target != null) {
            Location ground = null;
            if (carpet) {
                ground = Util.str2location(Util.put_point_on_ground(Util.location2str(target), false));
            } else {
                ground = Util.str2location(Util.put_point_on_ground(Util.location2str(target), true));
            }

            Block block = ground.getBlock();
            final int type = block.getTypeId();
            final byte data = block.getData();
            try {
                if (carpet) {
                    block.setTypeId(171);
                } else {
                    block.setTypeId(h_typ);
                }
            } catch (Exception e) {
                return;
            }
            VA_postal.plugin.getServer().getScheduler().runTaskLater(VA_postal.plugin, new Runnable() {
                public void run() {
                    try {
                        block.setTypeIdAndData(type, data, false);
                    } catch (Exception e) {
                    }
                }
            }, 40L);
        }
    }

    public static void increment_to_next_waypoint(int id) {
        Util.dinform("increment_to_next_waypoint: " + AnsiColor.GREEN + "id " + AnsiColor.WHITE + "= [" + AnsiColor.YELLOW + id + AnsiColor.WHITE + "]");
        Util.dinform("increment_to_next_waypoint: " + VA_postal.wtr_pos[id] + " " + VA_postal.wtr_pos_next[id] + " " + VA_postal.wtr_pos_final[id]);
        if (!C_Route.is_waypoint_defined(VA_postal.wtr_poffice[id], VA_postal.wtr_address[id], VA_postal.wtr_pos[id])) {
            cancel_route(id);
            Util.cinform("\033[0;33mMissing waypoint recovery. Is someone route editing? (from ID_WTR.java");
            Util.cinform("\033[0;33m" + VA_postal.wtr_poffice[id] + ", " + VA_postal.wtr_address[id]);
            return;
        }

        //Util.binform(VA_postal.wtr_pos[id]+ " "+ VA_postal.wtr_pos_final[id]);

        if (VA_postal.wtr_forward[id]) {
            if (VA_postal.wtr_pos[id] < VA_postal.wtr_pos_final[id]) {
                VA_postal.wtr_pos[id] += 1;

                if (VA_postal.wtr_pos_next[id] < VA_postal.wtr_pos_final[id]) {
                    VA_postal.wtr_pos_next[id] += 1;
                }

                /* else {
                    VA_postal.wtr_pos_final[id] -= 1;
                }*/
            } else {
                Util.dinform("\033[1;35mReached last formal waypoint at: " + VA_postal.wtr_swaypoint[id]);
                ID_Mail.SetAddress_Chest_nTP_point(id);
                arrived_at_address(id);
                C_Queue.queue_pair_activity_flag(VA_postal.wtr_qpair[id], true, false, true);
                VA_postal.wtr_forward[id] = false;
                VA_postal.wtr_pos_final[id] = 0;
                VA_postal.wtr_arriving[id] = true;
                VA_postal.wtr_waypoint_dynamic[id] = "null";
            }

        } else if (VA_postal.wtr_pos[id] > 0) {

            VA_postal.wtr_pos[id] -= 1;
            VA_postal.wtr_pos_next[id] = VA_postal.wtr_pos[id] - 1;

            if ((VA_postal.wtr_arriving[id]) && (!VA_postal.wtr_arrived[id])) {
                VA_postal.wtr_arrived[id] = true;
            } else {
                VA_postal.wtr_arriving[id] = false;
                VA_postal.wtr_arrived[id] = false;
            }
        } else {
            VA_postal.wtr_done[id] = true;
            returned_to_postoffice(id);
            return;
        }

        VA_postal.wtr_swaypoint_last[id] = VA_postal.wtr_swaypoint[id];
        VA_postal.wtr_waypoint_last[id] = Util.simplified_copy(VA_postal.wtr_waypoint[id]);
        VA_postal.wtr_pos_last[id] = VA_postal.wtr_pos[id];


        if ("null".equals(VA_postal.wtr_waypoint_dynamic[id])) {
            VA_postal.wtr_swaypoint[id] = C_Route.get_waypoint_location(VA_postal.wtr_poffice[id], VA_postal.wtr_address[id], VA_postal.wtr_pos[id]);
            //Util.dinform("SET FROM get_waypoint_location: "+id+ " "+VA_postal.wtr_swaypoint[id]);
        } else {
            //Util.dinform("SET DYNAMIC "+id+" "+VA_postal.wtr_waypoint_dynamic[id]);
            VA_postal.wtr_swaypoint[id] = VA_postal.wtr_waypoint_dynamic[id];
        }

        VA_postal.wtr_waypoint[id] = Util.str2location(VA_postal.wtr_swaypoint[id]);

        if (VA_postal.wtr_pos_next[id] >= 0) {
            if (VA_postal.wtr_pos_next[id] == VA_postal.wtr_pos[id]) {
                Util.dinform(AnsiColor.RED + "WTR POS AND NEXT ARE THE SAME: " + VA_postal.wtr_pos_next[id]);
            }

            try {
                VA_postal.wtr_swaypoint_next[id] = C_Route.get_waypoint_location(VA_postal.wtr_poffice[id], VA_postal.wtr_address[id], VA_postal.wtr_pos_next[id]);
                VA_postal.wtr_waypoint_next[id] = Util.str2location(VA_postal.wtr_swaypoint_next[id]);
                VA_postal.wtr_dist_next[id] = Util.get_2d_distance(VA_postal.wtr_waypoint_last[id], VA_postal.wtr_waypoint[id]);
            } catch (Exception e) {
                return;
            }
        }


        if (!VA_postal.wtr_door[id]) {
            if (VA_postal.wtr_swaypoint[id] == null) {
                Util.dinform(AnsiColor.RED + "WAYPOINT " + id + " IS NULL");
            }

            Location location = scan_for_door_enroute(id, VA_postal.wtr_swaypoint[id], VA_postal.wtr_swaypoint_next[id]);

            if (location != null) {
                VA_postal.wtr_sdoor_location[id] = Util.location2str(location);
                door_sequencer(id);
                Util.dinform("\033[1;35mInitial door detection at: " + VA_postal.wtr_sdoor_location[id]);
                Util.dinform("\033[1;35mBetween: " + VA_postal.wtr_swaypoint[id] + " and " + VA_postal.wtr_swaypoint_next[id]);
            }
        }
    }

    private static Location scan_for_door_enroute(int id, String start_waypoint, String target_waypoint) {
        Util.dinform("called scan_for_door_enroute");
        if (start_waypoint == null) {
            throw new NullPointerException("START WAYPOINT IS NULL");
        }
        //Util.dinform("scan_for_door_enroute: " + id + " " + start_waypoint + " " + target_waypoint);
        start_waypoint = Util.put_point_on_ground(start_waypoint, false);
        Location start = Util.simplified_copy(start_waypoint);
        Location target = Util.simplified_copy(target_waypoint);


        if ((start == null) || (target == null)) {
            Util.dinform(AnsiColor.RED + "scan_for_door_enroute RETURN DUE TO START AND TARGET NULL");
            return null;
        }

        int segment_dist = (int) start.distance(target);
        if (VA_postal.strict_door_nav) {
            if (segment_dist > 5) {
                Util.dinform("scan_for_door_enroute return due to scrict segments");
                return null;
            }
        } else if (segment_dist > 10) {
            Util.dinform("scan_for_door_enroute return due to segment over 10: " + segment_dist);
            return null;
        }


        double xs = (int) Math.floor(start.getX());
        double xt = (int) Math.floor(target.getX());
        double zs = (int) Math.floor(start.getZ());
        double zt = (int) Math.floor(target.getZ());
        double xdif = Math.abs(xs - xt);
        double zdif = Math.abs(zs - zt);
        int dist = 3;


        if (VA_postal.strict_door_nav) {
            if ((xdif != 0.0D) && (zdif != 0.0D)) {
                Util.dinform("scan_for_door_enroute return strict: " + xdif + " " + zdif);
                return null;
            }
            dist = 3;
        } else {
            if ((xdif > 2.0D) && (zdif > 2.0D)) {
                Util.dinform("scan_for_door_enroute return: " + xdif + " " + zdif);
                return null;
            }
            dist = 4;
        }

        if (segment_dist > 1) {
            segment_dist -= 1;
        }
        if (segment_dist < dist) {
            dist = segment_dist;
        }

        Location door;

        if (xdif > zdif) {
            if (xs < xt) {
                door = scan_x_axis_for_door(start, true, dist);
            } else {
                door = scan_x_axis_for_door(start, false, dist);
            }
        } else if (zs < zt) {
            door = scan_z_axis_for_door(start, true, dist);
        } else {
            door = scan_z_axis_for_door(start, false, dist);
        }


        if (door == null) {
            Util.dinform("scan_for_door_enroute: did not find door...");
            return null;
        } else
            Util.dinform("scan_for_door_enroute: did find door!");


        Location test_loc = Util.offset_from_ref(door, 0, -1, 0);
        Block test_block = test_loc.getBlock();
        int m_id = test_block.getTypeId();
        if ((m_id == 64) || (m_id == 71) || (m_id == 107)) {
            return test_loc;
        }

        return door;
    }


    private static Location scan_x_axis_for_door(Location location, boolean add, int dist) {
        //Util.dinform("scan_x_axis_for_door: " + AnsiColor.GREEN + "location " + AnsiColor.WHITE + "= [" + AnsiColor.YELLOW + location + AnsiColor.WHITE + "], " + AnsiColor.GREEN + "add " + AnsiColor.WHITE + "= [" + AnsiColor.YELLOW + add + AnsiColor.WHITE + "], " + AnsiColor.GREEN + "dist " + AnsiColor.WHITE + "= [" + AnsiColor.YELLOW + dist + AnsiColor.WHITE + "]");
        Location test_loc = Util.simplified_copy(location);
        Location result = null;
        result = scan_x_elevation_for_door(test_loc, add, dist);
        if (result != null) {
            return result;
        }

        test_loc = Util.offset_from_ref(location, 0, 1, 0);
        result = scan_x_elevation_for_door(test_loc, add, dist);
        if (result != null) {
            return result;
        }

        test_loc = Util.offset_from_ref(location, 0, -1, 0);
        result = scan_x_elevation_for_door(test_loc, add, dist);
        if (result != null) {
            return result;
        }

        test_loc = Util.offset_from_ref(location, 0, 2, 0);
        result = scan_x_elevation_for_door(test_loc, add, dist);
        if (result != null) {
            return result;
        }
        return null;
    }

    private static Location scan_x_elevation_for_door(Location location, boolean add, int dist) {
        //Util.dinform("scan_x_elevation_for_door: " + AnsiColor.GREEN + "location " + AnsiColor.WHITE + "= [" + AnsiColor.YELLOW + location + AnsiColor.WHITE + "], " + AnsiColor.GREEN + "add " + AnsiColor.WHITE + "= [" + AnsiColor.YELLOW + add + AnsiColor.WHITE + "], " + AnsiColor.GREEN + "dist " + AnsiColor.WHITE + "= [" + AnsiColor.YELLOW + dist + AnsiColor.WHITE + "]");
        Location base = Util.simplified_copy(location);
        for (int i = 0; i < dist; i++) {
            if (add) {
                base.add(1.0D, 0.0D, 0.0D);
            } else {
                base.subtract(1.0D, 0.0D, 0.0D);
            }
            if (is_door_hit(base)) {
                return base;
            }
        }
        if (!VA_postal.strict_door_nav) {
            base = Util.simplified_copy(location);
            base.add(0.0D, 0.0D, 1.0D);
            for (int i = 0; i < dist; i++) {
                if (add) {
                    base.add(1.0D, 0.0D, 0.0D);
                } else {
                    base.subtract(1.0D, 0.0D, 0.0D);
                }
                if (is_door_hit(base)) {
                    return base;
                }
            }

            base = Util.simplified_copy(location);
            base.subtract(0.0D, 0.0D, 1.0D);
            for (int i = 0; i < dist; i++) {
                if (add) {
                    base.add(1.0D, 0.0D, 0.0D);
                } else {
                    base.subtract(1.0D, 0.0D, 0.0D);
                }
                if (is_door_hit(base)) {
                    return base;
                }
            }
        }
        return null;
    }

    private static Location scan_z_axis_for_door(Location location, boolean add, int dist) {
        //Util.dinform("scan_z_axis_for_door: " + AnsiColor.GREEN + "location " + AnsiColor.WHITE + "= [" + AnsiColor.YELLOW + location + AnsiColor.WHITE + "], " + AnsiColor.GREEN + "add " + AnsiColor.WHITE + "= [" + AnsiColor.YELLOW + add + AnsiColor.WHITE + "], " + AnsiColor.GREEN + "dist " + AnsiColor.WHITE + "= [" + AnsiColor.YELLOW + dist + AnsiColor.WHITE + "]");
        Location test_loc = Util.simplified_copy(location);
        Location result = null;
        result = scan_z_elevation_for_door(test_loc, add, dist);
        if (result != null) {
            return result;
        }

        test_loc = Util.offset_from_ref(location, 0, 1, 0);
        result = scan_z_elevation_for_door(test_loc, add, dist);
        if (result != null) {
            return result;
        }

        test_loc = Util.offset_from_ref(location, 0, -1, 0);
        result = scan_z_elevation_for_door(test_loc, add, dist);
        if (result != null) {
            return result;
        }

        test_loc = Util.offset_from_ref(location, 0, 2, 0);
        result = scan_z_elevation_for_door(test_loc, add, dist);
        if (result != null) {
            return result;
        }
        return null;
    }

    private static Location scan_z_elevation_for_door(Location location, boolean add, int dist) {
        //Util.dinform("scan_z_elevation_for_door: " + AnsiColor.GREEN + "location " + AnsiColor.WHITE + "= [" + AnsiColor.YELLOW + location + AnsiColor.WHITE + "], " + AnsiColor.GREEN + "add " + AnsiColor.WHITE + "= [" + AnsiColor.YELLOW + add + AnsiColor.WHITE + "], " + AnsiColor.GREEN + "dist " + AnsiColor.WHITE + "= [" + AnsiColor.YELLOW + dist + AnsiColor.WHITE + "]");
        Location base = Util.simplified_copy(location);
        for (int i = 0; i < dist; i++) {
            if (add) {
                base.add(0.0D, 0.0D, 1.0D);
            } else {
                base.subtract(0.0D, 0.0D, 1.0D);
            }
            if (is_door_hit(base)) {
                return base;
            }
        }
        if (!VA_postal.strict_door_nav) {
            base = Util.simplified_copy(location);
            base.add(1.0D, 0.0D, 0.0D);
            for (int i = 0; i < dist; i++) {
                if (add) {
                    base.add(0.0D, 0.0D, 1.0D);
                } else {
                    base.subtract(0.0D, 0.0D, 1.0D);
                }
                if (is_door_hit(base)) {
                    return base;
                }
            }

            base = Util.simplified_copy(location);
            base.subtract(1.0D, 0.0D, 0.0D);
            for (int i = 0; i < dist; i++) {
                if (add) {
                    base.add(0.0D, 0.0D, 1.0D);
                } else {
                    base.subtract(0.0D, 0.0D, 1.0D);
                }
                if (is_door_hit(base)) {
                    return base;
                }
            }
        }
        return null;
    }

    private static boolean is_door_hit(Location base) {
        //Util.dinform("is_door_hit: " + AnsiColor.GREEN + "base " + AnsiColor.WHITE + "= [" + AnsiColor.YELLOW + base + AnsiColor.WHITE + "]");
        Material m = base.getBlock().getType();
        if (m.name().toLowerCase().contains("door")) {
            Util.dinform(AnsiColor.L_GREEN + "FOUND DOOR");
            return true;
        }
        return false;
    }

    public static String proper(String string) {
        try {
            if (string.length() > 0) {
                return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase().trim();
            }
        } catch (Exception e) {
        }

        return "";
    }

    public static String fixed_len(String input, int len, String filler) {
        try {
            input = input.trim();

            if (input.length() >= len) {
                return input.substring(0, len);
            }

            while (input.length() < len) {
                input = input + filler;
            }
            return input;
        } catch (Exception e) {
            String blank = "";
            for (int i = 0; i < len; i++) {
                blank = blank + filler;
            }
            return blank;
        }
    }
}
