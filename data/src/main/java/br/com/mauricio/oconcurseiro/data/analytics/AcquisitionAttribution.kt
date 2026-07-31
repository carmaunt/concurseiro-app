package br.com.mauricio.oconcurseiro.data.analytics

object AcquisitionAttribution {
    private val allowedId = Regex("^[A-Za-z0-9][A-Za-z0-9._:-]{7,159}$")

    fun normalize(value: String?): String? = value
        ?.trim()
        ?.takeIf { allowedId.matches(it) }

    fun enrich(metadata: Map<String, Any>, acquisitionId: String?): Map<String, Any> {
        val normalized = normalize(acquisitionId) ?: return metadata
        return metadata + (METADATA_KEY to normalized)
    }

    const val METADATA_KEY = "acquisition_id"
    const val PREFERENCE_KEY = "acquisition_id_v1"
}
