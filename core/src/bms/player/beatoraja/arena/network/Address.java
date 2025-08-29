package bms.player.beatoraja.arena.network;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.msgpack.value.ArrayValue;
import org.msgpack.value.Value;

import java.io.Serializable;

public class Address implements Serializable {
    private String host = "";
    private int port = 0;

    public Address() {

    }

    public Address(Value value) {
        ArrayValue arr = value.asArrayValue();
        String host = arr.get(0).asStringValue().asString();
        int port = arr.get(1).asIntegerValue().asInt();
        this.host = host;
        this.port = port;
    }

    public Address(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof Address rhs)
                && rhs.host.equals(this.host)
                && rhs.port == this.port;
    }

    @Override
    public int hashCode() {
        return host.hashCode() ^ (port << 1);
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
