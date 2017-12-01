package com.vodhanel.minecraft.va_postal.config;

import com.vodhanel.minecraft.va_postal.VA_postal;
import com.vodhanel.minecraft.va_postal.common.AnsiColor;
import com.vodhanel.minecraft.va_postal.common.P_Dynmap;
import com.vodhanel.minecraft.va_postal.common.Util;
import com.vodhanel.minecraft.va_postal.common.VA_Dispatcher;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

public class C_Queue {
    VA_postal plugin;

    public C_Queue(VA_postal instance) {
        plugin = instance;
    }


    public static String get_next_queue_task() {
        if (!VA_Dispatcher.dispatcher_running) return "BZY";
        String result;
        String idx;
        String time;
        String active;
        String[] parts;
        String path = GetConfig.path_format("dispatcher.queue");
        ConfigurationSection queue;
        try {
            queue = VA_postal.configsettings.getConfigurationSection(path);
        } catch (Exception e) {
            return "BZY";
        }

        if (queue == null) return "BZY";

        Object[] queue_temp;
        String[] queue_sort;
        try {
            Set<String> queue_index = queue.getKeys(false);
            queue_temp = queue_index.toArray();
            queue_sort = new String[queue_temp.length];
        } catch (Exception e) {
            return "BZY";
        }
        for (int i = 0; i < queue_temp.length; i++)
            try {
                idx = queue_temp[i].toString();
                path = GetConfig.path_format("dispatcher.queue." + idx + ".time");
                time = VA_postal.plugin.getConfig().getString(path);
                path = GetConfig.path_format("dispatcher.queue." + idx + ".active");
                active = VA_postal.plugin.getConfig().getString(path).toLowerCase().trim();
                try {
                    if (is_queue_open(idx)) queue_sort[i] = (time.trim() + "," + idx.trim() + "," + active.trim());
                    else queue_sort[i] = "9999999999,true";
                } catch (Exception e) {
                    queue_sort[i] = "9999999999,true";
                }
            } catch (Exception e) {
                return "BZY";
            }

        try {
            Arrays.sort(queue_sort);
        } catch (Exception e) {
            return "BZY";
        }

        String selected_queue;
        int index = 0;
        boolean open_queue = false;
        try {
            for (int i = 0; i < queue_sort.length; i++) {

                if (!VA_postal.wtr_concurrent) {
                    if (queue_sort[i].contains("true")) return "BZY";

                    open_queue = true;
                    index = 0;
                    break;
                }

                if (queue_sort[i].contains("false")) {
                    index = i;
                    open_queue = true;
                    break;
                }
            }
        } catch (Exception e) {
            open_queue = false;
        }

        if (!open_queue) return "BZY";
        try {
            parts = queue_sort[index].split(",");
            selected_queue = parts[1].trim();
        } catch (Exception e) {
            selected_queue = "";
        }

        if (selected_queue.isEmpty()) return "BZY";

        path = GetConfig.path_format("dispatcher.queue." + selected_queue + ".task");
        ConfigurationSection task;
        try {
            task = VA_postal.configsettings.getConfigurationSection(path);
        } catch (Exception e) {
            return "BZY";
        }
        Object[] task_temp;
        String[] task_sort;
        if (task != null) {
            try {
                Set<String> task_index = task.getKeys(false);
                task_temp = task_index.toArray();
                task_sort = new String[task_temp.length];
            } catch (Exception e) {
                return "BZY";
            }
            for (int i = 0; i < task_temp.length; i++)
                try {
                    idx = task_temp[i].toString();
                    path = GetConfig.path_format("dispatcher.queue." + selected_queue + ".task." + idx + ".time");
                    time = VA_postal.plugin.getConfig().getString(path);
                    if (is_task_open(selected_queue + "," + idx)) task_sort[i] = (time.trim() + "," + idx.trim());
                    else task_sort[i] = "9999999999,BZY";
                } catch (Exception e) {
                    task_sort[i] = "9999999999,BZY";
                }
            try {
                Arrays.sort(task_sort);
            } catch (Exception e) {
                return "BZY";
            }
            try {
                parts = task_sort[0].split(",");
                String selected_task = parts[1].trim();
                if ("BZY".equals(selected_task)) return "BZY";
                result = selected_queue + "," + selected_task;
                return result;
            } catch (Exception ignored) {
            }
        }
        return "BZY";
    }

