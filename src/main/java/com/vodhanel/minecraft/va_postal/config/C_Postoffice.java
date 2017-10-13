package com.vodhanel.minecraft.va_postal.config;

import com.vodhanel.minecraft.va_postal.VA_postal;
import com.vodhanel.minecraft.va_postal.common.AnsiColor;
import com.vodhanel.minecraft.va_postal.common.P_Dynmap;
import com.vodhanel.minecraft.va_postal.common.Util;
import com.vodhanel.minecraft.va_postal.common.VA_Dispatcher;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Set;

public class C_Postoffice {
    VA_postal plugin;

    public C_Postoffice(VA_postal instance) {
        plugin = instance;
    }

    public static synchronized String get_central_po_location() {
        try {
            String spath = GetConfig.path_format("postoffice.central.location");
            return VA_postal.plugin.getConfig().getString(spath);
        } catch (Exception e) {
        }
        return "null";
    }

    public static synchronized String get_local_po_location_by_name(String name) {
        if (name == null) {
            return "null";
        }
        try {
            String spath = GetConfig.path_format("postoffice.local." + name + ".location");
            return VA_postal.plugin.getConfig().getString(spath);
        } catch (Exception e) {
        }
        return "null";
    }

    public static synchronized boolean is_local_po_name_defined(String name) {
        try {
            String spath = GetConfig.path_format("postoffice.local." + name);
            return GetConfig.is_parent_defined(spath);
        } catch (Exception e) {
        }
        return false;
    }

    public static synchronized boolean is_central_po_defined() {
        try {
            String spath = GetConfig.path_format("postoffice.central");
            return GetConfig.is_parent_defined(spath);
        } catch (Exception e) {
        }
        return false;
    }

    public static synchronized int po_count() {
        try {
            String spath = GetConfig.path_format("postoffice.local");
            return GetConfig.get_number_of_children(spath);
        } catch (Exception e) {
        }
        return 0;
    }

    public static synchronized void delete_npc(String queue) {
        if (C_Queue.npc_exist_for_queue_pair(queue)) {
            int id = C_Queue.npc_id_for_queue_pair(queue);
            com.vodhanel.minecraft.va_postal.navigation.ID_WTR.clear_goal(id);
            if (VA_postal.wtr_npc[id] != null) {
                VA_postal.wtr_npc[id].destroy();
                VA_postal.wtr_npc[id] = null;
            }
            if (VA_postal.wtr_npc_player[id] != null) {
                VA_postal.wtr_npc_player[id] = null;
            }
            if (VA_postal.wtr_poffice[id] != null) {
                VA_postal.wtr_poffice[id] = null;
            }
            if (VA_postal.wtr_address[id] != null) {
                VA_postal.wtr_address[id] = null;
            }
        }
    }

    public static synchronized void delete_postoffice(Player player, String po_name) {
        if (is_local_po_name_defined(po_name)) {
            C_Dispatcher.open_poffice(po_name, false);
            if (!VA_Dispatcher.dispatcher_running) {
                delete_postoffice_worker(player, po_name);
                return;
            }

            String queue = C_Queue.get_queue(po_name);
            if ("null".equals(queue)) {
                delete_postoffice_worker(player, po_name);
                return;
            }
            if (C_Queue.is_queue_active(queue)) {
                if (player == null) {
                    Util.cinform("Activity detected at post office: " + po_name);
                    Util.cinform("Post office has been closed, try again when NPC is finished.");
                } else {
                    Util.pinform(player, "Activity detected at post office: " + po_name);
                    Util.pinform(player, "Post office has been closed, try again when NPC is finished.");
                }
                return;
            }

            delete_npc(queue);

            for (int i = 0; i < VA_postal.central_route_count; i++) {
                if ((VA_postal.central_array_name[i] != null) &&
                        (VA_postal.central_array_name[i].equalsIgnoreCase(po_name))) {
                    VA_postal.central_array_name[i] = null;
                    VA_postal.central_array_location[i] = null;
                    VA_postal.central_array_promoted[i] = false;
                    VA_postal.central_array_time[i] = -1L;
                    break;
                }
            }


            if (VA_postal.dynmap_configured) {
                P_Dynmap.delete_postman(po_name);
            }

            C_Queue.mark_queue_unused(queue, po_name);
            delete_postoffice_worker(player, po_name);
            if (VA_postal.dynmap_configured) {
                P_Dynmap.delete_po_label(po_name);
            }
        }
    }

    public static synchronized void delete_postoffice_worker(Player player, String po_name) {
        try {
            if (is_local_po_name_defined(po_name)) {
                String spath = GetConfig.path_format("address." + po_name);
                VA_postal.plugin.getConfig().set(spath, null);
                spath = GetConfig.path_format("postoffice.local." + po_name);
                VA_postal.plugin.getConfig().set(spath, null);
                VA_postal.plugin.saveConfig();
                if (player == null) {
                    Util.cinform("Successfully deleted post office: " + po_name);
                } else {
                    Util.pinform(player, "Successfully deleted post office: " + po_name);
                }
            }
        } catch (Exception e) {
            Util.cinform(AnsiColor.RED + "Problem deleting post office - delete_postoffice_worker");
        }
    }

