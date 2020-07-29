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
import com.whitemagicsoftware.kmcaster.util.ConsecutiveEventCounter;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.Optional;

import static com.whitemagicsoftware.kmcaster.HardwareState.SWITCH_PRESSED;
import static com.whitemagicsoftware.kmcaster.HardwareState.SWITCH_RELEASED;
import static com.whitemagicsoftware.kmcaster.HardwareSwitch.*;
import static com.whitemagicsoftware.kmcaster.ui.Constants.*;
import static java.awt.Toolkit.getDefaultToolkit;
import static javax.swing.SwingConstants.*;
import static javax.swing.SwingUtilities.invokeLater;

/**
 * Responsible for controlling the application state between the events
 * and the view.
 */
public final class EventHandler implements PropertyChangeListener {

  /**
   * Used for initializing the {@link AutofitLabel} instances.
   */
  private enum LabelConfig {
    LABEL_SHIFT( KEY_SHIFT, CENTER, CENTER ),
    LABEL_CTRL( KEY_CTRL, CENTER, CENTER ),
    LABEL_ALT( KEY_ALT, CENTER, CENTER ),
    LABEL_REGULAR( KEY_REGULAR, CENTER, CENTER ),
    LABEL_REGULAR_NUM_MAIN( CENTER, CENTER ),
    LABEL_REGULAR_NUM_SUPERSCRIPT( TOP, LEFT ),
    LABEL_REGULAR_COUNTER( TOP, RIGHT );

    private final HardwareSwitch mHardwareSwitch;
    private final int mHorizontalAlign;
    private final int mVerticalAlign;

    LabelConfig( final int vAlign, final int hAlign ) {
      this( null, vAlign, hAlign );
    }

    LabelConfig(
        final HardwareSwitch hwSwitch, final int vAlign, final int hAlign ) {
      mHardwareSwitch = hwSwitch;
      mVerticalAlign = vAlign;
      mHorizontalAlign = hAlign;
    }

    private Optional<HardwareSwitch> getHardwareSwitch() {
      return Optional.ofNullable( mHardwareSwitch );
    }

    private int getHorizontalAlign() {
      return mHorizontalAlign;
    }

    private int getVerticalAlign() {
      return mVerticalAlign;
    }

    /**
     * Returns a blank space when no {@link HardwareSwitch} is assigned.
     *
     * @return The title case version of the hardware switch, or a space if
     * there is no direct correlation.
     */
    private String toTitleCase() {
      return mHardwareSwitch == null ? " " : mHardwareSwitch.toTitleCase();
    }

    /**
     * Returns the number of values in the enumeration.
     *
     * @return {@link #values()}.length.
     */
    private static int size() {
      return values().length;
    }
  }

  /**
   * Maps key pressed states to key cap title colours.
   */
  private static final Map<HardwareState, Color> KEY_COLOURS = Map.of(
      SWITCH_PRESSED, COLOUR_KEY_DN,
      SWITCH_RELEASED, COLOUR_KEY_UP
  );

  private final HardwareImages mHardwareImages;
  private final AutofitLabel[] mLabels = new AutofitLabel[ LabelConfig.size() ];

  public EventHandler( final HardwareImages hardwareImages ) {
    mHardwareImages = hardwareImages;

    final var keyColour = KEY_COLOURS.get( SWITCH_PRESSED );

    for( final var config : LabelConfig.values() ) {
      final var label = new AutofitLabel( config.toTitleCase(), LABEL_FONT );

      label.setVerticalAlignment( config.getVerticalAlign() );
      label.setHorizontalAlignment( config.getHorizontalAlign() );
      label.setForeground( keyColour );

      mLabels[ config.ordinal() ] = label;

      config.getHardwareSwitch().ifPresentOrElse(
          s -> mHardwareImages.get( s ).add( label ),
          () -> mHardwareImages.get( KEY_REGULAR ).add( label )
      );
    }
  }

  /**
   * Called when a hardware switch has changed state.
   *
   * @param e Contains the identifier for the switch, its previous value,
   *          and its new value.
   */
  @Override
  public void propertyChange( final PropertyChangeEvent e ) {
    invokeLater(
        () -> {
          update( e );

          // Prevent collapsing multiple paint events.
          getDefaultToolkit().sync();
        }
    );
  }

  /**
   * Called to update the user interface after a keyboard or mouse event
   * has fired. This must be invoked from Swing's event dispatch thread.
   *
   * @param e Contains the identifier for the switch, its previous value,
   *          and its new value.
   */
  private void update( final PropertyChangeEvent e ) {
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
    final var hwState = state.getHardwareState();

    getLabel( LabelConfig.LABEL_REGULAR ).setVisible( false );
    getLabel( LabelConfig.LABEL_REGULAR_COUNTER ).setVisible( false );

    if( state.isModifier() ) {
      updateLabel( state );

      mKeyCounter.reset();
    }
    else {
      // Hide any previously displayed labels.
      final var main = getLabel( LabelConfig.LABEL_REGULAR_NUM_MAIN );
      final var sup = getLabel( LabelConfig.LABEL_REGULAR_NUM_SUPERSCRIPT );
      main.setVisible( false );
      sup.setVisible( false );

      if( hwState == SWITCH_PRESSED ) {
        final var keyValue = state.getValue();

        // Determine whether there are separate parts for the key label.
        final var index = keyValue.indexOf( ' ' );

        // If there's a space in the name, the text before the space is
        // positioned in the upper-left while the text afterwards takes up
        // the remainder. This is used for number pad keys, backspace, enter,
        // tab, and a few others.
        if( index > 0 ) {
          // Label for "Num", "Back", "Tab", and other dual-labelled keys.
          sup.setText( keyValue.substring( 0, index ) );
          sup.transform( .6f );

          // Label for number pad keys or icon glyphs.
          main.setText( keyValue.substring( index + 1 ) );
          main.transform( .9f );

          // Shift the main label down away from the superscript.
          final var mainLoc = main.getLocation();
          main.setLocation( mainLoc.x, mainLoc.y + (sup.getHeight() / 3) );

          main.setVisible( true );
          sup.setVisible( true );
        }
        else {
          updateLabel( state );
        }

        // Track the consecutive key presses for this value.
        if( mKeyCounter.apply( keyValue ) ) {
          final var tally = getLabel( LabelConfig.LABEL_REGULAR_COUNTER );

          tally.setText( mKeyCounter.toString() );
          tally.transform( .25f );
          tally.setVisible( true );
        }
      }
    }
  }

  /**
   * Changes the text label and colour for the given state.
   *
   * @param state The state of the hardware switch to look up.
   */
  private void updateLabel( final HardwareSwitchState state ) {
    final var container = getHardwareComponent( state );
    final var label = (AutofitLabel) container.getComponent( 0 );

    label.setVisible( false );
    label.setForeground( KEY_COLOURS.get( state.getHardwareState() ) );
    label.setText( state.getValue() );
    label.transform();
    label.setVisible( true );
  }

  private HardwareComponent<HardwareSwitchState, Image> getHardwareComponent(
      final HardwareSwitchState state ) {
    return mHardwareImages.get( state.getHardwareSwitch() );
  }

  private AutofitLabel getLabel( final LabelConfig config ) {
    return mLabels[ config.ordinal() ];
  }
}
