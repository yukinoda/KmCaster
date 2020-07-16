package com.whitemagicsoftware.kmcaster.listeners;

import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Map;
import java.util.Set;

import static java.util.Map.entry;
import static org.jnativehook.NativeInputEvent.*;
import static org.jnativehook.keyboard.NativeKeyEvent.getKeyText;

public class KeyboardListener implements NativeKeyListener {
  public final static String KEY_NAME_ALT = "ALT";
  public final static String KEY_NAME_CTRL = "CTRL";
  public final static String KEY_NAME_SHIFT = "SHIFT";
  public final static String KEY_NAME_REGULAR = "REGULAR";

  /**
   * The library has default names for these modifiers, which will be
   * converted to lower case for comparison with this set.
   */
  private final static Set<String> modifiers = Set.of(
      "alt", "ctrl", "shift"
  );

  @SuppressWarnings("RedundantTypeArguments")
  private final static Map<Integer, String> KEY_CODES =
      Map.<Integer, String>ofEntries(
          entry( 32, "␣" ),
          entry( 33, "!" ),
          entry( 34, "'" ),
          entry( 35, "#" ),
          entry( 36, "$" ),
          entry( 37, "%" ),
          entry( 38, "&" ),
          entry( 39, "\"" ),
          entry( 40, "(" ),
          entry( 41, ")" ),
          entry( 42, "*" ),
          entry( 43, "+" ),
          entry( 44, "," ),
          entry( 45, "-" ),
          entry( 46, "." ),
          entry( 47, "/" ),
          entry( 58, ":" ),
          entry( 59, ";" ),
          entry( 60, "<" ),
          entry( 61, "=" ),
          entry( 62, ">" ),
          entry( 63, "?" ),
          entry( 64, "@" ),
          entry( 91, "[" ),
          entry( 92, "\\" ),
          entry( 93, "]" ),
          entry( 94, "^" ),
          entry( 95, "_" ),
          entry( 96, "`" ),
          entry( 97, "a" ),
          entry( 98, "b" ),
          entry( 99, "c" ),
          entry( 100, "d" ),
          entry( 101, "e" ),
          entry( 102, "f" ),
          entry( 103, "g" ),
          entry( 104, "h" ),
          entry( 105, "i" ),
          entry( 106, "j" ),
          entry( 107, "k" ),
          entry( 108, "l" ),
          entry( 109, "m" ),
          entry( 110, "n" ),
          entry( 111, "o" ),
          entry( 112, "p" ),
          entry( 113, "q" ),
          entry( 114, "r" ),
          entry( 115, "s" ),
          entry( 116, "t" ),
          entry( 117, "u" ),
          entry( 118, "v" ),
          entry( 119, "w" ),
          entry( 120, "x" ),
          entry( 121, "y" ),
          entry( 122, "z" ),
          entry( 123, "{" ),
          entry( 124, "|" ),
          entry( 125, "}" ),
          entry( 126, "~" ),
          entry( 65288, "Back ⌫" ),
          entry( 65056, "Tab ↹" ),
          entry( 65289, "Tab ↹" ),
          entry( 65293, "Enter ⏎" ),
          entry( 65361, "←" ),
          entry( 65362, "↑" ),
          entry( 65363, "→" ),
          entry( 65364, "↓" ),
          entry( 65307, "Esc" ),
          entry( 65365, "PgUp" ),
          entry( 65366, "PgDn" ),
          entry( 65379, "Ins" ),
          entry( 65535, "Del" ),
          entry( 65506, "Shift" ),
          entry( 65407, "Num" ),
          entry( 65456, "Num 0" ),
          entry( 65436, "Num 1" ),
          entry( 65433, "Num 2" ),
          entry( 65435, "Num 3" ),
          entry( 65430, "Num 4" ),
          entry( 65437, "Num 5" ),
          entry( 65432, "Num 6" ),
          entry( 65429, "Num 7" ),
          entry( 65431, "Num 8" ),
          entry( 65434, "Num 9" ),
          entry( 65421, "Num ⏎" ),
          entry( 65438, "Num Ins" ),
          entry( 65439, "Num Del" ),
          entry( 65451, "Num +" ),
          entry( 65453, "Num -" ),
          entry( 65454, "Num ." ),
          entry( 65455, "Num /" ),
          entry( 65450, "Num *" ),
          entry( 65300, "Scrl" ),
          entry( 65509, "Caps" ),
          entry( 65377, "Print" ),
          entry( 65301, "SysRq" )
      );

