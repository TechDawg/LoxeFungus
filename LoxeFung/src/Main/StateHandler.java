package Main;

import Main.Helpers.Helper;
import Utils.Data.Location;
import org.osbot.rs07.api.Keyboard;
import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.api.ui.EquipmentSlot;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.event.WalkingEvent;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.utility.ConditionalSleep;

import java.util.List;

import static Utils.Data.Location.*;

public class StateHandler {

    private Helper helper;

    private String status = "Initializing";
    private MethodProvider api;
    private int buying;
    boolean isStoreOpen;
    public int matsObtained;
    boolean isShopEmpty = false;
    public Position topSpot = (new Position(3421, 3439, 0));
    Keyboard meKeyboard = new Keyboard();

    public StateHandler(MethodProvider methodProvider) {
        api = methodProvider;
    }

    private String salveTab = "Salve graveyard teleport";
    private String sickle = "Silver sickle (b)";
    private String mats = "Mort myre fungus";
    private Position doubleSpot = new Position(3421, 3438, 0);
//    private int currentMats = (int) api.getInventory().getAmount(mats);

    public int getMatsObtainedd() {
        return matsObtained;
    }


    private enum State {
        AFK,
        BANKING,
        WALKING_TO_TILE,
        WALKING_TO_GATE,
        WALKING_TO_FUNGUS,
        WALKING_TO_PORTAL,
        WALKING_TO_DOUBLE,
        HOVER_BLOOM,
        CASTING_BLOOM,
        PICKING_FUNGI,
        PICKING_ONE,
        PICKING_TWO,
        TELEPORTING_TO_CLANWARS,
        TELEPORTING_TO_SLAVE,
        HOPPING_WORLD,
        USING_PORTAL,
    }

    private State getState() {
        if (api.getInventory().isFull() && FUNGUS_AREA.getArea().contains(api.myPlayer())) {
            {
                return State.TELEPORTING_TO_CLANWARS;
            }
        }
        List<RS2Object> objs = api.objects.filter(new Filter<RS2Object>() {
            @Override
            public boolean match(RS2Object o) {
                return o.getId() == 3509 && BEST_SPOT.getArea().contains(o);
            }
        });
        RS2Object fungi = api.objects.closest(new String[]{"Fungi on log"});
        api.log(objs.size());
        if (objs.size() > 0 && !doubleSpot.getArea(0).contains(api.myPosition())) {
            if (objs.size() == 1) {
                return State.PICKING_ONE;
            }
            if (objs.size() == 2) {
                return State.WALKING_TO_DOUBLE;
            }
        }
        if (fungi != null && fungi.getPosition().getX() <= 3423 && fungi.getPosition().getY() >= 3436) {
            if (BEST_SPOT.getArea().contains(api.myPlayer()) && fungi.getY() == 3438) {
                return State.PICKING_TWO;
            }
            return State.PICKING_FUNGI;

        }
        if (CLAN_WARS.getArea().contains(api.myPlayer())) {
            return State.BANKING;
        }
        if (CLAN_WARS_BANK.getArea().contains(api.myPlayer())) {
            if (!api.getInventory().contains(mats)) {
                return State.WALKING_TO_PORTAL;
            }
            return State.BANKING;
        }
        if (PORTAL_AREA.getArea().contains(api.myPlayer())){
            return State.USING_PORTAL;
        }
        if (api.objects.closest(26645) != null && INSIDE_CLAN_WAR.getArea().contains(api.myPlayer().getPosition())) {
            return State.WALKING_TO_PORTAL;
        }
        if (CLAN_WARS_BANK.getArea().contains(api.myPlayer())) {
            return State.BANKING;
        }
        if (INSIDE_CLAN_WARS.getArea().contains(api.myPosition()) && !api.myPlayer().isAnimating()) {
            return State.TELEPORTING_TO_SLAVE;
        }
        if (SALVE_TELEPORT.getArea().contains(api.myPlayer())) {
            return State.WALKING_TO_GATE;
        }
        if (Location.FUNGUS_AREA.getArea().contains(api.myPlayer()) && api.getSkills().getDynamic(Skill.PRAYER) > 0) {
            if (!Location.BEST_TILE.getArea().contains(api.myPosition()) && !api.myPlayer().isMoving()) {
                return State.WALKING_TO_TILE;
            }}
        if (GATE_SALVE.getArea().contains(api.myPlayer())) {
            return State.WALKING_TO_FUNGUS;
        }
//        if (fungi != null && fungi.getPosition().getX() <= 3423 && fungi.getPosition().getY() == 3438) {
//            WalkingEvent tile = new WalkingEvent(new Position(3421, 3438, 0));
//            tile.setOperateCamera(false);
//            tile.isOperateCamera();
//            tile.setMinDistanceThreshold(0);
//            api.execute(tile);
//            return State.PICKING_FUNGI;
//
//        }
        if (api.myPlayer().getAnimation() != 1100 && BEST_TILE.getArea().contains(api.myPlayer()) && api.getSkills().getDynamic(Skill.PRAYER) > 0) { //& api.myPlayer().getAnimation() != 388
            return State.CASTING_BLOOM;
        }
        if (api.skills.getDynamic(Skill.PRAYER) == 0 && !Location.CLAN_WARS.getArea().contains(api.myPlayer()) && !Location.CLAN_WARS_BANK.getArea().contains(api.myPlayer()) && api.myPlayer().getAnimation() != 714 && !INSIDE_CLAN_WAR.getArea().contains(api.myPlayer())) {
            return State.TELEPORTING_TO_CLANWARS;
        }
        if (api.getInventory().isFull()) {
            return State.TELEPORTING_TO_CLANWARS;
        }
        if (Location.CLAN_WARS.getArea().contains(api.myPlayer()) && api.getInventory().contains(mats)) {
            return State.BANKING;
        }
        if (GATE_AREA.getArea().contains(api.myPlayer())) {
            RS2Object gate = api.objects.closest(new String[]{"Gate"});
            if (gate.getPosition().getX() == 3444 || gate.getPosition().getX() == 3443) {
                gate.interact("Open");
                MethodProvider.random(1000);
            }
        }
        if (SALVE_AREA.getArea().contains(api.myPlayer()) && !FUNGUS_AREA.getArea().contains(api.myPlayer())) {
            return State.WALKING_TO_FUNGUS;
        }
        return State.AFK;
    }

