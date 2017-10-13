package com.vodhanel.minecraft.va_postal.config;

import com.vodhanel.minecraft.va_postal.VA_postal;
import com.vodhanel.minecraft.va_postal.common.Util;

import java.util.Set;

public class GetConfig {
    VA_postal plugin;

    public GetConfig(VA_postal instance) {
        plugin = instance;
    }

    public static synchronized void update_config_settings() {
        try {
            String spath = path_format("settings.towny");
            String result = VA_postal.plugin.getConfig().getString(spath);
            if ((result.equalsIgnoreCase("true")) || (result.equalsIgnoreCase("false"))) {
                spath = path_format("settings.towny");
                VA_postal.plugin.getConfig().set(spath, null);
                spath = path_format("settings.towny.opt_in");
                VA_postal.plugin.getConfig().set(spath, "false");
                spath = path_format("settings.towny.blocks_per_po");
                VA_postal.plugin.getConfig().set(spath, "96");
                spath = path_format("settings.towny.blocks_per_addr");
                VA_postal.plugin.getConfig().set(spath, "8");
                spath = path_format("settings.towny.wypnts_per_addr");
                VA_postal.plugin.getConfig().set(spath, "30");
                VA_postal.plugin.saveConfig();
            }
        } catch (Exception e) {
        }
        try {
            String spath = path_format("economy.postage.distribution.local");
            String result = VA_postal.plugin.getConfig().getString(spath);
            int iresult = Util.str2int(result);
            if (iresult > 0) {
                spath = path_format("economy.postage.distribution");
                VA_postal.plugin.getConfig().set(spath, null);
                spath = path_format("economy.postage.distribution");
                VA_postal.plugin.getConfig().set(spath, "2");
                VA_postal.plugin.saveConfig();
            }
        } catch (Exception e) {
        }
    }

    public static synchronized int allowed_reditor_afk() {
        String spath = path_format("settings.allowed_reditor_afk");
        try {
            String str = VA_postal.plugin.getConfig().getString(spath);
            return Integer.parseInt(str);
        } catch (Exception e) {
        }
        return 180;
    }

    public static synchronized int new_mail_secs() {
        com.vodhanel.minecraft.va_postal.common.VA_Timers.new_mail_stamp = Util.time_stamp();
        String spath = path_format("settings.new_mail_secs");
        try {
            String str = VA_postal.plugin.getConfig().getString(spath);
            return Integer.parseInt(str);
        } catch (Exception e) {
        }
        return 300;
    }

    public static synchronized boolean strict_door_nav() {
        String spath = path_format("settings.strict_door_nav");
        String str = null;
        try {
            str = VA_postal.plugin.getConfig().getString(spath);
        } catch (Exception e) {
            return true;
        }
        return "true".equalsIgnoreCase(str);
    }

    public static synchronized boolean private_mailboxes() {
        String spath = path_format("settings.private_mailboxes");
        String str = null;
        try {
            str = VA_postal.plugin.getConfig().getString(spath);
        } catch (Exception e) {
            return false;
        }
        return "true".equalsIgnoreCase(str);
    }

    public static synchronized boolean debug() {
        String spath = path_format("Settings.Debug");
        String str = null;
        try {
            str = VA_postal.plugin.getConfig().getString(spath);
        } catch (Exception e) {
            return false;
        }
        return "true".equalsIgnoreCase(str);
    }

    public static synchronized boolean lossy_pathfinding() {
        String spath = path_format("settings.allow_lossy_pathfinding");
        String str;
        try {
            str = VA_postal.plugin.getConfig().getString(spath);
        } catch (Exception e) {
            return false;
        }
        return "true".equalsIgnoreCase(str);
    }

    public static synchronized boolean dynmap() {
        String spath = path_format("settings.dynmap");
        String str = null;
        try {
            str = VA_postal.plugin.getConfig().getString(spath);
        } catch (Exception e) {
            return true;
        }
        return !"false".equalsIgnoreCase(str);
    }

    public static synchronized boolean towny_opt_in() {
        String spath = path_format("settings.towny.opt_in");
        String str = null;
        try {
            str = VA_postal.plugin.getConfig().getString(spath);
        } catch (Exception e) {
            return false;
        }
        return "true".equalsIgnoreCase(str);
    }

