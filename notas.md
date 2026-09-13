***Um Aviso Importante***

> O Código apresentado aqui está longe de ser perfeito. Meu foco será exclusivamente na prática de recurso realizando um sobrevoo na estrutura de projeto que  o Java oferece para acesso a dados de forma "crua" e nativa o JDBC, que é o tema central deste nanoprojeto. Então, caro programador experiente que está lendo isso, peço que não se preocupe demais com outras questões como arquitetura ou boas práticas. foco no essencial!

# Perguntas que busco responder com este nanoprojeto:

## O que preciso fazer manualmente quando acesso um banco relacional diretamente através do JDBC?

## Quando uma operação envolve várias instruções SQL, eu preciso controlar explicitamente a transação?

---

## Criar, configurar e iniciar o projeto (1)

### Setup base

- [X] criar projeto [1]

```Shell
mvn archetype:generate \
  -DgroupId=com.seunome.delivery \
  -DartifactId=jdbc-delivery-crud \
  -DarchetypeArtifactId=maven-archetype-quickstart \
  -DarchetypeVersion=1.5 \
  -DinteractiveMode=false
```

* [X] configurando aplicação para conexão com postgres[2]

usei a dependência:

```XML
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.7.4</version>
</dependency>
```

- não consegui criar o banco pelo dbeaver, então usei linha de comando para criação do banco.
- posteriormente alterei o código para ter um factory de conexão com o banco(a ideia é prover uma conexão via singleton) e a aplicação se conectou [3]
- estou usando dados da.env para conexão e não .properties

Comandos utilizados:

```Shell
#criar banco de dados
psql -U postgres -c "CREATE DATABASE delivery"

#listar todos os bancos de dados criados
psql -U postgres -l

#para ver as tabelas criadas no banco
psql -U postgres -d delivery -c "\dt"
#ou
psql -U postgres -d delivery -c "\dt *.*"
```

* [X] Criar entidades que serão usadas na prática do projeto [4]
* [X] realizar consulta de entidades individualmente  realizar parsing para objeto

## Operações a serem realizadas:

### Consulta

* [X] findAll Product
* [X] findAll Order
* [X] findById Product
* [X] findById Order
* [X] Order + products

### Inserção

* [X] Product
* [X] Order
* [X] Order + Products

### Atualização

* [X] Product
* [X] Order

### Deleção

* [X] Product
* [X] Order
* [X] Relacionamento Order/Product

### Operações extras:

* [X] Paginação
* [X] Concorrência
* [X] Operação em lote

---

### Realizando buscas de uma entidade e parsing para o objeto.

- observa-se a necessidade de parsing manual, um atributo por vez, porém o que pode ocorrer é a necessidade de tratamento de valor nulo assim como combinação entre atributos buscados na consulta(projeção) e mapeamento para montagem do objeto.
- além disso é preciso ter atenção aos tipos de dados que são retornados pelo resultSet e como serão feitos parsing para os tipos dos objetos.

* [X] realizar consulta com junção de entidades e realizar parsing

### Realizando buscas de entidades por junção

```SQL
SELECT * FROM tb_order
INNER JOIN tb_order_product ON tb_order.id = tb_order_product.order_id
INNER JOIN tb_product ON tb_product.id = tb_order_product.product_id
```

![1788136594357](image/notas/1788136594357.png)

- como pode-se observar acima o retorno da query de junção provoca repetição de registros, isso se deu devido ao nome "id" ser igual para as tabels que estão sendo unidas, para tratar isso pode-se especificar melhor qual id está sendo consultado/utilizado para montar o objeto
- o ponto acima é importante pois a relação passa por uma tabela intermediária que representa e trata a relação N:N entre Product e Order.
- para evitar tratar no mapeamento diretamente onde fiz isolado em cada entidade, preferi especificar quais campos serão retornados na consulta e mapear em um retorno personalizado, o resultado a abaixo mostra que funciona corretamente agora.

![1788137647844](image/notas/1788137647844.png)

- cada ordem possuia 2 produtos no moento da consulta, foram retornados 2 registros compostos por dois produtos cada.

### Implelentei um Menu

implementei um menu para facilitar o uso e testes para ver o resultado limpo e rápido no terminal enquanto o projeto crescer e foco no que importa, que é o jdbc.

aproveitei pra separar em alguns pacotinhos inspirados em mvc.

