package com.whitemagicsoftware.kmcaster;

import com.whitemagicsoftware.kmcaster.listeners.FrameDragListener;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

class EventFrame extends JFrame {

  private static final float ARC = 8;
  private static final int FRAME_WIDTH = 380;
  private static final int FRAME_HEIGHT = 60;

  public EventFrame() {
    setUndecorated( true );
    setAlwaysOnTop( true );
    setBackground( new Color( .2f, .2f, .2f, 0.5f ) );

    setLocationRelativeTo( null );
    setSize( FRAME_WIDTH, FRAME_HEIGHT );
    setShape( new RoundRectangle2D.Double(
        0, 0, getWidth(), getHeight(), ARC, ARC ) );

    final var frameDragListener = new FrameDragListener( this );
    addMouseListener( frameDragListener );
    addMouseMotionListener( frameDragListener );
  }
}
