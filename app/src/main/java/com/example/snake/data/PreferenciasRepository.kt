package com.example.snake.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.snake.model.ConfiguracionPartida
import com.example.snake.model.TIEMPO_MAXIMO_DEFECTO_SEG
import com.example.snake.model.TamanoParrilla
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "preferencias_usuario")

class PreferenciasRepository(private val context: Context) {

    companion object {
        private val KEY_ALIAS          = stringPreferencesKey("alias")
        private val KEY_TAMANO         = stringPreferencesKey("tamano_parrilla")
        private val KEY_CONTROL_TIEMPO = booleanPreferencesKey("control_tiempo")
        private val KEY_TIEMPO_MAX     = intPreferencesKey("tiempo_maximo_seg")
    }

    val configuracion: Flow<ConfiguracionPartida> = context.dataStore.data.map { prefs ->
        val tamanoNom = prefs[KEY_TAMANO] ?: TamanoParrilla.MEDIANA.name
        val tamano    = TamanoParrilla.entries.firstOrNull { it.name == tamanoNom }
            ?: TamanoParrilla.MEDIANA
        ConfiguracionPartida(
            alias           = prefs[KEY_ALIAS]          ?: "",
            tamanoParrilla  = tamano,
            controlTiempo   = prefs[KEY_CONTROL_TIEMPO] ?: false,
            tiempoMaximoSeg = prefs[KEY_TIEMPO_MAX]     ?: TIEMPO_MAXIMO_DEFECTO_SEG
        )
    }

    suspend fun guardar(config: ConfiguracionPartida) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ALIAS]          = config.alias
            prefs[KEY_TAMANO]         = config.tamanoParrilla.name
            prefs[KEY_CONTROL_TIEMPO] = config.controlTiempo
            prefs[KEY_TIEMPO_MAX]     = config.tiempoMaximoSeg
        }
    }
}