    public static synchronized boolean is_queue_defined(String q_idx) {
        try {
            String spath = GetConfig.path_format("dispatcher.queue." + q_idx);
            return GetConfig.is_parent_defined(spath);
        } catch (Exception e) {
        }
        return false;
    }

    public static synchronized boolean is_task_defined(String q_idx, String t_idx) {
        try {
            String spath = GetConfig.path_format("dispatcher.queue." + q_idx + ".task." + t_idx);
            return GetConfig.is_parent_defined(spath);
        } catch (Exception e) {
        }
        return false;
    }

    public static synchronized String get_queue_pair(String town, String address) {
        String queue;
        String task;
        String qpair;
        String t_town;
        String t_address;
        String path = GetConfig.path_format("dispatcher.queue");
        ConfigurationSection queue_list;
        try {
            queue_list = VA_postal.configsettings.getConfigurationSection(path);
        } catch (Exception e) {
            return "null";
        }
        Iterator<String> itr_queue_list_keys;
        try {
            Set<String> queue_list_keys = queue_list.getKeys(false);
            itr_queue_list_keys = queue_list_keys.iterator();
        } catch (Exception e) {
            return "null";
        }
        while (itr_queue_list_keys.hasNext()) {
            queue = itr_queue_list_keys.next().trim();
            String tpath = GetConfig.path_format("dispatcher.queue." + queue + ".task");
            ConfigurationSection task_list;
            try {
                task_list = VA_postal.configsettings.getConfigurationSection(tpath);
            } catch (Exception e) {
                return "null";
            }
            Iterator<String> itr_task_list_keys;
            try {
                Set<String> task_list_keys = task_list.getKeys(false);
                itr_task_list_keys = task_list_keys.iterator();
            } catch (Exception e) {
                return "null";
            }
            while (itr_task_list_keys.hasNext()) {
                task = itr_task_list_keys.next().trim();
                qpair = queue + "," + task;
                try {
                    t_town = queue_pair_get_town(qpair);
                    t_address = queue_pair_get_address(qpair);
                    if ((town.equalsIgnoreCase(t_town)) && (address.equalsIgnoreCase(t_address))) return qpair;
                } catch (Exception e) {
                    return "null";
                }
            }
        }
        return "null";
    }

    public static synchronized String get_queue(String town) {
        String queue;
        String t_town;
        String path = GetConfig.path_format("dispatcher.queue");
        ConfigurationSection queue_list;
        try {
            queue_list = VA_postal.configsettings.getConfigurationSection(path);
        } catch (Exception e) {
            return "null";
        }
        Iterator<String> itr_queue_list_keys;
        try {
            Set<String> queue_list_keys = queue_list.getKeys(false);
            itr_queue_list_keys = queue_list_keys.iterator();
        } catch (Exception e) {
            return "null";
        }
        while (itr_queue_list_keys.hasNext()) {
            queue = itr_queue_list_keys.next().trim();
            String tpath = GetConfig.path_format("dispatcher.queue." + queue + ".name");
            t_town = VA_postal.plugin.getConfig().getString(tpath);
            if (town.equalsIgnoreCase(t_town)) return queue;
        }
        return "null";
    }

    public static synchronized String queue_pair_get_town(String pair) {
        String q_idx;
        String t_idx;
        String[] parts;
        try {
            parts = pair.split(",");
            q_idx = GetConfig.proper(parts[0]).trim();
            t_idx = GetConfig.proper(parts[1].trim());
            String spath = GetConfig.path_format("dispatcher.queue." + q_idx + ".task." + t_idx + ".postoffice");
            return VA_postal.plugin.getConfig().getString(spath);
        } catch (Exception e) {
        }
        return "null";
    }

