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

/**
 * Responsible for changing the {@link JLabel}'s font size
 */
public class AutofitLabel extends JLabel {

  /**
   * Constructs an instance of {@link AutofitLabel} that will rescale itself
   * to the parent {@link Container}, automatically.
   *
   * @param text  The text to write on the container's graphics context.
   * @param font  The font to use when writing the text.
   * @param color The colour to use when writing hte text.
   */
  public AutofitLabel( final String text, final Font font, final Color color ) {
    super( text );
    setFont( font );
    setForeground( color );

    addHierarchyListener( e -> {
      if( (e.getChangeFlags() & PARENT_CHANGED) != 0 ) {
        if( getParent() == e.getChangedParent() ) {
          rescale();
        }
      }
    } );
  }

  /**
   * Rescales the constructed font to fit within the bounds of the parent
   * {@link Container}. This must only be called after the parent
   * {@link Container} has been set, otherwise there will be no
   * {@link Graphics} context available to compute the maximum {@link Font}
   * size that will fit the parent's {@link Rectangle} bounds.
   */
  private void rescale() {
    final var component = getParent();
    final var bounds = component.getBounds();
    final var insets = component.getInsets();

    bounds.x += insets.left;
    bounds.y += insets.top;
    bounds.width -= insets.left + insets.right;
    bounds.height -= insets.top + insets.bottom;

    setLocation( insets.left, insets.top );
    setSize( bounds.width, bounds.height );
    setFont( computeScaledFont() );
    setHorizontalAlignment( CENTER );
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
    var maxSizePt = 200f;
    var scaledFont = getFont();
    float scaledPt = scaledFont.getSize();

    while( maxSizePt - minSizePt > 1f ) {
      scaledFont = scaledFont.deriveFont( scaledPt );

      final var layout = new TextLayout( text, scaledFont, frc );
      final var fontWidthPx = layout.getVisibleAdvance();
      final var metrics = scaledFont.getLineMetrics( text, frc );
      final var fontHeightPx = metrics.getHeight();

      if( (fontWidthPx > dstWidthPx) || (fontHeightPx > dstHeightPx) ) {
        maxSizePt = scaledPt;
      }
      else {
        minSizePt = scaledPt;
      }

      scaledPt = (minSizePt + maxSizePt) / 2;
    }

    // Round down to guarantee fit.
    return scaledFont.deriveFont( (float) Math.floor( scaledPt ) );
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
