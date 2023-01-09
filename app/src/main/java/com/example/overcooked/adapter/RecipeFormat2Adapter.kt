package com.example.overcooked.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.overcooked.R
import com.example.overcooked.data.model.Recipe
import com.example.overcooked.databinding.ListItemRecipeFormat2Binding
import com.example.overcooked.ui.viewrecipe.ViewRecipeFragment

class RecipeFormat2Adapter(
    private val context: Context,
    private val originalList: List<Recipe>,
    private val activity: FragmentActivity
) : RecyclerView.Adapter<RecipeFormat2Adapter.RecipeFormat2ViewHolder>() {

    class RecipeFormat2ViewHolder(val binding: ListItemRecipeFormat2Binding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeFormat2ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemRecipeFormat2Binding.inflate(layoutInflater, parent, false)
        return RecipeFormat2ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeFormat2ViewHolder, position: Int) {
        val targetRecipe = originalList[position]
        holder.binding.apply {
            if (targetRecipe.image.isNotEmpty())
                Glide.with(context)
                    .load(targetRecipe.image)
                    .into(ivRecipe)
            tvRecipeTitle.text = targetRecipe.title
            root.setOnClickListener {
                activity.supportFragmentManager.beginTransaction().setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right
                )
                    .replace(
                        R.id.fragmentContainerView,
                        ViewRecipeFragment.newInstance(targetRecipe.id)
                    )
                    .addToBackStack(null).commit()
            }
        }
    }

    override fun getItemCount() = originalList.size
}
