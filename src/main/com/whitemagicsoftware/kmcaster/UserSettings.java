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

import picocli.CommandLine;

import java.awt.*;
import java.util.concurrent.Callable;

@CommandLine.Command(
    name = "KmCaster",
    mixinStandardHelpOptions = true,
    description = "Displays key presses and mouse clicks on the screen."
)
@SuppressWarnings("FieldMayBeFinal")
public final class UserSettings implements Callable<Integer> {
  /**
   * Minimum application height, in pixels.
   */
  private static final int MIN_HEIGHT_PX = 20;

  /**
   * Executable class.
   */
  private final KmCaster mKmCaster;

  /**
   * Application height in pixels. Images are scaled to this height, maintaining
   * aspect ratio. The height constrains the width, so as long as the width
   * is large enough, the application's window will adjust to fit.
   */
  @CommandLine.Option(
      names = {"-s", "--size"},
      description = "Application size (${DEFAULT-VALUE} pixels)",
      paramLabel = "height",
      defaultValue = "100"
  )
  private int mHeight = 100;

  /**
   * Milliseconds to wait before releasing (clearing) the regular key.
   */
  @CommandLine.Option(
      names = {"-a", "--delay-alphanum"},
      description = "Delay for releasing non-modifier keys (${DEFAULT-VALUE} " +
          "milliseconds)",
      paramLabel = "delay",
      defaultValue = "250"
  )
  private int mDelayKeyRegular = 250;

  /**
   * Milliseconds to wait before releasing (clearing) any modifier key.
   */
  @CommandLine.Option(
      names = {"-m", "--delay-modifier"},
      description = "Delay for releasing modifier keys (${DEFAULT-VALUE} " +
          "milliseconds)",
      paramLabel = "delay",
      defaultValue = "150"
  )
  private int mDelayKeyModifier = 150;

  /**
   * Milliseconds to wait before releasing (clearing) any mouse button.
   */
  @CommandLine.Option(
      names = {"-b", "--delay-button"},
      description = "Delay for releasing mouse buttons (${DEFAULT-VALUE} " +
          "milliseconds)",
      paramLabel = "delay",
      defaultValue = "200"
  )
  private int mDelayMouseButton = 200;

  public UserSettings( final KmCaster kmCaster ) {
    assert kmCaster != null;

    mKmCaster = kmCaster;
  }

  /**
   * Invoked after the command-line arguments are parsed to launch the
   * application.
   *
   * @return Exit level zero.
   */
  @Override
  public Integer call() {
    mKmCaster.init();
    return 0;
  }

  /**
   * This will return the user-specified height
   *
   * @return The application height, in pixels.
   */
  public int getHeight() {
    return mHeight < MIN_HEIGHT_PX ? MIN_HEIGHT_PX : mHeight;
  }

  public int getDelayKeyRegular() {
    return mDelayKeyRegular;
  }

  public int getDelayKeyModifier() {
    return mDelayKeyModifier;
  }

  public int getDelayMouseButton() {
    return mDelayMouseButton;
  }

  public Dimension createAppDimensions() {
    return new Dimension( 1024 + getHeight(), getHeight() );
  }
}
