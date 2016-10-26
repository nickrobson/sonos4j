package xyz.nickr.sonos4j.api;

import xyz.nickr.sonos4j.api.speaker.Device;
import xyz.nickr.sonos4j.api.speaker.ServiceList;
import xyz.nickr.sonos4j.api.speaker.Speaker;

import java.util.function.Consumer;

/**
 * @author Nick Robson
 */
public class GetRoutes {

    public static void main(String[] args) {
        Speaker[] speakers = Discovery.getSpeakers();
        for (Speaker speaker : speakers) {
            Consumer<ServiceList.Service> printer = (service) -> {
                System.out.println(service.getControlUrl());
                service.getSchema().load(speaker);
                service.getSchema().getRoutes().forEach((name, route) -> {
                    System.out.println("-  " + name);
                    route.getArguments().forEach((varname, var) -> {
                        System.out.println("   -  " + var.getDirection() + " : " + varname + " : " + var.getVariable().getType().name());
                    });
                });
            };
            Device device = speaker.getDescription().getDevice();
            device.getServiceList().getServices().forEach(printer::accept);
            device.getDeviceList().getDevices().forEach(d -> {
                d.getServiceList().getServices().forEach(printer::accept);
            });
            return;
        }
    }

}
