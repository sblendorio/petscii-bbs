/*
 * Created on 2006/05/09
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
package org.zmpp.blorb;

import org.zmpp.media.Resolution;
import org.zmpp.media.ZmppImage;

/**
 * This class contains informations related to Blorb images and their
 * scaling. Scaling information is optional and probably only relevant
 * to V6 games. BlorbImage also calculates the correct image size,
 * according to the specification made in the Blorb standard specification.
 *
 * @author Wei-ju Wu
 * @version 1.5
 */
public class BlorbImage implements ZmppImage {

  /**
   * This class represents a rational number.
   */
  public static class Ratio {

    private int numerator;
    private int denominator;

    /**
     * Constructor.
     * @param numerator the numerator
     * @param denominator the denominator
     */
    public Ratio(int numerator, int denominator) {
      this.numerator = numerator;
      this.denominator = denominator;
    }
    /**
     * Returns the numerator.
     * @return numerator
     */
    public int getNumerator() { return numerator; }
    /**
     * Returns the denominator.
     * @return the denominator
     */
    public int getDenominator() { return denominator; }
    /**
     * Returns the calculated value as a float value.
     * @return calculated value
     */
    public float getValue() { return (float) numerator / denominator; }
    /**
     * Determines whether this value specifies valid value.
     * @return true if defined, false if undefined
     */
    public boolean isDefined() {
      return !(numerator == 0 && denominator == 0);
    }
    /** {@inheritDoc} */
    @Override
    public String toString() { return numerator + "/" + denominator; }
  }

  /**
   * This class represents resolution information.
   */
  protected static class ResolutionInfo {

    private Resolution standard;
    private Resolution minimum;
    private Resolution maximum;

    /**
     * Constructor.
     * @param std standard resolution
     * @param min minimum resolution
     * @param max maximum resolution
     */
    public ResolutionInfo(Resolution std, Resolution min, Resolution max) {
      standard = std;
      minimum = min;
      maximum = max;
    }
    /**
     * Returns the standard resolution.
     * @return standard resolution
     */
    public Resolution getStandard() { return standard; }
    /**
     * Returns the minimum resolution.
     * @return minimum resolution
     */
    public Resolution getMinimum() { return minimum; }
    /**
     * Returns the maximum resolution.
     * @return maximum resolution
     */
    public Resolution getMaximum() { return maximum; }
    /**
     * Calculates the ERF ("Elbow Room Factor").
     * @param screenwidth width of the display
     * @param screenheight height of the display
     * @return elbow room factor
     */
    public float computeERF(int screenwidth, int screenheight) {
      return Math.min(screenwidth / standard.getWidth(),
          screenheight / standard.getHeight());
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
      return "Std: " + standard.toString() + " Min: " + minimum.toString() +
        " Max: " + maximum.toString();
    }
  }

  /**
   * Representation of scaling information.
   */
  protected static class ScaleInfo {

    private ResolutionInfo resolutionInfo;
    private Ratio standard;
    private Ratio minimum;
    private Ratio maximum;

    /**
     * Constructor.
     * @param resinfo resolution information
     * @param std standard ratio
     * @param min minimum ratio
     * @param max maximum ratio
     */
    public ScaleInfo(ResolutionInfo resinfo, Ratio std, Ratio min, Ratio max) {
      this.resolutionInfo = resinfo;
      this.standard = std;
      this.minimum = min;
      this.maximum = max;
    }
    /**
     * Returns the resolution information.
     * @return resolution information
     */
    public ResolutionInfo getResolutionInfo() { return resolutionInfo; }
    /**
     * Returns the standard aspect ratio.
     * @return standard aspect ratio
     */
    public Ratio getStdRatio() { return standard; }
    /**
     * Returns the minimum aspect ratio.
     * @return minimum aspect ratio
     */
    public Ratio getMinRatio() { return minimum; }
    /**
     * Returns the maximum aspect ratio.
     * @return maximum aspect ratio
     */
    public Ratio getMaxRatio() { return maximum; }
    /**
     * Computes the scaling ratio depending on the specified screen dimensions.
     * @param screenwidth width
     * @param screenheight height
     * @return scaling ratio
     */
    public float computeScaleRatio(int screenwidth, int screenheight) {
      float value = resolutionInfo.computeERF(screenwidth, screenheight)
        * standard.getValue();

      if (minimum.isDefined() && value < minimum.getValue()) {
        value = minimum.getValue();
      }
      if (maximum.isDefined() && value > maximum.getValue()) {
        value = maximum.getValue();
      }
      return value;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
      return String.format("std: %s, min: %s, max: %s\n",
          standard.toString(), minimum.toString(), maximum.toString());
    }
  }

  private NativeImage image;
  private Resolution resolution;
  private ScaleInfo scaleinfo;

  /**
   * Constructor.
   * @param image NativeImage to wrap
   */
  public BlorbImage(NativeImage image) { this.image = image; }

  /**
   * Constructor.
   * @param width width
   * @param height height
   */
  public BlorbImage(int width, int height) {
    resolution = new Resolution(width, height);
  }

  /**
   * Returns the wrapped NativeImage.
   * @return NativeImage
   */
  public NativeImage getImage() { return image; }
  /**
   * Returns the scaling information.
   * @return scaling information
   */
  public ScaleInfo getScaleInfo() { return scaleinfo; }

  /**
   * Returns the size of the image, scaled to the specified screen
   * dimensions
   * @param screenwidth screen width
   * @param screenheight screen height
   * @return the scaled size
   */
  public Resolution getSize(int screenwidth, int screenheight) {
    if (scaleinfo != null) {
      float ratio = scaleinfo.computeScaleRatio(screenwidth, screenheight);
      if (image != null) {
        return new Resolution((int) (image.getWidth() * ratio),
          (int) (image.getHeight() * ratio));

      } else {
        return new Resolution((int) (resolution.getWidth() * ratio),
            (int) (resolution.getHeight() * ratio));
      }
    } else {
      if (image != null) {
        return new Resolution(image.getWidth(), image.getHeight());
      } else {
        return new Resolution(resolution.getWidth(), resolution.getHeight());
      }
    }
  }

  /**
   * Sets the ScaleInfo.
   * @param aScaleinfo ScaleInfo object
   */
  protected void setScaleInfo(ScaleInfo aScaleinfo) {
    this.scaleinfo = aScaleinfo;
  }
}