    public void handleNextState() throws InterruptedException {
        try {
            State state = getState();
            status = state.name().charAt(0) + state.name().substring(1).toLowerCase().replaceAll("_", " ");
            switch (state) {
                case BANKING:
                    api.log("Banking");
                    api.getWalking().webWalk(Location.CLAN_WARS_BANK.getArea());
                    api.getBank().open();
                    api.getBank().depositAllExcept(salveTab);
                    if (!api.getInventory().contains(salveTab)) {
                        api.getBank().withdraw(salveTab, 50);
                    }
                    if (api.getInventory().contains(mats)) {
                        api.getBank().depositAll(mats);
                    }
                    if (!api.getEquipment().isWearingItem(EquipmentSlot.RING)) {
                        api.getBank().withdraw("Ring of dueling(8)", 1);
                        api.getBank().close();
                        api.getInventory().interact("Wear", "Ring of dueling(8)");
                    }
                    break;
                case WALKING_TO_PORTAL:
                    api.log("Walking to portal");
                    api.getWalking().webWalk(PORTAL_AREA.getArea());
                    break;
                case USING_PORTAL:
                    RS2Object portal = api.objects.closest(26645);
                    portal.interact("Enter");
                    new ConditionalSleep(3000) {
                        @Override
                        public boolean condition() {
//                            return api.getInventory().getAmount(mats) == flaxInvAmountt;
                            return INSIDE_CLAN_WARS.getArea().contains(api.myPlayer());
                        }
                    }.sleep();
                case TELEPORTING_TO_SLAVE:
                    api.log("Teleporting To Salve");
                    api.getInventory().interact("Break", salveTab);
                    new ConditionalSleep(20000) {
                        @Override
                        public boolean condition() {
//                            return api.getInventory().getAmount(mats) == flaxInvAmountt;
                            return SALVE_TELEPORT.getArea().contains(api.myPlayer());
                        }
                    }.sleep();
                    break;
                case WALKING_TO_GATE:
                    api.log("Walking to gate");
                    api.getWalking().webWalk((new Area(3444, 3458, 3443, 3458)));
                    MethodProvider.random(1000);
                    RS2Object gate = api.getObjects().closest(new String[]{"Gate"});
                    gate.interact("Open");
                    MethodProvider.random(300);
                    break;
                case WALKING_TO_FUNGUS:
                    api.log("Walking to Fungus");
                    api.getWalking().webWalk(FUNGUS_AREA.getArea().getCentralPosition());
                case WALKING_TO_TILE:

                    api.log("Walking to tile");
//                    WalkingEvent tile = new WalkingEvent(new Position(3421, 3439, 0));
//                    tile.setOperateCamera(false);
//                    tile.isOperateCamera();
//                    tile.setMinDistanceThreshold(0);
//                    api.execute(tile);
                    api.getWalking().walk(new Position(3421, 3439, 0));
                    MethodProvider.random(100);
//                    if (tile.hasFailed()) {
//                        api.execute(tile);
//                    }
//                    if (!BEST_TILE.getArea().contains(api.myPlayer()) && !api.myPlayer().isMoving()) {
//                        api.getWalking().walk(BEST_TILE.getArea());
//                    }
                    api.getWalking().webWalk(BEST_TILE.getArea());
                    new ConditionalSleep(2000) {
                        @Override
                        public boolean condition() {
                            return BEST_TILE.getArea().contains(api.myPlayer());
                        }
                    };
                    api.log("Walking to tile");
                    break;
                case WALKING_TO_DOUBLE:
                    api.log("Walking to double");
                    WalkingEvent tile2 = new WalkingEvent(doubleSpot);
                    tile2.setOperateCamera(false);
                    tile2.isOperateCamera();
                    tile2.setMinDistanceThreshold(0);
                    tile2.getMinDistanceThreshold();
                    api.execute(tile2);
//                    api.getWalking().walk(doubleSpot.getArea(0));
                    new ConditionalSleep(2000) {
                        @Override
                        public boolean condition() {
                            return api.myPlayer().isMoving();
                        }
                    };

                    break;
                case CASTING_BLOOM:
                    api.log("Casting bloom");
                    api.getEquipment().interact(EquipmentSlot.WEAPON, "Bloom");

                    api.sleep(250);
                    new ConditionalSleep(1000) {
                        @Override
                        public boolean condition() {
                            return api.myPlayer().getAnimation() == -1;
                        }
                    }.sleep();

//
                    api.log("Sleep");
                    break;
                case PICKING_FUNGI:
                    RS2Object fungi = api.objects.closest(new String[]{"Fungi on log"});

                    long flaxInvAmount = api.getInventory().getAmount(mats) + 1;
                    if (fungi != null) {
                        if (fungi.getPosition().getX() <= 3423) { //&& api.myPlayer().getAnimation() != 827 && !api.myPlayer().isMoving()
                            api.log("Picking Fungi");
                            fungi.interact("Pick");
                            new ConditionalSleep(2000) {
                                @Override
                                public boolean condition() {
                                    return api.myPlayer().getAnimation() == 827;
                                }
                            }.sleep();
                            matsObtained += 1;
                        }
                    }
                    if (fungi == null) {
                        api.getWalking().walk(BEST_TILE.getArea());
                    }
                    break;
                case PICKING_ONE:
                    RS2Object fungoi = api.getObjects().closest(oba -> oba.getName().equals("Fungi on log") && BEST_SPOT.getArea().contains(oba));
                    long flaxInvAmountt = api.getInventory().getAmount(mats) + 1;
                    fungoi.interact("Pick");
                    new ConditionalSleep(2000) {
                        @Override
                        public boolean condition() {
//                            return api.getInventory().getAmount(mats) == flaxInvAmountt;
                            return api.myPlayer().getAnimation() == 827;
                        }
                    }.sleep();
                    matsObtained += 1;
                    break;
                case PICKING_TWO:
                    long fungInvAmountt = api.getInventory().getAmount(mats) + 1;
                    List<RS2Object> objs = api.objects.filter(new Filter<RS2Object>() {
                        @Override
                        public boolean match(RS2Object o) {
                            return o.getId() == 3509 && BEST_SPOT.getArea().contains(o);
                        }
                    });
                    if (objs.get(0).getId() == 3509){
                    objs.get(0).interact("Pick");
                    MethodProvider.sleep(100);
                    new ConditionalSleep(2000) {
                        @Override
                        public boolean condition() {
                            return api.myPlayer().getAnimation() == 827;
                        }
                    };}
                    matsObtained += 1;
                    if (objs.get(1).getId() == 3509){
                    objs.get(1).interact("Pick");
                    new ConditionalSleep(2000) {
                        @Override
                        public boolean condition() {
                            return api.myPlayer().getAnimation() == 827;
                        }
                    }.sleep();}
                    matsObtained += 1;
                    break;
                case TELEPORTING_TO_CLANWARS:
                    api.log("Teleporting To Clan wars");
                    api.getEquipment().interact(EquipmentSlot.RING, "Clan Wars");
                    new ConditionalSleep(20000) {
                        @Override
                        public boolean condition() {
//                            return api.getInventory().getAmount(mats) == flaxInvAmountt;
                            return CLAN_WARS.getArea().contains(api.myPlayer());
                        }
                    }.sleep();
                    break;
                case HOPPING_WORLD:
                    api.getWorlds().hopToP2PWorld();
                    break;
                case AFK:
                    MethodProvider.sleep(100);
            }
        } catch (Exception e) {
            api.log("ERROR:" + e.toString());

//            sleep(50000);
        }
//78 in ~8 mins
    }

    public String getStatus() {
        return status;
    }
}
//  if (fungi.getPosition().getX() == 3442) {
//          api.log("Picking Fungi 3");
//          fungi.interact("Pick");
//          MethodProvider.sleep(200);
//          RS2Object fungi = api.objects.closest(new String[]{"Fungi on log"};
//          }
//          if (fungi.getPosition().getX() == 3421) {
//          api.log("Picking Fungi 2");
//          fungi.interact("Pick");
//          MethodProvider.sleep(200);
//          }
//          if (fungi.getPosition().getX() == 4420) {
//          api.log("Picking Fungi 1");
//          fungi.interact("Pick");
//          MethodProvider.sleep(200);
//          }