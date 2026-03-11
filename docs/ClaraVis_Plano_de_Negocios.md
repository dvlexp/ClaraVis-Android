# ClaraVis — Plano de Negocios

> **Versao:** 2.0 (auditada) | **Data:** 2026-03-10
> **Fundador:** Daniel Valladares
> **Contato:** daniel.valladares@eventosexponenciais.com.br
> **Nota:** Todos os dados de mercado foram auditados com fontes oficiais. Referencias cruzadas em `REFERENCIAS.md`.

---

## 1. Resumo Executivo

**ClaraVis** e uma plataforma de tecnologia assistiva que utiliza inteligencia artificial embarcada para auxiliar pessoas com deficiencia visual a navegar com seguranca e autonomia. O sistema detecta obstaculos, escadas, buracos, veiculos e dezenas de outros elementos do ambiente em tempo real, fornecendo alertas sonoros e visuais instantaneos — tudo 100% offline, sem depender de internet.

**Diferencial:** Enquanto a maioria das solucoes do mercado oferece apenas descricoes por audio (como o Be My Eyes [REF-11] e Seeing AI [REF-17]), o ClaraVis e projetado para fornecer **overlays visuais em tempo real** — contornos, labels e alertas diretamente no campo de visao do usuario atraves de oculos inteligentes com display AR. Isso atende nao apenas cegos, mas tambem os **7,9 milhoes de brasileiros com dificuldade para enxergar** [REF-04] que ainda possuem visao residual.

**Status atual:** App Android funcional com modelo YOLO fine-tuned (54 classes unificadas, 71k+ imagens de treino), app iOS pronto para build, modelo v3 em treinamento com 20 datasets especializados.

---

## 2. Problema e Oportunidade de Mercado

### 2.1 O Problema

- **2,2 bilhoes** de pessoas no mundo com alguma forma de deficiencia visual [REF-01]
- **338 milhoes** com deficiencia visual severa: 295 milhoes com MSVI + 43,3 milhoes cegos [REF-03]
- **7,9 milhoes** no Brasil com dificuldade para enxergar, mesmo com oculos (IBGE Censo 2022) [REF-04]
- Solucoes existentes focam em **audio apenas** — Be My Eyes, Seeing AI, Google Lookout, Aira
- Nenhuma solucao brasileira oferece **deteccao em tempo real offline com display visual**
- Oculos assistivos custam **US$ 1.899 a US$ 4.950** (Envision [REF-15], eSight [REF-19]) e nao sao acessiveis
- **OrCam** (US$ 4.250 [REF-13]) esta em crise financeira severa, com valuation em queda de US$ 1B para ~US$ 30-40M [REF-14]

### 2.2 A Oportunidade

| Metrica | Valor | Fonte |
|---------|-------|-------|
| TAM (Total Addressable Market) | US$ 6,1 bilhoes (mercado global de tech assistiva para DV, 2024) | [REF-06][REF-08] |
| TAM projetado 2029 | US$ 11,25 bilhoes | [REF-08] |
| TAM projetado 2034 | US$ 20,89 bilhoes | [REF-08] |
| SAM (Serviceable Available Market) | US$ 1,5 bilhoes (Americas + Europa, segmento software + wearables) | Estimativa baseada em [REF-06][REF-07] |
| SOM (Serviceable Obtainable Market) | US$ 15 milhoes (Brasil + LATAM, primeiros 3 anos) | Projecao interna |
| CAGR do setor | 12,99% ao ano (2024-2029); 13,1% (2024-2034) | [REF-06][REF-08] |
| Usuarios potenciais Brasil | 7,9 milhoes (dificuldade para enxergar) | [REF-04] |
| Mercado brasileiro de dispositivos assistivos | US$ 726M (2023), projecao US$ 1B ate 2030 | [REF-10] |

### 2.3 Tendencias Favoraveis

- NVIDIA Jetson e edge AI reduzindo custo de hardware embarcado
- Oculos inteligentes de consumo (Meta Ray-Ban, Rokid) barateando displays AR
- LLMs e VLMs permitindo interacao natural por voz
- Politicas publicas de inclusao (Lei 13.146/2015 [REF-50])
- FINEP, FAPESP e BNDES com linhas dedicadas a inovacao e acessibilidade
- **Colapso da OrCam** [REF-14] cria vacuo no mercado de hardware assistivo offline
- Crescimento de investimento de impacto (ESG) em acessibilidade — Enable Ventures liderou rodada do Be My Eyes [REF-11]

---

## 3. Analise Estrategica de Marketing (Framework Kotler)

> Baseado em KOTLER, Philip; KELLER, Kevin Lane; CHERNEV, Alexander. *Marketing Management*, 16. ed., 2021 [REF-30] e KOTLER, Philip et al. *Marketing 5.0: Technology for Humanity*, 2021 [REF-31].

### 3.1 Segmentacao, Targeting e Posicionamento (STP)

**Segmentacao** (Cap. 6, [REF-30]):

