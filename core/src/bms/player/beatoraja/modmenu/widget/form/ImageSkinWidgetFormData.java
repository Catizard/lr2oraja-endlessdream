package bms.player.beatoraja.modmenu.widget.form;

import bms.player.beatoraja.MainState;
import bms.player.beatoraja.modmenu.widget.SkinResources;
import bms.player.beatoraja.skin.SkinImage;
import bms.player.beatoraja.skin.SkinObject;
import bms.player.beatoraja.skin.json.JSONSkinLoader;
import bms.player.beatoraja.skin.json.JsonSkin;
import bms.player.beatoraja.skin.property.BooleanProperty;
import bms.tool.util.Pair;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import imgui.ImGui;
import imgui.type.ImInt;
import imgui.type.ImString;

import java.util.List;

public class ImageSkinWidgetFormData extends AddSkinWidgetFormData {
	private final ImString widgetName = new ImString(128);
	private final ImInt widgetX = new ImInt(0);
	private final ImInt widgetY = new ImInt(0);
	private List<String> imageSources;
	private String chosenImageSource;
	private JSONSkinLoader.SourceData chosenSourceData;

	public ImageSkinWidgetFormData(SkinResources resources) {
		super(resources);
		widgetName.clear();
		widgetX.set(0);
		widgetY.set(0);
		imageSources = Pair.projectFirst(resources.imageSources());
		chosenImageSource = imageSources.get(0);
		chosenSourceData = null;
	}

	@Override
	boolean isSubmittable() {
		return !widgetName.isEmpty() && !chosenImageSource.isEmpty();
	}

	@Override
	void render() {
		ImGui.inputText("Name##ImageSkinWidgetFormData", widgetName);
		if (ImGui.beginCombo("ImageSourceCombo##ImageSkinWidgetFormData", chosenImageSource)) {
			for (String imageSource : imageSources) {
				if (ImGui.selectable(imageSource)) {
					chosenImageSource = imageSource;
					chosenSourceData = skinResources.imageSources().stream()
							.filter(p -> p.getFirst().equals(chosenImageSource))
							.findAny()
							.get()
							.getSecond();
				}
			}
			ImGui.endCombo();
		}
		ImGui.inputInt("x##ImageSkinWidgetFormData", widgetX);
		ImGui.inputInt("y##ImageSkinWidgetFormData", widgetY);
	}

	@Override
	SkinObject createSkinObject() {
		TextureRegion[] images = skinResources.skinLoader().getObjectLoader().getSourceImage(((Texture) chosenSourceData.data), 0, 0, -1, -1, 1, 1);
		SkinImage obj = new SkinImage(images, 0, 0);
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
		skinResources.skinLoader().setDestination(skinResources.skinRef(), obj, dst);
		return obj;
	}
}
