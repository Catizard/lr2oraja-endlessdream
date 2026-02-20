package bms.player.beatoraja.modmenu.setting;

import bms.player.beatoraja.MainController;
import bms.player.beatoraja.modmenu.FreqTrainerMenu;
import bms.player.beatoraja.modmenu.RandomTrainerMenu;
import bms.player.beatoraja.modmenu.setting.widget.Label;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;

public class ModMenuOverlay {
	public static ImBoolean MOD_MENU_OVERLAY = new ImBoolean(false);
	private static float OVERLAY_WIDTH = 500;
	private static float OVERLAY_HEIGHT = 0;

	public static void init(MainController main) {
		OVERLAY_HEIGHT = main.getConfig().getWindowHeight() + 20;
	}

	public static void show(ImBoolean showModMenu) {
		ImGui.setNextWindowPos(0, 0);
		ImGui.setNextWindowSize(OVERLAY_WIDTH, OVERLAY_HEIGHT);

		if (ImGui.begin("Mod Menu", showModMenu, ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoCollapse)) {
			Label.categoryLabel("Freq").render();
			FreqTrainerMenu.show(new ImBoolean(true));

			ImGui.newLine();

			Label.categoryLabel("Random").render();
			RandomTrainerMenu.show(new ImBoolean(true));

			for (int i = 0; i < 200; ++i) {
				ImGui.text("Dummy");
			}
		}
		ImGui.end();
	}
}