| Criterio | Segmentos Identificados |
|----------|------------------------|
| **Geografico** | Brasil (primario), LATAM (secundario), Global (longo prazo) |
| **Demografico** | Adultos com baixa visao (18-65); idosos com degeneracao macular (65+); criancas com DV em idade escolar |
| **Psicografico** | Buscadores de independencia (querem autonomia); Dependentes de cuidadores (familias); Profissionais ativos (necessitam mobilidade para trabalho) |
| **Comportamental** | Usuarios diarios de mobilidade urbana; usuarios situacionais (ambientes novos); usuarios institucionais (escolas, clinicas de reabilitacao) |

**Targeting:**

| Nivel | Segmento-Alvo | Tamanho | Justificativa |
|-------|---------------|---------|---------------|
| **Primario** | Adultos 18-65 com baixa visao que buscam independencia | ~5M no Brasil [REF-04][REF-05] | Maior parcela, poder de compra, valorizam autonomia |
| **Secundario** | Idosos 65+ com degeneracao macular | ~2M no Brasil | Crescente (envelhecimento pop.), alto willingness-to-pay |
| **Terciario** | Instituicoes (Fundacao Dorina, IBC, APAE, prefeituras) | ~500 instituicoes | Contratos B2B de maior valor, canal de distribuicao |

**Posicionamento** (Cap. 7, [REF-30]):

> *"A unica plataforma assistiva brasileira que combina deteccao de obstaculos por IA em tempo real com overlays visuais AR — 100% offline, acessivel, e criada por quem vive o problema."*

**Pontos de Diferenca (PoD):**
1. Display visual AR (vs. audio-only de todos os concorrentes diretos de software)
2. 100% offline (vs. dependencia de internet do Be My Eyes, Seeing AI, Aira)
3. Preco acessivel — a partir de R$ 29,90/mes (vs. US$ 1.899-6.950 dos concorrentes de hardware)
4. Modelo treinado especificamente para obstaculos urbanos brasileiros (54 classes)

**Pontos de Paridade (PoP):**
- Deteccao de objetos por IA (equivalente a Seeing AI, Lookout)
- TTS em portugues (equivalente a apps traduzidos)
- Multiplataforma Android/iOS

### 3.2 Os 5 Niveis de Produto (Cap. 8, [REF-30])

| Nivel | Definicao (Kotler) | Aplicacao ClaraVis |
|-------|--------------------|--------------------|
| **Beneficio Central** | Necessidade fundamental | Mobilidade segura e autonoma para pessoas com deficiencia visual |
| **Produto Basico** | Forma minima viavel | Camera + deteccao de objetos por IA + saida por audio em smartphone |
| **Produto Esperado** | O que clientes normalmente esperam | Deteccao em tempo real, acuracia razoavel, operacao offline, suporte em portugues |
| **Produto Ampliado** | Supera expectativas | Overlays visuais AR (contornos, labels, alertas, texto magnificado), modelo fine-tuned para ambiente urbano brasileiro, thresholds configuraveis |
| **Produto Potencial** | Evolucao futura | Wearable Jetson, VLM para compreensao de cenas, navegacao GPS integrada, modelos treinados pela comunidade, saida em Braille |

> Kotler: "A concorrencia ocorre mais no nivel ampliado do que no nivel central." O overlay visual AR e exatamente um diferenciador de nivel ampliado vs. concorrentes audio-only.

### 3.3 Marketing Mix — 4Ps (Caps. 8, 11, 13, 15, [REF-30])

| P | Estrategia ClaraVis |
|---|---------------------|
| **Produto** | Sistema integrado: app mobile (Android/iOS) + modelo IA proprietario + futuro kit hardware (Jetson + oculos AR). Modular — usuario escolhe o nivel de investimento |
| **Preco** | Value-based pricing [REF-30, Cap. 11]. Ancora de referencia: custo de um cao-guia (~R$ 100K lifetime) ou assistente humano (~R$ 2.000/mes). App premium R$ 29,90/mes e 99% mais barato que alternativas de hardware. Kit completo R$ 5.000-7.000 vs. OrCam US$ 4.250 [REF-13] ou Envision US$ 3.499 [REF-15] |
| **Praca** | D2C online (app stores + site); parcerias com organizacoes de DV (Fundacao Dorina Nowill, IBC, ONCE); indicacao por oftalmologistas e clinicas de reabilitacao; licitacoes governamentais (SUS, INSS) |
| **Promocao** | Advocacy-driven: historia do fundador como narrativa central; demonstracoes em eventos de acessibilidade (Reatech, Campus Party); parcerias com influenciadores da comunidade DV; marketing de conteudo (YouTube, Instagram com audiodescricao) |

### 3.4 Ciclo de Adocao Tecnologica (Rogers [REF-32], Moore [REF-33])

