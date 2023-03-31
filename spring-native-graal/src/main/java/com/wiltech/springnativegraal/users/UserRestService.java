package com.wiltech.springnativegraal.users;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/users", produces = "application/json")
public class UserRestService {

    @Autowired
    private UserAppService appService;

    @GetMapping("/template")
    public ResponseEntity<UserResource> template() {
        final UserResource response = UserResource.builder()
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("")
    public ResponseEntity<UserResource> create(@RequestBody final UserResource payload) {
        final UserResource createdResource = this.appService.create(payload);

        return ResponseEntity.ok(createdResource);
    }

    @GetMapping("")
    public ResponseEntity<List<UserResource>> findAll() {
        final List<UserResource> response = this.appService.findUsers();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResource> findById(@PathVariable("id") final Long id) {
        final UserResource resource = this.appService.findById(id);

        return ResponseEntity.ok(resource);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResource> update(@PathVariable("id") final Long id, @RequestBody final UserResource payload) {
        final UserResource updatedResource = this.appService.update(id, payload);

        return ResponseEntity.ok(updatedResource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteById(@PathVariable("id") final Long id) {
        this.appService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

}
