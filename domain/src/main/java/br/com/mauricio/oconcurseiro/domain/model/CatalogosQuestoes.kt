package br.com.mauricio.oconcurseiro.domain.model

data class CatalogosQuestoes(
    val disciplinas: List<CatalogoItem>,
    val bancas: List<CatalogoItem>,
    val instituicoes: List<CatalogoItem>
)