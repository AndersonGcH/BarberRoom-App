package com.example.barberroom.ui.usuario

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.barberroom.R
import com.example.barberroom.adapter.CarruselAdapter
import com.example.barberroom.databinding.FragmentInicioBinding

class InicioFragment : Fragment() {

    private var _binding: FragmentInicioBinding? = null
    private val binding get() = _binding!!
    private val handler by lazy { Handler(Looper.getMainLooper()) }
    private val imageList = listOf(R.drawable.ic_prom1, R.drawable.ic_prom2, R.drawable.ic_prom3)

    private val scroll = Runnable {
        binding.promocionViewPager.run {
            val next = if (currentItem == (adapter?.itemCount ?: 0) - 1) 0 else currentItem + 1
            setCurrentItem(next, true)
        }
        startAutoScroll()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentInicioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.promocionViewPager.adapter = CarruselAdapter(imageList)
        startAutoScroll()
    }

    private fun startAutoScroll() {
        handler.removeCallbacks(scroll)
        handler.postDelayed(scroll, 3000)
    }

    override fun onDestroyView() {
        handler.removeCallbacks(scroll)
        _binding = null
        super.onDestroyView()
    }
}