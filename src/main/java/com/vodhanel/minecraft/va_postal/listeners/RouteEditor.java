package com.vodhanel.minecraft.va_postal.listeners;

import com.vodhanel.minecraft.va_postal.VA_postal;
import com.vodhanel.minecraft.va_postal.commands.Cmd_static;
import com.vodhanel.minecraft.va_postal.common.P_Towny;
import com.vodhanel.minecraft.va_postal.common.Util;
import com.vodhanel.minecraft.va_postal.common.VA_Dispatcher;
import com.vodhanel.minecraft.va_postal.common.VA_Timers;
import com.vodhanel.minecraft.va_postal.config.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

import static com.vodhanel.minecraft.va_postal.common.Util.df;

public class RouteEditor implements org.bukkit.event.Listener {
    public static VA_postal plugin;
    public static org.bukkit.scheduler.BukkitTask plistener_hud_worker = null;
    public static List<Integer> route_blocks_type = null;
    public static List<Byte> route_blocks_data = null;
    public static List<Location> route_blocks_loc = null;
    public static int wpnt_hilite_id = 152;
    public static boolean process_ground_click = false;
    private static long last_click = 0L;
    private static String last_2dloc = "";
    private static boolean moving_waypoint = false;
    private static boolean removing_waypoint = false;
    private static boolean inserting_waypoint = false;
    private static boolean waypoint_inquire = false;
    private static int selected_pos = -1;
    private static Location saved_loc = null;

    public RouteEditor(VA_postal plugin) {
        this.plugin = plugin;
    }

