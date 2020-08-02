package com.whitemagicsoftware.kmcaster.listeners;

import com.whitemagicsoftware.kmcaster.HardwareSwitch;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;
import org.jnativehook.mouse.NativeMouseWheelEvent;
import org.jnativehook.mouse.NativeMouseWheelListener;

import java.util.HashMap;
import java.util.Map;

import static com.whitemagicsoftware.kmcaster.HardwareSwitch.MOUSE_UNDEFINED;
import static com.whitemagicsoftware.kmcaster.HardwareSwitch.mouseSwitches;
import static java.util.Map.entry;

/**
 * Listens for all mouse events: clicks and mouse wheel scrolls.
 */
public final class MouseListener
    extends PropertyDispatcher<HardwareSwitch>
    implements NativeMouseInputListener, NativeMouseWheelListener {

  private final static Map<Integer, String> SCROLL_CODES =
      Map.ofEntries(
          entry( 1, "↑" ),
          entry( -1, "↓" )
      );

  /**
   * Stores the state of button presses. The contents of the map reflect the
   * state of each switch, so the reference can be final but not its contents.
   */
  private final Map<HardwareSwitch, Boolean> mSwitches = new HashMap<>();

  public MouseListener() {
    for( final var key : mouseSwitches() ) {
      mSwitches.put( key, false );
    }
  }

  public void nativeMousePressed( final NativeMouseEvent e ) {
    dispatchMouseEvent( e, true );
  }

  public void nativeMouseReleased( final NativeMouseEvent e ) {
    dispatchMouseEvent( e, false );
  }

  public void nativeMouseWheelMoved( final NativeMouseWheelEvent e ) {
    //dispatchMouseEvent( e, e.getWheelRotation() );
  }

  /**
   * Called to send a mouse event to all listeners.
   *
   * @param e       The mouse event that was most recently triggered.
   * @param pressed {@code true} means pressed, {@code false} means released.
   */
  private void dispatchMouseEvent(
      final NativeMouseEvent e, final boolean pressed ) {
    final var hwSwitch = getMouseSwitch( e );

    // Percolate the button number as a string for any undefined (unmapped)
    // mouse buttons that are clicked. This enables additional mouse
    // buttons beyond two to appear, without an image representation.
    if( hwSwitch == MOUSE_UNDEFINED ) {
      final var button = Integer.toString( e.getButton() );
      final var n = pressed ? button : "";
      final var o = pressed ? "" : button;

      fire( hwSwitch, o, n );
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
      default -> MOUSE_UNDEFINED;
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
