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
import kotlinx.android.synthetic.main.activity_add_edit_note.*
import java.util.*

class AddEditNoteActivity : AppCompatActivity() {

    //inflate menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.save_note_menu, menu)
        return true
    }

    //save current note
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item?.itemId) {
            R.id.saveNote -> {

                //checking if either of the editTexts have an input
                var containsText: Boolean = false

                val inputTitle: String? = addEditNote_title_input.text.toString()
                val inputContent: String? = addEditNote_Content_input.text.toString()

                if(inputTitle != "" || inputContent != ""){ containsText = true }


                //at least one has text
                if(containsText){

                    //get intent with ID from NoteListActivity
                    //if EDIT -> intent should get the actual ID of the note
                    //if SAVE -> intent should be "NEW_NOTE" (also null if there was an error somewhere)
                    val noteIDforSaveOrEdit = intent.getStringExtra(NoteListActivity.EXTRA_NOTE_ID)

                    if(noteIDforSaveOrEdit == getString(R.string.NEW_NOTE) || noteIDforSaveOrEdit == null){

                        //intent is "NEW_NOTE" -> new Note with random ID will be uploaded

                        //create new Note obj
                        val uuidString = UUID.randomUUID().toString()
                        val newNoteToSave = Note(uuidString, inputTitle ?: "", inputContent ?: "", true)

                        //call func saveToDBAndUpload with new note
                        saveToDBAndUpload(newNoteToSave)

                    } else{

                        //intent contains the ID of the note -> note will be overridden

                        //create new Note with given ID
                        val editNote = Note(noteIDforSaveOrEdit ?: "", inputTitle ?: "", inputContent ?:"", true)
                        saveToDBAndUpload(editNote)
                    }

                } else{
                    //output missing input error
                    Toast.makeText(this, getString(R.string.no_input), Toast.LENGTH_LONG).show()
                }

                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


    //function to save a new or edited note to the db and upload it to the API
    fun saveToDBAndUpload(inputNote: Note){

        //save to local db
        NoteRepository.addNote(this, inputNote)

        //get accessToken
        val sharedPreferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString(getString(R.string.KEY_FOR_USER_TOKEN), "")

        //upload Note to API
        NoteRepository.addOrUpdateNote(accessToken ?: "", inputNote,
            success = {
                //add newly returned note to the local db
                NoteRepository.addNote(this, it)

                //start NoteListActivity and return result
                val resultIntent = Intent(this, NoteListActivity::class.java)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            },
            error = {
                //output unable to upload error
                Toast.makeText(this, getString(R.string.no_upload), Toast.LENGTH_LONG).show()
            })

    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_note)

        //get ID from intent
        val noteID = intent.getStringExtra(NoteListActivity.EXTRA_NOTE_ID)

        //output the given text for the specific note
        if(noteID != null){
            val myStoredNote = NoteRepository.getSingleNote(this, noteID)
            addEditNote_title_input.setText(myStoredNote?.title ?: "")
            addEditNote_Content_input.setText(myStoredNote?.text ?: "")
        }


    }
}
