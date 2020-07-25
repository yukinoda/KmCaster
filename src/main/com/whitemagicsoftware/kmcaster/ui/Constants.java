package com.whitemagicsoftware.kmcaster.ui;

import java.awt.*;

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
   * Private, empty constructor.
   */
  private Constants() {
  }
}
