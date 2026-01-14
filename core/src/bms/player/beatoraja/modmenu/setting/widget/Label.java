package bms.player.beatoraja.modmenu.setting.widget;

import bms.player.beatoraja.modmenu.ImGuiRenderer;
import imgui.ImColor;
import imgui.ImFont;
import imgui.ImGui;
import imgui.flag.ImGuiCol;

/**
 * Label is a simple text widget
 *
 * @implNote Imgui doesn't support dynamic font until 1.92, so it's not easy to change the font size dynamically
 *  here. Instead, we hardcoded the font data in ImguiRenderer :/
 */
public class Label implements SizedWidget {
	private final String hint;
	private final float hintWidth;
	public static final float DefaultHintWidth = 160F;
	private final String name;
	private final int color;
	private final ImFont font;
	public static final int DefaultColor = ImColor.rgb(0, 0, 0);

	public Label(String name) {
		this(name, null, DefaultHintWidth, DefaultColor);
	}

	public Label(String name, String hint, float hintWidth, int color) {
		this(name, hint, null, hintWidth, color);
	}

	public Label(String name, String hint, ImFont font, float hintWidth, int color) {
		this.name = name;
		this.hint = hint;
		this.font = font;
		this.hintWidth = hintWidth;
		this.color = color;
	}

	public static Label categoryLabel(String name) {
		return new Label(name, null, ImGuiRenderer.font24, DefaultHintWidth, ImColor.rgb(0, 128, 128));
	}

	@Override
	public float getWidth() {
		return ImGui.calcTextSizeX(this.name);
	}

	@Override
	public void render() {
		if (color != DefaultColor) {
			ImGui.pushStyleColor(ImGuiCol.Text, color);
		}
		if (font != null) {
			ImGui.pushFont(font);
		}
		ImGui.text(this.name);
		if (this.hint != null) {
			ImGui.setNextWindowSize(hintWidth, 0F);
			if (ImGui.beginItemTooltip()) {
				ImGui.textWrapped(this.hint);
				ImGui.endTooltip();
			}
		}
		if (font != null) {
			ImGui.popFont();
		}
		if (color != DefaultColor) {
			ImGui.popStyleColor();
		}
	}
}
