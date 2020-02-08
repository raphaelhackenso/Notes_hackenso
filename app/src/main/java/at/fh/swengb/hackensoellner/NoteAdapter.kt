package at.fh.swengb.hackensoellner

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_note.view.*

class NoteAdapter(val clickListener: (note: Note) -> Unit): RecyclerView.Adapter<NoteViewHolder>(){

    private var noteList = listOf<Note>()

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): NoteViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val noteItemView = inflater.inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(noteItemView, clickListener)
    }

    override fun getItemCount(): Int {
        return noteList.size
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        var note = noteList[position]
        holder.bindItem(note)
    }

    fun updateList(newList: List<Note>) {
        noteList = newList
        notifyDataSetChanged()
    }

}


class NoteViewHolder(itemView: View, val clickListener: (note: Note) -> Unit): RecyclerView.ViewHolder(itemView){
    fun bindItem(note: Note){


        //Output content for one note item
        itemView.item_note_Title_output.text = note.title
        itemView.item_note_Content_output.text = note.text


        itemView.setOnClickListener{
            clickListener(note)
        }

    }

}