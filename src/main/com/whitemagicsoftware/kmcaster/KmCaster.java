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
import com.whitemagicsoftware.kmcaster.listeners.KeyboardListener;
import com.whitemagicsoftware.kmcaster.listeners.MouseListener;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;

import static com.whitemagicsoftware.kmcaster.ui.Constants.TRANSLUCENT;
import static com.whitemagicsoftware.kmcaster.ui.Constants.TRANSPARENT;
import static com.whitemagicsoftware.kmcaster.ui.FontLoader.initFonts;
import static java.util.logging.Logger.getLogger;
import static javax.swing.SwingUtilities.invokeLater;
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
public class KmCaster extends JFrame {
  private static final float ARC = 8;

  /**
   * Fastest typing speed in words per minute.
   */
  private final static float TYPING_SPEED_WPM = 216f;

  /**
   * Fastest typing speed in words per second.
   */
  private final static float TYPING_SPEED_WPS = TYPING_SPEED_WPM / 60f;

  /**
   * Fastest typing speed in characters per second.
   */
  private final static float TYPING_SPEED_CPS = TYPING_SPEED_WPS * 5.1f;

  /**
   * Fastest typing speed in characters per millisecond, which will
   * govern the speed that any pressed key remains visible before showing
   * as released, even if the typist released the key sooner.
   */
  private final static float TYPING_SPEED_CPMS = TYPING_SPEED_CPS / 1000;

  private final HardwareImages mHardwareImages = new HardwareImages();
  private final EventHandler mEventHandler =
      new EventHandler( mHardwareImages );

  /**
   * Empty constructor; create an instance then call {@link #init()} from
   * within the {@link SwingUtilities#invokeLater(Runnable)} thread.
   */
  public KmCaster() {
  }

  private void init() {
    initWindowFrame();
    initWindowContents();
    initListeners();
    pack();
    setVisible( true );
  }

  private void initWindowFrame() {
    setDefaultCloseOperation( EXIT_ON_CLOSE );
    setLocationRelativeTo( null );
    setUndecorated( true );
    setAlwaysOnTop( true );
    setBackground( TRANSPARENT );
    setShape( createShape() );
  }

  private void initWindowContents() {
    final var switchPanel = new JPanel( true );
    switchPanel.setAlignmentX( CENTER_ALIGNMENT );
    switchPanel.setBackground( TRANSLUCENT );

    // Added using the enumerated type definition declaration order.
    for( final var hwSwitch : HardwareSwitch.values() ) {
      switchPanel.add( mHardwareImages.get( hwSwitch ) );
    }

    add( switchPanel );
  }

  private void initListeners() {
    initWindowDragListener( this );
    initMouseListener( getEventHandler() );
    initKeyboardListener( getEventHandler() );
  }

  private void initWindowDragListener( final JFrame listener ) {
    final var frameDragListener = new FrameDragListener( listener );
    addMouseListener( frameDragListener );
    addMouseMotionListener( frameDragListener );
  }

  private void initMouseListener( final PropertyChangeListener listener ) {
    final MouseListener mouseListener = new MouseListener();
    addNativeMouseListener( mouseListener );
    addNativeMouseMotionListener( mouseListener );
    addNativeMouseWheelListener( mouseListener );
    mouseListener.addPropertyChangeListener( listener );
  }

  private void initKeyboardListener( final PropertyChangeListener listener ) {
    final KeyboardListener keyboardListener = new KeyboardListener();
    addNativeKeyListener( keyboardListener );
    keyboardListener.addPropertyChangeListener( listener );
  }

  /**
   * Returns the shape for the application's window frame.
   *
   * @return A rounded rectangle.
   */
  private Shape createShape() {
    return new RoundRectangle2D.Double(
        0, 0, getWidth(), getHeight(), ARC, ARC
    );
  }

  private EventHandler getEventHandler() {
    return mEventHandler;
  }

  /**
   * Suppress writing logging messages to standard output.
   */
  private static void disableNativeHookLogger() {
    final var logger = getLogger( GlobalScreen.class.getPackage().getName() );
    logger.setLevel( Level.OFF );
    logger.setUseParentHandlers( false );
  }

  /**
   * Main entry point.
   *
   * @param args Unused.
   */
  public static void main( final String[] args ) throws NativeHookException {
    initFonts();
    disableNativeHookLogger();
    registerNativeHook();

    while( !isNativeHookRegistered() ) {
      Thread.yield();
    }

    final var kc = new KmCaster();
    invokeLater( kc::init );
  }
}
