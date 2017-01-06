package xyz.nickr.sonos4j.api.model.service;

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
            return "1".equals(s);
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
            int r = Integer.parseInt(s);
            if (r < 0 || r > 0xffff)
                throw new NumberFormatException("not an unsigned short");
            return r;
        }
    },
    UINT("ui4") {
        @Override
        public Object cast(String s) {
            long r = Long.parseLong(s);
            if (r < 0 || r > 0xffffffffL)
                throw new NumberFormatException("not an unsigned short");
            return r;
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
