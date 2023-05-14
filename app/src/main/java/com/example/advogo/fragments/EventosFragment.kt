package com.example.advogo.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.advogo.R
import com.example.advogo.databinding.FragmentEventosBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EventosFragment : BaseFragment() {
    private lateinit var binding: FragmentEventosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEventosBinding.inflate(layoutInflater, container, false)
        return binding.root
    }



//    calendarView.setOnDateChangedListener { widget, date, selected ->
//        // Obter eventos para a data selecionada
//        val events = getEventsForDate(date)
//
//        // Exibir eventos no ListView
//        eventsListView.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, events)
//    }

//    fun getEventsForDate(date: Date): List<String> {
//        // Conectar-se à base de dados MongoDB
//        val mongoClient = MongoClient("localhost", 27017)
//        val database = mongoClient.getDatabase("myDatabase")
//        val collection = database.getCollection("events")
//
//        // Executar consulta para obter eventos para o mês selecionado
//        val calendar = Calendar.getInstance()
//        calendar.time = date
//        val firstDayOfMonth = calendar.getActualMinimum(Calendar.DAY_OF_MONTH)
//        val lastDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
//        val query = and(
//            gte("dueDate", firstDayOfMonth),
//            lte("dueDate", lastDayOfMonth)
//        )
//        val events = mutableListOf<String>()
//        collection.find(query).forEach {
//            events.add(it["description"].toString())
//        }
//
//        return events
//    }
}