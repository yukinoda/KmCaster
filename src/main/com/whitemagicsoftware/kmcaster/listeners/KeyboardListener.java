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

import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.Map;

import static com.whitemagicsoftware.kmcaster.listeners.Key.*;
import static java.util.Map.entry;
import static org.jnativehook.NativeInputEvent.*;
import static org.jnativehook.keyboard.NativeKeyEvent.getKeyText;

public class KeyboardListener implements NativeKeyListener {
  private final List<KeyboardModifier> mModifiers = List.of(
      new KeyboardModifier( KEY_ALT, ALT_MASK ),
      new KeyboardModifier( KEY_CTRL, CTRL_MASK ),
      new KeyboardModifier( KEY_SHIFT, SHIFT_MASK )
  );

  @SuppressWarnings("RedundantTypeArguments")
  private final static Map<Integer, String> KEY_CODES =
      Map.<Integer, String>ofEntries(
          entry( 32, "␣" ),
          entry( 33, "!" ),
          entry( 34, "'" ),
          entry( 35, "#" ),
          entry( 36, "$" ),
          entry( 37, "%" ),
          entry( 38, "&" ),
          entry( 39, "\"" ),
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
          entry( 65288, "Back ⌫" ),
          entry( 65056, "Tab ↹" ),
          entry( 65289, "Tab ↹" ),
          entry( 65293, "Enter ⏎" ),
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
          entry( 65456, "Num 0" ),
          entry( 65436, "Num 1" ),
          entry( 65433, "Num 2" ),
          entry( 65435, "Num 3" ),
          entry( 65430, "Num 4" ),
          entry( 65437, "Num 5" ),
          entry( 65432, "Num 6" ),
          entry( 65429, "Num 7" ),
          entry( 65431, "Num 8" ),
          entry( 65434, "Num 9" ),
          entry( 65421, "Num ⏎" ),
          entry( 65438, "Num Ins" ),
          entry( 65439, "Num Del" ),
          entry( 65451, "Num +" ),
          entry( 65453, "Num -" ),
          entry( 65454, "Num ." ),
          entry( 65455, "Num /" ),
          entry( 65450, "Num *" ),
          entry( 65300, "Scrl" ),
          entry( 65509, "Caps" ),
          entry( 65377, "Print" ),
          entry( 65301, "SysRq" )
      );

  private String mRegularHeld = "";

  private final PropertyChangeSupport mDispatcher =
      new PropertyChangeSupport( this );

  public KeyboardListener() {
  }

  public void addPropertyChangeListener(
      final PropertyChangeListener listener ) {
    mDispatcher.addPropertyChangeListener( listener );
  }

  @SuppressWarnings("unused")
  public void removePropertyChangeListener(
      final PropertyChangeListener listener ) {
    mDispatcher.removePropertyChangeListener( listener );
  }

  @Override
  public void nativeKeyPressed( final NativeKeyEvent e ) {
    final String regularHeld = getDisplayText( e );
    boolean isModifier = false;

    // The key is regular iff its name does not match any modifier name.
    for( final var modifier : mModifiers ) {
      isModifier |= modifier.isKeyName( regularHeld );
    }

    // If it's not a modifier key, broadcast the regular value.
    if( !isModifier ) {
      tryFire( KEY_REGULAR, mRegularHeld, regularHeld );
      mRegularHeld = regularHeld;
    }

    updateModifiers( e );
  }

  @Override
  public void nativeKeyReleased( final NativeKeyEvent e ) {
    final String oldValue = getDisplayText( e );
    final String newValue = "";

    tryFire( KEY_REGULAR, oldValue, newValue );
    mRegularHeld = newValue;

    updateModifiers( e );
  }

  @Override
  public void nativeKeyTyped( final NativeKeyEvent e ) {
  }

  /**
   * Notifies of any modifier state changes.
   *
   * @param e The keyboard event that was most recently triggered.
   */
  private void updateModifiers( final NativeKeyEvent e ) {
    for( final var modifier : mModifiers ) {
      final boolean down = modifier.matches( e );
      tryFire( modifier.getKey(), modifier.isHeld(), down );
      modifier.setHeld( down );
    }
  }

  /**
   * Called to fire the property change with the two given values differ.
   *
   * @param key The name of the property that has changed.
   * @param o   Old property value.
   * @param n   New property value.
   */
  private void tryFire( final Key key, final String o, final String n ) {
    if( !o.equals( n ) ) {
      mDispatcher.firePropertyChange( key.toString(), o, n );
    }
  }

  /**
   * Delegates to {@link #tryFire(Key, String, String)} with {@link Boolean}
   * values as strings.
   *
   * @param key The name of the property that has changed.
   * @param o   Old property value.
   * @param n   New property value.
   */
  private void tryFire( final Key key, final boolean o, final boolean n ) {
    tryFire( key, Boolean.toString( o ), Boolean.toString( n ) );
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
}
