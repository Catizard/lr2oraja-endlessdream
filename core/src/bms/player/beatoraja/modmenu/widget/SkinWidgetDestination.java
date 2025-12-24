package bms.player.beatoraja.modmenu.widget;

import bms.player.beatoraja.modmenu.ImGuiNotify;
import bms.player.beatoraja.skin.SkinObject;

import java.util.function.Consumer;

/**
 * A simple wrapper class of SkinObject.SkinObjectDestination
 *
 * @implNote setter functions must provide an extra argument to not trigger event system
 */
public class SkinWidgetDestination {
	private static final double eps = 1e-5;

	private final String name;
	private final SkinObject.SkinObjectDestination destination;
	private final Consumer<Event<?>> pushEventHook;

	public enum MovingState {
		NONE, // Haven't started moving
		STALE, // User enabled the move feature, but hasn't moved around yet
		MOVED, // User has already moved the widget
	}

	private SkinObject.SkinObjectDestination beforeMove = null;
	private MovingState movingState;

	public SkinWidgetDestination(String name, SkinObject.SkinObjectDestination destination, Consumer<Event<?>> pushEventHook) {
		this.name = name;
		this.destination = destination;
		this.pushEventHook = pushEventHook;
		this.movingState = MovingState.NONE;
	}

	public String getName() {
		return name;
	}

	public MovingState getMovingState() {
		return movingState;
	}

	public void setMovingState(MovingState movingState) {
		this.movingState = movingState;
	}

	public SkinObject.SkinObjectDestination getBeforeMove() {
		return beforeMove;
	}

	public void setBeforeMove(SkinObject.SkinObjectDestination beforeMove) {
		this.beforeMove = beforeMove;
	}

	public float getDstX() {
		return destination.region.x;
	}

	public float getDstY() {
		return destination.region.y;
	}

	public float getDstW() {
		return destination.region.width;
	}

	public float getDstH() {
		return destination.region.height;
	}

	public void setDstX(float x) {
		setDstX(x, true);
	}

	public void setDstX(float x, boolean createEvent) {
		float previous = this.getDstX();
		if (createEvent && Math.abs(x - previous) > eps) {
			pushEventHook.accept(Event.createChangeSingleFieldEvent(EventType.CHANGE_X, this, previous, x));
		}
		destination.region.x = x;
	}

	public void setDstY(float y) {
		setDstY(y, true);
	}

	public void setDstY(float y, boolean createEvent) {
		float previous = this.getDstY();
		if (createEvent && Math.abs(y - previous) > eps) {
			pushEventHook.accept(Event.createChangeSingleFieldEvent(EventType.CHANGE_Y, this, previous, y));
		}
		destination.region.y = y;
	}

	public void setDstW(float w) {
		setDstW(w, true);
	}

	public void setDstW(float w, boolean createEvent) {
		float previous = this.getDstW();
		if (createEvent && Math.abs(w - previous) > eps) {
			pushEventHook.accept( Event.createChangeSingleFieldEvent(EventType.CHANGE_W, this, previous, w));
		}
		destination.region.width = w;
	}

	public void setDstH(float h) {
		setDstH(h, true);
	}

	public void setDstH(float h, boolean createEvent) {
		float previous = this.getDstH();
		if (createEvent && Math.abs(h - previous) > eps) {
			pushEventHook.accept(Event.createChangeSingleFieldEvent(EventType.CHANGE_H, this, previous, h));
		}
		destination.region.height = h;
	}

	/**
	 * Submit the move result, producing the event
	 */
	public void submitMovement() {
		if (beforeMove == null) {
			ImGuiNotify.error("Cannot submit the move result because there's no original position");
			return ;
		}
		float nextX = getDstX();
		float nextY = getDstY();
		float nextW = getDstW();
		float nextH = getDstH();
		// Reset the position, to mimic that we are never left the original position
		setDstX(beforeMove.region.x, false);
		setDstY(beforeMove.region.y, false);
		setDstW(beforeMove.region.width, false);
		setDstH(beforeMove.region.height, false);
		// Truly move to the target position
		setDstX(nextX);
		setDstY(nextY);
		setDstW(nextW);
		setDstH(nextH);
		beforeMove = null;
	}
}
