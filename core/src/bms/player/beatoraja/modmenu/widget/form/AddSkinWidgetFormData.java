package bms.player.beatoraja.modmenu.widget.form;

import bms.player.beatoraja.modmenu.widget.SkinResources;
import bms.player.beatoraja.skin.SkinObject;

public abstract class AddSkinWidgetFormData {
	protected SkinResources skinResources;

	public AddSkinWidgetFormData(SkinResources resources) {
		this.skinResources = resources;
	}

	abstract void render();

	abstract boolean isSubmittable();

	abstract SkinObject createSkinObject();

	public static AddSkinWidgetFormData createFormData(AddSkinWidgetForm.SkinWidgetType widgetType, SkinResources resources) {
		return switch (widgetType) {
			case Text -> new TextSkinWidgetFormData(resources);
			default -> null;
		};
	}
}
