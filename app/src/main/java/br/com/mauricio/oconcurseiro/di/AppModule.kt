package br.com.mauricio.oconcurseiro.di

import br.com.mauricio.oconcurseiro.data.auth.AuthRepository
import br.com.mauricio.oconcurseiro.data.auth.TokenStorage
import br.com.mauricio.oconcurseiro.data.local.AppDatabase
import br.com.mauricio.oconcurseiro.data.local.GuestUsageManager
import br.com.mauricio.oconcurseiro.data.local.RespostaDao
import br.com.mauricio.oconcurseiro.data.remote.ConcurseiroApi
import br.com.mauricio.oconcurseiro.domain.repository.QuestaoRepositoryContract
import br.com.mauricio.oconcurseiro.domain.usecase.BuscarPaginaQuestoesUseCase
import br.com.mauricio.oconcurseiro.domain.usecase.CarregarCatalogosQuestoesUseCase
import br.com.mauricio.oconcurseiro.domain.usecase.ListarAssuntosPorDisciplinaUseCase
import br.com.mauricio.oconcurseiro.domain.usecase.ListarSubAssuntosUseCase
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        @dagger.hilt.android.qualifiers.ApplicationContext context: android.content.Context,
        firebaseAuth: FirebaseAuth,
        api: ConcurseiroApi,
        tokenStorage: TokenStorage
    ): AuthRepository {
        return AuthRepository(
            context = context,
            auth = firebaseAuth,
            api = api,
            tokenStorage = tokenStorage
        )
    }

    @Provides
    @Singleton
    fun provideBuscarPaginaQuestoesUseCase(
        repository: QuestaoRepositoryContract
    ): BuscarPaginaQuestoesUseCase {
        return BuscarPaginaQuestoesUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideCarregarCatalogosQuestoesUseCase(
        repository: QuestaoRepositoryContract
    ): CarregarCatalogosQuestoesUseCase {
        return CarregarCatalogosQuestoesUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideListarAssuntosPorDisciplinaUseCase(
        repository: QuestaoRepositoryContract
    ): ListarAssuntosPorDisciplinaUseCase {
        return ListarAssuntosPorDisciplinaUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideListarSubAssuntosUseCase(
        repository: QuestaoRepositoryContract
    ): ListarSubAssuntosUseCase {
        return ListarSubAssuntosUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideDatabase(
        @dagger.hilt.android.qualifiers.ApplicationContext context: android.content.Context
    ): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    fun provideRespostaDao(database: AppDatabase): RespostaDao {
        return database.respostaDao()
    }

    @Provides
    @Singleton
    fun provideGuestUsageManager(
        @dagger.hilt.android.qualifiers.ApplicationContext context: android.content.Context
    ): GuestUsageManager {
        return GuestUsageManager(context)
    }
}