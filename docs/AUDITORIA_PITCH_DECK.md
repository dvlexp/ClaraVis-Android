# ClaraVis — Auditoria do Pitch Deck

> **Data:** 2026-03-10
> **Objetivo:** Correcoes de dados para tornar o pitch deck auditavel por investidores.
> **Referencia:** Todas as fontes em `REFERENCIAS.md`

---

## Slide 2 — "O Problema"

### CORRECAO NECESSARIA

| Elemento | Texto Atual | Problema | Texto Corrigido | Fonte |
|----------|-------------|---------|-----------------|-------|
| Numero principal | "Mais de 2 bilhoes de pessoas no mundo sofrem com deficiencia visual" | Subestimado e sem fonte | "2,2 bilhoes de pessoas no mundo possuem alguma forma de deficiencia visual" | WHO World Report on Vision, 2019 [REF-01] |

**Sugestao de texto revisado para o slide:**

> "2,2 bilhoes de pessoas no mundo possuem alguma forma de deficiencia visual (OMS, 2019). Dessas, 338 milhoes enfrentam deficiencia severa — e 80% vivem em paises em desenvolvimento onde solucoes acessiveis simplesmente nao existem. No Brasil, 7,9 milhoes de pessoas relatam dificuldade para enxergar mesmo com oculos (IBGE Censo 2022)."

**Nota:** O grafico/mapa do lado direito tem texto ilegivel/corrompido. Substituir por infografico limpo com os 3 numeros-chave: 2,2B global, 338M severo, 7,9M Brasil.

---

## Slide 6 — "Visao do Vestivel"

### CORRECOES NECESSARIAS

| Elemento | Texto Atual | Problema | Texto Corrigido | Fonte |
|----------|-------------|---------|-----------------|-------|
| Preco OrCam | "OrCam (US$ 4.500)" | Impreciso | "OrCam (~US$ 4.250)" | Revendedor autorizado [REF-13] |
| Preco eSight | "eSight (US$ 6.000+)" | Desatualizado — preco caiu | "eSight (US$ 4.950)" | eSight site oficial [REF-19] |

**Sugestao de texto revisado:**

> "O vestivel ClaraVis combina oculos inteligentes binoculares com cameras de profundidade e IA de borda. Preco-alvo de US$ 1.200-1.800 — 2-3x mais barato que OrCam MyEye (~US$ 4.250), Envision Glasses (US$ 1.899-3.499) e eSight Go (US$ 4.950). Vendas de hardware geram margens saudaveis mantendo acessibilidade via programas de saude publica."

---

## Slide 7 — "Modelo de Negocio & Oportunidade de Patrocinio"

### CORRECAO NECESSARIA

| Elemento | Texto Atual | Problema | Texto Corrigido | Fonte |
|----------|-------------|---------|-----------------|-------|
| Estatistica "64%" | "64% dos consumidores agem apos anuncios inclusivos" | Sem fonte citada; nao verificavel | Remover ou substituir por dado auditavel | — |

**Sugestao:** Substituir por: "O mercado de tecnologia assistiva para deficiencia visual vale US$ 5,4 bilhoes e cresce 12% ao ano (Research and Markets, 2024)." [REF-06]

---

## Slide 8 — "Mercado & Oportunidade de Receita"

### CORRECOES NECESSARIAS

| Elemento | Texto Atual | Problema | Texto Corrigido | Fonte |
|----------|-------------|---------|-----------------|-------|
| Numero de pessoas | "295 milhoes com deficiencia visual moderada a severa" | Dado parcial — nao inclui cegos | "338 milhoes com deficiencia visual severa (295M MSVI + 43,3M cegos)" | Lancet GBD 2021 [REF-03] |
| Tamanho do mercado | "mercado de tecnologia assistiva de US$ 7 bilhoes" | Nao encontrado em nenhuma fonte confiavel | "mercado de US$ 6,1 bilhoes (2024), projecao US$ 11,25B ate 2029" | Research and Markets / GlobeNewsWire [REF-06][REF-08] |
| CAGR | "cresce 7% ao ano" | Subestimado | "cresce ~13% ao ano (CAGR 2024-2029: 12,99%)" | [REF-08] |

