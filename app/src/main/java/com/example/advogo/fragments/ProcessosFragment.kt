package com.example.advogo.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.advogo.R
import com.example.advogo.activities.ProcessoCadastroActivity
import com.example.advogo.activities.ProcessoDetalheActivity
import com.example.advogo.adapters.ClientesAdapter
import com.example.advogo.adapters.OptionsAdapter
import com.example.advogo.adapters.ProcessosAdapter
import com.example.advogo.databinding.FragmentProcessosBinding
import com.example.advogo.dialogs.SearchDialog
import com.example.advogo.models.Cliente
import com.example.advogo.models.Processo
import com.example.advogo.repositories.IProcessoRepository
import com.example.advogo.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class ProcessosFragment : BaseFragment() {
    private lateinit var binding: FragmentProcessosBinding
    @Inject lateinit var processoRepository: IProcessoRepository

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    private lateinit var processosLista: List<Processo>
    private var isListaOrdenadaAsc = false
    private var isListaOrdenadaDesc = false
    private var onCreateCarregouLista = false

    private lateinit var dialogOrdenacao: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProcessosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showProgressDialog("Aguarde por favor")

        binding.fabProcessoCadastro.setOnClickListener {
            val intent = Intent(binding.root.context, ProcessoCadastroActivity::class.java)
            intent.putExtra(Constants.FROM_PROCESSO_ACTIVITY, Constants.FROM_PROCESSO_ACTIVITY)
            resultLauncher.launch(intent)
        }

        obterProcessos()
        onCreateCarregouLista = true

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data!!.hasExtra(Constants.FROM_PROCESSO_ACTIVITY)) {
                    processoRepository.obterProcessos(
                        { lista ->
                            setProcessosToUI(lista!! as ArrayList<Processo>)
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
            obterProcessos()
        }

        onCreateCarregouLista = false
        super.onResume()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_processos_acoes, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_ordernar_processos -> {
                showDialogOrdenarProcessos()
                return true
            }
            R.id.action_buscar_processos -> {
                showDialogBuscarProcesso("Buscar Processos", "Título")
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun obterProcessos() {
        processoRepository.obterProcessos(
            { processos ->
                setProcessosToUI(processos as ArrayList<Processo>)
                hideProgressDialog()
            },
            { hideProgressDialog() }
        )
    }

    private fun obterProcesso(value: String) {
        showProgressDialog("Buscando")

        processoRepository.obterProcessosByTituloContains(
            value,
            { processos ->
                setProcessosToUI(processos)
                hideProgressDialog()
            },
            { hideProgressDialog() }
        )
    }

    private fun setProcessosToUI(lista: List<Processo>) {
        processosLista = lista

        CoroutineScope(Dispatchers.Main).launch {
            if(lista.size > 0) {
                binding.rvBoardsList.visibility = View.VISIBLE
                binding.tvNoBoardsAvailable.visibility = View.GONE

                binding.rvBoardsList.layoutManager = LinearLayoutManager(binding.root.context)
                binding.rvBoardsList.setHasFixedSize(true)

                val adapter = ProcessosAdapter(binding.root.context, lista)
                binding.rvBoardsList.adapter = adapter

                adapter.notifyItemChanged(1, null)

                adapter.setOnItemClickListener(object :
                    ProcessosAdapter.OnItemClickListener {
                    override fun onClick(processo: Processo, position: Int, action: String) {
                        val intent = Intent(binding.root.context, ProcessoDetalheActivity::class.java)
                        intent.putExtra(Constants.PROCESSO_PARAM, processo)
                        startActivity(intent)
                    }
                })

            } else {
                binding.rvBoardsList.visibility = View.GONE
                binding.tvNoBoardsAvailable.visibility = View.VISIBLE
            }
        }
    }

    private fun showDialogBuscarProcesso(titulo: String, placeholder: String) {
        val searchDialog = object : SearchDialog(
            requireContext(),
            titulo,
            placeholder) {
            override fun onItemSelected(value: String) {
                obterProcesso(value)
            }
        }

        searchDialog.show()
    }

    private fun obterProcessosPorOrdenacao(selectedOption: String) {
        dialogOrdenacao.dismiss()

        var listaOrdenada: ArrayList<Processo> = ArrayList()

        when(selectedOption) {
            "Crescente (A-Z)" -> {
                //showProgressDialog("Aguarde por favor")
                listaOrdenada = ArrayList(processosLista.sortedBy { it.titulo })
            }
            "Decrescente (Z-A)" -> {
                //showProgressDialog("Aguarde por favor")
                listaOrdenada = ArrayList(processosLista.sortedByDescending { it.titulo })
            }
            "Limpar" -> {
                //showProgressDialog("Aguarde por favor")
                listaOrdenada = ArrayList(processosLista.sortedByDescending { it.data })
            } else -> {
            //Validar o que implementar
        }
        }

        (binding.rvBoardsList.adapter as ProcessosAdapter).updateList(listaOrdenada)
    }

    private fun showDialogOrdenarProcessos() {
        val options = resources.getStringArray(R.array.spinner_ordenar_opcoes)

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Selecione uma opção de ordenação")
        dialogOrdenacao = builder.create()

        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.dialog_list, null)

        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.rvList)
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager

        val adapter = OptionsAdapter(options, ::obterProcessosPorOrdenacao)
        recyclerView.adapter = adapter

        dialogOrdenacao.setView(dialogView)
        dialogOrdenacao.show()
    }
}