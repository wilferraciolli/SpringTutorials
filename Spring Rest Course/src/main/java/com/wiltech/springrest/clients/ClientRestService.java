package com.wiltech.springrest.clients;

import java.util.List;

import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/clients")
public class ClientRestService {

    @Autowired
    private ClientRestAppService appService;

    @GetMapping
    public List<ClientDTO> findAll() {

        return appService.findAll();
    }

    @PostMapping("/")
    public List<ClientDTO> create(final ClientDTO clientDTO) {

        return appService.findAll();
    }

    @GetMapping("{id}")
    public List<ClientDTO> findById(@PathParam("id") final Long id) {

        return appService.findAll();
    }

    @PutMapping("{id}")
    public List<ClientDTO> update(@PathParam("id") final Long id, final ClientDTO clientDTO) {

        return appService.findAll();
    }

    @DeleteMapping("{id}")
    public List<ClientDTO> deleteById(@PathParam("id") final Long id) {

        return appService.findAll();
    }
}
