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

import com.whitemagicsoftware.kmcaster.ui.DimensionTuple;
import com.whitemagicsoftware.kmcaster.util.Pair;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static com.whitemagicsoftware.kmcaster.HardwareState.ANY_KEY;
import static com.whitemagicsoftware.kmcaster.HardwareSwitch.*;
import static com.whitemagicsoftware.kmcaster.exceptions.Rethrowable.rethrow;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.String.format;

/**
 * Responsible for loading vector graphics representations of application
 * images. The images provide an on-screen interface that indicate to the user
 * what key or mouse events have been triggered.
 */
public class HardwareImages {
  private final static String DIR_IMAGES = "/images";
  private final static String DIR_IMAGES_KEYBOARD = DIR_IMAGES + "/key";
  private final static String DIR_IMAGES_MOUSE = DIR_IMAGES + "/mouse";

  private final static Map<HardwareSwitch, String> FILE_NAME_PREFIXES = Map.of(
      KEY_ALT, "medium",
      KEY_CTRL, "medium",
      KEY_SHIFT, "long",
      KEY_REGULAR, "short"
  );

  private final static SvgRasterizer sRasterizer = new SvgRasterizer();

  private final Map<HardwareSwitch, HardwareComponent<HardwareState, Image>>
      mSwitches = new HashMap<>();

  /**
   * Images are scaled to these dimensions, maintaining aspect ratio. The
   * height constrains the width, so as long as the width is sufficiently
   * large, the application's window will adjust to fit.
   */
  private final Dimension mDimension = new Dimension( 1024, 120 );

  public HardwareImages() {
    final var mouseStates = createHardwareComponent();

    for( int i = 1; i <= 3; i++ ) {
      final var s = Integer.toString( i );
      mouseStates.put( state( MOUSE, s ), mouseImage( s ) );
    }

    mouseStates.put( state( MOUSE, "1-3" ), mouseImage( "1-3" ) );
    mouseStates.put( state( MOUSE, FALSE.toString() ), mouseImage( "0" ) );
    mSwitches.put( MOUSE, mouseStates );

    for( final var key : HardwareSwitch.keyboardKeys() ) {
      final var stateNameOn = key == KEY_REGULAR ? ANY_KEY : TRUE.toString();

      final var stateOn = state( key, stateNameOn );
      final var stateOff = state( key, FALSE.toString() );
      final var imageDn = keyDnImage( FILE_NAME_PREFIXES.get( key ) );
      final var imageUp = keyUpImage( FILE_NAME_PREFIXES.get( key ) );
      final var scale = imageDn.getValue();

      final var insets = KeyCapInsets.scale( scale );
      final var hardwareComponent = createHardwareComponent( insets );

      hardwareComponent.put( stateOn, imageDn.getKey() );
      hardwareComponent.put( stateOff, imageUp.getKey() );

      mSwitches.put( key, hardwareComponent );
    }
  }

  private HardwareComponent<HardwareState, Image> createHardwareComponent() {
    return new HardwareComponent<>();
  }

  private HardwareComponent<HardwareState, Image> createHardwareComponent(
      final Insets insets ) {
    return new HardwareComponent<>( insets );
  }

  public HardwareComponent<HardwareState, Image> get(
      final HardwareSwitch hwSwitch ) {
    return mSwitches.get( hwSwitch );
  }

  private HardwareState state(
      final HardwareSwitch name, final String state ) {
    return new HardwareState( name, state );
  }

  private Image mouseImage( final String prefix ) {
    final var imagePair =
        createImage( format( "%s/%s", DIR_IMAGES_MOUSE, prefix ) );

    return imagePair.getKey();
  }

  private Pair<Image, DimensionTuple> keyImage(
      final String state, final String prefix ) {
    return createImage(
        format( "%s/%s/%s", DIR_IMAGES_KEYBOARD, state, prefix )
    );
  }

  private Pair<Image, DimensionTuple> keyUpImage( final String prefix ) {
    return keyImage( "up", prefix );
  }

  private Pair<Image, DimensionTuple> keyDnImage( final String prefix ) {
    return keyImage( "dn", prefix );
  }

  private Pair<Image, DimensionTuple> createImage( final String path ) {
    final var resource = format( "%s.svg", path );

    try {
      final var diagram = sRasterizer.loadDiagram( resource );
      final var scale = sRasterizer.calculateScale( diagram, mDimension );
      final var image = sRasterizer.rasterize( diagram, mDimension );

      return new Pair<>( image, scale );
    } catch( final Exception ex ) {
      rethrow( ex );
    }

    final var msg = format( "Missing resource %s", resource );
    throw new RuntimeException( msg );
  }
}
