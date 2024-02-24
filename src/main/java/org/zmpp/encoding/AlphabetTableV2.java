/*
 * Created on 2006/01/18
 * Copyright (c) 2005-2010, Wei-ju Wu.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of Wei-ju Wu nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.zmpp.encoding;

/**
 * An alphabet table in a V2 story file behaves "almost like" the default
 * alphabet table, in that they have the same characters in the alphabets.
 * There are however two differences: It only supports one abbreviation code
 * and it supports shift-lock.
 *
 * @author Wei-ju Wu
 * @version 1.5
 */
public class AlphabetTableV2 extends DefaultAlphabetTable {

  private static final long serialVersionUID = 1L;

  /** {@inheritDoc} */
  public boolean isAbbreviation(final char zchar) { return zchar == 1; }

  /** {@inheritDoc} */
  public boolean isShift1(final char zchar) {
    return zchar == SHIFT_2 || zchar == SHIFT_4;
  }

  /** {@inheritDoc} */
  public boolean isShift2(final char zchar) {
    return zchar == SHIFT_3 || zchar == SHIFT_5;
  }

  /** {@inheritDoc} */
  public boolean isShiftLock(final char zchar) {
    return zchar == SHIFT_4 || zchar == SHIFT_5;
  }
}
