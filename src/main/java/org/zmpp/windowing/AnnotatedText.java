/*
 * Created on 2008/04/26
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

import java.io.Serializable;

/**
 * An annotated text.
 * @author Wei-ju Wu
 * @version 1.5
 */
public class AnnotatedText implements Serializable {

  private static final long serialVersionUID = -1118683514461169397L;
  private TextAnnotation annotation;
  private String text;

  /**
   * Constructor.
   * @param annotation the annotation
   * @param text the text
   */
  public AnnotatedText(TextAnnotation annotation, String text) {
    this.annotation = annotation;
    this.text = text;
  }

  /**
   * Constructor.
   * @param text text
   */
  public AnnotatedText(String text) {
    this(new TextAnnotation(TextAnnotation.FONT_NORMAL,
                            TextAnnotation.TEXTSTYLE_ROMAN), text);
  }

  /**
   * Returns the annotation.
   * @return annotation
   */
  public TextAnnotation getAnnotation() { return annotation; }

  /**
   * Returns the text.
   * @return the text
   */
  public String getText() { return text; }
}