| Grupo | % | Perfil ClaraVis | Estrategia |
|-------|---|-----------------|------------|
| **Inovadores** | 2,5% | Tech enthusiasts com DV, beta testers em eventos | Acesso antecipado gratuito, feedback loops |
| **Early Adopters** | 13,5% | Lideres de opiniao em organizacoes de DV (NFB, Fundacao Dorina), profissionais de reabilitacao | Parcerias institucionais, demos personalizadas |
| **Early Majority** | 34% | Populacao mais ampla com DV, apos validacao institucional | Cobertura SUS, indicacao medica, marketing social |
| **Late Majority** | 34% | Usuarios avessos a risco, geralmente idosos | Opcoes subsidiadas por governo/seguro, treinamento presencial |
| **Laggards** | 16% | Podem nunca adotar tech assistiva digital | Nao e target; foco em aumentar acessibilidade geral |

> **O Abismo (Crossing the Chasm)** [REF-33]: A transicao critica para ClaraVis e de early adopters para early majority, o que requer endosso institucional (Fundacao Dorina, IBC), cobertura por planos de saude/SUS, e validacao clinica publicada.

---

## 4. Analise SWOT

### Forcas (Strengths)

| Forca | Detalhamento |
|-------|-------------|
| **Fundador com DV** | Daniel vive com baixa visao — "skin in the game", credibilidade unica perante investidores, usuarios e midia. So dotLumen tem conexao pessoal similar [REF-20] |
| **100% offline** | Funciona sem internet, critico para paises em desenvolvimento, areas rurais, e privacidade. Apenas OrCam [REF-13] (em crise) e eSight [REF-19] sao totalmente offline entre concorrentes |
| **Overlay visual AR** | Unico posicionamento: nao apenas audio, mas realce visual do ambiente. Nenhum concorrente de software oferece isso; Envision [REF-15] tem display limitado |
| **Custo acessivel** | Roda em smartphone de R$ 1.000; target hardware R$ 5.000-7.000 vs. OrCam US$ 4.250, Envision US$ 3.499, eSight US$ 4.950 |
| **Modelo fine-tuned proprietario** | v3 com 71k+ imagens, 54 classes especificas para obstaculos urbanos brasileiros (calcadas, bueiros, buracos). Nenhum concorrente tem modelo equivalente |
| **Multiplataforma** | Android deployed, iOS projetado, Jetson planejado — cobertura ampla |

### Fraquezas (Weaknesses)

| Fraqueza | Mitigacao Planejada |
|----------|-------------------|
| **Fundador solo** | Buscar co-founder tecnico ou operacional; investidores priorizam times de 2-3. Contratacoes prioritarias na Fase 1 |
| **Pre-receita** | Nao ha clientes pagantes ainda; validacao de willingness-to-pay pendente. Testes beta com NPS medirao aceitacao |
| **Integracao AR nao concluida** | Prototipo Jetson + oculos e marco M2 (2026 Q3); atualmente app-only em smartphone |
| **Dados de treino limitados para algumas classes** | Classes como flor, chinelo, folhas tem poucos exemplos. Treino v3 com datasets expandidos mitiga |
| **Sem validacao clinica** | Nao ha estudos publicados ou aprovacao ANVISA. Parceria academica planejada para Beta |
| **Capital restrito** | Hardware (Jetson, oculos AR) requer investimento significativo upfront |

### Oportunidades (Opportunities)

| Oportunidade | Evidencia |
|-------------|-----------|
| **Mercado em crescimento acelerado** | CAGR 12% para tech assistiva visual [REF-06]; projecao US$ 11,2B ate 2030 |
| **Colapso da OrCam** | De US$ 1B para ~US$ 30-40M, fechou divisao de oculos [REF-14] — vacuo no mercado |
| **Envelhecimento populacional** | Populacao 60+ do Brasil projetada para 30% ate 2050 (IBGE); degeneracao macular lidera perda visual em idosos |
| **Programas governamentais** | Lei 13.146/2015 [REF-50]; INSS beneficios assistivos; SUS Programa de Concessao de Orteses e Proteses |
| **Avancos em edge AI** | Modelos menores, NPUs em smartphones, Jetson mais barato — reduz custo de hardware progressivamente |
| **Nenhum concorrente brasileiro** | Nenhuma startup brasileira focada em IA assistiva para DV foi identificada na pesquisa. ClaraVis seria first-mover em Brasil/LATAM |
| **Investimento de impacto crescente** | ESG/impact funds buscando startups de acessibilidade — Enable Ventures liderou rodada BME [REF-11], 4impact financiou Envision [REF-16] |
| **80% dos DV em paises em desenvolvimento** | Solucao offline + baixo custo e ideal para esses mercados (WHO) [REF-01] |

### Ameacas (Threats)

