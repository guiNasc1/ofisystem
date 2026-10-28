---
name: OfiSystem
description: Gestão de oficina no balcão; cada registro carrega seu adesivo de próxima revisão.
colors:
  aco-forte: "#2B4A6F"
  aco-forte-hover: "#223D5C"
  aco-forte-escuro: "#3D6A9C"
  aco-forte-hover-escuro: "#4A79AD"
  aco-escuro: "#4A79AD"
  foco: "#4A79AD"
  foco-escuro: "#7FA6D3"
  trilho-topo: "#2B4A6F"
  trilho-base: "#1F3A5A"
  trilho-topo-escuro: "#1A2C40"
  trilho-base-escuro: "#121E2B"
  trilho-texto: "#DCE6F1"
  trilho-suave: "#A9BDD3"
  forja-claro: "#1B2B3D"
  forja-fundo: "#0B1219"
  chao: "#E9EDF2"
  chao-escuro: "#0F151C"
  vinil: "#FFFFFF"
  vinil-escuro: "#18212B"
  vinil-2: "#F4F6F9"
  vinil-2-escuro: "#1F2A36"
  borda: "#D3DAE2"
  borda-escuro: "#2A3643"
  tinta: "#16202B"
  tinta-escuro: "#E6ECF2"
  tinta-suave: "#4E5B68"
  tinta-suave-escuro: "#9DABB9"
  ambar-atencao: "#E8A33D"
  vermelho-critico: "#C8372D"
  vermelho-critico-escuro: "#E5605A"
  verde-faixa: "#23744A"
  verde-sucesso: "#2E8B57"
  verde-sucesso-escuro: "#4FB37E"
  faixa-inativo: "#6B7785"
  faixa-inativo-escuro: "#4A5868"
  info-fundo: "#E1EAF4"
  info-fundo-escuro: "#1C2D40"
  info-texto: "#1F3A5A"
  info-texto-escuro: "#A9C6E6"
  atencao-fundo: "#FDF1DC"
  atencao-fundo-escuro: "#3A2E17"
  atencao-texto: "#7A4E0B"
  atencao-texto-escuro: "#F2C27A"
  critico-fundo: "#FBE5E3"
  critico-fundo-escuro: "#3A1E1E"
  critico-texto: "#A52A21"
  critico-texto-escuro: "#F19A94"
  sucesso-fundo: "#E3F4EA"
  sucesso-fundo-escuro: "#18301F"
  sucesso-texto: "#1E6B3A"
  sucesso-texto-escuro: "#8FD6AE"
  inativo-fundo: "#EEF1F4"
  inativo-fundo-escuro: "#222C37"
typography:
  titulo:
    fontFamily: "Segoe UI, Inter, Noto Sans, Cantarell, Ubuntu, Liberation Sans, sans-serif"
    fontSize: "22px"
    fontWeight: 700
  texto:
    fontFamily: "Segoe UI, Inter, Noto Sans, Cantarell, Ubuntu, Liberation Sans, sans-serif"
    fontSize: "14px"
    fontWeight: 400
  texto-forte:
    fontFamily: "Segoe UI, Inter, Noto Sans, Cantarell, Ubuntu, Liberation Sans, sans-serif"
    fontSize: "14px"
    fontWeight: 700
  rotulo:
    fontFamily: "Segoe UI, Inter, Noto Sans, Cantarell, Ubuntu, Liberation Sans, sans-serif"
    fontSize: "12px"
    fontWeight: 700
  pequeno:
    fontFamily: "Segoe UI, Inter, Noto Sans, Cantarell, Ubuntu, Liberation Sans, sans-serif"
    fontSize: "12px"
    fontWeight: 400
rounded:
  etiqueta: "6px"
  controle: "8px"
  vinil: "12px"
spacing:
  xs: "4px"
  sm: "8px"
  md: "16px"
  lg: "24px"
  xl: "32px"
