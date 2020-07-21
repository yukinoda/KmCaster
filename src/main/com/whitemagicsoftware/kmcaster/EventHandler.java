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

import com.whitemagicsoftware.kmcaster.ui.AutofitLabel;
import com.whitemagicsoftware.kmcaster.ui.BoundsCalculator;
import com.whitemagicsoftware.kmcaster.ui.ScalableDimension;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;

import static com.whitemagicsoftware.kmcaster.HardwareState.ANY_KEY;
import static java.awt.Font.BOLD;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.Math.abs;
import static javax.swing.SwingConstants.CENTER;
import static javax.swing.SwingConstants.TOP;

public class EventHandler implements PropertyChangeListener {
  /**
   * DejaVu Sans is the only free, open, sans serif font that supports
   * all the Unicode blocks used by the application. The font size is
   * arbitrary, the font will be scaled dynamically to the window size.
   */
  private static final Font LABEL_FONT = new Font( "DejaVu Sans", BOLD, 32 );

  private static final String KEY_UP = FALSE.toString();
  private static final String KEY_DOWN = TRUE.toString();

  /**
   * Matches the shift-key arrow font colour when pressed.
   */
  private static final Color COLOUR_KEY_DN = new Color( 0x21, 0x21, 0x21 );

  /**
   * Matches the shift-key arrow font colour when released.
   */
  private static final Color COLOUR_KEY_UP = new Color( 0xE5, 0xE5, 0xE5 );

  /**
   * Maps key pressed states to key cap title colours.
   */
  private static final Map<String, Color> KEY_COLOURS = Map.of(
      KEY_DOWN, COLOUR_KEY_DN,
      KEY_UP, COLOUR_KEY_UP
  );

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

    // True indicates a modifier key was pressed; false indicates a key was
    // released (doesn't matter what kind of key).
    final var context =
        (!KEY_UP.equals( switchValue ) && !KEY_DOWN.equals( switchValue ))
            ? ANY_KEY
            : switchValue;

    final var switchState = new HardwareState( switchName, context );
    updateSwitchState( switchState );
    updateSwitchLabel( switchState, switchValue );
  }

  protected void updateSwitchState( final HardwareState state ) {
    final var component = getHardwareComponent( state );
    component.setState( state );
  }

  protected void updateSwitchLabel(
      final HardwareState state, final String value ) {
    final var container = getHardwareComponent( state );

    if( KEY_UP.equals( value ) ) {
      container.removeAll();
    }

    if( state.isModifier() ) {
      final var hwSwitch = state.getHardwareSwitch();
      final var switchName = hwSwitch.toTitleCase();
      final var keyColour = KEY_COLOURS.get( value );

      final var label = new AutofitLabel( switchName, LABEL_FONT, keyColour );
      label.setVisible( false );
      label.setHorizontalAlignment( CENTER );
      label.setVerticalAlignment( CENTER );
      container.removeAll();
      container.add( label );
      label.setVisible( true );
    }
    else if( !KEY_UP.equals( value ) ) {
      // A non-modifier key has been pressed.
      final var index = value.indexOf( ' ' );
      final var keyColour = KEY_COLOURS.get( KEY_DOWN );

      // If there's a space in the name, the text before the space is
      // positioned in the upper-left while the text afterwards takes up
      // the remainder. This is used for number pad keys, backspace, enter,
      // tab, and a few others.
      if( index > 0 ) {
        final var calculator = new BoundsCalculator( container );
        final var contDimen = new ScalableDimension( calculator.computeSize() );
        final var supSize = contDimen.scale( .6f );
        final var mainSize = contDimen.scale( .9f );

        final var s = new String[]{
            value.substring( 0, index ),
            value.substring( index + 1 )
        };

        // Label for "Num", "Back", "Tab", and other dual-labelled keys.
        final var sup = new AutofitLabel( s[ 0 ], LABEL_FONT, keyColour );
        sup.setVisible( false );
        sup.setVerticalAlignment( TOP );

        // Label for number pad keys or icon glyphs.
        final var main = new AutofitLabel( s[ 1 ], LABEL_FONT, keyColour );
        main.setVisible( false );
        main.setHorizontalAlignment( CENTER );
        main.setVerticalAlignment( CENTER );

        // Keep removing then adding as close together as possible to minimize
        // flicker.
        container.removeAll();
        container.add( main );
        container.add( sup );
        main.setSize( mainSize );
        sup.setSize( supSize );

        // Center-align the main text with respect to the container.
        final var location = main.getLocation();
        final var dx = abs( contDimen.getWidth() - main.getWidth() ) / 2;
        final var dy = abs( contDimen.getHeight() - main.getHeight() ) / 2;

        // Shift the main text down a smidgen, relative to the superscript.
        main.setLocation(
            (int) (location.getX() + dx),
            (int) (location.getY() + dy) + sup.getHeight() / 4 );
        main.setVisible( true );
        sup.setVisible( true );
      }
      else {
        // Single keys need no tweaking and can be added to the container
        // directly. The horizontal and vertical alignments
        final var label = new AutofitLabel( value, LABEL_FONT, keyColour );
        label.setVisible( false );
        label.setHorizontalAlignment( CENTER );
        label.setVerticalAlignment( CENTER );
        container.removeAll();
        container.add( label );
        label.setVisible( true );
      }
    }
  }

  private HardwareComponent<HardwareState, Image> getHardwareComponent(
      final HardwareState state ) {
    return mHardwareImages.get( state.getHardwareSwitch() );
  }
}
