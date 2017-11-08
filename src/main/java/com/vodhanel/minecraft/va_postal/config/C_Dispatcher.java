package com.vodhanel.minecraft.va_postal.config;

import com.vodhanel.minecraft.va_postal.VA_postal;
import com.vodhanel.minecraft.va_postal.common.AnsiColor;
import com.vodhanel.minecraft.va_postal.common.Util;
import com.vodhanel.minecraft.va_postal.common.VA_Dispatcher;
import com.vodhanel.minecraft.va_postal.listeners.BukkitListener;
import com.vodhanel.minecraft.va_postal.mail.ID_Mail;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

public class C_Dispatcher {
    VA_postal plugin;

    public C_Dispatcher(VA_postal instance) {
        plugin = instance;
    }

    public static synchronized boolean promote_central(String town, int seconds) {
        if ((!VA_Dispatcher.dispatcher_running) || (VA_postal.central_array_name == null)) {
            return false;
        }

        int index = -1;
        town = town.toLowerCase().trim();
        String test_town = "";
        for (int i = 0; i < VA_postal.central_array_name.length; i++) {
            if (VA_postal.central_array_name[i] != null) {
                test_town = VA_postal.central_array_name[i].toLowerCase().trim();
            } else {
                return false;
            }
            if (town.equals(test_town)) {
                index = i;
                break;
            }
        }
        if (index >= 0) {
            if (VA_postal.central_array_promoted[index]) {
                Util.dinform("Already promoted - CENTRAL " + test_town);
                return true;
            }

            VA_postal.central_array_promoted[index] = true;

            VA_postal.central_array_time[index] -= seconds;
            Util.dinform("Schedule promotion - CENTRAL " + test_town + ", " + VA_postal.central_array_time[index]);
            return true;
        }
        return false;
    }

    public static synchronized boolean demote_central(String town, int seconds) {
        if ((!VA_Dispatcher.dispatcher_running) || (VA_postal.central_array_name == null)) {
            return false;
        }

        int index = -1;
        town = town.toLowerCase().trim();
        for (int i = 0; i < VA_postal.central_array_name.length; i++) {
            String tname;
            if (VA_postal.central_array_name[i] != null) {
                tname = VA_postal.central_array_name[i].toLowerCase().trim();
            } else
                return false;
            if (town.equals(tname)) {
                index = i;
                break;
            }
        }
        if (index >= 0) {
            if (VA_postal.central_array_promoted[index]) {
                return true;
            }
            VA_postal.central_array_time[index] += seconds;
            return true;
        }
        return false;
    }