components:
  button-principal:
    backgroundColor: "{colors.aco-forte}"
    textColor: "{colors.vinil}"
    typography: "{typography.texto-forte}"
    rounded: "{rounded.controle}"
    padding: "0 16px"
    height: "40px"
  button-principal-hover:
    backgroundColor: "{colors.aco-forte-hover}"
  button-secundario:
    backgroundColor: "{colors.info-fundo}"
    textColor: "{colors.info-texto}"
    typography: "{typography.texto-forte}"
    rounded: "{rounded.controle}"
    padding: "0 16px"
    height: "40px"
  button-contorno:
    backgroundColor: "{colors.vinil}"
    textColor: "{colors.tinta}"
    typography: "{typography.texto-forte}"
    rounded: "{rounded.controle}"
    padding: "0 16px"
    height: "40px"
  button-contorno-hover:
    backgroundColor: "{colors.vinil-2}"
  button-discreto:
    backgroundColor: "transparent"
    textColor: "{colors.tinta}"
    typography: "{typography.texto-forte}"
    rounded: "{rounded.controle}"
    height: "40px"
  button-discreto-hover:
    backgroundColor: "{colors.vinil-2}"
  button-perigo:
    backgroundColor: "{colors.vinil}"
    textColor: "{colors.vermelho-critico}"
    typography: "{typography.texto-forte}"
    rounded: "{rounded.controle}"
    height: "40px"
  button-perigo-hover:
    backgroundColor: "{colors.critico-fundo}"
  campo:
    backgroundColor: "{colors.vinil}"
    textColor: "{colors.tinta}"
    typography: "{typography.texto}"
    rounded: "{rounded.controle}"
    height: "40px"
  etiqueta-status:
    backgroundColor: "{colors.info-fundo}"
    textColor: "{colors.info-texto}"
    typography: "{typography.rotulo}"
    rounded: "{rounded.etiqueta}"
    padding: "4px 10px"
  superficie-vinil:
    backgroundColor: "{colors.vinil}"
    textColor: "{colors.tinta}"
    rounded: "{rounded.vinil}"
    padding: "16px 24px"
  adesivo-faixa:
    backgroundColor: "{colors.aco-forte}"
    textColor: "{colors.vinil}"
    typography: "{typography.rotulo}"
    height: "32px"
  adesivo-corpo:
    backgroundColor: "{colors.vinil}"
    textColor: "{colors.tinta}"
    rounded: "{rounded.vinil}"
    padding: "12px 16px"
  trilho-item:
    textColor: "{colors.trilho-texto}"
    typography: "{typography.texto-forte}"
    rounded: "{rounded.controle}"
    height: "40px"
  trilho-item-ativo:
    backgroundColor: "{colors.vinil}"
    textColor: "{colors.info-texto}"
  trilho-subitem:
    textColor: "{colors.trilho-suave}"
    typography: "{typography.texto}"
    height: "36px"
---

# Design System: OfiSystem

## Overview

**Creative North Star: "O Adesivo de Revisão"**

O OfiSystem é a etiqueta de troca de óleo colada no para-brisa, transformada em interface: um retângulo de vinil branco com uma faixa azul-aço no topo, o estado que importa impresso em caixa alta na faixa e o valor que importa (data de entrega, total) grande no corpo. A janela é um chão de aço claro (grafite no tema escuro) sobre o qual esses vinis se apoiam com sombra macia; à esquerda, um trilho azul-aço em degradê carrega a marca e os módulos. O sistema é desktop Java 17 Swing + FlatLaf 3.4: os tokens vivem em `com.ofisystem.ui.style.Tema` (métodos de cor com par claro/escuro, constantes de tamanho e raio) e nos *global extra defaults* que `Tema.propriedades()` injeta no FlatLaf; não há CSS.

A densidade é de balcão: três tamanhos de texto, controles de 40px, linhas de tabela de 36px, grade de 8px. A hierarquia vem de peso e caixa, não de muitos tamanhos. A cor é semântica antes de decorativa: azul-aço é a voz da marca e da ação principal; âmbar, vermelho e verde só aparecem carregando um estado (`Tema.Tom`). Os dois temas são de primeira classe e alternáveis em tempo real com um fade de 320ms.

O momento memorável fica no login: a "Engrenagem forjada" monta a engrenagem a partir de placas recortadas do próprio `logo.png`, trava exatamente no logo e passa um único brilho de aço não cíclico. Fora dali, o movimento é curto e serve a estado (hover, foco, descolar do adesivo).

