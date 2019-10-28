package eu.sblendorio.bbs.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.IsNot.not;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TenantsTest {

  @Test
  @DisplayName("when list is empty null will be returned")
  void whenListIsEmpty_ThenNullMustBeFound() {
    assertThat(Tenants.INSTANCE.getTenantList(), not(empty()));
  }

  @Test
  @DisplayName("when tenant is present in the list then a value will be returned")
  void findTenant() {
    assertTrue(Tenants.INSTANCE.tenant("CsdbReleases").isPresent());
  }
}