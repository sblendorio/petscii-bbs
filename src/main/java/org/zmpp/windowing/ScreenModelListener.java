/*
 * Created on 2008/07/28
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
package org.zmpp.windowing;

/**
 * The ScreenModelListener interface.
 * @author Wei-ju Wu
 * @version 1.5
 */
public interface ScreenModelListener {
  /**
   * Notifies the listener that the screen model was updated.
   * @param screenModel the updated screen model
   */
  void screenModelUpdated(ScreenModel screenModel);

  /**
   * Called when the top window was changed.
   * @param cursorx cursor x-position
   * @param cursory cursor y-position
   * @param c character
   */
  void topWindowUpdated(int cursorx, int cursory, AnnotatedCharacter c);

  /**
   * Called when the screen split value changed.
   * @param linesUpperWindow lines in upper window
   */
  void screenSplit(int linesUpperWindow);

  /**
   * Called when a window is erased.
   * @param window the erased window
   */
  void windowErased(int window);
  /**
   * Called before the cursor positions is updated.
   * @param line the target line
   * @param column the target column
   */
  void topWindowCursorMoving(int line, int column);
}

