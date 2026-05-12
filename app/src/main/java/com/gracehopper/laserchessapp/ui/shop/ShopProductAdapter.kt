package com.gracehopper.laserchessapp.ui.shop

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gracehopper.laserchessapp.data.model.shop.ShopProduct
import com.gracehopper.laserchessapp.databinding.ItemShopProductBinding
import com.gracehopper.laserchessapp.ui.utils.ItemUtils

class ShopProductAdapter(
    private var products: List<ShopProduct>,
    private val onClick: (ShopProduct) -> Unit
) : RecyclerView.Adapter<ShopProductAdapter.ShopProductViewHolder>() {

    private var userMoney: Int = 0

    inner class ShopProductViewHolder(
        private val binding: ItemShopProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(product: ShopProduct) {
            binding.txtShopProductName.text = product.name
            binding.imgShopProduct.setImageResource(
                ItemUtils.getItemDrawable(product.itemId)
            )

            if (product.isOwned) {
                binding.txtShopProductPrice.text = "COMPRADO"
                binding.imgShopCoin.visibility = View.GONE
                binding.root.isEnabled = false
                binding.root.alpha = 0.6f
                binding.root.setOnClickListener(null)
            } else if (product.isLevelLocked) {
                binding.txtShopProductPrice.text = "Nivel ${product.levelRequisite}"
                binding.imgShopCoin.visibility = View.GONE
                binding.root.isEnabled = false
                binding.root.alpha = 0.6f
                binding.root.setOnClickListener(null)
            } else {
                binding.txtShopProductPrice.text = product.price.toString()
                binding.imgShopCoin.visibility = View.VISIBLE

                if (userMoney >= product.price) {
                    binding.root.isEnabled = true
                    binding.root.alpha = 1f
                    binding.root.setOnClickListener {
                        onClick(product)
                    }
                } else {
                    binding.root.isEnabled = false
                    binding.root.alpha = 0.6f
                    binding.root.setOnClickListener(null)
                }
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

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newProducts: List<ShopProduct>, newUserMoney: Int = userMoney) {
        products = newProducts
        userMoney = newUserMoney
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateUserMoney(newUserMoney: Int) {
        userMoney = newUserMoney
        notifyDataSetChanged()
    }

}
