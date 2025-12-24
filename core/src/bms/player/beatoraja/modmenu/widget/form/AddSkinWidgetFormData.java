package bms.player.beatoraja.modmenu.widget.form;

import bms.player.beatoraja.skin.Skin;
import bms.player.beatoraja.skin.SkinObject;

public abstract class AddSkinWidgetFormData {
	protected Skin skin;

	public AddSkinWidgetFormData(Skin skin) {
		this.skin = skin;
	}

	abstract void render();

	abstract boolean isSubmittable();

	abstract SkinObject createSkinObject();

	public static AddSkinWidgetFormData createFormData(AddSkinWidgetForm.SkinWidgetType widgetType, Skin skin) {
		return switch (widgetType) {
			case Text -> new TextSkinWidgetFormData(skin);
			default -> null;
		};
	}
}
