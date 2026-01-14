package bms.player.beatoraja.modmenu.setting.widget;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;

/**
 * Tile is a container widget, which composes a 'name' and an 'action'. It's designed to display a configurable option,
 *  its illustration looks like:
 *
 * <pre>
 * +-------------------------------------+
 * |Name       |                |        |
 * |           |                |        |
 * |Description|                |action  |
 * |           |                |        |
 * |           |                |        |
 * +-------------------------------------+
 * </pre>
 *
 * The action is a 'SizedWidget', which is used to handle the user inputs like flagging the option or input some value.
 *  The reason that action is not a 'Widget' is because 'Tile' will try to align the action widget to the right.
 *  Therefore, we need to know the width of the action widget to calculate how many space we need to fill in the middle.
 * <br>
 * However, Tile itself doesn't serve any capability of sending value to action widget: it' only a container widget
 *  that serves for rendering. See 'TiledOption' for how to handle the value. This is also the reason why 'Action' is
 *  a SizedWidget rather than ActionWidget: Tile is only a container class that supports rendering.
 * <br>
 *
 * Additionally, you can attach some labels around the 'name' to help you display some hints for the option.
 * <br>
 * TODO: Description is not implemented yet....
 */
public class Tile implements Widget {
	private IconLabel nameIconLabelLeft;
	private final String name;
	private IconLabel nameIconLabelRight;
	private String description;
	private int width;
	private final SizedWidget action;

	public Tile(String name) {
		this(name, "", null);
	}

	public Tile(String name, SizedWidget action) {
		this(name, "", action);
	}

	public Tile(String name, String description) {
		this(name, description, null);
	}

	public Tile(String name, String description, SizedWidget action) {
		this.name = name == null ? "" : name;
		this.description = description == null ? "" : description;
		this.action = action;
	}

	public void addLeftIcon(IconLabel iconLabel) {
		this.nameIconLabelLeft = iconLabel;
	}

	public void addRightIcon(IconLabel iconLabel) {
		this.nameIconLabelRight = iconLabel;
	}

	@Override
	public void render() {
		ImGui.beginGroup();
		ImGui.pushStyleColor(ImGuiCol.ChildBg, 0.2f, 0.2f, 0.2f, 0.8f);

		if (ImGui.beginChild(this.name + "##Container", 0, 30, false)) {
			float textHeight = ImGui.calcTextSizeY(this.name) + (
//					(this.nameIconLabelLeft == null ? 0 : nameIconLabelLeft.getWidth()) +
//					(this.nameIconLabelRight == null ? 0 : nameIconLabelRight.getWidth())
					0
			);
			float windowHeight = ImGui.getWindowHeight();
			float textY = (windowHeight - textHeight) / 2;
			ImGui.setCursorPosY(textY);
			ImGui.setCursorPosX(ImGui.getCursorPosX() + 16F);
			if (this.nameIconLabelLeft != null) {
				this.nameIconLabelLeft.render();
				ImGui.sameLine();
			}
			ImGui.text(this.name);
			if (this.nameIconLabelRight != null) {
				ImGui.sameLine();
				this.nameIconLabelRight.render();
			}

			if (this.action != null) {
				ImGui.sameLine();
				float spacing = ImGui.getContentRegionAvailX() - action.getWidth() - 16F;
				if (spacing > 0) {
					ImGui.dummy(new ImVec2(spacing, 0F));
					ImGui.sameLine();
				}

				this.action.render();
			}
		}

		ImGui.popStyleColor();

		ImGui.endChild();
		ImGui.endGroup();
	}
}
