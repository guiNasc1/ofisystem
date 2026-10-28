# Product

<!-- impeccable:product-schema 1 -->

## Platform

desktop

Aplicativo desktop Java Swing (FlatLaf 3.4), executado no computador de demonstração. Sem plano de migração para web. Fora dos valores `web`/`ios`/`android`/`adaptive` do schema: trabalho de interface segue convenções de app desktop (janela redimensionável, mouse e teclado, mínimo 900×600), não de página web.

## Users

Protótipo acadêmico apresentado pelos alunos à banca. O perfil de dono/mecânico de uma pequena oficina serve como referência para os cenários de demonstração, sem implantação ou uso operacional em uma oficina real.

## Product Purpose

Demonstrar cadastros, ordens de serviço, estoque, vendas de balcão e relatórios com regras delimitadas e coerência entre código e documentação. O roteiro central deve permitir abrir, editar, concluir e receber uma OS, observar as movimentações de estoque e consultar o financeiro.

## Positioning

Projeto Integrador de Sistemas de Informação. Utiliza uma base única de demonstração, compartilhada entre usuários. O nome de oficina personaliza a identificação visual e não representa isolamento entre empresas. SaaS, uso comercial e implantação operacional estão fora do escopo.

## Operating Context

- Fluxo central: pessoa cadastrada → OS aberta → serviços e peças lançados → salvamento com baixa de estoque → Concluir e receber com confirmação integral → documento interno opcional → recebimento exibido no financeiro.
- OS e venda de balcão são independentes; concluir OS não cria venda nem baixa peças novamente.
- A OS usa uma única forma de pagamento e recebimento integral na conclusão. Parcelamento, pagamentos parciais e contas a receber ficam para trabalhos futuros.
- Cancelar OS em aberto pressupõe devolução integral das peças. Devoluções parciais e reabertura após conclusão estão fora do escopo.
- Perfis funcionais: ADMIN e USUARIO. A primeira conta da base vazia recebe ADMIN e as seguintes USUARIO. A emissão de documento interno exige ADMIN; ambos acessam os demais fluxos e consultam documentos existentes.
- OS Concluída ou Cancelada não é mais editável.
- Peças usadas e movimentações (entrada/saída) alteram o estoque.
- Relatórios impressos/exportados via JasperReports: ordens de serviço, vendas, pessoas, estoque, financeiro.
- Endereço preenchido por CEP (ViaCEP); pessoa física ou jurídica (CPF/CNPJ).
- Login pelo nome de usuário, com os perfis Administrador e Usuário.

## Capabilities and Constraints

- Stack: alvo Java 17 (desenvolvimento com JDK 25), Swing + FlatLaf, Hibernate 6.4, PostgreSQL no Supabase, JasperReports 6.21, Gson. Testes com JUnit e H2 em memória, sem acesso ao Supabase.
- A configuração padrão conecta a uma base remota compartilhada no Supabase; não usar essa base para testes automatizados ou exemplos destrutivos. O protótipo não é destinado a produção.
- Módulos: Pessoas, Veículos, Serviços, Ordens de Serviço, Estoque (peças, consulta, movimentação), Financeiro (lançamentos), Relatórios, Manuais (catálogo).
- Termos do domínio: "OS"/"Ordem de Serviço", "Peça", "Lançamento", "Movimentação", "Pessoa" (cliente ou fornecedor).
- Em aberto: o menu ainda diz "Motos" e a interface usa emoji de moto, herdados de quando o foco era moto; o produto atende qualquer veículo e a terminologia deve virar "Veículos".
- Trabalhos futuros: isolamento entre oficinas, pagamentos parciais, reservas e devoluções parciais de peças, emissão fiscal eletrônica, valores monetários com BigDecimal e numeração concorrente.

## Brand Commitments

- Nome do produto: OfiSystem. O nome da oficina do usuário aparece no topo ("Oficina - <nome>").
- Interface e textos em português do Brasil.

## Evidence on Hand

- Assets: `src/main/resources/images/logo.png`, `logofundo.png`, `background.jpg`.
- Não há clientes, depoimentos, métricas de uso nem preço confirmados; nada disso deve ser inventado.

## Product Principles

1. Balcão primeiro: a tarefa mais frequente (abrir/atualizar OS) tem que caber em poucos cliques e não pedir dado que o sistema já tem.
2. Uma pessoa faz tudo: nenhuma tela pode depender de um especialista; o vocabulário é o da oficina, não o de ERP.
3. Estoque e caixa não mentem: toda peça usada e todo valor recebido passam por um caminho único que atualiza saldo e financeiro.
4. Genérico de veículo: nada na interface deve assumir carro ou moto.
