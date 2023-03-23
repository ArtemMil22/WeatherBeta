package com.example.weatherappcursey.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherappcursey.model.MainViewModel
import com.example.weatherappcursey.ui.adapter.WeatherAdapterRV
import com.example.weatherappcursey.model.WeatherModel
import com.example.weatherappcursey.databinding.FragmentDaysBinding


class DaysFragment : Fragment(), WeatherAdapterRV.Listener {

    private lateinit var adapter: WeatherAdapterRV
    lateinit var binding: FragmentDaysBinding
    private val model: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDaysBinding.inflate(inflater, container, false)
        return binding.root
    }

    // используется когда все Вью уже нарисованы
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRV()
        //инициализируем нашего наблюдателя (слушителя)
        model.liveDataList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    private fun initRV() = with(binding) {
        // указываем расположение списка оп умолч
        rcViewD.layoutManager = LinearLayoutManager(activity)
        adapter = WeatherAdapterRV(this@DaysFragment)
        rcViewD.adapter = adapter
    }

    companion object {
        @JvmStatic
        fun newInstance() = DaysFragment()
    }

    override fun onClick(item: WeatherModel) {
        model.liveDataCurrent.value = item
    }
}