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

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Responsible for drawing an image based on a state; the state can be
 * changed at any time.
 *
 * @param <S> The type of state associated with an image.
 */
public final class HardwareComponent
    <S extends HardwareSwitchState, I extends Image> extends JComponent {

  private final Map<S, I> mStateImages = new HashMap<>();

  /**
   * State that corresponds with the {@link Image} to paint.
   */
  private S mState;

  /**
   * Available space on the image for drawing.
   */
  private final Insets mInsets;

  private Dimension mPreferredSize;

  /**
   * Constructs a new {@link HardwareComponent} without an initial state. The
   * initial state must be set by calling {@link #setState(S)}
   * or {@link #put(S, Image)} before drawing the image.
   *
   * @param insets The padding to use around the component so that letters
   *               can be drawn within a safe region, without extending beyond
   *               what we'd expected to see visually for key cap text.
   */
  public HardwareComponent( final Insets insets ) {
    assert insets != null;

    mInsets = insets;
  }

  @Override
  public Dimension getPreferredSize() {
    return mPreferredSize == null
        ? mPreferredSize = calcPreferredSize()
        : mPreferredSize;
  }

  @Override
  public Insets getInsets() {
    return mInsets;
  }

  @Override
  protected void paintComponent( final Graphics g ) {
    g.drawImage( getActiveImage(), 0, 0, this );
  }

  /**
   * Associates a new (or existing) state with the given image. This sets
   * changes the current state to the given state.
   *
   * @param hwSwitch The state to associate with an image.
   * @param image The image to paint when the given state is selected.
   */
  public void put( final S hwSwitch, final I image ) {
    getStateImages().put( hwSwitch, image );

    // Change the state variable directly, no need to issue a repaint request.
    mState = hwSwitch;
  }

  /**
   * Repaints this component by changing its mutable state. The new state
   * must have been previously registered via {@link #put(S, Image)}.
   *
   * @param state The new state.
   */
  public void setState( final S state ) {
    assert state != null;

    if( !state.equals( mState ) ) {
      mState = state;
      repaint();
    }
  }

  public S getState() {
    return mState;
  }

  private Dimension calcPreferredSize() {
    // Race-condition guard.
    final var image = getActiveImage();

    return new Dimension(
        image.getWidth( null ), image.getHeight( null )
    );
  }

  private I getActiveImage() {
    return getStateImages().get( getState() );
  }

  private Map<S, I> getStateImages() {
    return mStateImages;
  }
}
