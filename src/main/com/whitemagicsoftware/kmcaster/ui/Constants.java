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

import static java.awt.Font.BOLD;

/**
 * Responsible for containing shared constants required by the GUI.
 */
public final class Constants {
  /**
   * Use with {@code setOpaque( false )}.
   */
  public static final Color TRANSPARENT = new Color( 0, 0, 0, 0 );

  /**
   * Partially see-through.
   */
  public static final Color TRANSLUCENT = new Color( .2f, .2f, .2f, 0.5f );

  /**
   * Default insets, has no padding.
   */
  public final static Insets INSETS_EMPTY = new Insets( 0, 0, 0, 0 );

  /**
   * DejaVu Sans is the only free, open, sans serif font that supports all
   * the Unicode blocks used by the application. The font size will be scaled
   * dynamically to the window size, but should be sufficiently large for the
   * autofit functionality to scale down as required.
   */
  public static final Font LABEL_FONT = new Font( "DejaVu Sans", BOLD, 100 );

  /**
   * Matches the shift-key arrow font colour when pressed.
   */
  public static final Color COLOUR_KEY_DN = new Color( 0x21, 0x21, 0x21 );

  /**
   * Matches the shift-key arrow font colour when released.
   */
  public static final Color COLOUR_KEY_UP = new Color( 0xE5, 0xE5, 0xE5 );

  /**
   * Private, empty constructor.
   */
  private Constants() {
  }
}
