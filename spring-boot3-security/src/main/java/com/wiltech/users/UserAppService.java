package com.wiltech.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserAppService {

    @Autowired
    private UserRepository repository;

    public List<UserResource> findUsers() {
        return this.repository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    public UserResource create(final UserResource userResourceCreate) {

        // This method errors as this needs to be the same logic used on the Authentication serveice
        final User user = User.builder()
                .username(userResourceCreate.getUsername())
                .password("password")
                .active(userResourceCreate.getActive())
                .build();

        this.repository.save(user);

        return this.convertToDTO(user);
    }

    public UserResource findById(final Long id) {
        final User user = this.repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("could not find user for given id"));

        return this.convertToDTO(user);
    }

    public UserResource update(final Long id, final UserResource userResourcePayload) {
        final User user = this.repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("could not find user for given id"));

        user.update(userResourcePayload.getFirstName());

        return this.convertToDTO(user);
    }

    public void deleteById(final Long id) {
        final User user = this.repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("could not find user for given id"));

        this.repository.delete(user);
    }

    private UserResource convertToDTO(final User user) {
        return UserResource.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .active(user.getActive())
                .roleIds(user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .build();
    }
}
