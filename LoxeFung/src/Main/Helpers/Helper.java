package Main.Helpers;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.script.MethodProvider;
import Utils.Data.Location;

public class Helper {
    MethodProvider api;

    public void walktogate(MethodProvider api) {
        api.getWalking().webWalk((new Area(3444, 3458, 3443, 3458)));
        while (api.myPlayer().isMoving()) {
        }
        RS2Object gate = api.getObjects().closest(new String[]{"Gate"});
        gate.interact("Open");
    }

    public void castbloom() {
        api.getTabs().open(Tab.EQUIPMENT);
        api.getInventory().getItem("Silver sickle (b)").interact("Cast Bloom");
    }


//    public boolean checkforPlayers(int area) {
//        java.util.List<Player> playerss = api.players.getAll();
//        for (Player p : playerss) {
//            if (api.myPlayer().getArea(area).contains(p) && !p.getName().equals(api.myPlayer().getName())) {
//                return true;
//            }
//        }
//        return false;
//    }

    public void opengate(MethodProvider api) {
        RS2Object gate = api.getObjects().closest("Gate");
        api.getWalking().webWalk(gate.getPosition());
        gate.interact("Open");
    }

}
