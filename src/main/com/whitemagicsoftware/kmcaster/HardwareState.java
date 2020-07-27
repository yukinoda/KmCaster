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

import static java.lang.Boolean.*;

/**
 * Responsible for defining hardware switch states.
 */
public enum HardwareState {
  /**
   * Defines when a hardware switch is down.
   */
  SWITCH_PRESSED,

  /**
   * Defines when a hardware switch is up.
   */
  SWITCH_RELEASED;

  /**
   * Convenience constant used to determine pressed or released state.
   */
  public final static String BOOLEAN_FALSE = FALSE.toString();

  /**
   * Returns the {@link HardwareState} that corresponds to the given
   * state string. If the state equals {@link Boolean#FALSE} (case is
   * sensitive) or is empty, then this will return {@link #SWITCH_RELEASED},
   * otherwise {@link #SWITCH_PRESSED}.
   *
   * @param state The state to convert to an enumerated type.
   * @return {@link #SWITCH_RELEASED} if the state is "false" or empty ("").
   */
  public static HardwareState valueFrom( final String state ) {
    return (BOOLEAN_FALSE.equals( state ) || state.isEmpty())
        ? SWITCH_RELEASED
        : SWITCH_PRESSED;
  }
}
