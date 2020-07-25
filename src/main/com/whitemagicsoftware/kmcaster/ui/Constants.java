package com.whitemagicsoftware.kmcaster.ui;

import java.awt.*;

import static java.awt.Font.BOLD;

/**
 * Responsible for containing shared constants required by the GUI.
 */
public class Constants {
  /**
   * Use with {@code setOpaque( false )}.
   */
  public static final Color TRANSPARENT = new Color( 0, 0, 0, 0 );

  /**
   * Partially see-through.
   */
  public static final Color TRANSLUCENT = new Color( .2f, .2f, .2f, 0.5f );

  /**
   * Application dimensions in pixels. Images are scaled to these dimensions,
   * maintaining aspect ratio. The height constrains the width, so as long as
   * the width is large enough, the application's window will adjust to fit.
   */
  public static final Dimension APP_DIMENSIONS = new Dimension( 1024, 100 );

  /**
   * Default insets, has no padding.
   */
  public final static Insets INSETS_EMPTY = new Insets( 0, 0, 0, 0 );

  /**
   * Milliseconds to wait before releasing (clearing) the regular key.
   */
  public final static int DELAY_KEY_REGULAR = 250;

  /**
   * Milliseconds to wait before releasing (clearing) any modifier key.
   */
  public final static int DELAY_KEY_MODIFIER = 150;

  /**
   * DejaVu Sans is the only free, open, sans serif font that supports
   * all the Unicode blocks used by the application. The font size is
   * arbitrary, the font will be scaled dynamically to the window size.
   */
  public static final Font LABEL_FONT = new Font( "DejaVu Sans", BOLD, 32 );

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
