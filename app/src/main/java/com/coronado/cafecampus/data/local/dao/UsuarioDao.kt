package com.coronado.cafecampus.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.coronado.cafecampus.data.local.entity.Usuario

@Dao
interface UsuarioDao {
    @Query("SELECT * FROM usuarios WHERE correo = :correo AND clave = :clave LIMIT 1")
    suspend fun login(correo: String, clave: String): Usuario?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(u: Usuario): Long

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contar(): Int

    /** Modo practica: permite cambiar de perfil sin volver a loguearse. */
    @Query("SELECT * FROM usuarios WHERE rol = :rol ORDER BY id LIMIT 1")
    suspend fun primeroPorRol(rol: String): Usuario?
}