package com.obrigada_eu.listadecompras.presentation

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.obrigada_eu.listadecompras.R
import com.obrigada_eu.listadecompras.databinding.ActivityListSetBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListSetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListSetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListSetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.list_set_container, ListSetFragment.newInstance())
                .commit()
        }
    }


    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, ListSetActivity::class.java)
        }
    }
}