package droid64.addons;

import eu.sblendorio.bbs.core.PetsciiThread;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsArrayWithSize.emptyArray;
import static org.junit.jupiter.api.Assertions.*;

class DiskUtilitiesTest {

  @ParameterizedTest
  @ValueSource(ints = {Integer.MIN_VALUE, -1, 0, 2, Integer.MAX_VALUE})
  @DisplayName("if count is different from 1 then null must be returned")
  void whenCountIsDifferentFromOne_ThenNullMustBeReturned(int count) throws IOException {
    PetsciiThread.DownloadData downloadData = DiskUtilities.getDownloadData(null, count, null);
    assertNull(downloadData);
  }

  @Test
  @DisplayName("if count is 1 then a DownloadData must be returned")
  void whenCountIsOne_ThenANonNullInstanceMustBeReturned() throws IOException {
    String fileName = "test";
    PetsciiThread.DownloadData downloadData = DiskUtilities.getDownloadData(new byte[1], 1, fileName);
    assertNotNull(downloadData);
    assertEquals(fileName, downloadData.getFilename());
    assertThat(ArrayUtils.toObject(downloadData.getContent()), emptyArray());
  }
}