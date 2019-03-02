package com.kadmiv.filmrepo.base.dialogs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import com.kadmiv.filmrepo.R
import com.kadmiv.filmrepo.databinding.DialogInfoBinding
import com.kadmiv.filmrepo.repo.InfoModel

const val EXTRAS_MODEL = "EXTRAS_MODEL"

class BaseDialog() : DialogFragment() {

    var model: InfoModel? = null

    @SuppressLint("ValidFragment")
    constructor(model: InfoModel) : this() {
        this.model = model
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val inflater = LayoutInflater.from(this.context)

        if (savedInstanceState != null) {
            var model: InfoModel? = savedInstanceState!!.getParcelable(EXTRAS_MODEL)
            if (model != null)
                this.model = model
        }

        val binding = DataBindingUtil.inflate<DialogInfoBinding>(
                inflater, R.layout.dialog_info,
                null, false
        )

        binding.model = model

        return AlertDialog.Builder(this.context!!)
                .setView(binding.root)
                .create()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(EXTRAS_MODEL, model)
        super.onSaveInstanceState(outState)
    }
}