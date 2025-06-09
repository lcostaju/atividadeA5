package com.iftm.client.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.iftm.client.dto.ClientDTO;
import com.iftm.client.entities.Client;
import com.iftm.client.repositories.ClientRepository;
import com.iftm.client.services.exceptions.ResourceNotFoundException;

@ExtendWith(SpringExtension.class)
public class ClientServiceIntegrationTest {

    @InjectMocks
    private ClientService servico;

    @Mock
    private ClientRepository repositorio;

    /**
     * Testa se o método delete do serviço não lança exceção ao tentar apagar um
     * cliente com ID existente.
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
     * Testa se o método delete do serviço lança ResourceNotFoundException ao tentar
     * apagar um cliente com ID inexistente.
     * Garante que o repositório lança a exceção esperada e que o serviço a converte
     * corretamente.
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
     * Testa se o método findAll do serviço retorna corretamente todos os clientes
     * cadastrados.
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
     * Testa se o método findByIncome do serviço retorna corretamente os clientes
     * com income informado.
     * Garante que a lista retornada não está vazia e que os dados são consistentes.
     */
    @Test
    public void findByIncomeDeveriaRetornarClientesComIncomeInformado() {
        Double income = 7500.0;
        List<Client> lista = List.of(new Client(4L, "Carolina", "123", income, Instant.now(), 0));

        Mockito.when(repositorio.findByIncome(income)).thenReturn(lista);

        List<Client> resultado = servico.findByIncome(income);

        Assertions.assertEquals(1, resultado.size());
        Assertions.assertEquals(income, resultado.get(0).getIncome());
        Mockito.verify(repositorio, Mockito.times(1)).findByIncome(income);
    }

    /**
     * Testa se o método findAllPaged do serviço retorna uma página contendo
     * todos os clientes cadastrados.
     * Garante que o método do repositório foi chamado e a página não está vazia.
     */
    @Test
    public void findAllPagedDeveriaRetornarPaginaComTodosClientes() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Client> lista = List.of(new Client());
        Page<Client> page = new PageImpl<>(lista);

        Mockito.when(repositorio.findAll(pageRequest)).thenReturn(page);

        Page<ClientDTO> resultado = servico.findAllPaged(pageRequest);

        Assertions.assertFalse(resultado.isEmpty());
        Mockito.verify(repositorio, Mockito.times(1)).findAll(pageRequest);
    }

    /**
     * Testa se o método findById do serviço retorna um ClientDTO
     * quando o ID informado existe no banco de dados.
     */
    @Test
    public void findByIdDeveriaRetornarClientDTOQuandoIdExistir() {
        Long id = 1L;
        Client client = new Client(id, "Nome", "123", 5000.0, Instant.now(), 0);

        Mockito.when(repositorio.findById(id)).thenReturn(Optional.of(client));

        ClientDTO resultado = servico.findById(id);

        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(id, resultado.getId());
        Mockito.verify(repositorio).findById(id);
    }

    /**
     * Testa se o método findById do serviço lança ResourceNotFoundException
     * quando o ID informado não existir no banco de dados.
     */
    @Test
    public void findByIdDeveriaLancarExcecaoQuandoIdNaoExistir() {
        Long id = 999L;
        Mockito.when(repositorio.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> servico.findById(id));
        Mockito.verify(repositorio).findById(id);
    }

    /**
     * Testa se o método insert do serviço retorna um ClientDTO ao inserir
     * um novo cliente com sucesso.
     */
    @Test
    public void insertDeveriaRetornarClientDTO() {
        ClientDTO dto = new ClientDTO(new Client());
        Client client = dto.toEntity();

        Mockito.when(repositorio.save(Mockito.any())).thenReturn(client);

        ClientDTO resultado = servico.insert(dto);

        Assertions.assertNotNull(resultado);
        Mockito.verify(repositorio).save(Mockito.any());
    }

    /**
     * Testa se o método update do serviço retorna um ClientDTO
     * ao atualizar um cliente existente.
     * Garante que os métodos getOne e save foram chamados.
     */
    @Test
    public void updateDeveriaRetornarClientDTOQuandoIdExistir() {
        Long id = 1L;
        ClientDTO dto = new ClientDTO(new Client());
        Client client = new Client();

        Mockito.when(repositorio.getOne(id)).thenReturn(client);
        Mockito.when(repositorio.save(client)).thenReturn(client);

        ClientDTO resultado = servico.update(id, dto);

        Assertions.assertNotNull(resultado);
        Mockito.verify(repositorio).getOne(id);
        Mockito.verify(repositorio).save(client);
    }

    /**
     * Testa se o método update do serviço lança ResourceNotFoundException
     * ao tentar atualizar um cliente com ID inexistente.
     */
    @Test
    public void updateDeveriaLancarResourceNotFoundExceptionQuandoIdNaoExistir() {
        Long id = 1000L;
        ClientDTO dto = new ClientDTO(new Client());

        Mockito.when(repositorio.getOne(id)).thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> servico.update(id, dto));
        Mockito.verify(repositorio).getOne(id);
    }
}
