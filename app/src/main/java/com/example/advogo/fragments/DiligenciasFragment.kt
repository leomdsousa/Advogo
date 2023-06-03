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
import com.example.advogo.activities.DiligenciaDetalheActivity
import com.example.advogo.adapters.DiligenciasAdapter
import com.example.advogo.databinding.FragmentDiligenciasBinding
import com.example.advogo.models.Diligencia
import com.example.advogo.repositories.IDiligenciaRepository
import com.example.advogo.utils.Constants
import com.example.advogo.utils.DataSelecionadaDecorator
import com.prolificinteractive.materialcalendarview.CalendarDay
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


@AndroidEntryPoint
class DiligenciasFragment : BaseFragment() {
    private lateinit var binding: FragmentDiligenciasBinding
    @Inject lateinit var diligenciaRepository: IDiligenciaRepository

    private var diligencias: List<Diligencia> = ArrayList()

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDiligenciasBinding.inflate(layoutInflater, container, false)

        configurarCalendarView()

        return binding.root
    }

    private fun configurarCalendarView() {
        val dataSelecionadaMap = HashMap<CalendarDay, Int>()

        val decorator = DataSelecionadaDecorator(dataSelecionadaMap)
        binding.calendarView.addDecorator(decorator)

        binding.calendarView
            .state().edit()
            .setMinimumDate(CalendarDay.from(2023, Calendar.JANUARY + 1, 1))
            .setMaximumDate(CalendarDay.from(2023, Calendar.DECEMBER + 1, 31))
            .commit()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fabDiligenciaCadastro.setOnClickListener {
            val intent = Intent(binding.root.context, DiligenciaCadastroActivity::class.java)
            intent.putExtra(Constants.FROM_DILIGENCIA_ACTIVITY, Constants.FROM_DILIGENCIA_ACTIVITY)
            resultLauncher.launch(intent)
        }

        diligenciaRepository.ObterDiligencias(
            { diligencias -> setDiligenciasToUI(diligencias) },
            { null } //TODO("Implementar")
        )

        binding.calendarView.setOnDateChangedListener { widget, date, selected ->
            val data = "${date.year}-${date.month}-${date.day}"

            CoroutineScope(Dispatchers.Main).launch {
                val diligencias = obterDiligenciasPorData(data)
                setDiligenciasToUI(diligencias)
            }
        }

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

    private fun setDiligenciasToUI(lista: List<Diligencia>) {
        //TODO("hideProgressDialog()")

        CoroutineScope(Dispatchers.Main).launch {
            if(lista.isNotEmpty()) {
                binding.rvDiligenciasList.visibility = View.VISIBLE
                binding.tvNoDiligenciasEncontrado.visibility = View.GONE

                binding.rvDiligenciasList.layoutManager = LinearLayoutManager(binding.root.context)
                binding.rvDiligenciasList.setHasFixedSize(true)

                val adapter = DiligenciasAdapter(binding.root.context, lista)
                binding.rvDiligenciasList.adapter = adapter

                adapter.setOnItemClickListener(object :
                    DiligenciasAdapter.OnItemClickListener {
                    override fun onClick(diligencia: Diligencia, position: Int) {
                        val intent = Intent(binding.root.context, DiligenciaDetalheActivity::class.java)
                        intent.putExtra(Constants.DILIGENCIA_PARAM, diligencia)
                        startActivity(intent)
                    }
                })

            } else {
                binding.rvDiligenciasList.visibility = View.GONE
                binding.tvNoDiligenciasEncontrado.visibility = View.VISIBLE
            }
        }
    }

    private suspend fun obterDiligenciasPorData(data: String): List<Diligencia> {
        return withContext(Dispatchers.Main) {
            diligenciaRepository.ObterDiligenciasPorData(data) ?: listOf()
        }
    }

}