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
package com.whitemagicsoftware.kmcaster.listeners;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static java.awt.Cursor.MOVE_CURSOR;
import static java.awt.Cursor.getPredefinedCursor;

/**
 * Responsible for moving the window when the user drags it around the screen.
 */
public final class FrameDragListener extends MouseAdapter {
  /**
   *
   */
  private final static Cursor DRAGGING =
      getPredefinedCursor( MOVE_CURSOR );

  /**
   * Observed for drag events.
   */
  private final JFrame mFrame;

  /**
   * Initial coordinates.
   */
  private Point mCoordinates;

  /**
   * Stores the previous cursor type when dragging so that it can be
   * restored when the mouse button is released.
   */
  private Cursor mCursor;

  public FrameDragListener( final JFrame frame ) {
    mFrame = frame;
  }

  /**
   * Restores the state to before dragging started.
   *
   * @param e The mouse button release event.
   */
  @Override
  public void mouseReleased( final MouseEvent e ) {
    // Race-condition guards.
    final var frame = mFrame;
    final var cursor = mCursor;

    if( frame != null && cursor != null ) {
      frame.setCursor( cursor );
    }

    mCoordinates = null;
  }

  @Override
  public void mousePressed( final MouseEvent e ) {
    mCoordinates = e.getPoint();
  }

  @Override
  public void mouseDragged( final MouseEvent e ) {
    // Race-condition guards.
    final var frame = mFrame;
    final var coordinates = mCoordinates;

    // Used to calculate delta between current and previous mouse position.
    final var dragCoordinates = e.getLocationOnScreen();

    if( frame != null && coordinates != null ) {
      final var cursor = frame.getCursor();

      if( cursor != DRAGGING ) {
        mCursor = cursor;
      }

      frame.setCursor( DRAGGING );
      frame.setLocation( dragCoordinates.x - coordinates.x,
                         dragCoordinates.y - coordinates.y );
    }
  }
}