| Ameaca | Probabilidade | Impacto | Mitigacao |
|--------|--------------|---------|-----------|
| **Big tech agrega funcoes assistivas** (Apple Vision Pro, Google Lookout [REF-24], Meta smart glasses) | Alta | Alto | Especializacao profunda em obstaculos/navegacao; modelo fine-tuned > modelo generico; offline; foco LATAM |
| **IA multimodal commoditiza deteccao** (GPT-4V, Gemini em smartphones) | Alta | Medio | Overlays visuais sao diferenciador que LLMs nao oferecem; latencia offline < cloud; especializacao urbana |
| **Barreiras regulatorias** (ANVISA classificacao como dispositivo medico) | Baixa | Medio | Consultoria juridica preventiva; processamento 100% local (LGPD compliance nativa) |
| **Resistencia a adocao** (estigma de wearables visiveis, literacia digital) | Media | Medio | Design discreto; treinamento presencial; parcerias com reabilitacao |
| **Escassez de funding no Brasil** | Media | Alto | VC brasileiro contraiu em 2023-2024 [REF-42]; mitigacao via grants (FAPESP, FINEP), crowdfunding, investidores anjo |
| **Supply chain de hardware** (Jetson, oculos AR) | Media | Medio | Design modular; suporte a multiplos modelos de oculos; estoque estrategico |

---

## 5. Analise Competitiva Detalhada

### 5.1 Mapeamento de Concorrentes

#### Concorrentes Diretos

| Solucao | Pais | Ano | Funding Total | Preco | Display Visual | Offline | Deteccao Tempo Real | Usuarios |
|---------|------|-----|--------------|-------|----------------|---------|--------------------|---------|
| **Be My Eyes** [REF-11] | DK | 2012 | ~US$ 17,4M | Gratis | Nao | Nao | Nao (foto/video call) | 900K DV + 9M voluntarios |
| **OrCam MyEye** [REF-13] | IL | 2010 | ~US$ 130M+ | US$ 4.250 | Nao | Sim | Parcial (OCR, faces) | N/D |
| **Envision AI** [REF-15] | NL | 2017 | ~EUR 1,75M | US$ 1.899-3.499 | Sim (prisma Google Glass) | Parcial | Parcial | N/D |
| **Seeing AI** [REF-17] | EUA | 2017 | Microsoft interno | Gratis | Nao | Parcial | Nao | 20M+ tarefas |
| **Aira** [REF-18] | EUA | 2015 | ~US$ 17,7M | US$ 26-1.160/mes | Nao | Nao | Sim (via agente humano) | N/D |
| **eSight** [REF-19] | CA | 2006 | ~US$ 15M | US$ 4.950 | Sim (OLED) | Sim | Nao (magnificacao optica) | N/D |
| **dotLumen** [REF-20] | RO | 2020 | ~EUR 10M+ | TBD (pre-comercial) | Nao (haptico) | Sim | Sim (navegacao haptica) | Pre-comercial |

#### Concorrentes Indiretos

| Solucao | Pais | Preco | Tipo | Relevancia |
|---------|------|-------|------|-----------|
| **Google Lookout** [REF-24] | EUA | Gratis | App Android, descricoes com Gemini | Media — audio-only, cloud |
| **Sullivan+** [REF-27] | KR | Gratis | App, descricao de imagem + OCR | Baixa — nao detecta obstaculos |
| **Lazarillo** [REF-21] | CL | Gratis | App GPS acessivel | Baixa — navegacao, nao deteccao |
| **WeWalk** [REF-22] | TR/UK | US$ 849-1.149 | Bengala inteligente | Media — haptico, complementar |
| **Glidance** [REF-23] | EUA | US$ 1.499 + US$ 30/mes | Robo guia autonomo (beta, entrega 2027) | Media — abordagem diferente |
| **IrisVision** [REF-25] | EUA | US$ 2.950-3.995 | VR headset magnificacao | Baixa — so magnificacao |
| **NuEyes** [REF-26] | EUA | ~US$ 5.995 | Oculos AR magnificacao | Baixa — so magnificacao |

### 5.2 Matriz Comparativa de Features

| Feature | ClaraVis | Be My Eyes | OrCam | Envision | Seeing AI | Lookout | dotLumen | WeWalk |
|---------|----------|-----------|-------|----------|-----------|---------|----------|--------|
| **Overlay visual AR** | **SIM** | Nao | Nao | Parcial | Nao | Nao | Nao | Nao |
| **100% offline** | **SIM** | Nao | Sim | Parcial | Parcial | Parcial | Sim | Parcial |
| **Deteccao tempo real** | **SIM** | Nao | Parcial | Parcial | Nao | Parcial | Sim | N/A |
| **Foco em obstaculos/perigos** | **SIM** | Nao | Nao | Nao | Nao | Nao | Sim | Parcial |
| **Preco acessivel (<US$100/ano)** | **SIM** | Sim (gratis) | Nao | Nao | Sim (gratis) | Sim (gratis) | TBD | Nao |
| **Wearable AR target** | **SIM** | Nao | Clip-on | Sim | Nao | Nao | Sim | N/A |
| **Modelo proprio fine-tuned** | **SIM** | Nao (GPT-4) | Sim | Parcial | Nao | Nao (Gemini) | Sim | N/A |
| **Disponivel Brasil (PT-BR)** | **SIM** | Sim | Sim | Parcial | Sim | Sim | Nao | Nao |

