package com.devtech.accahelps.di

import androidx.compose.runtime.Stable
import com.devtech.accahelps.data.repo.DemoQuestionsRepository
import com.devtech.accahelps.data.source.InMemoryDbStore
import com.devtech.accahelps.domain.repo.IQuestionRepository
import com.devtech.accahelps.domain.repo.SyncRepository
import com.devtech.accahelps.domain.store.AppDbStore

@Stable
class AppContainer(
    val repository: IQuestionRepository,
    val appDbHelper: AppDbStore,
    val syncRepository: SyncRepository = SyncRepository(appDataHelper = appDbHelper),
) {

    companion object {
        fun demo(): AppContainer {
            return AppContainer(
                repository = DemoQuestionsRepository(),
                appDbHelper = InMemoryDbStore(),
            )
        }
    }

}