**Nota:** O grafico do lado direito ("Assistive Technology Market") tem labels ilgiveis/corrompidos. Substituir por grafico limpo com dados de [REF-06] ou [REF-07].

**Sugestao de texto revisado:**

> "338 milhoes de pessoas com deficiencia visual severa representam um mercado massivo e mal atendido. O mercado de tecnologia assistiva para DV vale US$ 6,1 bilhoes (2024) e cresce ~13% ao ano, projetado para US$ 11,25 bilhoes ate 2029 e US$ 20,89 bilhoes ate 2034 (Research and Markets, mai. 2025). O ClaraVis captura valor atraves de vendas de hardware, assinaturas de app e reconhecimento contextual de marcas — transformando acessibilidade em oportunidade de negocio escalavel."

---

## Slide 4 — "Como Funciona"

### CORRECAO MENOR

| Elemento | Texto Atual | Problema | Texto Corrigido |
|----------|-------------|---------|-----------------|
| FPS | "deteccao YOLO a 10 FPS" | Verificar se v3 com 54 classes mantem 10 FPS no Helio G85 | Manter se confirmado em benchmark; caso contrario ajustar |

---

## Slide 5 — "App Funcional"

### CORRECAO NECESSARIA

| Elemento | Texto Atual | Problema | Texto Corrigido |
|----------|-------------|---------|-----------------|
| Linhas de codigo | "2600 linhas de codigo Kotlin" | Verificar se ainda e preciso com atualizacoes recentes | Atualizar contagem ou remover numero especifico |
| Estagio | "estagio pre-MVP" | Com treino v3 (54 classes, 71k imagens), ja e MVP | "estagio MVP" |

---

## Slides que precisam de ADICAO (recomendacoes para investidores)

### Slide novo recomendado: "Concorrencia"

Adicionar slide com tabela comparativa:

| | ClaraVis | Be My Eyes | OrCam | Envision | Seeing AI |
|---|---------|-----------|-------|----------|-----------|
| Preco | R$ 29,90/mes | Gratis | US$ 4.490 | US$ 1.899+ | Gratis |
| Display Visual | SIM | Nao | Nao | Parcial | Nao |
| Offline | SIM | Nao | Sim | Parcial | Parcial |
| Deteccao Obstaculos | SIM | Nao | Nao | Nao | Nao |

Fontes: [REF-11][REF-13][REF-15][REF-17]

### Slide novo recomendado: "Mercado Brasil"

- 7,9M brasileiros com dificuldade visual (Censo 2022) [REF-04]
- US$ 726M mercado assistivo Brasil (2023) [REF-10]
- Nenhum concorrente brasileiro identificado
- Lei 13.146/2015 garante direito a tecnologia assistiva [REF-50]

### Slide novo recomendado: "Ask" (Pedido ao investidor)

- R$ 1.500.000 pre-seed
- 10-15% equity
- 18 meses ate lancamento comercial
- Marcos claros com criterios de sucesso

---

## Resumo de Prioridade de Correcoes

| Prioridade | Slide | Correcao |
|-----------|-------|----------|
| **CRITICA** | 2 | Numero "2 bilhoes" → "2,2 bilhoes" com fonte OMS |
| **CRITICA** | 8 | "US$ 7 bilhoes" → "US$ 6,1 bilhoes" com fonte R&M/GlobeNewsWire |
| **CRITICA** | 8 | "7% ao ano" → "~13% ao ano" com fonte R&M |
| **ALTA** | 8 | "295 milhoes" → "338 milhoes" com breakdown |
| **ALTA** | 6 | Precos de concorrentes atualizados |
| **ALTA** | 7 | Remover dado "64%" sem fonte |
| **MEDIA** | 2, 8 | Substituir graficos com texto ilegivel |
| **MEDIA** | 5 | Atualizar estagio para "MVP" |
| **RECOMENDADO** | Novo | Adicionar slides de Concorrencia, Mercado Brasil, Ask |

---

*Documento de auditoria interna. Referencia cruzada com REFERENCIAS.md.*
