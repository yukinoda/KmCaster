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
package com.whitemagicsoftware.kmcaster.util;

/**
 * Responsible for formatting a string that represents whether consecutive
 * events have happened. When the event count exceeds a given limit, the
 * string representation shows that the number of tallied events exceeds the
 * limit. If fewer than two consecutive values are tallied, {@link #toString()}
 * returns the empty string.
 * <p>
 * If the new event value is not the same as the old value, the tally is reset.
 * </p>
 */
public class ConsecutiveEventCounter<Event> {
  /**
   * Previous event in the event chain, used to decide when the sequence breaks.
   */
  private Event mPrevious;

  /**
   * Increments when a repeated event is encountered, resets to 1 when the chain
   * of consecutive events is broken or {@link #reset()} is called.
   */
  private int mCount = 1;

  /**
   * The limit dictates how {@code mCount} is formatted.
   */
  private final int mLimit;

  /**
   * Creates a new counter that will change its format if the value to apply
   * causes the count to exceed the given limit.
   *
   * @param limit The maximum consecutive values before changing the format,
   *              must be greater than 1.
   */
  public ConsecutiveEventCounter( final int limit ) {
    assert limit > 1;

    mLimit = limit;
  }

  /**
   * Increments the internal counter if the given event matches the previous.
   * Answers whether the current count is part of a sequence.
   *
   * @param event The next event to tally.
   * @return {@code true} when the count is greater than 1.
   */
  public boolean apply( final Event event ) {
    assert event != null;

    mCount = event.equals( mPrevious ) ? mCount + 1 : 1;
    mPrevious = event;
    return mCount > 1;
  }

  /**
   * Resets the internal counter to 1 (i.e., indicates no consecutive events).
   */
  public void reset() {
    mCount = 1;
    mPrevious = null;
  }

  /**
   * Returns a formatted string to indicate the number of times that the
   * given event has been applied in succession. If the number of times is
   * fewer than two, this will return the empty string.
   *
   * @return A formatted string that includes the consecutive event tally.
   */
  @Override
  public String toString() {
    // Race-condition guards.
    final var count = mCount;
    final var limit = mLimit;
    final var s = Integer.toString( count );
    return count > 1 ? (count < limit ? "×" + s : limit + "+") : "";
  }
}
