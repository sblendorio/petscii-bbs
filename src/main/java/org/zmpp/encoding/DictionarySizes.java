/*
 * Created on 2006/01/19
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
 * Z-code compilers seem to truncate dictionary string pretty
 * sloppy (i.e. multibyte sequences such as A2 escape) so that in
 * dictionary entries, the end bit does not always exist. Unfortunately,
 * the entry size given in the dictionary header is not reliable either.
 * Therefore we need to provide a size to the dictionary that is taken
 * from the Standard Specification Document. The specification specifies
 * both the number of bytes and the number of maximum characters
 * per entry which we access here. By defining a dictionary
 * size object, we avoid keep dictionary classes clean of version
 * dependency.
 *
 * @author Wei-ju Wu
 * @version 1.5
 */
public interface DictionarySizes {

  /**
   * The number of bytes for an entry.
   * @return the number of bytes for an entry
   */
  int getNumEntryBytes();

  /**
   * The maximum number of characters for an entry.
   * @return the maximum number of characters
   */
  int getMaxEntryChars();
}
