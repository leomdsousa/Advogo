package com.example.advogo.fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.view.setPadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.advogo.R
import com.example.advogo.activities.DiligenciaCadastroActivity
import com.example.advogo.activities.DiligenciaDetalheActivity
import com.example.advogo.adapters.ClientesAdapter
import com.example.advogo.adapters.DiligenciasAdapter
import com.example.advogo.adapters.OptionsAdapter
import com.example.advogo.databinding.FragmentDiligenciasBinding
import com.example.advogo.dialogs.ProcessosDialog
import com.example.advogo.dialogs.SearchDialog
import com.example.advogo.models.Cliente
import com.example.advogo.models.Diligencia
import com.example.advogo.models.Processo
import com.example.advogo.repositories.IDiligenciaRepository
import com.example.advogo.utils.Constants
import com.example.advogo.utils.DataSelecionadaDecorator
import com.prolificinteractive.materialcalendarview.CalendarDay
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class DiligenciasFragment : BaseFragment() {
    private lateinit var binding: FragmentDiligenciasBinding
    @Inject lateinit var diligenciaRepository: IDiligenciaRepository

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    private var isListaOrdenadaAsc = false
    private var isListaOrdenadaDesc = false
    private var onCreateCarregouLista = false

    private var diligencias: List<Diligencia> = arrayListOf()
    private lateinit var dialogFiltros: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDiligenciasBinding.inflate(layoutInflater, container, false)

        obterDiligencias()
        onCreateCarregouLista = true

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fabDiligenciaCadastro.setOnClickListener {
            val intent = Intent(binding.root.context, DiligenciaCadastroActivity::class.java)
            intent.putExtra(Constants.FROM_DILIGENCIA_ACTIVITY, Constants.FROM_DILIGENCIA_ACTIVITY)
            resultLauncher.launch(intent)
        }

        binding.calendarView.setOnDateChangedListener { widget, date, selected ->
            val data = "${date.year}-${date.month.toString().padStart(2, '0')}-${date.day.toString().padStart(2, '0')}"

            CoroutineScope(Dispatchers.Main).launch {
                val diligencias = obterDiligenciasPorData(data)
                this@DiligenciasFragment.diligencias = diligencias
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_diligencia_acoes, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_ordernar_diligencias -> {
                var listaOrdenada: ArrayList<Diligencia>

                if(!isListaOrdenadaAsc && !isListaOrdenadaDesc) {
                    listaOrdenada = ArrayList(diligencias.sortedBy { it.descricao })
                    isListaOrdenadaAsc = true
                    isListaOrdenadaDesc = false
                } else if(!isListaOrdenadaDesc) {
                    listaOrdenada = ArrayList(diligencias.sortedByDescending { it.descricao })
                    isListaOrdenadaAsc = false
                    isListaOrdenadaDesc = true
                } else {
                    listaOrdenada = ArrayList(diligencias.sortedByDescending { it.descricao })
                    isListaOrdenadaAsc = false
                    isListaOrdenadaDesc = false
                }

                (binding.rvDiligenciasList.adapter as DiligenciasAdapter).updateList(listaOrdenada)

                return true
            }
            R.id.action_filtrar_diligencias -> {
                showDialogFiltrosDiligencias()
                return true
            }
            R.id.action_buscar_diligencias -> {
                showDialogBuscarDiligencia("Buscar Diligências", "Título")
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun configurarCalendarView() {
        CoroutineScope(Dispatchers.Main).launch {
            configurarCalendarViewDatas(diligencias)

            binding.calendarView.tileWidth = 150
            binding.calendarView.setPadding(0)

            binding.calendarView
                .state().edit()
                .setMinimumDate(CalendarDay.from(2023, Calendar.JANUARY + 1, 1))
                .setMaximumDate(CalendarDay.from(2023, Calendar.DECEMBER + 1, 31))
                .commit()
        }
    }

    private fun configurarCalendarViewDatas(
        diligencias: List<Diligencia>,
        minDateRange: String? = null,
        maxDateRange: String? = null
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            val dataSelecionadaMap = HashMap<CalendarDay, Int>()
            val calendario = Calendar.getInstance()
            var data: Date

            if(minDateRange != null && maxDateRange != null) {
                val dataRangeMap = HashMap<CalendarDay, Int>()

                for (dataRange in listOf(minDateRange, maxDateRange)) {
                    data =
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            .parse(dataRange)!!

                    calendario.time = data

                    val dataRange = CalendarDay.from(
                        calendario.get(Calendar.YEAR),
                        calendario.get(Calendar.MONTH) + 1,
                        calendario.get(Calendar.DAY_OF_MONTH)
                    )

                    dataRangeMap[dataRange] = Color.CYAN
                }

                binding.calendarView.selectRange(dataRangeMap.keys.first(), dataRangeMap.keys.last())
            }

            for (diligencia in diligencias) {
                data =
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .parse(diligencia.data.toString())!!

                calendario.time = data

                val dataDiligencia = CalendarDay.from(
                    calendario.get(Calendar.YEAR),
                    calendario.get(Calendar.MONTH) + 1,
                    calendario.get(Calendar.DAY_OF_MONTH)
                )

                dataSelecionadaMap[dataDiligencia] = Color.RED
            }

            //val decorator = DataSelecionadaDecorator(dataSelecionadaMap)
            //binding.calendarView.addDecorator(decorator)
            //binding.calendarView.state().edit()
        }
    }

    private fun obterDiligencias() {
        diligenciaRepository.obterDiligencias(
            { diligencias ->
                this.diligencias = diligencias
                setDiligenciasToUI(diligencias)
                configurarCalendarView()
                hideProgressDialog()
            },
            { hideProgressDialog() }
        )
    }

    private fun obterDiligencia(value: String) {
        showProgressDialog("Buscando")

        diligenciaRepository.obterDiligenciasByDescricaoContains(
            value,
            { diligencias ->
                this.diligencias = diligencias
                setDiligenciasToUI(this.diligencias)
                configurarCalendarView()
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showDialogFiltrosDiligencias() {
        val options = resources.getStringArray(R.array.spinner_filtros_opcoes)

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Selecione uma opção de filtro")
        dialogFiltros = builder.create()

        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.dialog_list, null)

        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.rvList)
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager

        val adapter = OptionsAdapter(options, ::obterDiligenciasPorFiltro)
        recyclerView.adapter = adapter

        dialogFiltros.setView(dialogView)
        dialogFiltros.show()
    }

    private fun showDialogBuscarDiligencia(titulo: String, placeholder: String) {
        val searchDialog = object : SearchDialog(
            requireContext(),
            titulo,
            placeholder) {
            override fun onItemSelected(value: String) {
                obterDiligencia(value)
            }
        }

        searchDialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun obterDiligenciasPorFiltro(selectedOption: String) {
        dialogFiltros.dismiss()

        when(selectedOption) {
            "Mensal" -> {
                showProgressDialog("Buscando")

                val dataInicial = LocalDate.now()
                val dataFinal = obterProximosDiasUteis(dataInicial, 30).last()

                val dataInicialStr = "${dataInicial.year}-${dataInicial.monthValue.toString().padStart(2,'0')}-${dataInicial.dayOfMonth.toString().padStart(2,'0')}"
                val dataFinalStr = "${dataFinal.year}-${dataFinal.monthValue.toString().padStart(2,'0')}-${dataFinal.dayOfMonth.toString().padStart(2,'0')}"

                CoroutineScope(Dispatchers.Main).launch {
                    val diligencias = obterDiligenciasPorData(dataInicialStr, dataFinalStr)
                    this@DiligenciasFragment.diligencias = diligencias
                    setDiligenciasToUI(diligencias)
                    configurarCalendarViewDatas(diligencias, dataInicialStr, dataFinalStr)

                    hideProgressDialog()
                }
            }
            "Quinzenal" -> {
                showProgressDialog("Buscando")

                val dataInicial = LocalDate.now()
                val dataFinal = obterProximosDiasUteis(dataInicial, 15).last()

                val dataInicialStr = "${dataInicial.year}-${dataInicial.monthValue.toString().padStart(2,'0')}-${dataInicial.dayOfMonth.toString().padStart(2,'0')}"
                val dataFinalStr = "${dataFinal.year}-${dataFinal.monthValue.toString().padStart(2,'0')}-${dataFinal.dayOfMonth.toString().padStart(2,'0')}"

                CoroutineScope(Dispatchers.Main).launch {
                    val diligencias = obterDiligenciasPorData(dataInicialStr, dataFinalStr)
                    this@DiligenciasFragment.diligencias = diligencias
                    setDiligenciasToUI(diligencias)
                    configurarCalendarViewDatas(diligencias, dataInicialStr, dataFinalStr)

                    hideProgressDialog()
                }
            }
            "Semanal" -> {
                showProgressDialog("Buscando")

                val dataInicial = LocalDate.now()
                val dataFinal = obterProximosDiasUteis(dataInicial, 7).last()

                val dataInicialStr = "${dataInicial.year}-${dataInicial.monthValue.toString().padStart(2,'0')}-${dataInicial.dayOfMonth.toString().padStart(2,'0')}"
                val dataFinalStr = "${dataFinal.year}-${dataFinal.monthValue.toString().padStart(2,'0')}-${dataFinal.dayOfMonth.toString().padStart(2,'0')}"

                CoroutineScope(Dispatchers.Main).launch {
                    val diligencias = obterDiligenciasPorData(dataInicialStr, dataFinalStr)
                    this@DiligenciasFragment.diligencias = diligencias
                    setDiligenciasToUI(diligencias)
                    configurarCalendarViewDatas(diligencias, dataInicialStr, dataFinalStr)

                    hideProgressDialog()
                }
            } else -> {
            //Validar o que implementar
            }
        }
    }
}