package eu.sblendorio.bbs.core;

import static java.util.Comparator.comparing;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;

public class Tenants {

  public static final Tenants INSTANCE = new Tenants();
  private List<Class<? extends PetsciiThread>> tenantList = new ArrayList<>();

  private Tenants() {
    ServiceLoader<PetsciiThread> services = ServiceLoader.load(PetsciiThread.class);
    for (PetsciiThread service : services) {
      tenantList.add(service.getClass());
    }
    tenantList.sort(comparing(Class::getSimpleName));
  }

  public List<Class<? extends PetsciiThread>> getTenantList() {
    return tenantList;
  }

  public Optional<Class<? extends PetsciiThread>> tenant(String tenantName) {
    for (Class<? extends PetsciiThread> tenant : tenantList) {
      if (tenant.getSimpleName().equalsIgnoreCase(tenantName)) {
        return Optional.of(tenant);
      }
    }
    return Optional.empty();
  }
}
