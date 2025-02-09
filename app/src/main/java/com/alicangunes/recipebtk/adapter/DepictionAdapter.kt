package com.alicangunes.recipebtk.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.alicangunes.recipebtk.databinding.RecyclerRowBinding
import com.alicangunes.recipebtk.model.Depiction
import com.alicangunes.recipebtk.view.ListFragmentDirections

class DepictionAdapter(val depictionList : List<Depiction>) : RecyclerView.Adapter<DepictionAdapter.DepictionHolder>() {

    class DepictionHolder(val binding: RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DepictionHolder {
        val recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return DepictionHolder(recyclerRowBinding)
    }

    override fun getItemCount(): Int {
        return depictionList.size
    }

    override fun onBindViewHolder(holder: DepictionHolder, position: Int) {
        holder.binding.recyclerViewTextView.text = depictionList[position].name
        holder.itemView.setOnClickListener {
            val action = ListFragmentDirections.actionListFragmentToDepictionFragment("old", id = depictionList[position].id)
            Navigation.findNavController(it).navigate(action)
        }
    }
}