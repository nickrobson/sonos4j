package xyz.nickr.sonos4j.api;

import java.util.Scanner;

/**
 * @author Nick Robson
 */
public class LEDState {

    public static void main(String[] args) {
        Speaker speaker = Discovery.getSpeakers()[0];
        System.out.println("Using speaker: " + speaker.getRoomName());

        new Thread(() -> {
            try {
                Scanner scanner = new Scanner(System.in);
                while (true) {
                    boolean oldState = speaker.getDevicePropertiesController().isLEDEnabled();
                    speaker.getDevicePropertiesController().setLEDEnabled(!oldState);
                    scanner.nextLine();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "LEDState").start();

        try {
            while (true) {
                Thread.sleep(1000L);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
