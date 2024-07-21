package hmy.self.clicker;

public enum Speed {

    SPEED_0_0_1("0.01S", 10),
    SPEED_0_1("0.1S", 100),
    SPEED_0_2("0.2S", 200),
    SPEED_0_3("0.3S", 300),
    SPEED_0_4("0.4S", 400),
    SPEED_0_5("0.5S", 500),
    SPEED_0_6("0.6S", 600),
    SPEED_0_7("0.7S", 700),
    SPEED_0_8("0.8S", 800),
    SPEED_0_9("0.9S", 900),
    SPEED_1("1S", 1000),
    SPEED_5("5S", 5000),
    SPEED_10("10S", 10000);

    public static final String DEFAULT_SPEED = "0.5S";

    private final String name;

    private final int speed;

    Speed(String name, int delay) {
        this.name = name;
        this.speed = delay;
    }

    public int getSpeed() {
        return speed;
    }

    public String getName() {
        return name;
    }
}
