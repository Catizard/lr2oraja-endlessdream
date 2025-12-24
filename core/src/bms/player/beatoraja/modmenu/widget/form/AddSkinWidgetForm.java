package bms.player.beatoraja.modmenu.widget.form;

import bms.player.beatoraja.modmenu.widget.SkinResources;
import bms.player.beatoraja.skin.Skin;
import bms.player.beatoraja.skin.SkinObject;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;

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

	// Form data
	private final AddSkinWidgetFormData formData;
	// Tell the outside to add the skin object
	private final Consumer<SkinObject> addSkinObjectHook;
	// Tell the outside close this form
	private final Runnable closeHook;

	public AddSkinWidgetForm(SkinWidgetType widgetType, SkinResources resources, Consumer<SkinObject> addSkinObjectHook, Runnable closeHook) {
		// The adding widget's type
		this.formData = AddSkinWidgetFormData.createFormData(widgetType, resources);
		// Current skin
		this.addSkinObjectHook = addSkinObjectHook;
		this.closeHook = closeHook;
	}

	public void render() {
		if (ImGui.begin("AddSkinWidgetForm", ImGuiWindowFlags.AlwaysAutoResize)) {
			formData.render();
			if (ImGui.button("Cancel##AddSkinWidgetForm")) {
				closeHook.run();
			}
			ImGui.sameLine();
			ImGui.beginDisabled(!formData.isSubmittable());
			if (ImGui.button("Submit##AddSkinWidgetForm")) {
				addSkinObjectHook.accept(formData.createSkinObject());
				closeHook.run();
			}
			ImGui.endDisabled();
		}
		ImGui.end();
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
