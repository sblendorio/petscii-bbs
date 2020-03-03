/*
 * $Id: StoryMetadata.java,v 1.4 2006/05/05 17:52:12 weiju Exp $
 * 
 * Created on 2006/03/10
 * Copyright 2005-2006 by Wei-ju Wu
 *
 * This file is part of The Z-machine Preservation Project (ZMPP).
 *
 * ZMPP is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * ZMPP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ZMPP; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.zmpp.media;


/**
 * This class holds information about a story.
 * 
 * @author Wei-ju Wu
 * @version 1.0
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
  
  public String getTitle() { return title; }
  public void setTitle(final String title) { this.title = title; }
  public String getHeadline() { return headline; }
  public void setHeadline(final String headline) { this.headline = headline; }
  public String getAuthor() { return author; }
  public void setAuthor(final String author) { this.author = author; }
  public String getGenre() { return genre; }
  public void setGenre(final String genre) { this.genre = genre; }
  public String getDescription() { return description; }
  public void setDescription(final String description) { this.description = description; }
  public String getYear() { return year; }
  public void setYear(final String year) { this.year = year; }
  public int getCoverPicture() { return coverpicture; }
  public void setCoverPicture(final int picnum) { this.coverpicture = picnum; }
  public String getGroup() { return group; }
  public void setGroup(final String group) { this.group = group; }
  
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
