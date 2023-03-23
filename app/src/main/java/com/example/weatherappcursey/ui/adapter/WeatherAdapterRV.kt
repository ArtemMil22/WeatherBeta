package com.example.weatherappcursey.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherappcursey.model.WeatherModel
import com.example.weatherappcursey.R
import com.example.weatherappcursey.databinding.ListItemBinding
import com.squareup.picasso.Picasso

class WeatherAdapterRV(val listener: Listener?) : ListAdapter<WeatherModel, WeatherAdapterRV.Holder>(
    Comporator()
) {
    //в конструктор Holder передаем Вью - разметка одного элемента холдера(1шаблон)
// RecyclerView.ViewHolder(view) сюда передаем вью которое будем сохранять(ссылки)
    class Holder(view: View,val listener: Listener?) : RecyclerView.ViewHolder(view) {
        val binding = ListItemBinding.bind(view)
        var itemTemp: WeatherModel? = null
        init{
            itemView.setOnClickListener{
                itemTemp?.let { it1 -> listener?.onClick(it1) }
            }
        }

        //функция которая будет заполнять этот элемент
        //передаем элемнт откуда и будем брать инфу для заполнения
        fun bind(item: WeatherModel) = with((binding)) {
            itemTemp = item
            tvDate.text = item.time
            tvCondition.text = item.condition
            tvTemp.text = item.currentTemp
               .ifEmpty { "${item.maxTemp}°/ ${item.minTemp}°" }
            Picasso.get().load("https:" + item.imageUrl).into(im)
        }
    }
    //item.currentTemp.let{if (item.currentTemp.isEmpty())
    //            {"${item.maxTemp}°C/ ${item.minTemp}°C"} else {"$item°C"}}

    class Comporator : DiffUtil.ItemCallback<WeatherModel>() {
        override fun areItemsTheSame(oldItem: WeatherModel, newItem: WeatherModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: WeatherModel, newItem: WeatherModel): Boolean {
            return oldItem == newItem
        }
    }

    // Основыные методы заполняющие шаблон, запускается столько раз, сколько элементов
    //тут создаем разметку рисуем Ход
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        //создаем view = LayoutInflater. для того чтобы загрузить в память
        //но создать должны из контекста from() во ViewGroup(parent) есть контекст
        //inflate() надувает разметку
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return Holder(view,listener)
    }

    // Заполняется шаблон элементами
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))

    }

    interface Listener{
        fun onClick(item: WeatherModel)
    }
}