# Product

<!-- impeccable:product-schema 1 -->

## Platform

desktop

Aplicativo desktop Java Swing (FlatLaf 3.4), instalado no PC da oficina. Sem plano de migração para web. Fora dos valores `web`/`ios`/`android`/`adaptive` do schema: trabalho de interface segue convenções de app desktop (janela redimensionável, mouse e teclado, mínimo 900×600), não de página web.

## Users

Dono/mecânico de oficina pequena que opera tudo sozinho: atende o balcão, abre a ordem de serviço, executa o serviço, baixa peças e controla o caixa no mesmo computador. Usa o sistema entre um atendimento e outro, muitas vezes com o cliente na frente, mãos sujas e pouco tempo.

## Product Purpose

Substituir caderno e planilha na gestão da oficina mecânica: cadastro de clientes e veículos, ordens de serviço, estoque de peças, lançamentos financeiros e relatórios. Sucesso é o dono abrir e fechar uma OS rápido no balcão, saber o que tem de peça sem contar prateleira e saber quanto entrou, saiu e está a receber.

## Positioning

Produto para várias oficinas (modelo SaaS): cada usuário cadastra a própria oficina no primeiro acesso, e todas compartilham o mesmo banco. Atende oficina mecânica geral, qualquer tipo de veículo (carro, moto, utilitário).

## Operating Context

- Fluxo central: cliente chega → cadastro/busca de pessoa e veículo → OS aberta → serviços e peças lançados → status acompanhado (Aberta, Em andamento, Aguardando peça, Concluída, Cancelada) → OS impressa → lançamento financeiro.
- OS Concluída ou Cancelada não é mais editável.
- Peças usadas e movimentações (entrada/saída) alteram o estoque.
- Relatórios impressos/exportados via JasperReports: ordens de serviço, vendas, pessoas, estoque, financeiro.
- Endereço preenchido por CEP (ViaCEP); pessoa física ou jurídica (CPF/CNPJ).
- Login por usuário com perfil (Administrador, Gerente, Usuário, Suporte).

## Capabilities and Constraints

- Stack: Java 17, Swing + FlatLaf, Hibernate 6.4, PostgreSQL no Supabase, JasperReports 6.21, Gson.
- Banco é compartilhado e real: rodar o app ou testes de conexão altera dados de produção.
- Módulos: Pessoas, Veículos, Serviços, Ordens de Serviço, Estoque (peças, consulta, movimentação), Financeiro (lançamentos), Relatórios, Manuais (catálogo).
- Termos do domínio: "OS"/"Ordem de Serviço", "Peça", "Lançamento", "Movimentação", "Pessoa" (cliente ou fornecedor).
- Em aberto: o menu ainda diz "Motos" e a interface usa emoji de moto, herdados de quando o foco era moto; o produto atende qualquer veículo e a terminologia deve virar "Veículos".
- Em aberto: isolamento entre oficinas. Hoje a oficina é só um texto no usuário (`usuario_oficina`); não há tenant que separe dados de uma oficina das outras.

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
