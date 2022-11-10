package one.digitalinnovation.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import one.digitalinnovation.model.Cliente;
import one.digitalinnovation.model.ClienteRepository;
import one.digitalinnovation.model.Endereco;
import one.digitalinnovation.model.EnderecoRepository;
import one.digitalinnovation.service.ClienteService;
import one.digitalinnovation.service.ViaCepService;

@Service
public class ClienteServiceImpl implements ClienteService {
	

	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private EnderecoRepository enderecoRepository;
	
	@Autowired
	private ViaCepService viaCepService;
	
	

	@Override
	public Iterable<Cliente> buscarTodos() {
		
		return clienteRepository.findAll();
	}

	@Override
	public Cliente buscarPorId(Long id) {		
		//Optional tem uma implementação de poder ou não existir um cliente.
		Optional<Cliente> cliente = clienteRepository.findById(id);
		return cliente.get();
	}
	
	@Override
	public void inserir(Cliente cliente) {
		salvarClienteComCep(cliente);
	}

	@Override
	public void atualizar(Long id, Cliente cliente) {
		//Optional tem uma implementação de poder ou não existir um cliente.
		Optional<Cliente> clienteBd = clienteRepository.findById(id);
		if(clienteBd.isPresent()) {
			salvarClienteComCep(cliente);			
		}
	}
	
	@Override
	public void deletar(Long id) {
		
		clienteRepository.deleteById(id);
	}
	
	private void salvarClienteComCep(Cliente cliente) {
		//Verifica se ja tem CEP cadastrado
		String cep = cliente.getEndereco().getCep();
		//Se existir, devolve o endereço, caso nao exista um cep, cai no CALLBACK (orElseGet). Que solicita um novo endereço a api ViaCep
		Endereco endereco = enderecoRepository.findById(cep).orElseGet(() -> {
			Endereco NovoEndereco = viaCepService.consultarCep(cep);
			enderecoRepository.save(NovoEndereco);
			return NovoEndereco;
		});
		cliente.setEndereco(endereco);		
		clienteRepository.save(cliente);
		
	}
	
	

}
