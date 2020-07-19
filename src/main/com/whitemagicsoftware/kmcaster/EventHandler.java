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

import java.awt.*;
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

    // True indicates a modifier key was pressed; false indicates a key was
    // released (doesn't matter what kind of key).
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
        final var label = new AutofitLabel( value, LABEL_FONT, LABEL_COLOUR );

        component.add( label );
      }
    }
  }
}