    public static synchronized boolean promote_schedule(String town, String address, int seconds, boolean force_open) {
        if (!VA_Dispatcher.dispatcher_running) {
            return false;
        }
        String qpair = C_Queue.get_queue_pair(town, address);
        if ("null".equals(qpair)) {
            return false;
        }
        String[] parts = new String[2];
        parts = qpair.split(",");
        String q_idx = null;
        String t_idx = null;
        try {
            q_idx = parts[0].trim();
            t_idx = parts[1].trim();
        } catch (Exception e) {
            return false;
        }
        if (C_Queue.is_task_promoted(q_idx + "," + t_idx)) {
            Util.dinform("Already promoted - " + town + ", " + address);
            return true;
        }
        if ((force_open) &&
                (!is_address_open(town, address))) {
            open_address(town, address, true);
        }

        int promoted_time = Util.time_stamp();
        promoted_time -= seconds;
        String spromoted_time = "";
        try {
            spromoted_time = Integer.toString(promoted_time);
        } catch (Exception e) {
            return false;
        }

        try {
            String spath = GetConfig.path_format("dispatcher.queue." + q_idx + ".task." + t_idx + ".time");
            VA_postal.plugin.getConfig().set(spath, spromoted_time);

            spath = GetConfig.path_format("dispatcher.queue." + q_idx + ".time");
            VA_postal.plugin.getConfig().set(spath, spromoted_time);

            set_promoted_flag(q_idx + "," + t_idx);
            Util.dinform("Schedule promotion - " + town + ", " + address + ", " + q_idx + ", " + t_idx + ", " + spromoted_time);
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    public static synchronized boolean demote_schedule(String town, String address, int seconds) {
        if (!VA_Dispatcher.dispatcher_running) {
            return false;
        }
        String qpair = C_Queue.get_queue_pair(town, address);
        if ("null".equals(qpair)) {
            return false;
        }
        String[] parts = new String[2];
        parts = qpair.split(",");
        String q_idx = null;
        String t_idx = null;
        try {
            q_idx = parts[0].trim();
            t_idx = parts[1].trim();
        } catch (Exception e) {
            return false;
        }
        if (C_Queue.is_task_demoted(q_idx + "," + t_idx)) {
            return true;
        }
        int demoted_time = Util.time_stamp();
        demoted_time += seconds;
        String sdemoted_time = "";
        try {
            sdemoted_time = Integer.toString(demoted_time);
        } catch (Exception e) {
            return false;
        }

        if (C_Queue.is_task_active(qpair)) {
            sdemoted_time = "*" + sdemoted_time;
        }
        try {
            String spath = GetConfig.path_format("dispatcher.queue." + q_idx + ".task." + t_idx + ".time");
            VA_postal.plugin.getConfig().set(spath, sdemoted_time);

            spath = GetConfig.path_format("dispatcher.queue." + q_idx + ".time");
            VA_postal.plugin.getConfig().set(spath, sdemoted_time);

            set_demoted_flag(q_idx + "," + t_idx);
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    public static synchronized boolean is_address_open(String stown, String saddress) {
        stown = stown.toLowerCase().trim();
        saddress = saddress.toLowerCase().trim();
        if (("null".equals(stown)) || ("null".equals(saddress))) {
            return false;
        }
        String sresult = "false";
        boolean result = false;
        try {
            String spath = GetConfig.path_format("address." + stown + "." + saddress + ".open");
            sresult = VA_postal.plugin.getConfig().getString(spath);
            if (sresult == null) {
                result = false;
            }
            if ("true".equals(sresult)) {
                result = true;
            }
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    public static synchronized boolean is_address_on_firstpass(String town, String address) {
        try {
            String qpair = C_Queue.get_queue_pair(town, address);
            return (!"null".equals(qpair)) && (C_Queue.is_task_firstpass(qpair));
        } catch (Exception e) {
        }
        return false;
    }

    public static synchronized boolean is_po_open_defined(String stown) {
        stown = stown.toLowerCase().trim();
        try {
            String spath = GetConfig.path_format("postoffice.local." + stown + ".open");
            String result = VA_postal.plugin.getConfig().getString(spath);
            if ((result.equalsIgnoreCase("true")) || (result.equalsIgnoreCase("false"))) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public static synchronized boolean is_addr_open_defined(String town, String address) {
        town = town.toLowerCase().trim();
        address = address.toLowerCase().trim();
        try {
            String spath = GetConfig.path_format("address." + town + "." + address + ".open");
            String result = VA_postal.plugin.getConfig().getString(spath);
            if ((result.equalsIgnoreCase("true")) || (result.equalsIgnoreCase("false"))) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public static synchronized boolean is_poffice_open(String stown) {
        if ("null".equals(stown)) {
            return false;
        }
        stown = stown.toLowerCase().trim();
        String sresult = "false";
        boolean result = false;
        try {
            String spath = GetConfig.path_format("postoffice.local." + stown + ".open");
            sresult = VA_postal.plugin.getConfig().getString(spath);
            if (sresult == null) {
                result = false;
            }
            if ("true".equals(sresult)) {
                result = true;
            }
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    public static synchronized void open_address(String town, String address, boolean open) {
        if (is_address_open(town, address) == open) {
            return;
        }
        town = town.toLowerCase().trim();
        address = address.toLowerCase().trim();
        String status = "true";
        if (!open) {
            status = "false";
        }
        try {
            if (C_Address.is_address_defined(town, address)) {
                String spath = GetConfig.path_format("address." + town + "." + address + ".open");
                VA_postal.plugin.getConfig().set(spath, status);
            } else {
                return;
            }
        } catch (Exception e) {
            return;
        }
        String pair = C_Queue.get_queue_pair(town, address);
        if ("null".equals(pair)) {
            return;
        }
        try {
            String[] parts = pair.split(",");
            if ((parts != null) && (parts.length >= 2)) {
                String q_idx = GetConfig.proper(parts[0]).trim();
                String t_idx = GetConfig.proper(parts[1].trim());

                if (C_Queue.is_task_defined(q_idx, t_idx)) {
                    String spath = GetConfig.path_format("dispatcher.queue." + q_idx + ".task." + t_idx + ".open");
                    VA_postal.plugin.getConfig().set(spath, status);
                }
            }
        } catch (Exception e) {
        }
        VA_postal.plugin.saveConfig();
    }

    public static synchronized void set_firstpass_done(String town, String address) {
        String status = "false";
        String q_idx = "";
        String t_idx = "";
        try {
            String pair = C_Queue.get_queue_pair(town, address);
            if ("null".equals(pair)) {
                return;
            }
            String[] parts = pair.split(",");
            q_idx = GetConfig.proper(parts[0]).trim();
            t_idx = GetConfig.proper(parts[1].trim());

            if (C_Queue.is_task_defined(q_idx, t_idx)) {
                String spath = GetConfig.path_format("dispatcher.queue." + q_idx + ".task." + t_idx + ".firstpass");
                VA_postal.plugin.getConfig().set(spath, status);
                VA_postal.plugin.saveConfig();
            }
        } catch (Exception e) {
            Util.cinform(AnsiColor.RED + "Problem setting firstpass status");
        }
    }

    public static synchronized void open_poffice(String stown, boolean open) {
        if (is_poffice_open(stown) == open) {
            return;
        }
        stown = stown.toLowerCase().trim();
        String status = "true";
        if (!open) {
            status = "false";
        }
        try {
            if (C_Postoffice.is_local_po_name_defined(stown)) {
                String spath = GetConfig.path_format("postoffice.local." + stown + ".open");
                VA_postal.plugin.getConfig().set(spath, status);
            } else {
                return;
            }
        } catch (Exception e) {
            return;
        }

        try {
            String queue = C_Queue.get_queue(stown);
            if (C_Queue.is_queue_defined(queue)) {
                String spath = GetConfig.path_format("dispatcher.queue." + queue + ".open");
                VA_postal.plugin.getConfig().set(spath, status);
            }
        } catch (Exception e) {
        }
        VA_postal.plugin.saveConfig();
    }

    public static synchronized void reset_pro_de_motion(String pair) {
        String q_idx = "";
        String t_idx = "";
        try {
            String[] parts = pair.split(",");
            q_idx = GetConfig.proper(parts[0]).trim();
            t_idx = GetConfig.proper(parts[1].trim());

            if (C_Queue.is_task_defined(q_idx, t_idx)) {
                String spath = GetConfig.path_format("dispatcher.queue." + q_idx + ".task." + t_idx + ".promoted");
                VA_postal.plugin.getConfig().set(spath, "false");
                spath = GetConfig.path_format("dispatcher.queue." + q_idx + ".task." + t_idx + ".demoted");
                VA_postal.plugin.getConfig().set(spath, "false");
                VA_postal.plugin.saveConfig();
            }
        } catch (Exception e) {
            Util.cinform(AnsiColor.RED + "Problem resetting promotion status");
        }
    }

    public static synchronized void set_promoted_flag(String pair) {
        String q_idx = "";
        String t_idx = "";
        try {
            String[] parts = pair.split(",");
            q_idx = GetConfig.proper(parts[0]).trim();
            t_idx = GetConfig.proper(parts[1].trim());

            if (C_Queue.is_task_defined(q_idx, t_idx)) {
                String spath = GetConfig.path_format("dispatcher.queue." + q_idx + ".task." + t_idx + ".promoted");
                VA_postal.plugin.getConfig().set(spath, "true");
                VA_postal.plugin.saveConfig();
            }
        } catch (Exception e) {
            Util.cinform(AnsiColor.RED + "Problem setting promotion status");
        }
    }

    public static synchronized void set_demoted_flag(String pair) {
        String q_idx = "";
        String t_idx = "";
        try {
            String[] parts = pair.split(",");
            q_idx = GetConfig.proper(parts[0]).trim();
            t_idx = GetConfig.proper(parts[1].trim());

            if (C_Queue.is_task_defined(q_idx, t_idx)) {
                String spath = GetConfig.path_format("dispatcher.queue." + q_idx + ".task." + t_idx + ".demoted");
                VA_postal.plugin.getConfig().set(spath, "true");
                VA_postal.plugin.saveConfig();
            }
        } catch (Exception e) {
            Util.cinform(AnsiColor.RED + "Problem setting promotion status");
        }
    }

    public static synchronized boolean central_time_stamp(String stown) {
        String q_idx = C_Queue.get_queue(stown);
        String spath = "";
        String spost_time = "";
        String slast_count = "";
        String slast_time = "";
        long time_sec = System.currentTimeMillis() / 1000L;
        try {
            spost_time = Long.toString(time_sec);
        } catch (Exception e) {
            spost_time = "-1";
        }

        try {
            spath = GetConfig.path_format("dispatcher.queue." + q_idx + ".cen_count");
            slast_count = VA_postal.plugin.getConfig().getString(spath);
            spath = GetConfig.path_format("dispatcher.queue." + q_idx + ".cen_time");
            slast_time = VA_postal.plugin.getConfig().getString(spath);
            VA_postal.plugin.getConfig().set(spath, spost_time);
        } catch (Exception e) {
            return false;
        }


        if ("null".equals(slast_count)) {
            slast_count = "0";
        }


        String sinterval = "";
        String scount = "";
        try {
            int last_count = Integer.parseInt(slast_count);
            long last_time = Long.parseLong(slast_time);
            long interval = time_sec - last_time;
            scount = Integer.toString(last_count + 1);
            sinterval = Long.toString(interval);
            spath = GetConfig.path_format("dispatcher.queue." + q_idx + ".cen_interval");
            VA_postal.plugin.getConfig().set(spath, sinterval);
            spath = GetConfig.path_format("dispatcher.queue." + q_idx + ".cen_count");
            VA_postal.plugin.getConfig().set(spath, scount);
        } catch (NumberFormatException numberFormatException) {
            return false;
        }
        try {
            VA_postal.plugin.saveConfig();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static synchronized String slocation2schunk(String slocation) {
        String result = "null";
        if (!"null".equals(slocation)) {
            Location location = Util.str2location(slocation);
            if (location != null) {
                try {
                    Chunk chunk = location.getChunk();
                    String sworld = chunk.getWorld().getName();
                    String X = Integer.toString(chunk.getX()).trim();
                    String Z = Integer.toString(chunk.getZ()).trim();
                    result = sworld + "," + X + "," + Z;
                } catch (Exception e) {
                    return "null";
                }
            }
        }
        return result;
    }

    public static synchronized void reality_check_n_chunk_list() {
        int chunk_overlap = GetConfig.chunk_overlap();

        String result = "";


        int total = 0;

        String spoffice = "";
        String saddress = "";
        String swpoint = "";


        VA_postal.needs_configuration = false;
        if (!C_Postoffice.is_central_po_defined()) {
            VA_postal.needs_configuration = true;
            Util.binform("&c&oVA_postal cannot find the central post office.");
            Util.binform("  &7&oFind a desired location and place a chest.");
            Util.binform("  &7&oThen enter &f&r/setcentral");
            return;
        }

        String path = GetConfig.path_format("address");
        ConfigurationSection po_addr = VA_postal.configsettings.getConfigurationSection(path);
        Set<String> po_addr_keys = po_addr.getKeys(false);
        Iterator<String> itr_poffice = po_addr_keys.iterator();
        if (!itr_poffice.hasNext()) {
            VA_postal.needs_configuration = true;
            Util.binform("&c&oVA_postal cannot find a local post office to service.");
            Util.binform("  &7&oFind a desired location and place a chest.");
            Util.binform("  &7&oThen enter &f&r/setlocal <TownName>");
            Util.binform("  &7&oA post office also needs at least one address to service.");
            Util.binform("  &7&oSee commands &f&r/setlocal &7&oand &f&r/setroute &7&oto create an address.");
            return;
        }
        int addr_count = 0;
        int rt_count = 0;
        while (itr_poffice.hasNext()) {
            spoffice = itr_poffice.next().trim();

            path = GetConfig.path_format("address." + spoffice);
            ConfigurationSection plr = VA_postal.configsettings.getConfigurationSection(path);

            if (plr == null) {
                rc_open_poffice(spoffice, false);
                Util.binform("&c&oVA_postal cannot find an address at post office: &f&r" + spoffice);
                if (addr_count == 0) {
                    Util.binform("  &7&oFind a desired location and place a chest.");
                    Util.binform("  &7&oThen enter &f&r/setaddr " + spoffice + " <Address>");
                    Util.binform("  &7&oAlternatively, you may delete the post along with any addresses attached.");
                    Util.binform("  &7&oTo delete, enter &f&r/deletelocal " + spoffice);
                }
            } else if (!is_po_open_defined(spoffice)) {
                rc_open_poffice(spoffice, true);
            }


            if (plr != null) {
                Set<String> plr_keys = plr.getKeys(false);
                Iterator<String> itr_plr = plr_keys.iterator();


                while (itr_plr.hasNext()) {
                    saddress = itr_plr.next().trim();
                    path = GetConfig.path_format("address." + spoffice + "." + saddress + ".route");
                    ConfigurationSection plr_route = VA_postal.configsettings.getConfigurationSection(path);

                    if (plr_route == null) {
                        rc_open_address(spoffice, saddress, false);
                        Util.binform("&c&oVA_postal cannot find a route at: &f&r" + spoffice + ", " + saddress);
                        if (rt_count == 0) {
                            Util.binform("  &7&oEnter &f&r/gotoaddr " + spoffice + " " + saddress);
                            Util.binform("  &7&oThen enter &f&r/setroute " + spoffice + " " + saddress);
                            Util.binform("  &7&oNow click waypoints to the post office.");
                            Util.binform("  &7&oAlternatively, you may delete this address.");
                            Util.binform("  &7&oTo delete, enter &f&r/deleteaddr " + spoffice + " " + saddress);
                        }
                    } else {
                        if (!is_addr_open_defined(spoffice, saddress)) {
                            rc_open_address(spoffice, saddress, true);
                        }
                        rt_count++;
                    }
                    if (plr_route != null) {
                        Set<String> plr_route_keys = plr_route.getKeys(false);
                        Iterator<String> itr_plr_route = plr_route_keys.iterator();

                        total += plr_route_keys.size();
                    }
                    addr_count++;
                }
            }
        }

        VA_postal.plugin.saveConfig();


        if ((addr_count == 0) || (rt_count == 0)) {
            VA_postal.needs_configuration = true;
            return;
        }


        String[] chunk_list = new String[total];

        int count = 0;

        itr_poffice = po_addr_keys.iterator();
        while (itr_poffice.hasNext()) {
            spoffice = itr_poffice.next().trim();

            path = GetConfig.path_format("address." + spoffice);
            ConfigurationSection plr = VA_postal.configsettings.getConfigurationSection(path);

            if (plr != null) {
                Set<String> plr_keys = plr.getKeys(false);
                Iterator<String> itr_plr = plr_keys.iterator();


                while (itr_plr.hasNext()) {
                    saddress = itr_plr.next().trim();
                    path = GetConfig.path_format("address." + spoffice + "." + saddress + ".route");
                    ConfigurationSection plr_route = VA_postal.configsettings.getConfigurationSection(path);

                    if (plr_route != null) {
                        Set<String> plr_route_keys = plr_route.getKeys(false);
                        Iterator<String> itr_plr_route = plr_route_keys.iterator();


                        while (itr_plr_route.hasNext()) {
                            swpoint = itr_plr_route.next();
                            path = GetConfig.path_format("address." + spoffice + "." + saddress + ".route." + swpoint + ".location");
                            result = VA_postal.plugin.getConfig().getString(path);
                            chunk_list[count] = (spoffice + "," + saddress + "," + slocation2schunk(result));
                            count++;
                        }
                    }
                }
            }
        }
        Arrays.sort(chunk_list);
        String this_route = "";
        String last_route = "";
        String sindex = "";
        String[] parts = new String[5];
        int list_count = 0;
        parts = chunk_list[0].split(",");
        String s_x_min = "";
        String s_x_max = "";
        String s_z_min = "";
        String s_z_max = "";
        String sworld = "";
        last_route = parts[0] + parts[1] + parts[2];
        int x_min = 0;
        int z_min = 0;
        int x_c = 0;
        int x_max = 0;
        int z_max = 0;
        int z_c = 0;
        int i = 0;
        BukkitListener.init_static_array(chunk_list.length);
        while (i < chunk_list.length) {
            parts = chunk_list[i].split(",");
            this_route = parts[0] + parts[1] + parts[2];
            sworld = parts[2].trim();
            x_min = Integer.parseInt(parts[3].trim());
            x_max = Integer.parseInt(parts[3].trim());
            z_min = Integer.parseInt(parts[4].trim());
            z_max = Integer.parseInt(parts[4].trim());
            while ((i < chunk_list.length) && (this_route.equals(last_route))) {
                x_c = Integer.parseInt(parts[3].trim());
                z_c = Integer.parseInt(parts[4].trim());
                if (x_c < x_min) {
                    x_min = x_c;
                }
                if (x_c > x_max) {
                    x_max = x_c;
                }
                if (z_c < z_min) {
                    z_min = z_c;
                }
                if (z_c > z_max) {
                    z_max = z_c;
                }
                parts = chunk_list[i].split(",");
                last_route = parts[0] + parts[1] + parts[2];
                i++;
            }
            BukkitListener.st_world[list_count] = sworld;
            BukkitListener.st_xmin[list_count] = (x_min - chunk_overlap);
            BukkitListener.st_xmax[list_count] = (x_max + chunk_overlap);
            BukkitListener.st_zmin[list_count] = (z_min - chunk_overlap);
            BukkitListener.st_zmax[list_count] = (z_max + chunk_overlap);
            list_count++;
            i++;
        }

        if (C_Postoffice.is_central_po_defined()) {
            Location central = Util.str2location(C_Postoffice.get_central_po_location());
            sworld = central.getWorld().getName();
            BukkitListener.st_world[list_count] = sworld;
            BukkitListener.st_xmin[list_count] = (central.getChunk().getX() - chunk_overlap);
            BukkitListener.st_xmax[list_count] = (central.getChunk().getX() + chunk_overlap);
            BukkitListener.st_zmin[list_count] = (central.getChunk().getZ() - chunk_overlap);
            BukkitListener.st_zmax[list_count] = (central.getChunk().getZ() + chunk_overlap);
            BukkitListener.st_count = list_count + 1;
        }
    }

    public static synchronized void rc_open_poffice(String stown, boolean open) {
        if (is_poffice_open(stown) == open) {
            return;
        }
        stown = stown.toLowerCase().trim();
        String status = "true";
        if (!open) {
            status = "false";
        }
        try {
            if (C_Postoffice.is_local_po_name_defined(stown)) {
                String spath = GetConfig.path_format("postoffice.local." + stown + ".open");
                VA_postal.plugin.getConfig().set(spath, status);
            }
        } catch (Exception e) {
        }
    }

    public static synchronized void rc_open_address(String town, String address, boolean open) {
        if (is_address_open(town, address) == open) {
            return;
        }
        town = town.toLowerCase().trim();
        address = address.toLowerCase().trim();
        String status = "true";
        if (!open) {
            status = "false";
        }
        try {
            if (C_Address.is_address_defined(town, address)) {
                String spath = GetConfig.path_format("address." + town + "." + address + ".open");
                VA_postal.plugin.getConfig().set(spath, status);
            }
        } catch (Exception e) {
        }
    }

    public static synchronized void load_static_regions() {
        String sworld = "";
        int xmin = -1;
        int xmax = -1;
        int zmin = -1;
        int zmax = -1;
        int len = BukkitListener.st_count;
        for (int i = 0; i < len; i++) {
            sworld = BukkitListener.st_world[i];
            xmin = BukkitListener.st_xmin[i];
            xmax = BukkitListener.st_xmax[i];
            zmin = BukkitListener.st_zmin[i];
            zmax = BukkitListener.st_zmax[i];
            npc_load_chunk_region(sworld, xmin, xmax, zmin, zmax);
        }
    }

    public static synchronized void npc_load_chunk_region(String sworld, int min_x, int max_x, int min_z, int max_z) {
        World world = Util.str2world(sworld);
        VA_postal.chunks_loaded = 0;
        VA_postal.chunks_requested = 0;
        for (int x = min_x; x <= max_x; x++) {
            for (int z = min_z; z <= max_z; z++) {
                if (world.getChunkAt(x, z).load()) {
                    VA_postal.chunks_loaded += 1;
                }
                VA_postal.chunks_requested += 1;
            }
        }
        Util.dinform("\033[1;34mChunk range: " + sworld + ", minX=" + min_x + " maxX=" + max_x + " minZ=" + min_z + " maxZ=" + max_z);
        Util.dinform("\033[0;32mChunk loads requested: " + VA_postal.chunks_requested);
        Util.dinform("\033[1;34mChunk loads completed: " + VA_postal.chunks_loaded);
    }

    public static synchronized void load_central_route_array(String[] town_list) {
        String result = "";

        if ((town_list != null) && (town_list.length > 0)) {
            long ltime = -1L;
            VA_postal.central_route_count = town_list.length;
            for (int i = 0; i < town_list.length; i++) {
                VA_postal.central_array_name[i] = GetConfig.proper(town_list[i]);
                String spath = GetConfig.path_format("postoffice.local." + town_list[i].toLowerCase().trim() + ".location");
                result = VA_postal.plugin.getConfig().getString(spath);
                VA_postal.central_array_location[i] = result;
                VA_postal.central_array_time[i] = ltime;
                VA_postal.central_array_promoted[i] = false;
            }
        }
    }

    public static synchronized void create_dispatcher() {
        String dpath = GetConfig.proper("dispatcher");
        VA_postal.plugin.getConfig().set(dpath, null);
        VA_postal.plugin.saveConfig();
        String patha = "";
        String pathp = "";
        String spostoffice = "";
        String saddress = "";
        String spo_index = "";
        String spl_index = "";
        String sdefault_time = Util.stime_stamp();
        String promoted_time = Util.s_adj_time_stamp(60536);
        String selected_time = "";

        String path = GetConfig.path_format("address");
        ConfigurationSection po = VA_postal.configsettings.getConfigurationSection(path);
        if (po != null) {
            Set<String> po_keys = po.getKeys(false);
            Iterator<String> po_iterator = po_keys.iterator();
            int pl_index = 0;
            int po_index = 0;
            while (po_iterator.hasNext()) {
                spostoffice = po_iterator.next();
                pathp = GetConfig.path_format("address." + spostoffice);
                ConfigurationSection pl = VA_postal.configsettings.getConfigurationSection(pathp);
                if (pl != null) {
                    try {
                        spo_index = Integer.toString(po_index).trim();
                    } catch (Exception e) {
                        spo_index = "null";
                    }
                    String po_status = "false";
                    if (is_poffice_open(spostoffice)) {
                        po_status = "true";
                    }
                    VA_postal.plugin.getConfig().set(GetConfig.path_format("dispatcher.queue." + spo_index + ".open"), po_status);
                    VA_postal.plugin.getConfig().set(GetConfig.path_format("dispatcher.queue." + spo_index + ".name"), spostoffice);
                    VA_postal.plugin.getConfig().set(GetConfig.path_format("dispatcher.queue." + spo_index + ".npc"), "null");
                    VA_postal.plugin.getConfig().set(GetConfig.path_format("dispatcher.queue." + spo_index + ".active"), "false");
                    VA_postal.plugin.getConfig().set(GetConfig.path_format("dispatcher.queue." + spo_index + ".time"), sdefault_time);
                    VA_postal.plugin.getConfig().set(GetConfig.path_format("dispatcher.queue." + spo_index + ".cen_time"), sdefault_time);
                    VA_postal.plugin.getConfig().set(GetConfig.path_format("dispatcher.queue." + spo_index + ".cen_count"), "null");
                    VA_postal.plugin.getConfig().set(GetConfig.path_format("dispatcher.queue." + spo_index + ".cen_interval"), "null");
                    Set<String> pl_keys = pl.getKeys(false);
                    Iterator<String> pl_iterator = pl_keys.iterator();
                    pl_index = 0;
                    while (pl_iterator.hasNext()) {
                        saddress = pl_iterator.next();
                        patha = GetConfig.path_format(pathp + "." + saddress);

                        ConfigurationSection rt = VA_postal.configsettings.getConfigurationSection(patha + ".route");

                        try {
                            spl_index = Integer.toString(pl_index).trim();
                        } catch (Exception e) {
                            spl_index = "null";
                        }
                        String addr_status = "false";
                        if (is_address_open(spostoffice, saddress)) {
                            addr_status = "true";
                        }
                        VA_postal.plugin.getConfig().set(GetConfig.path_format("dispatcher.queue." + spo_index + ".task." + spl_index + ".open"), addr_status);
                        VA_postal.plugin.getConfig().set(GetConfig.path_format("dispatcher.queue." + spo_index + ".task." + spl_index + ".active"), "false");
                        VA_postal.plugin.getConfig().set(GetConfig.path_format("dispatcher.queue." + spo_index + ".task." + spl_index + ".forward"), "true");
                        VA_postal.plugin.getConfig().set(GetConfig.path_format("dispatcher.queue." + spo_index + ".task." + spl_index + ".demoted"), "false");
                        VA_postal.plugin.getConfig().set(GetConfig.path_format("dispatcher.queue." + spo_index + ".task." + spl_index + ".firstpass"), "true");
                        VA_postal.plugin.getConfig().set(GetConfig.path_format("dispatcher.queue." + spo_index + ".task." + spl_index + ".count"), "null");
                        VA_postal.plugin.getConfig().set(GetConfig.path_format("dispatcher.queue." + spo_index + ".task." + spl_index + ".postoffice"), spostoffice);
                        VA_postal.plugin.getConfig().set(GetConfig.path_format("dispatcher.queue." + spo_index + ".task." + spl_index + ".address"), saddress);

                        String promoted = "false";
                        selected_time = sdefault_time;
                        if (mail_to_pick_up(spostoffice, saddress)) {
                            selected_time = promoted_time;
                            promoted = "true";
                        }
                        VA_postal.plugin.getConfig().set(GetConfig.path_format("dispatcher.queue." + spo_index + ".task." + spl_index + ".time"), selected_time);
                        VA_postal.plugin.getConfig().set(GetConfig.path_format("dispatcher.queue." + spo_index + ".task." + spl_index + ".promoted"), promoted);
                        VA_postal.plugin.getConfig().set(GetConfig.path_format("dispatcher.queue." + spo_index + ".task." + spl_index + ".interval"), "null");
                        pl_index++;
                    }
                }
                po_index++;
            }
        }
        VA_postal.plugin.saveConfig();
    }

    public static synchronized boolean mail_to_pick_up(String spostoffice, String saddress) {
        VA_postal.wtr_poffice[0] = spostoffice;
        VA_postal.wtr_address[0] = saddress;
        ID_Mail.set_address_chest_inv(0);
        return ID_Mail.chest_contains_outgoing_mail(0);
    }
}
