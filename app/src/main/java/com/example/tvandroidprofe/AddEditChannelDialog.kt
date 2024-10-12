package com.example.tvandroidprofe

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class AddEditChannelDialog(
    private val channel: Channel?,
    private val onSave: (Channel) -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_add_edit_channel, null)
        val nameEditText = view.findViewById<EditText>(R.id.nameEditText)
        val urlEditText = view.findViewById<EditText>(R.id.urlEditText)
        val logoEditText = view.findViewById<EditText>(R.id.logoEditText)

        // Si estamos editando un canal, llenamos los campos con los valores actuales
        if (channel != null) {
            nameEditText.setText(channel.name)
            urlEditText.setText(channel.url)
            logoEditText.setText(channel.logo)
        }

        return AlertDialog.Builder(requireContext())
            .setTitle(if (channel == null) "Agregar Canal" else "Editar Canal")
            .setView(view)
            .setPositiveButton("Guardar") { _, _ ->
                val name = nameEditText.text.toString()
                val url = urlEditText.text.toString()
                val logo = logoEditText.text.toString()

                if (name.isNotBlank() && url.isNotBlank() && logo.isNotBlank()) {
                    onSave(Channel(name, url, logo))
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()
    }
}
