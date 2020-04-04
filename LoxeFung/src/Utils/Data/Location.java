package Utils.Data;

import org.osbot.rs07.api.map.Area;

public enum Location {
    BEST_TILE(new Area(3421, 3439, 3421, 3439)),
    SALVE_TELEPORT(new Area(3429, 3458, 3436, 3463)),
    FUNGUS_AREA(new Area(3419, 3436, 3424, 3440)),
    BEST_SPOT(new Area(3420, 3438, 3422, 3438)),
    SALVE_AREA(new Area(3410, 3420, 3455, 3455)),
    GATE_SALVE(new Area(3443, 3457, 3444, 3457)),
    GATE_AREA(new Area(3440, 3458, 3450, 3460)),
    CLAN_WARS(new Area(3384, 3163, 3391, 3155)),
    CLAN_WARS_BANK(new Area(3368, 3170, 3370, 3171)),
    INSIDE_CLAN_WARS(new Area(3320, 4751, 3327, 4759)),
    INSIDE_CLAN_WAR(new Area(3327, 4751, 3330, 4753));
    private String name;
    private Area area;

    Location(Area area) {
        this.area = area;
        int nextIndex = name().indexOf("_");
        this.name = (name().charAt(0) + name().substring(1, nextIndex).toLowerCase() + name().substring(nextIndex++, nextIndex + 1).toUpperCase() + name().substring(nextIndex + 1).toLowerCase()).replaceAll("_", " ");
    }

    public Area getArea() {
        return area;
    }

    public String getName() {
        return name;
    }

}
