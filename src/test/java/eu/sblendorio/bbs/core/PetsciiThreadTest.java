package eu.sblendorio.bbs.core;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PetsciiThreadTest {

  @Test
  @DisplayName("change a name of the client must change the name. (regression test - the previous code will generate a NPE)")
  void changeClientName() {
    PetsciiThread client = spy(PetsciiThread.class);
    client.clientName = "originalName";
    PetsciiThread.clients = ImmutableMap.of(1L, client);
    PetsciiThread.changeClientName("originalName", "modifiedName");
    assertEquals("modifiedName", PetsciiThread.clients.get(1L).getClientName());
  }

  @Test
  @DisplayName("When clients list is empty, then null must be returned")
  void whenClienListIsEmpty_ThenNullMustBeReturned() {
    Optional<PetsciiThread> test = PetsciiThread.getClientByName("test");
    assertFalse(test.isPresent());
  }

  @Test
  @DisplayName("When a client name is not found in the clients list, then null must be returned")
  void whenClientIsFoundByName_ThenItsIntanceMustBeReturned() {
    PetsciiThread client = mock(PetsciiThread.class);
    String clientNameToSearch = "test";
    String clientNameIntoList = "listClientName";
    when(client.getClientName()).thenReturn(clientNameIntoList);
    PetsciiThread.clients = ImmutableMap.of(1L, client);
    Optional<PetsciiThread> test = PetsciiThread.getClientByName(clientNameToSearch);
    assertFalse(test.isPresent());
  }

  @Test
  @DisplayName("When a client name is found in the clients list, then the client must be returned")
  void whenClientIsNotFoundByName_ThenNullMustBeReturned() {
    PetsciiThread client = mock(PetsciiThread.class);
    String clientName = "test";
    when(client.getClientName()).thenReturn(clientName);
    PetsciiThread.clients = ImmutableMap.of(1L, client);
    Optional<PetsciiThread> test = PetsciiThread.getClientByName(clientName);
    assertEquals(client, test.orElse(null));
  }

  @Test
  @DisplayName("clients map implementation must not accept null values")
  void clientsMapImplementationMustNotAcceptNullValues() {
    PetsciiThread.clients = PetsciiThread.defaultClientsMapImplementation();
    assertThrows(NullPointerException.class, () -> PetsciiThread.clients.put(1L, null));
  }
}