package com.example.overcooked.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.example.overcooked.data.model.Ingredient
import com.example.overcooked.R

class IngredientFormat2Adapter(
    private val fragmentActivity: FragmentActivity,
    private val originalArray: Array<Ingredient>
) : BaseAdapter() {

    override fun getCount(): Int {
        return originalArray.size
    }

    override fun getItem(p0: Int): Any {
        return originalArray[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val inflater = LayoutInflater.from(fragmentActivity)
        val view = inflater.inflate(R.layout.list_item_ingredient_format_2, null)
        view.findViewById<TextView>(R.id.tvIngredientName).text = originalArray[p0].name
        view.findViewById<TextView>(R.id.tvIngredientMass).text =
            originalArray[p0].massInGram.toString() + "g"
        return view
    }
}