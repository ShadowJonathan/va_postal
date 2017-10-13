package com.vodhanel.minecraft.va_postal.mail;

import com.vodhanel.minecraft.va_postal.VA_postal;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;

public class SignManip {
    VA_postal plugin;

    public SignManip(VA_postal instance) {
        plugin = instance;
    }

    public static synchronized boolean is_this_a_postal_sign(Block block, int type) {
        if (block == null) {
            return false;
        }
        String key = "";
        switch (type) {
            case 0:
                key = "[Postal_";
                break;
            case 1:
                key = "[Postal_Mail]";
                break;
            case 2:
                key = "[Postal_Ship]";
                break;
            case 3:
                key = "[Postal_Accept]";
                break;
            case 4:
                key = "[Postal_Refuse]";
                break;
            default:
                key = "[Postal_";
        }
        if ((block.getState() instanceof Sign)) {
            Sign sign = (Sign) block.getState();
            if (sign.getLine(0).contains(key)) {
                return true;
            }
        }
        return false;
    }

    public static synchronized boolean text_exists_sign(Block block, String stext, int line) {
        if (block == null) {
            return false;
        }
        stext = stext.toLowerCase().trim();
        if ((block.getState() instanceof Sign)) {
            Sign sign = (Sign) block.getState();
            if ((line == 1) &&
                    (sign.getLine(0).toLowerCase().contains(stext))) {
                return true;
            }

            if ((line == 2) &&
                    (sign.getLine(1).toLowerCase().contains(stext))) {
                return true;
            }

            if ((line == 3) &&
                    (sign.getLine(2).toLowerCase().contains(stext))) {
                return true;
            }

            if ((line == 4) &&
                    (sign.getLine(3).toLowerCase().contains(stext))) {
                return true;
            }
        }

        return false;
    }

    public static synchronized int get_sign_type(Block block) {
        if (block == null) {
            return -1;
        }
        if (!(block.getState() instanceof Sign)) {
            return -1;
        }
        Sign sign = (Sign) block.getState();
        String key = sign.getLine(0).trim();
        if (key.contains("[Postal_Mail]"))
            return 1;
        if (key.contains("[Postal_Ship]"))
            return 2;
        if (key.contains("[Postal_Accept]"))
            return 3;
        if (key.contains("[Postal_Refuse]"))
            return 4;
        if (key.contains("[Postal_")) {
            return 0;
        }
        return -1;
    }

    public static synchronized String[] get_sign_set(Block block) {
        if (block == null) {
            return null;
        }
        if (!(block.getState() instanceof Sign)) {
            return null;
        }
        Sign sign = (Sign) block.getState();
        String[] set = new String[3];
        try {
            set[0] = sign.getLine(1).trim();
            if ((set[0] == null) || (set[0].isEmpty())) {
                set[0] = "null";
            }
            set[1] = sign.getLine(2).trim();
            if ((set[1] == null) || (set[1].isEmpty())) {
                set[1] = "null";
            }
            set[2] = sign.getLine(3).trim();
            if ((set[2] == null) || (set[2].isEmpty())) {
                set[2] = "null";
            }
        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            return null;
        }
        return set;
    }

    public static synchronized Block sign2chest_block(Block block) {
        if (block == null) {
            return null;
        }
        int dir = block.getData();
        Location sign_back = block.getLocation();
        if (dir == 3) {
            sign_back.subtract(0.0D, 0.0D, 1.0D);
        } else if (dir == 2) {
            sign_back.add(0.0D, 0.0D, 1.0D);
        } else if (dir == 5) {
            sign_back.subtract(1.0D, 0.0D, 0.0D);
        } else if (dir == 4) {
            sign_back.add(1.0D, 0.0D, 0.0D);
        }
        World w = sign_back.getWorld();
        Block c_block = w.getBlockAt(sign_back);
        if ((c_block.getState() instanceof Chest)) {
            return c_block;
        }
        return null;
    }

