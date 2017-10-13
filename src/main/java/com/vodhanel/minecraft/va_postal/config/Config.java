package com.vodhanel.minecraft.va_postal.config;

import com.vodhanel.minecraft.va_postal.VA_postal;


public class Config {
    VA_postal plugin;

    public Config(VA_postal instance) {
        plugin = instance;
    }

    public static void LoadConfiguration() {
        String path1 = path_format("settings.speed");
        String path2 = path_format("settings.new_mail_secs");
        String path3 = path_format("settings.alt_central_nav");
        String path4 = path_format("settings.alt_local_nav");
        String path5 = path_format("settings.distanceMargin");
        String path6 = path_format("settings.avoidWater");
        String path7 = path_format("settings.stationaryTicks");
        String path8 = path_format("settings.range");
        String path9 = path_format("settings.ground_waypoint");
        String path10 = path_format("settings.strict_door_nav");
        String path11 = path_format("settings.report_nav_probs");
        String path12 = path_format("settings.search_distance");
        String path13 = path_format("settings.allowed_geo_proximity");
        String path14 = path_format("settings.private_mailboxes");
        String path15 = path_format("settings.dynmap");
        String path16 = path_format("settings.towny.opt_in");
        String path17 = path_format("settings.towny.blocks_per_po");
        String path18 = path_format("settings.towny.blocks_per_addr");
        String path19 = path_format("settings.towny.wypnts_per_addr");
        String path20 = path_format("settings.name.local");
        String path21 = path_format("settings.name.central");
        String path22 = path_format("settings.uniform.central.helmet");
        String path23 = path_format("settings.uniform.central.chestplate");
        String path24 = path_format("settings.uniform.central.leggings");
        String path25 = path_format("settings.uniform.central.boots");
        String path26 = path_format("settings.uniform.local.helmet");
        String path27 = path_format("settings.uniform.local.chestplate");
        String path28 = path_format("settings.uniform.local.leggings");
        String path29 = path_format("settings.uniform.local.boots");
        String path30 = path_format("settings.debug");
        String path31 = path_format("settings.autostart");
        String path32 = path_format("settings.concurrent_postmen");
        String path33 = path_format("settings.lookclose_on_route");
        String path34 = path_format("settings.distr_exp_days");
        String path35 = path_format("settings.postman_cool_sec");
        String path36 = path_format("settings.central_cool_sec");
        String path37 = path_format("settings.residence_cool_ticks");
        String path38 = path_format("settings.heart_beat_ticks");
        String path39 = path_format("settings.heart_beat_async");
        String path40 = path_format("settings.heart_beat_auto");
        String path41 = path_format("settings.chunk_overlap");
        String path42 = path_format("settings.allow_monster_spawn");
        String path43 = path_format("settings.chat_onroute");
        String path44 = path_format("settings.chat_postoffice");
        String path45 = path_format("settings.chat_collision");
        String path46 = path_format("settings.join_message");
        String path47 = path_format("settings.wpnt_hilite_id");
        String path48 = path_format("settings.use_scoreboard");
        String path49 = path_format("settings.allowed_reditor_afk");
        String path50 = path_format("economy.use");
        String path51 = path_format("economy.postoffice.purchase_price");
        String path52 = path_format("economy.address.purchase_price");
        String path53 = path_format("economy.postage.letter.local");
        String path54 = path_format("economy.postage.letter.out_town");
        String path55 = path_format("economy.postage.shipment.local");
        String path56 = path_format("economy.postage.shipment.out_town");
        String path57 = path_format("economy.postage.shipment.cod_surchg");
        String path58 = path_format("economy.postage.distribution");

        String lossy = path_format("settings.allow_lossy_pathfinding");

        VA_postal.plugin.getConfig().addDefault(path1, "1.0");
        VA_postal.plugin.getConfig().addDefault(path2, "300");
        VA_postal.plugin.getConfig().addDefault(path3, "false");
        VA_postal.plugin.getConfig().addDefault(path4, "false");
        VA_postal.plugin.getConfig().addDefault(path5, "1.0");
        VA_postal.plugin.getConfig().addDefault(path6, "true");
        VA_postal.plugin.getConfig().addDefault(path7, "20");
        VA_postal.plugin.getConfig().addDefault(path8, "100");
        VA_postal.plugin.getConfig().addDefault(path9, "false");
        VA_postal.plugin.getConfig().addDefault(path10, "true");
        VA_postal.plugin.getConfig().addDefault(path11, "false");
        VA_postal.plugin.getConfig().addDefault(path12, "5");
        VA_postal.plugin.getConfig().addDefault(path13, "15");
        VA_postal.plugin.getConfig().addDefault(path14, "false");
        VA_postal.plugin.getConfig().addDefault(path15, "true");
        VA_postal.plugin.getConfig().addDefault(path16, "false");
        VA_postal.plugin.getConfig().addDefault(path17, "96");
        VA_postal.plugin.getConfig().addDefault(path18, "8");
        VA_postal.plugin.getConfig().addDefault(path19, "30");
        VA_postal.plugin.getConfig().addDefault(path20, "&cPost&9Man");
        VA_postal.plugin.getConfig().addDefault(path21, "&cPost&9Master");
        VA_postal.plugin.getConfig().addDefault(path22, "310");
        VA_postal.plugin.getConfig().addDefault(path23, "307");
        VA_postal.plugin.getConfig().addDefault(path24, "312");
        VA_postal.plugin.getConfig().addDefault(path25, "309");
        VA_postal.plugin.getConfig().addDefault(path26, "306");
        VA_postal.plugin.getConfig().addDefault(path27, "307");
        VA_postal.plugin.getConfig().addDefault(path28, "308");
        VA_postal.plugin.getConfig().addDefault(path29, "309");
        VA_postal.plugin.getConfig().addDefault(path30, "false");
        VA_postal.plugin.getConfig().addDefault(path31, "false");
        VA_postal.plugin.getConfig().addDefault(path32, "true");
        VA_postal.plugin.getConfig().addDefault(path33, "true");
        VA_postal.plugin.getConfig().addDefault(path34, "7");
        VA_postal.plugin.getConfig().addDefault(path35, "60");
        VA_postal.plugin.getConfig().addDefault(path36, "30");
        VA_postal.plugin.getConfig().addDefault(path37, "100");
        VA_postal.plugin.getConfig().addDefault(path38, "100");
        VA_postal.plugin.getConfig().addDefault(path39, "false");
        VA_postal.plugin.getConfig().addDefault(path40, "true");
        VA_postal.plugin.getConfig().addDefault(path41, "0");
        VA_postal.plugin.getConfig().addDefault(path42, "false");
        VA_postal.plugin.getConfig().addDefault(path43, "&9I wish I had time to chat, but gotto do my rounds.  By!");
        VA_postal.plugin.getConfig().addDefault(path44, "&9I don't have any mail for you,&b %player% &9maybe next time.");
        VA_postal.plugin.getConfig().addDefault(path45, "&9Pardon me.  That was awful clumsy of me.");
        VA_postal.plugin.getConfig().addDefault(path46, "&aType '/postal' for post office commands.");
        VA_postal.plugin.getConfig().addDefault(path47, "152");
        VA_postal.plugin.getConfig().addDefault(path48, "true");
        VA_postal.plugin.getConfig().addDefault(path49, "180");
        VA_postal.plugin.getConfig().addDefault(path50, "false");
        VA_postal.plugin.getConfig().addDefault(path51, "5000");
        VA_postal.plugin.getConfig().addDefault(path52, "500");
        VA_postal.plugin.getConfig().addDefault(path53, "4");
        VA_postal.plugin.getConfig().addDefault(path54, "6");
        VA_postal.plugin.getConfig().addDefault(path55, "10");
        VA_postal.plugin.getConfig().addDefault(path56, "15");
        VA_postal.plugin.getConfig().addDefault(path57, "10");
        VA_postal.plugin.getConfig().addDefault(path58, "2");

        VA_postal.plugin.getConfig().addDefault(lossy, "true");

        VA_postal.plugin.getConfig().options().copyDefaults(true);
        VA_postal.plugin.saveConfig();
    }

    public static String proper(String input) {
        input = input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase().trim();
        return input;
    }

    public static String path_format(String str) {
        String[] skeys = str.trim().split("\\.");
        String fstr = "";
        for (int i = 0; i < skeys.length; i++) {
            fstr = fstr + proper(skeys[i]) + ".";
        }
        String result = fstr.substring(0, fstr.length() - 1);

        return result;
    }
}
