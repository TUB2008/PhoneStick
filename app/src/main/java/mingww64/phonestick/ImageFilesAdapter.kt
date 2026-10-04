package mingww64.phonestick

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import mingww64.phonestick.databinding.ImageChooserRowBinding
import java.io.File

class ImageFilesAdapter(
    private val files: MutableList<File>,
    private val selectedPath: String,
    private val onFileSelected: (File) -> Unit,
    private val onFileMount: (File) -> Unit,
    private val onFileDeleted: (File) -> Unit,
    private val onFileRenamed: (File, File) -> Unit
) : RecyclerView.Adapter<ImageFilesAdapter.ViewHolder>() {

    var isSelectionMode: Boolean = false
        set(value) {
            field = value
            if (!value) checkedFiles.clear()
            notifyDataSetChanged()
        }

    val checkedFiles = mutableSetOf<File>()
    var onSelectionCountChanged: (Int) -> Unit = {}
    var onFileLongClick: ((File) -> Unit)? = null

    private companion object {
        const val MENU_MOUNT_NOW = 1
        const val MENU_RENAME = 2
        const val MENU_DELETE = 3
    }

    inner class ViewHolder(val binding: ImageChooserRowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ImageChooserRowBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val file = files[position]
        val context = holder.itemView.context

        holder.binding.filename.text = file.name
        holder.binding.fileSize.text = formatFileSize(context, file.length())

        if (file.extension.equals("iso", ignoreCase = true)) {
            holder.binding.ivFileIcon.setImageResource(R.drawable.ic_disc_vector)
        } else {
            holder.binding.ivFileIcon.setImageResource(R.drawable.ic_sd_storage_vector)
        }

        val isSelected = file.absolutePath == selectedPath

        // Set active box outline stroke and active badge
        if (isSelected) {
            holder.binding.cardItem.strokeWidth = dpToPx(context, 2)
            holder.binding.tvSelectedBadge.visibility = View.VISIBLE
        } else {
            holder.binding.cardItem.strokeWidth = 0
            holder.binding.tvSelectedBadge.visibility = View.GONE
        }

        if (isSelectionMode) {
            holder.binding.cbSelect.visibility = View.VISIBLE
            holder.binding.cbSelect.isChecked = checkedFiles.contains(file)
            holder.binding.btnOverflow.visibility = View.GONE

            holder.itemView.setOnClickListener {
                if (checkedFiles.contains(file)) {
                    checkedFiles.remove(file)
                } else {
                    checkedFiles.add(file)
                }
                notifyItemChanged(position)
                onSelectionCountChanged(checkedFiles.size)
            }
            holder.itemView.setOnLongClickListener(null)
        } else {
            holder.binding.cbSelect.visibility = View.GONE
            holder.binding.btnOverflow.visibility = View.VISIBLE

            holder.itemView.setOnClickListener {
                onFileSelected(file)
            }

            holder.itemView.setOnLongClickListener {
                onFileLongClick?.invoke(file)
                true
            }

            holder.binding.btnOverflow.setOnClickListener { view ->
                showPopupMenu(context, view, file)
            }
        }
    }

    private fun showPopupMenu(context: Context, anchorView: View, file: File) {
        val popup = PopupMenu(context, anchorView)
        popup.menu.add(0, MENU_MOUNT_NOW, 0, context.getString(R.string.action_mount_now))
        popup.menu.add(0, MENU_RENAME, 1, context.getString(R.string.action_rename))
        popup.menu.add(0, MENU_DELETE, 2, context.getString(R.string.action_delete))

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                MENU_MOUNT_NOW -> {
                    onFileMount(file)
                    true
                }
                MENU_RENAME -> {
                    showRenameDialog(context, file)
                    true
                }
                MENU_DELETE -> {
                    showDeleteDialog(context, file)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun showRenameDialog(context: Context, file: File) {
        val input = EditText(context)
        input.setText(file.name)
        input.setSelection(file.name.lastIndexOf('.').let { if (it > 0) it else file.name.length })

        MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.dialog_rename_title))
            .setView(input)
            .setPositiveButton(context.getString(R.string.action_rename)) { _, _ ->
                val newName = input.text.toString().trim()
                if (newName.isNotEmpty() && newName != file.name) {
                    val newFile = File(file.parentFile, newName)
                    if (file.renameTo(newFile)) {
                        onFileRenamed(file, newFile)
                    }
                }
            }
            .setNegativeButton(context.getString(R.string.cancel), null)
            .show()
    }

    private fun showDeleteDialog(context: Context, file: File) {
        val isSelected = file.absolutePath == selectedPath
        val msg = if (isSelected) {
            context.getString(R.string.dialog_delete_active_message, file.name)
        } else {
            context.getString(R.string.dialog_delete_confirm_message, file.name)
        }

        MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.dialog_delete_image_title))
            .setMessage(msg)
            .setPositiveButton(context.getString(R.string.action_delete)) { _, _ ->
                onFileDeleted(file)
            }
            .setNegativeButton(context.getString(R.string.cancel), null)
            .show()
    }

    override fun getItemCount(): Int = files.size

    private fun formatFileSize(context: Context, sizeBytes: Long): String {
        if (sizeBytes <= 0) return context.getString(R.string.size_zero)
        val kb = sizeBytes / 1024.0
        val mb = kb / 1024.0
        val gb = mb / 1024.0
        return when {
            gb >= 1.0 -> context.getString(R.string.size_gb, gb)
            mb >= 1.0 -> context.getString(R.string.size_mb, mb)
            kb >= 1.0 -> context.getString(R.string.size_kb, kb)
            else -> context.getString(R.string.size_bytes, sizeBytes)
        }
    }

    private fun dpToPx(context: Context, dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }
}