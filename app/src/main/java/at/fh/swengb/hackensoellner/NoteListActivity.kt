package at.fh.swengb.hackensoellner

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.android.synthetic.main.activity_note_list.*

class NoteListActivity : AppCompatActivity() {

    companion object {
        //send id as intent
        val EXTRA_NOTE_ID = "NOTE_ID_EXTRA"
        val KEY_FOR_USER_LASTSYNC = "MY_KEY_FOR_USER_LASTSYNC"
        val ADD_OR_EDIT_NOTE_REQUEST = 1
    }

    //inflate menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.new_logout_menu, menu)
        return true
    }


    //either logging out or creating a new note
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item?.itemId) {
            R.id.logout -> {

                //clear sharedPreferences
                val sharedPreferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)
                sharedPreferences.edit().clear().apply()

                //delete every note from local db
                NoteRepository.deleteAllNote(this)

                //start main and finish
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()

                true
            }
            R.id.newNote -> {

                //start AddEditNote to add a new note
                val intent = Intent(this, AddEditNoteActivity::class.java)
                intent.putExtra(EXTRA_NOTE_ID, getString(R.string.NEW_NOTE))
                startActivityForResult(intent, ADD_OR_EDIT_NOTE_REQUEST)

                true
            }
            R.id.sync -> {

                // This feature is added because a user might not have an internet connection
                // for a period of time. If they add/edit a note with no connection it's stored locally,
                // but upon logout the note is gone/not updated.
                // When connectivity becomes available again the user can press the sync icon to manually sync his/her notes.

                //manually sync all notes with the API
                syncNotes()

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }



    val noteAdapter = NoteAdapter(){
        //Start AddEditNoteActivity to edit a note
        val intent = Intent(this, AddEditNoteActivity ::class.java)
        intent.putExtra(EXTRA_NOTE_ID, it.id)
        startActivityForResult(intent, ADD_OR_EDIT_NOTE_REQUEST)

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_list)

        //fun syncNotes does
        // -> gets notes from local db
        // -> display local notes
        // -> load notes from API and update db
        // -> reload UI with new db

        //sync notes onCreate of the activity
        syncNotes()

    }


    fun syncNotes(){

        //Update the recycler view
        noteList_recyclerView.layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        noteList_recyclerView.adapter = noteAdapter

        //get notes from db and display to user
        val myStoredNotes = NoteRepository.getEveryNote(this)
        noteAdapter.updateList(myStoredNotes ?: emptyList())


        //get token and lastSync from sharedPreferences
        val sharedPreferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString(getString(R.string.KEY_FOR_USER_TOKEN), "")
        val lastSync = sharedPreferences.getLong(getString(R.string.KEY_FOR_USER_LASTSYNC), 0)


        //load notes from the API and update db
        NoteRepository.notes(accessToken ?: "", lastSync,
            success = {
                //update db
                it.notes.map { NoteRepository.addNote(this, it) }

                //storing returned LastSync
                sharedPreferences.edit().putLong(KEY_FOR_USER_LASTSYNC, it.lastSync).apply()

                //reload from db
                noteAdapter.updateList(NoteRepository.getEveryNote(this) ?: emptyList())

            },
            error = {
                //output error message
                Toast.makeText(this, getString(R.string.no_upload), Toast.LENGTH_LONG).show()
            })

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //handle return result from AddEditNoteActivity
        if (requestCode == ADD_OR_EDIT_NOTE_REQUEST && resultCode == Activity.RESULT_OK) {
            //output updated content
            syncNotes()
        }
    }


}
