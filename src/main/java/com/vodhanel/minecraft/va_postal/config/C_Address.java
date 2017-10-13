package com.vodhanel.minecraft.va_postal.config;

import com.vodhanel.minecraft.va_postal.VA_postal;
import com.vodhanel.minecraft.va_postal.common.AnsiColor;
import com.vodhanel.minecraft.va_postal.common.P_Dynmap;
import com.vodhanel.minecraft.va_postal.common.Util;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Set;

public class C_Address {
    VA_postal plugin;

    public C_Address(VA_postal instance) {
        plugin = instance;
    }

    public static synchronized String get_address_location(String town, String address) {
        try {
            String spath = GetConfig.path_format("address." + town + "." + address + ".residence.location");
            return VA_postal.plugin.getConfig().getString(spath);
        } catch (Exception e) {
        }
        return "null";
    }

    public static synchronized int address_count(String po_name) {
        try {
            String spath = GetConfig.path_format("address." + po_name);
            return GetConfig.get_number_of_children(spath);
        } catch (Exception e) {
        }
        return 0;
    }

    public static synchronized void delete_address(Player player, String town, String address) {
        if (is_address_defined(town, address)) {
            C_Dispatcher.open_address(town, address, false);
            if (!com.vodhanel.minecraft.va_postal.common.VA_Dispatcher.dispatcher_running) {
                delete_address_worker(player, town, address);
                return;
            }

            String qpair = C_Queue.get_queue_pair(town, address);
            if ("null".equals(qpair)) {
                delete_address_worker(player, town, address);
                return;
            }
            String[] parts = qpair.split(",");
            if ((parts != null) && (parts.length == 2)) {
                String q_idx = parts[0].trim();
                String t_idx = parts[1].trim();
                if (C_Queue.is_task_active(qpair)) {
                    if (player == null) {
                        Util.cinform("Activity detected at addresss: " + town + ", " + address);
                        Util.cinform("Route has been closed, try again when NPC is finished.");
                    } else {
                        Util.pinform(player, "Activity detected at addresss: " + town + ", " + address);
                        Util.pinform(player, "Route has been closed, try again when NPC is finished.");
                    }
                    return;
                }

                mark_task_unused(qpair);
                delete_address_worker(player, town, address);
                if (VA_postal.dynmap_configured) {
                    P_Dynmap.delete_addr_label(town, address);
                }
            }
        }
    }

    public static synchronized void delete_address_worker(Player player, String town, String address) {
        try {
            String spath = GetConfig.path_format("address." + town + "." + address);
            VA_postal.plugin.getConfig().set(spath, null);
            VA_postal.plugin.saveConfig();
            if (player == null) {
                Util.cinform("Successfully deleted addresss: " + town + ", " + address);
            } else {
                Util.pinform(player, "Successfully deleted addresss: " + town + ", " + address);
            }
        } catch (Exception e) {
            Util.cinform(AnsiColor.RED + "Problem deleting address - delete_address_worker");
        }
    }

