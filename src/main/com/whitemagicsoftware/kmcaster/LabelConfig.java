package com.whitemagicsoftware.kmcaster;

import com.whitemagicsoftware.kmcaster.ui.AutofitLabel;

import javax.swing.*;
import java.util.Optional;

import static com.whitemagicsoftware.kmcaster.HardwareSwitch.*;
import static java.util.Optional.*;
import static javax.swing.SwingConstants.*;

/**
 * Used for initializing the {@link AutofitLabel} instances.
 */
public enum LabelConfig {
  LABEL_SHIFT( KEY_SHIFT, CENTER, CENTER ),
  LABEL_CTRL( KEY_CTRL, CENTER, CENTER ),
  LABEL_ALT( KEY_ALT, CENTER, CENTER ),
  LABEL_REGULAR( KEY_REGULAR, CENTER, CENTER ),
  LABEL_REGULAR_NUM_MAIN( CENTER, CENTER ),
  LABEL_REGULAR_NUM_SUPERSCRIPT( TOP, LEFT ),
  LABEL_REGULAR_COUNTER( TOP, RIGHT );

  /**
   * A value of {@code null} indicates multiple labels adorn the switch.
   */
  private final HardwareSwitch mHardwareSwitch;
  private final int mHorizontalAlign;
  private final int mVerticalAlign;

  /**
   * Creates a configuration for a multi-label hardware switch (such as
   * a regular key).
   *
   * @param vAlign Vertical alignment.
   * @param hAlign Horizontal alignment.
   */
  LabelConfig( final int vAlign, final int hAlign ) {
    this( null, vAlign, hAlign );
  }

  /**
   * Creates a configuration for a hardware switch that supports a single
   * label (such as a modifier key).
   *
   * @param hwSwitch The switch to associate with the single label.
   * @param vAlign   Vertical alignment.
   * @param hAlign   Horizontal alignment.
   */
  LabelConfig(
      final HardwareSwitch hwSwitch, final int vAlign, final int hAlign ) {
    mHardwareSwitch = hwSwitch;
    mVerticalAlign = vAlign;
    mHorizontalAlign = hAlign;
  }

  /**
   * If present, a return value indicates the switch has a single label and
   * is associated with it.
   *
   * @return A switch associated with a label, or empty if multi-labelled.
   */
  Optional<HardwareSwitch> getHardwareSwitch() {
    return ofNullable( mHardwareSwitch );
  }

  /**
   * Returns an alignment value from {@link SwingConstants}.
   *
   * @return An indicator of left, right, or centered alignment.
   */
  int getHorizontalAlign() {
    return mHorizontalAlign;
  }

  /**
   * Returns an alignment value from {@link SwingConstants}.
   *
   * @return An indicator of top, bottom, or centered alignment.
   */
  int getVerticalAlign() {
    return mVerticalAlign;
  }

  /**
   * Returns a blank space when no {@link HardwareSwitch} is assigned.
   *
   * @return The title case version of the hardware switch, or a space if
   * there is no direct correlation.
   */
  String toTitleCase() {
    return mHardwareSwitch == null ? " " : mHardwareSwitch.toTitleCase();
  }

  /**
   * Returns the number of values in the enumeration.
   *
   * @return {@link #values()}.length.
   */
  static int size() {
    return values().length;
  }
}
