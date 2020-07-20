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

import com.whitemagicsoftware.kmcaster.ui.DimensionTuple;

import java.awt.*;

/**
 * Responsible for scaling graphical insets. The label of each key cannot
 * fully extend to the bounds of the image because each key has a 3D effect.
 * To center the key's label, we need to track the insets and padding. The
 * mouse images have neither insets nor padding.
 */
public class KeyCapInsets {
  /**
   * Defines the amount of space between the 3D base of a key and the 3D
   * top of the key; the vector graphic is a 2D projection and these values
   * are specific to the projected sizes.
   */
  private final static Insets INSET_PROJECTED =
      new Insets( 3, 7, 6, 7 );

  /**
   * Defines the padding around the inside of the key cap to give the letters
   * some whitespace.
   */
  private final static Insets INSET_PADDING =
      new Insets( 4, 4, 4, 4 );

  private final static Insets INSET_TOTAL =
      new Insets(
          INSET_PROJECTED.top + INSET_PADDING.top,
          INSET_PROJECTED.left + INSET_PADDING.left,
          INSET_PROJECTED.bottom + INSET_PADDING.bottom,
          INSET_PROJECTED.right + INSET_PADDING.right
      );

  /**
   * Scales the image insets and padding.
   */
  public static Insets scale( final DimensionTuple factor ) {
    final var wRatio = factor.getWidthRatio();
    final var hRatio = factor.getHeightRatio();

    return new Insets(
        (int) (INSET_TOTAL.top * hRatio),
        (int) (INSET_TOTAL.left * wRatio),
        (int) (INSET_TOTAL.bottom * hRatio),
        (int) (INSET_TOTAL.right * wRatio) );
  }
}
