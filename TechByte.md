# Comparacao de CRUD: Arquitetura MVC Monolitica vs REST API

Este documento apresenta uma analise tecnica do sistema de gerenciamento de estoque **TechByte Solutions** fornecido em Java, estabelecendo uma comparacao entre o modelo arquitetural atual (MVC monolitico via console) e a evolucao para o padrao Web API RESTful.

---

## Analise do Codigo Enviado

O codigo fornecido implementa um sistema de CRUD (Create, Read, Update, Delete) para produtos de informatica utilizando a linguagem Java.

### Estrutura do Projeto:
- **`br.com.senai.techbyte.model.Produto`**: Classe de dominio que representa a entidade do sistema (ID, Nome, Descricao, Preco).
- **`br.com.senai.techbyte.repository.ProdutoRepository` & `ProdutoMemoryRepository`**: Interface e implementacao do padrao Repository, responsavel pela manipulacao de dados armazenados em memoria (`List<Produto>`).
- **`br.com.senai.techbyte.service.ProdutoService`**: Camada responsavel pelas regras de negocio e validacoes (ex.: obrigatoriedade do nome, verificacao de tamanho minimo e adicao automatica da tag `[VALOR ALTO]` para produtos com preco igual ou superior a R$ 5.000,00).
- **`br.com.senai.techbyte.controller.ProdutoController`**: Atua como intermediario entre a interface com o usuario e os servicos de negocio.
- **`br.com.senai.techbyte.view.ProdutoConsoleView`**: Camada de interface com o usuario baseada em prompt de comando (Console), responsavel por capturar a entrada do teclado via `Scanner` e exibir os dados renderizados em texto.
- **`br.com.senai.techbyte.Main`**: Ponto de entrada da aplicacao, responsavel pela instanciacao e injecao de dependencias manual.

---

## Respostas as Questoes do Trabalho

### 1. Quais as diferencas arquiteturais?

- **Projeto Atual (MVC Monolitico / CLI):**
  - **Aoplamento Direto:** A View (`ProdutoConsoleView`), o Controller (`ProdutoController`), o Servico (`ProdutoService`) e o Repositorio (`ProdutoMemoryRepository`) executam na mesma JVM (Java Virtual Machine) e no mesmo processo.
  - **Interface Integrada:** A apresentacao (console) esta acoplada ao codigo-fonte da aplicacao backend.
  - **Comunicacao:** Acontece por meio de chamadas de metodos diretos em memoria entre objetos Java.

- **Padrao de uma API REST:**
  - **Desacoplamento Front-end/Back-end:** A API funciona de forma autônoma, atuando exclusivamente no fornecimento de dados e processamento de regras de negocio.
  - **Comunicacao via Rede:** A comunicacao ocorre sobre o protocolo HTTP/HTTPS utilizando formatos padronizados de troca de dados, como JSON ou XML.
  - **Stateless (Sem Estado):** Cada requisicao enviada a API contem todas as informacoes necessarias para seu processamento, eliminando a dependencia de sessoes mantidas no servidor.

---

### 2. Responsabilidades de cada camada?

#### No Sistema Atual:
- **Model (`Produto`):** Representa o objeto de dominio com seus atributos, contrutores, getters, setters, `equals` e `hashCode`.
- **Repository (`ProdutoRepository` / `ProdutoMemoryRepository`):** Isola o acesso aos dados, gerenciando operacoes de adicao, busca, atualizacao e remocao na lista em memoria.
- **Service (`ProdutoService`):** Concentra a logica de negocio, validacao dos campos (`validarDadosProduto`) e alteracoes de estado do negocio (adicao de tags).
- **Controller (`ProdutoController`):** Repassa chamadas da View para o Service.
- **View (`ProdutoConsoleView`):** Gerencia a interacao com o usuario, exibindo menus e capturando entradas via teclado.

#### Em uma Web API RESTful (ex.: Spring Boot):
- **Model/Entity:** Mapeia a estrutura das tabelas do Banco de Dados via ORM (ex.: JPA/Hibernate).
- **DTO (Data Transfer Object):** Define os dados de entrada e saida da API, evitando expor a entidade de banco diretamente na rede.
- **Repository:** Interface de persistencia integrada a ferramentas como Spring Data JPA.
- **Service:** Mantem a logica de negocio, controle transacional e regras de validacao.
- **Controller (REST Controller):** Mapeia os endpoints HTTP (`GET`, `POST`, `PUT`, `DELETE`), trata parametros de URL/corpo da requisicao e retorna codigos de status HTTP (`200 OK`, `201 Created`, `400 Bad Request`, `404 Not Found`).

---

### 3. Relacoes entre as camadas

No projeto analisado, as camadas relacionam-se atraves de **Injecao de Dependencia por Construtor**, seguindo uma cadeia direta:

```
[Main] ---> Instancia e Injeta:
              Repository ---> Service ---> Controller ---> View
```

- A **View** conhece o **Controller**.
- O **Controller** conhece o **Service**.
- O **Service** conhece a interface **Repository**.
- O **Repository** manipula a classe **Model**.