    public static synchronized String queue_pair_get_address(String pair) {
        String q_idx;
        String t_idx;
        String[] parts;
        try {
            parts = pair.split(",");
            q_idx = GetConfig.proper(parts[0]).trim();
            t_idx = GetConfig.proper(parts[1].trim());
            String spath = GetConfig.path_format("dispatcher.queue." + q_idx + ".task." + t_idx + ".address");
            return VA_postal.plugin.getConfig().getString(spath);
        } catch (Exception e) {
        }
        return "null";
    }

    public static synchronized boolean is_task_active(String pair) {
        String q_idx;
        String t_idx;

        String state;
        try {
            String[] parts = pair.split(",");
            q_idx = GetConfig.proper(parts[0]).trim();
            t_idx = GetConfig.proper(parts[1].trim());
            String spath = GetConfig.path_format("dispatcher.queue." + q_idx + ".task." + t_idx + ".active");
            state = VA_postal.plugin.getConfig().getString(spath);
        } catch (Exception e) {
            return false;
        }
        return "true".equalsIgnoreCase(state);
    }

    public static synchronized boolean is_queue_active(String pair) {
        String q_idx;

        String state;
        try {
            if (pair.contains(",")) {
                String[] parts = pair.split(",");
                q_idx = parts[0].trim();
            } else q_idx = pair.trim();
            String spath = GetConfig.path_format("dispatcher.queue." + q_idx + ".active");
            state = VA_postal.plugin.getConfig().getString(spath);
        } catch (Exception e) {
            return false;
        }
        return "true".equalsIgnoreCase(state);
    }

    public static synchronized boolean is_task_open(String pair) {
        String q_idx;
        String t_idx;

        String state = null;
        try {
            String[] parts = pair.split(",");
            q_idx = GetConfig.proper(parts[0]).trim();
            t_idx = GetConfig.proper(parts[1].trim());
            if (is_task_defined(q_idx, t_idx)) {
                String spath = GetConfig.path_format("dispatcher.queue." + q_idx + ".task." + t_idx + ".open");
                state = VA_postal.plugin.getConfig().getString(spath);
            }
        } catch (Exception e) {
            return false;
        }
        return "true".equalsIgnoreCase(state);
    }

    public static synchronized boolean is_task_firstpass(String pair) {
        String q_idx;
        String t_idx;

        String state = null;
        try {
            String[] parts = pair.split(",");
            q_idx = GetConfig.proper(parts[0]).trim();
            t_idx = GetConfig.proper(parts[1].trim());
            if (is_task_defined(q_idx, t_idx)) {
                String spath = GetConfig.path_format("dispatcher.queue." + q_idx + ".task." + t_idx + ".firstpass");
                state = VA_postal.plugin.getConfig().getString(spath);
            }
        } catch (Exception e) {
            return false;
        }
        return "true".equalsIgnoreCase(state);
    }

    public static synchronized boolean is_queue_open(String pair) {
        String q_idx;

        String state = null;
        try {
            if (pair.contains(",")) {
                String[] parts = pair.split(",");
                q_idx = GetConfig.proper(parts[0]).trim();
            } else q_idx = pair.trim();
            if (is_queue_defined(q_idx)) {
                String spath = GetConfig.path_format("dispatcher.queue." + q_idx + ".open");
                state = VA_postal.plugin.getConfig().getString(spath);
            }
        } catch (Exception e) {
            return false;
        }
        return "true".equalsIgnoreCase(state);
    }

    public static synchronized void open_task(String pair, boolean open) {
        String status = "true";
        if (!open) status = "false";
        String q_idx;
        String t_idx;
        try {
            String[] parts = pair.split(",");
            q_idx = GetConfig.proper(parts[0]).trim();
            t_idx = GetConfig.proper(parts[1].trim());

            if (is_task_defined(q_idx, t_idx)) {
                String spath = GetConfig.path_format("dispatcher.queue." + q_idx + ".task." + t_idx + ".open");
                VA_postal.plugin.getConfig().set(spath, status);
                VA_postal.plugin.saveConfig();
            }
        } catch (Exception e) {
            Util.cinform(AnsiColor.RED + "Problem setting task open status");
        }
    }

