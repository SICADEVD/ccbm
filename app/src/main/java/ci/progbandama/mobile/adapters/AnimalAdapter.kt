package ci.progbandama.mobile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView
import ci.progbandama.mobile.R
import ci.progbandama.mobile.adapters.AnimalAdapter.AnimalHolder
import ci.progbandama.mobile.databinding.AnimalItemsListBinding


class AnimalAdapter(val animauxList: MutableList<String>): RecyclerView.Adapter<AnimalHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalHolder {
        return AnimalHolder(
            AnimalItemsListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//            LayoutInflater.from(parent.context).inflate(R.layout.animal_items_list, parent, false)
        )
    }


    override fun onBindViewHolder(holder: AnimalHolder, position: Int) {
        val animal = animauxList[position]
        holder.animalLabel.text = animal

        holder.animalDelete.setOnClickListener {
            if (animauxList.size == 1) {
                notifyItemRemoved(0)
                animauxList.removeAt(0)
            } else {
                notifyItemRemoved(position)
                animauxList.removeAt(position)
            }
        }
    }

    fun deleteEcole(animalPosition: Int) {
        notifyItemRemoved(animalPosition)
        animauxList.removeAt(animalPosition)
    }


    override fun getItemCount(): Int = animauxList.size


    class AnimalHolder(animalHolder: AnimalItemsListBinding) : RecyclerView.ViewHolder(animalHolder.root) {
        val animalLabel = animalHolder.labelAnimalNomItems
        val animalDelete = animalHolder.imageDeleteAnimalItems
    }

}
