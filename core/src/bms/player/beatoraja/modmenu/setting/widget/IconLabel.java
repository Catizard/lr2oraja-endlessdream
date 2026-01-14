package bms.player.beatoraja.modmenu.setting.widget;


import bms.player.beatoraja.modmenu.FontAwesomeIcons;
import imgui.ImColor;

/**
 * IconLabel is a special variant of Label that only renders a single character
 */
public class IconLabel extends Label {
	public IconLabel(String icon, String hint, int color) {
		this(icon, hint, DefaultHintWidth, color);
	}

	public IconLabel(String icon, String hint, float hintWidth, int color) {
		super(icon, hint, null, hintWidth, color);
		if (icon.length() != 1) {
			throw new IllegalArgumentException("IconLabel's icon length must be 1");
		}
	}

	public static IconLabel question(String hint) {
		return new IconLabel(FontAwesomeIcons.Question, hint, Label.DefaultColor);
	}

	public static IconLabel assist() {
		return assist("Your play would be restricted to assist clear if this option is flagged");
	}

	public static IconLabel assist(String hint) {
		return new IconLabel(FontAwesomeIcons.Child, hint, ImColor.rgb("#FF9FF9"));
	}

	public static IconLabel assist(String hint, float hintWidth) {
		return new IconLabel(FontAwesomeIcons.Child, hint, hintWidth, ImColor.rgb("#FF9FF9"));
	}
}
