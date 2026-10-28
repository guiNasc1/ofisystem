# Roteiro de demonstração acadêmica

## Preparação

Usar dados de demonstração. Em uma base já existente, executar as migrações V001
e V002 antes de iniciar a versão revisada. Não duplicar o mesmo atendimento nos
módulos de OS e venda de balcão.

Em uma base vazia, criar duas contas: a primeira recebe ADMIN e a segunda USUARIO.
Em uma base antiga sem ADMIN, V002 atribui esse perfil à conta mais antiga.

## Apresentação

1. Entrar pelo nome de usuário. Mostrar a rejeição de nome ou e-mail repetido no cadastro.
2. Cadastrar um cliente e um serviço de **R$ 80,00**.
3. Cadastrar uma peça de **R$ 25,00** com saldo inicial de **10 unidades**.
4. Abrir uma OS com o serviço e duas peças. Salvar: total **R$ 130,00**, saldo **8**.
5. Alterar para três peças. Salvar: total **R$ 155,00**, saldo **7**.
6. Clicar em **Concluir e receber**, escolher PIX e confirmar recebimento integral.
   O saldo continua **7** e a consulta de vendas permanece sem uma nova venda dessa OS.
7. Consultar o financeiro e localizar a origem **OS**, com **R$ 155,00** recebidos.
8. Abrir outra OS com uma peça: saldo **6**. Cancelar confirmando a devolução integral:
   saldo **7**. Essa OS não entra nos recebimentos.
9. Criar uma venda de balcão independente com uma peça e PIX de **R$ 25,00**.
   O saldo passa a **6** e o financeiro mostra a origem **Balcão**. Total dos dois
   recebimentos de demonstração: **R$ 180,00**.
10. Mostrar os bloqueios de estoque insuficiente, desconto integral e conclusão
   sem itens. Confirmar que OS encerradas não podem ser editadas.

## Limites a explicar à banca

O protótipo usa uma base única compartilhada e conexão com o Supabase. A conclusão
da OS representa recebimento integral. Parcelamento, contas a receber, devoluções
parciais, isolamento entre oficinas e integração fiscal ficam como trabalhos futuros.

## Verificação automatizada

Executar `mvn test`. A suíte usa H2 em memória e não consulta o Supabase.
As migrações possuem scripts específicos em `src/test/sql` para PostgreSQL descartável.
