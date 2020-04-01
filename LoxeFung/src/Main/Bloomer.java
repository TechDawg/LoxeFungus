package Main;

import Utils.Painter;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import Utils.Mouse.MouseTrail;

import java.awt.*;

@ScriptManifest(
        logo = "Shop",
        name = "Dawg Fungi",
        info = "make bank",
        author = "TechDawg",
        version = 1.00
)
public class Bloomer extends Script {
    private MethodProvider api;
    private long startTime;
    private StateHandler stateHandler;
    private Painter painter = new Painter(this, getName(), getVersion());
    private MouseTrail trail = new MouseTrail(0, 255, 255, 2000, this);

    @Override
    public void onStart() {

    }

    @Override
    public int onLoop() throws InterruptedException {
        if (stateHandler == null) {
            stateHandler = new StateHandler(this);
        } else if (stateHandler != null) {
            stateHandler.handleNextState();
        }


        return random(700);
    }

    @Override
    public void onPaint(Graphics2D g) {
        trail.paint(g);
        if (stateHandler == null) {
            painter.setStatus("Initializing");
        } else {
            painter.setStatus(stateHandler.getStatus());
            painter.setAmount(stateHandler.getMatsObtainedd());
        }
        painter.paintTo(g);
    }

    public void onExit() {
        log("Collected Fungus: " + stateHandler.getMatsObtainedd());


    }
}
