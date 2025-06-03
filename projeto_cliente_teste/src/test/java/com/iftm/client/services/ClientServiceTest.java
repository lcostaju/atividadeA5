package com.iftm.client.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.iftm.client.entities.Client;
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

    @Test
    public void retornaTodosClientes() {
        // assign
        List<Client> listaClientes = new ArrayList<Client>();
        listaClientes.add(new Client(4l, "Carolina Maria de Jesus", "10419244771", 7500.0,
                Instant.parse("1996-12-23T07:00:00Z"), 0));

        // act and assert
        Mockito.when(repositorio.findAll()).thenReturn(listaClientes);

        List<Client> clientesRetornados = servico.findAll();

        Assertions.assertFalse(clientesRetornados.isEmpty());
        Assertions.assertEquals(listaClientes.size(), clientesRetornados.size());
        Assertions.assertEquals(listaClientes.get(0), clientesRetornados.get(0));

        Mockito.verify(repositorio, Mockito.times(1)).findAll();
    }

    @Test
    public void retornaClientesComIncomeInformado() {
        // assign
        List<Client> listaClientes = new ArrayList<Client>();
        listaClientes.add(new Client(4l, "Carolina Maria de Jesus", "10419244771", 7500.0,
                Instant.parse("1996-12-23T07:00:00Z"), 0));

        // act and assert
        Mockito.when(repositorio.findByIncome()).thenReturn(listaClientes);

        List<Client> clientesRetornados = servico.findByIncome();

        Assertions.assertFalse(clientesRetornados.isEmpty());
        Assertions.assertEquals(listaClientes.size(), clientesRetornados.size());
        Assertions.assertEquals(listaClientes.get(0), clientesRetornados.get(0));

        Mockito.verify(repositorio, Mockito.times(1)).findAll();
    }

}
