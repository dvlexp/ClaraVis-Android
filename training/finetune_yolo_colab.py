"""
ClaraVis — YOLO Fine-Tuning Script
===================================
Rodar no Google Colab (GPU gratis) ou localmente com GPU.

Objetivo: treinar YOLOv8s com classes otimizadas para navegação assistiva.

Uso no Colab:
    1. Upload este script
    2. !pip install ultralytics roboflow
    3. !python finetune_yolo_colab.py

Ou rodar as células abaixo individualmente.
"""

# ── 1. Instalar dependências ──
# !pip install ultralytics roboflow

from ultralytics import YOLO
from pathlib import Path
import os

# ── 2. Configuração ──
BASE_MODEL = "yolov8s.pt"       # Modelo base (COCO pré-treinado)
EPOCHS = 100                     # Épocas de treino
IMG_SIZE = 416                   # Resolução de treino (416 para mobile)
BATCH_SIZE = 16                  # Reduzir se der OOM
DEVICE = "0"                     # GPU (0) ou "cpu"
PROJECT = "claravis_training"
NAME = "yolov8s_navigation"

# ── 3. Dataset ──
# Opção A: Usar datasets públicos do Roboflow (recomendado para começar)
# Opção B: Criar dataset próprio

USE_ROBOFLOW = True  # Mudar para False se usar dataset local

if USE_ROBOFLOW:
    from roboflow import Roboflow

    # Datasets recomendados para navegação assistiva (Roboflow Universe):
    # Escolher UM ou combinar vários:

    # 1. Sidewalk obstacles (buracos, postes, lixeiras)
    #    https://universe.roboflow.com/search?q=sidewalk+obstacle
    #
    # 2. Crosswalk detection (faixas de pedestre)
    #    https://universe.roboflow.com/search?q=crosswalk
    #
    # 3. Traffic signs/lights
    #    https://universe.roboflow.com/search?q=traffic+sign
    #
    # 4. Indoor obstacles (cadeiras, mesas, portas)
    #    https://universe.roboflow.com/search?q=indoor+obstacle
    #
    # 5. Stairs detection
    #    https://universe.roboflow.com/search?q=stairs+detection

    # Exemplo: download de um dataset (substituir com sua API key do Roboflow)
    # Criar conta gratuita em roboflow.com → Settings → API Key
    ROBOFLOW_API_KEY = "SUA_API_KEY_AQUI"  # Substituir!

    rf = Roboflow(api_key=ROBOFLOW_API_KEY)

    # Exemplo: dataset de obstáculos em calçadas
    # project = rf.workspace("WORKSPACE").project("PROJECT_NAME")
    # dataset = project.version(1).download("yolov8")
    # DATA_YAML = dataset.location + "/data.yaml"

    print("="*60)
    print("INSTRUÇÃO:")
    print("1. Crie conta em roboflow.com (gratuita)")
    print("2. Busque datasets relevantes em universe.roboflow.com")
    print("3. Exporte no formato YOLOv8")
    print("4. Substitua ROBOFLOW_API_KEY e descomente as linhas acima")
    print("="*60)
    print()
    print("Datasets recomendados para ClaraVis:")
    print("  - 'sidewalk obstacles' (buracos, postes)")
    print("  - 'crosswalk detection' (faixas de pedestre)")
    print("  - 'stairs detection' (escadas)")
    print("  - 'traffic sign detection' (placas)")
    print("  - 'indoor obstacle detection' (móveis)")
    print()
    print("Enquanto não configurar o dataset, vou treinar com COCO subset.")
    print()

    # Fallback: usar COCO mas filtrar apenas classes relevantes
    DATA_YAML = None

else:
    # Opção B: Dataset local
    # Estrutura esperada:
    # dataset/
    #   train/
    #     images/
    #     labels/
    #   val/
    #     images/
    #     labels/
    #   data.yaml
    DATA_YAML = "dataset/data.yaml"


# ── 4. Treinar ──
print(f"Carregando modelo base: {BASE_MODEL}")
model = YOLO(BASE_MODEL)

if DATA_YAML:
    print(f"Treinando com dataset: {DATA_YAML}")
    results = model.train(
        data=DATA_YAML,
        epochs=EPOCHS,
        imgsz=IMG_SIZE,
        batch=BATCH_SIZE,
        device=DEVICE,
        project=PROJECT,
        name=NAME,
        # Otimizações para melhor generalização em mobile
        augment=True,
        mosaic=1.0,         # Mosaic augmentation (combina 4 imagens)
        mixup=0.1,          # Mixup augmentation
        hsv_h=0.02,         # Variação de matiz (adapta a diferentes iluminações)
        hsv_s=0.7,          # Variação de saturação
        hsv_v=0.5,          # Variação de brilho (IMPORTANTE para low-light)
        flipud=0.0,         # Não inverter vertical (gravidade importa)
        fliplr=0.5,         # Inverter horizontal OK
        degrees=5.0,        # Rotação leve (câmera na cabeça pode oscilar)
        translate=0.1,      # Translação
        scale=0.5,          # Escala (objetos em distâncias variadas)
        perspective=0.0005, # Perspectiva leve
        # Learning rate
        lr0=0.01,
        lrf=0.01,
        warmup_epochs=3,
        # Regularização
        weight_decay=0.0005,
        dropout=0.1,
    )
else:
    print("Nenhum dataset configurado. Pulando treino.")
    print("Configure DATA_YAML com um dataset do Roboflow ou local.")
    results = None


# ── 5. Exportar para TFLite (mobile) ──
if results:
    best_model = Path(PROJECT) / NAME / "weights" / "best.pt"
    print(f"\nExportando melhor modelo: {best_model}")

    trained = YOLO(str(best_model))

    # Exportar float16 (metade do tamanho, quase mesma qualidade)
    print("\n--- Exportando TFLite float16 (416x416) ---")
    trained.export(format="tflite", imgsz=IMG_SIZE, half=True)

    # Exportar float16 (320x320 para fallback em devices mais fracos)
    print("\n--- Exportando TFLite float16 (320x320) ---")
    trained.export(format="tflite", imgsz=320, half=True)

    print("\n" + "="*60)
    print("PRONTO! Modelos exportados.")
    print("Copie o .tflite para:")
    print("  ClaraVis-Android/app/src/main/assets/")
    print("  (ou /sdcard/Download/claravis_model.tflite)")
    print("="*60)

else:
    # Se não treinou, pelo menos exportar o modelo base em 416x416
    print("\nExportando modelo base COCO em 416x416 float16...")
    model = YOLO(BASE_MODEL)
    model.export(format="tflite", imgsz=416, half=True)
    print("Exportado! Copie para assets.")


# ── 6. Validar modelo exportado ──
print("\n--- Validação ---")
export_path = Path(PROJECT) / NAME / "weights" if results else Path(".")
tflite_files = list(export_path.rglob("*.tflite")) if results else list(Path(".").glob("*416*float16*.tflite"))

for f in tflite_files:
    size_mb = f.stat().st_size / 1024 / 1024
    print(f"  {f.name}: {size_mb:.1f} MB")
