package com.claravis.app

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

/**
 * AdaptiveConfidence — Sistema de confiança adaptativa que aprende com o uso.
 *
 * Registra padrões de detecção e ajusta thresholds automaticamente:
 * - Classes frequentemente detectadas sozinhas (sem contexto) = possível falso positivo
 * - Classes detectadas consistentemente em conjunto = confirmação mútua
 * - Thresholds aumentam para classes com alta taxa de falsos positivos
 *
 * Dados são persistidos em SharedPreferences para sobreviver entre sessões.
 */
class AdaptiveConfidence(context: Context) {

    companion object {
        private const val TAG = "ClaraVis-Adaptive"
        private const val PREFS_NAME = "claravis_adaptive"
        private const val BASE_THRESHOLD = 0.35f
        private const val MAX_THRESHOLD = 0.70f
        private const val MIN_THRESHOLD = 0.25f
        private const val LEARNING_RATE = 0.02f  // Quão rápido ajusta
        private const val DECAY_RATE = 0.005f     // Quão rápido esquece penalizações
        private const val SAVE_INTERVAL = 30       // Salvar a cada N frames processados
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // Contadores por classe
    private val detectionCount = IntArray(80)   // Quantas vezes cada classe foi detectada
    private val soloCount = IntArray(80)        // Quantas vezes detectada SOZINHA (sem outras classes)
    private val contextCount = IntArray(80)     // Quantas vezes detectada com classes relacionadas

    // Penalty por classe (0.0 a 0.35) — adicionado ao threshold base
    private val classPenalty = FloatArray(80)

    // Contagem de frames processados para decidir quando salvar
    private var framesSinceLastSave = 0

    // Classes que frequentemente são falsos positivos em contextos de navegação
    private val indoorFalsePosClasses = setOf(
        62, // TV — confundida com janelas, monitores, quadros
        72, // geladeira — confundida com paredes, portas
        57, // sofá — confundido com bancos
        60, // mesa — confundida com bancadas
        68, // micro-ondas — confundido com quadros
        69, // forno — confundido com gavetas
        41, // copo — detectado em qualquer superfície
        64, // mouse — muito pequeno, frequente falso positivo
        65, // controle remoto — muito pequeno, alta falso positiva
        66, // teclado — confundido com grades, telas
        63, // notebook — confundido com livros, telas
    )

    // Classes que CONFIRMAM o contexto (se existem, as detecções são mais confiáveis)
    private val contextConfirmers = mapOf(
        0 to setOf(24, 26, 28, 67),     // pessoa + mochila/bolsa/mala/celular = confirma
        56 to setOf(60),                  // cadeira + mesa = confirma
        2 to setOf(9, 11),               // carro + semáforo/placa = confirma (exterior)
    )

    init {
        loadState()
    }

    /** Retorna o threshold de confiança ajustado para uma classe específica */
    fun getThreshold(classId: Int, cameraOrientation: CameraOrientation): Float {
        if (classId < 0 || classId >= 80) return BASE_THRESHOLD

        var threshold = BASE_THRESHOLD + classPenalty[classId]

        // Ajustar baseado na orientação da câmera
        when (cameraOrientation) {
            CameraOrientation.LOOKING_DOWN -> {
                // Olhando para o chão: relaxar threshold para obstáculos, aumentar para eletrodomésticos
                if (classId in setOf(62, 72, 68, 69)) threshold += 0.15f  // TV, geladeira, etc — improvável no chão
                if (classId in setOf(32, 39, 28, 24)) threshold -= 0.05f  // bola, garrafa, mala, mochila — no chão faz sentido
            }
            CameraOrientation.LOOKING_UP -> {
                // Olhando para cima: relaxar para semáforos/placas, aumentar para objetos de chão
                if (classId in setOf(9, 11)) threshold -= 0.05f   // semáforo, placa — faz sentido
                if (classId in setOf(56, 57, 59, 60)) threshold += 0.15f  // cadeira, sofá, cama, mesa — não no teto
            }
            CameraOrientation.LOOKING_FORWARD -> {
                // Olhando para frente: thresholds normais
            }
        }

        return threshold.coerceIn(MIN_THRESHOLD, MAX_THRESHOLD)
    }

    /** Registra as detecções de um frame para aprendizado */
    fun recordFrame(detections: List<Detection>) {
        if (detections.isEmpty()) return

        val classIds = detections.map { it.classId }.toSet()
        val isSolo = classIds.size == 1

        for (det in detections) {
            val cid = det.classId
            if (cid < 0 || cid >= 80) continue

            detectionCount[cid]++

            if (isSolo) {
                soloCount[cid]++
                // Se uma classe é frequentemente detectada sozinha, pode ser falso positivo
                val soloRatio = soloCount[cid].toFloat() / detectionCount[cid].toFloat()
                if (soloRatio > 0.7f && detectionCount[cid] > 20 && cid in indoorFalsePosClasses) {
                    // Aumentar penalidade
                    classPenalty[cid] = (classPenalty[cid] + LEARNING_RATE).coerceAtMost(MAX_THRESHOLD - BASE_THRESHOLD)
                    if (detectionCount[cid] % 50 == 0) {
                        Log.d(TAG, "Increased penalty for class $cid: ${classPenalty[cid]} (solo ratio: $soloRatio)")
                    }
                }
            } else {
                // Detectada com outras classes — verificar se há confirmação de contexto
                val confirmedBy = contextConfirmers[cid]
                if (confirmedBy != null && classIds.intersect(confirmedBy).isNotEmpty()) {
                    contextCount[cid]++
                    // Reduzir penalidade (a detecção é confirmada pelo contexto)
                    classPenalty[cid] = (classPenalty[cid] - DECAY_RATE).coerceAtLeast(0f)
                }
            }
        }

        framesSinceLastSave++
        if (framesSinceLastSave >= SAVE_INTERVAL) {
            saveState()
            framesSinceLastSave = 0
        }
    }

    /** Aplica decay gradual a todas as penalidades (chamado periodicamente) */
    fun applyDecay() {
        for (i in classPenalty.indices) {
            if (classPenalty[i] > 0f) {
                classPenalty[i] = (classPenalty[i] - DECAY_RATE * 0.5f).coerceAtLeast(0f)
            }
        }
    }

    private fun loadState() {
        try {
            for (i in 0 until 80) {
                classPenalty[i] = prefs.getFloat("penalty_$i", 0f)
                detectionCount[i] = prefs.getInt("det_count_$i", 0)
                soloCount[i] = prefs.getInt("solo_count_$i", 0)
            }
            Log.i(TAG, "Loaded adaptive state. Non-zero penalties: ${classPenalty.count { it > 0.01f }}")
        } catch (e: Exception) {
            Log.w(TAG, "Error loading adaptive state: ${e.message}")
        }
    }

    private fun saveState() {
        try {
            val editor = prefs.edit()
            for (i in 0 until 80) {
                if (classPenalty[i] > 0.001f) {
                    editor.putFloat("penalty_$i", classPenalty[i])
                }
                if (detectionCount[i] > 0) {
                    editor.putInt("det_count_$i", detectionCount[i])
                    editor.putInt("solo_count_$i", soloCount[i])
                }
            }
            editor.apply()
        } catch (e: Exception) {
            Log.w(TAG, "Error saving adaptive state: ${e.message}")
        }
    }

    /** Retorna estatísticas de aprendizado para debug */
    fun getStats(): String {
        val penalized = (0 until 80).filter { classPenalty[it] > 0.01f }
            .map { "class$it: +${"%.2f".format(classPenalty[it])}" }
        return "Adaptive: ${penalized.size} classes penalized. ${penalized.take(5).joinToString(", ")}"
    }

    /** Reseta todo o aprendizado (para debug/configurações) */
    fun reset() {
        classPenalty.fill(0f)
        detectionCount.fill(0)
        soloCount.fill(0)
        contextCount.fill(0)
        prefs.edit().clear().apply()
        Log.i(TAG, "Adaptive state reset")
    }
}
