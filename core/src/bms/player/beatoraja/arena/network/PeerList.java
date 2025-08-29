package bms.player.beatoraja.arena.network;

import org.msgpack.value.ArrayValue;
import org.msgpack.value.Value;

import java.util.HashMap;
import java.util.Map;

public class PeerList {
    private Map<Address, Peer> list = new HashMap<>();
    private Address host;

    public PeerList() {

    }

    public PeerList(Value value) {
        ArrayValue arrayValue = value.asArrayValue();
        Value listValue = arrayValue.get(0);
        Map<Value, Value> map = listValue.asMapValue().map();
        map.forEach((k, v) -> {
            // k -> Address || v -> Peer
            Address address = new Address(k);
            Peer peer = new Peer(v);
            this.list.put(address, peer);
        });
        Value hostValue = arrayValue.get(1);
        this.host = new Address(hostValue);
    }

    public Map<Address, Peer> getList() {
        return list;
    }

    public void setList(Map<Address, Peer> list) {
        this.list = list;
    }

    public Address getHost() {
        return host;
    }

    public void setHost(Address host) {
        this.host = host;
    }
}
