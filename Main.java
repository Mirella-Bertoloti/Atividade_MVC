package br.com.senai.techbyte;

import br.com.senai.techbyte.service.ProdutoService;
import br.com.senai.techbyte.controller.ProdutoController;
import br.com.senai.techbyte.repository.ProdutoMemoryRepository;
import br.com.senai.techbyte.repository.ProdutoRepository;
import br.com.senai.techbyte.view.ProdutoConsoleView;

public class Main {
    static void main() {
        //1. Instanciar o repositório concreto na memória
        ProdutoRepository repository = new ProdutoMemoryRepository();

        //2. Injetar o repositorio na camada service (regras de negócio)
        ProdutoService service = new ProdutoService(repository);

        //3. Injetar o service no controller
        ProdutoController controller = new ProdutoController(service);

        //4. Injetar o controller na view
        ProdutoConsoleView view = new ProdutoConsoleView(controller);

        //5. Disparar a aplicação
        view.iniciar();
    }

}