- Observei duas possibilidades de passar params para query quando fiz findByid como params posicionais, jdbc puro não tem opção de params nomeados, porém Hibernate e NamedParameterJdbcTemplate do Spring permitem. [7]
- o PreparedStatatement transforma a query em string para query de fato e resolve os params.
  - também evita sql injection dado que ele nativamente trata os poarams como dados e não como parte da query string.
  - faz tratamento e escaping de tipos
  - Planeja e otimiza a consulta melhorando seu plano de execução ao compilá-la.

também usei um optional e ter um exemplo simples guardado [8]

Operações implementadas

- FIndAll
- Order + products
- FIndById

---

- para criação de Insert[9] utilizei PrepareStatement com **RETURN_GENERATED_KEYS** de param que disponibiliza as chaves geradas pelo banco após o insert, onde nesse caso o Postgre irá gerar automaticamente o ID da entidade salva no banco.
- para execução fiz uso de executeUpdate(), que é o método recomendado para INSERT, UPDATE e DELETE, e retorna quantidade de registros afetados.
- depois na obtenção do resultado o getGeneratedKeys devolveu as chaves geradas para que eu pudesse atualizar o objeto que está em memória.
- Nos insert simples a aplicação abre a transção e realizar commit automaticamente devido a configuração padrão do JDBC em autoCommit[10], e com isso eu só preciso controlar abertura, commit e rollback de transação quando tiver múltiplas operações.
- na inserção de OrderWithProducts usei commit = false para que eu pudesse criar ordem e depois as relações com produtos, e só depois realizar commit, mas além disso validei antes de colocar os produtos na ordem, para garantir que eles existem.
- após a execução voltei o autocommit para true para retornar o comportamento padrão do jdbc
- o que chamou minha atenção nesse ponto é o controle manual, se o desenvolvedor fica responsável por controlar autocommit e outros pontos sensíveis do acesso e manipulação de dados junto a base de dados e cometer algum esquecimento ele pode ter problemas de integridade de dados.

### Sobre o Update

- vi que rowsAffected mostra que uma operação foi realizada na linha, não que dados mudaram, ou seja, está a nível de execução do update e não de dados, não vejo garantia a não ser que compare mudança com objeto antes e depois.
- Preço do produto é double, mas o objeto passa Double(wrapper), logo pode ocorrer nullPointer pois se o atributo estiver vazio ele não realizar unboxing do dado, pra resolver isso o jdbc permite passar um `st.setNull(2, Types.DOUBLE)`visando garantir o tratamento de dados nulos(como no caso do preço).
- o update que criei atualiza todos os atributos da entidade, com isso se o objeto passar atributo nulo e o campo no banco permitir nulo ele será limpo, o hibernate lida com isso da mesma forma pois monitora a entidade com ela em memória.
- para evitar problemas de update limpando dados de registros do banco pode-se obter a entidade e alterar apenas o que é preciso, ou então escrever um update que altera apenas o que for passado, por exemplo: escrever um update por atributo ou um update que monta a query apenas com os atributos que não são nulos, daí surge outra necessidade, quando quiser limpar um dado será necessário usar o update completo passando tudo e limpando o atributo desejado.
- alteração de entidade completa ou parcial é essencialmente o que é tratado em um endpoint de API realizando PUT(completo) ou Patch(parcial), é um ponto de atenção que não tenha tanta especialização de comportamento para evitar descontrole na manipulação dos atributos de entidades, geralmente o que vejo um objeto sendo recuperado e passado para um update completo, apenas com os campos alterados, independente do endpoint.

### sobre delete

- jdbc se comporta bem com um delete em uma entidade associada, ele emite uma excessão informação que não foi possível  deletar um registro por estar associado em uma outra entidade via chave estrangeira.
- na deleção da relação entre order e product a consulta simples de order não basta para seguir para deleção da relação com product, pois ela traz apenas a order, então faz-se necessário a criação de uma consulta que possa tazer order completa(incluindo os products), o ponto dessa operação é que envolve o conceito de eager e lazyload [11] que o jdbc não implementa, sendo necessário implementação manual, ou seja, uma consulta que recupere as duas entidades do banco e depois o resultset precisa ser parseado na composição de order que contem products, como feito na findOrdersWithProducts, só que agora é byID, ou seja, retorna apenas 1 registro.

### Operações extras

- para praticar um pouco mais visualizando como o jdbc trabalha, decidi implementar outras operações para ver de amostra e experimento.

**Paginação. [12]**

- a execução de consulta paginada resume-se em definir em qual página está e qual o salto (size ou offset calculado) de cada página, desta forma é possível navegar entre "páginas" que é o mesmo que ver uma sequência de itens específicos.
- a query mudou muito pouco, mas foi preciso implementar um cálculo de deslocamento da página (offset)
- a paginação feita com offset e limit na query não fica em cache, ou seja, eu posso parar aplicação e passar outra página que funcionará, cada consulta retorna um grupo de registros sob o conceito de página.

