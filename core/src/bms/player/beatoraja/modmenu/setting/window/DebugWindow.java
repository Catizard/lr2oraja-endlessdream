package bms.player.beatoraja.modmenu.setting.window;

import bms.player.beatoraja.Config;
import bms.player.beatoraja.PlayerConfig;
import bms.player.beatoraja.Version;
import bms.player.beatoraja.modmenu.ImGuiNotify;
import imgui.ImGui;
import org.lwjgl.glfw.GLFW;

public class DebugWindow extends BaseSettingWindow {
	public DebugWindow(Config config, PlayerConfig playerConfig) {
		super(config, playerConfig);
	}

	@Override
	public String getName() {
		return "Debug";
	}

	@Override
	public void render() {
		ImGui.text("Commit hash: " + Version.getGitCommitHash());
		ImGui.text("Java version: " + System.getProperty("java.version"));
		ImGui.text("Java vendor: " + System.getProperty("java.vendor"));
		ImGui.text("Build time: " + Version.getBuildDate());
		ImGui.text("GLFW version: " + GLFW.glfwGetVersionString());
		if (ImGui.button("Report a Bug")) {
			ImGuiNotify.info("Not implemented yet :(");
		}
	}

	@Override
	public void refresh() {

	}
}