### 5.3 Analise de Precos vs. Valor (Kotler Value-Based Pricing, [REF-30] Cap. 11)

| Alternativa de Referencia | Custo | ClaraVis e... |
|--------------------------|-------|---------------|
| Cao-guia (lifetime) | ~R$ 100.000+ | 99,97% mais barato (app) |
| Assistente humano (mensal) | ~R$ 2.000/mes | 98,5% mais barato (app R$ 29,90/mes) |
| OrCam MyEye 3 Pro [REF-13] | US$ 4.250 (~R$ 23.000) | 70-78% mais barato (kit R$ 5.000-7.000) |
| Envision Professional [REF-15] | US$ 3.499 (~R$ 19.000) | 63-74% mais barato (kit R$ 5.000-7.000) |
| eSight Go [REF-19] | US$ 4.950 (~R$ 27.000) | 74-81% mais barato (kit R$ 5.000-7.000) |
| Aira Gold 2-stars (anual) [REF-18] | US$ 2.400/ano (~R$ 13.000) | 98% mais barato (app R$ 249/ano) |

### 5.4 Status Financeiro dos Concorrentes (Inteligencia Competitiva)

| Empresa | Situacao Financeira (Mar 2026) | Implicacao para ClaraVis |
|---------|-------------------------------|--------------------------|
| **OrCam** | Em crise severa [REF-14]: de US$ 1B para ~US$ 30-40M; fechou divisao de oculos; multiplas demissoes | Vacuo no mercado de hardware offline; valida que hardware caro + single-purpose e vulneravel |
| **Be My Eyes** | Saudavel — Series A+ de US$ 6,1M em jan. 2025 [REF-11]; parceria OpenAI | Valida o mercado; posicao complementar (cloud) vs. ClaraVis (edge) |
| **Envision** | Estavel — ~EUR 1,75M total; equipe enxuta [REF-16] | Concorrente mais proximo; ClaraVis compete por ser offline e mais barato |
| **dotLumen** | Crescendo — EUR 10M+ total; EUR 5M em jan. 2025 [REF-20] | Abordagem diferente (haptico); potencial parceria ou convivencia no mercado |
| **Aira** | Estavel — ~US$ 17,7M total [REF-18] | Modelo humano nao escala; ClaraVis e automatizado |
| **eSight** | Adquirido pela Gentex (jan. 2024) [REF-19] | Validacao de exit; nicho de magnificacao, nao compete diretamente em deteccao |
| **Soundscape (Microsoft)** | Descontinuado em jun. 2023 | Big tech pode abandonar projetos de acessibilidade; independencia e vantagem |

---

## 6. Produto e Tecnologia

### 6.1 Plataforma ClaraVis

| Componente | Descricao | Status |
|------------|-----------|--------|
| **App Android** | Kotlin, YOLO TFLite, OCR, TTS pt-BR, 100% offline | Funcional |
| **App iOS** | Swift nativo, mesma arquitetura | Codigo pronto |
| **Modelo IA v3** | YOLOv8s fine-tuned, 54 classes unificadas, 416x416, float16, 71k+ imagens | Em treinamento |
| **Edge AI (Jetson)** | NVIDIA Jetson Orin NX 16GB, VLM + YOLO + depth | Em planejamento |
| **Oculos AR** | Integracao com Rokid/RayNeo/Mentra para display visual | Prototipo planejado |

### 6.2 Diferenciais Tecnicos

1. **100% offline** — funciona sem internet, privacidade total (LGPD compliance nativa)
2. **Overlay visual AR** — contornos coloridos por categoria no campo de visao
3. **Modelo treinado com 71k+ imagens** de 20 datasets especificos para navegacao
4. **54 classes unificadas** — obstaculos urbanos brasileiros (bueiros, calcadas irregulares, buracos, escadas, poste, etc.)
5. **TTS inteligente** — prioriza perigos, anuncia em 0.8s, velocidade 2x
6. **Configuravel pelo usuario** — thresholds, velocidade, sensibilidade ajustaveis
7. **Multiplataforma** — Android, iOS, e futuro Jetson embarcado

### 6.3 Roadmap de Produto

| Fase | Periodo | Entregas |
|------|---------|----------|
| **MVP** (atual) | 2026 Q1-Q2 | App Android/iOS, modelo v3 (54 classes), testes com usuarios |
| **Alpha** | 2026 Q3-Q4 | Prototipo Jetson + oculos AR, VLM reativado, sensor depth |
| **Beta** | 2027 Q1-Q2 | Testes com 50 usuarios, iteracao UX, parcerias institucionais |
| **Launch** | 2027 Q3 | Lancamento comercial Brasil (app + kit hardware) |
| **Expansao** | 2028 | LATAM, EUA, Europa. Oculos ClaraVis custom |

---

## 7. Modelo de Negocios

### 7.1 Fontes de Receita

