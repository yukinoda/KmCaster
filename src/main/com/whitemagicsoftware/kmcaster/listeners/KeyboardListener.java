package com.whitemagicsoftware.kmcaster.listeners;

import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import static org.jnativehook.NativeInputEvent.SHIFT_MASK;
import static org.jnativehook.keyboard.NativeKeyEvent.getKeyText;

public class KeyboardListener implements NativeKeyListener {
  public void nativeKeyPressed( final NativeKeyEvent e ) {
    System.out.println( "Key Pressed: " + getKeyText( e.getKeyCode() ) );

    if( isShiftDown( e ) ) {
      System.out.println( "SHIFTING" );
    }
  }

  public void nativeKeyReleased( final NativeKeyEvent e ) {
    System.out.println( "Key Released: " + getKeyText( e.getKeyCode() ) );
  }

  public void nativeKeyTyped( final NativeKeyEvent e ) {
    System.out.println( "Key Typed: " + getKeyText( e.getKeyCode() ) );
  }

  private boolean isShiftDown( final NativeKeyEvent e ) {
    return (e.getModifiers() & SHIFT_MASK) != 0;
  }
}
