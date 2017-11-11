package com.vodhanel.minecraft.va_postal.common;

import com.darkblade12.particleeffect.ParticleEffect;
import com.darkblade12.particleeffect.ParticleEffect.OrdinaryColor;
import com.darkblade12.particleeffect.ParticleEffect.ParticleColor;
import com.vodhanel.minecraft.va_postal.VA_postal;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Particles {
    private static final double wide = 0.25;

    public static synchronized void displayLine(Location from, Location to, Player For, ParticleEffect effect) {
        //Util.dinform("displayLine: " + AnsiColor.GREEN + "from " + AnsiColor.WHITE + "= [" + AnsiColor.YELLOW + from + AnsiColor.WHITE + "], " + AnsiColor.GREEN + "to " + AnsiColor.WHITE + "= [" + AnsiColor.YELLOW + to + AnsiColor.WHITE + "], " + AnsiColor.GREEN + "For " + AnsiColor.WHITE + "= [" + AnsiColor.YELLOW + For + AnsiColor.WHITE + "], " + AnsiColor.GREEN + "effect " + AnsiColor.WHITE + "= [" + AnsiColor.YELLOW + effect + AnsiColor.WHITE + "]");
        from = from.clone();
        Vector dir = to.toVector().subtract(from.toVector());
        Vector one_xth_wide = dir.clone().multiply((10 / dir.length()) / 10).multiply(wide);
        ParticleColor col = new OrdinaryColor(VA_postal.showroute_COL);
        for (int counter = 0; counter < (dir.length() / wide); counter++) {
            Location point = from.add(one_xth_wide);
            try {
                effect.display(col, point, For);
            } catch (Exception e) {
                effect.display(0, 0, 0, 0, 1, point, For);
            }
        }
    }
}
