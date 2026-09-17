package com.drones.backend1PM.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "viaCepClient", url = "https://viacep.com.br")
public interface ViaCepClient {

    @GetMapping("/ws/{cep}/json/")
    EnderecoViaCepResponse buscarPorCep(@PathVariable("cep") String cep);

    record EnderecoViaCepResponse(
            String cep,
            String logradouro,
            String bairro,
            String localidade,
            String uf,
            String complemento,
            Boolean erro) {
    }
}
