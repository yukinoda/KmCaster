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
 * Provides the ability to scale a dimension in relation to another
 * dimension. The dimensions are unit-less.
 */
public final class ScalableDimension extends Dimension {

  /**
   * Delegates construction to the superclass.
   *
   * @param w The dimension's width.
   * @param h The dimension's height.
   */
  public ScalableDimension( final int w, final int h ) {
    super( w, h );
  }

  /**
   * Scales the given source {@link Dimension} to the destination
   * {@link Dimension}, maintaining the aspect ratio with respect to
   * the best fit.
   *
   * @param dst The desired image dimensions to scale.
   * @return The given source dimensions scaled to the destination dimensions,
   * maintaining the aspect ratio.
   */
  public Dimension scale( final Dimension dst ) {
    final var srcWidth = getWidth();
    final var srcHeight = getHeight();

    // Determine the ratio that will have the best fit.
    final var ratio = Math.min(
        dst.getWidth() / srcWidth, dst.getHeight() / srcHeight
    );

    // Scale both dimensions with respect to the best fit ratio.
    return new Dimension( (int) (srcWidth * ratio), (int) (srcHeight * ratio) );
  }
}
