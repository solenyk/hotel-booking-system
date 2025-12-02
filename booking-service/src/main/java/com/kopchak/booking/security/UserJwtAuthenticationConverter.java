package com.kopchak.booking.security;

import com.kopchak.booking.domain.HotelUser;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserJwtAuthenticationConverter implements Converter<Jwt, UsernamePasswordAuthenticationToken> {

    private final JwtRolesGrantedAuthoritiesConverter jwtRolesGrantedAuthoritiesConverter;

    @Override
    public UsernamePasswordAuthenticationToken convert(Jwt jwt) {
        HotelUser hotelUser = HotelUser
                .builder()
                .id(UUID.nameUUIDFromBytes(jwt.getSubject().getBytes(StandardCharsets.UTF_8)))
                .email(jwt.getClaimAsString("email"))
                .firstName(jwt.getClaimAsString("given_name"))
                .lastName(jwt.getClaimAsString("family_name"))
                .build();
        Collection<GrantedAuthority> authorities = jwtRolesGrantedAuthoritiesConverter.convert(jwt);
        return new UsernamePasswordAuthenticationToken(hotelUser, jwt, authorities);
    }
}
