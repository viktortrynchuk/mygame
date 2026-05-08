package com.example.mygame.views

import androidx.lifecycle.ViewModel
import com.example.mygame.database.OptionRepository
import com.example.mygame.database.persistence_and_game_state.ScenarioEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltViewModel
class HostViewModel @Inject constructor(
    private val repo: OptionRepository
) : ViewModel() {

    suspend fun loadOptionsUi(): List<OptionUi> = withContext(Dispatchers.IO) {
        repo.loadOptions().map { OptionUi(it.scenarioId, it.descr) }
    }

    suspend fun loadOptionsById(): Map<Long, ScenarioEntity> = withContext(Dispatchers.IO) {
        repo.loadOptions().associateBy { it.scenarioId }
    }
}