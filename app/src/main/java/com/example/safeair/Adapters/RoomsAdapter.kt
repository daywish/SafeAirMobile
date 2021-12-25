package com.example.safeair.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.safeair.Model.Room
import com.example.safeair.R


class RoomsAdapter(private val dataSet: Array<Room>) :
    RecyclerView.Adapter<RoomsAdapter.ViewHolder>(){


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textNum: TextView
        val textTemp: TextView
        val textWet: TextView
        val button: Button
        val roomId: TextView

        init {
            // Define click listener for the ViewHolder's View.
            textNum = itemView.findViewById(R.id.txt_roomNum)
            textTemp = itemView.findViewById(R.id.txt_roomTemp)
            textWet = itemView.findViewById(R.id.txt_roomWet)
            roomId = itemView.findViewById(R.id.text_id)
            button = itemView.findViewById(R.id.changeButton)

            /*this.bundle = bundleOf("RoomId" to roomId.text.toString())
            button.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_roomsFragment_to_editRoomFragment, bundle));*/

            /*    button.setOnClickListener {
                    Navigation.createNavigateOnClickListener(R.id.action_roomsFragment_to_editRoomFragment, bundle)
                }*/
        }
    }
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.rooms_layout, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.textNum.text = dataSet[position].RoomNumber.toString()
        viewHolder.textTemp.text = dataSet[position].RoomTemperature.toString()
        viewHolder.textWet.text = dataSet[position].RoomWetness.toString()
        viewHolder.roomId.text = dataSet[position].RoomId.toString()
        var bundle = bundleOf("RoomId" to dataSet[position].RoomId.toString())
        bundle.putString("Temperature", dataSet[position].RoomTemperature.toString())
        bundle.putString("Wetness", dataSet[position].RoomWetness.toString())
        viewHolder.button.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_roomsFragment_to_editRoomFragment, bundle))
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
}