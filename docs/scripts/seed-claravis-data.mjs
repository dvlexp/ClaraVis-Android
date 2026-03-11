// Seed inicial: popula ClaraVis Directus com dados do projeto existente
const DIRECTUS_URL = 'https://project.resultadosexponenciais.com.br';
const DIRECTUS_TOKEN = 'claude-mcp-token-183c0965094b2d21baebdc65f4716982';
const headers = { 'Authorization': `Bearer ${DIRECTUS_TOKEN}`, 'Content-Type': 'application/json' };

async function api(method, path, body) {
  const res = await fetch(`${DIRECTUS_URL}${path}`, { method, headers, ...(body && { body: JSON.stringify(body) }) });
  const data = await res.json();
  if (!res.ok) { console.error(`  ERROR: ${path} — ${data.errors?.[0]?.message || JSON.stringify(data)}`); return null; }
  return data.data;
}

async function create(collection, item) {
  const r = await api('POST', `/items/${collection}`, item);
  if (r) console.log(`  ✓ ${collection}: ${item.name || item.title || item.code || item.version || item.ref_id}`);
  return r;
}

async function main() {
  console.log('🌱 Seeding ClaraVis data...\n');

  // ═══════════════════════════════
  // FASES DO PROJETO (PMI)
  // ═══════════════════════════════
  console.log('📅 Phases...');
  const f1 = await create('claravis_phases', {
    code: 'F1', name: 'Software-Only (Android)', status: 'in_progress',
    description: 'App Android com YOLO fine-tuned, TFLite, detecção offline no Helio G85. Inclui treinos v1-v3.',
    start_date: '2025-12-01', end_date: '2026-06-30',
    budget_brl: 500, budget_spent_brl: 200, progress_pct: 65, color: '#4CAF50',
  });
  const f2 = await create('claravis_phases', {
    code: 'F2', name: 'Jetson + Display AR', status: 'planned',
    description: 'Migrar pipeline para Jetson Orin NX 16GB. Adicionar Depth Anything, áudio espacial 3D, VLM on-device, display AR.',
    start_date: '2026-07-01', end_date: '2027-06-30',
    budget_brl: 12000, budget_spent_brl: 0, progress_pct: 0, color: '#2196F3',
  });
  const f3 = await create('claravis_phases', {
    code: 'F3', name: 'Sensores Adicionais', status: 'backlog',
    description: 'FLIR Lepton XDS (termal), OAK-D stereo, UWB indoor nav, SLAM semântico, haptic band.',
    start_date: '2027-07-01', end_date: '2028-06-30',
    budget_brl: 8000, budget_spent_brl: 0, progress_pct: 0, color: '#FF9800',
  });
  const f4 = await create('claravis_phases', {
    code: 'F4', name: 'Glasses Standalone', status: 'backlog',
    description: 'Óculos standalone com Snapdragon AR1+ ou Hailo-8. LLM on-device, full sensor fusion, eye tracking.',
    start_date: '2028-07-01', end_date: '2029-06-30',
    budget_brl: 50000, budget_spent_brl: 0, progress_pct: 0, color: '#9C27B0',
  });

  // ═══════════════════════════════
  // ENTREGAS (F1)
  // ═══════════════════════════════
  console.log('\n📦 Deliverables (F1)...');
  await create('claravis_deliverables', {
    phase_id: f1?.id, code: 'F1.1', title: 'App Android base (Kotlin)', status: 'completed',
    priority: 'critical', assigned_to: 'Daniel', progress_pct: 100,
    description: 'App funcional com camera, YOLO TFLite, TTS, overlay visual. 8 source files.',
    acceptance_criteria: '- Roda offline\n- Detecção >5 FPS no Helio G85\n- TTS em português',
    completed_date: '2026-02-15',
  });
  await create('claravis_deliverables', {
    phase_id: f1?.id, code: 'F1.2', title: 'Treino v1 (36 classes, 5K imgs)', status: 'completed',
    priority: 'critical', assigned_to: 'Daniel', progress_pct: 100,
    description: 'Primeiro modelo fine-tuned. mAP50=0.800, P=0.810, R=0.762.',
    completed_date: '2026-02-20',
  });
  await create('claravis_deliverables', {
    phase_id: f1?.id, code: 'F1.3', title: 'Treino v2 (37 classes, 38K imgs)', status: 'completed',
    priority: 'critical', assigned_to: 'Daniel', progress_pct: 100,
    description: 'Modelo deployed. mAP50=0.694, P=0.717, R=0.654. 6 datasets unificados.',
    completed_date: '2026-03-05',
  });
  await create('claravis_deliverables', {
    phase_id: f1?.id, code: 'F1.4', title: 'Treino v3 (54 classes, 71K imgs)', status: 'in_progress',
    priority: 'critical', assigned_to: 'Daniel', progress_pct: 50,
    description: 'YOLOv8s, 20 datasets, parou na epoch 49 por falta de compute no Colab.',
    acceptance_criteria: '- mAP50 > 0.75\n- 100 epochs completas\n- Deploy no device',
  });
  await create('claravis_deliverables', {
    phase_id: f1?.id, code: 'F1.5', title: 'Pitch Deck auditado (PT/EN/ES)', status: 'completed',
    priority: 'high', assigned_to: 'Daniel', progress_pct: 100,
    description: 'Pitch deck com dados verificados, fontes auditadas, 3 idiomas.',
    completed_date: '2026-03-10',
  });
  await create('claravis_deliverables', {
    phase_id: f1?.id, code: 'F1.6', title: 'Plano de Negócios v2 (Kotler)', status: 'completed',
    priority: 'high', assigned_to: 'Daniel', progress_pct: 100,
    description: 'Plano completo com STP, 5 Níveis, 4Ps, SWOT, 14 concorrentes, referencias auditadas.',
    completed_date: '2026-03-10',
  });
  await create('claravis_deliverables', {
    phase_id: f1?.id, code: 'F1.7', title: 'Outreach patrocinadores (10 emails)', status: 'in_progress',
    priority: 'high', assigned_to: 'Daniel', progress_pct: 80,
    description: '10 drafts criados no Gmail. Pendente: preencher placeholders e enviar.',
  });
  await create('claravis_deliverables', {
    phase_id: f1?.id, code: 'F1.8', title: 'App iOS (Swift)', status: 'blocked',
    priority: 'medium', assigned_to: 'Daniel', progress_pct: 40,
    description: 'Port nativo em Swift. Precisa Xcode/Mac para build e teste.',
  });

  // ═══════════════════════════════
  // MILESTONES
  // ═══════════════════════════════
  console.log('\n🏁 Milestones...');
  await create('claravis_milestones', {
    phase_id: f1?.id, title: 'MVP Android funcional', status: 'achieved',
    target_date: '2026-03-01', actual_date: '2026-02-15',
    gate_type: 'release', success_criteria: 'App detecta obstaculos offline com >5 FPS e TTS',
  });
  await create('claravis_milestones', {
    phase_id: f1?.id, title: 'Treino v3 concluído (54 classes)', status: 'delayed',
    target_date: '2026-03-15', gate_type: 'review',
    success_criteria: 'mAP50 > 0.75, modelo deployed no device, 54 classes funcionais',
  });
  await create('claravis_milestones', {
    phase_id: f1?.id, title: 'Primeiro investidor/patrocinador confirmado', status: 'pending',
    target_date: '2026-06-30', gate_type: 'go_nogo',
    success_criteria: 'Pelo menos 1 patrocinador com compromisso formal (LOI ou contrato)',
  });
  await create('claravis_milestones', {
    phase_id: f2?.id, title: 'Jetson Orin NX operacional', status: 'pending',
    target_date: '2026-09-30', gate_type: 'demo',
    success_criteria: 'YOLO + Depth Anything rodando simultaneamente >15 FPS no Jetson',
  });
  await create('claravis_milestones', {
    phase_id: f2?.id, title: 'Primeiro protótipo com display AR', status: 'pending',
    target_date: '2027-03-31', gate_type: 'demo',
    success_criteria: 'Overlays visuais (contornos + labels) visíveis em display AR conectado ao Jetson',
  });

  // ═══════════════════════════════
  // RISCOS
  // ═══════════════════════════════
  console.log('\n⚠️ Risks...');
  await create('claravis_risks', {
    title: 'Falta de funding para hardware Fase 2', status: 'monitoring',
    category: 'financial', probability: 'high', impact: 'high', risk_score: 16,
    mitigation_plan: 'Diversificar fontes: patrocinadores corporativos, FAPESP, editais FINEP/CNPq, crowdfunding.',
    contingency_plan: 'Priorizar Fase 1 com melhorias software-only. Buscar parceria acadêmica (UNESP NavWear).',
    owner: 'Daniel',
  });
  await create('claravis_risks', {
    title: 'Performance insuficiente do YOLO no Helio G85', status: 'mitigated',
    category: 'technical', probability: 'low', impact: 'medium', risk_score: 4,
    mitigation_plan: 'Migrar para YOLO11n (menos params). Reduzir resolução. Threshold adaptativo.',
    contingency_plan: 'Fase 2 resolve com Jetson (100-157 TOPS).',
    owner: 'Daniel',
  });
  await create('claravis_risks', {
    title: 'Colab compute units insuficientes para treino', status: 'occurred',
    category: 'technical', probability: 'very_high', impact: 'medium', risk_score: 12,
    mitigation_plan: 'Comprar 100 CU extras ($9.99). Usar T4 quando possível. Checkpointing agressivo.',
    contingency_plan: 'Treinar local em GPU alugada (Lambda, Vast.ai). Reduzir epochs.',
    owner: 'Daniel',
  });
  await create('claravis_risks', {
    title: 'Nenhum concorrente brasileiro = mercado não validado', status: 'monitoring',
    category: 'market', probability: 'medium', impact: 'medium', risk_score: 9,
    mitigation_plan: 'Validar com usuários reais (beta testers DV). Parcerias com institutos de cegos.',
    contingency_plan: 'Pivotar para mercado global se BR não responder. US/EU já validados.',
    owner: 'Daniel',
  });
  await create('claravis_risks', {
    title: 'OrCam ou big tech lança produto similar barato', status: 'monitoring',
    category: 'market', probability: 'medium', impact: 'high', risk_score: 12,
    mitigation_plan: 'Acelerar diferencial AR overlay visual. Foco no mercado BR (preço, português, contexto local).',
    contingency_plan: 'Posicionar como plataforma open-source/acadêmica se mercado commoditizar.',
    owner: 'Daniel',
  });

  // ═══════════════════════════════
  // TRAINING RUNS
  // ═══════════════════════════════
  console.log('\n🧠 Training Runs...');
  await create('claravis_training_runs', {
    version: 'v1', status: 'completed', model_arch: 'YOLOv8s', num_classes: 36,
    num_images: 5000, num_datasets: 1, image_size: 416, epochs_planned: 100, epochs_completed: 100,
    gpu: 'T4', platform: 'Colab Pro', map50: 0.800, precision: 0.810, recall: 0.762,
    model_file: 'claravis_model_416.tflite',
    notes: 'Primeiro treino. Bom resultado mas poucas imagens e classes limitadas.',
  });
  await create('claravis_training_runs', {
    version: 'v2', status: 'completed', model_arch: 'YOLOv8s', num_classes: 37,
    num_images: 38000, num_datasets: 6, image_size: 416, epochs_planned: 100, epochs_completed: 100,
    gpu: 'A100', platform: 'Colab Pro', map50: 0.694, precision: 0.717, recall: 0.654,
    model_file: 'claravis_v2_model_416.tflite',
    notes: 'Modelo deployed. mAP caiu vs v1 por ter mais classes e dados variados. Threshold ajustado para 0.50.',
  });
  await create('claravis_training_runs', {
    version: 'v3', status: 'failed', model_arch: 'YOLOv8s', num_classes: 54,
    num_images: 71000, num_datasets: 20, image_size: 416, epochs_planned: 100, epochs_completed: 49,
    gpu: 'A100', platform: 'Colab Pro',
    notes: 'Parou na epoch 49 — Colab desconectou, compute units esgotados. Erro load_state_dict resolvido (checkpoint v2 incompatível). Precisa retomar.',
  });

  // ═══════════════════════════════
  // TOP FEATURES (do mapa)
  // ═══════════════════════════════
  console.log('\n⭐ Features (essenciais)...');
  const essentials = [
    { name: 'Detecção YOLO on-device', category: 'detection', status: 'implemented', roadmap_phase: 'phase_1', complexity: 'medium', estimated_cost_brl: 0, description: 'YOLOv8s fine-tuned, 54 classes, TFLite. Upgrade path: YOLO11n/YOLOv10n.', reference_ids: ['REF-60'] },
    { name: 'Profundidade monocular (Depth Anything V2)', category: 'depth', status: 'essential', roadmap_phase: 'phase_1', complexity: 'medium', estimated_cost_brl: 0, description: 'Estima distância de cada objeto detectado. ~25MB, ~30 FPS no Jetson. Zero hardware adicional.', reference_ids: ['REF-71'] },
    { name: 'Áudio espacial 3D (HRTF)', category: 'audio', status: 'essential', roadmap_phase: 'phase_1', complexity: 'medium', estimated_cost_brl: 0, description: '3D Tune-In Toolkit ou Steam Audio. Som posicionado no espaço 3D. Fones de condução óssea.', reference_ids: ['REF-77', 'REF-78'] },
    { name: 'OCR on-device', category: 'ocr', status: 'essential', roadmap_phase: 'phase_1', complexity: 'low', estimated_cost_brl: 0, description: 'Google ML Kit v2 (Android) ou PaddleOCR (9.6MB, 100+ idiomas).', reference_ids: ['REF-97', 'REF-98'] },
    { name: 'Detecção de semáforos (LYTNet)', category: 'detection', status: 'essential', roadmap_phase: 'phase_1', complexity: 'medium', estimated_cost_brl: 0, description: 'CNN leve, open-source com dataset PTL. Detecta verde/vermelho/piscando + faixa.', reference_ids: ['REF-65'] },
    { name: 'Reconhecimento facial (InsightFace)', category: 'face', status: 'essential', roadmap_phase: 'phase_1', complexity: 'medium', estimated_cost_brl: 0, description: 'InspireFace SDK, 64MB RAM, ARM/Jetson. Identifica pessoas conhecidas.', reference_ids: ['REF-99'] },
    { name: 'Overlays visuais AR (contornos, labels, alertas)', category: 'display', status: 'essential', roadmap_phase: 'phase_2', complexity: 'high', estimated_cost_brl: 4000, description: 'PRINCIPAL DIFERENCIAL. Nenhum concorrente faz em hardware acessível. Display Xreal ou RayNeo.', reference_ids: ['REF-88'], competitors_who_have: ['Apple Vision Pro ($3499)'] },
    { name: 'VLM on-device (SmolVLM2-500M)', category: 'vlm', status: 'essential', roadmap_phase: 'phase_2', complexity: 'high', estimated_cost_brl: 0, description: 'Menor VLM para vídeo. Roda no Jetson 16GB quantizado. Descrição de cena offline.', reference_ids: ['REF-92'] },
    { name: 'SLAM visual (ORB-SLAM3)', category: 'navigation', status: 'essential', roadmap_phase: 'phase_2', complexity: 'high', estimated_cost_brl: 0, description: 'Mapeamento + localização indoor. Transforma ClaraVis de "detector" em "navegador".', reference_ids: ['REF-102'] },
    { name: 'Jetson Orin NX 16GB (belt-pack)', category: 'hardware', status: 'essential', roadmap_phase: 'phase_2', complexity: 'medium', estimated_cost_brl: 5500, description: 'reComputer Super J4012. 100-157 TOPS. Roda tudo: YOLO + Depth + SLAM + VLM.', reference_ids: ['REF-111'] },
  ];
  for (const f of essentials) {
    await create('claravis_features', { ...f, priority: 'critical' });
  }

  console.log('\n⭐ Features (interessantes)...');
  const interesting = [
    { name: 'FLIR Lepton XDS (câmera termal)', category: 'thermal', roadmap_phase: 'phase_3', complexity: 'medium', estimated_cost_brl: 700, description: 'Tamanho de moeda, $109-239, 150mW. Detecta pessoas/animais no escuro, superfícies quentes.', reference_ids: ['REF-110'] },
    { name: 'Pulseira/cinto háptico direcional', category: 'haptic', roadmap_phase: 'phase_2', complexity: 'low', estimated_cost_brl: 300, description: 'Arduino + 4-8 motores vibração. Complemento ao áudio em ambientes barulhentos.', reference_ids: ['REF-84'] },
    { name: 'UWB indoor navigation', category: 'navigation', roadmap_phase: 'phase_3', complexity: 'medium', estimated_cost_brl: 100, description: 'Precisão centimétrica indoor. Já comprovado para DV (estádio Londres).', reference_ids: ['REF-107'] },
    { name: 'Night vision digital', category: 'thermal', roadmap_phase: 'phase_3', complexity: 'medium', estimated_cost_brl: 400, description: 'Sensor STARVIS 2 + NIR LED. Caminhada noturna segura. Nenhum concorrente tem.', reference_ids: ['REF-91'] },
    { name: 'Segmentação de calçada (QPULM)', category: 'detection', roadmap_phase: 'phase_2', complexity: 'high', estimated_cost_brl: 0, description: 'Pixel-level: onde é seguro pisar. UNet-MobileNet quantizado, Android.', reference_ids: ['REF-68'] },
    { name: 'Person tracking (ByteTrack)', category: 'tracking', roadmap_phase: 'phase_2', complexity: 'low', estimated_cost_brl: 0, description: 'Seguir acompanhante em multidão. Open-source, leve.', reference_ids: ['REF-120'] },
  ];
  for (const f of interesting) {
    await create('claravis_features', { ...f, status: 'interesting', priority: 'medium' });
  }

  // ═══════════════════════════════
  // STAKEHOLDERS
  // ═══════════════════════════════
  console.log('\n👥 Stakeholders...');
  await create('claravis_stakeholders', {
    name: 'Daniel Valladares', organization: 'ClaraVis', role: 'Fundador / Desenvolvedor',
    category: 'team', status: 'active', influence: 'high', interest: 'high',
    engagement_strategy: 'Core team — execução diária',
  });
  await create('claravis_stakeholders', {
    name: 'NavWear Team (UNESP/UFES)', organization: 'UNESP + UFES',
    role: 'Potencial parceiro acadêmico', category: 'academic', status: 'potential',
    influence: 'medium', interest: 'medium',
    engagement_strategy: 'Keep Informed — contatar via FAPESP Grant 2019/14438-4',
    notes: 'Projeto brasileiro mais próximo: Jetson Nano + RealSense + háptico. Publicação 2025.',
  });

  // ═══════════════════════════════
  // CUSTOS INICIAIS
  // ═══════════════════════════════
  console.log('\n💰 Costs...');
  await create('claravis_costs', {
    phase_id: f1?.id, category: 'cloud', status: 'paid',
    item: 'Google Colab Pro (mensal)', amount_brl: 55, amount_usd: 9.99,
    vendor: 'Google', purchase_date: '2026-03-01',
  });
  await create('claravis_costs', {
    phase_id: f2?.id, category: 'hardware', status: 'planned',
    item: 'reComputer Super J4012 (Jetson Orin NX 16GB)', amount_brl: 5500, amount_usd: 950,
    vendor: 'SeeedStudio',
  });
  await create('claravis_costs', {
    phase_id: f2?.id, category: 'hardware', status: 'planned',
    item: 'Display AR (Xreal Light ou similar)', amount_brl: 4000, amount_usd: 699,
    vendor: 'Xreal',
  });
  await create('claravis_costs', {
    phase_id: f3?.id, category: 'hardware', status: 'planned',
    item: 'FLIR Lepton XDS (câmera termal)', amount_brl: 700, amount_usd: 120,
    vendor: 'Teledyne FLIR',
  });
  await create('claravis_costs', {
    phase_id: f3?.id, category: 'hardware', status: 'planned',
    item: 'Luxonis OAK-D Lite (stereo depth)', amount_brl: 900, amount_usd: 149,
    vendor: 'Luxonis',
  });

  console.log('\n\n✅ Seed complete!');
}

main().catch(console.error);
