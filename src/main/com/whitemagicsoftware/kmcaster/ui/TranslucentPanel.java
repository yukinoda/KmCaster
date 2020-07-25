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
package com.whitemagicsoftware.kmcaster.ui;

import javax.swing.*;
import java.awt.*;

import static com.whitemagicsoftware.kmcaster.ui.Constants.TRANSLUCENT;
import static com.whitemagicsoftware.kmcaster.ui.Constants.TRANSPARENT;

/**
 * Renders a panel---and its borders---as a translucent colour.
 */
public class TranslucentPanel extends JPanel {
  public TranslucentPanel() {
    setOpaque( true );
  }

  @Override
  public void paintComponent( final Graphics g ) {
    super.paintComponent( g );

    final var graphics = (Graphics2D) g;

    // https://docs.oracle.com/javase/tutorial/2d/advanced/compositing.html
    graphics.setComposite( AlphaComposite.Src );
    graphics.setBackground( TRANSLUCENT );
    graphics.setColor( TRANSPARENT );

    final var r = graphics.getClipBounds();
    graphics.fillRect( r.x, r.y, r.width, r.height );
  }
}