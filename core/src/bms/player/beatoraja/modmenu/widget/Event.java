package bms.player.beatoraja.modmenu.widget;

/**
 * An event is a record of operation that's happened on type T
 *
 * @implNote To create event, use factory methods. This design is because outside doesn't need to know what an actual
 *  event sub-class looks like, what fields is inside. See SkinWidgetManager for its usages.
 */
public abstract class Event<T> {
	protected EventType type;
	protected T handle; // reference to the event object

	public Event(EventType type, T handle) {
		this.type = type;
		this.handle = handle;
	}

	public abstract void undo();

	public abstract String getDescription();

	public abstract String getName();

	// Factory methods

	public static Event<SkinWidget> createToggleVisibleEvent(SkinWidget skinWidget, boolean isVisibleBefore) {
		return new ToggleVisibleEvent(skinWidget, isVisibleBefore);
	}

	public static Event<SkinWidgetDestination> createChangeSingleFieldEvent(EventType type, SkinWidgetDestination destination, float prev, float next) {
		return new ChangeSingleFieldEvent(type, destination, prev, next);
	}

	// Events implementation

	/**
	 * Records the event when changing a single field from a widget
	 */
	private static class ChangeSingleFieldEvent extends Event<SkinWidgetDestination> {
		private final float previous;
		private final float current;

		public ChangeSingleFieldEvent(EventType type, SkinWidgetDestination dst, float previous, float current) {
			super(type, dst);
			this.previous = previous;
			this.current = current;
		}

		@Override
		public void undo() {
			switch (type) {
				case CHANGE_X -> handle.setDstX(previous, false);
				case CHANGE_Y -> handle.setDstY(previous, false);
				case CHANGE_W -> handle.setDstW(previous, false);
				case CHANGE_H -> handle.setDstH(previous, false);
				default -> { /* Intentionally do nothing */ }
			}
		}

		@Override
		public String getDescription() {
			String fieldName = switch (type) {
				case CHANGE_X -> "x";
				case CHANGE_Y -> "y";
				case CHANGE_W -> "width";
				case CHANGE_H -> "height";
				default -> "[ERROR] Not a ChangeSingleFieldEvent";
			};
			return String.format("Changed %s's %s from %.4f to %.4f", handle.getName(), fieldName, previous, current);
		}

		@Override
		public String getName() {
			return handle.getName();
		}
	}

	/**
	 * Records the event when toggling the visibility of a widget
	 */
	private static class ToggleVisibleEvent extends Event<SkinWidget> {
		private final boolean isVisibleBefore;

		public ToggleVisibleEvent(SkinWidget handle, boolean isVisibleBefore) {
			super(EventType.TOGGLE_VISIBLE, handle);
			this.isVisibleBefore = isVisibleBefore;
		}

		@Override
		public void undo() {
			handle.toggleVisible(false);
		}

		@Override
		public String getDescription() {
			return isVisibleBefore
					? String.format("Make %s widget invisible", handle.getName())
					: String.format("Make %s widget visible", handle.getName());
		}

		@Override
		public String getName() {
			return handle.getName();
		}
	}
}
