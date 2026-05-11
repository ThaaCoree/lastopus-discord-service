package util;

import java.text.DecimalFormat;

public class LogWriterUtil {

    public static void log(String string, Object turn) {
        // Combine log message and turn info in one method
        String turnMessage = "";

        // Handle different types of turn (String or Double)
        if (turn instanceof Integer) {
            DecimalFormat df = new DecimalFormat("0.##");
            turnMessage = "<< TURN " + df.format((Integer) turn) + " >>";
        } else if (turn instanceof String) {
            turnMessage = "<< TURN " + turn + " >>";
        }

        System.out.println(String.format("[LOG] %s %s", turnMessage, string));
    }

    public static void log(String string) {
        System.out.println(String.format("[LOG] %s", string));
    }

}
