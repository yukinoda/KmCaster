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
import java.awt.font.TextLayout;

import static java.awt.event.HierarchyEvent.PARENT_CHANGED;
import static java.lang.Math.floor;

/**
 * Responsible for changing a {@link JLabel}'s font size, dynamically. This
 * requires a valid {@link Graphics} context in order to determine the ideal
 * font point size for this component's size dimensions. The {@link Graphics}
 * context is set when the component is added to another container.
 */
public final class AutofitLabel extends JLabel {

  /**
   * Constructs an instance of {@link AutofitLabel} that will rescale itself
   * to the parent {@link Container}, automatically.
   * <p>
   * When this label is added to a container, it will appear immediately. If
   * additional scaling is required, then:
   * </p>
   * <ol>
   *   <li>call {@link #setVisible(boolean)} with {@code false};</li>
   *   <li>add the label to its container;</li>
   *   <li>perform the necessary size or location computations;</li>
   *   <li>call {@link #setSize(Dimension)} to update the label;</li>
   *   <li>then make the label visible again.</li>
   * </ol>
   * <p>
   * Without following the prescribed steps, the label may display at an
   * unexpected size.
   * </p>
   * </p>
   *
   * @param text The text to write on the container's graphics context.
   * @param font The font to use when writing the text.
   */
  public AutofitLabel( final String text, final Font font ) {
    super( text );
    setFont( font );

    addHierarchyListener( e -> {
      final var parent = getParent();

      if( (e.getChangeFlags() & PARENT_CHANGED) != 0 &&
          (e.getChangedParent() == parent) ) {
        final var bounds = BoundsCalculator.getBounds( parent );

        setSize( bounds.width, bounds.height );
        setLocation( bounds.x, bounds.y );
      }
    } );
  }

  /**
   * Note that {@link #setSize(Dimension)} eventually delegates to calling this
   * method, so there's no need to override both. The {@link Graphics} context
   * must be valid before calling this method.
   *
   * @param w The new width constraint.
   * @param h The new height constraint.
   */
  @Override
  public void setSize( final int w, final int h ) {
    super.setSize( w, h );
    rescale();
  }

  /**
   * Rescales the constructed font to fit within the label's dimensions,
   * governed by {@link #getWidth()} and {@link #getHeight()}. This must only
   * be called after a {@link Graphics} context is available to compute the
   * maximum {@link Font} size that will fit the label's {@link Rectangle}
   * bounds.
   */
  private void rescale() {
    setFont( computeScaledFont() );
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
    final var frc = getFontRenderContext();
    final var text = getText();

    final var dstWidthPx = getWidth();
    final var dstHeightPx = getHeight();

    var minSizePt = 1f;
    var maxSizePt = 100f;
    var scaledFont = getFont();
    float scaledPt = scaledFont.getSize();

    // TextLayout cannot suffer null or empty values, so return the default
    // font size if the label is cleared out.
    if( text != null && !text.isEmpty() ) {
      while( maxSizePt - minSizePt > 1f ) {
        scaledFont = scaledFont.deriveFont( scaledPt );

        final var layout = new TextLayout( text, scaledFont, frc );
        final var metrics = scaledFont.getLineMetrics( text, frc );
        final var fontWidthPx = layout.getVisibleAdvance();
        final var fontHeightPx = metrics.getHeight();

        if( (fontWidthPx > dstWidthPx) || (fontHeightPx > dstHeightPx) ) {
          maxSizePt = scaledPt;
        }
        else {
          minSizePt = scaledPt;
        }

        scaledPt = (minSizePt + maxSizePt) / 2;
      }
    }

    // Round down to guarantee fit.
    return scaledFont.deriveFont( (float) floor( scaledPt ) );
  }

  /**
   * Gets the {@link FontRenderContext} for the parent {@link Container}'s
   * {@link Graphics} context, casting it to a {@link Graphics2D} context.
   *
   * @return The parent's {@link Graphics2D} context.
   */
  private FontRenderContext getFontRenderContext() {
    return ((Graphics2D) getGraphics()).getFontRenderContext();
  }
}
