package com.vodhanel.minecraft.va_postal.config;

import com.vodhanel.minecraft.va_postal.VA_postal;

public class C_Economy {
    VA_postal plugin;

    public C_Economy(VA_postal instance) {
        plugin = instance;
    }

    public static synchronized double postage_price(boolean local) {
        double result = 0.0D;
        String spath;
        if (local) {
            spath = GetConfig.path_format("economy.postage.letter.local");
        } else {
            spath = GetConfig.path_format("economy.postage.letter.out_town");
        }
        try {
            String str = VA_postal.plugin.getConfig().getString(spath);
            result = Double.parseDouble(str);
        } catch (Exception e) {
            return 0.0D;
        }
        return result;
    }

    public static synchronized double ship_price(boolean local) {
        double result = 0.0D;
        String spath;
        if (local) {
            spath = GetConfig.path_format("economy.postage.shipment.local");
        } else {
            spath = GetConfig.path_format("economy.postage.shipment.out_town");
        }
        try {
            String str = VA_postal.plugin.getConfig().getString(spath);
            result = Double.parseDouble(str);
        } catch (Exception e) {
            return 0.0D;
        }
        return result;
    }

    public static synchronized double cod_surchg() {
        double result = 0.0D;

        String spath = GetConfig.path_format("economy.postage.shipment.cod_surchg");
        try {
            String str = VA_postal.plugin.getConfig().getString(spath);
            result = Double.parseDouble(str);
        } catch (Exception e) {
            return 0.0D;
        }
        return result;
    }

    public static synchronized double distr_price() {
        double result = 0.0D;
        String spath = GetConfig.path_format("economy.postage.distribution");
        try {
            String str = VA_postal.plugin.getConfig().getString(spath);
            result = Double.parseDouble(str);
        } catch (Exception e) {
            return 0.0D;
        }
        return result;
    }

    public static synchronized double po_purchase_price() {
        double result = 0.0D;
        String spath = GetConfig.path_format("economy.postoffice.purchase_price");
        try {
            String str = VA_postal.plugin.getConfig().getString(spath);
            result = Double.parseDouble(str);
        } catch (Exception e) {
            return 0.0D;
        }
        return result;
    }

    public static synchronized double addr_purchase_price() {
        double result = 0.0D;
        String spath = GetConfig.path_format("economy.address.purchase_price");
        try {
            String str = VA_postal.plugin.getConfig().getString(spath);
            result = Double.parseDouble(str);
        } catch (Exception e) {
            return 0.0D;
        }
        return result;
    }
}