    public static synchronized void mark_task_unused(String qpair) {
        String[] parts = qpair.split(",");
        if ((parts != null) && (parts.length == 2)) {
            String q_idx = parts[0].trim();
            String t_idx = parts[1].trim();
            if (C_Queue.is_task_defined(q_idx, t_idx)) {
                try {
                    String spath = GetConfig.path_format("dispatcher.queue." + q_idx + ".task." + t_idx + ".open");
                    VA_postal.plugin.getConfig().set(spath, "false");
                    spath = GetConfig.path_format("dispatcher.queue." + q_idx + ".task." + t_idx + ".active");
                    VA_postal.plugin.getConfig().set(spath, "false");
                    spath = GetConfig.path_format("dispatcher.queue." + q_idx + ".task." + t_idx + ".postoffice");
                    VA_postal.plugin.getConfig().set(spath, "null");
                    spath = GetConfig.path_format("dispatcher.queue." + q_idx + ".task." + t_idx + ".address");
                    VA_postal.plugin.getConfig().set(spath, "null");
                    spath = GetConfig.path_format("dispatcher.queue." + q_idx + ".task." + t_idx + ".forward");
                    VA_postal.plugin.getConfig().set(spath, "true");
                    spath = GetConfig.path_format("dispatcher.queue." + q_idx + ".task." + t_idx + ".demoted");
                    VA_postal.plugin.getConfig().set(spath, "false");
                    spath = GetConfig.path_format("dispatcher.queue." + q_idx + ".task." + t_idx + ".firstpass");
                    VA_postal.plugin.getConfig().set(spath, "false");
                    spath = GetConfig.path_format("dispatcher.queue." + q_idx + ".task." + t_idx + ".count");
                    VA_postal.plugin.getConfig().set(spath, "0");
                    spath = GetConfig.path_format("dispatcher.queue." + q_idx + ".task." + t_idx + ".time");
                    VA_postal.plugin.getConfig().set(spath, "0000000000");
                    spath = GetConfig.path_format("dispatcher.queue." + q_idx + ".task." + t_idx + ".promoted");
                    VA_postal.plugin.getConfig().set(spath, "false");
                    spath = GetConfig.path_format("dispatcher.queue." + q_idx + ".task." + t_idx + ".interval");
                    VA_postal.plugin.getConfig().set(spath, "0");
                    VA_postal.plugin.saveConfig();
                } catch (Exception e) {
                    Util.cinform(AnsiColor.RED + "Problem using mark_task_unused");
                }
            } else {
                Util.cinform(AnsiColor.RED + "Problem using mark_task_unused");
            }
        } else {
            Util.cinform(AnsiColor.RED + "Problem using mark_task_unused");
        }
    }

    public static synchronized void save_postal_address_by_player(Player player, String town, String address) {
        address = Util.name_validate(address);
        try {
            String location = Util.get_str_sender_location(player);
            String spath = GetConfig.path_format("address." + town + "." + address + ".residence.location");
            VA_postal.plugin.getConfig().set(spath, location);
            VA_postal.plugin.saveConfig();
            Util.pinform(player, "Residence set: " + town + ", " + address + ", " + location);
        } catch (Exception e) {
            Util.cinform(AnsiColor.RED + "Problem saving postal address by player");
        }
    }

    public static synchronized void save_postal_address_by_location(String location, String town, String address) {
        address = Util.name_validate(address);
        try {
            String spath = GetConfig.path_format("address." + town + "." + address + ".residence.location");
            VA_postal.plugin.getConfig().set(spath, location);
            VA_postal.plugin.saveConfig();
        } catch (Exception e) {
            Util.cinform(AnsiColor.RED + "Problem saving postal address by location");
        }
    }

    public static synchronized void init_address(String stown, String saddress) {
        if (is_address_defined(stown, saddress)) {
            String queue = C_Queue.get_queue(stown);
            if ((queue != null) &&
                    (C_Queue.get_queue_pair(stown, saddress).equals("null"))) {
                String task = C_Queue.get_null_or_next_task(queue);
                if (task != null) {
                    try {
                        String spath = GetConfig.path_format("dispatcher.queue." + queue + ".task." + task + ".open");
                        VA_postal.plugin.getConfig().set(spath, "false");
                        spath = GetConfig.path_format("dispatcher.queue." + queue + ".task." + task + ".active");
                        VA_postal.plugin.getConfig().set(spath, "false");
                        spath = GetConfig.path_format("dispatcher.queue." + queue + ".task." + task + ".postoffice");
                        VA_postal.plugin.getConfig().set(spath, stown);
                        spath = GetConfig.path_format("dispatcher.queue." + queue + ".task." + task + ".address");
                        VA_postal.plugin.getConfig().set(spath, saddress);
                        spath = GetConfig.path_format("dispatcher.queue." + queue + ".task." + task + ".forward");
                        VA_postal.plugin.getConfig().set(spath, "true");
                        spath = GetConfig.path_format("dispatcher.queue." + queue + ".task." + task + ".demoted");
                        VA_postal.plugin.getConfig().set(spath, "false");
                        spath = GetConfig.path_format("dispatcher.queue." + queue + ".task." + task + ".firstpass");
                        VA_postal.plugin.getConfig().set(spath, "false");
                        spath = GetConfig.path_format("dispatcher.queue." + queue + ".task." + task + ".count");
                        VA_postal.plugin.getConfig().set(spath, "0");
                        spath = GetConfig.path_format("dispatcher.queue." + queue + ".task." + task + ".time");
                        VA_postal.plugin.getConfig().set(spath, Util.stime_stamp());
                        spath = GetConfig.path_format("dispatcher.queue." + queue + ".task." + task + ".promoted");
                        VA_postal.plugin.getConfig().set(spath, "false");
                        spath = GetConfig.path_format("dispatcher.queue." + queue + ".task." + task + ".interval");
                        VA_postal.plugin.getConfig().set(spath, "0");
                        VA_postal.plugin.saveConfig();
                        if (VA_postal.dynmap_configured) {
                            P_Dynmap.create_addr_label(stown, saddress);
                        }
                    } catch (Exception e) {
                        Util.cinform(AnsiColor.RED + "Problem using mark_task_unused");
                    }
                } else {
                    Util.cinform(AnsiColor.RED + "Problem using mark_task_unused");
                }
            }
        }
    }

