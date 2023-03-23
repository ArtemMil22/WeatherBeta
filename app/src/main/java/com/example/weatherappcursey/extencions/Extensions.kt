package com.example.weatherappcursey.fragments

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

/*Функция для запроса навигация Булеан будет
ЭкстеншелФункция у него в описании есть 5 урок
Мы будем не одно постоянно спрашивать(чтобы не один и тот же ответ)
, поэтому указываем параметр*/

fun Fragment.isPermissionGranted(p:String): Boolean{
    return ContextCompat.checkSelfPermission(
        activity as AppCompatActivity,p)==PackageManager.PERMISSION_GRANTED
    // ContextCOmpat чтобы проверить разрешение , а check... выдает число
    /* -1 и 0 Денайт(отклонил пользователь) и Гранте(дал разрешение)
    передаем контекст и название разрешение которое можем проверить
     После делаем сравнение с Пермишен Грантед и передаем название разрешения
     один сравниваем с константой
     Делаем ноль = ноль типа, если нет то Фолс
     Проверку будем делать в Main_Activity*/
        // тут проверка на уже имеющееся разрешение !
}