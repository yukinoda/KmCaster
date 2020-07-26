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

import static java.lang.Math.*;
import static java.lang.Math.min;

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
   * Delegates construction to this class.
   *
   * @param w The width, rounded up to nearest integer.
   * @param h The height, rounded up to nearest integer.
   */
  public ScalableDimension( final double w, final double h ) {
    this( (int) ceil( w ), (int) ceil( h ) );
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
    assert dst != null;

    // Determine the ratio that has the best fit, then scale both dimensions
    // with respect to said ratio.
    return scale( min(
        dst.getWidth() / getWidth(),
        dst.getHeight() / getHeight()
    ) );
  }

  /**
   * Scale both dimensions according to some multiplication factor. Factors
   * between 0 and 1 will return an instance of {@link Dimension} that is
   * smaller than this instance.
   *
   * @param factor The factor for scaling, greater than or equal to zero.
   * @return A new dimension with the width and height of this instance
   * multiplied out by the given factor. The
   */
  public Dimension scale( final double factor ) {
    assert factor >= 0;

    return new ScalableDimension(
        getWidth() * factor, getHeight() * factor
    );
  }
}
