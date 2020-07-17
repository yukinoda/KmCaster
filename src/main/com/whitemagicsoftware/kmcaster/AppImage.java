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

import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGException;
import com.kitfox.svg.SVGUniverse;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Map;

import static com.whitemagicsoftware.kmcaster.KmCaster.rethrow;
import static java.awt.RenderingHints.*;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static java.lang.String.format;

/**
 * Responsible for loading vector graphics representations of application
 * images. The images provide an on-screen interface that indicate to the user
 * what key or mouse events are being triggered.
 */
public class AppImage {
  public final static Map<Object, Object> RENDERING_HINTS = Map.of(
      KEY_ANTIALIASING,
      VALUE_ANTIALIAS_ON,
      KEY_ALPHA_INTERPOLATION,
      VALUE_ALPHA_INTERPOLATION_QUALITY,
      KEY_COLOR_RENDERING,
      VALUE_COLOR_RENDER_QUALITY,
      KEY_DITHERING,
      VALUE_DITHER_DISABLE,
      KEY_FRACTIONALMETRICS,
      VALUE_FRACTIONALMETRICS_ON,
      KEY_INTERPOLATION,
      VALUE_INTERPOLATION_BICUBIC,
      KEY_RENDERING,
      VALUE_RENDER_QUALITY,
      KEY_STROKE_CONTROL,
      VALUE_STROKE_PURE,
      KEY_TEXT_ANTIALIASING,
      VALUE_TEXT_ANTIALIAS_ON
  );

  private final static String IMAGES = "/images";
  private final static String IMAGES_KEY = IMAGES + "/key";
  private final static String IMAGES_MOUSE = IMAGES + "/mouse";

  public static final AppImage MOUSE_REST = mouseImage( "0" );
  public static final AppImage MOUSE_LEFT = mouseImage( "1" );
  public static final AppImage MOUSE_CHORD = mouseImage( "2" );
  public static final AppImage MOUSE_RIGHT = mouseImage( "3" );
  public static final AppImage MOUSE_LR = mouseImage( "1-3" );
  public static final AppImage KEY_UP_SHIFT = keyUpImage( "long" );
  public static final AppImage KEY_UP_ALT = keyUpImage( "medium" );
  public static final AppImage KEY_UP_CTRL = keyUpImage( "medium" );
  public static final AppImage KEY_UP_REGULAR = keyUpImage( "short" );
  public static final AppImage KEY_DN_SHIFT = keyDnImage( "long" );
  public static final AppImage KEY_DN_ALT = keyDnImage( "medium" );
  public static final AppImage KEY_DN_CTRL = keyDnImage( "medium" );
  public static final AppImage KEY_DN_REGULAR = keyDnImage( "short" );

  private static AppImage mouseImage( final String prefix ) {
    return createImage( format( "%s/%s", IMAGES_MOUSE, prefix ) );
  }

  private static AppImage keyImage( final String state, final String prefix ) {
    return createImage( format( "%s/%s/%s", IMAGES_KEY, state, prefix ) );
  }

  private static AppImage keyUpImage( final String prefix ) {
    return keyImage( "up", prefix );
  }

  private static AppImage keyDnImage( final String prefix ) {
    return keyImage( "dn", prefix );
  }

  private static AppImage createImage( final String path ) {
    return new AppImage( format( "%s.svg", path ) );
  }

  private final SVGUniverse mRenderer = new SVGUniverse();
  private final String mPath;

  /**
   * Constructs an enumerated type that represents the different types of
   * images shown when keyboard and mouse events are triggered.
   *
   * @param path File name, including directory, to load.
   */
  private AppImage( final String path ) {
    mPath = path;
  }

  public JComponent toComponent( final Dimension dimension ) {
    final var image = toImage( dimension );

    return new JComponent() {
      @Override
      public Dimension getPreferredSize() {
        return new Dimension(
            image.getWidth( null ), image.getHeight( null )
        );
      }

      @Override
      protected void paintComponent( final Graphics graphics ) {
        super.paintComponent( graphics );

        final var g = (Graphics2D) graphics.create();
        g.drawImage( image, 0, 0, this );
      }
    };
  }

  public Image toImage( final Dimension dstDim ) {
    final var diagram = loadDiagram();
    final var diaWidth = diagram.getWidth();
    final var diaHeight = diagram.getHeight();
    final var srcDim = new Dimension( (int) diaWidth, (int) diaHeight );

    final var scaledDim = scale( srcDim, dstDim );
    final var w = (int) scaledDim.getWidth();
    final var h = (int) scaledDim.getHeight();

    final var image = new BufferedImage( w, h, TYPE_INT_ARGB );

    try {
      final Graphics2D g = image.createGraphics();
      g.setRenderingHints( RENDERING_HINTS );

      final AffineTransform transform = g.getTransform();
      transform.setToScale( w / diaWidth, h / diaHeight );

      g.setTransform( transform );
      diagram.render( g );
      g.dispose();
    } catch( final SVGException e ) {
      rethrow( e );
    }

    return image;
  }

  private SVGDiagram loadDiagram() {
    final var url = getResourceUrl();
    return applySettings( mRenderer.getDiagram( mRenderer.loadSVG( url ) ) );
  }

  private SVGDiagram applySettings( final SVGDiagram diagram ) {
    diagram.setIgnoringClipHeuristic( true );
    return diagram;
  }

  private URL getResourceUrl() {
    return AppImage.class.getResource( getPath() );
  }

  private String getPath() {
    return mPath;
  }

  private Dimension scale( final Dimension src, final Dimension dst ) {
    final var srcWidth = src.getWidth();
    final var srcHeight = src.getHeight();
    final var dstHeight = dst.getHeight();

    var newWidth = srcWidth;
    var newHeight = srcHeight;

    if( newHeight < dstHeight ) {
      newHeight = dstHeight;
      newWidth = (newHeight * srcWidth) / srcHeight;
    }

    return new Dimension( (int) newWidth, (int) newHeight );
  }
}
