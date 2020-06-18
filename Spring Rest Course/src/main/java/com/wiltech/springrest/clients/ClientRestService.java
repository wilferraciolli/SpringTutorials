package com.wiltech.springrest.clients;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/clients")
public class ClientRestService {

    @Autowired
    private ClientAppService appService;

    @GetMapping
    public List<ClientDTO> findAll() {

        return appService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClientDTO create(@Valid @RequestBody final ClientDTO clientDTO) {

        // should be created
        return appService.create(clientDTO);
    }

    @GetMapping("/{id}")
    public ClientDTO findById(@PathVariable("id") final Long id) {

        return appService.findById(id);
    }

    @PutMapping("/{id}")
    public ClientDTO update(@PathVariable("id") final Long id, @Valid @RequestBody final ClientDTO clientDTO) {

        return appService.update(id, clientDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") final Long id) {

        appService.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
