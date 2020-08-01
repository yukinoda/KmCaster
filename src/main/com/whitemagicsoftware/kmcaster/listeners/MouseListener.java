package com.whitemagicsoftware.kmcaster.listeners;

import com.whitemagicsoftware.kmcaster.HardwareSwitch;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;
import org.jnativehook.mouse.NativeMouseWheelEvent;
import org.jnativehook.mouse.NativeMouseWheelListener;

import java.util.HashMap;
import java.util.Map;

import static com.whitemagicsoftware.kmcaster.HardwareSwitch.*;

/**
 * Listens for all mouse events: clicks and mouse wheel scrolls.
 */
public final class MouseListener
    extends PropertyDispatcher<HardwareSwitch>
    implements NativeMouseInputListener, NativeMouseWheelListener {

  /**
   * Stores the state of button presses. The contents of the map reflect the
   * state of each switch, so the reference can be final but not its contents.
   */
  private final Map<HardwareSwitch, Boolean> mSwitches = new HashMap<>();

  public MouseListener() {
    mSwitches.put( MOUSE_LEFT, false );
    mSwitches.put( MOUSE_MIDDLE, false );
    mSwitches.put( MOUSE_RIGHT, false );
    mSwitches.put( MOUSE_REGULAR, false );
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
    try {
      final var id = Integer.toString( e.getButton() );
      final var hwSwitch = HardwareSwitch.valueFrom( id );

      tryFire( hwSwitch, mSwitches.get( hwSwitch ), pressed );
      mSwitches.put( hwSwitch, pressed );
    } catch( final Exception ex ) {
      // The mouse button wasn't found. This means that there is no visual
      // representation for the button, so pass up the generic brand instead.
      //tryFire( MOUSE_REGULAR, mSwitches.get( MOUSE_REGULAR ), pressed );
    }
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
