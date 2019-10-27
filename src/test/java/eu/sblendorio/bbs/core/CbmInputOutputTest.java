package eu.sblendorio.bbs.core;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.IsNot.not;

class CbmInputOutputTest {

  @Test
  void readTextFile() throws IOException {
    List<String> fileEntries = CbmInputOutput.readTextFile("notEmptyTestFile.txt");
    assertThat(fileEntries, not(empty()));
  }

  @Test
  void readEmptyTextFile() throws IOException {
    List<String> fileEntries = CbmInputOutput.readTextFile("emptyTestFile.txt");
    assertThat(fileEntries, empty());
  }

  @Test
  void notFoundTextFile() throws IOException {
    List<String> fileEntries = CbmInputOutput.readTextFile("notFoundTestFile.txt");
    assertThat(fileEntries, empty());
  }
}