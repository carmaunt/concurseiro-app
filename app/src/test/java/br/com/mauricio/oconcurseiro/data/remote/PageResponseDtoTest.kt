package br.com.mauricio.oconcurseiro.data.remote

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PageResponseDtoTest {

    @Test
    fun `resolvedTotalElements usa totalElements quando page e nulo`() {
        val page = PageResponse<String>(
            content = listOf("item"),
            totalElements = 42L,
            totalPages = 5,
            page = null
        )
        assertEquals(42L, page.resolvedTotalElements)
    }

    @Test
    fun `resolvedTotalElements prefere page totalElements quando page existe`() {
        val pageInfo = PageInfo(size = 1, number = 0, totalElements = 99L, totalPages = 99)
        val page = PageResponse<String>(
            content = listOf("item"),
            totalElements = 42L,
            page = pageInfo
        )
        assertEquals(99L, page.resolvedTotalElements)
    }

    @Test
    fun `resolvedTotalPages usa totalPages quando page e nulo`() {
        val page = PageResponse<String>(
            content = emptyList(),
            totalPages = 10,
            page = null
        )
        assertEquals(10, page.resolvedTotalPages)
    }

    @Test
    fun `resolvedTotalPages prefere page totalPages quando page existe`() {
        val pageInfo = PageInfo(size = 1, number = 0, totalElements = 50L, totalPages = 50)
        val page = PageResponse<String>(
            content = emptyList(),
            totalPages = 10,
            page = pageInfo
        )
        assertEquals(50, page.resolvedTotalPages)
    }

    @Test
    fun `resolvedLast e true quando ultima pagina sem page info`() {
        val page = PageResponse<String>(content = emptyList(), last = true, page = null)
        assertTrue(page.resolvedLast)
    }

    @Test
    fun `resolvedLast e false quando nao e ultima pagina sem page info`() {
        val page = PageResponse<String>(content = emptyList(), last = false, page = null)
        assertFalse(page.resolvedLast)
    }

    @Test
    fun `resolvedLast calcula corretamente via page info`() {
        val pageInfo = PageInfo(size = 1, number = 2, totalElements = 3L, totalPages = 3)
        val page = PageResponse<String>(content = emptyList(), last = false, page = pageInfo)
        assertTrue(page.resolvedLast)
    }

    @Test
    fun `resolvedLast e false quando ainda ha paginas via page info`() {
        val pageInfo = PageInfo(size = 1, number = 0, totalElements = 3L, totalPages = 3)
        val page = PageResponse<String>(content = emptyList(), last = true, page = pageInfo)
        assertFalse(page.resolvedLast)
    }
}
