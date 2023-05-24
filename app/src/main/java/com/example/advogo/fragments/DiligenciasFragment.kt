package com.example.advogo.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.advogo.activities.DiligenciaCadastroActivity
import com.example.advogo.activities.ProcessoDetalheActivity
import com.example.advogo.adapters.ProcessosAdapter
import com.example.advogo.databinding.FragmentDiligenciasBinding
import com.example.advogo.models.Cliente
import com.example.advogo.models.Diligencia
import com.example.advogo.models.Processo
import com.example.advogo.repositories.IDiligenciaRepository
import com.example.advogo.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DiligenciasFragment : BaseFragment() {
    private lateinit var binding: FragmentDiligenciasBinding
    @Inject lateinit var diligenciaRepository: IDiligenciaRepository

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDiligenciasBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fabDiligenciaCadastro.setOnClickListener {
            val intent = Intent(binding.root.context, DiligenciaCadastroActivity::class.java)
            intent.putExtra(Constants.FROM_CLIENTE_ACTIVITY, Constants.FROM_CLIENTE_ACTIVITY)
            resultLauncher.launch(intent)
        }

        diligenciaRepository.ObterDiligencias(
            { diligencias -> setDiligenciasToUI(diligencias as ArrayList<Diligencia>) },
            { null } //TODO("Implementar")
        )

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data!!.hasExtra(Constants.FROM_DILIGENCIA_ACTIVITY)) {
                    diligenciaRepository.ObterDiligencias(
                        { lista -> setDiligenciasToUI(lista as ArrayList<Diligencia>) },
                        { ex -> null } //TODO("Imlementar OnFailure")
                    )
                }
            } else {
                Log.e("Cancelado", "Cancelado")
            }
        }
    }

    private fun setDiligenciasToUI(lista: ArrayList<Diligencia>) {
        //TODO("hideProgressDialog()")

//        CoroutineScope(Dispatchers.Main).launch {
//            if(lista.size > 0) {
//                binding.rvBoardsList.visibility = View.VISIBLE
//                binding.tvNoBoardsAvailable.visibility = View.GONE
//
//                binding.rvBoardsList.layoutManager = LinearLayoutManager(binding.root.context)
//                binding.rvBoardsList.setHasFixedSize(true)
//
//                val adapter = ProcessosAdapter(binding.root.context, lista)
//                binding.rvBoardsList.adapter = adapter
//
//                adapter.setOnItemClickListener(object :
//                    ProcessosAdapter.OnItemClickListener {
//                    override fun onClick(model: Processo, position: Int) {
//                        val intent = Intent(binding.root.context, ProcessoDetalheActivity::class.java)
//                        intent.putExtra(Constants.PROCESSO_PARAM, model)
//                        startActivity(intent)
//                    }
//                })
//
//            } else {
//                binding.rvBoardsList.visibility = View.GONE
//                binding.tvNoBoardsAvailable.visibility = View.VISIBLE
//            }
//        }
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