| Modelo | Descricao | Preco Estimado |
|--------|-----------|----------------|
| **App Freemium** | App gratuito com deteccao basica (10 classes). Premium desbloqueia todas as classes, VLM, OCR avancado | R$ 29,90/mes ou R$ 249/ano |
| **Kit Hardware** | Jetson + camera + fone bone conduction (para quem nao tem smartphone potente) | R$ 2.500-3.500 |
| **Kit Premium** | Kit hardware + oculos AR com display | R$ 5.000-7.000 |
| **B2B/Institucional** | Licenca para instituicoes (Fundacao Dorina, IBC, prefeituras) — volume | R$ 15.000-50.000/ano |
| **SUS/Governo** | Inclusao na tabela SUS como dispositivo assistivo (longo prazo) | Reembolso por unidade |

### 7.2 Projecao de Receita

| Ano | Usuarios App | Usuarios Premium | Hardware | B2B | Receita Total |
|-----|-------------|-----------------|----------|-----|---------------|
| 2027 (lancamento) | 5.000 | 500 | 50 | 3 | R$ 478.000 |
| 2028 | 25.000 | 3.000 | 300 | 10 | R$ 2.840.000 |
| 2029 | 80.000 | 12.000 | 1.000 | 30 | R$ 10.580.000 |
| 2030 | 200.000 | 35.000 | 3.000 | 80 | R$ 28.400.000 |

**Premissas:**
- Conversao free→premium: 10% (padrao SaaS acessibilidade)
- Ticket medio premium: R$ 249/ano
- Ticket medio hardware: R$ 3.000
- Ticket medio B2B: R$ 30.000/ano
- Churn premium: 15%/ano (abaixo do mercado — produto essencial)

---

## 8. Equipe Necessaria

### 8.1 Fase 1 — MVP/Alpha (2026) — 3 pessoas

| Cargo | Perfil | Salario/Mes | Tipo |
|-------|--------|-------------|------|
| **CEO/CTO** (Daniel) | Fundador. Dev, IA, visao computacional | Pro-labore R$ 5.000 | Full-time |
| **Dev Mobile Sr** | Android/iOS, Kotlin/Swift, TFLite | R$ 12.000 | Full-time |
| **UX/Acessibilidade** | Design inclusivo, testes com usuarios, WCAG | R$ 8.000 | Part-time -> Full |

**Custo mensal equipe Fase 1: R$ 25.000**

### 8.2 Fase 2 — Beta (2027 Q1-Q2) — 6 pessoas

| Cargo | Perfil | Salario/Mes |
|-------|--------|-------------|
| CEO/CTO (Daniel) | Lideranca + dev | R$ 8.000 |
| Dev Mobile Sr | Android/iOS | R$ 12.000 |
| Dev Embedded/Jetson | C++/Python, JetPack, edge AI | R$ 14.000 |
| ML Engineer | Treinamento de modelos, datasets, MLOps | R$ 14.000 |
| UX/Acessibilidade | Design inclusivo + QA | R$ 10.000 |
| Comercial/Parcerias | Vendas B2B, relacoes institucionais | R$ 8.000 |

**Custo mensal equipe Fase 2: R$ 66.000**

### 8.3 Fase 3 — Lancamento (2027 Q3+) — 10 pessoas

Adicionar:
- Dev Backend (infra, analytics, OTA updates): R$ 12.000
- Hardware Engineer (design do oculos custom): R$ 15.000
- Community Manager (suporte, redes, comunidade): R$ 6.000
- Estagiario de IA/dados: R$ 2.500

**Custo mensal equipe Fase 3: R$ 101.500**

---

## 9. Orcamento Detalhado — 18 Meses (2026 Q2 - 2027 Q3)

### 9.1 Hardware e Infraestrutura

| Item | Custo | Nota |
|------|-------|------|
| NVIDIA Jetson Orin NX 16GB (reComputer) | R$ 5.500 | Prototipo principal |
| NVMe 1TB + camera IMX477 + acessorios | R$ 800 | Desenvolvimento |
| Intel RealSense D435i (sensor depth) | R$ 1.700 | Prototipo avancado |
| Sensores (ultrasonico, ToF, mic array) | R$ 500 | Testes |
| Oculos AR (Rokid + RayNeo Air 4 Pro) | R$ 3.500 | 2 pares para prototipo |
| Fone bone conduction (Shokz) | R$ 500 | Audio assistivo |
| Smartphones de teste (2 unidades) | R$ 3.000 | Android + iPhone |
| Servidor/GPU cloud (treinamento modelos) | R$ 6.000 | 18 meses Colab Pro+ ou RunPod |
| Impressora 3D (prototipagem mounts) | R$ 2.500 | Ender 3 V3 ou similar |
| Componentes eletronicos diversos | R$ 1.000 | Cabos, baterias, PCBs |
| **Subtotal Hardware** | **R$ 25.000** | |

### 9.2 Equipe (18 meses)

