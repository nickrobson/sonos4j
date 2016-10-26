package xyz.nickr.sonos4j.api.speaker.service;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Nick Robson
 */
@Getter
public enum ServiceStateVariableType {

    BOOLEAN("boolean") {
        @Override
        public Object cast(String s) {
            return Boolean.parseBoolean(s);
        }
    },
    STRING("string") {
        @Override
        public Object cast(String s) {
            return s;
        }
    },
    SHORT("i2") {
        @Override
        public Object cast(String s) {
            return Short.parseShort(s);
        }
    },
    INT("i4") {
        @Override
        public Object cast(String s) {
            return Integer.parseInt(s);
        }
    },
    USHORT("ui2") {
        @Override
        public Object cast(String s) {
            return Integer.parseInt(s);
        }
    },
    UINT("ui4") {
        @Override
        public Object cast(String s) {
            return Long.parseLong(s);
        }
    };

    private String dataType;

    ServiceStateVariableType(String dataType) {
        this.dataType = dataType;
    }

    public abstract Object cast(String s);

    private static final Map<String, ServiceStateVariableType> types = new HashMap<>();

    static {
        for (ServiceStateVariableType type : values()) {
            types.put(type.getDataType(), type);
        }
    }

    public static ServiceStateVariableType fromString(String dataType) {
        ServiceStateVariableType type = types.get(dataType);
        if (type == null)
            throw new IllegalArgumentException("Unknown stateVariable data type: " + dataType);
        return type;
    }

}
