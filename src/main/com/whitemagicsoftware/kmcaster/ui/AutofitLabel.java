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

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

import static java.lang.Math.floor;

/**
 * Responsible for changing a {@link JLabel}'s font size, dynamically.
 */
public final class AutofitLabel extends JLabel {

  /**
   * Constructs an instance of {@link AutofitLabel} that can rescale itself
   * relative to either the parent {@link Container} or a given dimension.
   *
   * @param text The text to write on the container's graphics context.
   * @param font The font to use when writing the text.
   */
  public AutofitLabel( final String text, final Font font ) {
    super( text );
    setFont( font );
  }

  /**
   * Scales the dimensions of the label to fit within the given width and
   * height, while maintaining the aspect ratio; relocates the label relative
   * to the bounds of the container, honouring {@link #getVerticalAlignment()}
   * and {@link #getHorizontalAlignment()}.
   *
   * @param width  The maximum label width.
   * @param height The maximum label height.
   */
  public void transform( final int width, final int height ) {
    setSize( width, height );
    setFont( computeScaledFont() );

    final var bounds = BoundsCalculator.getBounds( getParent() );

    // LEFT by default.
    int x = bounds.x;

    // TOP by default.
    int y = bounds.y;

    switch( getHorizontalAlignment() ) {
      case CENTER -> x += (bounds.getWidth() - getWidth()) / 2;
      case RIGHT -> x += (bounds.getWidth() - getWidth());
    }

    switch( getVerticalAlignment() ) {
      case CENTER -> y += (bounds.getHeight() - getHeight()) / 2;
      case BOTTOM -> y += (bounds.getHeight() - getHeight());
    }

    setLocation( x, y );
  }

  /**
   * Convenience method to scale to the given dimensions then relocate the
   * label with respect to the vertical and horizontal alignment.
   *
   * @param dimension The maximum label width and height.
   */
  public void transform( final Dimension dimension ) {
    transform( dimension.width, dimension.height );
  }

  /**
   * Scales the dimensions of the label to fit its parent's boundaries, while
   * maintaining the aspect ratio, then relocate the label with respect to
   * the vertical and horizontal alignment.
   */
  public void transform() {
    final var bounds = BoundsCalculator.getBounds( getParent() );
    transform( bounds.width, bounds.height );
  }

  /**
   * Calculates a new {@link Font} size such that it fits within the bounds
   * of this label instance. This uses the label's current size, which must
   * be set prior to calling this method.
   *
   * @return A new {@link Font} instance that is guaranteed to write the given
   * string within the bounds of the given {@link Rectangle}.
   */
  private Font computeScaledFont() {
    final var g = getGraphics();

    if( g == null ) {
      return getFont();
    }

    final var text = getText();
    final var dstWidthPx = getWidth();
    final var dstHeightPx = getHeight();

    // Derived using a binary search algorithm to minimize text width lookups.
    var scaledFont = getFont();

    // Using the scaledPt as a relative max size reduces the iterations by two.
    var scaledPt = scaledFont.getSize();
    var minSizePt = 1;
    var maxSizePt = scaledPt * 2;

    while( maxSizePt - minSizePt > 1 ) {
      scaledFont = scaledFont.deriveFont( (float) scaledPt );

      final var bounds = getBounds( text, scaledFont, g );
      final var fontWidthPx = (int) bounds.getWidth();
      final var fontHeightPx = (int) bounds.getHeight();

      if( (fontWidthPx > dstWidthPx) || (fontHeightPx > dstHeightPx) ) {
        maxSizePt = scaledPt;
      }
      else {
        minSizePt = scaledPt;
      }

      scaledPt = (minSizePt + maxSizePt) / 2;
    }

    g.dispose();

    // Round down to guarantee fit.
    scaledFont = scaledFont.deriveFont( (float) floor( scaledPt ) );

    // Recompute the bounds of the label based on the text extents that fit.
    final var bounds = getBounds( text, scaledFont, g );
    setSize( (int) bounds.getWidth(), (int) bounds.getHeight() );

    return scaledFont;
  }

  /**
   * Helper method to determine the width and height of the text.
   *
   * @param text     Text having a width and height to derive.
   * @param font     Font used to render the next.
   * @param graphics Graphics context needed for calculating the text extents.
   * @return Text width and height.
   */
  private Rectangle2D getBounds(
      final String text, final Font font, final Graphics graphics ) {
    return getFontMetrics( font ).getStringBounds( text, graphics );
  }
}
