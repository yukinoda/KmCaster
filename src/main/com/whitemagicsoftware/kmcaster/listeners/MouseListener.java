package com.whitemagicsoftware.kmcaster.listeners;

import com.whitemagicsoftware.kmcaster.HardwareSwitch;
import com.whitemagicsoftware.kmcaster.util.Pair;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;
import org.jnativehook.mouse.NativeMouseWheelEvent;
import org.jnativehook.mouse.NativeMouseWheelListener;

import java.util.HashMap;
import java.util.Map;

import static com.whitemagicsoftware.kmcaster.HardwareSwitch.*;
import static java.util.Map.entry;
import static org.jnativehook.mouse.NativeMouseWheelEvent.WHEEL_HORIZONTAL_DIRECTION;
import static org.jnativehook.mouse.NativeMouseWheelEvent.WHEEL_VERTICAL_DIRECTION;

/**
 * Listens for all mouse events: clicks and mouse wheel scrolls.
 */
public final class MouseListener
    extends PropertyDispatcher<HardwareSwitch>
    implements NativeMouseInputListener, NativeMouseWheelListener {

  private final static Map<Pair<Integer, Integer>, HardwareSwitch>
      SCROLL_CODES = Map.ofEntries(
      entry( new Pair<>( WHEEL_VERTICAL_DIRECTION, -1 ), MOUSE_SCROLL_U ),
      entry( new Pair<>( WHEEL_VERTICAL_DIRECTION, 1 ), MOUSE_SCROLL_D ),
      entry( new Pair<>( WHEEL_HORIZONTAL_DIRECTION, -1 ), MOUSE_SCROLL_L ),
      entry( new Pair<>( WHEEL_HORIZONTAL_DIRECTION, 1 ), MOUSE_SCROLL_R )
  );

  /**
   * Most recently pressed non-mapped button value, empty signifies release.
   */
  private String mExtra = "";

  /**
   * Stores the state of button presses. The contents of the map reflect the
   * state of each switch, so the reference can be final but not its contents.
   */
  private final Map<HardwareSwitch, Boolean> mSwitches = new HashMap<>();

  /**
   * Initializes the mouse switches to a released state.
   */
  public MouseListener() {
    for( final var key : mouseSwitches() ) {
      mSwitches.put( key, false );
    }
  }

  public void nativeMousePressed( final NativeMouseEvent e ) {
    dispatchButtonEvent( e, true );
  }

  public void nativeMouseReleased( final NativeMouseEvent e ) {
    dispatchButtonEvent( e, false );
  }

  public void nativeMouseWheelMoved( final NativeMouseWheelEvent e ) {
    final var pair = new Pair<>( e.getWheelDirection(), e.getWheelRotation() );
    final var scrollSwitch = SCROLL_CODES.get( pair );

    for( final var hwSwitch : scrollSwitches() ) {
      if( mSwitches.get( hwSwitch ) ) {
        tryFire( hwSwitch, true, false );
        mSwitches.put( hwSwitch, false );
      }
    }

    tryFire( scrollSwitch, mSwitches.get( scrollSwitch ), true );
    mSwitches.put( scrollSwitch, true );
  }

  /**
   * Called to send a mouse event to all listeners.
   *
   * @param e       The mouse event that was most recently triggered.
   * @param pressed {@code true} means pressed, {@code false} means released.
   */
  private void dispatchButtonEvent(
      final NativeMouseEvent e, final boolean pressed ) {
    final var hwSwitch = getMouseSwitch( e );

    // Percolate the button number as a string for any undefined (unmapped)
    // mouse buttons that are clicked. This enables additional mouse
    // buttons beyond two to appear, without an image representation.
    if( hwSwitch == MOUSE_EXTRA ) {
      final var button = Integer.toString( e.getButton() );
      final var n = pressed ? button : "";
      final var o = pressed ? mExtra : button;

      fire( hwSwitch, o, n );
      mExtra = n;
    }
    else {
      tryFire( hwSwitch, mSwitches.get( hwSwitch ), pressed );
    }

    mSwitches.put( hwSwitch, pressed );
  }

  private HardwareSwitch getMouseSwitch( final NativeMouseEvent e ) {
    final var button = e.getButton();

    return switch( button ) {
      case 1, 2, 3 -> HardwareSwitch.valueFrom( Integer.toString( button ) );
      default -> MOUSE_EXTRA;
    };
  }

  /**
   * Unused.
   *
   * @param e Ignored.
   */
  public void nativeMouseClicked( final NativeMouseEvent e ) {
  }

  /**
   * Unused.
   *
   * @param e Ignored.
   */
  public void nativeMouseMoved( final NativeMouseEvent e ) {
  }

  /**
   * Unused.
   *
   * @param e Ignored.
   */
  public void nativeMouseDragged( final NativeMouseEvent e ) {
  }
}