    public static synchronized Block LookForSignChest(Location search_location, int maxradius, String line1, String line2, String line3, String line4) {
        double y_limit = search_location.getY();

        Block b = search_location.getBlock();
        BlockFace[] faces = {BlockFace.UP, BlockFace.NORTH, BlockFace.EAST};
        BlockFace[][] orth = {{BlockFace.NORTH, BlockFace.EAST}, {BlockFace.UP, BlockFace.EAST}, {BlockFace.NORTH, BlockFace.UP}};
        for (int r = 0; r <= maxradius; r++) {
            for (int s = 0; s < 6; s++) {
                BlockFace f = faces[(s % 3)];
                BlockFace[] o = orth[(s % 3)];
                if (s >= 3) {
                    f = f.getOppositeFace();
                }
                Block c = b.getRelative(f, r);
                for (int x = -r; x <= r; x++) {
                    for (int y = -r; y <= r; y++) {
                        Block a = c.getRelative(o[0], x).getRelative(o[1], y);
                        if (ChestManip.is_chest(a.getTypeId())) {
                            if (a.getY() > y_limit - 2.0D) {

                                if (a.getY() < y_limit + 4.0D) {

                                    if (text_lines_exists_sign_id_chest(a, line1, line2, line3, line4))
                                        return a;
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public static synchronized boolean text_lines_exists_sign_id_chest(Block block, String line1, String line2, String line3, String line4) {
        if (!(block.getState() instanceof Chest)) {
            return false;
        }
        byte dir = block.getData();
        Location chest_front = block.getLocation();

        if (dir == 2) {
            chest_front.subtract(0.0D, 0.0D, 1.0D);
        } else if (dir == 3) {
            chest_front.add(0.0D, 0.0D, 1.0D);
        } else if (dir == 4) {
            chest_front.subtract(1.0D, 0.0D, 0.0D);
        } else if (dir == 5) {
            chest_front.add(1.0D, 0.0D, 0.0D);
        }
        World w = chest_front.getWorld();
        Block block_sign = w.getBlockAt(chest_front);
        if (!(block_sign.getState() instanceof Sign)) {
            return false;
        }
        Sign sign = (Sign) block_sign.getState();

        if (line1 != null) {
            line1 = line1.toLowerCase().trim();
            if (!sign.getLine(0).toLowerCase().contains(line1)) {
                return false;
            }
        }
        if (line2 != null) {
            line2 = line2.toLowerCase().trim();
            if (!sign.getLine(1).toLowerCase().contains(line2)) {
                return false;
            }
        }
        if (line3 != null) {
            line3 = line3.toLowerCase().trim();
            if (!sign.getLine(2).toLowerCase().contains(line3)) {
                return false;
            }
        }
        if (line4 != null) {
            line4 = line4.toLowerCase().trim();
            if (!sign.getLine(3).toLowerCase().contains(line4)) {
                return false;
            }
        }
        return true;
    }

    public static synchronized void create_sign_id_chest(Block block, String line1, String line2, String line3, String line4) {
        if (block == null) {
            return;
        }
        if (!(block.getState() instanceof Chest)) {
            return;
        }
        byte dir = block.getData();
        Location chest_front = block.getLocation();

        if (dir == 2) {
            chest_front.subtract(0.0D, 0.0D, 1.0D);
        } else if (dir == 3) {
            chest_front.add(0.0D, 0.0D, 1.0D);
        } else if (dir == 4) {
            chest_front.subtract(1.0D, 0.0D, 0.0D);
        } else if (dir == 5) {
            chest_front.add(1.0D, 0.0D, 0.0D);
        }
        World w = chest_front.getWorld();
        Block block_sign = w.getBlockAt(chest_front);
        try {
            block_sign.setTypeId(68);
            block_sign.setData(dir);
        } catch (Exception e) {
            return;
        }
        if ((block_sign.getState() instanceof Sign)) {
            Sign sign = (Sign) block_sign.getState();
            sign.setLine(0, line1);
            sign.setLine(1, line2);
            sign.setLine(2, line3);
            sign.setLine(3, line4);
            sign.update();
        }
    }

    public static synchronized void edit_sign_id_chest(Block block, String line1, String line2, String line3, String line4) {
        if (block == null) {
            return;
        }
        if (!(block.getState() instanceof Chest)) {
            return;
        }
        byte dir = block.getData();
        Location chest_front = block.getLocation();

        if (dir == 2) {
            chest_front.subtract(0.0D, 0.0D, 1.0D);
        } else if (dir == 3) {
            chest_front.add(0.0D, 0.0D, 1.0D);
        } else if (dir == 4) {
            chest_front.subtract(1.0D, 0.0D, 0.0D);
        } else if (dir == 5) {
            chest_front.add(1.0D, 0.0D, 0.0D);
        }
        World w = chest_front.getWorld();
        Block block_sign = w.getBlockAt(chest_front);
        if ((block_sign.getState() instanceof Sign)) {
            Sign sign = (Sign) block_sign.getState();
            if (line1 != null) {
                sign.setLine(0, line1);
            }
            if (line2 != null) {
                sign.setLine(1, line2);
            }
            if (line3 != null) {
                sign.setLine(2, line3);
            }
            if (line4 != null) {
                if (line4.length() > 15) {
                    line4 = line4.substring(0, 15);
                }
                sign.setLine(3, line4);
            }
            sign.update();
        }
    }

    public static synchronized void remove_sign_id_chest(Block block) {
        if (block == null) {
            return;
        }
        if (!(block.getState() instanceof Chest)) {
            return;
        }
        byte dir = block.getData();
        Location chest_front = block.getLocation();

        if (dir == 2) {
            chest_front.subtract(0.0D, 0.0D, 1.0D);
        } else if (dir == 3) {
            chest_front.add(0.0D, 0.0D, 1.0D);
        } else if (dir == 4) {
            chest_front.subtract(1.0D, 0.0D, 0.0D);
        } else if (dir == 5) {
            chest_front.add(1.0D, 0.0D, 0.0D);
        }
        World w = chest_front.getWorld();
        Block block_sign = w.getBlockAt(chest_front);
        try {
            block_sign.setTypeId(0);
        } catch (Exception e) {
        }
    }

    public static synchronized boolean exists_sign_id_chest(Block block) {
        if (block == null) {
            return false;
        }
        if (!(block.getState() instanceof Chest)) {
            return false;
        }
        byte dir = block.getData();
        Location chest_front = block.getLocation();

        if (dir == 2) {
            chest_front.subtract(0.0D, 0.0D, 1.0D);
        } else if (dir == 3) {
            chest_front.add(0.0D, 0.0D, 1.0D);
        } else if (dir == 4) {
            chest_front.subtract(1.0D, 0.0D, 0.0D);
        } else if (dir == 5) {
            chest_front.add(1.0D, 0.0D, 0.0D);
        }
        World w = chest_front.getWorld();
        Block block_sign = w.getBlockAt(chest_front);
        return block_sign.getTypeId() == 68;
    }

    public static synchronized boolean text_exists_sign_id_chest(Block block, String stext, int line, boolean global) {
        if (block == null) {
            return false;
        }
        stext = stext.toLowerCase().trim();
        if (!(block.getState() instanceof Chest)) {
            return false;
        }
        byte dir = block.getData();
        Location chest_front = block.getLocation();

        if (dir == 2) {
            chest_front.subtract(0.0D, 0.0D, 1.0D);
        } else if (dir == 3) {
            chest_front.add(0.0D, 0.0D, 1.0D);
        } else if (dir == 4) {
            chest_front.subtract(1.0D, 0.0D, 0.0D);
        } else if (dir == 5) {
            chest_front.add(1.0D, 0.0D, 0.0D);
        }
        World w = chest_front.getWorld();
        Block block_sign = w.getBlockAt(chest_front);
        if ((block_sign.getState() instanceof Sign)) {
            Sign sign = (Sign) block_sign.getState();
            if (((global) || (line == 1)) &&
                    (sign.getLine(0).toLowerCase().contains(stext))) {
                return true;
            }

            if (((global) || (line == 2)) &&
                    (sign.getLine(1).toLowerCase().contains(stext))) {
                return true;
            }

            if (((global) || (line == 3)) &&
                    (sign.getLine(2).toLowerCase().contains(stext))) {
                return true;
            }

            if (((global) || (line == 4)) &&
                    (sign.getLine(3).toLowerCase().contains(stext))) {
                return true;
            }
        }

        return false;
    }
}