  private boolean mAltHeld;
  private boolean mCtrlHeld;
  private boolean mShiftHeld;
  private String mRegularHeld = "";

  private final PropertyChangeSupport mDispatcher =
      new PropertyChangeSupport( this );

  public KeyboardListener() {
  }

  public void addPropertyChangeListener(
      final PropertyChangeListener listener ) {
    mDispatcher.addPropertyChangeListener( listener );
  }

  public void removePropertyChangeListener(
      final PropertyChangeListener listener ) {
    mDispatcher.removePropertyChangeListener( listener );
  }

  @Override
  public void nativeKeyPressed( final NativeKeyEvent e ) {
    final String regularHeld = getDisplayText( e );

    // If it's not a modifier key, broadcast the regular value.
    if( !modifiers.contains( regularHeld.toLowerCase() ) ) {
      tryFire( KEY_NAME_REGULAR, mRegularHeld, regularHeld );
      mRegularHeld = regularHeld;
    }

    // If the modifier has changed, it'll be caught here.
    updateModifiers( e );
  }

  @Override
  public void nativeKeyReleased( final NativeKeyEvent e ) {
    mRegularHeld = "";
    updateModifiers( e );
  }

  @Override
  public void nativeKeyTyped( final NativeKeyEvent e ) {
  }

  private void updateModifiers( final NativeKeyEvent e ) {
    final boolean alt = isAltDown( e );
    final boolean ctrl = isControlDown( e );
    final boolean shift = isShiftDown( e );

    tryFire( KEY_NAME_ALT, mAltHeld, alt );
    tryFire( KEY_NAME_CTRL, mCtrlHeld, ctrl );
    tryFire( KEY_NAME_SHIFT, mShiftHeld, shift );

    mAltHeld = alt;
    mCtrlHeld = ctrl;
    mShiftHeld = shift;
  }

  /**
   * Called to fire the property change with the two given values differ.
   *
   * @param name The name of the property that has changed.
   * @param o    Old property value.
   * @param n    New property value.
   */
  @SuppressWarnings("SameParameterValue")
  private void tryFire( final String name, final String o, final String n ) {
    if( !o.equals( n ) ) {
      mDispatcher.firePropertyChange( name, o, n );
    }
  }

  /**
   * Called to fire the property change with the two given values differ.
   *
   * @param name The name of the property that has changed.
   * @param o    Old property value.
   * @param n    New property value.
   */
  private void tryFire( final String name, final boolean o, final boolean n ) {
    if( o != n ) {
      mDispatcher.firePropertyChange( name, o, n );
    }
  }

  /**
   * Looks up the key code for the given event. If the key code is not mapped,
   * this will return the default value from the native implementation.
   *
   * @param e The keyboard event that was triggered.
   * @return The human-readable name for the key relating to the event.
   */
  private String getDisplayText( final NativeKeyEvent e ) {
    return KEY_CODES.getOrDefault(
        e.getRawCode(), getKeyText( e.getKeyCode() )
    );
  }

  private boolean isModifierDown( final NativeKeyEvent e, final int mask ) {
    return (e.getModifiers() & mask) != 0;
  }

  private boolean isAltDown( final NativeKeyEvent e ) {
    return isModifierDown( e, ALT_MASK );
  }

  private boolean isControlDown( final NativeKeyEvent e ) {
    return isModifierDown( e, CTRL_MASK );
  }

  private boolean isShiftDown( final NativeKeyEvent e ) {
    return isModifierDown( e, SHIFT_MASK );
  }
}
