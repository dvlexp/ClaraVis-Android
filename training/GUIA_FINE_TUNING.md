# ClaraVis — Guia de Fine-Tuning do YOLO (Passo a Passo)

Este guia ensina como treinar o modelo YOLO para detectar melhor os objetos
que importam para navegacao de pessoas com deficiencia visual.

O que vamos fazer:
- Pegar imagens de ruas, calcadas, escadas, obstaculos
- Ensinar o modelo a reconhecer esses objetos
- Exportar o modelo treinado
- Colocar no celular

Tudo gratis. Nao precisa de GPU no seu computador.

---

## PARTE 1: Criar conta no Roboflow (5 minutos)

O Roboflow e um site que guarda e organiza imagens para treinar IA.
Ele tem milhares de datasets prontos que podemos usar de graca.

1. Abra o navegador e va em: **https://roboflow.com**
2. Clique em **"Sign Up Free"**
3. Pode usar sua conta Google para entrar (mais rapido)
4. Quando pedir para criar um workspace, coloque o nome **"ClaraVis"**
5. Pronto, conta criada!

### Pegar sua API Key (chave de acesso)

1. No Roboflow, clique no seu avatar (canto superior direito)
2. Clique em **"Settings"**
3. No menu lateral, clique em **"Roboflow API"**
4. Voce vai ver uma chave tipo: `rf_aBcDeFgHiJkL1234`
5. **Copie essa chave** — vamos usar no Colab

---

## PARTE 2: Escolher o dataset (10 minutos)

Vamos usar datasets que ja existem no Roboflow Universe.
Sao imagens que outras pessoas ja tiraram e anotaram para nos.

1. Abra: **https://universe.roboflow.com**
2. Na barra de busca, pesquise por esses termos (um de cada vez):
   - `sidewalk obstacle detection`
   - `pothole detection`
   - `crosswalk detection`
   - `stairs detection`
   - `traffic light detection`

3. Para cada resultado, olhe:
   - Quantas imagens tem (quanto mais, melhor — ideal 1000+)
   - Quantas classes tem
   - Se tem preview das imagens (clique para ver se sao boas)

4. Quando achar um bom, clique nele e depois clique em **"Download Dataset"**
5. Escolha o formato **"YOLOv8"**
6. Escolha **"download zip to computer"** OU **"show download code"**
   - Se escolher "show download code", ele vai mostrar um codigo Python
   - **Copie esse codigo** — vamos colar no Colab

### Datasets recomendados para comecar:

Esses sao os que eu recomendo para o ClaraVis:

**Opcao A (mais facil, um dataset so):**
- Busque: `obstacle detection for blind`
- Ou: `sidewalk obstacles`
- Escolha o que tiver mais imagens

**Opcao B (melhor resultado, combinar varios):**
- Busque e baixe separadamente:
  1. `pothole detection` (buracos no chao)
  2. `crosswalk detection` (faixas de pedestre)
  3. `stairs detection` (escadas)
- Depois combinamos no Colab

---

## PARTE 3: Abrir o Google Colab (2 minutos)

O Google Colab e como um computador na nuvem com GPU gratis.
Voce roda codigo Python sem instalar nada.

1. Abra: **https://colab.research.google.com**
2. Entre com sua conta Google
3. Clique em **"File" > "Upload notebook"**
4. Faca upload do arquivo: `ClaraVis_FineTuning.ipynb`
   (esta na pasta `training/` do projeto)
5. O notebook vai abrir com todas as celulas prontas

### IMPORTANTE: Ativar a GPU gratis

1. No Colab, clique em **"Runtime"** (menu superior)
2. Clique em **"Change runtime type"**
3. Em **"Hardware accelerator"**, selecione **"T4 GPU"**
4. Clique em **"Save"**

Sem isso, o treino vai levar horas em vez de minutos.

---

## PARTE 4: Rodar o notebook (30-60 minutos)

O notebook tem celulas numeradas. Rode cada uma clicando no botao
de play (triangulo) no canto esquerdo de cada celula.

### Celula 1: Instalar dependencias
- Clique play e espere terminar (1-2 minutos)
- Vai aparecer texto de instalacao — isso e normal

### Celula 2: Configurar
- Aqui voce cola sua API Key do Roboflow
- Substitua `SUA_API_KEY_AQUI` pela sua chave
- Clique play

### Celula 3: Baixar o dataset
- Essa celula baixa as imagens para dentro do Colab
- Se voce copiou o "download code" do Roboflow, cole aqui
- Clique play e espere (pode levar 2-5 minutos dependendo do tamanho)

### Celula 4: Treinar o modelo
- ESSA E A CELULA PRINCIPAL — aqui a magica acontece
- Clique play e **espere**
- Vai demorar entre 20-60 minutos dependendo do dataset
- Voce vai ver o progresso: `Epoch 1/100`, `Epoch 2/100`, etc.
- Vai ver metricas como `mAP50` — quanto maior, melhor (0.7+ e bom)
- **NAO feche a aba do navegador** enquanto treina!

