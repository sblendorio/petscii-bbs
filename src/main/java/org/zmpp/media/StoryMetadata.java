/*
 * Created on 2006/03/10
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
package org.zmpp.media;


/**
 * This class holds information about a story.
 *
 * @author Wei-ju Wu
 * @version 1.5
 */
public class StoryMetadata {

  private static final char NEWLINE = '\n';

  private String title;
  private String headline;
  private String author;
  private String genre;
  private String description;
  private String year;
  private int coverpicture;
  private String group;

  /**
   * Returns the story title.
   * @return story title
   */
  public String getTitle() { return title; }
  /**
   * Sets the story title.
   * @param title story title
   */
  public void setTitle(final String title) { this.title = title; }
  /**
   * Returns the headline.
   * @return headline
   */
  public String getHeadline() { return headline; }
  /**
   * Sets the headline.
   * @param headline the headline
   */
  public void setHeadline(final String headline) { this.headline = headline; }
  /**
   * Returns the author.
   * @return author
   */
  public String getAuthor() { return author; }
  /**
   * Sets the author.
   * @param author author
   */
  public void setAuthor(final String author) { this.author = author; }
  /**
   * Returns the genre.
   * @return genre
   */
  public String getGenre() { return genre; }
  /**
   * Sets the genre.
   * @param genre genre
   */
  public void setGenre(final String genre) { this.genre = genre; }
  /**
   * Returns the description.
   * @return description
   */
  public String getDescription() { return description; }
  /**
   * Sets the description.
   * @param description description
   */
  public void setDescription(final String description) {
    this.description = description;
  }
  /**
   * Returns the year.
   * @return year
   */
  public String getYear() { return year; }
  /**
   * Sets the year.
   * @param year year
   */
  public void setYear(final String year) { this.year = year; }
  /**
   * Returns the cover picture number.
   * @return cover picture number
   */
  public int getCoverPicture() { return coverpicture; }
  /**
   * Sets the cover picture number.
   * @param picnum cover picture number
   */
  public void setCoverPicture(final int picnum) { this.coverpicture = picnum; }
  /**
   * Returns the group.
   * @return group
   */
  public String getGroup() { return group; }
  /**
   * Sets the group.
   * @param group group
   */
  public void setGroup(final String group) { this.group = group; }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("Title: '" + title + NEWLINE);
    builder.append("Headline: '" + headline + NEWLINE);
    builder.append("Author: '" + author + NEWLINE);
    builder.append("Genre: '" + genre + NEWLINE);
    builder.append("Description: '" + description + NEWLINE);
    builder.append("Year: '" + year + NEWLINE);
    builder.append("Cover picture: " + coverpicture + NEWLINE);
    builder.append("Group: '" + group + NEWLINE);
    return builder.toString();
  }
}
