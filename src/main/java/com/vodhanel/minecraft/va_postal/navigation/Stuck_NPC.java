package com.vodhanel.minecraft.va_postal.navigation;

import com.vodhanel.minecraft.va_postal.VA_postal;
import com.vodhanel.minecraft.va_postal.common.Util;
import net.citizensnpcs.api.ai.Navigator;
import net.citizensnpcs.api.ai.StuckAction;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;

public class Stuck_NPC implements StuckAction {
    public Stuck_NPC() {
    }

    public boolean run(NPC npc, Navigator navigator) {
        int id = -1;
        for (int i = 0; i < VA_postal.wtr_npc.length; i++) {
            if ((VA_postal.wtr_npc[i] != null) && (VA_postal.wtr_npc[i] == npc)) {
                id = i;
                break;
            }
        }

        if (id >= 0) {
            Util.dinform("STUCKACTION FOR " + id);

            long interval = System.currentTimeMillis() - VA_postal.wtr_last_stuck_stamp[id];
            if (interval < 750L) {
                return true;
            }


            if (!ID_WTR.npc_should_run(id)) {
                VA_postal.wtr_last_stuck_stamp[id] = System.currentTimeMillis();
                return false;
            }


            if (ID_WTR.at_waypoint(id)) {
                ID_WTR.invoke_next_waypoint(id);
                VA_postal.wtr_last_stuck_stamp[id] = System.currentTimeMillis();
                return false;
            }


            if ((VA_postal.wtr_door_nav[id]) && (!VA_postal.wtr_door_nav_enter[id]) &&
                    (!"DOOR5".equals(VA_postal.wtr_last_stuck_action[id]))) {
                if ("DOOR3".equals(VA_postal.wtr_last_stuck_action[id])) {
                    ID_WTR.report_recovery(id, "Door Navigation, Soft Reset");
                }
                ID_WTR.open_door(id, true, true);
                soft_reset_stuck_door(id, npc, "DOOR");
                VA_postal.wtr_last_stuck_stamp[id] = System.currentTimeMillis();
                return true;
            }

            if (VA_postal.wtr_last_stuck_action[id].contains("DOOR")) {
                VA_postal.wtr_last_stuck_action[id] = "";
            }

            String stuck_action = VA_postal.wtr_poffice[id] + "," + VA_postal.wtr_address[id] + "," + Util.int2str(VA_postal.wtr_pos[id]);


            if ((VA_postal.wtr_last_stuck_action[id] != null) && (VA_postal.wtr_last_stuck_action[id].equals(stuck_action))) {
                ID_WTR.report_recovery(id, "Route Navigation, Teleport Reset");
                ID_WTR.tp_npc(npc, VA_postal.wtr_waypoint[id].clone().add(0.5, 0, 0.5));
                VA_postal.wtr_last_stuck_action[id] = "";
                ID_WTR.invoke_next_waypoint(id);
                VA_postal.wtr_last_stuck_stamp[id] = System.currentTimeMillis();
                return false;
            }


            if (npc.getEntity().getLocation().distance(VA_postal.wtr_waypoint[id]) > 2.0D) {


                soft_reset_npc_in_place(id, npc, stuck_action);
                VA_postal.wtr_last_stuck_stamp[id] = System.currentTimeMillis();
                return true;
            }


            ID_WTR.tp_npc(npc, VA_postal.wtr_waypoint[id].clone().add(0.5, 0, 0.5));
            VA_postal.wtr_last_stuck_action[id] = "";
            ID_WTR.invoke_next_waypoint(id);
            VA_postal.wtr_last_stuck_stamp[id] = System.currentTimeMillis();
            return false;
        }


        VA_postal.wtr_last_stuck_stamp[id] = System.currentTimeMillis();
        return false;
    }

    public void soft_reset_npc_in_place(int id, NPC npc, String stuck_action) {
        double t_elev = VA_postal.wtr_waypoint[id].getY();
        if (!RouteMngr.cit_ground_waypoint) {
            t_elev -= 1.0D;
        }
        double n_elev = npc.getEntity().getLocation().getY();
        Location target = npc.getEntity().getLocation();
        if (t_elev > n_elev) {
            target.setY(t_elev);
        } else {
            target.setY(n_elev);
        }


        String starget = Util.put_point_on_ground(Util.location2str(target), RouteMngr.cit_ground_waypoint);
        target = Util.str2location(starget);


        ID_WTR.tp_npc(npc, target.clone().add(0.5, 0, 0.5));


        VA_postal.wtr_nav[id].getDefaultParameters().speedModifier(0.7F);
        ID_WTR.safe_re_target(id);

        VA_postal.wtr_last_stuck_action[id] = stuck_action;
    }

    public void soft_reset_stuck_door(int id, NPC npc, String stuck_action) {
        int pass = -1;
        if (VA_postal.wtr_last_stuck_action[id] == "DOOR") {
            pass = 2;
            stuck_action = "DOOR2";
        } else if (VA_postal.wtr_last_stuck_action[id] == "DOOR2") {
            pass = 3;
            stuck_action = "DOOR3";
        } else if (VA_postal.wtr_last_stuck_action[id] == "DOOR3") {
            pass = 4;
            stuck_action = "DOOR4";
        } else if (VA_postal.wtr_last_stuck_action[id] == "DOOR4") {
            pass = 5;
            stuck_action = "DOOR5";
        } else {
            pass = 1;
        }

        if (pass < 3) {
            VA_postal.wtr_nav[id].getDefaultParameters().speedModifier(0.7F);
            ID_WTR.safe_re_target(id);
            VA_postal.wtr_last_stuck_action[id] = stuck_action;
        } else {
            soft_reset_npc_in_place(id, npc, stuck_action);
        }
    }
}
