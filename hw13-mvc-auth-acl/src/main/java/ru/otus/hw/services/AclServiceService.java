package ru.otus.hw.services;

import org.springframework.security.acls.model.Permission;

public interface AclServiceService {

    void createAcl(Object object, Boolean adminSidOnly, Permission... permission);

    void deleteAcl(long entityId, Class<?> entityClass);

}
