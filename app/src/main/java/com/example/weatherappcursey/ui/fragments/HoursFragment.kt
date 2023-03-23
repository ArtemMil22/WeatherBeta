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
import com.example.weatherappcursey.databinding.FragmentHoursBinding
import org.json.JSONArray
import org.json.JSONObject

class HoursFragment : Fragment() {

    private lateinit var binding: FragmentHoursBinding
    private lateinit var adapter: WeatherAdapterRV
    private val model: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHoursBinding.inflate(inflater, container, false)
        return binding.root
    }

    // запускается когда элементы уже в памяти (чтто менять)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcView()
        model.liveDataCurrent.observe(viewLifecycleOwner) {
            adapter.submitList(getHoursList(it))
        }
    }

    private fun initRcView() = with(binding) {
        rcView.layoutManager = LinearLayoutManager(activity)
        adapter = WeatherAdapterRV(null)
        rcView.adapter = adapter
    }

    // передаем данные из главной карточки, но берем определенный час
    // превратим ДЖЕЙСОН массим и возьмем из него данныые
    private fun getHoursList(wItem: WeatherModel): List<WeatherModel> {
        val hoursArray = JSONArray(wItem.hours)
        //список с классами ВейзорМодел
        val list = ArrayList<WeatherModel>()
        for (i in 0 until hoursArray.length()) {
            val item = WeatherModel(
                wItem.city,
                (hoursArray[i] as JSONObject).getString("time"),
                (hoursArray[i] as JSONObject)
                    .getJSONObject("condition").getString("text"),
                (hoursArray[i] as JSONObject).getString("temp_c").toFloat().toInt().toString(),
                "","",
                (hoursArray[i] as JSONObject)
                    .getJSONObject("condition").getString("icon"),
                ""
                )
            list.add(item)
        }
        return list
    }
    companion object {
        @JvmStatic
        fun newInstance() = HoursFragment()
    }
}