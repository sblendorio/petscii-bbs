package eu.sblendorio.bbs.core;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.IsNot.not;

class PetsciiInputOutputTest {

  @Test
  void readTextFile() throws IOException {
    List<String> fileEntries = PetsciiInputOutput.readTextFile("eu/sblendorio/bbs/core/notEmptyTestFile.txt");
    assertThat(fileEntries, not(empty()));
  }

  @Test
  void readEmptyTextFile() throws IOException {
    List<String> fileEntries = PetsciiInputOutput.readTextFile("eu/sblendorio/bbs/core/emptyTestFile.txt");
    assertThat(fileEntries, empty());
  }

  @Test
  void notFoundTextFile() throws IOException {
    List<String> fileEntries = PetsciiInputOutput.readTextFile("eu/sblendorio/bbs/core/notFoundTestFile.txt");
    assertThat(fileEntries, empty());
  }
}