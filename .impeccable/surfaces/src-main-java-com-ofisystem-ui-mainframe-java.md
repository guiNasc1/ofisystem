---
version: 1
slug: "src-main-java-com-ofisystem-ui-mainframe-java"
primary_target: "src/main/java/com/ofisystem/ui/MainFrame.java"
related_targets: ["src/main/java/com/ofisystem/ui/LoginFrame.java","src/main/java/com/ofisystem/ui/panels"]
---

# OfiSystem — app desktop inteiro (login, shell, telas)

Modo: Operate. Público: dono/mecânico que faz tudo no balcão, com o cliente na frente. Tarefa central: abrir/acompanhar OS, vender no balcão, saber estoque e caixa. Restrições: Java 17 Swing + FlatLaf, janela mínima 900×600, tema claro e escuro alternável (pedido do usuário), logo da engrenagem e família azul-aço mantidos (pedido do usuário). Momento memorável: abertura "Engrenagem forjada" no login (escolhida pelo usuário).

## Direction contract

THESIS: Cada registro carrega seu adesivo de próxima revisão: o estado que importa impresso grande onde o cliente veria. Recusa o painel cinza com menu escuro e cartões iguais.

OWN-WORLD: Faixa azul-aço #2B4A6F como cabeçalho de todo adesivo e do trilho; chão aço claro #E9EDF2 / grafite #0F151C no escuro; vinil branco #FFFFFF / #18212B; tinta #16202B / #E6ECF2; âmbar #E8A33D só para atenção, vermelho #C8372D só para crítico, verde #2E8B57 só para concluído. Cantos 12, sombra com deslocamento, rótulos em caixa alta pequenos e valores grandes tabulares.

STORY: O dono entra pela engrenagem forjada, cai na fila do dia, enxerga de relance o que está atrasado ou esperando peça e age em um clique.

FIRST VIEWPORT: Trilho azul-aço à esquerda com módulos e alternador de tema; topo com saudação e ações (Nova OS, Venda); fila do dia como adesivos de OS; abaixo, últimas movimentações e caixa de hoje.

FORM: Adesivo de Revisão (etiqueta de troca de óleo do para-brisa), candidato 7 da minha lista, raises: hierarquia por peso/caixa com só 3 tamanhos (timetable), números tabulares à direita (j-card), grade de 8 px (Crouwel), foco levanta e escurece o resto (streaming), uma família tipográfica (Emigre). seed: seed:01e048a83d16067a

FINISH: unreviewed and undocumented is unfinished; this build ends with the finish review, the verdict, DESIGN.md, and every shipping raster carrying its provenance
