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

import com.whitemagicsoftware.kmcaster.listeners.FrameDragListener;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.Map;

import static com.whitemagicsoftware.kmcaster.HardwareSwitches.state;
import static com.whitemagicsoftware.kmcaster.SwitchName.*;

public class EventFrame extends JFrame {

  private static final float ARC = 8;
  private static final Dimension FRAME_DIMENSIONS = new Dimension( 484, 70 );
  private static final Color TRANSLUCENT = new Color( .2f, .2f, .2f, 0.5f );
  private static final Color TRANSPARENT = new Color( 0, 0, 0, 0 );

  private final HardwareSwitches mSwitches;
  private final Map<SwitchName, ImageComponent> mSwitchViews = new HashMap<>();

  public EventFrame() {
    setDefaultCloseOperation( EXIT_ON_CLOSE );
    setLocationRelativeTo( null );
    setUndecorated( true );
    setAlwaysOnTop( true );
    setBackground( TRANSPARENT );
    setSize( FRAME_DIMENSIONS );
    setShape( createShape() );

    final var frameDragListener = new FrameDragListener( this );
    addMouseListener( frameDragListener );
    addMouseMotionListener( frameDragListener );

    final var dimensions = new Dimension( getWidth(), getHeight() - 10 );
    mSwitches = new HardwareSwitches( dimensions );

    final var mouseImage = mSwitches.get( state( MOUSE_LEFT, false ) );
    final var mouseComponent = createImageComponent( mouseImage );

    final var shiftUpImage = mSwitches.get( state( KEY_SHIFT, false ) );
    final var ctrlUpImage = mSwitches.get( state( KEY_CTRL, false ) );
    final var altUpImage = mSwitches.get( state( KEY_ALT, false ) );
    final var regularUpImage = mSwitches.get( state( KEY_REGULAR, false ) );

    final var shiftComponent = createImageComponent( shiftUpImage );
    final var ctrlComponent = createImageComponent( ctrlUpImage );
    final var altComponent = createImageComponent( altUpImage );
    final var regularComponent = createImageComponent( regularUpImage );

    final var panel = new JPanel();
    panel.setAlignmentX( Component.CENTER_ALIGNMENT );
    panel.setBackground( TRANSLUCENT );
    panel.add( mouseComponent );
    panel.add( shiftComponent );
    panel.add( ctrlComponent );
    panel.add( altComponent );
    panel.add( regularComponent );

    final var content = getContentPane();
    final var layout = new BoxLayout( content, BoxLayout.Y_AXIS );

    content.setLayout( layout );
    content.add( panel );

    mSwitchViews.put( KEY_SHIFT, shiftComponent );
    mSwitchViews.put( KEY_CTRL, ctrlComponent );
    mSwitchViews.put( KEY_ALT, altComponent );
    mSwitchViews.put( KEY_REGULAR, regularComponent );
  }

  protected void updateSwitchState( final HardwareState keyState ) {
    final var image = mSwitches.get( keyState );
    final var component = mSwitchViews.get( keyState.getKey() );

    System.out.println( keyState );

    component.redraw( image );
  }

  protected void updateSwitchLabel(
      final SwitchName name, final String label ) {
    System.out.printf( "%s = %s%n", name, label );
  }

  private ImageComponent createImageComponent( final Image image ) {
    return new ImageComponent( image );
  }

  private Shape createShape() {
    return new RoundRectangle2D.Double(
        0, 0, getWidth(), getHeight(), ARC, ARC
    );
  }
}