**Key Characteristics:**
- Vinil sobre chão de aço: superfícies brancas/grafite com borda de 1px e sombra macia empilhada.
- Faixa de status de 32px em todo adesivo, cor pelo `Tom` do registro.
- Uma família tipográfica, três tamanhos (12/14/22), dois pesos (400/700).
- Trilho azul-aço em degradê vertical com item ativo em pílula.
- Ícones desenhados em Java2D numa grade de 24 unidades, traço 1.9, pontas redondas.
- Tema claro e escuro com tokens pareados; nenhuma cor fixa fora de `Tema` para superfícies.

## Colors

Uma família azul-aço sobre neutros frios de aço e vinil, com três cores de estado reservadas e nunca decorativas.

### Primary
- **Azul-Aço de Faixa** (aco-forte; no escuro aco-forte-escuro): faixa de todo adesivo de tom NEUTRO/INFO, preenchimento do botão PRINCIPAL. No escuro sobe para um aço mais claro porque o aço original some sobre o vinil grafite. Hover escurece um degrau (aco-forte-hover / aco-forte-hover-escuro).
- **Aço de Acento** (aco-forte no claro; aco-escuro no escuro): `@accentColor` do FlatLaf, sublinhado da aba ativa, seleção de combo e lista, checkbox marcado, total do caixa no início.
- **Aço de Foco** (foco / foco-escuro): anel de foco de 2px de todos os controles, borda de campo focado, seleção de texto.

### Secondary
- **Trilho em Degradê** (trilho-topo → trilho-base; no escuro trilho-topo-escuro → trilho-base-escuro): fundo da navegação lateral, degradê vertical. Texto do trilho em trilho-texto; subitens, descrições e chevrons em trilho-suave.
- **Forja** (forja-claro → forja-fundo): palco escuro diagonal da abertura no login e da tela de boas-vindas, com grade fina de 4% de branco e halo de aço. Exclusivo dessas duas telas.

### Tertiary (cores de estado, `Tema.Tom`)
- **Âmbar de Atenção** (ambar-atencao): faixa do tom ATENCAO (aguardando peça). Único fundo de faixa com texto escuro (tinta), porque branco não tem contraste sobre âmbar.
- **Vermelho Crítico** (vermelho-critico; texto no escuro vermelho-critico-escuro): faixa do tom CRITICO (OS atrasada, estoque abaixo do mínimo), data atrasada no adesivo, texto de erro, botão PERIGO.
- **Verde de Faixa** (verde-faixa): faixa do tom SUCESSO. Diverge do contrato (verde-sucesso) por legibilidade: o texto branco de 12px na faixa precisa de 4.5:1, e verde-sucesso fica em ~4.25:1. verde-sucesso / verde-sucesso-escuro continuam como cor de texto de sucesso.
- **Pares de etiqueta** (info-, atencao-, critico-, sucesso-, inativo- fundo/texto): fundo tingido claro com texto escuro do mesmo matiz (inverso no escuro). Usados em etiquetas de status, seleção de tabela (par INFO) e botões SECUNDARIO.
- **Faixa Inativa** (faixa-inativo / faixa-inativo-escuro): faixa de registro cancelado, inativo ou ainda vazio.

### Neutral
- **Chão de Aço** (chao / chao-escuro): fundo da janela e dos painéis; tudo apoia nele.
- **Vinil** (vinil / vinil-escuro): superfícies, adesivos, campos, tabelas, popups.
- **Vinil Recuado** (vinil-2 / vinil-2-escuro): cabeçalho de tabela, campo desabilitado, hover de CONTORNO e DISCRETO, verso da dobra do adesivo.
- **Borda** (borda / borda-escuro): contorno de 1px de vinil e controles, linhas horizontais de tabela, separadores, thumb da rolagem.
- **Tinta** (tinta / tinta-escuro): texto principal; também fundo do tooltip (invertido).
- **Tinta Suave** (tinta-suave / tinta-suave-escuro): texto de apoio, rótulos, placeholders, cabeçalho de tabela, estado vazio.

### Named Rules
**The Cor Carrega Estado Rule.** Âmbar, vermelho e verde só entram via `Tema.Tom` (faixa, etiqueta, texto de estado). Nunca como acento, ícone decorativo ou fundo de seção.

