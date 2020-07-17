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

import com.whitemagicsoftware.kmcaster.listeners.KeyboardListener;
import com.whitemagicsoftware.kmcaster.listeners.MouseListener;
import org.jnativehook.GlobalScreen;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;

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
public class KmCaster extends EventFrame implements PropertyChangeListener {

  public KmCaster() {
    final MouseListener mouseEventListener = new MouseListener();
    addNativeMouseListener( mouseEventListener );
    addNativeMouseMotionListener( mouseEventListener );
    addNativeMouseWheelListener( mouseEventListener );

    final KeyboardListener keyboardListener = new KeyboardListener();
    addNativeKeyListener( keyboardListener );
    keyboardListener.addPropertyChangeListener( this );
  }

  @Override
  public void propertyChange( final PropertyChangeEvent e ) {
    var switchValue = e.getNewValue().toString();

    if( !"false".equals( switchValue ) && !"true".equals( switchValue ) ) {
      switchValue = "*";
    }

    final var switchState = createState( e.getPropertyName(), switchValue );
    updateSwitchState( switchState );
    updateSwitchLabel( switchState.getKey(), switchValue );
  }

  private HardwareState createState(
      final String name, final String state ) {
    assert name != null;
    assert state != null;

    final var key = SwitchName.valueFrom( name );

    return new HardwareState( key, state );
  }

  /**
   * Initialize the key and mouse event listener native interface.
   */
  private static void initNativeHook() {
    try {
      registerNativeHook();

      final var logger = getLogger( GlobalScreen.class.getPackage().getName() );
      logger.setLevel( Level.OFF );
      logger.setUseParentHandlers( false );
    } catch( final Exception ex ) {
      rethrow( ex );
    }
  }

  /**
   * Main entry point.
   *
   * @param args Unused.
   */
  public static void main( final String[] args ) {
    initNativeHook();

    invokeLater( () -> {
      final var kc = new KmCaster();
      kc.setVisible( true );
    } );
  }

  /**
   * Cast a checked exception as a {@link RuntimeException}.
   *
   * @param <T>       What type of {@link Throwable} to throw.
   * @param throwable The problem to cast.
   * @throws T The throwable is casted to this type.
   */
  @SuppressWarnings("unchecked")
  public static <T extends Throwable> void rethrow( final Throwable throwable )
      throws T {
    throw (T) throwable;
  }
}
