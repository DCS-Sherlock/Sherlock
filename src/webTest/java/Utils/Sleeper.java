package Utils;

import java.util.concurrent.TimeUnit;

public class Sleeper {
    public static void sleep() {
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            throw new Error(e);
        }
    }
}
