package com.wiltech.auth.security.filter;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.wiltech.auth.users.ApplicationUser;
import com.wiltech.core.properties.JwtConfiguration;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class JwtUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtConfiguration jwtConfiguration;

    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("1 - Attempting to authenticate user");

        final ApplicationUser applicationUser = new ObjectMapper().readValue(request.getInputStream(), ApplicationUser.class);
        if (Objects.isNull(applicationUser)) {
            throw new UsernameNotFoundException("Unable to parse the user/password");
        }

        // create an object of the user for authentication
        final UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                applicationUser.getUsername(), emptyList());
        // pass the user details to be authenticated
        usernamePasswordAuthenticationToken.setDetails(applicationUser);

        // get the authenticated used and return
        return authenticationManager.authenticate(usernamePasswordAuthenticationToken);
    }

    @SneakyThrows @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException, ServletException {
        log.info("9 - Processing token for '{}' and encrypting it", authResult.getName());

        SignedJWT signedJWT = createSignedJWT(authResult);
        String encryptedToken = encryptToken(signedJWT);

        log.info("Adding custom headers for Javascript to work well with the headers and authorization header");
        response.addHeader("Access-Control-Expose-Headers", "XSRF-TOKEN, " + jwtConfiguration.getHeader().getName());
        response.addHeader(jwtConfiguration.getHeader().getName(), jwtConfiguration.getHeader().getPrefix() + encryptedToken);
    }

    private SignedJWT createSignedJWT(Authentication authentication) throws JOSEException {
        log.info("2 - Creating token");

        ApplicationUser applicationUser = (ApplicationUser) authentication.getPrincipal();
        JWTClaimsSet jwtClaimsSet = createJWTClaimsSet(authentication, applicationUser);
        final KeyPair rsaKeys = generateKeyPair();

        log.info("3 - Building JWK from RSA keys");
        final RSAKey jwk = new RSAKey.Builder((RSAPublicKey) rsaKeys.getPublic())
                .keyID(UUID.randomUUID().toString())
                .build();

        // build the token and add the public key
        final SignedJWT signedJWT = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.ES256)
                .jwk(jwk)
                .type(JOSEObjectType.JWT)
                .build(), jwtClaimsSet);

        log.info("6 - Signing the token with the private RSA key");
        final RSASSASigner signer = new RSASSASigner(rsaKeys.getPrivate());
        signedJWT.sign(signer);

        log.info("8 - Serialized and encrypted token '{};", signedJWT.serialize());

        return signedJWT;
    }

    private JWTClaimsSet createJWTClaimsSet(Authentication authentication, ApplicationUser applicationUser) {
        log.info("4 - Creating token claims set for '{}'", applicationUser);

        return new JWTClaimsSet.Builder()
                .subject(applicationUser.getUsername())
                .claim("authorities", authentication.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(toList()))
                .claim("userId", applicationUser.getId())
                .issuer("http://academy.devdojo")
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + (jwtConfiguration.getExpiration() * 1000)))
                .build();
    }

    @SneakyThrows
    private KeyPair generateKeyPair() {
        log.info("5 - Generating RSA 2048 bits Keys");

        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);

        return generator.genKeyPair();
    }

    private String encryptToken(SignedJWT signedJWT) throws JOSEException {
        log.info("7 - Encrypt the token");

        final DirectEncrypter directEncrypter = new DirectEncrypter(jwtConfiguration.getPrivateKey().getBytes());
        final JWEObject jweObject = new JWEObject(new JWEHeader.Builder(JWEAlgorithm.DIR, EncryptionMethod.A128CBC_HS256)
                .contentType("JWT")
                .build(), new Payload(signedJWT));

        log.info("Encrypting token with system's private key");
        jweObject.encrypt(directEncrypter);

        return jweObject.serialize();
    }

}
