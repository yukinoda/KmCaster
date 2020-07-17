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

import com.whitemagicsoftware.kmcaster.listeners.SwitchName;

/**
 * Responsible for defining hardware switch states.
 */
public class HardwareState {
  public final static String ANY_KEY = "*";

  private final SwitchName mName;
  private final String mState;

  /**
   * Constructs a new instance that represents whether a key or mouse button
   * was pressed.
   *
   * @param name  The {@link SwitchName} representing the type of switch
   *              state to represent.
   * @param state A value of "*' means a regular key was pressed; otherwise,
   *              "true" or "false" indicate pressed or released, respectively.
   */
  public HardwareState( final SwitchName name, final String state ) {
    assert name != null;
    assert valid( state );

    mName = name;
    mState = state;
  }

  public SwitchName getKey() {
    return mName;
  }

  /**
   * Test whether the given state conforms to specification.
   *
   * @param state A value of "*' means a regular key was pressed; otherwise,
   *              "true" or "false" indicate pressed or released, respectively.
   * @return {@code true} The given {@code state} is a known value.
   */
  private boolean valid( final String state ) {
    return ANY_KEY.equals( state ) ||
        "true".equals( state ) ||
        "false".equals( state );
  }

  @Override
  public boolean equals( final Object o ) {
    if( this == o ) {
      return true;
    }
    if( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final HardwareState that = (HardwareState) o;

    if( mName != that.mName ) {
      return false;
    }
    return mState.equals( that.mState );
  }

  @Override
  public int hashCode() {
    int result = mName.hashCode();
    result = 31 * result + mState.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return getClass().getName() + "{" +
        "mKey=" + mName +
        ", mState='" + mState + '\'' +
        '}';
  }
}
