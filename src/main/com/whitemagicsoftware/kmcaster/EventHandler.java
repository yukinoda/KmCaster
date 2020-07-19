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

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import static com.whitemagicsoftware.kmcaster.HardwareState.ANY_KEY;
import static java.awt.Font.BOLD;
import static java.lang.Boolean.parseBoolean;

public class EventHandler implements PropertyChangeListener {
  private static final Font LABEL_FONT = new Font( "DejaVu Sans", BOLD, 12 );
  private static final Color LABEL_COLOUR = new Color( 33, 33, 33 );

  private final HardwareImages mHardwareImages;

  public EventHandler( final HardwareImages hardwareImages ) {
    mHardwareImages = hardwareImages;
  }

  /**
   * Called when a hardware switch has changed state.
   *
   * @param e Contains the identifier for the switch, its previous value,
   *          and its new value.
   */
  @Override
  public void propertyChange( final PropertyChangeEvent e ) {
    final var switchName = e.getPropertyName();
    final var switchValue = e.getNewValue().toString();

    // True or false indicates a non-regular key was pressed.
    final var context =
        (!"false".equals( switchValue ) && !"true".equals( switchValue ))
            ? ANY_KEY
            : switchValue;

    final var switchState = new HardwareState( switchName, context );
    updateSwitchState( switchState );
    updateSwitchLabel( switchState, switchValue );
  }

  protected void updateSwitchState( final HardwareState state ) {
    final var component = mHardwareImages.get( state.getHardwareSwitch() );

    component.setState( state );
  }

  protected void updateSwitchLabel(
      final HardwareState state, final String value ) {
    if( state.isModifier() ) {
      System.out.println( parseBoolean( value ) );
    }
    else {
      final var component = mHardwareImages.get( state.getHardwareSwitch() );
      component.removeAll();

      if( !"false".equals( value ) ) {
        final var bounds = component.getBounds();
        final var insets = component.getInsets();

        bounds.x += insets.left;
        bounds.y += insets.top;
        bounds.width -= insets.right + insets.left;
        bounds.height -= insets.bottom + insets.top;

        final var label = createLabel( value, bounds, component.getGraphics() );

        label.setLocation( insets.left, insets.top );

        component.add( label );
        component.repaint();
      }
    }
  }

  /**
   * Creates a label that can fit within bounds defined by the given
   * {@link Rectangle} using the given {@link Graphics} context for deriving
   * the actualized font dimensions (for a particular font point size).
   *
   * @param text     The label's text.
   * @param r        The label's maximum width and height.
   * @param graphics The graphics context used to determine font scale (must
   *                 be a {@link Graphics2D instance}).
   * @return A label adjusted to the given dimensions.
   */
  private JLabel createLabel(
      final String text, final Rectangle r, final Graphics graphics ) {
    assert text != null;
    assert r != null;
    assert graphics != null;

    final int width = (int) r.getWidth();
    final int height = (int) r.getHeight();

    final var label = new JLabel( text );
    label.setFont( LABEL_FONT );
    label.setSize( width, height );
    label.setForeground( LABEL_COLOUR );

    final var scaledFont = scaleFont( label, r, graphics );
    label.setFont( scaledFont );

    return label;
  }

  /**
   * Adjusts the given {@link Font}/{@link String} size such that it fits
   * within the bounds of the given {@link Rectangle}.
   *
   * @param label    Contains the text and font to scale.
   * @param dst      The bounds for fitting the string.
   * @param graphics The context for rendering the string.
   * @return A new {@link Font} instance that is guaranteed to write the given
   * string within the bounds of the given {@link Rectangle}.
   */
  public Font scaleFont(
      final JLabel label, final Rectangle dst, final Graphics graphics ) {
    assert label != null;
    assert dst != null;
    assert graphics != null;

    final var font = label.getFont();
    final var text = label.getText();

    final var frc = ((Graphics2D) graphics).getFontRenderContext();

    final var dstWidthPx = dst.getWidth();
    final var dstHeightPx = dst.getHeight();

    var minSizePt = 1f;
    var maxSizePt = 1000f;
    var scaledFont = font;
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

    return scaledFont.deriveFont( (float) Math.floor( scaledPt ) );
  }
}
