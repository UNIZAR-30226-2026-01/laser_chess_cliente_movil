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
) : RecyclerView.Adapter<ShopProductAdapter.ShopProductViewHolder>() {

    inner class ShopProductViewHolder(
        private val binding: ItemShopProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: ShopProduct) {
            binding.txtShopProductName.text = product.name
            binding.txtShopProductPrice.text = product.price.toString()
            binding.imgShopProduct.setImageResource(
                ItemUtils.getItemDrawable(product.itemType, product.itemId)
            )

            binding.root.setOnClickListener {
                onClick(product)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopProductViewHolder {
        val binding = ItemShopProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ShopProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShopProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size

    fun updateData(newProducts: List<ShopProduct>) {
        products = newProducts
        notifyDataSetChanged()
    }

}