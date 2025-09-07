package bms.player.beatoraja.arena.server;

import bms.player.beatoraja.arena.network.Address;
import bms.player.beatoraja.arena.network.Peer;

import java.util.HashMap;
import java.util.Map;

public class ServerState {
    private Address host;
    private int currentRandomSeed;
    // private ItemSettings itemSettings;
    // private boolean itemModeEnabled;
    private Map<Address, Peer> peers = new HashMap<>();

    public Address getHost() {
        return host;
    }

    public void setHost(Address host) {
        this.host = host;
    }

    public int getCurrentRandomSeed() {
        return currentRandomSeed;
    }

    public void setCurrentRandomSeed(int currentRandomSeed) {
        this.currentRandomSeed = currentRandomSeed;
    }

    public Map<Address, Peer> getPeers() {
        return peers;
    }

    public void setPeers(Map<Address, Peer> peers) {
        this.peers = peers;
    }
}
