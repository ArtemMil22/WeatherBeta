package com.example.weatherappcursey.ui.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weatherappcursey.ui.DialogManager
import com.example.weatherappcursey.model.MainViewModel
import com.example.weatherappcursey.ui.adapter.VPadapter
import com.example.weatherappcursey.model.WeatherModel
import com.example.weatherappcursey.databinding.FragmentMainBinding
import com.example.weatherappcursey.fragments.isPermissionGranted
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso
import org.json.JSONObject

const val API_KEY = "dd4c99e9128f4741a5483140232901"

class MainFragment : Fragment() {

    private lateinit var fLocationClient: FusedLocationProviderClient
    private val fList = listOf(HoursFragment.newInstance(), DaysFragment.newInstance())
    private val tList = listOf("by Time", "by Days")
    private lateinit var pLauncher: ActivityResultLauncher<String>
    private lateinit var binding: FragmentMainBinding //1
    private val model: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }
    // проверку делаем когда открывается основной фрагмент
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState) //  в этой фун делаем проверку
        checkPermission()
        initP()
        updateCurrentCard()
    }
    override fun onResume() {
        super.onResume()
        checkLocation()
    }

    // Для пейджера логика для переключения между фрагментами
    private fun initP() = with(binding) {
        fLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        val adapter = VPadapter(activity as FragmentActivity, fList)
        vp.adapter = adapter
        // для перелистывания элементов
        TabLayoutMediator(tabLayout, vp) { tab, pos ->
            tab.text = tList[pos]
        }.attach()
        ibSing.setOnClickListener {
            //чтобы перекидывало на часы обращаемся к ТабЛояут
            tabLayout.selectTab(tabLayout.getTabAt(0))
            //getLocation() было
            checkLocation()
        }
        // делаем поиск по городу
        ibSeartch.setOnClickListener {
            DialogManager.searchByNameDialog(
                requireContext(), object : DialogManager.Listener {
                override fun onClick(name: String?) {
                    name?.let { it1 -> requestWeatherData(it1) }
                }
            })
        }
    }
    //функция для AlertDialog
    private fun checkLocation() {
        if (isLocationEmable()) {
            getLocation()
        } else {
            DialogManager.locationSettingsDialog(requireContext(), object : DialogManager.Listener {
                override fun onClick(name: String?) {
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
            })
        }
    }

    // функция для проверки вкл/выкл геопозиции
    private fun isLocationEmable(): Boolean {
        val lm = activity?.getSystemService(
            Context.LOCATION_SERVICE
        ) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    // фукнция с помощбю которой мы будем получать сведения
    // о местополож, только один раз получаем
    private fun getLocation() {
    //        if (!isLocationEmable()) {
    //            Toast.makeText( requireContext(), "Location disabled!", Toast.LENGTH_LONG ).show()
    //            return }
        // создаем конселейшен токен, чтобы его передать
        val ct = CancellationTokenSource()
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) { return }
        //добавляем спец слушатель куда будем получать результ местополож
        fLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, ct.token)
            .addOnCompleteListener {
                requestWeatherData("${it.result.latitude},${it.result.longitude}")
            }
    }
    private fun updateCurrentCard() = with(binding) {
        //viewLifecycleOwner Знает циклы активити  знает когда надо обновлять
        model.liveDataCurrent.observe(viewLifecycleOwner) {
            val maxMinTemp = "${it.maxTemp}° / ${it.minTemp}°"
            tvData.text = it.time
            tvCity.text = it.city
            tvCurrentTemp.text = it.currentTemp.ifEmpty { maxMinTemp }
            tvCondition.text = it.condition
            tvmaxmin.text = if (it.currentTemp.isEmpty()) "" else maxMinTemp
            Picasso.get().load("https:" + it.imageUrl).into(imWeather)
        }
    }

    // нам нужен специальный ЛАНЧЕР вывод спец диалога
    private fun permissionListener() {
        pLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            Toast.makeText(activity, "Permission is $it", Toast.LENGTH_LONG).show()
        }
        // здесь будем создавать специал коллБек, с помощью которого будем ждать ответа от пользователя (все про местоположение)
        // после всего запускаем эту функцию регистрируем
    }

    // димонстрируем регистрацию вверхней фун если нет разрешения
    private fun checkPermission() {
        if (!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            // если нет разрешения делаес следующий код   2передаем разрешение которое Это название которое мы хотим спросить(выводим диалог )
            permissionListener()
            pLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun requestWeatherData(city: String) {
        val url = "https://api.weatherapi.com/v1/forecast.json?key=" + API_KEY + " &q=" + city + "&days=" + "3" + "&aqi=no&alerts=no"
        val queue = Volley.newRequestQueue(context) // очередь
        val request = StringRequest(Request.Method.GET, // теперь надо передать ссылку
            url, {
                //резолт и есть результат
                    result ->
                parseWeatherData(result)
            }, { //слушитель ошибок
                    error ->
                Log.d("MyLoge", "Ошибка бах-бах: $error")
            })
        queue.add(request)
    }

    private fun parseWeatherData(result: String) {
        //весь JSON формат для одного дня
        val mainObject = JSONObject(result)
        val list = parseDays(mainObject)
        parseCurrentData(mainObject, list[0])
    }

    // для всех дней
    private fun parseDays(mainObject: JSONObject): List<WeatherModel> {

        val list = ArrayList<WeatherModel>()
        val daysArray = mainObject.getJSONObject("forecast").getJSONArray("forecastday")
        val name = mainObject.getJSONObject("location").getString("name")

        for (i in 0 until daysArray.length()) {
            val day = daysArray[i] as JSONObject
            val item = WeatherModel(
                name,
                day.getString("date"),
                day.getJSONObject("day").getJSONObject("condition").getString("text"),
                "",
                day.getJSONObject("day").getString("maxtemp_c").toFloat().toInt().toString(),
                day.getJSONObject("day").getString("mintemp_c").toFloat().toInt().toString(),
                day.getJSONObject("day").getJSONObject("condition").getString("icon"),
                day.getJSONArray("hour").toString()
            )
            list.add(item)
        }
        model.liveDataList.value = list
        return list
    }

    private fun parseCurrentData(mainObject: JSONObject, weatherItem: WeatherModel) {

        val item = WeatherModel( // отсюда будем доставать стрингу
            mainObject.getJSONObject("location").getString("name"),
            mainObject.getJSONObject("current").getString("last_updated"),
            mainObject.getJSONObject("current").getJSONObject("condition").getString("text"),
            mainObject.getJSONObject("current").getString("temp_c").toFloat().toInt().toString(),
            weatherItem.maxTemp,
            weatherItem.minTemp,
            mainObject.getJSONObject("current").getJSONObject("condition").getString("icon"),
            weatherItem.hours
        ) // когда заполнили item его нужно передать в лайфДатаКаррент
        //мутэйблЛайфдата ждет от нас значения
        // а с помощью observe мы ждем информацию, безопасно
        model.liveDataCurrent.value = item

        Log.d("MyLoge", "Данные получаем: ${item.maxTemp}")
        Log.d("MyLoge", "Данные получаем: ${item.minTemp}")
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}
