package com.example.barberroom.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.barberroom.R
import com.example.barberroom.databinding.ActivityBarberoBinding
import com.example.barberroom.ui.barbero.BarberoAgendaFragment
import com.example.barberroom.ui.barbero.BarberoPerfilFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class BarberoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBarberoBinding
    private val barberoAgendaFragment = BarberoAgendaFragment()
    private val barberoPerfilFragment = BarberoPerfilFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBarberoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomNavigation = binding.bottomNavigationBarber

        if (savedInstanceState == null) {
            setCurrentFragment(barberoAgendaFragment)
        }
        bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navegacion_agenda_barber -> setCurrentFragment(barberoAgendaFragment)
                R.id.navegacion_perfil_barber -> setCurrentFragment(barberoPerfilFragment)
            }
            true
        }
    }
    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container_barber, fragment)
            commit()
        }
    }
}