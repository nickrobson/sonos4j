package xyz.nickr.sonos4j.api;

import xyz.nickr.sonos4j.api.model.ServiceList;
import xyz.nickr.sonos4j.api.model.SpeakerDevice;
import xyz.nickr.sonos4j.api.model.service.ServiceRouteDirection;

import java.util.List;
import java.util.function.Consumer;

import static org.junit.Assert.fail;

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
                        List<String> vars = var.getVariable().getAllowedValues();
                        System.out.println("   " + (var.getDirection() == ServiceRouteDirection.IN ? "<--" : "-->") + "  " + varname + " : " + var.getVariable().getType().name() + (vars != null ? " : " + vars : ""));
                    });
                });
            };
            SpeakerDevice device = speaker.getDescription().getDevice();
            device.getServiceList().getServices().forEach(printer);
            device.getDeviceList().getDevices().forEach(d -> d.getServiceList().getServices().forEach(printer));
            return;
        }
        fail("No Sonos system found");
    }

}
