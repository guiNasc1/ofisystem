# Registro de alterações e testes

Data da verificação: **28/10/2026**.

Este registro reúne as alterações feitas para alinhar o OfiSystem ao projeto de
faculdade, desde a correção do vínculo dos itens da OS até a revisão da documentação
e a rodada final de testes. O escopo é um protótipo acadêmico desktop, com base
compartilhada de demonstração.

## 1. Ordens de serviço e estoque

- `ItemOs`: o vínculo `ordem_servico_id` aponta explicitamente para a tabela
  `ordem_servico`, com a chave `fk_item_os_ordem_servico` nomeada no mapeamento.
- A migração V001 corrige a chave estrangeira antiga que apontava para `item_nota`.
  Preserva registros e outras chaves, pode ser repetida e valida uma chave correta
  já existente. Se houver itens sem OS correspondente, interrompe a transação.
- O salvamento valida o desconto: uma OS com itens deve ter total positivo. Um
  rascunho sem itens e sem desconto continua permitido, mas não pode ser concluído.
- A conclusão valida a forma de pagamento e recalcula os totais a partir dos itens
  persistidos dentro da transação, antes de gravar status, data e pagamento.
- A alteração genérica de status recusa status nulo e estados finais. Concluir e
  cancelar usam seus fluxos específicos.
- O botão passou a se chamar **Concluir e receber**. A janela informa o valor
  integral e exige marcar a confirmação do recebimento. Sem a marcação, a OS
  permanece aberta e é exibido um aviso.
- O cancelamento informa que todas as peças devem ter sido devolvidas antes de
  confirmar a reposição ao estoque.
- Os botões de consulta foram distribuídos em duas linhas para acomodar os novos
  textos.

Foram preservadas e verificadas as regras existentes: peças são baixadas ao salvar,
edições movimentam apenas a diferença, concluir não baixa novamente, cancelar uma
OS aberta devolve as peças uma única vez e OS encerradas não podem ser editadas.
**Concluir uma OS não cria uma venda de balcão.** O financeiro consulta OS concluídas
e vendas finalizadas de forma independente.

## 2. Cadastro, login e perfis

- O novo `UsuarioController` centraliza a validação do cadastro: usuário com 3 a 80
  caracteres, formato básico de e-mail, e-mail com até 254 caracteres e senha com
  pelo menos seis caracteres. Nome e e-mail recebem normalização de espaços nas
  pontas e letras minúsculas.
- `UsuarioDAO` impede repetição de nome ou e-mail, considerando maiúsculas e espaços
  nas pontas, e permite login com essa mesma normalização.
- A primeira conta de uma base vazia recebe ADMIN; as seguintes recebem USUARIO.
  Os perfis GERENTE e SUPORTE foram removidos do código do protótipo.
- `Usuario` passou a declarar nome e e-mail únicos e perfil obrigatório, com USUARIO
  como padrão. A migração V002 acrescenta índices que também impedem duplicidades
  sem diferenciar maiúsculas e espaços.
- A senha continua armazenada com o mecanismo existente. Uma tentativa de cadastro
  rejeitada restaura a senha do objeto e remove o identificador da tentativa, de
  modo que a conta possa ser corrigida e cadastrada sem dupla aplicação do hash.
- A tela de cadastro valida antes de consultar o banco, mostra o perfil atribuído
  e apresenta mensagens de regra de negócio, inclusive quando o processamento
  acontece em segundo plano.

## 3. Documento interno (removido)

- A funcionalidade foi removida por completo: botão e fluxos na tela de OS,
  `NotaFiscal`, `ItemNota`, `StatusNota`, `NotaFiscalDAO`, a impressão específica,
  os métodos do controller, o registro no `persistence.xml` e os testes relacionados.
- As tabelas `nota_fiscal` e `item_nota` não são mais mapeadas. Em bases já criadas
  elas continuam existindo, sem uso; podem ser descartadas manualmente com
  `DROP TABLE item_nota, nota_fiscal;`.

## 4. Migrações e correção da base de demonstração

| Arquivo | Comportamento |
| --- | --- |
| `V001__corrigir_fk_item_os.sql` | Corrige o vínculo dos itens de serviço com a OS, valida referências e preserva dados. |
| `V002__usuarios_escopo_academico.sql` | Normaliza cadastros, converte perfis legados/vazios para USUARIO, garante um ADMIN se ainda não existir e cria índices únicos. |

As migrações usam transação e limite de dez segundos para esperar bloqueios. A V002
interrompe antes de alterar dados se encontrar nome/e-mail vazio ou repetido.
O projeto não possui executor automático de migrações: os arquivos são aplicados
manualmente no SQL Editor do banco existente.

Durante a aplicação pelo usuário, a V002 detectou duas contas com o mesmo e-mail.
Foi preparada uma correção pontual para atribuir um endereço de teste de
`example.com` a uma das contas, mantendo ambas e suas senhas. Após a correção,
o usuário informou sucesso ao executar novamente a migração. Essa alteração de
cadastro pertence à base de demonstração; não é um dado distribuído pelo Git.

A rodada de testes descrita aqui não realizou gravações no Supabase. A aplicação
remota foi informada pelo usuário; as verificações automáticas das migrações foram
executadas em PostgreSQL local descartável.

## 5. Compilação e isolamento dos testes

- `pom.xml`: alvo de compilação Java 17 por `maven.compiler.release`; remoção da
  dependência duplicada do FlatLaf; classe principal do manifesto corrigida para
  `com.ofisystem.Main`; inclusão de JUnit Jupiter 5.10.2 e H2 2.2.224 apenas nos testes.
