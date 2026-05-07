package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AclServiceServiceImpl implements AclServiceService {

    private final MutableAclService mutableAclService;

    @Transactional
    @Override
    public void createAcl(Object object, Boolean adminSidOnly, Permission... permission) {
        var oid = new ObjectIdentityImpl(object);
        var acl = mutableAclService.createAcl(oid);
        adminAcl(permission, acl);
        if (!adminSidOnly) {
            ownerAcl(permission, acl);
        }
        mutableAclService.updateAcl(acl);
    }

    @Transactional
    @Override
    public void deleteAcl(long entityId, Class<?> entityClass) {
        var oid = new ObjectIdentityImpl(entityClass, entityId);
        mutableAclService.deleteAcl(oid, true);
    }

    private void ownerAcl(Permission[] permission, MutableAcl acl) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var owner = new PrincipalSid(authentication);
        for (Permission p : permission) {
            acl.insertAce(acl.getEntries().size(), p, owner, true);
        }
    }

    private void adminAcl(Permission[] permission, MutableAcl acl) {
        var admin = new GrantedAuthoritySid("ROLE_ADMIN");
        for (Permission p : permission) {
            acl.insertAce(acl.getEntries().size(), p, admin, true);
        }
    }
}