    public static synchronized boolean is_task_promoted(String pair) {
        String q_idx;
        String t_idx;

        String state = null;
        try {
            String[] parts = pair.split(",");
            q_idx = GetConfig.proper(parts[0]).trim();
            t_idx = GetConfig.proper(parts[1].trim());
            if (is_task_defined(q_idx, t_idx)) {
                String spath = GetConfig.path_format("dispatcher.queue." + q_idx + ".task." + t_idx + ".promoted");
                state = VA_postal.plugin.getConfig().getString(spath);
            }
        } catch (Exception e) {
            return false;
        }
        return "true".equalsIgnoreCase(state);
    }

    public static synchronized boolean is_task_demoted(String pair) {
        String q_idx;
        String t_idx;

        String state = null;
        try {
            String[] parts = pair.split(",");
            q_idx = GetConfig.proper(parts[0]).trim();
            t_idx = GetConfig.proper(parts[1].trim());
            if (is_task_defined(q_idx, t_idx)) {
                String spath = GetConfig.path_format("dispatcher.queue." + q_idx + ".task." + t_idx + ".demoted");
                state = VA_postal.plugin.getConfig().getString(spath);
            }
        } catch (Exception e) {
            return false;
        }
        return "true".equalsIgnoreCase(state);
    }

    public static synchronized void open_queue(String queue, boolean open) {
        String status = "true";
        if (!open) status = "false";
        try {
            if (is_queue_defined(queue)) {
                String spath = GetConfig.path_format("dispatcher.queue." + queue + ".open");
                VA_postal.plugin.getConfig().set(spath, status);
                VA_postal.plugin.saveConfig();
            }
        } catch (Exception e) {
            Util.cinform(AnsiColor.RED + "Problem setting queue open status");
        }
    }

    public static synchronized String queue_pair_get_sinterval(String pair) {
        String q_idx;
        String t_idx;
        String[] parts;
        try {
            parts = pair.split(",");
            q_idx = GetConfig.proper(parts[0]).trim();
            t_idx = GetConfig.proper(parts[1].trim());
            String spath = GetConfig.path_format("dispatcher.queue." + q_idx + ".task." + t_idx + ".interval");
            String result = VA_postal.plugin.getConfig().getString(spath);
            if (result != null) return result;
        } catch (Exception e) {
            return "null";
        }
        return "null";
    }

    public static synchronized String queue_get_sinterval(String queue) {
        try {
            String spath = GetConfig.path_format("dispatcher.queue." + queue + ".cen_interval");
            String result = VA_postal.plugin.getConfig().getString(spath);
            if (result != null) return result;
        } catch (Exception e) {
            return "null";
        }
        return "null";
    }

    public static synchronized int queue_pair_get_age(String pair) {
        String stime;
        String q_idx;
        String[] parts;
        try {
            parts = pair.split(",");
            q_idx = GetConfig.proper(parts[0]).trim();
            String spath = GetConfig.path_format("dispatcher.queue." + q_idx + ".time");
            stime = VA_postal.plugin.getConfig().getString(spath);
            return Integer.parseInt(stime);
        } catch (Exception e) {
        }
        return -1;
    }

    public static synchronized void queue_pair_set_task_age(String pair, String age, String interval) {
        String result = "";
        String q_idx;
        String t_idx;
        String[] parts;
        try {
            parts = pair.split(",");
            q_idx = GetConfig.proper(parts[0]).trim();
            t_idx = GetConfig.proper(parts[1].trim());

            String spath = GetConfig.path_format("dispatcher.queue." + q_idx + ".task." + t_idx + ".time");
            if (!is_task_promoted(q_idx + "," + t_idx)) VA_postal.plugin.getConfig().set(spath, age);
            spath = GetConfig.path_format("dispatcher.queue." + q_idx + ".task." + t_idx + ".interval");
            VA_postal.plugin.getConfig().set(spath, interval);
            VA_postal.plugin.saveConfig();
        } catch (Exception e) {
            Util.cinform(AnsiColor.RED + "Problem setting task age");
        }
    }

