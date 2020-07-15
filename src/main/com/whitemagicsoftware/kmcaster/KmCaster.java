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

import org.jnativehook.NativeHookException;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

import static org.jnativehook.GlobalScreen.*;

/**
 * This class is responsible for logging key presses and mouse clicks on the
 * screen. While there is a plethora of software out here that does this,
 * none meet all the following criteria: small size, easily positioned,
 * show single key stroke or chord, shows left/right mouse clicks, shows
 * release of modifier keys when quickly typing, and traps all keys typed
 * from within Sikuli.
 * <p>
 * When using the XFCE Window Manager, enable compositing as follows:
 * </p>
 * <ol>
 *   <li>Open the Xfce Applications Menu.</li>
 *   <li>Click Settings, Settings Editor.</li>
 *   <li>Select xfwm4, near the bottom left.</li>
 *   <li>Check the value column for use_compositing.</li>
 * </ol>
 */
@SuppressWarnings("unused")
public class KmCaster {
  private static final float ARC = 8;
  private static final int FRAME_WIDTH = 380;
  private static final int FRAME_HEIGHT = 60;

  public static class MouseListener implements NativeMouseInputListener {
    public void nativeMouseClicked( NativeMouseEvent e ) {
//      System.out.println( "Mouse Clicked: " + e.getClickCount() );
    }

    public void nativeMousePressed( NativeMouseEvent e ) {
//      System.out.println( "Mouse Pressed: " + e.getButton() );
    }

    public void nativeMouseReleased( NativeMouseEvent e ) {
//      System.out.println( "Mouse Released: " + e.getButton() );
    }

    public void nativeMouseMoved( NativeMouseEvent e ) {
//      System.out.println( "Mouse Moved: " + e.getX() + ", " + e.getY() );
    }

    public void nativeMouseDragged( NativeMouseEvent e ) {
//      System.out.println( "Mouse Dragged: " + e.getX() + ", " + e.getY() );
    }
  }

  public static class FrameDragListener extends MouseAdapter {
    private final JFrame mFrame;
    private Point mInitCoordinates;

    public FrameDragListener( final JFrame frame ) {
      mFrame = frame;
    }

    public void mouseReleased( final MouseEvent e ) {
      mInitCoordinates = null;
    }

    public void mousePressed( final MouseEvent e ) {
      mInitCoordinates = e.getPoint();
    }

    public void mouseDragged( final MouseEvent e ) {
      final Point dragCoordinates = e.getLocationOnScreen();
      mFrame.setLocation( dragCoordinates.x - mInitCoordinates.x,
                          dragCoordinates.y - mInitCoordinates.y );
    }
  }

  private static class EventFrame extends JFrame {
    public EventFrame() {
      setUndecorated( true );
      setAlwaysOnTop( true );
      setBackground( new Color( .2f, .2f, .2f, 0.5f ) );

      setLocationRelativeTo( null );
      setSize( FRAME_WIDTH, FRAME_HEIGHT );
      setShape( new RoundRectangle2D.Double(
          0, 0, getWidth(), getHeight(), ARC, ARC ) );

      FrameDragListener frameDragListener = new FrameDragListener( this );
      addMouseListener( frameDragListener );
      addMouseMotionListener( frameDragListener );
    }
  }

  private final EventFrame mFrame = new EventFrame();

  public KmCaster() {
    final MouseListener mouseListener = new MouseListener();

    // Add the appropriate listeners.
    addNativeMouseListener( mouseListener );
    addNativeMouseMotionListener( mouseListener );
  }

  public void show() {
    mFrame.setVisible( true );
  }

  public static void main( final String[] args ) {
    try {
      registerNativeHook();

      final var kc = new KmCaster();
      kc.show();
    } catch( final NativeHookException ex ) {
      ex.printStackTrace();
    }
  }
}
