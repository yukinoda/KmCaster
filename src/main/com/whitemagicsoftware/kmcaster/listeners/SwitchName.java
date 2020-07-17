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

/**
 * Used for compile-time binding between change listeners input events.
 */
public enum SwitchName {
  KEY_ALT( "alt" ),
  KEY_CTRL( "ctrl" ),
  KEY_SHIFT( "shift" ),
  KEY_REGULAR( "regular" ),
  MOUSE_LEFT( "button 1" ),
  MOUSE_WHEEL( "button 2" ),
  MOUSE_RIGHT( "button 3" ),
  MOUSE_LR( "button 1-3" );

  private final String mName;

  SwitchName( final String name ) {
    mName = name;
  }

  public boolean isName( final String name ) {
    return mName.equalsIgnoreCase( name );
  }

  /**
   * Looks up the key that matches the given name, case-insensitively.
   *
   * @param name The name of the key to find in this enum.
   * @return The {@link SwitchName} object that matches the name.
   */
  public static SwitchName valueFrom( final String name ) {
    for( final var b : SwitchName.values() ) {
      if( b.isName( name ) ) {
        return b;
      }
    }

    return KEY_REGULAR;
  }

  @Override
  public String toString() {
    return mName;
  }
}
