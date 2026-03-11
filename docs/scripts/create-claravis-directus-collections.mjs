// Script para criar as collections ClaraVis no Directus da RXP
// Prefixo: claravis_ (evita conflito com rxp_site_, finmag_, etc.)
// Metodologia: PMI PMBOK 7a ed. adaptado para startup/hardware

const DIRECTUS_URL = 'https://project.resultadosexponenciais.com.br';
const DIRECTUS_TOKEN = 'claude-mcp-token-183c0965094b2d21baebdc65f4716982';

const headers = {
  'Authorization': `Bearer ${DIRECTUS_TOKEN}`,
  'Content-Type': 'application/json',
};

async function api(method, path, body) {
  const res = await fetch(`${DIRECTUS_URL}${path}`, {
    method,
    headers,
    ...(body && { body: JSON.stringify(body) }),
  });
  const data = await res.json();
  if (!res.ok) {
    const msg = data.errors?.[0]?.message || JSON.stringify(data);
    console.error(`  ERROR ${res.status}: ${path} — ${msg}`);
    return null;
  }
  return data.data;
}

// ── Helper: Create folder ──
async function createFolder(collection, icon, color, note) {
  console.log(`\n📁 Creating folder "${collection}"...`);
  const result = await api('POST', '/collections', {
    collection,
    meta: { collection, icon, color, note, hidden: false, singleton: false },
    schema: null,
  });
  if (result) console.log(`  ✓ Folder ${collection} created`);
  return result;
}

// ── Helper: Create collection + fields ──
async function createCollection(name, meta, fields) {
  console.log(`\n📦 Creating "${name}"...`);
  const col = await api('POST', '/collections', {
    collection: name,
    meta: { ...meta, group: meta.group || 'ClaraVis' },
    schema: {},
  });
  if (!col) return;
  console.log(`  ✓ Collection ${name} created`);

  for (const field of fields) {
    const f = await api('POST', `/fields/${name}`, field);
    if (f) console.log(`  ✓ Field: ${field.field}`);
  }
}

// ── Helper: Create relation ──
async function createRelation(collection, field, related_collection, opts = {}) {
  console.log(`\n🔗 Relation: ${collection}.${field} → ${related_collection}`);
  const result = await api('POST', '/relations', {
    collection,
    field,
    related_collection,
    meta: { one_field: opts.one_field || null, junction_field: null },
    schema: { on_delete: opts.on_delete || 'SET NULL' },
  });
  if (result) console.log('  ✓ Relation created');
}

// ══════════════════════════════════════════════════════
// COMMON FIELD TEMPLATES
// ══════════════════════════════════════════════════════

const STATUS_FIELD = (choices, defaultVal = 'draft') => ({
  field: 'status', type: 'string',
  meta: { interface: 'select-dropdown', display: 'labels', width: 'half', sort: 1,
    options: { choices } },
  schema: { default_value: defaultVal },
});

const PMI_STATUS = STATUS_FIELD([
  { text: 'Backlog', value: 'backlog' },
  { text: 'Planejado', value: 'planned' },
  { text: 'Em Andamento', value: 'in_progress' },
  { text: 'Bloqueado', value: 'blocked' },
  { text: 'Concluído', value: 'completed' },
  { text: 'Cancelado', value: 'cancelled' },
], 'backlog');

const PRIORITY_FIELD = {
  field: 'priority', type: 'string',
  meta: { interface: 'select-dropdown', display: 'labels', width: 'half', sort: 2,
    options: { choices: [
      { text: 'Crítica', value: 'critical' },
      { text: 'Alta', value: 'high' },
      { text: 'Média', value: 'medium' },
      { text: 'Baixa', value: 'low' },
    ] } },
  schema: { default_value: 'medium' },
};

const SORT_FIELD = {
  field: 'sort_order', type: 'integer',
  meta: { interface: 'input', width: 'half', sort: 99 },
  schema: { default_value: 0 },
};

// ══════════════════════════════════════════════════════
// MAIN
// ══════════════════════════════════════════════════════