    public static synchronized boolean economy_use() {
        String spath = path_format("economy.use");
        String str = null;
        try {
            str = VA_postal.plugin.getConfig().getString(spath);
        } catch (Exception e) {
            return true;
        }
        return !"false".equalsIgnoreCase(str);
    }

    public static synchronized boolean use_scoreboard() {
        String spath = path_format("settings.use_scoreboard");
        String str = null;
        try {
            str = VA_postal.plugin.getConfig().getString(spath);
        } catch (Exception e) {
            return false;
        }
        return "true".equalsIgnoreCase(str);
    }

    public static synchronized int wpnt_hilite_id() {
        String spath = path_format("settings.wpnt_hilite_id");
        try {
            String str = VA_postal.plugin.getConfig().getString(spath);
            return Integer.parseInt(str);
        } catch (Exception e) {
        }
        return 152;
    }

    public static synchronized String get_central_pman_name() {
        try {
            String spath = path_format("settings.name.central");
            return VA_postal.plugin.getConfig().getString(spath);
        } catch (Exception e) {
        }
        return "null";
    }

    public static synchronized String get_local_pman_name() {
        try {
            String spath = path_format("settings.name.local");
            return VA_postal.plugin.getConfig().getString(spath);
        } catch (Exception e) {
        }
        return "null";
    }

    public static synchronized boolean lookclose_on_route() {
        String spath = path_format("settings.lookclose_on_route");
        String str = null;
        try {
            str = VA_postal.plugin.getConfig().getString(spath);
        } catch (Exception e) {
            return true;
        }
        return !"false".equalsIgnoreCase(str);
    }

    public static synchronized boolean is_wtr_concurrent() {
        String spath = path_format("Settings.Concurrent_Postmen");
        String str = null;
        try {
            str = VA_postal.plugin.getConfig().getString(spath);
        } catch (Exception e) {
            return false;
        }
        return "true".equalsIgnoreCase(str);
    }

    public static synchronized float speed() {
        Float result = Float.valueOf(1.0F);
        String spath = path_format("Settings.Speed");
        try {
            String str = VA_postal.plugin.getConfig().getString(spath);
            result = Float.valueOf(Float.parseFloat(str));
        } catch (Exception e) {
            return 1.0F;
        }
        if ((result.floatValue() < 0.5F) || (result.floatValue() > 2.0F)) {
            return 1.0F;
        }
        return result.floatValue();
    }

    public static synchronized int search_distance() {
        String spath = path_format("settings.search_distance");
        try {
            String str = VA_postal.plugin.getConfig().getString(spath);
            return Integer.parseInt(str);
        } catch (Exception e) {
        }
        return 5;
    }

    public static synchronized int allowed_geo_proximity() {
        String spath = path_format("settings.allowed_geo_proximity");
        try {
            String str = VA_postal.plugin.getConfig().getString(spath);
            return Integer.parseInt(str);
        } catch (Exception e) {
        }
        return 15;
    }

    public static synchronized int distr_exp_days() {
        String spath = path_format("settings.distr_exp_days");
        try {
            String str = VA_postal.plugin.getConfig().getString(spath);
            return Integer.parseInt(str);
        } catch (Exception e) {
        }
        return 7;
    }

    public static synchronized int uniform_part_config(int slot, boolean local) {
        String spotype = "local";
        if (!local) {
            spotype = "central";
        }
        String uniform_part = "";
        switch (slot) {
            case 1:
                uniform_part = "helmet";
                break;
            case 2:
                uniform_part = "chestplate";
                break;
            case 3:
                uniform_part = "leggings";
                break;
            case 4:
                uniform_part = "boots";
                break;
            default:
                return -1;
        }
        String spath = path_format("settings.uniform." + spotype + "." + uniform_part);
        try {
            String str = VA_postal.plugin.getConfig().getString(spath);
            return Integer.parseInt(str);
        } catch (Exception e) {
        }
        return -1;
    }

