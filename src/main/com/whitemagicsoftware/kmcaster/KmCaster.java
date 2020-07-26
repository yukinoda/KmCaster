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
import com.whitemagicsoftware.kmcaster.ui.TranslucentPanel;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import picocli.CommandLine.Command;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URISyntaxException;

import static com.whitemagicsoftware.kmcaster.ui.Constants.*;
import static com.whitemagicsoftware.kmcaster.ui.FontLoader.initFonts;
import static java.util.logging.Level.OFF;
import static java.util.logging.Logger.getLogger;
import static javax.swing.SwingUtilities.invokeLater;
import static org.jnativehook.GlobalScreen.*;

/**
 * This class is responsible for casting key presses and mouse clicks on the
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
@Command(
    name = "KmCaster",
    mixinStandardHelpOptions = true,
    description = "Displays key presses and mouse clicks on the screen."
)
public class KmCaster extends JFrame {

  /**
   * Application dimensions in pixels. Images are scaled to these dimensions,
   * maintaining aspect ratio. The height constrains the width, so as long as
   * the width is large enough, the application's window will adjust to fit.
   */
  public static final Dimension APP_DIMENSIONS = new Dimension( 1024, 70 );

  /**
   * Milliseconds to wait before releasing (clearing) the regular key.
   */
  public final static int DELAY_KEY_REGULAR = 250;

  /**
   * Milliseconds to wait before releasing (clearing) any modifier key.
   */
  public final static int DELAY_KEY_MODIFIER = 150;

  private final HardwareImages mHardwareImages;
  private final EventHandler mEventHandler;

  /**
   * Create an instance then call {@link #init()} from within the
   * {@link SwingUtilities#invokeLater(Runnable)} thread.
   */
  public KmCaster() {
    mHardwareImages = new HardwareImages( APP_DIMENSIONS );
    mEventHandler = new EventHandler( mHardwareImages );
  }

  private void init() {
    initWindowFrame();
    initWindowContents();
    pack();
    setResizable( false );
    initListeners();
    setVisible( true );
  }

  private void initWindowFrame() {
    setDefaultCloseOperation( EXIT_ON_CLOSE );
    setLocationRelativeTo( null );
    setUndecorated( true );
    setAlwaysOnTop( true );
    setBackground( TRANSLUCENT );

    // Prevent tabbing to non-existent components.
    setFocusTraversalKeysEnabled( false );
  }

  private void initWindowContents() {
    final var panel = new TranslucentPanel();

    for( final var hwSwitch : HardwareSwitch.values() ) {
      final var component = mHardwareImages.get( hwSwitch );
      panel.add( component );
    }

    getContentPane().add( panel );
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
    final KeyboardListener keyboardListener = new KeyboardListener(
        DELAY_KEY_REGULAR, DELAY_KEY_MODIFIER
    );
    addNativeKeyListener( keyboardListener );
    keyboardListener.addPropertyChangeListener( listener );
    keyboardListener.initModifiers();
  }

  private EventHandler getEventHandler() {
    return mEventHandler;
  }

  /**
   * Suppress writing logging messages to standard output.
   */
  private static void disableNativeHookLogger() {
    final var logger = getLogger( GlobalScreen.class.getPackage().getName() );
    logger.setLevel( OFF );
    logger.setUseParentHandlers( false );
  }

  /**
   * Main entry point.
   *
   * @param args Unused.
   */
  public static void main( final String[] args )
      throws NativeHookException, IOException, URISyntaxException {
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
