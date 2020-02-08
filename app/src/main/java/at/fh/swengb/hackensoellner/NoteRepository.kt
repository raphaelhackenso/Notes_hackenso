package at.fh.swengb.hackensoellner

import android.content.Context
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.AccessControlContext


object NoteRepository {


    fun notes(accessToken: String, lastSync: Long, success: (notesResponse: NotesResponse) -> Unit, error: (errorMessage: String) -> Unit){
        NoteApi.retrofitService.notes(accessToken, lastSync).enqueue(object: Callback<NotesResponse> {
            override fun onFailure(call: Call<NotesResponse>, t: Throwable) {
                error("The call failed")
            }

            override fun onResponse(call: Call<NotesResponse>, response: Response<NotesResponse>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    success(responseBody)
                } else {
                    error("Something went wrong")
                }
            }
        })
    }



    fun login(body: AuthRequest, success: (authResponse: AuthResponse) -> Unit, error: (errorMessage: String) -> Unit){
        NoteApi.retrofitService.login(body).enqueue(object: Callback<AuthResponse> {
            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                error("The call failed")
            }

            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    success(responseBody)
                } else {
                    error("Something went wrong")
                }
            }
        })
    }



    fun addOrUpdateNote(accessToken: String, body:Note, success: (note: Note) -> Unit, error: (errorMessage: String) -> Unit){
        NoteApi.retrofitService.addOrUpdateNote(accessToken, body).enqueue(object: Callback<Note> {
            override fun onFailure(call: Call<Note>, t: Throwable) {
                error("The call failed")
            }

            override fun onResponse(call: Call<Note>, response: Response<Note>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    success(responseBody)
                } else {
                    error("Something went wrong")
                }
            }
        })
    }


    fun deleteAllNote(context: Context){
        val db = NoteDatabase.getDatabase(context.applicationContext)
        db.noteDao.deleteAllNote()
    }



    fun addNote(context: Context, note: Note) {
        val applicationContext = context.applicationContext
        val db = NoteDatabase.getDatabase(applicationContext)
        db.noteDao.insert(note)
    }


    fun getEveryNote(context: Context):List<Note>?{
        val db = NoteDatabase.getDatabase(context.applicationContext)
        return db.noteDao.getEveryNote()
    }



    fun getSingleNote(context: Context, inputID: String):Note?{
        val db = NoteDatabase.getDatabase(context.applicationContext)
        return db.noteDao.getSingleNote(inputID)
    }



}