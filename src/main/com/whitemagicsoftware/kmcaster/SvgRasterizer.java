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

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Map;

import static com.whitemagicsoftware.kmcaster.KmCaster.rethrow;
import static java.awt.RenderingHints.*;
import static java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

/**
 * Responsible for converting SVG images into rasterized PNG images.
 */
public class SvgRasterizer {
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

  private final static SVGUniverse sRenderer = new SVGUniverse();

  /**
   * Rasterizes a vector graphic to a given size using a {@link BufferedImage}.
   * The rendering hints are set to produce high quality output.
   *
   * @param path   Fully qualified path to the image resource to rasterize.
   * @param dstDim The output image dimensions.
   * @return The rasterized {@link Image}.
   */
  public Image rasterize( final String path, final Dimension dstDim ) {
    final var diagram = loadDiagram( path );
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

  private URL getResourceUrl( final String path ) {
    return HardwareSwitches.class.getResource( path );
  }

  private SVGDiagram loadDiagram( final String path ) {
    final var url = getResourceUrl( path );
    return applySettings( sRenderer.getDiagram( sRenderer.loadSVG( url ) ) );
  }

  private SVGDiagram applySettings( final SVGDiagram diagram ) {
    diagram.setIgnoringClipHeuristic( true );
    return diagram;
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
