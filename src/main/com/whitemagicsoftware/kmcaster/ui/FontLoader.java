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
package com.whitemagicsoftware.kmcaster.ui;

import com.whitemagicsoftware.kmcaster.KmCaster;

import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import static com.whitemagicsoftware.kmcaster.exceptions.Rethrowable.rethrow;

/**
 * Responsible for loading application-specific fonts into the local
 * {@link GraphicsEnvironment}.
 */
public final class FontLoader {
  private static final String FONT_DIRECTORY = "/fonts";

  /**
   * Recursively search all directories under {@link #FONT_DIRECTORY} for
   * TrueType Files (ttf).
   */
  private static final String FONT_GLOB = "glob:**.ttf";

  /**
   * Reads all fonts packaged with the application.
   */
  public static void initFonts() {
    final var ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    final var rw = new ResourceWalker( FONT_GLOB );

    try {
      rw.walk(
          FONT_DIRECTORY, path -> {
            final var uri = path.toUri();
            final var filename = path.toString();

            try( final var is = openFont( uri, filename ) ) {
              final var font = Font.createFont( Font.TRUETYPE_FONT, is );

              ge.registerFont( font.deriveFont( font.getAttributes() ) );
            } catch( final Exception e ) {
              rethrow( e );
            }
          }
      );
    } catch( final Exception e ) {
      rethrow( e );
    }
  }

  private static InputStream openFont( final URI uri, final String filename )
      throws IOException {
    return uri.getScheme().equals( "jar" )
        ? KmCaster.class.getResourceAsStream( filename )
        : new FileInputStream( filename );
  }
}
