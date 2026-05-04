package br.com.mauricio.oconcurseiro.data.di

import br.com.mauricio.oconcurseiro.data.repository.QuestaoRepository
import br.com.mauricio.oconcurseiro.data.repository.RespostaRepository
import br.com.mauricio.oconcurseiro.domain.repository.QuestaoRepositoryContract
import br.com.mauricio.oconcurseiro.domain.repository.RespostaRepositoryContract
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindQuestaoRepository(
        repository: QuestaoRepository
    ): QuestaoRepositoryContract

    @Binds
    @Singleton
    abstract fun bindRespostaRepository(
        repository: RespostaRepository
    ): RespostaRepositoryContract
}