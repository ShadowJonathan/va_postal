package com.vodhanel.minecraft.va_postal.mail;

import com.vodhanel.minecraft.va_postal.VA_postal;
import com.vodhanel.minecraft.va_postal.common.AnsiColor;
import com.vodhanel.minecraft.va_postal.common.Util;
import com.vodhanel.minecraft.va_postal.config.C_Postoffice;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ChestManip {
    VA_postal plugin;

    public ChestManip(VA_postal instance) {
        plugin = instance;
    }

    public static synchronized Block block2postal_block(Block block) {
        if (block == null) {
            return null;
        }
        int chest_type = postal_chest_type(block);
        if (chest_type == -1)
            return null;
        if (chest_type == 1) {
            return block;
        }

        if (chest2sign_block(block) != null) {
            return block;
        }
        Location test_loc = block.getLocation().clone();
        test_loc.add(1.0D, 0.0D, 0.0D);

        if (chest2sign_block(test_loc.getBlock()) != null) {
            return test_loc.getBlock();
        }
        test_loc = block.getLocation().clone();
        test_loc.subtract(1.0D, 0.0D, 0.0D);

        if (chest2sign_block(test_loc.getBlock()) != null) {
            return test_loc.getBlock();
        }
        test_loc = block.getLocation().clone();
        test_loc.add(0.0D, 0.0D, 1.0D);

        if (chest2sign_block(test_loc.getBlock()) != null) {
            return test_loc.getBlock();
        }
        test_loc = block.getLocation().clone();
        test_loc.subtract(0.0D, 0.0D, 1.0D);

        if (chest2sign_block(test_loc.getBlock()) != null) {
            return test_loc.getBlock();
        }

        return null;
    }

    public static synchronized Block chest2sign_block(Block block) {
        if (block == null) {
            return null;
        }
        BlockFace dir = ((org.bukkit.material.Chest) block.getState().getData()).getFacing();
        Location chest_front = block.getLocation();

        if (dir == BlockFace.NORTH) {
            chest_front.subtract(0.0D, 0.0D, 1.0D);
        } else if (dir == BlockFace.SOUTH) {
            chest_front.add(0.0D, 0.0D, 1.0D);
        } else if (dir == BlockFace.WEST) {
            chest_front.subtract(1.0D, 0.0D, 0.0D);
        } else if (dir == BlockFace.EAST) {
            chest_front.add(1.0D, 0.0D, 0.0D);
        }
        World w = chest_front.getWorld();
        Block s_block = w.getBlockAt(chest_front);
        if ((s_block.getState() instanceof Sign)) {
            return s_block;
        }
        return null;
    }

    public static synchronized boolean is_this_a_postal_chest(Block block) {
        if (block == null) {
            return false;
        }
        return block.getState() instanceof Chest && (BookManip.valid_postal_log(block, null, null) || block2postal_block(block) != null);

    }

    public static synchronized int postal_chest_type(Block block) {
        if (block == null) {
            return -1;
        }
        if ((block.getState() instanceof Chest)) {
            Inventory inventory = BookManip.postal_inventory(block);
            if (inventory == null) {
                return -1;
            }
            if (inventory.getSize() == 27) {
                return 1;
            }
            return 2;
        }

        return -1;
    }

    public static synchronized Block at_chest(Player player) {
        Location location = player.getLocation();
        return getNearestPackageChest(location, 1, player);
    }

    public static synchronized Block get_parcel_chest(Player player) {
        Location location = player.getLocation();
        return getNearestPackageChest(location, 1, player);
    }

    public static synchronized Block parcel_place_chest_accept(Player player) {
        BlockFace dir = BlockFace.NORTH;
        Location location = player.getLocation();
        double rotation = (player.getLocation().getYaw() - 90.0F) % 360.0F;
        if (rotation < 0.0D) {
            rotation += 360.0D;
        }
        if ((0.0D <= rotation) && (rotation < 22.5D)) {
            location.subtract(1.0D, 0.0D, 0.0D);
            dir = BlockFace.EAST;
        } else if ((22.5D <= rotation) && (rotation < 67.5D)) {
            location.subtract(1.0D, 0.0D, 1.0D);
            dir = BlockFace.EAST;
        } else if ((67.5D <= rotation) && (rotation < 112.5D)) {
            location.subtract(0.0D, 0.0D, 1.0D);
            dir = BlockFace.SOUTH;
        } else if ((112.5D <= rotation) && (rotation < 157.5D)) {
            location.subtract(0.0D, 0.0D, 1.0D);
            location.add(1.0D, 0.0D, 0.0D);
            dir = BlockFace.SOUTH;
        } else if ((157.5D <= rotation) && (rotation < 202.5D)) {
            location.add(1.0D, 0.0D, 0.0D);
            dir = BlockFace.WEST;
        } else if ((202.5D <= rotation) && (rotation < 247.5D)) {
            location.add(1.0D, 0.0D, 1.0D);
            dir = BlockFace.WEST;
        } else if ((247.5D <= rotation) && (rotation < 292.5D)) {
            location.add(0.0D, 0.0D, 1.0D);
            dir = BlockFace.NORTH;
        } else if ((292.5D <= rotation) && (rotation < 337.5D)) {
            location.add(0.0D, 0.0D, 1.0D);
            location.subtract(1.0D, 0.0D, 0.0D);
            dir = BlockFace.NORTH;
        } else if ((337.5D <= rotation) && (rotation < 360.0D)) {
            location.subtract(1.0D, 0.0D, 0.0D);
            dir = BlockFace.EAST;
        }
        World w = location.getWorld();
        Block test_block = w.getBlockAt(location);
        if (test_block == null) {
            return null;
        }
        int stack_height = 0;

        while (test_block.getType() != Material.AIR) {
            location.add(0.0D, 1.0D, 0.0D);
            test_block = w.getBlockAt(location);
            stack_height++;
            if (stack_height > 4) {
                break;
            }
        }

        if (stack_height <= 4) {
            while (test_block.getType() == Material.AIR) {
                location.subtract(0.0D, 1.0D, 0.0D);
                test_block = w.getBlockAt(location);
            }

            location.add(0.0D, 1.0D, 0.0D);
            test_block = w.getBlockAt(location);

            if (test_block.getType() == Material.AIR) {
                try {
                    test_block.setType(Material.CHEST);
                    ((org.bukkit.material.Chest) test_block.getState().getData()).setFacingDirection(dir);
                    return test_block;
                } catch (Exception e) {
                    return null;
                }
            }
        }
        return null;
    }

    public static synchronized Block parcel_place_chest_refuse(ItemStack ind_item) {
        Book book;
        try {
            book = new Book(ind_item);
        } catch (Exception e) {
            return null;
        }


        String[] spage = book.getPages();
        String[] parts = spage[1].split("\n");
        String sworld = parts[0].substring(2).trim();
        String scoords = parts[1].trim();
        String[] parts_c = scoords.split(",");
        String X;
        String Y;
        String Z;
        String sdir;
        byte dir;
        try {
            X = parts_c[0].trim();
            Y = parts_c[1].trim();
            Z = parts_c[2].trim();
            sdir = parts_c[3].trim();
            dir = Byte.parseByte(sdir);
        } catch (Exception e) {
            return null;
        }
        String slocation = sworld + "," + X + "," + Y + "," + Z;
        Location location = Util.str2location(slocation);
        World w = location.getWorld();
        Block test_block = w.getBlockAt(location);
        if (test_block == null) {
            return null;
        }
        int stack_height = 0;

        while (test_block.getType() != Material.AIR) {
            location.add(0.0D, 1.0D, 0.0D);
            test_block = w.getBlockAt(location);
            stack_height++;
            if (stack_height > 4) {
                break;
            }
        }
        if (stack_height <= 4) {
            while (test_block.getType() == Material.AIR) {
                location.subtract(0.0D, 1.0D, 0.0D);
                test_block = w.getBlockAt(location);
            }

            location.add(0.0D, 1.0D, 0.0D);
            test_block = w.getBlockAt(location);

            if (test_block.getType() == Material.AIR) {
                try {
                    test_block.setType(Material.CHEST);
                    ((org.bukkit.material.Chest) test_block.getState().getData()).setFacingDirection(Util.int2BF(dir));
                    return test_block;
                } catch (Exception e) {
                    return null;
                }
            }
        }
        return null;
    }

    public static synchronized void parcel_remove_origen_chest(ItemStack ind_item) {
        Book book;
        try {
            book = new Book(ind_item);
        } catch (Exception e) {
            return;
        }
        String[] spage = book.getPages();
        String[] parts = spage[1].split("\n");
        String sworld = parts[0].substring(2).trim();
        String scoords = parts[1].trim();
        String[] parts_c = scoords.split(",");
        String X;
        String Y;
        String Z;
        try {
            X = parts_c[0].trim();
            Y = parts_c[1].trim();
            Z = parts_c[2].trim();
        } catch (Exception e) {
            return;
        }
        String slocation = sworld + "," + X + "," + Y + "," + Z;
        Location location = Util.str2location(slocation);
        World w = location.getWorld();
        Block block = w.getBlockAt(location);
        if (block == null) {
            return;
        }
        if (!(block.getState() instanceof Chest)) {
            return;
        }
        Chest chest = (Chest) block.getState();

        chest.getInventory().clear();

        SignManip.remove_sign_id_chest(block);
        block.setType(Material.AIR);
    }

    public static synchronized void add_to_central_chest(ItemStack book_item) {
        if (book_item == null) {
            return;
        }
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(VA_postal.plugin, () -> VA_postal.central_po_inventory.addItem(book_item), 12L);


    }

    public static synchronized void set_central_chest_inv() {
        VA_postal.central_po_slocation = Util.put_point_on_ground(C_Postoffice.get_central_po_location(), false);
        if (VA_postal.cstalk) {
            Util.cinform(AnsiColor.CYAN + "Searching for Central post office chest....");
            Util.cinform(AnsiColor.CYAN + "  Starting at:  " + VA_postal.central_po_slocation);
        }
        ID_Mail.SetPostoffice_Chest_nTP_point(1000);
        if ("null".equals(VA_postal.central_schest_location)) {
            Util.cinform(AnsiColor.RED + "[set_central_chest_inv] unable to locate chest");
            return;
        }
        Util.dinform(AnsiColor.CYAN + "[Central PO] chest location: " + VA_postal.central_schest_location);
        Location mailbox_loc = Util.str2location(VA_postal.central_schest_location);
        Chest chest = (Chest) mailbox_loc.getBlock().getState();
        if ((chest == null) || (!is_chest(chest.getType()))) {
            Util.cinform(AnsiColor.RED + "[set_central_chest_inv] unable set inventory");
            VA_postal.central_po_inventory = null;
            return;
        }
        VA_postal.central_po_inventory = chest.getInventory();
    }

    public static synchronized ItemStack central_create_log() {
        String title = "§c[Postal Log]";
        String author = "Central";

        String slocation = VA_postal.central_schest_location;
        String[] parts = slocation.split(",");
        String sworld = parts[0];
        String slocation_mod = parts[1] + "," + parts[2] + "," + parts[3];
        String[] pages = new String[]{Book.makeFirstLogPage(sworld, slocation_mod, "Postal_Central", "Server", VA_postal.SERVER)};

        Book book = new Book(title, author, pages);

        return book.generateItemStack();
    }

    public static synchronized String chest_front(Block block) {
        if (block == null) {
            return "null";
        }
        BlockFace dir = ((org.bukkit.material.Chest) block.getState().getData()).getFacing();
        Location chest_front = block.getLocation();

        if (dir == Util.int2BF(2)) {
            chest_front.subtract(0.0D, 0.0D, 1.0D);
        } else if (dir == Util.int2BF(3)) {
            chest_front.add(0.0D, 0.0D, 1.0D);
        } else if (dir == Util.int2BF(4)) {
            chest_front.subtract(1.0D, 0.0D, 0.0D);
        } else if (dir == Util.int2BF(5)) {
            chest_front.add(1.0D, 0.0D, 0.0D);
        }
        return Util.location2str(chest_front);
    }


    public static synchronized Block getNearestPackageChest(Location location, int maxradius, Player player) {
        Block b = location.getBlock();
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
                        if (is_chest(a.getType())) {
                            if (!is_this_a_postal_chest(a)) {
                                return a;
                            }

                            if (player != null) {
                                Util.pinform(player, "&c&oYou may not use a Postal chest.");
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    public static synchronized Block getNearestGenericChest_to_player(Player player, int maxradius) {
        Location location = player.getLocation();
        Block b = location.getBlock();
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
                        if (is_chest(a.getType())) {
                            return a;
                        }
                    }
                }
            }
        }
        return null;
    }

    public static synchronized Block getNearestGenericChest(Block b, int maxradius) {
        BlockFace[] faces = {BlockFace.UP, BlockFace.NORTH, BlockFace.EAST};
        BlockFace[][] orth = {{BlockFace.NORTH, BlockFace.EAST}, {BlockFace.UP, BlockFace.EAST}, {BlockFace.NORTH, BlockFace.UP}};
        for (int r = 0; r < maxradius; r++) {
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
                        if (is_chest(a.getType())) {
                            return a;
                        }
                    }
                }
            }
        }
        return null;
    }

    public static synchronized boolean does_chest_have_sign(Block b) {
        BlockFace[] faces = {BlockFace.UP, BlockFace.NORTH, BlockFace.EAST};
        BlockFace[][] orth = {{BlockFace.NORTH, BlockFace.EAST}, {BlockFace.UP, BlockFace.EAST}, {BlockFace.NORTH, BlockFace.UP}};
        for (int s = 0; s < 6; s++) {
            BlockFace f = faces[(s % 3)];
            BlockFace[] o = orth[(s % 3)];
            if (s >= 3) {
                f = f.getOppositeFace();
            }
            Block c = b.getRelative(f, 1);
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    Block a = c.getRelative(o[0], x).getRelative(o[1], y);
                    if (a.getType() == Material.WALL_SIGN) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static synchronized boolean is_chest(Material type) {
        return (type == Material.CHEST) || (type == Material.TRAPPED_CHEST);
    }

    public static synchronized boolean ok_to_use_chest(Block block, boolean check_is_postal) {
        if (block == null) {
            return false;
        }
        if (!(block.getState() instanceof Chest)) {
            return false;
        }

        if ((check_is_postal) &&
                (is_this_a_postal_chest(block))) {
            return true;
        }


        if (!does_chest_have_sign(block)) {
            return true;
        }

        Chest chest = (Chest) block.getState();
        Inventory inventory = chest.getInventory();
        if (inventory.getSize() > 27) {
            Location test_loc = block.getLocation().clone();
            test_loc.add(1.0D, 0.0D, 0.0D);

            if ((is_chest(test_loc.getBlock().getType())) &&
                    (!does_chest_have_sign(test_loc.getBlock()))) {
                return true;
            }

            test_loc = block.getLocation().clone();
            test_loc.subtract(1.0D, 0.0D, 0.0D);

            if ((is_chest(test_loc.getBlock().getType())) &&
                    (!does_chest_have_sign(test_loc.getBlock()))) {
                return true;
            }

            test_loc = block.getLocation().clone();
            test_loc.add(0.0D, 0.0D, 1.0D);

            if ((is_chest(test_loc.getBlock().getType())) &&
                    (!does_chest_have_sign(test_loc.getBlock()))) {
                return true;
            }

            test_loc = block.getLocation().clone();
            test_loc.subtract(0.0D, 0.0D, 1.0D);

            if ((is_chest(test_loc.getBlock().getType())) &&
                    (!does_chest_have_sign(test_loc.getBlock()))) {
                return true;
            }
        }


        return false;
    }
}
