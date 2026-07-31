package br.com.mauricio.oconcurseiro.data.analytics

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AcquisitionAttributionTest {
    @Test
    fun `accepts opaque portal session id`() {
        assertEquals(
            "web_550e8400-e29b-41d4-a716-446655440000",
            AcquisitionAttribution.normalize(" web_550e8400-e29b-41d4-a716-446655440000 "),
        )
    }

    @Test
    fun `rejects short or unsafe values`() {
        assertNull(AcquisitionAttribution.normalize("short"))
        assertNull(AcquisitionAttribution.normalize("web-session?<script>"))
    }

    @Test
    fun `adds acquisition without removing event metadata`() {
        assertEquals(
            mapOf("correct" to true, "acquisition_id" to "web_session-1234"),
            AcquisitionAttribution.enrich(
                mapOf("correct" to true),
                "web_session-1234",
            ),
        )
    }
}
