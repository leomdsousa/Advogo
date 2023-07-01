package com.example.advogo.fragments

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.advogo.R
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
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


@AndroidEntryPoint
class DiligenciasFragment : BaseFragment() {
    private lateinit var binding: FragmentDiligenciasBinding
    @Inject lateinit var diligenciaRepository: IDiligenciaRepository

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private var onCreateCarregouLista = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDiligenciasBinding.inflate(layoutInflater, container, false)

        configurarCalendarView()
        configurarSpinnerFiltros()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fabDiligenciaCadastro.setOnClickListener {
            val intent = Intent(binding.root.context, DiligenciaCadastroActivity::class.java)
            intent.putExtra(Constants.FROM_DILIGENCIA_ACTIVITY, Constants.FROM_DILIGENCIA_ACTIVITY)
            resultLauncher.launch(intent)
        }

        obterDiligencias()
        onCreateCarregouLista = true

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
                    diligenciaRepository.obterDiligencias(
                        { lista ->
                            setDiligenciasToUI(lista as ArrayList<Diligencia>)
                            hideProgressDialog()
                        },
                        { hideProgressDialog() }
                    )
                }
            } else {
                Log.e("Cancelado", "Cancelado")
            }
        }
    }

    override fun onResume() {
        if(!onCreateCarregouLista) {
            obterDiligencias()
        }

        onCreateCarregouLista = false
        super.onResume()
    }

    private fun configurarSpinnerFiltros() {
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.spinner_filtros_opcoes,
            android.R.layout.simple_spinner_item
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFiltros.adapter = adapter

        binding.spinnerFiltros.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()

                selectedItem.let { item ->
                    when(item) {
                        "Mensal" -> {
                            val dataInicial = LocalDate.now()
                            val dataFinal = obterProximosDiasUteis(dataInicial, 7).last()

                            val dataInicialStr = "${dataInicial.year}-${dataInicial.month}-${dataInicial.dayOfMonth}"
                            val dataFinalStr = "${dataFinal.year}-${dataFinal.month}-${dataFinal.dayOfMonth}"

                            CoroutineScope(Dispatchers.Main).launch {
                                val diligencias = obterDiligenciasPorData(dataInicialStr, dataFinalStr)
                                setDiligenciasToUI(diligencias)
                            }
                        }
                        "Quinzenal" -> {
                            val dataInicial = LocalDate.now()
                            val dataFinal = obterProximosDiasUteis(dataInicial, 15).last()

                            val dataInicialStr = "${dataInicial.year}-${dataInicial.month}-${dataInicial.dayOfMonth}"
                            val dataFinalStr = "${dataFinal.year}-${dataFinal.month}-${dataFinal.dayOfMonth}"

                            CoroutineScope(Dispatchers.Main).launch {
                                val diligencias = obterDiligenciasPorData(dataInicialStr, dataFinalStr)
                                setDiligenciasToUI(diligencias)
                            }
                        }
                        "Semanal" -> {
                            val dataInicial = LocalDate.now()
                            val dataFinal = obterProximosDiasUteis(dataInicial, 30).last()

                            val dataInicialStr = "${dataInicial.year}-${dataInicial.month}-${dataInicial.dayOfMonth}"
                            val dataFinalStr = "${dataFinal.year}-${dataFinal.month}-${dataFinal.dayOfMonth}"

                            CoroutineScope(Dispatchers.Main).launch {
                                val diligencias = obterDiligenciasPorData(dataInicialStr, dataFinalStr)
                                setDiligenciasToUI(diligencias)
                            }
                        } else -> {
                            //Validar o que implementar
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
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

    private fun obterDiligencias() {
        diligenciaRepository.obterDiligencias(
            { diligencias ->
                setDiligenciasToUI(diligencias)
                hideProgressDialog()
            },
            { hideProgressDialog() }
        )
    }

    private fun setDiligenciasToUI(lista: List<Diligencia>) {
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
            diligenciaRepository.obterDiligenciasPorData(data) ?: listOf()
        }
    }

    private suspend fun obterDiligenciasPorData(dataInicial: String, dataFinal: String): List<Diligencia> {
        return withContext(Dispatchers.Main) {
            diligenciaRepository.obterDiligenciasPorData(dataInicial, dataFinal) ?: listOf()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun obterProximosDiasUteis(dataInicial: LocalDate, quantidadeDias: Int): List<LocalDate> {
        val diasUteis = mutableListOf<LocalDate>()
        var data = dataInicial

        while (diasUteis.size < quantidadeDias) {
            if (data.dayOfWeek != DayOfWeek.SATURDAY && data.dayOfWeek != DayOfWeek.SUNDAY) {
                diasUteis.add(data)
            }
            data = data.plusDays(1)
        }

        return diasUteis
    }
}