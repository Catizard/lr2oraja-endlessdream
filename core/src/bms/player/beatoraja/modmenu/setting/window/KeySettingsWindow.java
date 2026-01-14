package bms.player.beatoraja.modmenu.setting.window;

import bms.player.beatoraja.Config;
import bms.player.beatoraja.PlayerConfig;
import bms.player.beatoraja.modmenu.FontAwesomeIcons;
import bms.player.beatoraja.modmenu.ImGuiKeyHelper;
import bms.player.beatoraja.modmenu.setting.SettingMenu;
import com.badlogic.gdx.Input;
import imgui.ImColor;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiTableColumnFlags;
import imgui.flag.ImGuiTableFlags;
import imgui.type.ImBoolean;

public class KeySettingsWindow extends BaseSettingWindow {
	private int[] previousKeys;
	private int[] keys;
	private int currentEditing = 0;
	private final ImBoolean editing = new ImBoolean(false);

	public KeySettingsWindow(Config config, PlayerConfig playerConfig) {
		super(config, playerConfig);
	}

	@Override
	public String getName() {
		return "Key";
	}

	@Override
	public void render() {
		ImGui.beginDisabled(editing.get());
		SettingMenu.currentPlayModeSelect.render();
		ImGui.endDisabled();
		if (ImGui.checkbox("Edit##KeySettings", editing)) {
			if (editing.get()) {
				// From not editing to editing
				System.arraycopy(keys, 0, previousKeys, 0, keys.length);
			} else {
				// From editing to not editing
				resetEditingState();
			}
		}
		if (ImGui.beginTable("##KeySettingsMenuKeyTable", keys.length, ImGuiTableFlags.SizingFixedFit)) {
			for (int i = 0; i < keys.length;++i) {
				ImGui.tableSetupColumn("" + i, 50, ImGuiTableColumnFlags.WidthFixed);
			}
			ImGui.tableNextRow();
			for (int i = 0; i < keys.length;++i) {
				if (i == currentEditing) {
					ImGui.tableSetColumnIndex(i);
					float width = ImGui.getColumnWidth();
					float arrowWidth = ImGui.calcTextSizeX(FontAwesomeIcons.ArrowDown);
					ImGui.setCursorPosX(ImGui.getCursorPosX() + (width - arrowWidth) * 0.5F);
					ImGui.text(FontAwesomeIcons.ArrowDown);
				}
			}
			ImGui.tableNextRow();
			for (int i = 0; i < keys.length; ++i) {
				ImGui.pushID(i);
				ImGui.tableSetColumnIndex(i);
				if (i == keys.length - 2 || i == keys.length - 1) {
					ImGui.pushStyleColor(ImGuiCol.Button, ImColor.rgb(125   ,0,0));
					ImGui.pushStyleColor(ImGuiCol.Text, ImColor.rgb(230,230,230));
				} else if (i % 2 == 0) {
					ImGui.pushStyleColor(ImGuiCol.Button, ImColor.rgb(0,0,139));
					ImGui.pushStyleColor(ImGuiCol.Text, ImColor.rgb(230,230,230));
				} else {
					ImGui.pushStyleColor(ImGuiCol.Button, ImColor.rgb(230,230,230));
					ImGui.pushStyleColor(ImGuiCol.Text, ImColor.rgb(49,49,49));
				}
				ImGui.button(Input.Keys.toString(keys[i]), 50, 80);
				if (i != keys.length - 1) {
					ImGui.sameLine();
				}
				ImGui.popStyleColor(2);
				ImGui.popID();
			}
			ImGui.endTable();
		}
		if (editing.get()) {
			int lastPressedKey = ImGuiKeyHelper.getLastPressedKey();
			if (lastPressedKey != -1) {
				if (lastPressedKey == Input.Keys.ESCAPE) {
					// Escaping before every key has been set, rollback the changes
					resetEditingState();
					return ;
				}
				keys[currentEditing] = lastPressedKey;
				currentEditing++;
			}
			if (currentEditing == keys.length) {
				currentEditing = 0;
				editing.set(false);
			}
		}
	}

	@Override
	public void refresh() {
		keys = getPlayModeConfig().getKeyboardConfig().getKeyAssign();
		previousKeys = new int[keys.length];
	}

	private void resetEditingState() {
		System.arraycopy(previousKeys, 0, keys, 0, keys.length);
		currentEditing = 0;
		editing.set(false);
	}
}
