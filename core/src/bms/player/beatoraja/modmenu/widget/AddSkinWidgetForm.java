package bms.player.beatoraja.modmenu.widget;

import bms.player.beatoraja.MainState;
import bms.player.beatoraja.modmenu.ImGuiNotify;
import bms.player.beatoraja.skin.Skin;
import bms.player.beatoraja.skin.SkinObject;
import bms.player.beatoraja.skin.SkinText;
import bms.player.beatoraja.skin.json.JsonSkin;
import bms.player.beatoraja.skin.json.JsonSkinObjectLoader;
import bms.player.beatoraja.skin.property.BooleanProperty;
import bms.tool.util.Pair;
import imgui.ImGui;

import java.util.function.Consumer;

public class AddSkinWidgetForm {
	public enum SkinWidgetType {
		Text(false),
		Image(true),
		Float(true),
		Number(false);

		private final boolean disabled;

		SkinWidgetType(boolean disabled) {
			this.disabled = disabled;
		}

		public boolean isDisabled() {
			return disabled;
		}
	}

	private final SkinWidgetType widgetType;
	private final Skin skin;
	private final Consumer<SkinObject> addSkinObjectHook;

	public AddSkinWidgetForm(SkinWidgetType widgetType, Skin skin, Consumer<SkinObject> addSkinObjectHook) {
		this.widgetType = widgetType;
		this.skin = skin;
		this.addSkinObjectHook = addSkinObjectHook;
	}

	public void render() {
		JsonSkinObjectLoader objectLoader = skin.getObjectLoader();
		JsonSkin.Text text = new JsonSkin.Text();
		text.font = "font_songlist";
		text.size = 30;
		text.constantText = "I'm an object added in-game LOLOLOL";
		SkinText testObj = objectLoader.createText(text, skin.header.getPath());
		testObj.setName("TEST1234");
		JsonSkin.Destination dst = new JsonSkin.Destination();
		dst.dst = new JsonSkin.Animation[]{
				new JsonSkin.Animation() {{
					x = 229;
					y = 417;
				}}
		};
		dst.draw = new BooleanProperty() {
			@Override
			public boolean isStatic(MainState state) {
				return true;
			}

			@Override
			public boolean get(MainState state) {
				return true;
			}
		};
		objectLoader.getSkinLoader().setDestination(skin, testObj, dst);
		addSkinObjectHook.accept(testObj);
		ImGuiNotify.info("Added. Now crash!");
	}

	public static SkinWidgetType renderSelectableList() {
		for (SkinWidgetType widgetType : SkinWidgetType.values()) {
			String typeName = widgetType.name();
			boolean disabled = widgetType.isDisabled();
			if (disabled) {
				ImGui.beginDisabled();
			}
			if (ImGui.selectable(typeName)) {
				return widgetType;
			}
			if (disabled) {
				ImGui.endDisabled();
			}
		}
		return null;
	}
}
