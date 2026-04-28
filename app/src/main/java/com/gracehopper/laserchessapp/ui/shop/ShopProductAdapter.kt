package com.gracehopper.laserchessapp.ui.shop

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gracehopper.laserchessapp.data.model.shop.ShopProduct
import com.gracehopper.laserchessapp.databinding.ItemShopProductBinding
import com.gracehopper.laserchessapp.ui.utils.ItemUtils

class ShopProductAdapter(
    private var products: List<ShopProduct>,
    private val onClick: (ShopProduct) -> Unit
) : RecyclerView.Adapter<ShopProductAdapter.ShopViewHolder>() {

    class ShopViewHolder(val binding: ItemShopProductBinding) :
            RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ShopViewHolder {
        val binding = ItemShopProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ShopViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShopViewHolder, position: Int) {
        val product = products[position]

        holder.binding.txtShopProductName.text = product.name
        holder.binding.txtShopProductPrice.text = product.price.toString()
        holder.binding.imgShopProduct.setImageResource(
            ItemUtils.getItemDrawable(product.itemType, product.itemId)
        )
        holder.binding.root.setOnClickListener {
            onClick(product)
        }
    }

    override fun getItemCount(): Int = products.size

    fun updateData(newProducts: List<ShopProduct>) {
        products = newProducts
        notifyDataSetChanged()
    }

}