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
import com.whitemagicsoftware.kmcaster.ui.DimensionTuple;
import com.whitemagicsoftware.kmcaster.ui.ScalableDimension;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Map;

import static java.awt.RenderingHints.*;
import static java.awt.image.BufferedImage.TYPE_4BYTE_ABGR;

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
   * Loads the resource specified by the given path into an instance of
   * {@link SVGDiagram} that can be rasterized into a bitmap format. The
   * {@link SVGUniverse} class will
   *
   * @param path The full path (starting at the root), relative to the
   *             application or JAR file's resources directory.
   * @return An {@link SVGDiagram} that can be rasterized onto a
   * {@link BufferedImage}.
   */
  public SVGDiagram loadDiagram( final String path ) {
    final var url = getResourceUrl( path );
    final var uri = sRenderer.loadSVG( url );
    final var diagram = sRenderer.getDiagram( uri );
    return applySettings( diagram );
  }

  /**
   * A reusable method to help compute the scaling factor between the
   * given {@link SVGDiagram} image and the target {@link Dimension}s.
   *
   * @param diagram A 2-dimensional vector graphic having a width and height.
   * @param dstDim  The image's target dimensions.
   * @return A key-value pair of the source image dimensions (key) and the
   * scaled image dimensions (value).
   */
  public DimensionTuple calculateScale(
      final SVGDiagram diagram, final Dimension dstDim ) {
    final var srcDim = new ScalableDimension(
        (int) diagram.getWidth(), (int) diagram.getHeight()
    );
    final var scaled = srcDim.scale( dstDim );

    return new DimensionTuple( srcDim, scaled );
  }

  /**
   * Rasterizes a vector graphic to a given size using a {@link BufferedImage}.
   * The rendering hints are set to produce high quality output.
   *
   * @param diagram The diagram to rasterize.
   * @param tuple   The source and destination image dimensions.
   * @return The rasterized {@link Image}.
   * @throws SVGException Could not open, read, parse, or render SVG data.
   */
  public BufferedImage rasterize(
      final SVGDiagram diagram, final DimensionTuple tuple )
      throws SVGException {
    final var scaled = tuple.getValue();
    final var wScaled = (int) scaled.getWidth();
    final var hScaled = (int) scaled.getHeight();
    final var image = new BufferedImage( wScaled, hScaled, TYPE_4BYTE_ABGR );
    final var graphics = image.createGraphics();
    graphics.setRenderingHints( RENDERING_HINTS );

    final var transform = graphics.getTransform();
    transform.setToScale( tuple.getWidthRatio(), tuple.getHeightRatio() );

    graphics.setTransform( transform );
    diagram.render( graphics );
    graphics.dispose();

    return image;
  }

  /**
   * Rasterizes a vector graphic to a given size using a {@link BufferedImage}.
   * The rendering hints are set to produce high quality output.
   *
   * @param diagram The diagram to rasterize.
   * @param dstDim  The output image dimensions.
   * @return The rasterized {@link Image}.
   * @throws SVGException Could not open, read, parse, or render SVG data.
   */
  public Image rasterize(
      final SVGDiagram diagram, final Dimension dstDim ) throws SVGException {
    return rasterize( diagram, calculateScale( diagram, dstDim ) );
  }

  /**
   * Gets an instance of {@link URL} that references a file in the
   * application's resources.
   *
   * @param path The full path (starting at the root), relative to the
   *             application or JAR file's resources directory.
   * @return A {@link URL} to the file or {@code null} if the path does not
   * point to a resource.
   */
  private URL getResourceUrl( final String path ) {
    return SvgRasterizer.class.getResource( path );
  }

  /**
   * Instructs the SVG renderer to rasterize the image even if it would be
   * clipped.
   *
   * @param diagram The {@link SVGDiagram} to render.
   * @return The same instance with ignore clip heuristics set to {@code true}.
   */
  private SVGDiagram applySettings( final SVGDiagram diagram ) {
    diagram.setIgnoringClipHeuristic( true );
    return diagram;
  }
}