| Periodo | Meses | Custo/Mes | Subtotal |
|---------|-------|-----------|----------|
| Fase 1 (MVP/Alpha) | 6 | R$ 25.000 | R$ 150.000 |
| Fase 2 (Beta) | 6 | R$ 66.000 | R$ 396.000 |
| Fase 3 (Lancamento) | 6 | R$ 101.500 | R$ 609.000 |
| **Subtotal Equipe** | **18** | | **R$ 1.155.000** |

### 9.3 Operacional e Servicos

| Item | Custo Total (18 meses) | Nota |
|------|----------------------|------|
| Espaco coworking/escritorio | R$ 36.000 | R$ 2.000/mes |
| Juridico (empresa, patentes, contratos) | R$ 25.000 | Constituicao + PI |
| Contabilidade | R$ 13.500 | R$ 750/mes |
| Dominio, hospedagem, servicos cloud | R$ 5.400 | R$ 300/mes |
| Marketing e branding | R$ 30.000 | Site, materiais, eventos |
| Viagens (feiras, testes, parcerias) | R$ 20.000 | NVIDIA GTC, Reatech, etc. |
| Certificacoes e regulatorio | R$ 15.000 | ANVISA (se aplicavel), INMETRO |
| Seguro e contingencia | R$ 10.000 | |
| **Subtotal Operacional** | **R$ 154.900** | |

### 9.4 Resumo Orcamentario

| Categoria | Valor | % do Total |
|-----------|-------|------------|
| Equipe | R$ 1.155.000 | 86,5% |
| Hardware e infra | R$ 25.000 | 1,9% |
| Operacional e servicos | R$ 154.900 | 11,6% |
| **TOTAL 18 MESES** | **R$ 1.334.900** | 100% |

---

## 10. Pretensao de Investimento

### 10.1 Rodada Buscada

| Parametro | Valor | Benchmark |
|-----------|-------|-----------|
| **Investimento buscado** | **R$ 1.500.000** (US$ ~270.000) | Range tipico pre-seed Brasil: R$ 500K-3M [REF-42] |
| **Tipo** | Pre-Seed / Angel | |
| **Equity oferecido** | 10-15% | Benchmark Carta Q3 2025: 10-15% diluicao [REF-40] |
| **Valuation pre-money** | R$ 8.500.000 - R$ 13.500.000 | Brasil pre-seed: R$ 5M-15M [REF-42]; desconto de 40-60% vs. EUA [REF-40] |
| **Uso dos fundos** | 18 meses de operacao ate o lancamento | |
| **Proxima rodada** | Seed de R$ 5-8M em 2027 Q4 (pos-lancamento, com tracao) | |

### 10.2 Justificativa do Valuation

- Mercado global de tech assistiva para DV: **US$ 6,1 bilhoes**, crescendo 13% ao ano [REF-06]
- Tecnologia proprietaria com modelo treinado em **71k+ imagens, 54 classes**
- App funcional em 2 plataformas (Android + iOS)
- Pipeline de hardware validado (Jetson + oculos AR)
- Fundador com deficiencia visual (skin in the game — credibilidade unica)
- Propriedade intelectual: arquitetura de IA embarcada offline para navegacao
- **Nenhum concorrente brasileiro** identificado
- **Comparaveis auditados:**
  - dotLumen: EUR 10M+ total (EUR 5M em jan. 2025) [REF-20]
  - Envision: ~EUR 1,75M total [REF-16]
  - Be My Eyes: ~US$ 17,4M total (Series A+ US$ 6,1M em jan. 2025) [REF-11]
  - Aira: ~US$ 17,7M total [REF-18]
  - eSight: ~US$ 15M total, adquirido pela Gentex em 2024 [REF-19]
  - OrCam: ~US$ 130M+ total, valuation pre-crise US$ 1B [REF-14]

### 10.3 Aplicacao dos Recursos (R$ 1.500.000)

```
Equipe e Pessoas ███████████████████████████████████  77%  R$ 1.155.000
Operacional      ████████░░░░░░░░░░░░░░░░░░░░░░░░░░  10%  R$   154.900
Hardware/Infra   ██░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░   2%  R$    25.000
Reserva          █████░░░░░░░░░░░░░░░░░░░░░░░░░░░░░  11%  R$   165.100
```

### 10.4 Detalhamento por Trimestre

| Trimestre | Foco | Investimento | Acumulado |
|-----------|------|-------------|-----------|
| **2026 Q2** | MVP final + modelo v3 + testes iniciais | R$ 170.000 | R$ 170.000 |
| **2026 Q3** | Prototipo Jetson + oculos + contratacoes | R$ 230.000 | R$ 400.000 |
| **2026 Q4** | Beta fechado + 20 usuarios + parcerias | R$ 280.000 | R$ 680.000 |
| **2027 Q1** | Beta aberto + 50 usuarios + iteracao | R$ 280.000 | R$ 960.000 |
| **2027 Q2** | Preparacao lancamento + marketing + B2B | R$ 280.000 | R$ 1.240.000 |
| **2027 Q3** | Lancamento comercial + operacao inicial | R$ 260.000 | R$ 1.500.000 |

