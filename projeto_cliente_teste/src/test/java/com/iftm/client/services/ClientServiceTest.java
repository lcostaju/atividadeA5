package com.iftm.client.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.iftm.client.repositories.ClientRepository;
import com.iftm.client.services.exceptions.ResourceNotFoundException;

@ExtendWith(SpringExtension.class)
public class ClientServiceTest {

    @InjectMocks
    private ClientService servico;

    @Mock
    private ClientRepository repositorio;

    @Test
    public void testarApagarClienteQuandoIDExistenteSemRetorno() {
        // assign
        Long IdExistente = 1L;
        Mockito.doNothing().when(repositorio).deleteById(IdExistente);
        // act and assert
        Assertions.assertDoesNotThrow(() -> {
            servico.delete(IdExistente);
        });

        Mockito.verify(repositorio, Mockito.times(1)).deleteById(IdExistente);
    }

    @Test
    public void apagarGeraExcecaoQuandoIdNaoExiste() {
        // assign
        Long IdNaoExistente = 1000L;

        // act and assert
        Mockito.doThrow(EmptyResultDataAccessException.class).when(repositorio).deleteById(IdNaoExistente);
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            servico.delete(IdNaoExistente);
        });
        Mockito.verify(repositorio, Mockito.times(1)).deleteById(IdNaoExistente);
    }

}
