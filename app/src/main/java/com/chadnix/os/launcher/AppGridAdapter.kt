package com.chadnix.os.launcher

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chadnix.os.core.AppInfo
import com.chadnix.os.databinding.ItemAppBinding

class AppGridAdapter(
    private val apps: List<AppInfo>,
    private val onAppClick: (AppInfo) -> Unit
) : RecyclerView.Adapter<AppGridAdapter.AppViewHolder>() {

    inner class AppViewHolder(val binding: ItemAppBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val binding = ItemAppBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AppViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val app = apps[position]
        holder.binding.appIcon.setImageDrawable(app.icon)
        holder.binding.appLabel.text = app.label
        holder.binding.root.setOnClickListener { onAppClick(app) }
    }

    override fun getItemCount(): Int = apps.size
}
