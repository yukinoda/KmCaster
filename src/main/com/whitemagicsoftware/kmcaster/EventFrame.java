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

import static com.whitemagicsoftware.kmcaster.AppImage.*;

class EventFrame extends JFrame {

  private static final float ARC = 8;
  private static final Dimension FRAME_DIMENSIONS = new Dimension( 484, 70 );
  private static final Color TRANSLUCENT = new Color( .2f, .2f, .2f, 0.5f );
  private static final Color TRANSPARENT = new Color( 0, 0, 0, 0 );

  public EventFrame() {
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
    final var mouseView = MOUSE_LR.toComponent( dimensions );
    final var shiftView = KEY_UP_SHIFT.toComponent( dimensions );
    final var ctrlView = KEY_UP_CTRL.toComponent( dimensions );
    final var altView = KEY_UP_ALT.toComponent( dimensions );
    final var regularView = KEY_UP_REGULAR.toComponent( dimensions );

    final var panel = new JPanel();
    panel.setAlignmentX( Component.CENTER_ALIGNMENT );
    panel.setBackground( TRANSLUCENT );
    panel.add( mouseView );
    panel.add( shiftView );
    panel.add( ctrlView );
    panel.add( altView );
    panel.add( regularView );

    final var content = getContentPane();
    final var layout = new BoxLayout( content, BoxLayout.Y_AXIS );

    content.setLayout( layout );
    content.add( panel );
  }

  private Shape createShape() {
    return new RoundRectangle2D.Double(
        0, 0, getWidth(), getHeight(), ARC, ARC
    );
  }
}
