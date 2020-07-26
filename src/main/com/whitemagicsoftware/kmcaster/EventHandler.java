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
import com.whitemagicsoftware.kmcaster.util.ConsecutiveEventCounter;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;

import static com.whitemagicsoftware.kmcaster.HardwareState.SWITCH_PRESSED;
import static com.whitemagicsoftware.kmcaster.HardwareState.SWITCH_RELEASED;
import static com.whitemagicsoftware.kmcaster.ui.Constants.*;
import static javax.swing.SwingConstants.*;

/**
 * Responsible for controlling the application state between the events
 * and the view.
 */
public class EventHandler implements PropertyChangeListener {

  /**
   * Maps key pressed states to key cap title colours.
   */
  private static final Map<HardwareState, Color> KEY_COLOURS = Map.of(
      SWITCH_PRESSED, COLOUR_KEY_DN,
      SWITCH_RELEASED, COLOUR_KEY_UP
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
    final var oldValue = e.getOldValue().toString();
    final var newValue = e.getNewValue().toString();
    final var switchValue = newValue.isEmpty() ? oldValue : newValue;

    final var hwSwitch = HardwareSwitch.valueFrom( switchName );
    final var hwState = HardwareState.valueFrom( newValue );

    final var switchState = new HardwareSwitchState(
        hwSwitch, hwState, switchValue );

    updateSwitchState( switchState );

    if( hwSwitch.isKeyboard() ) {
      updateSwitchLabel( switchState );
    }
  }

  protected void updateSwitchState( final HardwareSwitchState switchState ) {
    final var component = getHardwareComponent( switchState );
    component.setState( switchState );
  }

  private final ConsecutiveEventCounter<String> mKeyCounter =
      new ConsecutiveEventCounter<>( 9 );

  /**
   * Changes the text on labels when the state of a key changes.
   *
   * @param state The key that has changed.
   */
  protected void updateSwitchLabel( final HardwareSwitchState state ) {
    final var keyColour = KEY_COLOURS.get( state.getHardwareState() );
    final var pressed = state.isHardwareState( SWITCH_PRESSED );

    if( state.isModifier() ) {
      updateLabel( state, keyColour );

      if( pressed ) {
        mKeyCounter.reset();
      }
    }
    else {
      final var component = getHardwareComponent( state );
      final var keyValue = state.getValue();

      if( pressed ) {
        // A non-modifier key has been pressed.
        System.out.println( "KEY PRESSED: " + keyValue );

        // Determine whether there are separate parts for the key label.
        final var index = keyValue.indexOf( ' ' );

        final var calculator = new BoundsCalculator( component );
        final var bounds = calculator.getBounds();
        final var compDimen = new ScalableDimension( bounds.width, bounds.height );

        // If there's a space in the name, the text before the space is
        // positioned in the upper-left while the text afterwards takes up
        // the remainder. This is used for number pad keys, backspace, enter,
        // tab, and a few others.
        if( index > 0 ) {
          final var supSize = compDimen.scale( .6f );
          final var mainSize = compDimen.scale( .9f );

          final var s = new String[]{
              keyValue.substring( 0, index ),
              keyValue.substring( index + 1 )
          };

          // Label for "Num", "Back", "Tab", and other dual-labelled keys.
          final var sup = new AutofitLabel( s[ 0 ], LABEL_FONT );
          sup.setVisible( false );
          sup.setForeground( keyColour );
          sup.setVerticalAlignment( TOP );

          // Label for number pad keys or icon glyphs.
          final var main = new AutofitLabel( s[ 1 ], LABEL_FONT );
          main.setVisible( false );
          main.setForeground( keyColour );
          main.setHorizontalAlignment( CENTER );
          main.setVerticalAlignment( CENTER );

          // Keep removeAll/add operations close together to minimize flicker.
          component.removeAll();
          component.add( main );
          component.add( sup );
          main.setSize( mainSize );
          sup.setSize( supSize );

          // Center-align the main text with respect to the container.
          final var location = main.getLocation();
          final var dx = (compDimen.getWidth() - main.getWidth()) / 2;
          final var dy = (compDimen.getHeight() - main.getHeight()) / 2;

          // Shift the main text down a smidgen, relative to the superscript.
          final var my = (int) (location.getY() + dy) + sup.getHeight() / 4;
          final var mx = (int) (location.getX() + dx);

          main.setLocation( mx, my );
          main.setVisible( true );
          sup.setVisible( true );
        }
        else {
          component.removeAll();
          updateLabel( state, keyColour );
        }

        // Track the consecutive key presses for this value.
        if( mKeyCounter.apply( keyValue ) ) {
          final var count = mKeyCounter.toString();
          final var tallySize = compDimen.scale( .25f );

          final var tally = new AutofitLabel( count, LABEL_FONT );
          tally.setVisible( false );
          component.add( tally );

          tally.setSize( tallySize );
          tally.setVerticalAlignment( TOP );
          tally.setHorizontalAlignment( RIGHT );

          // Get the upper-left point, accounting for padding and insets.
          final var tx = bounds.x + compDimen.getWidth() - tally.getWidth();
          final var ty = bounds.y;

          tally.setLocation( (int)tx, ty );
          tally.setVisible( true );
        }
      }
      else {
        component.removeAll();
      }
    }
  }

  /**
   * Creates the label if it does not already exist.
   *
   * @param state The state of the hardware switch to look up.
   */
  private void updateLabel(
      final HardwareSwitchState state,
      final Color keyColour ) {
    final var container = getHardwareComponent( state );
    final var value = state.getValue();

    if( container.getComponentCount() == 0 ) {
      // Regular keys will have labels recreated each time to auto-fit the text.
      final var label = new AutofitLabel( value, LABEL_FONT );
      label.setVisible( false );
      label.setHorizontalAlignment( CENTER );
      label.setForeground( keyColour );
      container.add( label );
      label.setVisible( true );
    }
    else {
      // Modifier keys can reuse labels.
      final var label = (AutofitLabel) container.getComponent( 0 );
      label.setForeground( keyColour );
      label.setText( value );
    }
  }

  private HardwareComponent<HardwareSwitchState, Image> getHardwareComponent(
      final HardwareSwitchState state ) {
    return getHardwareImages().get( state.getHardwareSwitch() );
  }

  private HardwareImages getHardwareImages() {
    return mHardwareImages;
  }
}
