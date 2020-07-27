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
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Help.Ansi.Style;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.Callable;

import static com.whitemagicsoftware.kmcaster.ui.Constants.TRANSLUCENT;
import static com.whitemagicsoftware.kmcaster.ui.FontLoader.initFonts;
import static java.util.logging.Level.OFF;
import static java.util.logging.Logger.getLogger;
import static javax.swing.SwingUtilities.invokeLater;
import static org.jnativehook.GlobalScreen.*;
import static picocli.CommandLine.Help.ColorScheme;
import static picocli.CommandLine.Option;

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
@SuppressWarnings("FieldMayBeFinal")
public class KmCaster extends JFrame implements Callable<Integer> {

  /**
   * Application height in pixels. Images are scaled to this height, maintaining
   * aspect ratio. The height constrains the width, so as long as the width
   * is large enough, the application's window will adjust to fit.
   */
  @Option(
      names = {"-s", "--size"},
      description = "Application size (${DEFAULT-VALUE} pixels)",
      paramLabel = "height",
      defaultValue = "100"
  )
  private int mHeight = 100;

  /**
   * Milliseconds to wait before releasing (clearing) the regular key.
   */
  @Option(
      names = {"-a", "--delay-alphanum"},
      description = "Delay for releasing non-modifier keys (${DEFAULT-VALUE} " +
          "milliseconds)",
      paramLabel = "delay",
      defaultValue = "250"
  )
  private int mDelayKeyRegular = 250;

  /**
   * Milliseconds to wait before releasing (clearing) any modifier key.
   */
  @Option(
      names = {"-m", "--delay-modifier"},
      description = "Delay for releasing modifier keys (${DEFAULT-VALUE} " +
          "milliseconds)",
      paramLabel = "delay",
      defaultValue = "150"
  )
  private int mDelayKeyModifier = 150;

  /**
   * Empty constructor so that command line arguments may be parsed.
   */
  public KmCaster() {
    super( KmCaster.class.getSimpleName() );
  }

  private void init() {
    final var appDimension = new Dimension( 1024 + mHeight, mHeight );
    final var hardwareImages = new HardwareImages( appDimension );
    final var eventHandler = new EventHandler( hardwareImages );

    initWindowFrame();
    initWindowContents( hardwareImages );
    pack();
    setResizable( false );
    initListeners( eventHandler );
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

  private void initWindowContents( final HardwareImages hardwareImages ) {
    final var panel = new TranslucentPanel();

    for( final var hwSwitch : HardwareSwitch.values() ) {
      final var component = hardwareImages.get( hwSwitch );
      panel.add( component );
    }

    getContentPane().add( panel );
  }

  private void initListeners( final EventHandler eventHandler ) {
    initWindowDragListener( this );
    initMouseListener( eventHandler );
    initKeyboardListener( eventHandler );
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
        mDelayKeyRegular, mDelayKeyModifier
    );
    addNativeKeyListener( keyboardListener );
    keyboardListener.addPropertyChangeListener( listener );
    keyboardListener.initModifiers();
  }

  /**
   * Suppress writing logging messages to standard output.
   */
  private static void disableNativeHookLogger() {
    final var logger = getLogger( GlobalScreen.class.getPackage().getName() );
    logger.setLevel( OFF );
    logger.setUseParentHandlers( false );
  }

  private static ColorScheme createColourScheme() {
    return new ColorScheme.Builder()
        .commands( Style.bold )
        .options( Style.fg_blue, Style.bold )
        .parameters( Style.fg_blue )
        .optionParams( Style.italic )
        .errors( Style.fg_red, Style.bold )
        .stackTraces( Style.italic )
        .build();
  }

  /**
   * Invoked after the command-line arguments are parsed.
   *
   * @return Exit level zero.
   */
  @Override
  public Integer call() {
    init();
    return 0;
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
    final var parser = new CommandLine( kc );
    parser.setColorScheme( createColourScheme() );

    invokeLater( () -> {
      final var exitCode = parser.execute( args );
      final var parseResult = parser.getParseResult();

      if( parseResult.isUsageHelpRequested() ) {
        System.exit( exitCode );
      }
    } );
  }
}