async function main() {
  console.log('🚀 Creating ClaraVis Directus collections...\n');

  // ── Root folder ──
  await createFolder('ClaraVis', 'visibility', '#4CAF50',
    'ClaraVis — Tecnologia assistiva para deficientes visuais. Gestão de projeto (PMI) + Site/Blog.');

  // ── Sub-folders ──
  await createFolder('claravis_pmi', 'assignment', '#2196F3',
    'Gestão de Projeto — PMI PMBOK 7a ed.');
  // Set parent
  await api('PATCH', '/collections/claravis_pmi', { meta: { group: 'ClaraVis' } });

  await createFolder('claravis_produto', 'memory', '#FF9800',
    'Produto — Features, roadmap, benchmarks');
  await api('PATCH', '/collections/claravis_produto', { meta: { group: 'ClaraVis' } });

  await createFolder('claravis_site', 'language', '#9C27B0',
    'Site & Blog — claravis.com.br');
  await api('PATCH', '/collections/claravis_site', { meta: { group: 'ClaraVis' } });


  // ═══════════════════════════════════════════════════
  // PARTE 1: GESTÃO DE PROJETO (PMI)
  // ═══════════════════════════════════════════════════

  // ── 1.1 Fases do Projeto (Project Phases / WBS Level 1) ──
  await createCollection('claravis_phases', {
    group: 'claravis_pmi',
    icon: 'timeline',
    note: 'Fases do projeto — WBS nível 1 (PMI)',
    sort: 1,
  }, [
    PMI_STATUS,
    { field: 'name', type: 'string', meta: { interface: 'input', required: true, width: 'half', sort: 2 }, schema: { is_nullable: false } },
    { field: 'code', type: 'string', meta: { interface: 'input', required: true, width: 'half', sort: 3, note: 'Ex: F1, F2, F3' }, schema: { is_unique: true, is_nullable: false } },
    { field: 'description', type: 'text', meta: { interface: 'input-multiline', width: 'full', sort: 4 }, schema: {} },
    { field: 'start_date', type: 'date', meta: { interface: 'datetime', width: 'half', sort: 5 }, schema: {} },
    { field: 'end_date', type: 'date', meta: { interface: 'datetime', width: 'half', sort: 6 }, schema: {} },
    { field: 'budget_brl', type: 'float', meta: { interface: 'input', width: 'half', sort: 7, note: 'Orçamento em R$' }, schema: { default_value: 0 } },
    { field: 'budget_spent_brl', type: 'float', meta: { interface: 'input', width: 'half', sort: 8, note: 'Gasto realizado em R$' }, schema: { default_value: 0 } },
    { field: 'progress_pct', type: 'integer', meta: { interface: 'slider', width: 'half', sort: 9, options: { minValue: 0, maxValue: 100 } }, schema: { default_value: 0 } },
    { field: 'color', type: 'string', meta: { interface: 'select-color', width: 'half', sort: 10 }, schema: {} },
    SORT_FIELD,
  ]);

  // ── 1.2 Entregas / Work Packages (WBS Level 2) ──
  await createCollection('claravis_deliverables', {
    group: 'claravis_pmi',
    icon: 'inventory_2',
    note: 'Entregas / pacotes de trabalho — WBS nível 2',
    sort: 2,
  }, [
    PMI_STATUS,
    PRIORITY_FIELD,
    { field: 'phase_id', type: 'integer', meta: { interface: 'select-dropdown-m2o', width: 'half', sort: 3 }, schema: {} },
    { field: 'code', type: 'string', meta: { interface: 'input', width: 'half', sort: 4, note: 'Ex: F1.1, F2.3' }, schema: {} },
    { field: 'title', type: 'string', meta: { interface: 'input', required: true, width: 'full', sort: 5 }, schema: { is_nullable: false } },
    { field: 'description', type: 'text', meta: { interface: 'input-rich-text-md', width: 'full', sort: 6 }, schema: {} },
    { field: 'acceptance_criteria', type: 'text', meta: { interface: 'input-rich-text-md', width: 'full', sort: 7, note: 'Critérios de aceitação (DoD)' }, schema: {} },
    { field: 'assigned_to', type: 'string', meta: { interface: 'input', width: 'half', sort: 8 }, schema: {} },
    { field: 'start_date', type: 'date', meta: { interface: 'datetime', width: 'half', sort: 9 }, schema: {} },
    { field: 'due_date', type: 'date', meta: { interface: 'datetime', width: 'half', sort: 10 }, schema: {} },
    { field: 'completed_date', type: 'date', meta: { interface: 'datetime', width: 'half', sort: 11 }, schema: {} },
    { field: 'estimated_hours', type: 'float', meta: { interface: 'input', width: 'half', sort: 12 }, schema: {} },
    { field: 'actual_hours', type: 'float', meta: { interface: 'input', width: 'half', sort: 13 }, schema: {} },
    { field: 'cost_brl', type: 'float', meta: { interface: 'input', width: 'half', sort: 14, note: 'Custo em R$' }, schema: { default_value: 0 } },
    { field: 'progress_pct', type: 'integer', meta: { interface: 'slider', width: 'half', sort: 15, options: { minValue: 0, maxValue: 100 } }, schema: { default_value: 0 } },
    { field: 'dependencies', type: 'json', meta: { interface: 'tags', width: 'full', sort: 16, note: 'Códigos das dependências (ex: F1.1, F1.2)' }, schema: {} },
    { field: 'tags', type: 'json', meta: { interface: 'tags', width: 'half', sort: 17 }, schema: {} },
    SORT_FIELD,
  ]);

  // ── 1.3 Milestones (Marcos do Projeto) ──
  await createCollection('claravis_milestones', {
    group: 'claravis_pmi',
    icon: 'flag',
    note: 'Marcos — portões de decisão e entregas-chave',
    sort: 3,
  }, [
    STATUS_FIELD([
      { text: 'Pendente', value: 'pending' },
      { text: 'Atingido', value: 'achieved' },
      { text: 'Atrasado', value: 'delayed' },
      { text: 'Cancelado', value: 'cancelled' },
    ], 'pending'),
    { field: 'phase_id', type: 'integer', meta: { interface: 'select-dropdown-m2o', width: 'half', sort: 2 }, schema: {} },
    { field: 'title', type: 'string', meta: { interface: 'input', required: true, width: 'full', sort: 3 }, schema: { is_nullable: false } },
    { field: 'description', type: 'text', meta: { interface: 'input-multiline', width: 'full', sort: 4 }, schema: {} },
    { field: 'target_date', type: 'date', meta: { interface: 'datetime', width: 'half', sort: 5 }, schema: {} },
    { field: 'actual_date', type: 'date', meta: { interface: 'datetime', width: 'half', sort: 6 }, schema: {} },
    { field: 'gate_type', type: 'string', meta: { interface: 'select-dropdown', width: 'half', sort: 7,
      options: { choices: [
        { text: 'Go/No-Go', value: 'go_nogo' },
        { text: 'Review', value: 'review' },
        { text: 'Release', value: 'release' },
        { text: 'Demo', value: 'demo' },
        { text: 'Pitch', value: 'pitch' },
      ] } }, schema: {} },
    { field: 'success_criteria', type: 'text', meta: { interface: 'input-multiline', width: 'full', sort: 8 }, schema: {} },
    SORT_FIELD,
  ]);

  // ── 1.4 Riscos (Risk Register — PMI) ──
  await createCollection('claravis_risks', {
    group: 'claravis_pmi',
    icon: 'warning',
    note: 'Registro de riscos — PMI Risk Management',
    sort: 4,
  }, [
    STATUS_FIELD([
      { text: 'Identificado', value: 'identified' },
      { text: 'Em Monitoramento', value: 'monitoring' },
      { text: 'Mitigado', value: 'mitigated' },
      { text: 'Ocorreu', value: 'occurred' },
      { text: 'Encerrado', value: 'closed' },
    ], 'identified'),
    { field: 'title', type: 'string', meta: { interface: 'input', required: true, width: 'full', sort: 2 }, schema: { is_nullable: false } },
    { field: 'description', type: 'text', meta: { interface: 'input-multiline', width: 'full', sort: 3 }, schema: {} },
    { field: 'category', type: 'string', meta: { interface: 'select-dropdown', width: 'half', sort: 4,
      options: { choices: [
        { text: 'Técnico', value: 'technical' },
        { text: 'Financeiro', value: 'financial' },
        { text: 'Regulatório', value: 'regulatory' },
        { text: 'Mercado', value: 'market' },
        { text: 'Equipe', value: 'team' },
        { text: 'Fornecedor', value: 'supplier' },
        { text: 'IP/Patente', value: 'ip' },
      ] } }, schema: {} },
    { field: 'probability', type: 'string', meta: { interface: 'select-dropdown', width: 'half', sort: 5,
      options: { choices: [
        { text: 'Muito Alta (>80%)', value: 'very_high' },
        { text: 'Alta (60-80%)', value: 'high' },
        { text: 'Média (40-60%)', value: 'medium' },
        { text: 'Baixa (20-40%)', value: 'low' },
        { text: 'Muito Baixa (<20%)', value: 'very_low' },
      ] } }, schema: { default_value: 'medium' } },
    { field: 'impact', type: 'string', meta: { interface: 'select-dropdown', width: 'half', sort: 6,
      options: { choices: [
        { text: 'Catastrófico', value: 'catastrophic' },
        { text: 'Alto', value: 'high' },
        { text: 'Médio', value: 'medium' },
        { text: 'Baixo', value: 'low' },
        { text: 'Insignificante', value: 'negligible' },
      ] } }, schema: { default_value: 'medium' } },
    { field: 'risk_score', type: 'integer', meta: { interface: 'input', width: 'half', sort: 7, note: 'Prob x Impacto (1-25)' }, schema: {} },
    { field: 'mitigation_plan', type: 'text', meta: { interface: 'input-rich-text-md', width: 'full', sort: 8 }, schema: {} },
    { field: 'contingency_plan', type: 'text', meta: { interface: 'input-rich-text-md', width: 'full', sort: 9 }, schema: {} },
    { field: 'owner', type: 'string', meta: { interface: 'input', width: 'half', sort: 10 }, schema: {} },
    { field: 'phase_id', type: 'integer', meta: { interface: 'select-dropdown-m2o', width: 'half', sort: 11 }, schema: {} },
    SORT_FIELD,
  ]);

  // ── 1.5 Stakeholders (Partes Interessadas) ──
  await createCollection('claravis_stakeholders', {
    group: 'claravis_pmi',
    icon: 'people',
    note: 'Stakeholders — mapa de partes interessadas (PMI)',
    sort: 5,
  }, [
    STATUS_FIELD([
      { text: 'Ativo', value: 'active' },
      { text: 'Potencial', value: 'potential' },
      { text: 'Inativo', value: 'inactive' },
    ], 'active'),
    { field: 'name', type: 'string', meta: { interface: 'input', required: true, width: 'half', sort: 2 }, schema: { is_nullable: false } },
    { field: 'organization', type: 'string', meta: { interface: 'input', width: 'half', sort: 3 }, schema: {} },
    { field: 'role', type: 'string', meta: { interface: 'input', width: 'half', sort: 4, note: 'Papel no projeto' }, schema: {} },
    { field: 'category', type: 'string', meta: { interface: 'select-dropdown', width: 'half', sort: 5,
      options: { choices: [
        { text: 'Investidor', value: 'investor' },
        { text: 'Patrocinador', value: 'sponsor' },
        { text: 'Parceiro Acadêmico', value: 'academic' },
        { text: 'Fornecedor', value: 'supplier' },
        { text: 'Mentor/Advisor', value: 'mentor' },
        { text: 'Usuário Beta', value: 'beta_user' },
        { text: 'Instituição Pública', value: 'public' },
        { text: 'Mídia', value: 'media' },
        { text: 'Equipe', value: 'team' },
      ] } }, schema: {} },
    { field: 'influence', type: 'string', meta: { interface: 'select-dropdown', width: 'half', sort: 6,
      options: { choices: [
        { text: 'Alta', value: 'high' },
        { text: 'Média', value: 'medium' },
        { text: 'Baixa', value: 'low' },
      ] } }, schema: { default_value: 'medium' } },
    { field: 'interest', type: 'string', meta: { interface: 'select-dropdown', width: 'half', sort: 7,
      options: { choices: [
        { text: 'Alto', value: 'high' },
        { text: 'Médio', value: 'medium' },
        { text: 'Baixo', value: 'low' },
      ] } }, schema: { default_value: 'medium' } },
    { field: 'engagement_strategy', type: 'text', meta: { interface: 'input-multiline', width: 'full', sort: 8, note: 'Como engajar (Manage Closely / Keep Satisfied / Keep Informed / Monitor)' }, schema: {} },
    { field: 'email', type: 'string', meta: { interface: 'input', width: 'half', sort: 9 }, schema: {} },
    { field: 'phone', type: 'string', meta: { interface: 'input', width: 'half', sort: 10 }, schema: {} },
    { field: 'notes', type: 'text', meta: { interface: 'input-multiline', width: 'full', sort: 11 }, schema: {} },
    SORT_FIELD,
  ]);

  // ── 1.6 Custos / Budget Items (Cost Management) ──
  await createCollection('claravis_costs', {
    group: 'claravis_pmi',
    icon: 'payments',
    note: 'Controle de custos — itens de orçamento',
    sort: 6,
  }, [
    STATUS_FIELD([
      { text: 'Planejado', value: 'planned' },
      { text: 'Aprovado', value: 'approved' },
      { text: 'Comprado', value: 'purchased' },
      { text: 'Pago', value: 'paid' },
      { text: 'Cancelado', value: 'cancelled' },
    ], 'planned'),
    { field: 'phase_id', type: 'integer', meta: { interface: 'select-dropdown-m2o', width: 'half', sort: 2 }, schema: {} },
    { field: 'category', type: 'string', meta: { interface: 'select-dropdown', width: 'half', sort: 3,
      options: { choices: [
        { text: 'Hardware', value: 'hardware' },
        { text: 'Software/Licença', value: 'software' },
        { text: 'Cloud/Compute', value: 'cloud' },
        { text: 'Prototipagem', value: 'prototyping' },
        { text: 'Jurídico/PI', value: 'legal' },
        { text: 'Marketing', value: 'marketing' },
        { text: 'Viagem', value: 'travel' },
        { text: 'Pessoal', value: 'personnel' },
        { text: 'Outro', value: 'other' },
      ] } }, schema: {} },
    { field: 'item', type: 'string', meta: { interface: 'input', required: true, width: 'full', sort: 4 }, schema: { is_nullable: false } },
    { field: 'description', type: 'text', meta: { interface: 'input-multiline', width: 'full', sort: 5 }, schema: {} },
    { field: 'vendor', type: 'string', meta: { interface: 'input', width: 'half', sort: 6 }, schema: {} },
    { field: 'amount_brl', type: 'float', meta: { interface: 'input', required: true, width: 'half', sort: 7 }, schema: { default_value: 0 } },
    { field: 'amount_usd', type: 'float', meta: { interface: 'input', width: 'half', sort: 8 }, schema: {} },
    { field: 'purchase_date', type: 'date', meta: { interface: 'datetime', width: 'half', sort: 9 }, schema: {} },
    { field: 'invoice_url', type: 'string', meta: { interface: 'input', width: 'full', sort: 10, note: 'Link para NF/recibo' }, schema: {} },
    { field: 'tags', type: 'json', meta: { interface: 'tags', width: 'half', sort: 11 }, schema: {} },
    SORT_FIELD,
  ]);

  // ── 1.7 Activity Log / Diário de Projeto ──
  await createCollection('claravis_activity_log', {
    group: 'claravis_pmi',
    icon: 'history',
    note: 'Diário de bordo — registro de atividades, decisões e aprendizados',
    sort: 7,
  }, [
    { field: 'date', type: 'date', meta: { interface: 'datetime', required: true, width: 'half', sort: 1 }, schema: { is_nullable: false } },
    { field: 'type', type: 'string', meta: { interface: 'select-dropdown', width: 'half', sort: 2,
      options: { choices: [
        { text: 'Desenvolvimento', value: 'dev' },
        { text: 'Decisão', value: 'decision' },
        { text: 'Treinamento IA', value: 'training' },
        { text: 'Teste', value: 'test' },
        { text: 'Reunião', value: 'meeting' },
        { text: 'Pesquisa', value: 'research' },
        { text: 'Compra', value: 'purchase' },
        { text: 'Bug/Issue', value: 'issue' },
        { text: 'Marco', value: 'milestone' },
      ] } }, schema: { default_value: 'dev' } },
    { field: 'title', type: 'string', meta: { interface: 'input', required: true, width: 'full', sort: 3 }, schema: { is_nullable: false } },
    { field: 'content', type: 'text', meta: { interface: 'input-rich-text-md', width: 'full', sort: 4 }, schema: {} },
    { field: 'phase_id', type: 'integer', meta: { interface: 'select-dropdown-m2o', width: 'half', sort: 5 }, schema: {} },
    { field: 'deliverable_id', type: 'integer', meta: { interface: 'select-dropdown-m2o', width: 'half', sort: 6 }, schema: {} },
    { field: 'tags', type: 'json', meta: { interface: 'tags', width: 'half', sort: 7 }, schema: {} },
    { field: 'attachments', type: 'json', meta: { interface: 'list', width: 'full', sort: 8 }, schema: {} },
    SORT_FIELD,
  ]);


  // ═══════════════════════════════════════════════════
  // PARTE 2: PRODUTO (Features, Roadmap, Benchmarks)
  // ═══════════════════════════════════════════════════

  // ── 2.1 Features (Mapa de Features) ──
  await createCollection('claravis_features', {
    group: 'claravis_produto',
    icon: 'auto_awesome',
    note: 'Catálogo de features — mapa completo do produto',
    sort: 1,
  }, [
    STATUS_FIELD([
      { text: 'Essencial', value: 'essential' },
      { text: 'Interessante', value: 'interesting' },
      { text: 'Futuro', value: 'future' },
      { text: 'Descartado', value: 'discarded' },
      { text: 'Implementado', value: 'implemented' },
    ], 'essential'),
    PRIORITY_FIELD,
    { field: 'category', type: 'string', meta: { interface: 'select-dropdown', width: 'half', sort: 3,
      options: { choices: [
        { text: 'Detecção de Objetos', value: 'detection' },
        { text: 'Profundidade/Distância', value: 'depth' },
        { text: 'Áudio/Sonificação', value: 'audio' },
        { text: 'Haptico', value: 'haptic' },
        { text: 'Display AR/Visual', value: 'display' },
        { text: 'VLM/Descrição', value: 'vlm' },
        { text: 'OCR/Leitura', value: 'ocr' },
        { text: 'Face Recognition', value: 'face' },
        { text: 'Navegação/SLAM', value: 'navigation' },
        { text: 'Thermal/Night', value: 'thermal' },
        { text: 'Hardware', value: 'hardware' },
        { text: 'IoT/Smart Home', value: 'iot' },
        { text: 'Tracking', value: 'tracking' },
      ] } }, schema: {} },
    { field: 'name', type: 'string', meta: { interface: 'input', required: true, width: 'full', sort: 4 }, schema: { is_nullable: false } },
    { field: 'description', type: 'text', meta: { interface: 'input-multiline', width: 'full', sort: 5 }, schema: {} },
    { field: 'how_to_implement', type: 'text', meta: { interface: 'input-rich-text-md', width: 'full', sort: 6, note: 'Como implementar — ferramentas, libs, papers' }, schema: {} },
    { field: 'competitors_who_have', type: 'json', meta: { interface: 'tags', width: 'half', sort: 7, note: 'Concorrentes que já têm' }, schema: {} },
    { field: 'open_source_url', type: 'string', meta: { interface: 'input', width: 'half', sort: 8, note: 'GitHub/repo principal' }, schema: {} },
    { field: 'reference_ids', type: 'json', meta: { interface: 'tags', width: 'half', sort: 9, note: 'IDs de referência (REF-XX)' }, schema: {} },
    { field: 'roadmap_phase', type: 'string', meta: { interface: 'select-dropdown', width: 'half', sort: 10,
      options: { choices: [
        { text: 'Fase 1 — Software (0-3m)', value: 'phase_1' },
        { text: 'Fase 2 — Jetson+Display (3-12m)', value: 'phase_2' },
        { text: 'Fase 3 — Sensores (12-24m)', value: 'phase_3' },
        { text: 'Fase 4 — Glasses (24-36m)', value: 'phase_4' },
      ] } }, schema: {} },
    { field: 'estimated_cost_brl', type: 'float', meta: { interface: 'input', width: 'half', sort: 11 }, schema: { default_value: 0 } },
    { field: 'complexity', type: 'string', meta: { interface: 'select-dropdown', width: 'half', sort: 12,
      options: { choices: [
        { text: 'Baixa', value: 'low' },
        { text: 'Média', value: 'medium' },
        { text: 'Alta', value: 'high' },
        { text: 'Muito Alta', value: 'very_high' },
      ] } }, schema: {} },
    { field: 'justification', type: 'text', meta: { interface: 'input-multiline', width: 'full', sort: 13, note: 'Por que incluir/excluir' }, schema: {} },
    SORT_FIELD,
  ]);

  // ── 2.2 Competitors (Análise de Concorrentes) ──
  await createCollection('claravis_competitors', {
    group: 'claravis_produto',
    icon: 'groups',
    note: 'Análise detalhada de concorrentes',
    sort: 2,
  }, [
    STATUS_FIELD([
      { text: 'Ativo', value: 'active' },
      { text: 'Em Declínio', value: 'declining' },
      { text: 'Descontinuado', value: 'discontinued' },
      { text: 'Pré-lançamento', value: 'pre_launch' },
    ], 'active'),
    { field: 'name', type: 'string', meta: { interface: 'input', required: true, width: 'half', sort: 2 }, schema: { is_nullable: false } },
    { field: 'company', type: 'string', meta: { interface: 'input', width: 'half', sort: 3 }, schema: {} },
    { field: 'country', type: 'string', meta: { interface: 'input', width: 'half', sort: 4 }, schema: {} },
    { field: 'form_factor', type: 'string', meta: { interface: 'select-dropdown', width: 'half', sort: 5,
      options: { choices: [
        { text: 'Smart Glasses', value: 'glasses' },
        { text: 'Clip-on', value: 'clipon' },
        { text: 'Headset VR/AR', value: 'headset' },
        { text: 'App Mobile', value: 'app' },
        { text: 'Smart Cane', value: 'cane' },
        { text: 'Robot Guide', value: 'robot' },
        { text: 'Smartphone', value: 'smartphone' },
        { text: 'Braille Device', value: 'braille' },
        { text: 'Outro', value: 'other' },
      ] } }, schema: {} },
    { field: 'price_usd', type: 'string', meta: { interface: 'input', width: 'half', sort: 6, note: 'Preço ou range' }, schema: {} },
    { field: 'pricing_model', type: 'string', meta: { interface: 'select-dropdown', width: 'half', sort: 7,
      options: { choices: [
        { text: 'Compra Única', value: 'one_time' },
        { text: 'Assinatura', value: 'subscription' },
        { text: 'Freemium', value: 'freemium' },
        { text: 'Grátis', value: 'free' },
        { text: 'Híbrido', value: 'hybrid' },
      ] } }, schema: {} },
    { field: 'hardware_specs', type: 'text', meta: { interface: 'input-rich-text-md', width: 'full', sort: 8 }, schema: {} },
    { field: 'ai_capabilities', type: 'text', meta: { interface: 'input-rich-text-md', width: 'full', sort: 9 }, schema: {} },
    { field: 'has_ocr', type: 'boolean', meta: { interface: 'boolean', width: 'quarter', sort: 10 }, schema: { default_value: false } },
    { field: 'has_scene_desc', type: 'boolean', meta: { interface: 'boolean', width: 'quarter', sort: 11 }, schema: { default_value: false } },
    { field: 'has_face_recog', type: 'boolean', meta: { interface: 'boolean', width: 'quarter', sort: 12 }, schema: { default_value: false } },
    { field: 'has_navigation', type: 'boolean', meta: { interface: 'boolean', width: 'quarter', sort: 13 }, schema: { default_value: false } },
    { field: 'has_ar_overlay', type: 'boolean', meta: { interface: 'boolean', width: 'quarter', sort: 14 }, schema: { default_value: false } },
    { field: 'has_offline', type: 'boolean', meta: { interface: 'boolean', width: 'quarter', sort: 15 }, schema: { default_value: false } },
    { field: 'has_haptic', type: 'boolean', meta: { interface: 'boolean', width: 'quarter', sort: 16 }, schema: { default_value: false } },
    { field: 'has_spatial_audio', type: 'boolean', meta: { interface: 'boolean', width: 'quarter', sort: 17 }, schema: { default_value: false } },
    { field: 'strengths', type: 'text', meta: { interface: 'input-multiline', width: 'half', sort: 18 }, schema: {} },
    { field: 'weaknesses', type: 'text', meta: { interface: 'input-multiline', width: 'half', sort: 19 }, schema: {} },
    { field: 'funding', type: 'string', meta: { interface: 'input', width: 'half', sort: 20, note: 'Funding recebido' }, schema: {} },
    { field: 'url', type: 'string', meta: { interface: 'input', width: 'half', sort: 21 }, schema: {} },
    { field: 'reference_ids', type: 'json', meta: { interface: 'tags', width: 'half', sort: 22 }, schema: {} },
    { field: 'notes', type: 'text', meta: { interface: 'input-multiline', width: 'full', sort: 23 }, schema: {} },
    SORT_FIELD,
  ]);

  // ── 2.3 Training Runs (Treinos do Modelo) ──
  await createCollection('claravis_training_runs', {
    group: 'claravis_produto',
    icon: 'model_training',
    note: 'Histórico de treinos do modelo YOLO',
    sort: 3,
  }, [
    STATUS_FIELD([
      { text: 'Planejado', value: 'planned' },
      { text: 'Rodando', value: 'running' },
      { text: 'Concluído', value: 'completed' },
      { text: 'Falhou', value: 'failed' },
      { text: 'Cancelado', value: 'cancelled' },
    ], 'planned'),
    { field: 'version', type: 'string', meta: { interface: 'input', required: true, width: 'half', sort: 2, note: 'Ex: v1, v2, v3' }, schema: { is_nullable: false } },
    { field: 'model_arch', type: 'string', meta: { interface: 'input', width: 'half', sort: 3, note: 'Ex: YOLOv8s, YOLO11n' }, schema: {} },
    { field: 'num_classes', type: 'integer', meta: { interface: 'input', width: 'half', sort: 4 }, schema: {} },
    { field: 'num_images', type: 'integer', meta: { interface: 'input', width: 'half', sort: 5 }, schema: {} },
    { field: 'num_datasets', type: 'integer', meta: { interface: 'input', width: 'half', sort: 6 }, schema: {} },
    { field: 'image_size', type: 'integer', meta: { interface: 'input', width: 'half', sort: 7, note: 'Ex: 416, 640' }, schema: {} },
    { field: 'epochs_planned', type: 'integer', meta: { interface: 'input', width: 'half', sort: 8 }, schema: {} },
    { field: 'epochs_completed', type: 'integer', meta: { interface: 'input', width: 'half', sort: 9 }, schema: {} },
    { field: 'gpu', type: 'string', meta: { interface: 'input', width: 'half', sort: 10, note: 'Ex: A100, T4, V100' }, schema: {} },
    { field: 'platform', type: 'string', meta: { interface: 'input', width: 'half', sort: 11, note: 'Ex: Colab Pro, Local' }, schema: {} },
    { field: 'map50', type: 'float', meta: { interface: 'input', width: 'half', sort: 12, note: 'mAP@50' }, schema: {} },
    { field: 'precision', type: 'float', meta: { interface: 'input', width: 'half', sort: 13 }, schema: {} },
    { field: 'recall', type: 'float', meta: { interface: 'input', width: 'half', sort: 14 }, schema: {} },
    { field: 'inference_ms', type: 'float', meta: { interface: 'input', width: 'half', sort: 15, note: 'ms por frame no target device' }, schema: {} },
    { field: 'model_file', type: 'string', meta: { interface: 'input', width: 'full', sort: 16, note: 'Nome do arquivo .tflite' }, schema: {} },
    { field: 'classes_list', type: 'json', meta: { interface: 'tags', width: 'full', sort: 17 }, schema: {} },
    { field: 'datasets_used', type: 'json', meta: { interface: 'tags', width: 'full', sort: 18 }, schema: {} },
    { field: 'notes', type: 'text', meta: { interface: 'input-rich-text-md', width: 'full', sort: 19 }, schema: {} },
    { field: 'started_at', type: 'timestamp', meta: { interface: 'datetime', width: 'half', sort: 20 }, schema: {} },
    { field: 'completed_at', type: 'timestamp', meta: { interface: 'datetime', width: 'half', sort: 21 }, schema: {} },
    SORT_FIELD,
  ]);

  // ── 2.4 References (Referências Bibliográficas) ──
  await createCollection('claravis_references', {
    group: 'claravis_produto',
    icon: 'menu_book',
    note: 'Referências bibliográficas e fontes — controle REFERENCIAS.md',
    sort: 4,
  }, [
    { field: 'ref_id', type: 'string', meta: { interface: 'input', required: true, width: 'half', sort: 1, note: 'Ex: REF-01, REF-60' }, schema: { is_unique: true, is_nullable: false } },
    { field: 'category', type: 'string', meta: { interface: 'select-dropdown', width: 'half', sort: 2,
      options: { choices: [
        { text: 'Epidemiologia', value: 'epidemiology' },
        { text: 'Mercado', value: 'market' },
        { text: 'Concorrente', value: 'competitor' },
        { text: 'Marketing/Framework', value: 'marketing' },
        { text: 'Investimento', value: 'investment' },
        { text: 'Legislação', value: 'legislation' },
        { text: 'Detecção/IA', value: 'detection_ai' },
        { text: 'Profundidade', value: 'depth' },
        { text: 'Áudio', value: 'audio' },
        { text: 'Háptico', value: 'haptic' },
        { text: 'AR/Display', value: 'display' },
        { text: 'VLM', value: 'vlm' },
        { text: 'OCR/Face', value: 'ocr_face' },
        { text: 'Navegação/SLAM', value: 'navigation' },
        { text: 'Hardware', value: 'hardware' },
        { text: 'Pesquisa BR', value: 'brazil_research' },
      ] } }, schema: {} },
    { field: 'title', type: 'string', meta: { interface: 'input', required: true, width: 'full', sort: 3 }, schema: { is_nullable: false } },
    { field: 'authors', type: 'string', meta: { interface: 'input', width: 'full', sort: 4 }, schema: {} },
    { field: 'year', type: 'integer', meta: { interface: 'input', width: 'half', sort: 5 }, schema: {} },
    { field: 'venue', type: 'string', meta: { interface: 'input', width: 'half', sort: 6, note: 'Conferência, journal, etc.' }, schema: {} },
    { field: 'url', type: 'string', meta: { interface: 'input', width: 'full', sort: 7 }, schema: {} },
    { field: 'github_url', type: 'string', meta: { interface: 'input', width: 'half', sort: 8 }, schema: {} },
    { field: 'key_data', type: 'text', meta: { interface: 'input-multiline', width: 'full', sort: 9, note: 'Dados-chave extraídos' }, schema: {} },
    { field: 'verified', type: 'boolean', meta: { interface: 'boolean', width: 'half', sort: 10 }, schema: { default_value: false } },
    { field: 'verified_date', type: 'date', meta: { interface: 'datetime', width: 'half', sort: 11 }, schema: {} },
    SORT_FIELD,
  ]);


  // ═══════════════════════════════════════════════════
  // PARTE 3: SITE & BLOG
  // ═══════════════════════════════════════════════════

  // ── 3.1 Blog Posts ──
  await createCollection('claravis_blog_posts', {
    group: 'claravis_site',
    icon: 'article',
    note: 'Posts do blog ClaraVis',
    sort: 1,
    archive_field: 'status',
    archive_value: 'archived',
    unarchive_value: 'draft',
  }, [
    STATUS_FIELD([
      { text: 'Publicado', value: 'published' },
      { text: 'Rascunho', value: 'draft' },
      { text: 'Arquivado', value: 'archived' },
    ], 'draft'),
    { field: 'title', type: 'string', meta: { interface: 'input', required: true, width: 'full', sort: 2 }, schema: { is_nullable: false } },
    { field: 'slug', type: 'string', meta: { interface: 'input', required: true, width: 'half', sort: 3 }, schema: { is_unique: true, is_nullable: false } },
    { field: 'excerpt', type: 'text', meta: { interface: 'input-multiline', width: 'full', sort: 4 }, schema: {} },
    { field: 'content', type: 'text', meta: { interface: 'input-rich-text-md', width: 'full', sort: 5 }, schema: {} },
    { field: 'featured_image', type: 'uuid', meta: { interface: 'file-image', width: 'half', sort: 6 }, schema: {} },
    { field: 'author', type: 'string', meta: { interface: 'input', width: 'half', sort: 7 }, schema: { default_value: 'Daniel Valladares' } },
    { field: 'category', type: 'string', meta: { interface: 'select-dropdown', width: 'half', sort: 8,
      options: { choices: [
        { text: 'Desenvolvimento', value: 'development' },
        { text: 'IA & Visão Computacional', value: 'ai' },
        { text: 'Acessibilidade', value: 'accessibility' },
        { text: 'Hardware', value: 'hardware' },
        { text: 'Mercado', value: 'market' },
        { text: 'Pesquisa', value: 'research' },
        { text: 'Novidades', value: 'news' },
      ] } }, schema: {} },
    { field: 'tags', type: 'json', meta: { interface: 'tags', width: 'half', sort: 9 }, schema: {} },
    { field: 'locale', type: 'string', meta: { interface: 'select-dropdown', width: 'half', sort: 10,
      options: { choices: [
        { text: 'Português', value: 'pt-BR' },
        { text: 'English', value: 'en' },
        { text: 'Español', value: 'es' },
      ] } }, schema: { default_value: 'pt-BR' } },
    { field: 'seo_title', type: 'string', meta: { interface: 'input', width: 'half', sort: 11, note: 'Max 60 chars' }, schema: {} },
    { field: 'seo_description', type: 'string', meta: { interface: 'input', width: 'half', sort: 12, note: 'Max 160 chars' }, schema: {} },
    { field: 'reading_time', type: 'integer', meta: { interface: 'input', width: 'half', sort: 13 }, schema: { default_value: 5 } },
    { field: 'published_at', type: 'timestamp', meta: { interface: 'datetime', width: 'half', sort: 14 }, schema: {} },
  ]);

  // ── 3.2 Pages (Conteúdo dinâmico do site) ──
  await createCollection('claravis_pages', {
    group: 'claravis_site',
    icon: 'web',
    note: 'Conteúdo dinâmico por página/seção do site ClaraVis',
    sort: 2,
  }, [
    { field: 'page_slug', type: 'string', meta: { interface: 'input', required: true, width: 'half', sort: 1, note: 'Ex: home, about, product, pricing' }, schema: { is_nullable: false } },
    { field: 'section_key', type: 'string', meta: { interface: 'input', required: true, width: 'half', sort: 2, note: 'Ex: hero, features, cta' }, schema: { is_nullable: false } },
    { field: 'locale', type: 'string', meta: { interface: 'select-dropdown', width: 'half', sort: 3,
      options: { choices: [
        { text: 'Português', value: 'pt-BR' },
        { text: 'English', value: 'en' },
        { text: 'Español', value: 'es' },
      ] } }, schema: { default_value: 'pt-BR' } },
    { field: 'title', type: 'string', meta: { interface: 'input', width: 'full', sort: 4 }, schema: {} },
    { field: 'subtitle', type: 'string', meta: { interface: 'input', width: 'full', sort: 5 }, schema: {} },
    { field: 'body', type: 'text', meta: { interface: 'input-rich-text-md', width: 'full', sort: 6 }, schema: {} },
    { field: 'data', type: 'json', meta: { interface: 'input-code', options: { language: 'json' }, width: 'full', sort: 7 }, schema: {} },
    { field: 'cta_text', type: 'string', meta: { interface: 'input', width: 'half', sort: 8 }, schema: {} },
    { field: 'cta_url', type: 'string', meta: { interface: 'input', width: 'half', sort: 9 }, schema: {} },
    { field: 'image', type: 'uuid', meta: { interface: 'file-image', width: 'half', sort: 10 }, schema: {} },
    SORT_FIELD,
  ]);

  // ── 3.3 FAQ ──
  await createCollection('claravis_faq', {
    group: 'claravis_site',
    icon: 'help_outline',
    note: 'FAQ do site ClaraVis',
    sort: 3,
  }, [
    { field: 'page_slug', type: 'string', meta: { interface: 'input', required: true, width: 'half', sort: 1 }, schema: { is_nullable: false } },
    { field: 'question', type: 'string', meta: { interface: 'input', required: true, width: 'full', sort: 2 }, schema: { is_nullable: false } },
    { field: 'answer', type: 'text', meta: { interface: 'input-rich-text-md', width: 'full', sort: 3 }, schema: { is_nullable: false } },
    { field: 'locale', type: 'string', meta: { interface: 'select-dropdown', width: 'half', sort: 4,
      options: { choices: [
        { text: 'Português', value: 'pt-BR' },
        { text: 'English', value: 'en' },
        { text: 'Español', value: 'es' },
      ] } }, schema: { default_value: 'pt-BR' } },
    SORT_FIELD,
  ]);

  // ── 3.4 Testimonials / Early Adopters ──
  await createCollection('claravis_testimonials', {
    group: 'claravis_site',
    icon: 'format_quote',
    note: 'Depoimentos de beta testers e early adopters',
    sort: 4,
  }, [
    { field: 'name', type: 'string', meta: { interface: 'input', required: true, width: 'half', sort: 1 }, schema: { is_nullable: false } },
    { field: 'role', type: 'string', meta: { interface: 'input', width: 'half', sort: 2, note: 'Ex: Beta Tester, Oftalmologista' }, schema: {} },
    { field: 'photo', type: 'uuid', meta: { interface: 'file-image', width: 'half', sort: 3 }, schema: {} },
    { field: 'testimonial', type: 'text', meta: { interface: 'input-multiline', required: true, width: 'full', sort: 4 }, schema: { is_nullable: false } },
    { field: 'rating', type: 'integer', meta: { interface: 'select-dropdown', width: 'half', sort: 5,
      options: { choices: [{ text: '5', value: 5 }, { text: '4', value: 4 }, { text: '3', value: 3 }] } }, schema: { default_value: 5 } },
    { field: 'featured', type: 'boolean', meta: { interface: 'boolean', width: 'half', sort: 6 }, schema: { default_value: false } },
    { field: 'locale', type: 'string', meta: { interface: 'select-dropdown', width: 'half', sort: 7,
      options: { choices: [
        { text: 'Português', value: 'pt-BR' },
        { text: 'English', value: 'en' },
        { text: 'Español', value: 'es' },
      ] } }, schema: { default_value: 'pt-BR' } },
    SORT_FIELD,
  ]);


  // ═══════════════════════════════════════════════════
  // RELATIONS
  // ═══════════════════════════════════════════════════

  // Deliverables → Phases
  await createRelation('claravis_deliverables', 'phase_id', 'claravis_phases', { one_field: 'deliverables' });

  // Milestones → Phases
  await createRelation('claravis_milestones', 'phase_id', 'claravis_phases', { one_field: 'milestones' });

  // Risks → Phases
  await createRelation('claravis_risks', 'phase_id', 'claravis_phases', { one_field: 'risks' });

  // Costs → Phases
  await createRelation('claravis_costs', 'phase_id', 'claravis_phases', { one_field: 'costs' });

  // Activity Log → Phases
  await createRelation('claravis_activity_log', 'phase_id', 'claravis_phases', { one_field: 'activity_logs' });

  // Activity Log → Deliverables
  await createRelation('claravis_activity_log', 'deliverable_id', 'claravis_deliverables', { one_field: 'activity_logs' });


  console.log('\n\n✅ ClaraVis collections created successfully!');
  console.log('📊 Total: 3 folders + 15 collections + 6 relations');
  console.log('🔗 Access: https://project.resultadosexponenciais.com.br');
}

main().catch(console.error);
