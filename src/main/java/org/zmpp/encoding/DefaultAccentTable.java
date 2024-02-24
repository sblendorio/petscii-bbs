/*
 * Created on 2005/01/15
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
 * Default implementation of AccentTable.
 * @author Wei-ju Wu
 * @version 1.5
 */
public class DefaultAccentTable implements AccentTable {

  private static final char[] STANDARD_TRANSLATION_TABLE = {
    '\u00e4', '\u00f6', '\u00fc', '\u00c4', '\u00d6', '\u00dc', '\u00df',
    '\u00bb', '\u00ab',
    '\u00eb', '\u00ef', '\u00ff', '\u00cb', '\u00cf',
    '\u00e1', '\u00e9', '\u00ed', '\u00f3', '\u00fa', '\u00fd',
    '\u00c1', '\u00c9', '\u00cd', '\u00d3', '\u00da', '\u00dd',
    '\u00e0', '\u00e8', '\u00ec', '\u00f2', '\u00f9',
    '\u00c0', '\u00c8', '\u00cc', '\u00d2', '\u00d9',
    '\u00e2', '\u00ea', '\u00ee', '\u00f4', '\u00fb',
    '\u00c2', '\u00ca', '\u00ce', '\u00d4', '\u00db',
    '\u00e5', '\u00c5', '\u00f8', '\u00d8',
    '\u00e3', '\u00f1', '\u00f5', '\u00c3', '\u00d1', '\u00d5',
    '\u00e6', '\u00c6', '\u00e7', '\u00c7',
    '\u00fe', '\u00fd', '\u00f0', '\u00d0',
    '\u00a3', '\u0153', '\u0152', '\u00a1', '\u00bf'
  };

  /** {@inheritDoc} */
  public int getLength() {
    return STANDARD_TRANSLATION_TABLE.length;
  }

  /** {@inheritDoc} */
  public char getAccent(final int index) {
    return STANDARD_TRANSLATION_TABLE[index];
  }

  /** {@inheritDoc} */
  public int getIndexOfLowerCase(final int index) {
    final char c = (char) getAccent(index);
    final char lower = Character.toLowerCase(c);
    final int length = getLength();
    for (int i = 0; i < length; i++) {
      if (getAccent(i) == lower) return i;
    }
    return index;
  }
}
