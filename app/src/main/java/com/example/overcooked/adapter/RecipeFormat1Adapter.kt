package com.example.overcooked.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.example.overcooked.R
import com.example.overcooked.data.model.Recipe
import com.example.overcooked.data.model.User

class RecipeFormat1Adapter(
    private val fragmentActivity: FragmentActivity,
    private val originalRecipeList: List<Recipe>,
    private val originalUserList: List<User>
) : BaseAdapter() {
    private var filteredRecipeList = ArrayList<Recipe>(originalRecipeList)
    private var filteredUserList = ArrayList<User>(originalUserList)

    override fun getCount(): Int {
        return filteredRecipeList.size
    }

    override fun getItem(p0: Int): Any {
        return filteredRecipeList[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val inflater = LayoutInflater.from(fragmentActivity)
        val view = inflater.inflate(R.layout.list_item_recipe_format_1, null)
        val user = filteredUserList[p0]
        val recipe = filteredRecipeList[p0]
        if (user.image.isNotEmpty())
            Glide.with(view.findViewById<ImageView>(R.id.ivAuthor).context)
                .load(user.image)
                .into(view.findViewById(R.id.ivAuthor))
        view.findViewById<TextView>(R.id.tvAuthorName).text = user.username
        if (filteredRecipeList[p0].image.isNotEmpty())
            Glide.with(view.findViewById<ImageView>(R.id.ivRecipe).context)
                .load(recipe.image)
                .into(view.findViewById(R.id.ivRecipe))
        view.findViewById<TextView>(R.id.tvRecipeTitle).text = recipe.title
        return view
    }

    fun filter(): Filter {
        return MyFilter()
    }

    inner class MyFilter : Filter() {
        override fun performFiltering(p0: CharSequence?): FilterResults {
            val filterString = p0?.toString() ?: ""
            val result = FilterResults()
            val newRecipeList = ArrayList<Recipe>()
            val newUserList = ArrayList<User>()
            for (i in originalRecipeList.indices) {
                val recipe = originalRecipeList[i]
                val user = originalUserList[i]
                if (recipe.title.startsWith(filterString, true)) {
                    newRecipeList.add(recipe)
                    newUserList.add(user)
                }
            }
            result.values = newRecipeList
            result.count = newRecipeList.size
            filteredUserList = newUserList
            return result
        }

        override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
            if (p1!!.values as? ArrayList<Recipe> != null) {
                filteredRecipeList = p1.values as ArrayList<Recipe>
                notifyDataSetChanged()
            }
        }
    }
}