package com.example.barberroom.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.barberroom.R
import com.example.barberroom.databinding.ActivityMainBinding
import com.example.barberroom.ui.auth.LoginActivity
import com.example.barberroom.ui.usuario.BarberosFragment
import com.example.barberroom.ui.usuario.CitasFragment
import com.example.barberroom.ui.usuario.InicioFragment
import com.example.barberroom.ui.usuario.MisCitasFragment
import com.example.barberroom.ui.usuario.ServiciosFragment
import com.google.firebase.auth.FirebaseAuth
import android.app.Dialog
import android.view.Menu
import android.view.MenuItem
import com.example.barberroom.databinding.DialogoPerfilBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val auth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.menuSup)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        initNavigation()
    }

    override fun onCreateOptionsMenu(menu: Menu) =
        menuInflater.inflate(R.menu.menu_perfil, menu).let { true }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.icon_perfil -> showProfileDialog().let { true }
            else -> super.onOptionsItemSelected(item)
        }

    private fun initNavigation() {
        replaceFragment(InicioFragment())
        binding.menuNav.setOnItemSelectedListener { item ->
            replaceFragment(
                when (item.itemId) {
                    R.id.navegacion_inicio -> InicioFragment()
                    R.id.navegacion_cita -> CitasFragment()
                    R.id.navegacion_miscita -> MisCitasFragment()
                    R.id.navegacion_servicio -> ServiciosFragment()
                    else -> BarberosFragment()
                }
            )
            true
        }
    }

    private fun showProfileDialog() {
        val dialogBinding = DialogoPerfilBinding.inflate(layoutInflater)
        val dialog = Dialog(this).apply { setContentView(dialogBinding.root) }

        dialogBinding.apply {
            tvUser.text = auth.currentUser?.displayName ?: "Sin Nombre"
            tvEmail.text = auth.currentUser?.email ?: "Sin Correo"

            btnLogout.setOnClickListener {
                auth.signOut()
                Toast.makeText(this@MainActivity, "Has cerrado sesión", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
            }
        }
        dialog.show()
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .commit()
    }

    fun updateBottomNavSelection(itemId: Int) {
        binding.menuNav.selectedItemId = itemId
    }
}