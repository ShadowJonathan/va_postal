package com.vodhanel.minecraft.va_postal.listeners;

import com.vodhanel.minecraft.va_postal.VA_postal;
import com.vodhanel.minecraft.va_postal.commands.Cmdexecutor;
import com.vodhanel.minecraft.va_postal.common.AnsiColor;
import com.vodhanel.minecraft.va_postal.common.Util;
import com.vodhanel.minecraft.va_postal.common.VA_Dispatcher;
import com.vodhanel.minecraft.va_postal.config.C_Route;
import com.vodhanel.minecraft.va_postal.config.GetConfig;
import com.vodhanel.minecraft.va_postal.mail.BookManip;
import com.vodhanel.minecraft.va_postal.mail.ChestManip;
import com.vodhanel.minecraft.va_postal.mail.MailSecurity;
import com.vodhanel.minecraft.va_postal.mail.SignManip;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Chest;
import org.bukkit.material.Sign;

import java.util.ArrayList;
import java.util.List;

public class BukkitListener implements Listener {
    public static VA_postal plugin;
    public static String[] st_world;
    public static int[] st_xmin;
    public static int[] st_xmax;
    public static int[] st_zmin;
    public static int[] st_zmax;
    public static int st_count;
    public static List<Chunk> temp_chunk_list = null;


    public BukkitListener(VA_postal plugin) {
        BukkitListener.plugin = plugin;
    }

