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

/**
 * Responsible for drawing an image, which can be changed at any time.
 */
public class ImageComponent extends JComponent {
  /**
   * Mutable image.
   */
  private Image mImage;

  ImageComponent( final Image image ) {
    mImage = image;
  }

  @Override
  public Dimension getPreferredSize() {
    // Race-condition guard.
    final var image = mImage;

    return new Dimension(
        image.getWidth( null ), image.getHeight( null )
    );
  }

  @Override
  protected void paintComponent( final Graphics graphics ) {
    super.paintComponent( graphics );

    final var g = (Graphics2D) graphics.create();
    g.drawImage( mImage, 0, 0, this );
  }

  /**
   * Repaints this component using the given image. This is a mutable
   * operation that changes the internal {@link Image} instance.
   *
   * @param image The new image to use for painting.
   */
  public void redraw( final Image image ) {
    mImage = image;
    repaint();
  }
}