Em uma **API REST**, a camada **View e removida do backend**. O fluxo passa a ser:

```
[Cliente HTTP / Front-end] --- Requisicao HTTP (JSON) ---> [Controller]
                                                                |
                                                            [Service]
                                                                |
                                                           [Repository]
                                                                |
                                                           [Banco de Dados]
```

---

### 4. Persistencia em um BD

No projeto fornecido, a persistencia e temporaria e em memoria (`ProdutoMemoryRepository`), utilizando a estrutura `List<Produto>`. Quando a aplicacao e encerrada, os dados sao perdidos.

Para converter o projeto para persistir em um Banco de Dados Relacional (ex.: PostgreSQL, MySQL):

1. **Substituicao da Implementacao do Repositorio:**
   Criar uma classe que implemente `ProdutoRepository` utilizando conectores JDBC ou frameworks ORM (como Hibernate / JPA / Spring Data JPA).
2. **Mapeamento de Entidade:**
   Adicionar anotacoes na classe `Produto` (ex.: `@Entity`, `@Table`, `@Id`, `@GeneratedValue`).
3. **Uso de SQL/HQL:**
   Substituir a manipulacao de listas Java por instrucoes SQL como `INSERT`, `SELECT`, `UPDATE` e `DELETE`.

---

### 5. Qual o padrao arquitetural mais utilizado no mercado?

O padrao arquitetural mais adotado no mercado atual para desenvolvimento de sistemas web e enterprise e a **Arquitetura Desacoplada baseada em APIs RESTful** (frequentemente utilizando a arquitetura de Microservicos ou Monolitos Modularizados).

#### Motivos da adocao:
- **Reutilizacao de Backend:** Uma unica API pode atender simultaneamente a aplicacoes Web (React, Angular, Vue), aplicativos Mobile (Android, iOS) e integracoes entre sistemas (B2B).
- **Especializacao de Equipes:** Permite a separacao clara entre equipes de Front-end e Back-end.
- **Escalabilidade:** Permite escalar o backend e o frontend de forma independente conforme a demanda.

---

### 6. Como testar esta API?

A testagem de uma API REST e realizada em diferentes niveis:

1. **Testes Unitarios:**
   Testam isoladamente os metodos do `ProdutoService` utilizando frameworks como **JUnit** e **Mockito** para simular o comportamento do repositorio.
2. **Testes de Integracao:**
   Verificam o funcionamento conjunto entre Controller, Service e Banco de Dados (ex.: utilizando Spring LocalServerPort ou `@SpringBootTest`).
3. **Testes Manuais de Endpoints (Clientes HTTP):**
   Uso de ferramentas como **Postman**, **Insomnia**, ou o utilitario de linha de comando **cURL** para enviar requisicoes HTTP reais aos endpoints.
4. **Testes Automatizados de E2E / API:**
   Uso de bibliotecas como **RestAssured** ou **Newman** para validar contratos JSON e status HTTP em esteiras de integracao continua (CI/CD).

---

### 7. Como documentar esta API?

A documentacao de APIs REST e padronizada no mercado atraves da especificacao **OpenAPI (Swagger)**.

- **Ferramentas:** No ecossistema Java/Spring, utiliza-se a biblioteca **Springdoc-OpenAPI** ou **Swagger UI**.
- **Funcionamento:** Anotacoes no codigo (como `@Operation`, `@ApiResponse`, `@Schema`) geram automaticamente uma interface web interativa.
- **Beneficios:**
  - Permite aos desenvolvedores front-end visualizar os endpoints disponiveis, parametros exigidos e modelos de resposta.
  - Oferece um ambiente para testar requisicoes diretamente pelo navegador.

---

### 8. Como consumir esta API no front-end?

O consumo de uma API no front-end (ex.: React, Vue, Angular ou JavaScript puro) e realizado enviando requisicoes HTTP assincronas para as URLs expostas pela API.

#### Exemplo de integracao com JavaScript moderno (Fetch API):

```javascript
// Exemplo de requisicao GET para listar produtos
async function obterProdutos() {
    try {
        const response = await fetch('http://localhost:8080/api/produtos');
        if (!response.ok) {
            throw new Error('Erro ao buscar produtos');
        }
        const produtos = await response.json();
        console.log(produtos);
    } catch (error) {
        console.error('Falha na operacao:', error);
    }
}

// Exemplo de requisicao POST para cadastrar um produto
async function cadastrarProduto(novoProduto) {
    try {
        const response = await fetch('http://localhost:8080/api/produtos', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(novoProduto)
        });
        const produtoCriado = await response.json();
        console.log('Produto cadastrado:', produtoCriado);
    } catch (error) {
        console.error('Falha ao cadastrar:', error);
    }
}
```

Para requisicoes entre dominios diferentes (ex.: front-end rodando na porta 3000 e backend na 8080), e necessario configurar o recurso de **CORS (Cross-Origin Resource Sharing)** no backend.