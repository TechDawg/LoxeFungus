package Main;

import Main.Helpers.Helper;
import org.osbot.rs07.api.Keyboard;
import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.api.ui.EquipmentSlot;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.event.WalkingEvent;
import org.osbot.rs07.script.MethodProvider;
import Utils.Data.*;
import org.osbot.rs07.utility.ConditionalSleep;

import java.util.List;

import static org.osbot.rs07.script.MethodProvider.random;
import static Utils.Data.Location.*;
import static org.osbot.rs07.script.MethodProvider.sleep;

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
        CASTING_BLOOM,
        PICKING_FUNGI,
        TELEPORTING_TO_CLANWARS,
        WALKING_TO_PORTAL,
        TELEPORTING_TO_SLAVE,
        HOPPING_WORLD,
    }

    private State getState() throws InterruptedException {
        if (CLAN_WARS.getArea().contains(api.myPlayer())) {
            return State.BANKING;
        }
        if (CLAN_WARS_BANK.getArea().contains(api.myPlayer())) {
            if (!api.getInventory().contains(mats)) {
                return State.WALKING_TO_PORTAL;
            }
            return State.BANKING;
        }
        if (api.objects.closest(26645) != null && !INSIDE_CLAN_WAR.getArea().contains(api.myPlayer().getPosition())) {
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
        if (GATE_SALVE.getArea().contains(api.myPlayer())) {
            return State.WALKING_TO_FUNGUS;
        }
        if (api.getInventory().isFull()) {
            return State.TELEPORTING_TO_CLANWARS;
        }
        List<RS2Object> objs = api.objects.filter(new Filter<RS2Object>() {
            @Override
            public boolean match(RS2Object o) {
                return o.getId() == 3509 && BEST_SPOT.getArea().contains(o);
            }
        });
        RS2Object fungi = api.objects.closest(new String[]{"Fungi on log"});

        api.log(objs.size());
        if (fungi != null && fungi.getPosition().getX() <= 3423 && fungi.getPosition().getY() >= 3436) {
            return State.PICKING_FUNGI;

        }
        if (fungi != null && fungi.getPosition().getX() <= 3423 && fungi.getPosition().getY() == 3438) {
            WalkingEvent tile = new WalkingEvent(new Position(3421, 3438, 0));
            tile.setOperateCamera(false);
            tile.isOperateCamera();
            tile.setMinDistanceThreshold(0);
            api.execute(tile);
            return State.PICKING_FUNGI;

        }
        if (Location.FUNGUS_AREA.getArea().contains(api.myPlayer()) && api.getSkills().getDynamic(Skill.PRAYER) > 0) {
            if (!Location.BEST_TILE.getArea().contains(api.myPosition()) && !api.myPlayer().isMoving()) {
                return State.WALKING_TO_TILE;
            }

        }
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
                if (!api.getEquipment().isWearingItem(EquipmentSlot.RING)) {
                    api.getBank().withdraw("Ring of dueling(8)", 1);
                    api.getBank().close();
                    api.getInventory().interact("Wear", "Ring of dueling(8)");
                }
                break;
            case WALKING_TO_PORTAL:
                api.log("Walking to portal");
                api.getWalking().walk(new Area(3353, 3163, 3352, 3164));
                RS2Object portal = api.objects.closest(26645);
                portal.interact("Enter");
                MethodProvider.sleep(2000);
                break;
            case TELEPORTING_TO_SLAVE:
                api.log("Teleporting To Salve");
                api.getInventory().interact("Break", salveTab);
                MethodProvider.sleep(3000);
                break;
            case WALKING_TO_GATE:
                api.log("Walking to gate");
                api.getWalking().webWalk((new Area(3444, 3458, 3443, 3458)));
                MethodProvider.sleep(1000);
                RS2Object gate = api.getObjects().closest(new String[]{"Gate"});
                gate.interact("Open");
                MethodProvider.sleep(300);
                break;
            case WALKING_TO_FUNGUS:
                api.log("Walking to Fungus");
                api.getWalking().webWalk(FUNGUS_AREA.getArea());
            case WALKING_TO_TILE:
                api.log("Walking to tile");
                WalkingEvent tile = new WalkingEvent(new Position(3421, 3439, 0));
                tile.setOperateCamera(false);
                tile.isOperateCamera();
                tile.setMinDistanceThreshold(0);
                api.execute(tile);
                if (!BEST_TILE.getArea().contains(api.myPlayer()) && !api.myPlayer().isMoving()) {
                    api.getWalking().walk(BEST_TILE.getArea());
                }
                api.log("Walking to tile");
                break;
            case CASTING_BLOOM:
                api.log("Casting bloom");
                api.getEquipment().interact(EquipmentSlot.WEAPON, "Bloom");
                new ConditionalSleep(1000) {
                    @Override
                    public boolean condition() throws InterruptedException {
                        return api.objects.closest(new String[]{"Fungi on log"}) != null;
                    }
                }.sleep();

                MethodProvider.sleep(500);
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
                            public boolean condition() throws InterruptedException {
                                return api.getInventory().getAmount(mats) == flaxInvAmount;
                            }
                        }.sleep();
                        matsObtained += 1;
                    }
                }
                if (fungi == null) {
                    api.getWalking().walk(BEST_TILE.getArea());
                }
                break;
            case TELEPORTING_TO_CLANWARS:
                api.log("Teleporting To Clan wars");
                api.getEquipment().interact(EquipmentSlot.RING, "Clan Wars");
                MethodProvider.sleep(3000);
                break;
            case HOPPING_WORLD:
                api.getWorlds().hopToP2PWorld();
                break;
            case AFK:
                MethodProvider.sleep(100);
        }

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