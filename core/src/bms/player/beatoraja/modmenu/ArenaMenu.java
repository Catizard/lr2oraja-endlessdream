package bms.player.beatoraja.modmenu;

import bms.player.beatoraja.arena.client.Client;
import bms.player.beatoraja.arena.lobby.Lobby;
import bms.player.beatoraja.arena.server.Server;
import imgui.ImGui;
import imgui.flag.ImGuiFocusedFlags;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;

public class ArenaMenu {
    public static boolean isFocused = false;
    public static boolean isShow = false;

    public static void show(ImBoolean showArenaMenu) {
        isShow = showArenaMenu.get();
        if (!isShow) {
            isFocused = false;
        }
        ImGui.begin("EndlessDream ArenaEX", showArenaMenu, ImGuiWindowFlags.AlwaysAutoResize | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoBringToFrontOnFocus);
        {
            isFocused = ImGui.isWindowFocused(ImGuiFocusedFlags.RootAndChildWindows);
            if (ImGui.beginTabBar("TabBar")) {
                if (ImGui.beginTabItem("Client")) {
                    if (ImGui.collapsingHeader("Connect", ImGuiTreeNodeFlags.DefaultOpen)) {
                        ImGui.inputText("Username", Client.userName);
                        ImGui.inputText("Server", Client.host);

                        ImGui.beginDisabled(Client.host.isEmpty() || Client.userName.isEmpty());
                        if (ImGui.button("Connect##Button")) {
                            Client.connect(Client.host.get(), Client.userName.get());
                        }
                        ImGui.endDisabled();

                        ImGui.sameLine();

                        ImGui.beginDisabled(!Client.connected.get());
                        if (ImGui.button("Disconnect")) {
                            Client.disconnect();
                        }
                        ImGui.endDisabled();
                    }
                    ImGui.endTabItem();
                    if (Client.connected.get()) {
                        ImGui.separator();
                        Lobby.render();
                    }
                }

                if (ImGui.beginTabItem("Server")) {
                    ImGui.text("Server");
                    ImGui.separator();

                    ImGui.beginDisabled(Server.started.get());
                    if (ImGui.button("Start")) {
                        Server.start();
                    }
                    ImGui.endDisabled();

                    ImGui.beginDisabled(!Server.started.get());
                    if (ImGui.button("Stop")) {
                        Server.stop();
                    }
                    ImGui.endDisabled();
                    ImGui.checkbox("Auto-rotate host after each song", Server.autoRotateHost);
                    ImGui.endTabItem();
                }
                ImGui.endTabBar();
            }
            ImGui.end();
        }
    }
}
