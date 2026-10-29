package br.com.senai.techbyte.service;

import br.com.senai.techbyte.model.Produto;
import br.com.senai.techbyte.repository.ProdutoRepository;

import java.math.BigDecimal;
import java.util.*;

public class ProdutoService {
    private final ProdutoRepository repository;

    public ProdutoService(ProdutoRepository repository){
        this.repository = repository;
    }

    public Produto cadastrarProduto(String nome, String descricao, BigDecimal preco){
        validarDadosProduto(nome, descricao, preco);

        //Regra de Negócio: Adiciona tag em produtos com preco >= 5000
        if(preco.compareTo(new BigDecimal("5000")) >= 0 && !descricao.contains("[VALOR ALTO]")){
            descricao = "[VALOR ALTO] " + descricao;
        }

        Produto novoProduto = new Produto(null, nome.trim(), descricao.trim(), preco);
        return repository.salvar(novoProduto);
    }

    public List<Produto> listarProdutos(){
        return repository.listarTodos();
    }

    public Optional<Produto> buscarPorId(Long id){
        if(id == null || id <= 0){
            throw new IllegalArgumentException("O ID informado deve ser um número positivo.");
        }
        return repository.buscarPorId(id);
    }

    public boolean atualizarProduto(Long id, String nome, String descricao, BigDecimal preco) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido para atualização.");
        }

        validarDadosProduto(nome, descricao,preco);

        Optional<Produto> existente = repository.buscarPorId(id);
        if (existente.isEmpty()) {
            return false;
        }

        // Aplica a mesma regra de tag para atualização
        if (preco.compareTo(new BigDecimal("5000.00")) >= 0 && !descricao.contains("[ALTO VALOR]")) {
            descricao = "[ALTO VALOR] " + descricao;
        }

        Produto produtoAtualizado = new Produto(id, nome.trim(), descricao.trim(), preco);
        return repository.atualizar(produtoAtualizado);
    }

    public boolean removerProduto(Long id) {
        if(id == null || id <= 0){
            throw new IllegalArgumentException("ID inválido!");
        }
        return repository.deletar(id);
    }

    public void validarDadosProduto(String nome, String descricao, BigDecimal preco){
        if(nome == null || nome.isBlank()){
            throw new IllegalArgumentException("O nome do produto é inválido");
        }
        if(nome.trim().length() < 3){
            throw new IllegalArgumentException("O nome deve ter no mínimo 3 caracteres");
        }
        if(descricao == null || descricao.isBlank()){
            throw new IllegalArgumentException("A descrição do produto é obrigatória");
        }
        if(preco == null || preco.compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("O preço não pode ser menor que zero");
        }
    }
}