**The Par Sempre Rule.** Toda cor de superfície ou texto é um método de `Tema` com par claro/escuro (`cor("#claro", "#escuro")`), lido na hora da pintura. Componente que guarda um `Color` fixo quebra a troca de tema.

**The Faixa Legível Rule.** Texto de faixa é branco, exceto sobre âmbar (tinta). Uma cor de faixa só entra se o texto de 12px negrito atingir 4.5:1; é por isso que a faixa verde é verde-faixa e a faixa escura de aço é aco-forte-escuro.

## Typography

**Display Font:** nenhuma separada; o título usa a mesma família.
**Body Font:** fonte sans instalada na máquina, resolvida por `Tema.familia()` na ordem Segoe UI → Inter → Noto Sans → Cantarell → Ubuntu → Liberation Sans → `SansSerif` lógico. Aplicada como `defaultFont` do FlatLaf.
**Label/Mono Font:** não há.

**Character:** uma única sans de sistema, trabalhada só por tamanho, peso e caixa. Decisão do usuário (28/10/2026): manter a fonte do sistema; Barlow não será empacotada.

### Hierarchy
- **Título** (700, 22px): título de tela, data/valor grande no adesivo ("Ontem", "30/10"), total do caixa e da venda, título da forja.
- **Texto** (400, 14px): corpo, campos, células de tabela, valores em linhas de resumo, texto de apoio (em tinta suave).
- **Texto forte** (700, 14px): botões, itens principais do trilho, nome do cliente e total no adesivo, documento nas listas.
- **Rótulo** (700, 12px, CAIXA ALTA, tinta suave): rótulo de campo, rótulo de dado sobre seu valor, título de bloco de vinil, cabeçalho de tabela, texto da faixa e das etiquetas.
- **Pequeno** (400, 12px): metadados (hora · cliente), dica da forja, contagem de usuários no login.

### Named Rules
**The Três Degraus Rule.** Só existem 12, 14 e 22. `AbstractPanel.criarLabel` arredonda qualquer tamanho pedido para o degrau mais próximo; não crie um quarto.

**The Rótulo Nomeia Dado Rule.** Caixa alta de 12px nomeia um campo, um valor ou um bloco ("Entrega prevista", "Caixa de hoje"). Nunca decora acima de um título como sobretítulo.

## Layout

Janela mínima 900×600 (login e principal). O principal é trilho fixo de 248px à esquerda + área de conteúdo rolável sobre o chão. Margem lateral de conteúdo 24px; título com 24px acima e 16px abaixo (`MARGEM_TOPO_TITULO`, `MARGEM_BASE_TITULO`). O ritmo é a grade de 8px: gaps de 4/8/16/24/32, controles e itens de trilho de 40px, subitens e linhas/cabeçalho de tabela de 36px, abas de 36px.

Reflow é por célula inteira (`GradeCelulas`): colunas = largura disponível ÷ (largura mínima + gap), todas as células de uma linha com a mesma altura. Adesivos da fila usam mínimo 264px e gap 16px; os blocos de resumo, 272px. Nunca se encolhe um adesivo abaixo do mínimo; ele desce de linha. O topo do início move as ações para baixo do título abaixo de 1000px.

Formulários vivem em uma superfície de vinil (16px vertical, 24px horizontal) com GridBag e insets de 4/8px; rótulos em cima ou à esquerda do campo. Rodapé de ações alinhado à direita com gap de 8px e linha de borda no topo. Números (valores, quantidades) alinham à direita em células e cabeçalhos; não há garantia de algarismos tabulares, porque a fonte do sistema não é forçada a `tnum`.

## Elevation & Depth

Híbrido: tonal (chão → vinil → vinil recuado) mais uma sombra macia empilhada pintada à mão por `Tema.sombra`. A sombra é sempre para baixo, nunca dura. Desfoque = min(11, 4 + elevação) camadas de `fillRoundRect` deslocadas 1px a cada passo, alfa caindo quadraticamente (peso² × 0.42 × alfa base). Alfa base: rgba(16,24,32,36/255) no claro, rgba(0,0,0,115/255) no escuro. Todo vinil reserva 12px (`FOLGA_SOMBRA`) abaixo de si para a sombra não ser cortada.