    private static boolean invalid_player(org.bukkit.entity.Entity entity) {
        if (VA_postal.plistener_player == null) {
            return true;
        }

        if (entity == null) {
            return true;
        }

        if (entity != VA_postal.plistener_player) {
            return true;
        }
        return false;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public static void onPlayerInteract(PlayerInteractEvent event) {
        if (invalid_player(event.getPlayer())) {
            return;
        }

        Player player = event.getPlayer();


        if ((event.getClickedBlock() == null) || (event.getAction() == null)) {
            return;
        }


        VA_postal.plistener_last_used = Util.time_stamp();

        Block block = event.getClickedBlock();
        Action action = event.getAction();
        process_ground_click = false;
        if (should_cancel(player, block, action)) {
            if (!event.isCancelled()) {
                event.setCancelled(true);
            }
            return;
        }


        if (process_ground_click) {
            if (!event.isCancelled()) {
                event.setCancelled(true);
            }

            if ((!moving_waypoint) && (!removing_waypoint) && (!inserting_waypoint) && (!waypoint_inquire)) {
                if (action == Action.LEFT_CLICK_BLOCK) {
                    Append_Waypoint(player, block);
                } else {
                    Undo_Waypoint(player, block, false);
                }
            } else {
                if (moving_waypoint) {
                    if (action == Action.LEFT_CLICK_BLOCK) {
                        move_click(player, block);
                    } else {
                        cancel_pending_mov_del_ins(player, false);
                    }
                    return;
                }

                if (removing_waypoint) {
                    if (action == Action.LEFT_CLICK_BLOCK) {
                        remove_click(player, block);
                    } else {
                        cancel_pending_mov_del_ins(player, false);
                    }
                    return;
                }

                if (inserting_waypoint) {
                    if (action == Action.LEFT_CLICK_BLOCK) {
                        insert_click(player, block);
                    } else {
                        cancel_pending_mov_del_ins(player, false);
                    }
                    return;
                }

                if (action == Action.LEFT_CLICK_BLOCK) {
                    inquire_click(player, block);
                } else {
                    cancel_pending_mov_del_ins(player, false);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public static void onBlockPlaceEvent(BlockPlaceEvent event) {
        if ((event.isCancelled()) || (invalid_player(event.getPlayer()))) {
            return;
        }


        VA_postal.plistener_last_used = Util.time_stamp();


        event.setCancelled(true);
        event.setBuild(false);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public static void onBlockBreakEvent(BlockBreakEvent event) {
        if ((event.isCancelled()) || (invalid_player(event.getPlayer()))) {
            return;
        }


        VA_postal.plistener_last_used = Util.time_stamp();


        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public static void onBlockDamageEvent(BlockDamageEvent event) {
        if ((event.isCancelled()) || (invalid_player(event.getPlayer()))) {
            return;
        }


        VA_postal.plistener_last_used = Util.time_stamp();


        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public static void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
        if (VA_postal.plistener_player == null) {
            return;
        }


        if ((event.getPlayer() != VA_postal.plistener_player) || (event.getMessage() == null) || (event.getPlayer() == null)) {
            return;
        }


        VA_postal.plistener_last_used = Util.time_stamp();

        Player player = event.getPlayer();
        String entered_cmd = event.getMessage().toLowerCase().trim();
        String[] parts = entered_cmd.split(" ");
        String root = parts[0].trim();
        String[] args = new String[parts.length - 1];
        for (int i = 0; i < args.length; i++) {
            args[i] = parts[(i + 1)].trim();
        }
        cancel_pending_mov_del_ins(player, false);

        if (("/exit".equalsIgnoreCase(root)) || ("/e".equalsIgnoreCase(root))) {
            if (!event.isCancelled()) {
                event.setCancelled(true);
            }
            Exit_routeEditor(player);
            return;
        }
        if (("/undo".equalsIgnoreCase(root)) || ("/u".equalsIgnoreCase(root))) {
            if (!event.isCancelled()) {
                event.setCancelled(true);
            }
            Undo_Waypoint(player, null, true);
            return;
        }
        if (("/new".equalsIgnoreCase(root)) || ("/n".equalsIgnoreCase(root))) {
            if (!event.isCancelled()) {
                event.setCancelled(true);
            }
            New_Route(player);
            return;
        }
        if (("/pos".equalsIgnoreCase(root)) || ("/p".equalsIgnoreCase(root))) {
            if (!event.isCancelled()) {
                event.setCancelled(true);
            }
            Waypoint_Pos(player, args[0]);
            return;
        }
        if (("/first".equalsIgnoreCase(root)) || ("/f".equalsIgnoreCase(root))) {
            if (!event.isCancelled()) {
                event.setCancelled(true);
            }
            First_Waypoint(player);
            return;
        }
        if (("/mid".equalsIgnoreCase(root)) || ("/d".equalsIgnoreCase(root))) {
            if (!event.isCancelled()) {
                event.setCancelled(true);
            }
            Mid_Waypoint(player);
            return;
        }
        if (("/last".equalsIgnoreCase(root)) || ("/l".equalsIgnoreCase(root))) {
            if (!event.isCancelled()) {
                event.setCancelled(true);
            }
            Last_Waypoint(player);
            return;
        }
        if (("/move".equalsIgnoreCase(root)) || ("/m".equalsIgnoreCase(root))) {
            if (!event.isCancelled()) {
                event.setCancelled(true);
            }
            Move_Waypoint(player);
            return;
        }
        if (("/insert".equalsIgnoreCase(root)) || ("/i".equalsIgnoreCase(root))) {
            if (!event.isCancelled()) {
                event.setCancelled(true);
            }
            Insert_Waypoint(player);
            return;
        }
        if (("/remove".equalsIgnoreCase(root)) || ("/r".equalsIgnoreCase(root))) {
            if (!event.isCancelled()) {
                event.setCancelled(true);
            }
            Remove_Waypoint(player);
            return;
        }
        if ("/".equals(root)) {
            if (!event.isCancelled()) {
                event.setCancelled(true);
            }
            Position_Inquire(player);
            return;
        }

        if (!event.isCancelled()) {
            event.setCancelled(true);
        }
        Util.pinform(player, "&cYou are currently in the route editor");
        Util.pinform(player, "&eEditing:  &f" + df(VA_postal.plistener_local_po) + ", " + df(VA_postal.plistener_address));
        Util.pinform(player, "&6Double click your final waypint to exit, or use /Exit");
        Util.pinform(player, "&eGeneral: &f/Undo  /New  /First  /miD  /Last  /Pos <#>");
        Util.pinform(player, "&eEdit:      &f/Move  /Insert  /Remove '/' = position inquire");
        Util.pinform(player, "&eAlias:     &6/U  /N  /F  /D  /L  /P  /M  /I  /R  /E");
    }

    public static boolean should_cancel(Player player, Block block, Action action) {
        if (VA_postal.plistener_cooling) {
            return true;
        }

        switch (action) {
            case LEFT_CLICK_BLOCK:
                break;
            case RIGHT_CLICK_BLOCK:
                break;
            default:
                return false;
        }

        String s2dlocation = location_2_XZ(block.getLocation());
        if (s2dlocation.equals(last_2dloc)) {
            long elapse_click_ms = System.currentTimeMillis() - last_click;
            if ((elapse_click_ms > 100L) && (elapse_click_ms < 500L)) {
                if (action == Action.LEFT_CLICK_BLOCK) {
                    Exit_routeEditor(player);
                    return true;
                }
            }
        }
        last_click = System.currentTimeMillis();
        last_2dloc = location_2_XZ(block.getLocation());


        int ti = block.getTypeId();
        if ((ti == 64) || (ti == 71) || (ti == 96) || (ti == 63) || (ti == 68) || (ti == 107)) {
            if (action == Action.RIGHT_CLICK_BLOCK) {
                return false;
            }

            return true;
        }

        process_ground_click = true;
        return false;
    }

    public static void Append_Waypoint(Player player, Block block) {
        Location location = block.getLocation();
        String stown = VA_postal.plistener_local_po;
        String saddress = VA_postal.plistener_address;

        String s2dlocation = location_2_XZ(location);
        String slast_waypoint = C_Route.get_last_waypoint_location(stown, saddress);
        String s2dlocation_last = location_2_XZ(Util.str2location(slast_waypoint));
        if (!s2dlocation.equals(s2dlocation_last)) {
            if ((VA_postal.using_towny()) && (P_Towny.is_waypoint_limit(player, stown, saddress))) {
                return;
            }
            Location current_location = block.getLocation();
            String slocation = Util.location2str(current_location);
            VA_postal.plistener_last_slocation = slocation;

            if (append_marker(slocation)) {
                int pos = C_Route.append_route_waypoint(stown, saddress, slocation);
                if (pos >= 0) {
                    Util.pinform(player, "&7&oWaypoint:  &f&r" + pos + " &7&oLocation:  &f&r" + slocation);
                } else {
                    Util.pinform(player, "&c&oUnexpected problem seting waypoint.  Exiting.");
                    Util.pinform(player, "&7&oWaypoint:  &f&r" + pos + " &7&oLocation:  &f&r" + slocation);
                    Exit_routeEditor(player);
                }

                VA_postal.plistener_last_2d_location = s2dlocation;
            } else {
                Util.pinform(player, "&c&oThat is not a valid surface to mark a waypoint.");
            }
        }
    }

    public static void Undo_Waypoint(Player player, Block block, boolean overide_dist) {
        String stown = VA_postal.plistener_local_po;
        String saddress = VA_postal.plistener_address;
        int cur_pos = C_Route.get_last_waypoint_position(stown, saddress);
        String slocation = C_Route.get_waypoint_location(stown, saddress, cur_pos);
        long click_dist = 0L;
        if (!overide_dist) {
            Location click_location = block.getLocation();
            try {
                click_dist = (long) click_location.distance(Util.str2location(slocation));
            } catch (Exception e) {
                click_dist = 5L;
            }
        }


        if ((click_dist < 2L) || (overide_dist)) {
            if (cur_pos > 0) {
                boolean result = C_Route.delete_last_waypoint(stown, saddress);
                if (result) {
                    remove_last_marker();
                    cur_pos -= 1;
                    slocation = C_Route.get_waypoint_location(stown, saddress, cur_pos);
                    if (overide_dist) {
                        Util.safe_tps(player, slocation);
                    }
                    Util.pinform(player, "&7&oBack one waypoint.  You are now on waypoint: &f&r" + cur_pos);
                } else {
                    Util.pinform(player, "&c&oUnexpected problem undo'ing waypoint.  Exiting.");
                    Util.pinform(player, "&7&oWaypoint:  &f&r" + cur_pos + " &7&oLocation:  &f&r" + slocation);
                    Exit_routeEditor(player);
                }
            } else {
                New_Route(player);
            }
        } else {
            Util.pinform(player, "&7&oWaypoint:  &f&r" + cur_pos + " &7&oLocation:  &f&r" + slocation);
            Util.pinform(player, "&7&oYou are &f&r" + click_dist + " &7&oBlocks from your last waypoint.");
        }
    }

    public static void New_Route(Player player) {
        String stown = VA_postal.plistener_local_po;
        String saddress = VA_postal.plistener_address;
        clear_route_markers();
        C_Route.delete_route(stown, saddress);
        Util.pinform(player, "&7&oExisting route deleted, teleported back to: &f&r" + VA_postal.plistener_local_po);
        String slocation = C_Postoffice.get_local_po_location_by_name(VA_postal.plistener_local_po);
        Util.safe_tps(player, slocation);
    }

    public static void First_Waypoint(Player player) {
        String stown = VA_postal.plistener_local_po;
        String saddress = VA_postal.plistener_address;
        int cur_pos = 0;
        String slocation = C_Route.get_waypoint_location(stown, saddress, cur_pos);
        Util.pinform(player, "&7&oTeleported to first waypoint: &f&r" + slocation);
        Util.safe_tps(player, slocation);
    }

    public static void Mid_Waypoint(Player player) {
        String stown = VA_postal.plistener_local_po;
        String saddress = VA_postal.plistener_address;
        int cur_pos = C_Route.get_last_waypoint_position(stown, saddress);
        cur_pos /= 2;
        String slocation = C_Route.get_waypoint_location(stown, saddress, cur_pos);
        Util.pinform(player, "&7&oTeleported to mid waypoint: &f&r" + slocation);
        Util.safe_tps(player, slocation);
    }

    public static void Last_Waypoint(Player player) {
        String stown = VA_postal.plistener_local_po;
        String saddress = VA_postal.plistener_address;
        int cur_pos = C_Route.get_last_waypoint_position(stown, saddress);
        String slocation = C_Route.get_waypoint_location(stown, saddress, cur_pos);
        Util.pinform(player, "&7&oTeleported to last waypoint: &f&r" + slocation);
        Util.safe_tps(player, slocation);
    }

    public static void Waypoint_Pos(Player player, String spos) {
        String stown = VA_postal.plistener_local_po;
        String saddress = VA_postal.plistener_address;
        if ((spos != null) && (!spos.isEmpty())) {
            int req_pos = Util.str2int(spos);
            int wypnt_count = C_Route.route_waypoint_count(stown, saddress);
            if ((req_pos >= 0) && (req_pos < wypnt_count)) {
                String slocation = C_Route.get_waypoint_location(stown, saddress, req_pos);
                Util.pinform(player, "&7&oTeleported to: &f&r" + slocation);
                Util.safe_tps(player, slocation);
            } else {
                Util.pinform(player, "&7&oInvalid waypoint number entered.");
            }
        } else {
            Util.pinform(player, "&7&oUsage:  /pos <waypoint position>");
        }
    }

    public static void cancel_pending_mov_del_ins(Player player, boolean quiet) {
        if (!quiet) {
            if (moving_waypoint) {
                Util.pinform(player, "&7&oCanceling waypoint move.");
            } else if (removing_waypoint) {
                Util.pinform(player, "&7&oCanceling waypoint remove.");
            } else if (inserting_waypoint) {
                Util.pinform(player, "&7&oCanceling waypoint insert.");
            }
        }
        moving_waypoint = false;
        removing_waypoint = false;
        inserting_waypoint = false;
        waypoint_inquire = false;
        selected_pos = -1;
        saved_loc = null;
    }

    public static void Move_Waypoint(Player player) {
        moving_waypoint = true;
        Util.pinform(player, "&e&oLeft click an existing waypoint to move.");
        Util.pinform(player, "&7&o<Right click to cancel move>");
    }

    public static void move_click(Player player, Block block) {
        if (block == null) {
            return;
        }
        Location location = block.getLocation();
        if (Util.valid_waypnt_block(location) == null) {
            Util.pinform(player, "&e&oInvalid waypoint location.");
            return;
        }
        if (moving_waypoint) {
            String stown = VA_postal.plistener_local_po;
            String saddress = VA_postal.plistener_address;
            int wypnt_count = C_Route.route_waypoint_count(stown, saddress);
            if (selected_pos < 0) {
                for (int i = 0; i < wypnt_count; i++) {
                    String pos_loc = C_Route.get_waypoint_location(stown, saddress, i);
                    if (Util.are_wpts_equal_2d(location, pos_loc, 2)) {
                        selected_pos = i;
                        Util.pinform(player, "&e&oLeft click the new location.");
                        Util.pinform(player, "&7&o<Right click to cancel move>");
                        break;
                    }
                }

                if (selected_pos < 0) {
                    Util.pinform(player, "&e&oLeft click an existing waypoint to move.");
                    Util.pinform(player, "&7&o<Right click to cancel move>");
                }
            } else {
                String slocation = Util.location2str(location);
                C_Route.set_route_waypoint(stown, saddress, selected_pos, slocation);
                clear_route_markers();
                place_route_markers(stown, saddress);
                cancel_pending_mov_del_ins(player, true);
                Util.pinform(player, "&e&oWaypoint successfully moved.");
            }
        }
    }

    public static void Remove_Waypoint(Player player) {
        removing_waypoint = true;
        Util.pinform(player, "&e&oLeft click an existing waypoint to remove.");
        Util.pinform(player, "&7&o<Right click to cancel waypoint removal>");
    }

    public static void remove_click(Player player, Block block) {
        if (block == null) {
            return;
        }
        Location location = Util.simplified_copy(block.getLocation());
        if (Util.valid_waypnt_block(location) == null) {
            Util.pinform(player, "&e&oInvalid waypoint location.");
            return;
        }
        if (removing_waypoint) {
            String stown = VA_postal.plistener_local_po;
            String saddress = VA_postal.plistener_address;
            int wypnt_count = C_Route.route_waypoint_count(stown, saddress);
            if (selected_pos < 0) {
                for (int i = 0; i < wypnt_count; i++) {
                    String pos_loc = C_Route.get_waypoint_location(stown, saddress, i);
                    if (Util.are_wpts_equal_2d(location, pos_loc, 2)) {
                        selected_pos = i;
                        saved_loc = location.clone();
                        Util.pinform(player, "&e&oLeft click this waypoint again to confirm.");
                        Util.pinform(player, "&7&o<Right click to cancel waypoint removal>");
                        break;
                    }
                }

                if (selected_pos < 0) {
                    Util.pinform(player, "&e&oLeft click an existing waypoint to remove.");
                    Util.pinform(player, "&7&o<Right click to cancel waypoint removal>");
                }
            } else if (Util.are_wpts_equal_3d(saved_loc, location)) {
                wypnt_count -= 1;

                String[] wp_list = new String[wypnt_count];
                int index = 0;
                for (int i = 0; i < wypnt_count; i++) {
                    if (i == selected_pos) {
                        index++;
                    }
                    wp_list[i] = C_Route.get_waypoint_location(stown, saddress, index);
                    index++;
                }

                C_Route.delete_route(stown, saddress);
                for (int i = 0; i < wp_list.length; i++) {
                    C_Route.set_route_waypoint_ns(stown, saddress, i, wp_list[i]);
                }
                C_Route.save_config();

                clear_route_markers();
                place_route_markers(stown, saddress);
                Util.pinform(player, "&e&oWaypoint successfully removed.");
                cancel_pending_mov_del_ins(player, true);
            } else {
                cancel_pending_mov_del_ins(player, false);
            }
        }
    }

    public static void Insert_Waypoint(Player player) {
        inserting_waypoint = true;
        Util.pinform(player, "&e&oLeft click location to insert waypoint.");
        Util.pinform(player, "&7&o<Right click to cancel insert>");
    }

    public static void insert_click(Player player, Block block) {
        if (block == null) {
            return;
        }
        Location location = Util.simplified_copy(block.getLocation());
        if (Util.valid_waypnt_block(location) == null) {
            Util.pinform(player, "&e&oInvalid waypoint location.");
            return;
        }
        if (inserting_waypoint) {
            if (saved_loc == null) {
                block = Util.valid_waypnt_block(location);
                if (block == null) {
                    Util.pinform(player, "&e&oInvalid waypoint location, try again.");
                    Util.pinform(player, "&7&o<Right click to cancel waypoint insertion>");
                    return;
                }
                saved_loc = location.clone();
                Util.pinform(player, "&e&oLeft click this waypoint again to confirm.");
                Util.pinform(player, "&7&o<Right click to cancel waypoint insertion>");
            } else if (Util.are_wpts_equal_3d(saved_loc, location)) {
                String stown = VA_postal.plistener_local_po;
                String saddress = VA_postal.plistener_address;
                int wypnt_count = C_Route.route_waypoint_count(stown, saddress);

                String[] wp_list = new String[wypnt_count];
                for (int i = 0; i < wypnt_count; i++) {
                    wp_list[i] = C_Route.get_waypoint_location(stown, saddress, i);
                }
                int pos1 = -1;
                int pos2 = -1;
                double test_dist = -1.0D;
                double saved_dist = 1000.0D;

                for (int i = 0; i < wypnt_count; i++) {
                    test_dist = Util.get_3d_distance(saved_loc, wp_list[i]);
                    if (test_dist < saved_dist) {
                        saved_dist = test_dist;
                        pos1 = i;
                    }
                }

                saved_dist = 1000.0D;
                for (int i = 0; i < wypnt_count; i++) {
                    test_dist = Util.get_3d_distance(saved_loc, wp_list[i]);
                    if ((test_dist < saved_dist) && (i != pos1)) {
                        saved_dist = test_dist;
                        pos2 = i;
                    }
                }

                if ((pos1 + 1 != pos2) && (pos1 - 1 != pos2)) {
                    Util.pinform(player, "&e&oThe adjacent points are not in sequence.");
                    cancel_pending_mov_del_ins(player, false);
                    return;
                }

                int pos_high = pos1;
                if (pos2 > pos_high) {
                    pos_high = pos2;
                }

                String slocation = Util.location2str(saved_loc);
                wypnt_count += 1;
                C_Route.delete_route(stown, saddress);
                int index = 0;
                for (int i = 0; i < wypnt_count; i++) {
                    if (i != pos_high) {
                        C_Route.set_route_waypoint_ns(stown, saddress, i, wp_list[index]);
                        index++;
                    } else {
                        C_Route.set_route_waypoint_ns(stown, saddress, i, slocation);
                    }
                }
                C_Route.save_config();

                clear_route_markers();
                place_route_markers(stown, saddress);
                Util.pinform(player, "&e&oWaypoint successfully inserted.");
                cancel_pending_mov_del_ins(player, true);
            } else {
                cancel_pending_mov_del_ins(player, false);
            }
        }
    }

    public static void Position_Inquire(Player player) {
        waypoint_inquire = true;
        Util.pinform(player, "&eLeft click locations to inquire the position sequence.");
        Util.pinform(player, "&7<Right click to cancel inquire>");
    }

    public static void inquire_click(Player player, Block block) {
        if (block == null) {
            return;
        }
        Location location = Util.simplified_copy(block.getLocation());

        String stown = VA_postal.plistener_local_po;
        String saddress = VA_postal.plistener_address;
        int wypnt_count = C_Route.route_waypoint_count(stown, saddress);
        for (int i = 0; i < wypnt_count; i++) {
            String pos_loc = C_Route.get_waypoint_location(stown, saddress, i);
            if (Util.are_wpts_equal_2d(location, pos_loc, 2)) {
                selected_pos = i;
                Util.pinform(player, "&6That is waypoint sequence number: &f&l" + Util.int2str(selected_pos));
                return;
            }
        }
        Util.pinform(player, "&e&oYou must click on a highlighted waypoint.");
        Util.pinform(player, "&7&o<Right click to cancel inquire>");
    }

    public static void Exit_routeEditor(Player player) {
        restore_quickbar(player);
        VA_Timers.routeRditor_start(false, player);
        delete_hud();
        String stown = VA_postal.plistener_local_po;
        String saddress = VA_postal.plistener_address;
        C_Dispatcher.open_address(stown, saddress, true);
        clear_route_markers();
        Cmd_static.validate_route(player, stown, saddress);

        if ((VA_postal.using_towny()) && (!P_Towny.does_route_comply(player, stown, saddress))) {
            C_Route.delete_route(stown, saddress);
            Cmd_static.validate_route(player, stown, saddress);
            Util.pinform(player, "&e&oExiting the route editor.......");
            restore_quickbar(player);
            VA_postal.plistener_player = null;
            return;
        }
        String slocation = C_Route.get_last_waypoint_location(stown, saddress);
        Location las_wyypnt = Util.str2location(slocation);
        Location addr_loc = Util.str2location(C_Address.get_address_location(stown, saddress));
        if ((las_wyypnt != null) && (addr_loc.distance(las_wyypnt) < 50.0D)) {
            C_Address.save_postal_address_by_location(slocation, stown, saddress);
        } else {
            Util.pinform(player, "&6&oYou are not close enough to the defined, address.");
            Util.pinform(player, "&6&oPlease finish route with /setroute");
            Util.pinform(player, "&6&oOr delete " + df(stown) + ", " + df(saddress) + " and re-define.");
        }
        if ((player != null) &&
                (VA_Dispatcher.dispatcher_running)) {
            if (VA_postal.plistener_newroute) {
                C_Dispatcher.open_poffice(stown, true);

                BukkitListener.add_new_route_to_chunklist(stown, saddress);
            }

            if (C_Dispatcher.promote_schedule(stown, saddress, 9000, true)) {
                Util.pinform(player, "&7&oPushing schedule forward: " + df(stown) + ", " + df(saddress));
            } else {
                Util.pinform(player, "&7&oModified route: &r&o" + df(stown) + ", " + df(saddress));
            }
            Util.pinform(player, "&e&oExiting the route editor.......");
        }

        VA_postal.plistener_player = null;
    }

    public static void create_hud(String po, String addr) {
        if (VA_postal.plistener_using_scoreboard) {
            if (VA_postal.plistener_hud_board != null) {
                delete_hud();
            }
            String display_title = df(addr);
            VA_postal.plistener_hud_board = VA_postal.plistener_sb_manager.getNewScoreboard();
            VA_postal.plistener_hud_objective = VA_postal.plistener_hud_board.registerNewObjective("Dst_To_Poffce:", "dummy");
            VA_postal.plistener_hud_objective = VA_postal.plistener_hud_board.registerNewObjective("Dst_To_Addrss:", "dummy");
            VA_postal.plistener_hud_objective = VA_postal.plistener_hud_board.registerNewObjective("Elv_To_Addrss:", "dummy");
            VA_postal.plistener_hud_objective = VA_postal.plistener_hud_board.registerNewObjective("Dst_To_Lst_WP:", "dummy");
            VA_postal.plistener_hud_objective = VA_postal.plistener_hud_board.registerNewObjective("Total_WPoints:", "dummy");
            VA_postal.plistener_hud_objective.setDisplaySlot(org.bukkit.scoreboard.DisplaySlot.SIDEBAR);
            VA_postal.plistener_hud_objective.setDisplayName(display_title);
            VA_postal.plistener_hud_po = VA_postal.plistener_hud_objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + "Dst_To_Poffce:"));
            VA_postal.plistener_hud_addr = VA_postal.plistener_hud_objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + "Dst_To_Addrss:"));
            VA_postal.plistener_hud_addr_elev = VA_postal.plistener_hud_objective.getScore(Bukkit.getOfflinePlayer(ChatColor.BLUE + "Elv_To_Addrss:"));
            VA_postal.plistener_hud_lwp = VA_postal.plistener_hud_objective.getScore(Bukkit.getOfflinePlayer(ChatColor.YELLOW + "Dst_To_Lst_WP:"));
            VA_postal.plistener_hud_tot = VA_postal.plistener_hud_objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GOLD + "Total_WPoints:"));
            VA_postal.plistener_player.setScoreboard(VA_postal.plistener_hud_board);
            Location loc_po = Util.str2location(C_Postoffice.get_local_po_location_by_name(po));
            Location loc_addr = Util.str2location(C_Address.get_address_location(po, addr));
            hud_worker(po, addr, loc_po, loc_addr);
        }
    }

    public static void delete_hud() {
        if (VA_postal.plistener_using_scoreboard) {
            if (plistener_hud_worker != null) {
                plistener_hud_worker.cancel();
                plistener_hud_worker = null;
            }
            if (VA_postal.plistener_hud_board != null) {
                VA_postal.plistener_hud_po = null;
                VA_postal.plistener_hud_addr = null;
                VA_postal.plistener_hud_addr_elev = null;
                VA_postal.plistener_hud_lwp = null;
                VA_postal.plistener_hud_objective.unregister();
                VA_postal.plistener_hud_objective = null;
                VA_postal.plistener_hud_board.clearSlot(org.bukkit.scoreboard.DisplaySlot.SIDEBAR);
                VA_postal.plistener_hud_board = null;
            }
        }
    }

    public static void hud_worker(String po, final String addr, final Location loc_po, final Location loc_addr) {
        if (VA_postal.plistener_using_scoreboard) {
            if ((po == null) || (addr == null) || (loc_po == null) || (loc_addr == null)) {
                return;
            }
            plistener_hud_worker = VA_postal.plugin.getServer().getScheduler().runTaskTimer(VA_postal.plugin, new Runnable() {
                public void run() {
                    if ((VA_postal.plistener_player == null) || (!VA_postal.plistener_player.isOnline())) {
                        RouteEditor.delete_hud();
                        return;
                    }
                    Location playr_loc = VA_postal.plistener_player.getLocation();
                    Location lwpt_loc = Util.str2location(C_Route.get_last_waypoint_location(po, addr));
                    if ((playr_loc != null) && (loc_po != null)) {
                        int dist_po = (int) playr_loc.distance(loc_po);
                        VA_postal.plistener_hud_po.setScore(dist_po);
                    }
                    int dist_addr = (int) playr_loc.distance(loc_addr);
                    int elev_addr = (int) (-1.0D * (playr_loc.getY() - loc_addr.getY() - 1.0D));
                    VA_postal.plistener_hud_addr.setScore(dist_addr);
                    VA_postal.plistener_hud_addr_elev.setScore(elev_addr);
                    if (lwpt_loc != null) {
                        int dist_lwp = (int) playr_loc.distance(lwpt_loc);
                        VA_postal.plistener_hud_lwp.setScore(dist_lwp);
                    }
                    int tot_wp = C_Route.route_waypoint_count(po, addr);
                    VA_postal.plistener_hud_tot.setScore(tot_wp);
                }
            }, 10L, 10L);
        }
    }

    public static void restore_quickbar(Player player) {
        if ((player != null) && (player.isOnline())) {
            for (int i = 0; i < 9; i++) {
                player.getInventory().setItem(i, VA_postal.plistener_quickbar[i]);
            }
        }
    }

    public static synchronized void place_route_markers(String stown, String saddress) {
        if (route_blocks_type != null) {
            clear_route_markers();
        }
        route_blocks_type = new ArrayList();
        route_blocks_data = new ArrayList();
        route_blocks_loc = new ArrayList();
        wpnt_hilite_id = GetConfig.wpnt_hilite_id();
        String slocation = "";
        if (C_Route.is_route_defined(stown, saddress)) {
            int route_len = C_Route.route_waypoint_count(stown, saddress);
            for (int i = 0; i < route_len; i++) {
                if (C_Route.is_waypoint_defined(stown, saddress, i)) {
                    slocation = C_Route.get_waypoint_location(stown, saddress, i);
                    append_marker(slocation);
                }
            }
        }
    }

    public static synchronized void clear_route_markers() {
        if ((route_blocks_type != null) && (!route_blocks_type.isEmpty())) {
            World w = ((Location) route_blocks_loc.get(0)).getWorld();
            for (int i = 0; i < route_blocks_loc.size(); i++) {
                Block marker = w.getBlockAt((Location) route_blocks_loc.get(i));
                int type = ((Integer) route_blocks_type.get(i)).intValue();
                byte data = ((Byte) route_blocks_data.get(i)).byteValue();
                try {
                    marker.setTypeIdAndData(type, data, false);
                } catch (Exception e) {
                }
            }
            route_blocks_type.clear();
            route_blocks_type = null;
            route_blocks_data.clear();
            route_blocks_data = null;
            route_blocks_loc.clear();
            route_blocks_loc = null;
        }
    }

    public static synchronized boolean append_marker(String slocation) {
        Location location = Util.str2location(slocation);
        location.add(0.0D, 1.0D, 0.0D);
        Block block = Util.valid_waypnt_block(location);
        int count = 0;
        while ((block == null) && (count <= 5)) {
            location.subtract(0.0D, 1.0D, 0.0D);
            block = Util.valid_waypnt_block(location);
            count++;
        }
        if (block == null) {
            return false;
        }
        if (route_blocks_type == null) {
            route_blocks_type = new ArrayList();
            route_blocks_data = new ArrayList();
            route_blocks_loc = new ArrayList();
        }
        int type = block.getTypeId();
        byte data = block.getData();
        route_blocks_type.add(Integer.valueOf(type));
        route_blocks_data.add(Byte.valueOf(data));
        route_blocks_loc.add(location);
        try {
            block.setTypeId(wpnt_hilite_id);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static synchronized void remove_last_marker() {
        if (route_blocks_type != null) {
            int index = route_blocks_type.size() - 1;
            if (index >= 0) {
                World w = ((Location) route_blocks_loc.get(index)).getWorld();
                Block marker = w.getBlockAt((Location) route_blocks_loc.get(index));
                int type = ((Integer) route_blocks_type.get(index)).intValue();
                byte data = ((Byte) route_blocks_data.get(index)).byteValue();
                try {
                    marker.setTypeIdAndData(type, data, false);
                } catch (Exception e) {
                }
                route_blocks_loc.remove(index);
                route_blocks_type.remove(index);
                route_blocks_data.remove(index);
            }
        }
    }

    private static String location_2_XZ(Location loc) {
        String result = "null";
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

    public static String proper(String string) {
        try {
            if (string.length() > 0) {
                return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase().trim();
            }
        } catch (Exception e) {
        }

        return "";
    }

    public static String fixed_len(String input, int len) {
        try {
            input = input.trim();

            if (input.length() >= len) {
                return input.substring(0, len);
            }

            while (input.length() < len) {
                input = input + " ";
            }
            return input;
        } catch (Exception e) {
            String blank = "";
            for (int i = 0; i < len; i++) {
                blank = blank + " ";
            }
            return blank;
        }
    }
}
