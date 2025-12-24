package bms.player.beatoraja.modmenu.widget.form;

import bms.player.beatoraja.MainState;
import bms.player.beatoraja.skin.Skin;
import bms.player.beatoraja.skin.SkinObject;
import bms.player.beatoraja.skin.SkinText;
import bms.player.beatoraja.skin.json.JsonSkin;
import bms.player.beatoraja.skin.json.JsonSkinObjectLoader;
import bms.player.beatoraja.skin.property.BooleanProperty;
import imgui.ImGui;
import imgui.type.ImInt;
import imgui.type.ImString;

import java.util.List;

public class TextSkinWidgetFormData extends AddSkinWidgetFormData {
	private static final ImString widgetName = new ImString(128);
	private static final ImString widgetContent = new ImString(128);
	private static final ImInt widgetX = new ImInt(0);
	private static final ImInt widgetY = new ImInt(0);
	private static final ImInt fontSize = new ImInt(0);

	private List<String> fontCandidates;
	private String fontName;

	public TextSkinWidgetFormData(Skin skin) {
		super(skin);
		widgetName.clear();
		widgetContent.clear();
		widgetX.set(0);
		widgetY.set(0);
		fontSize.set(0);
		// TODO: Handle no font to use case
		fontCandidates = skin.getObjectLoader().getSkinLoader().getSkinFonts().stream().map(font -> font.id).toList();
		fontName = fontCandidates.get(0);
	}

	@Override
	public boolean isSubmittable() {
		return !widgetName.isEmpty()
				&& !widgetContent.isEmpty()
				&& !fontName.isEmpty()
				&& fontSize.get() != 0;
	}

	@Override
	void render() {
		ImGui.inputText("Name##TextSkinWidgetFormData", widgetName);
		ImGui.inputText("Content##TextSkinWidgetFormData", widgetContent);
		if (ImGui.beginCombo("FontCombo##TextSkinWidgetFormData", fontName)) {
			for (String font : fontCandidates) {
				if (ImGui.selectable(font)) {
					fontName = font;
				}
			}
			ImGui.endCombo();
		}
		ImGui.inputInt("size##TextSkinWidgetFormData", fontSize);
		ImGui.inputInt("x##TextSkinWidgetFormData", widgetX);
		ImGui.inputInt("y##TextSkinWidgetFormData", widgetY);
	}

	@Override
	public SkinObject createSkinObject() {
		JsonSkinObjectLoader objectLoader = skin.getObjectLoader();
		JsonSkin.Text text = new JsonSkin.Text();
		text.font = fontName;
		text.size = fontSize.get();
		text.constantText = widgetContent.get();
		SkinText obj = objectLoader.createText(text, skin.header.getPath());
		obj.setName(widgetName.get());
		JsonSkin.Destination dst = new JsonSkin.Destination();
		dst.dst = new JsonSkin.Animation[]{
				new JsonSkin.Animation() {{
					x = widgetX.get();
					y = widgetY.get();
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
		objectLoader.getSkinLoader().setDestination(skin, obj, dst);
		return obj;
	}
}
