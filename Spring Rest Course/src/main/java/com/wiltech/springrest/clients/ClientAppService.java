package com.wiltech.springrest.clients;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientAppService {

    @Autowired
    private ClientRepository clientRepository;

    public List<ClientDTO> findAll() {

        List<Client> clients = clientRepository.findAll();

        return clients.stream()
                .map(c -> convertToDTO(c))
                .collect(Collectors.toList());
    }

    public ClientDTO create(final ClientDTO clientDTO) {

        Client client = convertToEntity(clientDTO);
        clientRepository.save(client);

        return convertToDTO(client);
    }

    public ClientDTO findById(final Long id) {

        Client client = clientRepository.findById(id)
                .orElse(null);

        return convertToDTO(client);
    }

    public ClientDTO update(final Long id, final ClientDTO clientDTO) {

        Client client = clientRepository.findById(id)
                .orElse(null);

        client.updateDetails(clientDTO.getName(), clientDTO.getTelephone());
        clientRepository.saveAndFlush(client);

        return convertToDTO(client);
    }

    public void deleteById(final Long id) {

        clientRepository.deleteById(id);
    }

    private Client convertToEntity(final ClientDTO clientDTO) {

        return Client.builder()
                .name(clientDTO.getName())
                .email(clientDTO.getEmail())
                .telephone(clientDTO.getTelephone())
                .build();
    }

    private ClientDTO convertToDTO(final Client client) {

        return ClientDTO.builder()
                .id(client.getId())
                .name(client.getName()).email(client.getEmail())
                .telephone(client.getTelephone())
                .build();
    }
}
