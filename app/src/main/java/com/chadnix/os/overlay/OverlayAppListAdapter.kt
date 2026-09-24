package com.chadnix.os.overlay

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chadnix.os.core.AppInfo
import com.chadnix.os.databinding.ItemAppBinding

class OverlayAppListAdapter(
    private val apps: List<AppInfo>,
    private val onAppClick: (AppInfo) -> Unit
) : RecyclerView.Adapter<OverlayAppListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemAppBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAppBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = apps[position]
        holder.binding.appIcon.setImageDrawable(app.icon)
        holder.binding.appLabel.text = app.label
        holder.binding.root.setOnClickListener { onAppClick(app) }
    }

    override fun getItemCount(): Int = apps.size
}
