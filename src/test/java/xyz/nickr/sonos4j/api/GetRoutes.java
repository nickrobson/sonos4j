package xyz.nickr.sonos4j.api;

import java.util.Map;
import xyz.nickr.sonos4j.api.model.DeviceList;
import xyz.nickr.sonos4j.api.model.ServiceList;
import xyz.nickr.sonos4j.api.model.SpeakerDevice;
import xyz.nickr.sonos4j.api.model.service.ServiceRoute;
import xyz.nickr.sonos4j.api.model.service.ServiceRouteArgument;
import xyz.nickr.sonos4j.api.model.service.ServiceRouteDirection;

import java.util.List;

import static org.junit.Assert.fail;

/**
 * @author Nick Robson
 */
public class GetRoutes {

    public static void main(String[] args) {
        Speaker[] speakers = Discovery.getSpeakers();
        for (Speaker speaker : speakers) {
            SpeakerDevice device = speaker.getDescription().getDevice();
            for (ServiceList.Service service : device.getServiceList().getServices()) {
                print(speaker, service);
            }
            for (DeviceList.Device d : device.getDeviceList().getDevices()) {
                print(speaker, d);
            }
            return;
        }
        fail("No Sonos system found");
    }

    public static void print(Speaker speaker, DeviceList.Device device) {
        for (ServiceList.Service service : device.getServiceList().getServices()) {
            print(speaker, service);
        }
    }

    public static void print(Speaker speaker, ServiceList.Service service) {
        System.out.println(service.getControlUrl());
        service.getSchema().load(speaker);
        for (Map.Entry<String, ServiceRoute> entry : service.getSchema().getRoutes().entrySet()) {
            print(speaker, entry.getKey(), entry.getValue());
        }
    }

    public static void print(Speaker speaker, String routeName, ServiceRoute route) {
        System.out.println("-  " + routeName);
        for (Map.Entry<String, ServiceRouteArgument> entry : route.getArguments().entrySet()) {
            String varname = entry.getKey();
            ServiceRouteArgument var = entry.getValue();
            List<String> vars = var.getVariable().getAllowedValues();
            System.out.println("   " + (var.getDirection() == ServiceRouteDirection.IN ? "<--" : "-->") + "  " + varname + " : " + var.getVariable().getType().name() + (vars != null ? " : " + vars : ""));
        }
    }

}
