package com.caminepalgym

import android.content.Context
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.FlowType
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseClient {
    val client = createSupabaseClient(
        supabaseUrl = "https://vpxrzsdayfslvhtllqry.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InZweHJ6c2RheWZzbHZodGxscXJ5Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3Nzg3MTIyMDQsImV4cCI6MjA5NDI4ODIwNH0.12bkMzwapvBqjBQzSqlBYF-i7K-_UN_jfawq3AkcnBU"
    ) {
        install(Auth) {
            // Persiste la sesión automáticamente entre cierres de app
            flowType = FlowType.PKCE
        }
        install(Postgrest)
        install(Storage)
    }
}