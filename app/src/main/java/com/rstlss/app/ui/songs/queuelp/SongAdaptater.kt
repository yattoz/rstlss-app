package com.rstlss.app.ui.songs.queuelp

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.rstlss.app.R
import com.rstlss.app.colorBlue
import com.rstlss.app.colorWhited
import com.rstlss.app.playerstore.Song
import kotlinx.android.synthetic.main.song_view.view.*
import kotlin.collections.ArrayList

class SongAdaptater(private val dataSet: ArrayList<Song>
                    /*,
                    context: Context,
                    resource: Int,
                    objects: Array<out Song>*/
) : RecyclerView.Adapter<SongAdaptater.MyViewHolder>() /*ArrayAdapter<Song>(context, resource, objects)*/ {


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(view: ConstraintLayout) : RecyclerView.ViewHolder(view)


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MyViewHolder {
        // create a new view
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.song_view, parent, false) as ConstraintLayout
        // set the view's size, margins, paddings and layout parameters
        //...
        return MyViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.item.text = "${dataSet[position].artist.value} - ${dataSet[position].title.value}"
        if (dataSet[position].type.value == 1)
            holder.itemView.item.setTextColor(colorBlue)
        else
            holder.itemView.item.setTextColor(colorWhited)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size


    /*
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // create a new view
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.song_view, parent, false) as ConstraintLayout
    }
    */

}

