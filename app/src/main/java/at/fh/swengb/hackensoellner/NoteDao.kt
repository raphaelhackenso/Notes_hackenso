package at.fh.swengb.hackensoellner

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(note: Note)

    @Query("DELETE FROM Note")
    fun deleteAllNote()

    @Query("SELECT * FROM Note")
    fun getEveryNote(): List<Note>?

    @Query("SELECT * FROM NOTE WHERE id = :id")
    fun getSingleNote(id: String): Note?



}