package Utils;

public class Timer {

    private long startTime;
    private long timeout;

    public Timer() {
        startTime = System.currentTimeMillis();
    }

    public boolean isActive() {
        return timeout > System.currentTimeMillis();
    }

    public void set(long timeout) {
        this.timeout = timeout;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getElapsedTime() {
        return System.currentTimeMillis() - startTime;
    }

    public String toString() {
        return Format.msToString(getElapsedTime());
    }

    public long getPerHour(long value) {
        return value * 3600000 / getElapsedTime();
    }

}
