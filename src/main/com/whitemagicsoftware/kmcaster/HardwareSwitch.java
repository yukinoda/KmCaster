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
package com.whitemagicsoftware.kmcaster;

import java.util.NoSuchElementException;

import static org.jnativehook.NativeInputEvent.*;

/**
 * Used for compile-time binding between change listeners input events.
 */
public enum HardwareSwitch {
  MOUSE_LEFT( "1" ),
  MOUSE_MIDDLE( "2" ),
  MOUSE_RIGHT( "3" ),
  KEY_SHIFT( "shift", SHIFT_MASK ),
  KEY_CTRL( "ctrl", CTRL_MASK ),
  KEY_ALT( "alt", ALT_MASK ),
  KEY_REGULAR( "regular" );

  /**
   * Indicates the switch is not a modifier.
   */
  private final static int NO_MASK = -1;

  private final String mName;
  private final int mMask;

  /**
   * Constructs a new switch with no mask value.
   *
   * @param name The switch name.
   */
  HardwareSwitch( final String name ) {
    this( name, NO_MASK );
  }

  /**
   * Constructs a new switch associated with a mask value that can be used
   * to determine whether a modifier key is pressed.
   *
   * @param name The switch name.
   * @param mask Modifier key bitmask.
   */
  HardwareSwitch( final String name, final int mask ) {
    mName = name;
    mMask = mask;
  }

  /**
   * Answers whether this enumerated item represents a keyboard modifier.
   *
   * @return {@code true} when the switch is a modifier key.
   */
  public boolean isModifier() {
    return mMask != NO_MASK;
  }

  /**
   * Answers whether the given name and the switch's name are the same,
   * ignoring case.
   *
   * @param name The switch name to compare against this name.
   * @return {@code true} when the names match, regardless of case.
   */
  public boolean isName( final String name ) {
    return mName.equalsIgnoreCase( name );
  }

  /**
   * Answers whether this hardware switch represents a keyboard key.
   *
   * @return {@code true} when this is a keyboard key.
   */
  public boolean isKeyboard() {
    return isPrefix( "KEY" );
  }

  /**
   * Answers whether this hardware switch represents a mouse button.
   *
   * @return {@code true} when this is a mouse button.
   */
  public boolean isMouse() {
    return isPrefix( "MOUSE" );
  }

  /**
   * Answers whether the enum's name starts with the given string, using
   * a case sensitive comparison.
   *
   * @param prefix The prefix to check against.
   * @return {@code true} when the prefix matches the enumerated name.
   */
  private boolean isPrefix( final String prefix ) {
    return name().startsWith( prefix );
  }

  /**
   * Answers whether this hardware switch is pressed, but only applies to
   * keyboard modifier keys, not mouse buttons.
   *
   * @param modifiers Bit set that indicates what modifier keys are pressed.
   * @return {@code true} if this switch's modifier bit is set in the
   * given {@code modifiers} value.
   */
  public boolean isModifierPressed( final int modifiers ) {
    assert isModifier();

    return (modifiers & mMask) != 0;
  }

  /**
   * Looks up the key that matches the given name, case-insensitively.
   *
   * @param name The name of the key to find in this enum.
   * @return The {@link HardwareSwitch} object that matches the name.
   */
  public static HardwareSwitch valueFrom( final String name ) {
    for( final var b : HardwareSwitch.values() ) {
      if( b.isName( name ) ) {
        return b;
      }
    }

    // This could be thrown when a mouse button is pressed that does not have
    // a visual representation.
    throw new NoSuchElementException( name );
  }

  /**
   * Returns a list of all keyboard keys.
   * <p>
   * The element declaration order dictates the on-screen order.
   * </p>
   *
   * @return The complete list of keyboard keys.
   */
  public static HardwareSwitch[] keyboardSwitches() {
    return new HardwareSwitch[]{KEY_SHIFT, KEY_CTRL, KEY_ALT, KEY_REGULAR};
  }

  /**
   * Returns a list of all mouse buttons.
   *
   * @return The complete list of mouse buttons.
   */
  public static HardwareSwitch[] mouseSwitches() {
    return new HardwareSwitch[]{MOUSE_LEFT, MOUSE_MIDDLE, MOUSE_RIGHT};
  }

  /**
   * Converts the switch name from to Title Case. This will only convert the
   * first letter, leaving all remaining characters lowercase, even if there
   * are multiple words in the name.
   *
   * @return The switch name with its first letter capitalized.
   */
  public String toTitleCase() {
    final var s = toString().toLowerCase();
    return Character.toTitleCase( s.charAt( 0 ) ) + s.substring( 1 );
  }

  /**
   * Returns the switch name.
   *
   * @return The switch name, not the enum name.
   */
  @Override
  public String toString() {
    return mName;
  }
}
