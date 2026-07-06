package com.droidproger.byedpilight

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class PrefStore(private val context: Context) {
    companion object{
        val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
        val cmdLineKey = stringPreferencesKey("cmdLine")
        val proxyPortKey = intPreferencesKey("proxyPort")
        val dnsKey = stringPreferencesKey("dns")
        val providerKey = stringPreferencesKey("provider")
        val sniHostKey = stringPreferencesKey("sniHost")
        val udpOverTcpKey = booleanPreferencesKey("udpOverTcp")
        val obfuscationKey = booleanPreferencesKey("obfuscation")
        val ipv6key = booleanPreferencesKey("ipv6enable")

        val mobKey = booleanPreferencesKey("autoStartMobile")
        val otherKey = booleanPreferencesKey("autoStartOther")
    }

    val cmdLine: Flow<String> = context.dataStore.data.map{
            preferences -> preferences[cmdLineKey] ?: ""
    }

    suspend fun saveCmdLine(value: String){
        context.dataStore.edit { preference ->
            preference[cmdLineKey] = value }
    }

    val proxyPort: Flow<Int> = context.dataStore.data.map{
            preferences -> preferences[proxyPortKey] ?: 0
    }

    suspend fun saveProxyPort(value: Int){
        context.dataStore.edit { preference ->
            preference[proxyPortKey] = value }
    }

    val dnsIp: Flow<String> = context.dataStore.data.map{
            preferences -> preferences[dnsKey] ?: ""
    }

    suspend fun saveDns(value: String){
        context.dataStore.edit { preference ->
            preference[dnsKey] = value }
    }

    val provider: Flow<String> = context.dataStore.data.map {
            preferences -> preferences[providerKey] ?: "auto"
    }

    suspend fun saveProvider(value: String){
        context.dataStore.edit { preference ->
            preference[providerKey] = value }
    }

    val sniHost: Flow<String> = context.dataStore.data.map {
            preferences -> preferences[sniHostKey] ?: ""
    }

    suspend fun saveSniHost(value: String){
        context.dataStore.edit { preference ->
            preference[sniHostKey] = value }
    }

    val udpOverTcp: Flow<Boolean> = context.dataStore.data.map {
            preferences -> preferences[udpOverTcpKey] ?: false
    }

    suspend fun saveUdpOverTcp(value: Boolean){
        context.dataStore.edit { preference ->
            preference[udpOverTcpKey] = value }
    }

    val obfuscation: Flow<Boolean> = context.dataStore.data.map {
            preferences -> preferences[obfuscationKey] ?: false
    }

    suspend fun saveObfuscation(value: Boolean){
        context.dataStore.edit { preference ->
            preference[obfuscationKey] = value }
    }

    val ipv6Enable: Flow<Boolean> = context.dataStore.data.map {
            preferences -> preferences[ipv6key] ?: false
    }

    suspend fun saveIpv6enable(value: Boolean){
        context.dataStore.edit { preference ->
            preference[ipv6key] = value }
    }

    val autoStartMobile: Flow<Boolean> = context.dataStore.data.map {
            preferences -> preferences[mobKey] ?: false
    }

    suspend fun saveAutoStartMobile(value: Boolean){
        context.dataStore.edit { preference ->
            preference[mobKey] = value }
    }

    val autoStartOther: Flow<Boolean> = context.dataStore.data.map {
            preferences -> preferences[otherKey] ?: false
    }

    suspend fun saveAutoStartOther(value: Boolean){
        context.dataStore.edit { preference ->
            preference[otherKey] = value }
    }
}