### Shadow Vocabulary
- **Vinil em repouso** (elevação 3): superfícies e adesivos parados.
- **Adesivo descolado** (elevação 3 → 12): hover/foco do adesivo; a sombra cresce enquanto o corpo sobe 3px.

### Named Rules
**The Sombra Macia Rule.** Profundidade é desfoque empilhado para baixo com alfa baixo. Sombra dura deslocada não pertence a este mundo.

**The Folga Reservada Rule.** Qualquer componente que pinte sombra soma `FOLGA_SOMBRA` (12px) ao inset inferior.

## Shapes

Cantos arredondados em três raios: 12px no vinil (superfícies, adesivos), 8px em controles (botão, campo, combo, item ativo do trilho; `Component.arc`, `Button.arc`, `TextComponent.arc`), 6px em etiquetas de status; checkbox 4px; thumb de rolagem totalmente arredondado. Bordas de 1px em borda; foco é anel de 2px em foco, sem anel interno. A única silhueta própria é a dobra do adesivo: o canto superior direito se descola em até 22px, mostrando o verso em degradê vinil-recuado → vinil.

## Components

### Buttons
Firmes e retangulares, sempre 40px de altura, rótulo em negrito 14px.
- **Shape:** cantos de controle (8px); largura mínima 96px; só-ícone vira quadrado 40×40.
- **PRINCIPAL:** aço de faixa com texto branco, padding 0 16px, gap ícone-texto 8px, ícone de 18px na cor do texto. Uma por área de ação.
- **SECUNDARIO:** par INFO (fundo tingido, texto aço escuro); hover mistura 18% de aço.
- **CONTORNO:** vinil com borda de 1px; hover vai para vinil recuado. Ações paralelas ("Venda de balcão", "Limpar").
- **DISCRETO:** sem fundo; hover pinta vinil recuado. Ações terciárias ("Criar conta", "Consultar vendas").
- **PERIGO:** vinil com texto vermelho e borda 50% vermelha; hover vai para critico-fundo.
- **Hover / Focus:** cor interpolada em 120ms (entrada) / 180ms (saída) com SAIDA_SUAVE; pressionado desce 1px; foco é anel de 2px em foco por dentro do botão.
- **Desabilitado:** vinil recuado com borda e texto tinta suave.
- ATENCAO e TRILHO existem no enum mas não têm uso no build; não são parte do vocabulário ativo.

### Chips (Etiqueta)
- **Style:** par fundo/texto do `Tom`, rótulo 12px negrito em caixa alta, padding 4px 10px, cantos de 6px, sem borda. NEUTRO é mostrado como INFO.
- **Em tabela:** a coluna nomeada por `colunaEtiqueta()` desenha a mesma etiqueta (22px de altura, recuo 8px) com o `Tom` da linha; linhas INATIVO ficam em tinta suave.
- **State:** etiquetas no "Precisa de atenção" são clicáveis (cursor de mão, abrem a lista filtrada).

### Cards / Containers (Superfície de vinil)
- **Corner Style:** 12px.
- **Background:** vinil, borda de 1px.
- **Shadow Strategy:** vinil em repouso (elevação 3), folga de 12px.
- **Internal Padding:** 16px × 24px por padrão; 16px × 16px nos blocos de resumo; tabela entra sem padding com 1px de recuo.

### Inputs / Fields
- **Style:** vinil, borda de 1px, cantos de 8px, 40px de altura; ícone de 18px à esquerda quando ajuda (usuário, cadeado); placeholder em tinta suave.
- **Focus:** borda e anel de 2px em foco.
- **Error / Disabled:** erro é texto vermelho 12px negrito abaixo do campo; desabilitado vai para vinil recuado.

### Tables
Linhas e cabeçalho de 36px, só linhas horizontais em borda, cabeçalho em vinil recuado com rótulo 12px caixa alta em tinta suave. Seleção usa o par INFO. Células com 12px de recuo lateral; números à direita. Tabela vazia pinta uma frase em tinta suave centralizada a um terço da altura.

