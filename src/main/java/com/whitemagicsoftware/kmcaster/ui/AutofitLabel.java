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
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

/**
 * Responsible for changing a {@link JLabel}'s font size, dynamically.
 */
public final class AutofitLabel extends JLabel {

  /**
   * Lazily initialized to the parent's container's safe drawing area.
   */
  private Rectangle mParentBounds;

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
    setFont( computeScaledFontNew() );

    final var bounds = getParentBounds();

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
   * Scales the dimensions of the label to fit its parent's boundaries,
   * then multiplying the value by the given factor.
   *
   * @param factor The scaling coefficient value.
   */
  public void transform( final float factor ) {
    final var compDimen = new ScalableDimension( getParentBounds() );
    transform( compDimen.scale( factor ) );
  }

  /**
   * Scales the dimensions of the label to fit its parent's boundaries, while
   * maintaining the aspect ratio, then relocate the label with respect to
   * the vertical and horizontal alignment.
   */
  public void transform() {
    final var bounds = getParentBounds();
    transform( bounds.width, bounds.height );
  }

  private Font computeScaledFontNew() {
    final var font = getFont();
    final var text = getText();

    // Without the - 1 the word Esc fails to appear.
    final var dstWidthPx = getWidth() - 1;
    final var dstHeightPx = getHeight();

    float shrink = 0;

    Rectangle2D newExtents;
    Font newFont;

    do {
      final var oldExtents = getTextExtents( text, font );
      final var widthText = oldExtents.getWidth();
      final var widthRatio = dstWidthPx / widthText;
      final var widthFontSizeNew = (int) (font.getSize() * widthRatio);
      final var widthFontSizeNorm =
        (float) Math.min( widthFontSizeNew, dstHeightPx );

      newFont = font.deriveFont( widthFontSizeNorm - shrink );
      newExtents = getTextExtents( text, newFont );
      shrink++;
    }
    while( newExtents.getHeight() > dstHeightPx );

    return newFont;
  }

  private Rectangle2D getTextExtents( final String text, final Font font ) {
    final var transform = new AffineTransform();
    final var context = new FontRenderContext( transform, true, true );
    return font.getStringBounds( text, context );
  }

  /**
   * Returns the bounds of the parent component, accounting for insets.
   *
   * @return The parent's safe drawing area.
   */
  private Rectangle getParentBounds() {
    final var bounds = mParentBounds;

    return bounds == null
      ? mParentBounds = calculateBounds( getParent() )
      : bounds;
  }

  /**
   * Returns the safe area for writing on the {@link Container} parameter
   * provided during construction.
   *
   * @return The {@link Container}'s safe area, based on the
   * {@link Container}'s bounded dimensions and insets.
   */
  public static Rectangle calculateBounds( final Container container ) {
    final var insets = container.getInsets();

    return new Rectangle(
      insets.left, insets.top,
      container.getWidth() - (insets.left + insets.right),
      container.getHeight() - (insets.top + insets.bottom)
    );
  }
}
