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
package com.whitemagicsoftware.kmcaster.ui;

import java.awt.*;

/**
 * Responsible for scaling graphical insets. The label of each key cannot
 * fully extend to the bounds of the image because each key has a 3D effect.
 * To center the key's label, we need to track the insets and padding. The
 * mouse images have neither insets nor padding.
 */
public final class PaddedInsets {
  /**
   * Defines the padding around the inside of the key cap to give the letters
   * some whitespace.
   */
  private final static Insets INSET_PADDING =
      new Insets( 4, 4, 4, 4 );

  /**
   * Includes safe zone and internal padding.
   */
  private final Insets mInsets;

  /**
   * Creates a new area for drawing on a key cap. This class will add an
   * internal padding amount.
   *
   * @param insets The insets that correlate the "safe zone" for drawing
   *               items on the key cap, without padding.
   */
  public PaddedInsets( final Insets insets ) {
    mInsets = new Insets(
        insets.top + INSET_PADDING.top,
        insets.left + INSET_PADDING.left,
        insets.bottom + INSET_PADDING.bottom,
        insets.right + INSET_PADDING.right
    );
  }

  /**
   * Scales the image insets and padding.
   */
  public Insets scale( final DimensionTuple factor ) {
    final var wRatio = factor.getWidthRatio();
    final var hRatio = factor.getHeightRatio();

    return new Insets(
        (int) (mInsets.top * hRatio),
        (int) (mInsets.left * wRatio),
        (int) (mInsets.bottom * hRatio),
        (int) (mInsets.right * wRatio) );
  }
}
