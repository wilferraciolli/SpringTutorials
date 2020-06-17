package com.wiltech.springrest.clients;



import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientRestAppService {

    @Autowired
    private ClientRepository clientRepository;

    public List<ClientDTO> findAll() {

        List<Client> clients = clientRepository.findAll();

        return clients.stream()
                .map(c -> convertToDTO(c))
                .collect(Collectors.toList());
    }

    private ClientDTO convertToDTO(final Client client) {

        return ClientDTO.builder()
                .id(client.getId())
                .nome(client.getNome()).email(client.getEmail())
                .telefone(client.getTelefone())
                .build();
    }
}
