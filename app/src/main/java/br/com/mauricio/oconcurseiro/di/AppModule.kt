package br.com.mauricio.oconcurseiro.di

import br.com.mauricio.oconcurseiro.data.auth.AuthRepository
import br.com.mauricio.oconcurseiro.data.local.AppDatabase
import br.com.mauricio.oconcurseiro.data.local.GuestUsageManager
import br.com.mauricio.oconcurseiro.data.local.RespostaDao
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import br.com.mauricio.oconcurseiro.data.remote.ConcurseiroApi
import br.com.mauricio.oconcurseiro.data.auth.TokenStorage

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