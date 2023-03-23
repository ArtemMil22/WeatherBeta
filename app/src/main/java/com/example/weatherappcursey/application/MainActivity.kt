package com.example.weatherappcursey.application

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.weatherappcursey.R
import com.example.weatherappcursey.ui.fragments.MainFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /* Майн Активити будем использовать только для того чтобы
        запускать фрагменты, поэтому подключать ВьюБайдинг не будем */
        supportFragmentManager.beginTransaction().replace(
            R.id.placeHolder,
            MainFragment.newInstance())
            .commit()
    // вверху для открытия нашего фрагмента , но нужно указать идентификатор контейнера
        //beginTransaction() специальный класс для того чтобы заменить фрагмент в разметке,
    }
}