    public static synchronized void save_local_postoffice(String town, String slocation) {
        town = Util.name_validate(town);
        String spath = "";
        try {
            if (!is_local_po_name_defined(town)) {
                spath = GetConfig.path_format("address." + town);
                VA_postal.plugin.getConfig().set(spath, "");
            }
            spath = GetConfig.path_format("postoffice.local." + town + ".location");
            VA_postal.plugin.getConfig().set(spath, slocation);
            VA_postal.plugin.saveConfig();
        } catch (Exception e) {
            Util.cinform(AnsiColor.RED + "Problem saving post office location");
        }
    }

    public static synchronized boolean init_local_postoffice(String town) {
        if ((is_local_po_name_defined(town)) &&
                (C_Queue.get_queue(town).equals("null"))) {
            String queue = C_Queue.get_null_or_next_queue();
            if (queue != null) {
                String spath = GetConfig.path_format("dispatcher.queue." + queue + ".open");
                VA_postal.plugin.getConfig().set(spath, "false");
                spath = GetConfig.path_format("dispatcher.queue." + queue + ".name");
                VA_postal.plugin.getConfig().set(spath, town);
                spath = GetConfig.path_format("dispatcher.queue." + queue + ".npc");
                VA_postal.plugin.getConfig().set(spath, "null");
                spath = GetConfig.path_format("dispatcher.queue." + queue + ".active");
                VA_postal.plugin.getConfig().set(spath, "false");
                spath = GetConfig.path_format("dispatcher.queue." + queue + ".time");
                VA_postal.plugin.getConfig().set(spath, Util.stime_stamp());
                spath = GetConfig.path_format("dispatcher.queue." + queue + ".cen_time");
                VA_postal.plugin.getConfig().set(spath, "0000000000");
                spath = GetConfig.path_format("dispatcher.queue." + queue + ".cen_count");
                VA_postal.plugin.getConfig().set(spath, "0");
                spath = GetConfig.path_format("dispatcher.queue." + queue + ".cen_interval");
                VA_postal.plugin.getConfig().set(spath, "0");
                VA_postal.plugin.saveConfig();
                if (!VA_Dispatcher.dispatcher_running) {
                    return true;
                }

                String slocation = get_local_po_location_by_name(town);
                boolean slot_found = false;
                for (int i = 0; i < VA_postal.central_route_count; i++) {
                    if (VA_postal.central_array_name[i] == null) {
                        VA_postal.central_array_name[i] = town;
                        VA_postal.central_array_location[i] = slocation;
                        VA_postal.central_array_promoted[i] = false;
                        VA_postal.central_array_time[i] = Util.time_stamp();
                        slot_found = true;
                        break;
                    }
                }
                if (!slot_found) {
                    if ((VA_postal.central_array_name != null) &&
                            (VA_postal.central_route_count + 1 >= VA_postal.central_array_name.length)) {
                        Util.cinform(AnsiColor.RED + "Ran out of NPC slots, restart to allocate more");
                        return false;
                    }

                    int i = VA_postal.central_route_count;
                    VA_postal.central_array_name[i] = town;
                    VA_postal.central_array_location[i] = slocation;
                    VA_postal.central_array_promoted[i] = false;
                    VA_postal.central_array_time[i] = Util.time_stamp();
                    VA_postal.central_route_count += 1;
                }
                if (VA_postal.dynmap_configured) {
                    P_Dynmap.create_po_label(town);
                }
            } else {
                Util.cinform(AnsiColor.RED + "Problem finding an open open/new queue.");
                return false;
            }
        }

        return true;
    }

    public static synchronized String get_po_interval(String stown) {
        try {
            String sq = C_Queue.get_queue(stown);
            if ("null".equals(sq)) {
                return "0";
            }
            String result = C_Queue.queue_get_sinterval(sq);
            if (!"null".equals(result)) {
                return result;
            }
        } catch (Exception e) {
        }
        return "0";
    }

    public static synchronized String town_complete(String stown) {
        if (stown == null) {
            return "null";
        }
        String result = "null";
        ConfigurationSection cs = null;
        try {
            String path = GetConfig.path_format("postoffice.local");
            cs = VA_postal.configsettings.getConfigurationSection(path);
        } catch (Exception e) {
            return "null";
        }
        if (cs != null) {
            Set<String> addr = cs.getKeys(false);
            if (addr.size() > 0) {
                Object[] addr_k = null;
                try {
                    addr_k = addr.toArray();
                    Arrays.sort(addr_k);
                } catch (Exception e) {
                    return "null";
                }
                String saddr = stown.toLowerCase().trim();
                String kaddr = "";
                int hit = 0;

                for (Object anAddr_k : addr_k) {
                    kaddr = anAddr_k.toString().toLowerCase().trim();
                    if (kaddr.contains(saddr)) {
                        result = kaddr;
                        hit++;
                    }
                }
                if (hit == 1) {
                    return result;
                }
                hit = 0;

                for (Object anAddr_k : addr_k) {
                    kaddr = anAddr_k.toString().toLowerCase().trim();
                    if (kaddr.length() >= saddr.length()) {
                        kaddr = kaddr.substring(0, saddr.length());
                        if (kaddr.contains(saddr)) {
                            result = anAddr_k.toString().toLowerCase().trim();

                            if (result.equals(saddr)) {
                                return result;
                            }
                            hit++;
                        }
                    }
                }
                if (hit == 1) {
                    return result;
                }
            }
        }

        return "null";
    }
}