**Concorrência. [ 13 ]**

- para simular concorrência vou testar o uso de lock otimista.
- consiste em basicamente usa um campo no banco de dados para ter validação de versão do dado observado e usa esse campo como validação de garantia de escrita ou não de informação.
- em uma operação usei  id + price do produto, em outra use id + versão, apenas para deixar salvo as duas formas de realizar, sendo o uso de versão padronizado inclusive pelo Hibernate.
- existe a possibilidade também do uso de timestamp mas pela probabilidade de ocorrer sinc de nework time proocol pode falhar.
- para usar version(atributo criado em produto) precisei criar o atributo na entidade, alterar query findById para adicionar o retorno(que pode ocorrer se for necessário o retorno de version em outras consultas também), além de setar version na consulta com jdbc, ou seja, ocorreram diversos pontos de alteração de forma manual, não apenas na entidade, mas também em métodos do dao, e provalmente em uma aplicação com regras de negócio implementadas em um service várias regras precisariam ser alteradas também.

```SQL
ALTER TABLE tb_product
ADD COLUMN version INTEGER NOT NULL DEFAULT 0;
```

- usei threads[14] para poder simular dois usuários concorrendo pela escrita no dado que está no banco.
- precisei capturar dentro do método que realiza o teste de concorrencia a SQLException pois o run() da interface runnable não lança SQLExeption[15]
- para sincronização das threads e observar o comportamento de validação de dados obsoletos da concorrência funcionando fiz uso de CountDown e controle de sincronização da thread com await
- para validar no teste eu consultei com ambas threads, depois executei A enquanto B esperava e então liberei B para que ela tetasse executar o update com dados obsoletos.

**Batch [16]**

- para operação em lote, fiz inserção de 5 produtos onde uma transação fica aberta,
- o prepareStatement vai ser montando recebendo params de todos os products,
- por fim ao ser executado o executeBatch irá retornar um array contendo o resultaod de cada operação, sendo 1 para sucesso...

---

### Resumo

- uma implementação que fiz no experimento foi de colocar as consultas e transformações em uma camada Dao com as implementações isoladas  e só chamá-las no App.
- Este repositório permanecerá salvo no github para possíveis novas implementações e novos experimentos ligados a jdbc.

Pesquisando observei diversos pontos que levaram o ecossitema a naturalmente criar o Hibernate como:

- aumento de produtividade por prover boilerplate
- gestão de transações integrado.
- redução de uso de try-catch
- uso de HQL, uma forma mais próxima dos objetos do que do banco relacional para escrita de querys.
- tratamento facilitado para lidar com concorrência

há ainda outros pontos relevantes que me levam a criar um nanoprojeto para explorar mais a configuração e uso do hibernate em breve.

---

## Pesquisas

