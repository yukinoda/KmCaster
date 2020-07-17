package com.whitemagicsoftware.kmcaster.listeners;

import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;
import org.jnativehook.mouse.NativeMouseWheelEvent;
import org.jnativehook.mouse.NativeMouseWheelListener;

/**
 * Listens for all mouse events: clicks and mouse wheel scrolls.
 */
public class MouseListener implements
    NativeMouseInputListener, NativeMouseWheelListener {

  public void nativeMouseClicked( NativeMouseEvent e ) {
//    System.out.println( "Clicked # Times: " + e.getClickCount() );
//    System.out.println( "Button clicked: " + e.getButton() );
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
//    System.out.println( "Mouse Dragged: " + e.getX() + ", " + e.getY() );
  }

  public void nativeMouseWheelMoved( NativeMouseWheelEvent e ) {
    System.out.println( "Mouse Scroll: " + e.getWheelRotation() );
  }
}
