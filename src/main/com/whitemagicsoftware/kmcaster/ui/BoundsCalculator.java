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

import java.awt.*;

/**
 * Responsible for computing the safe region for displaying information on
 * a given {@link Container}. The safe region is computed using the
 * {@link Container}'s bounds {@link Rectangle} and {@link Insets}.
 */
public class BoundsCalculator {
  private final Container mContainer;

  public BoundsCalculator( final Container container ) {
    assert container != null;

    mContainer = container;
  }

  /**
   * Returns the total width and height of an area that is safe for
   * writing on the {@link Container} parameter provided during construction.
   *
   * @return The width and height of the {@link Container}'s safe area, based
   * on the {@link Container}'s bounded dimensions and insets.
   */
  public Dimension computeSize() {
    final var container = getContainer();
    final var insets = getInsets();

    return new Dimension(
        container.getWidth() - (insets.left + insets.right),
        container.getHeight() - (insets.top + insets.bottom)
    );
  }

  public Insets getInsets() {
    return mContainer.getInsets();
  }

  public Point getLocation() {
    final var insets = getInsets();
    return new Point( insets.left, insets.top );
  }

  private Container getContainer() {
    return mContainer;
  }
}
