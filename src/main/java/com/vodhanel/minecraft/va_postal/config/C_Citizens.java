package com.vodhanel.minecraft.va_postal.config;

import com.vodhanel.minecraft.va_postal.VA_postal;

public class C_Citizens {
    VA_postal plugin;

    public C_Citizens(VA_postal instance) {
        plugin = instance;
    }

    public static synchronized boolean report_nav_probs() {
        String spath = GetConfig.path_format("settings.report_nav_probs");
        String str = null;
        try {
            str = VA_postal.plugin.getConfig().getString(spath);
        } catch (Exception e) {
            return true;
        }
        return !"false".equalsIgnoreCase(str);
    }

    public static synchronized boolean ground_waypoint() {
        String spath = GetConfig.path_format("settings.ground_waypoint");
        String str = null;
        try {
            str = VA_postal.plugin.getConfig().getString(spath);
        } catch (Exception e) {
            return true;
        }
        return !"false".equalsIgnoreCase(str);
    }

    public static synchronized boolean alt_local_nav() {
        String spath = GetConfig.path_format("settings.alt_local_nav");
        String str = null;
        try {
            str = VA_postal.plugin.getConfig().getString(spath);
        } catch (Exception e) {
            return false;
        }
        return !"false".equalsIgnoreCase(str);
    }

    public static synchronized boolean alt_central_nav() {
        String spath = GetConfig.path_format("settings.alt_central_nav");
        String str = null;
        try {
            str = VA_postal.plugin.getConfig().getString(spath);
        } catch (Exception e) {
            return false;
        }
        return !"false".equalsIgnoreCase(str);
    }

    public static synchronized boolean avoidWater() {
        String spath = GetConfig.path_format("settings.avoidWater");
        String str = null;
        try {
            str = VA_postal.plugin.getConfig().getString(spath);
        } catch (Exception e) {
            return true;
        }
        return !"false".equalsIgnoreCase(str);
    }

    public static synchronized int stationaryTicks() {
        String spath = GetConfig.path_format("settings.stationaryTicks");
        try {
            String str = VA_postal.plugin.getConfig().getString(spath);
            return Integer.parseInt(str);
        } catch (Exception e) {
        }
        return 20;
    }

    public static synchronized double distanceMargin() {
        double result = 0.5D;
        String spath = GetConfig.path_format("settings.distanceMargin");
        try {
            String str = VA_postal.plugin.getConfig().getString(spath);
            result = Double.parseDouble(str);
        } catch (Exception e) {
            return 0.5D;
        }
        return result;
    }

    public static synchronized float range() {
        Float result = Float.valueOf(100.0F);
        String spath = GetConfig.path_format("settings.range");
        try {
            String str = VA_postal.plugin.getConfig().getString(spath);
            if (str.toLowerCase().contains("auto")) {
                return -1.0F;
            }
            return Float.parseFloat(str);
        } catch (Exception e) {
        }
        return 100.0F;
    }

    public static synchronized String chat_onroute() {
        String spath = GetConfig.path_format("settings.chat_onroute");
        try {
            return VA_postal.plugin.getConfig().getString(spath);
        } catch (Exception e) {
        }
        return "null";
    }

    public static synchronized String chat_postoffice() {
        String spath = GetConfig.path_format("settings.chat_postoffice");
        try {
            return VA_postal.plugin.getConfig().getString(spath);
        } catch (Exception e) {
        }
        return "null";
    }

    public static synchronized String chat_collision() {
        String spath = GetConfig.path_format("settings.chat_collision");
        try {
            return VA_postal.plugin.getConfig().getString(spath);
        } catch (Exception e) {
        }
        return "null";
    }
}
