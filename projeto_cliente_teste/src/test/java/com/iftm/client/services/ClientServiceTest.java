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

    /**
     * Testa se o método delete do serviço não lança exceção ao tentar apagar um cliente com ID existente.
     * Garante que o repositório é chamado corretamente para deletar o cliente.
     */
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

    /**
     * Testa se o método delete do serviço lança ResourceNotFoundException ao tentar apagar um cliente com ID inexistente.
     * Garante que o repositório lança a exceção esperada e que o serviço a converte corretamente.
     */
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

    /**
     * Testa se o método findAll do serviço retorna corretamente todos os clientes cadastrados.
     * Garante que a lista retornada não está vazia e que os dados são consistentes.
     */
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

    /**
     * Testa se o método findByIncome do serviço retorna corretamente os clientes com income informado.
     * Garante que a lista retornada não está vazia e que os dados são consistentes.
     */
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
