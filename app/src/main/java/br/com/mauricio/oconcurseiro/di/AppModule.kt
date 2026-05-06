package br.com.mauricio.oconcurseiro.di

import br.com.mauricio.oconcurseiro.data.auth.AuthRepository
import br.com.mauricio.oconcurseiro.data.auth.TokenStorage
import br.com.mauricio.oconcurseiro.data.local.AppDatabase
import br.com.mauricio.oconcurseiro.data.local.GuestUsageManager
import br.com.mauricio.oconcurseiro.data.local.RespostaDao
import br.com.mauricio.oconcurseiro.data.remote.ConcurseiroApi
import br.com.mauricio.oconcurseiro.domain.repository.ComentarioRepositoryContract
import br.com.mauricio.oconcurseiro.domain.repository.QuestaoRepositoryContract
import br.com.mauricio.oconcurseiro.domain.repository.RespostaRepositoryContract
import br.com.mauricio.oconcurseiro.domain.usecase.BuscarPaginaQuestoesUseCase
import br.com.mauricio.oconcurseiro.domain.usecase.BuscarRespostaAnteriorUseCase
import br.com.mauricio.oconcurseiro.domain.usecase.CarregarCatalogosQuestoesUseCase
import br.com.mauricio.oconcurseiro.domain.usecase.CarregarDesempenhoHomeUseCase
import br.com.mauricio.oconcurseiro.domain.usecase.CriarComentarioUseCase
import br.com.mauricio.oconcurseiro.domain.usecase.CurtirComentarioUseCase
import br.com.mauricio.oconcurseiro.domain.usecase.DescurtirComentarioUseCase
import br.com.mauricio.oconcurseiro.domain.usecase.ListarAssuntosPorDisciplinaUseCase
import br.com.mauricio.oconcurseiro.domain.usecase.ListarComentariosUseCase
import br.com.mauricio.oconcurseiro.domain.usecase.ListarSubAssuntosUseCase
import br.com.mauricio.oconcurseiro.domain.usecase.SalvarRespostaQuestaoUseCase
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
    fun provideSalvarRespostaQuestaoUseCase(
        repository: RespostaRepositoryContract
    ): SalvarRespostaQuestaoUseCase {
        return SalvarRespostaQuestaoUseCase(repository)
    }


    @Provides
    @Singleton
    fun provideCarregarDesempenhoHomeUseCase(
        repository: RespostaRepositoryContract
    ): CarregarDesempenhoHomeUseCase {
        return CarregarDesempenhoHomeUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideBuscarRespostaAnteriorUseCase(
        repository: RespostaRepositoryContract
    ): BuscarRespostaAnteriorUseCase {
        return BuscarRespostaAnteriorUseCase(repository)
    }


    @Provides
    @Singleton
    fun provideListarComentariosUseCase(
        repository: ComentarioRepositoryContract
    ): ListarComentariosUseCase {
        return ListarComentariosUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideCriarComentarioUseCase(
        repository: ComentarioRepositoryContract
    ): CriarComentarioUseCase {
        return CriarComentarioUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideCurtirComentarioUseCase(
        repository: ComentarioRepositoryContract
    ): CurtirComentarioUseCase {
        return CurtirComentarioUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideDescurtirComentarioUseCase(
        repository: ComentarioRepositoryContract
    ): DescurtirComentarioUseCase {
        return DescurtirComentarioUseCase(repository)
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