    public static synchronized boolean queue_pair_activity_flag(String pair, boolean busy, boolean forward, boolean time_stamp) {
        boolean result = false;
        String q_idx;
        String t_idx;
        String[] parts;
        try {
            parts = pair.split(",");
            q_idx = GetConfig.proper(parts[0]).trim();
            t_idx = GetConfig.proper(parts[1].trim());
        } catch (Exception e) {
            return false;
        }
        String sbusy;
        if (busy) sbusy = "true";
        else sbusy = "false";
        String sforward;
        if (forward) sforward = "true";
        else sforward = "false";

        String spath;
        try {
            spath = GetConfig.path_format("dispatcher.queue." + q_idx + ".active");
            VA_postal.plugin.getConfig().set(spath, sbusy);
            spath = GetConfig.path_format("dispatcher.queue." + q_idx + ".task." + t_idx + ".active");
            VA_postal.plugin.getConfig().set(spath, sbusy);
        } catch (Exception e) {
            return false;
        }
        String spost_time;
        String slast_time;
        long time_sec = System.currentTimeMillis() / 1000L;
        try {
            spost_time = Long.toString(time_sec);
        } catch (Exception e) {
            spost_time = "-1";
        }
        try {
            spath = GetConfig.path_format("dispatcher.queue." + q_idx + ".time");
            slast_time = VA_postal.plugin.getConfig().getString(spath);
            VA_postal.plugin.getConfig().set(spath, spost_time);
        } catch (Exception e) {
            return false;
        }

        String sinterval;
        try {
            long last_time = Long.parseLong(slast_time);
            long interval = time_sec - last_time;
            sinterval = Long.toString(interval);
        } catch (NumberFormatException numberFormatException) {
            sinterval = "null";
        }

        if (time_stamp) try {
            queue_pair_set_task_age(pair, spost_time, sinterval);
        } catch (Exception e) {
            return false;
        }

        String scnt;
        try {
            spath = GetConfig.path_format("dispatcher.queue." + q_idx + ".task." + t_idx + ".forward");
            VA_postal.plugin.getConfig().set(spath, sforward);
            spath = GetConfig.path_format("dispatcher.queue." + q_idx + ".task." + t_idx + ".count");
            scnt = VA_postal.plugin.getConfig().getString(spath);
        } catch (Exception e) {
            return false;
        }

        if ("null".equals(scnt)) VA_postal.plugin.getConfig().set(spath, "1");
        else try {
            int cnt = Integer.parseInt(scnt);
            cnt += 1;
            scnt = Integer.toString(cnt);
            VA_postal.plugin.getConfig().set(spath, scnt);
        } catch (NumberFormatException e) {
        }
        try {
            VA_postal.plugin.saveConfig();
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    public static synchronized boolean npc_exist_for_queue_pair(String qpair) {
        String q_idx;
        if (qpair.contains(",")) {
            String[] parts;
            try {
                parts = qpair.split(",");
                q_idx = parts[0].trim();
            } catch (Exception e) {
                return false;
            }
        } else q_idx = qpair.trim();
        String snpc_id;
        try {
            String spath = GetConfig.path_format("dispatcher.queue." + q_idx + ".npc");
            snpc_id = VA_postal.plugin.getConfig().getString(spath);
        } catch (Exception e) {
            return false;
        }
        return !"null".equals(snpc_id);
    }

    public static synchronized int npc_id_for_queue_pair(String qpair) {
        String q_idx;
        if (qpair.contains(",")) {
            String[] parts;
            try {
                parts = qpair.split(",");
                q_idx = parts[0].trim();
            } catch (Exception e) {
                return -1;
            }
        } else q_idx = qpair.trim();
        try {
            String spath = GetConfig.path_format("dispatcher.queue." + q_idx + ".npc");
            String snpc_id = VA_postal.plugin.getConfig().getString(spath);
            return Integer.parseInt(snpc_id);
        } catch (Exception e) {
        }
        return -1;
    }

    public static synchronized boolean update_npc_id_for_queue_pair(String qpair, int npc_id) {
        String q_idx;
        if (qpair.contains(",")) {
            String[] parts;
            try {
                parts = qpair.split(",");
                q_idx = parts[0].trim();
            } catch (Exception e) {
                return false;
            }
        } else q_idx = qpair.trim();
        try {
            String snpc_id = Integer.toString(npc_id);
            String spath = GetConfig.path_format("dispatcher.queue." + q_idx + ".npc");
            VA_postal.plugin.getConfig().set(spath, snpc_id);
            VA_postal.plugin.saveConfig();
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    public static synchronized boolean delete_npc_for_queue_pair(String qpair) {
        String q_idx;
        if (qpair.contains(",")) {
            String[] parts;
            try {
                parts = qpair.split(",");
                q_idx = parts[0].trim();
            } catch (Exception e) {
                return false;
            }
        } else q_idx = qpair.trim();
        try {
            String spath = GetConfig.path_format("dispatcher.queue." + q_idx + ".npc");
            VA_postal.plugin.getConfig().set(spath, "null");
            VA_postal.plugin.saveConfig();
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    public static synchronized void mark_queue_unused(String queue, String stown) {
        if (is_queue_defined(queue)) {
            int task_cnt = task_count(queue);
            for (int i = 0; i < task_cnt; i++) {
                String task = Util.int2str(i);
                String qpair = queue + "," + task;
                if (is_task_defined(queue, task)) {
                    String saddress = queue_pair_get_address(qpair);
                    if (VA_postal.dynmap_configured) P_Dynmap.delete_addr_label(stown, saddress);
                    C_Address.mark_task_unused(qpair);
                }
            }

            String spath = GetConfig.path_format("dispatcher.queue." + queue + ".open");
            VA_postal.plugin.getConfig().set(spath, "false");
            spath = GetConfig.path_format("dispatcher.queue." + queue + ".name");
            VA_postal.plugin.getConfig().set(spath, "null");
            spath = GetConfig.path_format("dispatcher.queue." + queue + ".npc");
            VA_postal.plugin.getConfig().set(spath, "null");
            spath = GetConfig.path_format("dispatcher.queue." + queue + ".active");
            VA_postal.plugin.getConfig().set(spath, "false");
            spath = GetConfig.path_format("dispatcher.queue." + queue + ".time");
            VA_postal.plugin.getConfig().set(spath, "0000000000");
            spath = GetConfig.path_format("dispatcher.queue." + queue + ".cen_time");
            VA_postal.plugin.getConfig().set(spath, "0000000000");
            spath = GetConfig.path_format("dispatcher.queue." + queue + ".cen_count");
            VA_postal.plugin.getConfig().set(spath, "0");
            spath = GetConfig.path_format("dispatcher.queue." + queue + ".cen_interval");
            VA_postal.plugin.getConfig().set(spath, "0");
            VA_postal.plugin.saveConfig();
        }
    }

    public static synchronized int queue_count() {
        try {
            String spath = GetConfig.path_format("dispatcher.queue");
            return GetConfig.get_number_of_children(spath);
        } catch (Exception e) {
        }
        return 0;
    }

    public static synchronized int task_count(String queue) {
        try {
            String spath = GetConfig.path_format("dispatcher.queue." + queue + ".task");
            return GetConfig.get_number_of_children(spath);
        } catch (Exception e) {
        }
        return 0;
    }

    public static synchronized String get_null_or_next_queue() {
        int q_cnt = queue_count();
        if (q_cnt == 0) return "0";
        String new_queue = "null";
        for (int i = 0; i < q_cnt; i++) {
            String queue = Util.int2str(i);
            String spath = GetConfig.path_format("dispatcher.queue." + queue + ".name");
            String name = VA_postal.plugin.getConfig().getString(spath);
            if (name.equals("null")) {
                new_queue = Util.int2str(i);
                break;
            }
        }
        if (new_queue.equals("null")) new_queue = Util.int2str(q_cnt);
        return new_queue;
    }

    public static synchronized String get_null_or_next_task(String queue) {
        int t_cnt = task_count(queue);
        if (t_cnt == 0) return "0";
        String new_task = "null";
        for (int i = 0; i < t_cnt; i++) {
            String task = Util.int2str(i);
            String spath = GetConfig.path_format("dispatcher.queue." + queue + ".task." + task + ".address");
            String name = VA_postal.plugin.getConfig().getString(spath);
            if (name.equals("null")) {
                new_task = Util.int2str(i);
                break;
            }
        }
        if (new_task.equals("null")) new_task = Util.int2str(t_cnt);
        return new_task;
    }
}
