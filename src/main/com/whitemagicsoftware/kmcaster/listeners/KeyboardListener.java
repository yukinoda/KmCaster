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
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;

import static com.whitemagicsoftware.kmcaster.HardwareSwitch.*;
import static java.util.Map.entry;
import static java.util.Optional.ofNullable;
import static org.jnativehook.keyboard.NativeKeyEvent.getKeyText;

/**
 * Responsible for sending property change events for keyboard state changes.
 */
public final class KeyboardListener
    extends PropertyDispatcher<HardwareSwitch>
    implements NativeKeyListener {
  private final static String KEY_SPACE = "Space";
  private final static String KEY_BACKSPACE = "Back ⌫";
  private final static String KEY_TAB = "Tab ↹";
  private final static String KEY_ENTER = "Enter ⏎";

  /**
   * The key is the raw key code return from the {@link NativeKeyEvent}, the
   * value is the human-readable text to display on screen.
   */
  @SuppressWarnings("JavacQuirks")
  private final static Map<Integer, String> KEY_CODES =
      Map.ofEntries(
          entry( 32, KEY_SPACE ),
          entry( 33, "!" ),
          entry( 34, "\"" ),
          entry( 35, "#" ),
          entry( 36, "$" ),
          entry( 37, "%" ),
          entry( 38, "&" ),
          entry( 39, "'" ),
          entry( 40, "(" ),
          entry( 41, ")" ),
          entry( 42, "*" ),
          entry( 43, "+" ),
          entry( 44, "," ),
          entry( 45, "-" ),
          entry( 46, "." ),
          entry( 47, "/" ),
          entry( 58, ":" ),
          entry( 59, ";" ),
          entry( 60, "<" ),
          entry( 61, "=" ),
          entry( 62, ">" ),
          entry( 63, "?" ),
          entry( 64, "@" ),
          entry( 91, "[" ),
          entry( 92, "\\" ),
          entry( 93, "]" ),
          entry( 94, "^" ),
          entry( 95, "_" ),
          entry( 96, "`" ),
          entry( 97, "a" ),
          entry( 98, "b" ),
          entry( 99, "c" ),
          entry( 100, "d" ),
          entry( 101, "e" ),
          entry( 102, "f" ),
          entry( 103, "g" ),
          entry( 104, "h" ),
          entry( 105, "i" ),
          entry( 106, "j" ),
          entry( 107, "k" ),
          entry( 108, "l" ),
          entry( 109, "m" ),
          entry( 110, "n" ),
          entry( 111, "o" ),
          entry( 112, "p" ),
          entry( 113, "q" ),
          entry( 114, "r" ),
          entry( 115, "s" ),
          entry( 116, "t" ),
          entry( 117, "u" ),
          entry( 118, "v" ),
          entry( 119, "w" ),
          entry( 120, "x" ),
          entry( 121, "y" ),
          entry( 122, "z" ),
          entry( 123, "{" ),
          entry( 124, "|" ),
          entry( 125, "}" ),
          entry( 126, "~" ),
          entry( 65056, KEY_TAB ),
          entry( 65289, KEY_TAB ),
          entry( 65293, KEY_ENTER ),
          entry( 65288, KEY_BACKSPACE ),
          entry( 65301, "SysRq" ),
          entry( 65377, "Print" ),
          entry( 65361, "←" ),
          entry( 65362, "↑" ),
          entry( 65363, "→" ),
          entry( 65364, "↓" ),
          entry( 65307, "Esc" ),
          entry( 65365, "PgUp" ),
          entry( 65366, "PgDn" ),
          entry( 65379, "Ins" ),
          entry( 65535, "Del" ),
          entry( 65506, "Shift" ),
          entry( 65407, "Num" ),
          entry( 65421, "Num ⏎" ),
          entry( 65430, "Num ←" ),
          entry( 65431, "Num ↑" ),
          entry( 65432, "Num →" ),
          entry( 65433, "Num ↓" ),
          entry( 65429, "Num Home" ),
          entry( 65434, "Num PgUp" ),
          entry( 65435, "Num PgDn" ),
          entry( 65436, "Num End" ),
          entry( 65437, "Num Clear" ),
          entry( 65438, "Num Ins" ),
          entry( 65439, "Num Del" ),
          entry( 65450, "Num *" ),
          entry( 65451, "Num +" ),
          entry( 65452, "Num Sep" ),
          entry( 65453, "Num -" ),
          entry( 65454, "Num ." ),
          entry( 65455, "Num /" ),
          entry( 65456, "Num 0" ),
          entry( 65457, "Num 1" ),
          entry( 65458, "Num 2" ),
          entry( 65459, "Num 3" ),
          entry( 65460, "Num 4" ),
          entry( 65461, "Num 5" ),
          entry( 65462, "Num 6" ),
          entry( 65463, "Num 7" ),
          entry( 65464, "Num 8" ),
          entry( 65465, "Num 9" ),
          entry( 65300, "Scrl" ),
          entry( 65509, "Caps" )
      );

  /**
   * Whether a modifier key state is pressed or released depends on the state
   * of multiple keys (left and right). This map assigns the left and right
   * key codes to the same modifier key so that the physical state can be
   * represented by a single on-screen button (the logical state).
   * <p>
   * The 65511, 65512 are shifted alt key codes.
   * </p>
   */
  private final Map<Integer, HardwareSwitch> mModifierCodes =
      Map.ofEntries(
          entry( 65505, KEY_SHIFT ),
          entry( 65506, KEY_SHIFT ),
          entry( 65507, KEY_CTRL ),
          entry( 65508, KEY_CTRL ),
          entry( 65511, KEY_ALT ),
          entry( 65512, KEY_ALT ),
          entry( 65513, KEY_ALT ),
          entry( 65514, KEY_ALT )
      );

  /**
   * Most recently pressed non-modifier key value, empty signifies release.
   */
  private String mRegularHeld = "";

  /**
   * Stores the state of modifier keys. The contents of the map reflect the
   * state of each switch, so the reference can be final but not its contents.
   * An integer is used because keyboards usually have two separate keys for
   * each modifier, both can be pressed and released independently.
   */
  private final Map<HardwareSwitch, Integer> mModifiers = new HashMap<>();

  /**
   * Informing the application of a key release is delayed so that the user
   * interface will give the end user a momentary glance of what key was
   * pressed before it is released. Without this delay the keys disappear
   * as fast as a typist can type, which can be too quick to read as individual
   * keystrokes.
   * <p>
   * Track the number of key release timers are running so that they can
   * all be stopped to prevent releasing the key when another key has been
   * pressed in the mean time.
   * </p>
   */
  private final Stack<Timer> mTimerStack = new Stack<>();

  private final int mDelayRegular;
  private final int mDelayModifier;

  /**
   * Creates a keyboard listener that publishes events when keys are either
   * pressed or released. The constructor initializes all modifier keys to
   * the released state because the native keyboard hook API does not offer
   * a way to query what keys are currently pressed.
   *
   * @param delayRegular  Milliseconds to wait before releasing a regular key.
   * @param delayModifier Milliseconds to wait before releasing a modifier key.
   */
  public KeyboardListener( final int delayRegular, final int delayModifier ) {
    mDelayRegular = delayRegular;
    mDelayModifier = delayModifier;

    for( final var key : modifierSwitches() ) {
      mModifiers.put( key, 0 );
    }
  }

  @Override
  public void nativeKeyPressed( final NativeKeyEvent e ) {
    getKey( e ).ifPresentOrElse(
        keyValue -> updateModifier( keyValue, 1 ),
        () -> {
          while( !mTimerStack.isEmpty() ) {
            mTimerStack.pop().stop();
          }

          updateRegular( mRegularHeld, getDisplayText( e ) );
        } );
  }

  @Override
  public void nativeKeyReleased( final NativeKeyEvent e ) {
    getKey( e ).ifPresentOrElse(
        keyValue -> delayedAction( mDelayModifier, ( action ) ->
            updateModifier( keyValue, -1 )
        ),
        () -> {
          final var timer = delayedAction( mDelayRegular, ( action ) ->
              updateRegular( getDisplayText( e ), "" )
          );

          mTimerStack.push( timer );
        }
    );
  }

  /**
   * Convenience method to start a one-time action at a relative time in
   * the future.
   *
   * @param delay    When to perform the action.
   * @param listener The listener that will perform some future action.
   * @return The {@link Timer} that will perform a one-time action.
   */
  @SuppressWarnings("SameParameterValue")
  private Timer delayedAction(
      final int delay, final ActionListener listener ) {
    final var timer = new Timer( delay, listener );

    timer.setRepeats( false );
    timer.start();

    return timer;
  }

  /**
   * Unused. Key up and key down are tracked separately from a typed key.
   *
   * @param e Ignored.
   */
  @Override
  public void nativeKeyTyped( final NativeKeyEvent e ) {
  }

  /**
   * Sets the initial state of the modifiers.
   */
  public void initModifiers() {
    for( final var key : mModifiers.keySet() ) {
      final var state = mModifiers.get( key );

      // All modifiers keys are "false" by default, so firing fake transition
      // events from "true" to "false" will cause the GUI to repaint with the
      // text label affixed to each key, drawn in the released state. This
      // happens before the frame is set to visible.
      tryFire( key, state == 0, state == 1 );
    }
  }

  /**
   * Notifies of any modifier state changes. There's a bug whereby this
   * method is never called by the native library when both Left/Right Ctrl
   * keys are pressed followed by pressing either Shift key. Similarly,
   * holding both Left/Right Shift keys followed by pressing either Ctrl key
   * fails to call this method.
   *
   * @param key       A modifier key.
   * @param increment {@code -1} means released, {@code 1} means pressed.
   */
  private void updateModifier( final HardwareSwitch key, final int increment ) {
    final var oldCount = mModifiers.get( key );
    final var newCount = oldCount + increment;

    tryFire( key, oldCount > 0, newCount > 0 );
    mModifiers.put( key, newCount );
  }

  /**
   * State for a regular (non-modifier) key has changed.
   *
   * @param o Previous key value.
   * @param n Current key value.
   */
  private void updateRegular( final String o, final String n ) {
    assert o != null;
    assert n != null;

    tryFire( KEY_REGULAR, o, n );
    mRegularHeld = n;
  }

  /**
   * Looks up the key code for the given event. If the key code is not mapped,
   * this will return the default value from the native implementation.
   *
   * @param e The keyboard event that was triggered.
   * @return The human-readable name for the key relating to the event.
   */
  private String getDisplayText( final NativeKeyEvent e ) {
    return KEY_CODES.getOrDefault(
        e.getRawCode(), getKeyText( e.getKeyCode() )
    );
  }

  /**
   * Returns the modifier key that corresponds to the raw key code from
   * the given event. This is necessary to ensure that both left and right
   * modifier keys return the same {@link HardwareSwitch} value.
   *
   * @param e The event containing a raw key code to look up.
   * @return The switch matching the raw key code, or {@code null} if the
   * raw key code does not represent a modifier.
   */
  private Optional<HardwareSwitch> getKey( final NativeKeyEvent e ) {
    return ofNullable( mModifierCodes.get( e.getRawCode() ) );
  }

  @SuppressWarnings("unused")
  private void log( final String s, final NativeKeyEvent e ) {
    System.out.printf( "%s: %d %s%n", s, e.getRawCode(), getDisplayText( e ) );
  }
}