[1] [maven-apache-org.translate.goog/guides/getting-started/maven-in-five-minutes.html?_x_tr_sl=en&amp;_x_tr_tl=pt&amp;_x_tr_hl=pt&amp;_x_tr_pto=tc](https://maven-apache-org.translate.goog/guides/getting-started/maven-in-five-minutes.html?_x_tr_sl=en&_x_tr_tl=pt&_x_tr_hl=pt&_x_tr_pto=tc)

[1.1]  [www.devmedia.com.br/introducao-ao-maven/25128](https://www.devmedia.com.br/introducao-ao-maven/25128)

[2] [www.instaclustr.com/support/documentation/postgresql/using-postgresql/connect-to-postgresql-with-java](https://www.instaclustr.com/support/documentation/postgresql/using-postgresql/connect-to-postgresql-with-java/)

[2.1] [learn.microsoft.com/pt-br/azure/postgresql/connectivity/connect-java?tabs=passwordless](https://learn.microsoft.com/pt-br/azure/postgresql/connectivity/connect-java?tabs=passwordless)

[2.2] [neon-com.translate.goog/postgresql/jdbc/connecting-to-postgresql-database?_x_tr_sl=en&amp;_x_tr_tl=pt&amp;_x_tr_hl=pt&amp;_x_tr_pto=tc&amp;_x_tr_hist=true](https://neon-com.translate.goog/postgresql/jdbc/connecting-to-postgresql-database?_x_tr_sl=en&_x_tr_tl=pt&_x_tr_hl=pt&_x_tr_pto=tc&_x_tr_hist=true)

[3] [www.devmedia.com.br/aprendendo-java-com-jdbc/29116](https://www.devmedia.com.br/aprendendo-java-com-jdbc/29116)

[3.1] [www.geeksforgeeks.org/java/establishing-jdbc-connection-in-java](https://www.geeksforgeeks.org/java/establishing-jdbc-connection-in-java/)

[3.2] [www.guj.com.br/t/connectionfactory-jdbc-boas-ideias-para-melhorar-a-classe/294712](https://www.guj.com.br/t/connectionfactory-jdbc-boas-ideias-para-melhorar-a-classe/294712/)

[3.3] [cursos.alura.com.br/forum/topico-connectionfactory-como-singleton-124574](https://cursos.alura.com.br/forum/topico-connectionfactory-como-singleton-124574)

[4] [github.com/devsuperior/jdbc-postgres](https://github.com/devsuperior/jdbc-postgres)

[5][www.geeksforgeeks.org/java/jdbc-result-set](https://www.geeksforgeeks.org/java/jdbc-result-set/)

[5.1] [www.devmedia.com.br/introducao-a-jpa-java-persistence-api/28173](https://www.devmedia.com.br/introducao-a-jpa-java-persistence-api/28173)

[5.2] [jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2](https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2)

[5.3] [jakarta.ee/specifications/persistence/3.2/apidocs/jakarta.persistence/module-summary.html](https://jakarta.ee/specifications/persistence/3.2/apidocs/jakarta.persistence/module-summary.html)

[6][medium.com/javarevisited/why-hibernate-is-better-than-jdbc-key-advantages-and-examples-201b75fb5687](https://medium.com/javarevisited/why-hibernate-is-better-than-jdbc-key-advantages-and-examples-201b75fb5687)

[6.1][cybernite.in/blog/advantages-of-hibernate-over-jdbc](https://cybernite.in/blog/advantages-of-hibernate-over-jdbc/)

[7] [docs.oracle.com/javase/tutorial/jdbc/basics/prepared.html](https://docs.oracle.com/javase/tutorial/jdbc/basics/prepared.html)

[7.1] [www.geeksforgeeks.org/sql/difference-between-statement-and-preparedstatement](https://www.geeksforgeeks.org/sql/difference-between-statement-and-preparedstatement/)

[8] [pt.stackoverflow.com/questions/447672/para-que-serve-o-optional-do-java-8-como-usar](https://pt.stackoverflow.com/questions/447672/para-que-serve-o-optional-do-java-8-como-usar)

[9][neon.com/postgresql/jdbc/insert](https://neon.com/postgresql/jdbc/insert)

[10][neon.com/postgresql/jdbc/transaction](https://neon.com/postgresql/jdbc/transaction)

[11] [www.devmedia.com.br/lazy-e-eager-loading-com-hibernate/29554](https://www.devmedia.com.br/lazy-e-eager-loading-com-hibernate/29554)

[12] [www.baeldung.com/java-jdbc-pagination](https://www.baeldung.com/java-jdbc-pagination)

[13][bytebytego.com/guides/pessimistic-vs-optimistic-locking](https://bytebytego.com/guides/pessimistic-vs-optimistic-locking/)

[13.1] [dev.to/jordihofc/criando-sistemas-de-reservas-consistentes-com-optimistic-locking-spring-boot-e-jpahibernate-2h8b](https://dev.to/jordihofc/criando-sistemas-de-reservas-consistentes-com-optimistic-locking-spring-boot-e-jpahibernate-2h8b)

[13;2][www.youtube.com/watch?v=vocYtV-9Bys](https://www.youtube.com/watch?v=vocYtV-9Bys)[9Bys](https://www.google.com/url?sa=t&source=web&rct=j&opi=89978449&url=https://www.youtube.com/watch%3Fv%3DvocYtV-9Bys)

[14][www.devmedia.com.br/trabalhando-com-threads-em-java/28780](https://www.devmedia.com.br/trabalhando-com-threads-em-java/28780)

[14.1][www.geeksforgeeks.org/java/countdownlatch-in-java](https://www.geeksforgeeks.org/java/countdownlatch-in-java/)

[15] [www.geeksforgeeks.org/java/runnable-interface-in-java](https://www.geeksforgeeks.org/java/runnable-interface-in-java/)

[16] [www.baeldung.com/jdbc-batch-processing](https://www.baeldung.com/jdbc-batch-processing)

[16.1] [www.geeksforgeeks.org/java/inserting-records-in-batch-using-jdbc](https://www.geeksforgeeks.org/java/inserting-records-in-batch-using-jdbc/)
