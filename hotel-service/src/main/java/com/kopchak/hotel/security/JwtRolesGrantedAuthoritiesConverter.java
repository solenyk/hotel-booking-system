package com.kopchak.hotel.security;

import jakarta.annotation.Nonnull;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class JwtRolesGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    private final Log logger = LogFactory.getLog(getClass());

    private static final String AUTHORITY_PREFIX = "ROLE_";

    private static final String REALM_ACCESS_CLAIM = "realm_access";

    private static final String ROLES_CLAIM = "roles";

    @Override
    public Collection<GrantedAuthority> convert(@Nonnull Jwt jwt) {
        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (String authority : getAuthorities(jwt)) {
            grantedAuthorities.add(new SimpleGrantedAuthority(AUTHORITY_PREFIX + authority));
        }
        return grantedAuthorities;
    }

    private Collection<String> getAuthorities(Jwt jwt) {
        Map<String, List<String>> realmAccessRoles = jwt.getClaim(REALM_ACCESS_CLAIM);
        if (realmAccessRoles == null) {
            logger.debug("Realm access roles not present");
            return Collections.emptyList();
        }
        return realmAccessRoles.get(ROLES_CLAIM);
    }
}
