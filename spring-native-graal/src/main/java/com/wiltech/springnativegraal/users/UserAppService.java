package com.wiltech.springnativegraal.users;

import org.springframework.beans.factory.annotation.Autowired;
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
                .collect(Collectors.toList());
    }


    public UserResource create(final UserResource userResourceCreate) {
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
                .firstName(user.getUsername())
                .lastName(user.getUsername())
                .dateOfBirth(LocalDate.now())
                .active(user.getActive())
                .roleIds(List.of("Role1", "Role2"))
                .build();
    }
}
