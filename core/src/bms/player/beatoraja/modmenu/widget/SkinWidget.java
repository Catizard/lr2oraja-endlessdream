package bms.player.beatoraja.modmenu.widget;

import bms.player.beatoraja.skin.SkinObject;

import java.util.List;
import java.util.function.Consumer;

/**
 * A simple wrapper class of SkinObject
 *
 * @implNote setter functions must provide an extra argument to not trigger event system
 */
public class SkinWidget {
	private final String name;
	// DON'T ACCESS THESE FIELDS DIRECTLY, USE GETTER/SETTER INSTEAD
	private final SkinObject skinObject;
	private final List<SkinWidgetDestination> destinations;
	private final Consumer<Event<SkinWidget>> pushEventHook;

	public SkinWidget(String name, SkinObject skinObject, List<SkinWidgetDestination> destinations, Consumer<Event<SkinWidget>> pushEventHook) {
		this.name = name;
		this.skinObject = skinObject;
		this.destinations = destinations;
		this.pushEventHook = pushEventHook;
	}

	public String getName() {
		return name;
	}

	public List<SkinWidgetDestination> getDestinations() {
		return destinations;
	}

	public boolean isDrawingOnScreen() {
		return skinObject.draw && skinObject.visible;
	}

	public void toggleVisible() {
		toggleVisible(true);
	}

	public void toggleVisible(boolean createEvent) {
		boolean isVisibleBefore = skinObject.visible;
		if (createEvent) {
			pushEventHook.accept(Event.createToggleVisibleEvent(this, isVisibleBefore));
		}
		skinObject.visible = !isVisibleBefore;
	}
}