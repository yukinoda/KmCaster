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

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static com.whitemagicsoftware.kmcaster.HardwareState.ANY_KEY;
import static com.whitemagicsoftware.kmcaster.HardwareSwitch.*;
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

  private final static SvgRasterizer sRasterizer = new SvgRasterizer();

  private final Map<HardwareState, Image> mImages = new HashMap<>();
  private final Dimension mDimension;

  /**
   * Constructs an enumerated type that represents the different types of
   * images shown when keyboard and mouse events are triggered.
   *
   * @param dimension The image will be scaled to the given dimensions, aspect
   *                  ratio is maintained.
   */
  public HardwareImages( final Dimension dimension ) {
    assert dimension != null;

    mDimension = dimension;

    final var mouseReleased = mouseImage( "0" );

    for( int i = 1; i <= 3; i++ ) {
      final var s = Integer.toString( i );
      final var switchName = HardwareSwitch.valueFrom( "button " + s );
      mImages.put( state( switchName, true ), mouseImage( s ) );
      mImages.put( state( switchName, false ), mouseReleased );
    }

    mImages.put( state( MOUSE_LR, true ), mouseImage( "1-3" ) );
    mImages.put( state( MOUSE_LR, false ), mouseReleased );

    mImages.put( state( KEY_ALT, true ), keyDnImage( "medium" ) );
    mImages.put( state( KEY_ALT, false ), keyUpImage( "medium" ) );
    mImages.put( state( KEY_CTRL, true ), keyDnImage( "medium" ) );
    mImages.put( state( KEY_CTRL, false ), keyUpImage( "medium" ) );
    mImages.put( state( KEY_SHIFT, true ), keyDnImage( "long" ) );
    mImages.put( state( KEY_SHIFT, false ), keyUpImage( "long" ) );
    mImages.put( state( KEY_REGULAR, ANY_KEY ), keyDnImage( "short" ) );
    mImages.put( state( KEY_REGULAR, false ), keyUpImage( "short" ) );
  }

  public Image get( final HardwareState state ) {
    return mImages.get( state );
  }

  public static HardwareState state(
      final HardwareSwitch name, final boolean state ) {
    return state( name, Boolean.toString( state ) );
  }

  public static HardwareState state(
      final HardwareSwitch name, final String state ) {
    return new HardwareState( name, state );
  }

  private Image mouseImage( final String prefix ) {
    return createImage( format( "%s/%s", DIR_IMAGES_MOUSE, prefix ) );
  }

  private Image keyImage(
      final String state, final String prefix ) {
    return createImage(
        format( "%s/%s/%s", DIR_IMAGES_KEYBOARD, state, prefix )
    );
  }

  private Image keyUpImage( final String prefix ) {
    return keyImage( "up", prefix );
  }

  private Image keyDnImage( final String prefix ) {
    return keyImage( "dn", prefix );
  }

  private Image createImage( final String path ) {
    assert mDimension != null;

    final var resource = format( "%s.svg", path );

    try {
      return sRasterizer.rasterize( resource, mDimension );
    } catch( final Exception ex ) {
      rethrow( ex );
    }

    final var msg = format( "Missing resource %s", resource );
    throw new RuntimeException( msg );
  }

  /**
   * Cast a checked exception as a {@link RuntimeException}.
   *
   * @param <T> What type of {@link Throwable} to throw.
   * @param t   The problem to cast.
   * @throws T The throwable is casted to this type.
   */
  @SuppressWarnings("unchecked")
  private static <T extends Throwable> void rethrow( final Throwable t )
      throws T {
    throw (T) t;
  }
}
