package Utils;

import Main.StateHandler;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.MethodProvider;

import java.awt.*;

public class Painter {
    public int charcoalObtained;
    private final Font font = new Font("Lucida Sans Unicode", Font.PLAIN, 11);

    private final int x = 4;
    private final int y = 248;
    private final int spacing = 15;
    private Skill[] skillsToTrack;
    private MethodProvider api;
    private double version;
    private String status;
    private String name;
    private final Timer timer = new Timer();
//    long startCoins = api.getInventory().getItem("Coins").getAmount();




    private int lines;
    private int amount;
    private int fungusPrice = 700;
    private int profitPer = fungusPrice;




    public Painter(MethodProvider methodProvider, String name, double version) {
        this.name = name;
        this.version = version;
        api = methodProvider;
//        api.log(timer.getElapsedTime());
    }

    public void setStatus(String status) {
        status = status.replaceAll("_", " ");
        this.status = status;
    }

    public void setAmount(int amount) { this.amount = amount; }

    public void paintTo(Graphics2D g) {
        RenderingHints antialiasing = new RenderingHints(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        );
        g.setRenderingHints(antialiasing);
        lines = 0;
        g.setFont(font);
        g.setColor(Color.WHITE);
        g.drawString("[" + name + " v" + version + "]", x, nextY());
        g.drawString("Runtime: " + timer.toString(), x, nextY());
        g.drawString("Status: " + status, x, nextY());
         long elapsedTime = timer.getElapsedTime();

         long fungusPerHr = amount * (3600000 / elapsedTime);
         long profitPerHour = fungusPerHr * profitPer;
//         long spentGp = startCoins - api.getInventory().getItem("Coins").getAmount();
//         long averagePrice = spentGp / amount;
//        double profitPerHour = (long) (amount * (3600000.0 / elapsedTime))/(hours <= 0 ? 1 : hours);
//        g.drawString("Profit per hour:" +
        g.drawString("Profit per hour: " + String.valueOf(profitPerHour), x, nextY());
        g.drawString("Fungus Picked " + amount, x, nextY());
//        g.drawString("Average cost: " + averagePrice, x, nextY());
    }

    private int nextY() {
        return y + spacing * (lines++ - 1);
    }

}
