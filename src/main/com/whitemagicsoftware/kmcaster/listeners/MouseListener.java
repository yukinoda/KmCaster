/*
 * Copyright 2020 White Magic Software, Ltd.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