### Navigation (Trilho)
- **Style:** 248px, degradê vertical do trilho; marca no topo (engrenagem de 40px, nome em branco negrito, oficina em trilho suave); rodapé com usuário, alternador de tema, trocar usuário e sair, separado por linha branca de 11% de alfa.
- **Itens:** principais e seções 40px negrito 14px com ícone de 18px em x=24 e texto em x=52; subitens 36px regulares em trilho suave.
- **Hover:** véu branco de até 8% em pílula de 8px com margem de 12px; texto clareia para branco (100ms entrada / 180ms saída).
- **Ativo:** pílula sólida: vinil com texto info-texto no claro, aco-forte com texto branco no escuro.
- **Seções:** acordeão; o chevron gira 90° em 200ms.
- **Foco:** anel de 2px em info-texto-escuro dentro da pílula.

### Adesivo de Revisão (assinatura)
Vinil de 12px com faixa de 32px no topo pintada pelo `Tom` do registro. A faixa leva o identificador à esquerda ("OS 000038") e o estado à direita ("ATRASADA"), ambos 12px negrito caixa alta, o esquerdo cortado com reticências. Corpo: rótulo de dado, valor de 22px negrito (vermelho quando crítico), nome em texto forte, metadado e total alinhado à direita. Padding: 32+12px no topo, 16px laterais. Em hover ou foco o adesivo descola (220ms entrando, 160ms saindo): sobe 3px, sombra vai de 3 a 12 e a dobra do canto abre até 22px. Enquanto um adesivo está em foco, os demais da fila apagam com um véu da cor do chão de até 55% em 160ms. Enter/Espaço abre. No login, o próprio cartão de entrada é um adesivo NEUTRO de faixa vazia que assenta (descolar 1 → 0 em 520ms) ao fim da forja.

### Engrenagem Forjada (abertura do login)
Palco de forja com placas recortadas do `logo.png` que se montam e travam no logo exato em 1700ms; nome e subtítulo entram com 12px de subida; um único brilho de aço passa na fase final e não se repete. Qualquer tecla ou clique pula. Exclusiva do login; não reutilize como loader.

### Ícones
Conjunto próprio desenhado em Java2D (`Icones`): grade de 24 unidades, traço 1.9 com pontas e junções redondas, cor fornecida por `Supplier<Color>` para seguir o tema. Tamanho padrão 18px em botões, campos e trilho; chevron de 16px com traço 2.2.

### Motion
Uma curva: SAIDA_SUAVE, cubic-bezier(0.16, 1, 0.3, 1), via `Movimento.animar` (resolução de 15ms; respeita `Animator.useAnimation()` e salta para o fim quando a animação está desligada). Durações: 100 a 220ms para hover/foco, 320ms para a troca de tema (fade de captura da janela), 520ms para o cartão assentar, 700ms para o total do caixa contar de zero, 1700ms para a forja.

## Do's and Don'ts

### Do:
- **Do** pegar toda cor de `Tema` na hora da pintura (`Tema.vinil()`, `Tema.texto(tom)`), para que a troca de tema repinte certo.
- **Do** expressar estado de registro por `Tema.Tom` e deixar faixa, etiqueta e texto derivarem dele.
- **Do** usar só 12/14/22 e pesos 400/700; rótulo é 12px negrito em caixa alta na tinta suave.
- **Do** manter controles em 40px, linhas de tabela em 36px e gaps na grade de 8px (4 só como meio-passo).
- **Do** reservar `FOLGA_SOMBRA` (12px) abaixo de qualquer vinil que pinte sombra.
- **Do** alinhar números à direita em tabelas e linhas de valor.
- **Do** usar `GradeCelulas` para grades de cartões, reflow por célula inteira com largura mínima.
- **Do** animar com `Movimento.animar` e SAIDA_SUAVE, entre 100 e 220ms para estado.

### Don't:
- **Don't** usar âmbar, vermelho ou verde como decoração ou acento de marca.
- **Don't** colocar texto branco em faixa sem 4.5:1; sobre âmbar o texto é tinta.
- **Don't** criar um quarto tamanho de texto nem um terceiro peso.
- **Don't** pintar sombra dura deslocada; a sombra deste mundo é macia e empilhada.
- **Don't** usar rótulo em caixa alta como sobretítulo decorativo acima de um título.
- **Don't** reaproveitar a Engrenagem Forjada fora do login nem fazer o brilho de aço ciclar.
- **Don't** empacotar fonte externa: a fonte do sistema é a escolha definitiva.
