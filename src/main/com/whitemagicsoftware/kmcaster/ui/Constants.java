package com.whitemagicsoftware.kmcaster.ui;

import java.awt.*;

/**
 * Responsible for containing shared constants required by the GUI.
 */
public class Constants {
  public static final Color TRANSPARENT = new Color( 0, 0, 0, 0 );
  public static final Color TRANSLUCENT = new Color( .2f, .2f, .2f, 0.5f );

  /**
   * Default insets, has no padding.
   */
  public final static Insets INSETS_EMPTY =
      new Insets( 0, 0, 0, 0 );

  /**
   * Private, empty constructor.
   */
  private Constants() {
  }
}