    public static synchronized int postman_cool_sec() {
        String spath = path_format("settings.postman_cool_sec");
        try {
            String str = VA_postal.plugin.getConfig().getString(spath);
            return Integer.parseInt(str);
        } catch (Exception e) {
        }
        return 10;
    }

    public static synchronized int central_cool_sec() {
        String spath = path_format("settings.central_cool_sec");
        try {
            String str = VA_postal.plugin.getConfig().getString(spath);
            return Integer.parseInt(str);
        } catch (Exception e) {
        }
        return 10;
    }

    public static synchronized long residence_cool_ticks() {
        String spath = path_format("settings.residence_cool_ticks");
        try {
            String str = VA_postal.plugin.getConfig().getString(spath);
            return Long.parseLong(str);
        } catch (Exception e) {
        }
        return 100L;
    }

    public static synchronized long heartbeat_ticks() {
        String spath = path_format("settings.heart_beat_ticks");
        try {
            String str = VA_postal.plugin.getConfig().getString(spath);
            return Long.parseLong(str);
        } catch (Exception e) {
        }
        return 100L;
    }

    public static synchronized boolean set_heartbeat_ticks(long ticks) {
        String sticks = null;
        String spath = path_format("settings.heart_beat_ticks");
        try {
            sticks = Long.toString(ticks).trim();
            VA_postal.plugin.getConfig().set(spath, sticks);
            VA_postal.plugin.saveConfig();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static synchronized boolean heart_beat_async() {
        String sbool = null;
        try {
            String spath = path_format("settings.heart_beat_async");
            sbool = VA_postal.plugin.getConfig().getString(spath);
        } catch (Exception e) {
            return false;
        }
        return "true".equals(sbool.toLowerCase().trim());
    }

    public static synchronized boolean heart_beat_auto() {
        String sbool = null;
        try {
            String spath = path_format("settings.heart_beat_auto");
            sbool = VA_postal.plugin.getConfig().getString(spath);
        } catch (Exception e) {
            return false;
        }
        return "true".equals(sbool.toLowerCase().trim());
    }

    public static synchronized String path_format(String str) {
        try {
            String[] skeys = str.trim().split("\\.");
            String fstr = "";
            for (String skey : skeys) {
                fstr = fstr + proper(skey) + ".";
            }
            return fstr.substring(0, fstr.length() - 1);
        } catch (Exception e) {
        }
        return "null";
    }

    public static synchronized String join_message() {
        String spath = path_format("settings.join_message");
        try {
            return VA_postal.plugin.getConfig().getString(spath);
        } catch (Exception e) {
        }
        return "null";
    }

    public static synchronized boolean auto_start() {
        String sbool = null;
        try {
            String spath = path_format("settings.autostart");
            sbool = VA_postal.plugin.getConfig().getString(spath);
        } catch (Exception e) {
            return false;
        }
        return "true".equals(sbool.toLowerCase().trim());
    }

    public static synchronized boolean allow_monster_spawn() {
        String sbool = null;
        try {
            String spath = path_format("settings.allow_monster_spawn");
            sbool = VA_postal.plugin.getConfig().getString(spath);
        } catch (Exception e) {
            return false;
        }
        return "true".equals(sbool.toLowerCase().trim());
    }

    public static synchronized int chunk_overlap() {
        String spath = path_format("settings.chunk_overlap");
        try {
            String str = VA_postal.plugin.getConfig().getString(spath);
            int result = 3;
            result += Integer.parseInt(str);
            if ((result < 3) || (result > 10)) {
            }
            return 3;
        } catch (Exception e) {
        }

        return 3;
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

    public static synchronized int get_number_of_children(String path) {
        org.bukkit.configuration.ConfigurationSection cs = null;
        try {
            path = path_format(path);
            cs = VA_postal.configsettings.getConfigurationSection(path);
        } catch (Exception e) {
            return 0;
        }
        if (cs != null) {
            try {
                Set<String> local_poffices = cs.getKeys(false);
                return local_poffices.size();
            } catch (Exception e) {
                return 0;
            }
        }
        return 0;
    }

    public static synchronized boolean is_parent_defined(String path) {
        try {
            path = path_format(path);
            return VA_postal.configsettings.isConfigurationSection(path);
        } catch (Exception e) {
        }
        return false;
    }
}
