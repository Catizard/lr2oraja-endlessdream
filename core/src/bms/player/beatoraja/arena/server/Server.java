package bms.player.beatoraja.arena.server;

import bms.player.beatoraja.arena.client.Client;
import bms.player.beatoraja.arena.enums.ServerToClient;
import bms.player.beatoraja.arena.network.Address;
import bms.player.beatoraja.arena.network.PeerList;
import imgui.type.ImBoolean;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.logging.Logger;

public class Server extends WebSocketServer {
    public static ImBoolean started = new ImBoolean(false);
    public static ImBoolean autoRotateHost = new ImBoolean(false);
    public static ServerState state;

    public Server() {
        this(2222);
    }

    public Server(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        Address clientAddress = new Address(conn.getRemoteSocketAddress());
        Logger.getGlobal().info(String.format("[+] Client (%s:%d) connected", clientAddress.getHost(), clientAddress.getPort()));
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        Address clientAddress = new Address(conn.getRemoteSocketAddress());
        Logger.getGlobal().info(String.format("[+] Client(%s:%d) disconnected", clientAddress.getHost(), clientAddress.getPort()));
        if (started.get()) {
            state.getPeers().remove(clientAddress);
            if (state.getHost().equals(clientAddress)) {
                Optional<Address> any = state.getPeers().keySet().stream().findAny();
                any.ifPresent(nextHost -> state.setHost(nextHost));
            }
            if (!state.getPeers().isEmpty()) {
                PeerList newPeerList = new PeerList(state.getPeers(), state.getHost());
                broadcast(ServerToClient.STC_USERLIST, newPeerList.pack());
            }
        }
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        Address clientAddress = new Address(conn.getRemoteSocketAddress());
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {

    }

    @Override
    public void onStart() {
        started.set(true);
        Logger.getGlobal().info("[+] Server started at " + this.getPort());
    }

    @Override
    public void stop() throws InterruptedException {
        super.stop();
        started.set(false);
        state = new ServerState();
        Logger.getGlobal().info("[+] Server stoped");
    }

    private void broadcast(ServerToClient id, byte[] data) {
        byte[] out = new byte[data.length + 1];
        out[0] = ((byte) id.getValue());
        System.arraycopy(data, 0, out, 1, data.length);
        super.broadcast(out);
    }
}