### Celula 5: Ver resultados
- Mostra graficos de como o modelo aprendeu
- Mostra exemplos de deteccao em imagens do dataset
- Se o mAP50 ficou acima de 0.5, ja e util
- Se ficou acima de 0.7, esta otimo

### Celula 6: Exportar para TFLite
- Converte o modelo treinado para o formato do celular
- Gera dois arquivos:
  - `best_416_float16.tflite` (para o celular, 416x416)
  - `best_320_float16.tflite` (fallback menor)

### Celula 7: Baixar o modelo
- Faz download do arquivo .tflite para o seu computador
- **Salve em um lugar que voce lembre** (ex: Desktop)

---

## PARTE 5: Colocar o modelo no celular (5 minutos)

### Opcao A: Via ADB (mais rapido, precisa do cabo USB)

1. Conecte o celular no computador
2. Abra o terminal e rode:

```bash
# Copiar o modelo para o celular
adb push best_416_float16.tflite /sdcard/Download/claravis_model.tflite

# Reiniciar o app
adb shell am force-stop com.claravis.app
adb shell am start -n com.claravis.app/.MainActivity
```

O app vai detectar automaticamente o modelo novo no SD card
(tem prioridade sobre os modelos padrão dentro do app).

### Opcao B: Sem cabo (mais lento)

1. Envie o arquivo `best_416_float16.tflite` para o celular
   (por email, Google Drive, WhatsApp — o que for mais facil)
2. No celular, renomeie o arquivo para `claravis_model.tflite`
3. Mova para a pasta **Downloads** do celular
4. Feche e abra o ClaraVis

### Opcao C: Dentro do APK (permanente)

1. Copie o arquivo para:
   `ClaraVis-Android/app/src/main/assets/yolov8s_416_float16.tflite`
   (substituindo o modelo antigo)
2. Rebuilde e instale:
```bash
cd ~/ClaraVis-Android
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## PARTE 6: Testar e melhorar (continuo)

### Como saber se melhorou?

1. Abra o app e aponte para objetos
2. Veja se detecta coisas que antes nao detectava (escadas, buracos, etc.)
3. Veja se tem menos "erros" (detectar coisas erradas)

### Como melhorar ainda mais?

O segredo e: **mais dados = modelo melhor**

1. **Adicionar suas proprias imagens:**
   - Tire fotos com o celular dos lugares onde voce anda
   - Foque em: calcadas, escadas, obstaculos, portas, faixas de pedestre
   - Faca upload para o Roboflow
   - Anote os objetos (o Roboflow tem ferramenta de anotacao)
   - Retreine com o dataset maior

2. **Combinar datasets:**
   - Baixe 2-3 datasets diferentes do Roboflow Universe
   - O notebook tem uma celula para combinar

3. **Ajustar parametros:**
   - Se o modelo esta detectando coisas erradas: aumente `epochs` para 150
   - Se esta muito lento no celular: use `imgsz=320` em vez de 416
   - Se as deteccoes sao fracas: aumente `batch` para 32 (se a GPU aguentar)

---

## RESUMO RAPIDO

| Passo | O que fazer | Tempo |
|-------|------------|-------|
| 1 | Criar conta Roboflow | 5 min |
| 2 | Escolher dataset | 10 min |
| 3 | Abrir Colab + ativar GPU | 2 min |
| 4 | Rodar notebook | 30-60 min |
| 5 | Baixar modelo | 1 min |
| 6 | Copiar pro celular | 5 min |
| **Total** | | **~1 hora** |

---

## DUVIDAS COMUNS

**P: O Colab desconectou no meio do treino!**
R: Isso acontece se voce ficar muito tempo sem mexer. Mova o mouse na aba
do Colab de vez em quando. Se desconectou, rode tudo de novo (o dataset
ja fica em cache, entao e mais rapido da segunda vez).

**P: O modelo treinado ficou pior que o original!**
R: Provavelmente o dataset era muito pequeno ou muito diferente do que
voce precisa. Tente um dataset maior ou combine com COCO (o notebook
tem opcao para isso).

**P: Quanto custa?**
R: Zero. Google Colab gratis da ~4 horas de GPU por dia.
Roboflow gratis permite ate 10.000 imagens.

**P: Posso treinar no meu computador?**
R: Sim, se tiver GPU NVIDIA. Rode o mesmo notebook como script Python.
Sem GPU, vai levar muitas horas — use o Colab.

**P: Como sei se o dataset e bom?**
R: Olhe as imagens de preview. Se parecem com o que voce ve no dia-a-dia
(ruas, calcadas, ambientes internos), e um bom dataset. Se sao so fotos
de laboratorio ou muito diferentes da sua realidade, nao vai ajudar muito.
