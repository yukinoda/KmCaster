package com.whitemagicsoftware.kmcaster.listeners;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FrameDragListener extends MouseAdapter {
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
