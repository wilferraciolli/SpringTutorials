package com.wiltech.springrest.clients;

import lombok.Builder;

@Builder
public class ClientDTO {

    private Long id;

    private String nome;

    private String email;

    private String telefone;

    public ClientDTO(Long id, String nome, String email, String telefone) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
    }
}