    public static synchronized void list_addresses(Player player, String spoffice) {
        String saddress = "";
        String sowner = "";
        String sinterval = "";
        String sworld = "";
        String display = "";
        String path = GetConfig.path_format("address." + spoffice);
        ConfigurationSection cs = VA_postal.configsettings.getConfigurationSection(path);
        if (cs != null) {
            Set<String> child_keys = cs.getKeys(false);
            if (child_keys.size() > 0) {
                Object[] a_child_keys = child_keys.toArray();
                Arrays.sort(a_child_keys);
                int total = 0;
                try {
                    for (Object a_child_key : a_child_keys) {
                        saddress = a_child_key.toString().trim();
                        sowner = C_Owner.get_owner_address(spoffice, saddress);
                        sinterval = get_addr_interval(spoffice, saddress);
                        total += Util.str2int(sinterval);
                        saddress = C_List.fixed_len(Util.df(saddress), 15, "-");
                        if ("null".equals(sowner)) {
                            sowner = "Server";
                        }
                        sowner = C_List.fixed_len(Util.df(sowner), 15, "-");
                        display = saddress + " &7&o" + sowner + " &f&o" + sinterval;
                        Util.pinform(player, "    &a&l" + display);
                    }
                } catch (Exception e) {
                }
                Util.pinform(player, "&7&oTotal post man walk time for all routes in seconds:  &f&r" + total);
            }
        }
    }

    public static synchronized String get_addr_interval(String stown, String saddress) {
        try {
            String sqp = C_Queue.get_queue_pair(stown, saddress);
            if ("null".equals(sqp)) {
                return "0";
            }
            String result = C_Queue.queue_pair_get_sinterval(sqp);
            if (!"null".equals(result)) {
                return result;
            }
        } catch (Exception e) {
        }
        return "0";
    }

    public static synchronized String addresses_complete(String spoffice, String saddress) {
        if ((spoffice == null) || (saddress == null)) {
            return "null";
        }
        String result = "null";
        ConfigurationSection cs = null;
        try {
            String path = GetConfig.path_format("address." + spoffice);
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
                String saddr = saddress.toLowerCase().trim();
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

    public static synchronized boolean is_address_newmail(String stown, String saddress) {
        if (("null".equals(stown)) || ("null".equals(saddress))) {
            return false;
        }
        String sresult = "false";
        boolean result = false;
        try {
            String spath = GetConfig.path_format("address." + stown + "." + saddress + ".owner.newmail");
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

    public static synchronized void set_address_newmail(String stown, String saddress, boolean new_mail) {
        if (C_Owner.is_address_owner_defined(stown, saddress)) {
            String s_state = "false";
            if (new_mail) {
                s_state = "true";
            }
            try {
                String spath = GetConfig.path_format("address." + stown + "." + saddress + ".owner.newmail");
                VA_postal.plugin.getConfig().set(spath, s_state);
                VA_postal.plugin.saveConfig();
            } catch (Exception e) {
                Util.cinform(AnsiColor.RED + "Problem setting address new mail flag");
            }
        }
    }

    public static synchronized boolean is_address_defined(String po_name, String address) {
        try {
            String spath = GetConfig.path_format("address." + po_name + "." + address + ".residence");
            return GetConfig.is_parent_defined(spath);
        } catch (Exception e) {
        }
        return false;
    }
}