    private static boolean invalid_player(Entity entity, boolean check_live_player) {
        if (entity == null) {
            return true;
        }

        if ((VA_postal.plistener_player != null) && (entity == VA_postal.plistener_player)) {
            return true;
        }

        if (check_live_player) {
            if ((entity instanceof Player)) {
                Player player = (Player) entity;
                if (!player.isOnline()) {
                    return true;
                }
            } else {
                return true;
            }
        }
        return false;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public static void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
        if ((event.isCancelled()) || (invalid_player(event.getPlayer(), false))) {
            return;
        }


        String entered_cmd;
        String root;
        if (event.getMessage() != null) {
            entered_cmd = event.getMessage().toLowerCase().trim();
            String[] parts = entered_cmd.split(" ");
            if (parts.length > 0) {
                root = parts[0].trim();
            } else {
                return;
            }
        } else {
            return;
        }


        Player player = event.getPlayer();


        if (Cmdexecutor.is_player_comfirmation_registered(player)) {
            if ("/".equals(entered_cmd)) {
                String reg_cmd = Cmdexecutor.get_registered_comfirmation_cmd(player);
                if ("null".equals(reg_cmd)) {
                    Util.pinform(player, "&c&oCMD confirmation failure - could not find registered command.");

                    Cmdexecutor.deregister_player_comfirmation(player);
                    event.setCancelled(true);
                    return;
                }
                event.setMessage(reg_cmd);


                return;
            }


            Cmdexecutor.deregister_player_comfirmation(player);
            Util.pinform(player, "&e&oPrevious command canceled.");
            return;
        }

        if ("/go".equalsIgnoreCase(root)) {
            String re_route = entered_cmd.replace(root, "/va_go");
            event.setMessage(entered_cmd);
            return;
        }
        if ("/gps".equalsIgnoreCase(root)) {
            String re_route = entered_cmd.replace(root, "/gpsp");
            event.setMessage(re_route);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public static void onServerCommandEvent(ServerCommandEvent event) {
        String entered_cmd = event.getCommand().toLowerCase().trim();

        if (Cmdexecutor.is_player_comfirmation_registered(null)) {
            if ("/".equals(entered_cmd)) {
                String reg_cmd = Cmdexecutor.get_registered_comfirmation_cmd(null);
                if ("null".equals(reg_cmd)) {
                    Util.cinform(AnsiColor.RED + "CMD confirmation failure - could not find registered command.");

                    Cmdexecutor.deregister_player_comfirmation(null);
                    event.setCommand("");
                    return;
                }
                event.setCommand(reg_cmd);


                return;
            }


            Cmdexecutor.deregister_player_comfirmation(null);
            Util.cinform("\033[0;33mPrevious command canceled.");
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public static void onPlayerInteract(PlayerInteractEvent event) {
        if (VA_postal.admin_overide) {
            return;
        }

        if (invalid_player(event.getPlayer(), true)) {
            return;
        }

        Player player = event.getPlayer();

        if ((event.getClickedBlock() == null) || (event.getAction() == null)) {
            return;
        }

        Action action = event.getAction();


        BlockState state = event.getClickedBlock().getState();
        if ((!(state instanceof Chest)) && (!(state instanceof Sign))) {
            return;
        }

        if ((state instanceof Sign)) {
            if (SignManip.is_this_a_postal_sign(event.getClickedBlock(), 2)) {
                if (!event.isCancelled()) {
                    event.setCancelled(true);
                }

                if ((action == Action.LEFT_CLICK_BLOCK) && (MailSecurity.allowed_to_break_shipment(event.getClickedBlock(), player))) {
                    event.getClickedBlock().setType(Material.AIR);
                    Block block = SignManip.sign2chest_block(event.getClickedBlock());
                    block.setType(Material.AIR);
                    player.setItemOnCursor(null);
                }
            }
        } else {
            Block sign_block = ChestManip.chest2sign_block(event.getClickedBlock());
            if ((sign_block != null) && (SignManip.is_this_a_postal_sign(sign_block, 2))) {
                if (!event.isCancelled()) {
                    event.setCancelled(true);
                }

                if ((action == Action.LEFT_CLICK_BLOCK) && (MailSecurity.allowed_to_break_shipment(sign_block, player))) {
                    event.getClickedBlock().setType(Material.AIR);
                    sign_block.setType(Material.AIR);
                    player.setItemOnCursor(null);
                }
                return;
            }
        }


        Block sign_block;
        Block block;

        if ((state instanceof Chest)) {
            block = ChestManip.block2postal_block(event.getClickedBlock());
            if (block == null) {
                return;
            }
            sign_block = ChestManip.chest2sign_block(block);
            if (sign_block == null) {
                return;
            }
        } else {
            block = SignManip.sign2chest_block(event.getClickedBlock());
            if (block == null) {
                return;
            }
            sign_block = event.getClickedBlock();
            if (sign_block == null) {
                return;
            }
        }


        if (!ChestManip.is_this_a_postal_chest(block)) {
            return;
        }


        if (!event.isCancelled()) {
            event.setCancelled(true);
        }


        if ((action == Action.LEFT_CLICK_BLOCK) &&
                (MailSecurity.is_authorized_to_break_chest_event(block, player))) {
            sign_block.setType(Material.AIR);
            block.setType(Material.AIR);
            return;
        }


        if (MailSecurity.qualified_mailbox_open(player, block)) {
            MailSecurity.remove_new_mail_marker_event(player, block);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public static void onInventoryClose(InventoryCloseEvent event) {
        if (VA_postal.admin_overide) {
            return;
        }

        if (invalid_player(event.getPlayer(), true)) {
            return;
        }

        if (event.getInventory() == null) {
            return;
        }

        if (InventoryType.CHEST == event.getInventory().getType()) {
            MailSecurity.event_check_chest_for_new_mail(event.getInventory());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public static void onInventoryClick(InventoryClickEvent event) {
        if (VA_postal.admin_overide) {
            return;
        }

        if ((event.isCancelled()) || (invalid_player(event.getWhoClicked(), true))) {
            return;
        }

        Player player = (Player) event.getWhoClicked();


        if ((event.getInventory() != null) && (!BookManip.is_there_a_postal_log(event.getInventory()))) {
            return;
        }


        Inventory inventory = event.getInventory();
        ItemStack stack = event.getCurrentItem();
        int slot = event.getRawSlot();
        if ((inventory != null) && (stack != null) && (slot >= 0) &&
                ("CONTAINER".equals(event.getSlotType().name())) &&
                (stack.getType() == Material.WRITTEN_BOOK)) {
            if (!MailSecurity.may_player_access_this_mail(inventory, stack, slot, player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public static void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String msg1 = GetConfig.join_message();
        String splayer = proper(player.getName());
        String msg = msg1.replace("%player%", splayer);

        if ((msg != null) && (!"null".equals(msg)) && (!msg.trim().isEmpty())) {
            Util.pinform(player, msg);
        }
        Util.list_newmail(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public static void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if ((VA_postal.plistener_player != null) &&
                (VA_postal.plistener_player == player)) {
            Util.cinform("[Postal] Ending route editor session for " + player.getName() + ", quit while editing.");
            RouteEditor.Exit_routeEditor(player);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public static void onCreatureSpawnEvent(CreatureSpawnEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Entity spawned = event.getEntity();
        if (((spawned instanceof Monster)) &&
                (!GetConfig.allow_monster_spawn())) {
            Chunk chunk = event.getLocation().getChunk();
            if (is_chunk_on_route(chunk)) {
                event.setCancelled(true);
                return;
            }
            if ((temp_chunk_list != null) &&
                    (temp_chunk_list.contains(chunk))) {
                event.setCancelled(true);
            }
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public static void onChunkUnload(ChunkUnloadEvent event) {
        if ((!VA_Dispatcher.dispatcher_running) || (event.isCancelled()) || (VA_postal.needs_configuration)) {
            return;
        }
        Chunk chunk = event.getChunk();
        if (is_chunk_on_route(chunk)) {
            event.setCancelled(true);
            return;
        }
        if ((temp_chunk_list != null) &&
                (temp_chunk_list.contains(chunk))) {
            event.setCancelled(true);
        }
    }

    public static boolean is_chunk_on_route(Chunk chunk) {
        String sworld = chunk.getWorld().getName();
        int X = chunk.getX();
        int Z = chunk.getZ();
        String[] parts = new String[5];
        int c_Xmin;
        int c_Zmin;
        int c_Xmax;
        int c_Zmax;
        for (int i = 0; i < st_count; i++) {
            if (st_world[i].equals(sworld)) {
                c_Xmin = st_xmin[i];
                c_Xmax = st_xmax[i];
                if ((X >= c_Xmin) && (X <= c_Xmax)) {
                    c_Zmin = st_zmin[i];
                    c_Zmax = st_zmax[i];
                    if ((Z >= c_Zmin) && (Z <= c_Zmax)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void init_static_array(int len) {
        st_world = new String[len];
        st_xmin = new int[len];
        st_xmax = new int[len];
        st_zmin = new int[len];
        st_zmax = new int[len];
        st_count = len;
    }

    public static synchronized void append_temp_chunk_list(Chunk chunk) {
        if (chunk == null) {
            return;
        }
        if (temp_chunk_list == null) {
            temp_chunk_list = new ArrayList<Chunk>();
        }
        if (!temp_chunk_list.contains(chunk)) {
            temp_chunk_list.add(chunk);
        }
    }

    public static synchronized void append_temp_chunk_list(String slocation) {
        if (slocation == null) {
            return;
        }
        Location location = Util.str2location(slocation);
        Chunk chunk = location.getChunk();
        append_temp_chunk_list(chunk);
    }

    public static synchronized void add_new_route_to_chunklist(String stown, String saddress) {
        if ((stown == null) || (saddress == null)) {
            return;
        }
        String slocation;
        if (C_Route.is_route_defined(stown, saddress)) {
            int route_len = C_Route.route_waypoint_count(stown, saddress);
            for (int i = 0; i < route_len; i++) {
                if (C_Route.is_waypoint_defined(stown, saddress, i)) {
                    slocation = C_Route.get_waypoint_location(stown, saddress, i);
                    append_temp_chunk_list(slocation);
                }
            }
        }
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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPluginDisableEvent(PluginDisableEvent event) {
    }
}
