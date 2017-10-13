package com.vodhanel.minecraft.va_postal.common;

import com.vodhanel.minecraft.va_postal.VA_postal;

public class AnsiColor {
    public static final String RESET = "\033[0;37m";
    public static final String BLACK = "\033[0;30m";
    public static final String RED = "\033[1;31m";
    public static final String GREEN = "\033[0;32m";
    public static final String L_GREEN = "\033[1;32m";
    public static final String YELLOW = "\033[0;33m";
    public static final String L_YELLOW = "\033[1;33m";
    public static final String BLUE = "\033[1;34m";
    public static final String MAGENTA = "\033[1;35m";
    public static final String CYAN = "\033[0;36m";
    public static final String L_CYAN = "\033[1;36m";
    public static final String WHITE = "\033[0;37m";
    public static final String L_WHITE = "\033[1;37m";
    VA_postal plugin;

    public AnsiColor(VA_postal instance) {
        plugin = instance;
    }
}