### 10.5 Marcos (Milestones) para o Investidor

| Marco | Prazo | Criterio de Sucesso |
|-------|-------|-------------------|
| **M1** — Modelo v3 treinado e deployed | 2026 Q2 | mAP50 > 0.75 com 54 classes |
| **M2** — Prototipo Jetson + oculos funcional | 2026 Q3 | Demo ao vivo com overlay AR |
| **M3** — Beta fechado com 20 usuarios | 2026 Q4 | NPS > 60, feedback qualitativo positivo |
| **M4** — Beta aberto com 50 usuarios | 2027 Q1 | Retencao 30 dias > 70% |
| **M5** — Primeiro cliente B2B (instituicao) | 2027 Q2 | Contrato assinado |
| **M6** — Lancamento comercial | 2027 Q3 | App nas stores + kit a venda |

---

## 11. Analise de Riscos

| Risco | Probabilidade | Impacto | Mitigacao |
|-------|--------------|---------|-----------|
| Modelo IA com acuracia insuficiente | Media | Alto | Treinos iterativos, 71k+ imagens, fine-tuning continuo, 20 datasets |
| Big tech agrega funcoes assistivas | Alta | Alto | Especializacao em obstaculos/navegacao; overlay visual; offline; LATAM focus |
| Hardware de oculos AR descontinuado | Media | Medio | Suporte a multiplos modelos (Rokid, RayNeo, Mentra), design modular |
| Adocao lenta pelos usuarios | Media | Alto | Parcerias com Fundacao Dorina/IBC, distribuicao via SUS, advocacy |
| OrCam/outro concorrente com pivot agressivo | Baixa | Medio | Vantagem de custo 3-10x, modelo especializado, presenca local |
| Regulatorio (ANVISA, LGPD) | Baixa | Medio | Processamento 100% local; consultoria juridica preventiva |
| Dificuldade de contratacao (ML/embedded) | Media | Medio | Trabalho remoto, parcerias com universidades (USP, Unicamp) |
| Fundador solo (bus factor) | Media | Alto | Prioridade de contratacao: co-founder tecnico ou operacional |

---

## 12. Impacto Social

- **7,9 milhoes** de brasileiros com dificuldade para enxergar podem se beneficiar [REF-04]
- **338 milhoes** globalmente com deficiencia visual severa [REF-03]
- **80%** dos casos de deficiencia visual sao em paises em desenvolvimento [REF-01] — onde solucao offline e acessivel e mais necessaria
- **Autonomia e seguranca** na mobilidade urbana
- **Inclusao no mercado de trabalho** — maior independencia = mais empregabilidade
- **Reducao de acidentes** — alertas em tempo real para escadas, buracos, veiculos
- **Saude mental** — independencia reduz isolamento e depressao
- Alinhado com **ODS 3** (saude), **ODS 10** (reducao de desigualdades), **ODS 11** (cidades sustentaveis) [REF-52]

---

## 13. Estrategia de Saida (Exit)

| Cenario | Horizonte | Valuation Estimado | Comparavel |
|---------|-----------|-------------------|-----------|
| Aquisicao por big tech (Google, Microsoft, Meta) | 5-7 anos | R$ 50-200M | OrCam atingiu US$ 1B pre-crise [REF-14] |
| Aquisicao por empresa de saude/med-tech | 4-6 anos | R$ 30-100M | eSight adquirido pela Gentex em 2024 [REF-19] |
| IPO (se escala internacional) | 7-10 anos | R$ 200M+ | Cenario otimista com expansao global |
| Empresa lucrativa e independente | 3-4 anos | Dividendos | Receita recorrente SaaS + hardware |

**Comparaveis de exit recentes:**
- eSight (CA): Adquirido pela Gentex Corporation em jan. 2024 [REF-19] — validacao de exit no setor
- OrCam (IL): Avaliado em US$ 1B (2018), atualmente em reestruturacao a ~US$ 30-40M [REF-14] — cautionary tale sobre dependencia de hardware caro
- dotLumen (RO): EUR 10M+ total funding, expansao para robos autonomos [REF-20] — mostra caminho de diversificacao tecnologica

---

## 14. Informacoes para Contato

| Dado | Detalhe |
|------|---------|
| **Fundador** | Daniel Valladares |
| **Email** | daniel.valladares@eventosexponenciais.com.br |
| **GitHub** | https://github.com/dvlexp/ClaraVis-Android |
| **Pitch Deck** | Disponivel em PT-BR, EN e ES (PDF) |
| **Demo** | App Android funcional — disponivel para demonstracao |
| **Referencias** | Ver `REFERENCIAS.md` para todas as fontes citadas neste documento |

---

*Documento confidencial. Preparado para apresentacao a investidores.*
*Dados de mercado auditados em marco de 2026. Todas as fontes em REFERENCIAS.md.*
*ClaraVis (c) 2026. Todos os direitos reservados.*