- `JPAutil`: permite fornecer propriedades JDBC e do Hibernate ao processo de teste.
  Todos os testes JPA configuram explicitamente H2 em memória e conferem a URL.
  A configuração padrão do aplicativo continua sendo a do `persistence.xml`.
- `scripts/testar-migracoes.ps1`: prepara um servidor PostgreSQL local de testes,
  executa as duas suítes SQL e encerra o servidor em `finally`. Não lê credenciais
  nem configuração de conexão do Supabase.
- `VerificacaoVisual`: programa auxiliar em `src/test/java` que prepara dados
  fictícios em H2 e abre os componentes reais de OS e financeiro para conferência.

## 6. Documentação revisada

- `OfiSystem_Documentacao_revisada.odt` e `.docx`: revisão do resumo, objetivos,
  escopo, regras, tecnologias, arquitetura, modelagem, conclusões e trabalhos futuros.
  O arquivo original em Downloads foi preservado.
- Os textos foram alinhados ao funcionamento existente: base única compartilhada,
  pagamento integral na conclusão e OS independente de balcão.
- A revisão contém 16 figuras, 16 tabelas, dicionário de 15 entidades e os diagramas
  de classes, relacionamentos e sequências correspondentes ao código.
- Os 16 diagramas possuem fontes PlantUML editáveis e versões PNG em `docs/modelagem`.
- A quebra da folha de aprovação foi ajustada para manter os autores junto da banca.
  O sumário e as listas de figuras/tabelas foram preenchidos com a paginação real.
- A seção de verificação foi atualizada para os 27 testes aprovados nesta rodada.
- `README.md` documenta execução, migrações, regras e testes; `PRODUCT.md` registra
  o escopo acadêmico e os dois perfis; `roteiro-demonstracao.md` orienta a apresentação.

## 7. Resultados dos testes

Ambiente: Windows, JDK 25.0.2 com alvo Java 17, Maven, H2 2.2.224 em memória e
PostgreSQL 18 local. As versões são as efetivamente utilizadas nesta verificação.

| Verificação | Resultado |
| --- | --- |
| `mvn -o -B verify` | Compilação, testes e empacotamento aprovados. |
| `FluxoAcademicoTest` | 13 testes de integração aprovados. |
| `UsuarioValidacaoTest` | 13 testes de validação aprovados. |
| `ImpressaoOsTest` | 1 teste real de geração Jasper/PDF aprovados. |
| Total JUnit | **27 testes, zero falhas, zero erros e zero ignorados.** |
| V001 em PostgreSQL | Falha antiga reproduzida; correção, repetição, preservação de outras chaves, validação e reversão por órfãos aprovadas. |
| V002 em PostgreSQL | Perfis, administrador existente, preservação de senhas, normalização, unicidade, repetição, rollback e correção de duplicidades aprovados. |
| Interface Swing | Conferidos botões, bloqueio sem confirmação, conclusão com confirmação e financeiro com origens separadas. |
| Impressão | Conferida visualmente a impressão da OS, incluindo total e desconto. |
| Documentação | ODT e DOCX com 40 páginas cada, mesmo texto por página, 16 figuras e 16 tabelas; índices e formatação conferidos. |
| Repositório | Diferenças verificadas com `git diff --check`; arquivos pessoais da IDE preservados fora do commit. |

Os testes de integração verificam também estoque insuficiente com reversão de toda
a gravação, aumento/redução/remoção de peças na edição, cancelamento único,
impossibilidade de editar/reconcluir estados finais, desconto integral recusado,
rascunho sem itens, forma de pagamento ausente, recálculo de total persistido
inconsistente e consulta de documento por USUARIO.

O cenário combinado dos relatórios resulta em **R$ 180,00**, sendo R$ 155,00 da OS
e R$ 25,00 do balcão, sem incluir OS aberta ou cancelada. Após cancelar a venda,
o relatório volta a R$ 155,00. A conferência pela interface utilizou outros dados
fictícios: OS de R$ 130,00 e balcão de R$ 25,00, totalizando R$ 155,00 em duas origens.

Os arquivos temporários de impressão, relatórios JUnit e logs ficam em `target/`,
fora do commit. Os códigos de teste e este registro permitem repetir a verificação.

## 8. Como repetir

```sh
mvn verify
```

```powershell
./scripts/testar-migracoes.ps1
```

Se a instalação do PostgreSQL for diferente, informar `-PostgresBin` e, se necessário,
`-Porta`. Também é possível executar cada arquivo de `src/test/sql` com `psql` em uma
base descartável, conforme o README.

Para conferir a interface, executar `com.ofisystem.VerificacaoVisual` pela IDE com
as dependências de testes. Esse programa não abre o login nem acessa a base remota.

## 9. Limites e arquivos preservados

A verificação cobre as alterações desta revisão e as integrações exercitadas.
Não representa teste exaustivo de todas as telas antigas, operação comercial,
impressora física, concorrência intensa ou teste de carga. A consulta ao ViaCEP e a
disponibilidade da conexão remota não foram exercitadas nesta rodada isolada.

Pagamentos parciais, contas a receber, isolamento entre oficinas, devoluções
parciais, integração fiscal, valores monetários com BigDecimal e numeração sob
concorrência permanecem como trabalhos futuros.

As alterações locais de `.idea/misc.xml` e `.idea/IntelliLang.xml` já existiam antes
desta revisão. Foram mantidas no computador e excluídas do commit do projeto.
