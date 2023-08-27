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
import com.example.advogo.activities.ClienteCadastroActivity
import com.example.advogo.activities.ClienteDetalheActivity
import com.example.advogo.adapters.ClientesAdapter
import com.example.advogo.adapters.DiligenciasAdapter
import com.example.advogo.adapters.OptionsAdapter
import com.example.advogo.databinding.DialogProcessoAnexoBinding
import com.example.advogo.databinding.DialogSearchBinding
import com.example.advogo.databinding.FragmentClienteBinding
import com.example.advogo.dialogs.SearchDialog
import com.example.advogo.models.Cliente
import com.example.advogo.models.Diligencia
import com.example.advogo.repositories.IClienteRepository
import com.example.advogo.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ClienteFragment : BaseFragment() {
    private lateinit var binding: FragmentClienteBinding
    private lateinit var bindingSearch: DialogSearchBinding
    @Inject lateinit var clienteRepository: IClienteRepository

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    private lateinit var clientesLista: List<Cliente>
    private var isListaOrdenadaAsc = false
    private var isListaOrdenadaDesc = false
    private var onCreateCarregouLista = false

    private lateinit var dialogOrdenacao: AlertDialog

    private var selectedDialogOrdenacao: Int? = null
    private var selectedSearchText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentClienteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fabClienteCadastro.setOnClickListener {
            val intent = Intent(binding.root.context, ClienteCadastroActivity::class.java)
            intent.putExtra(Constants.FROM_CLIENTE_ACTIVITY, Constants.FROM_CLIENTE_ACTIVITY)
            resultLauncher.launch(intent)
        }

        obterClientes()
        onCreateCarregouLista = true

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data!!.hasExtra(Constants.FROM_CLIENTE_ACTIVITY)) {
                    clienteRepository.obterClientes(
                        { lista ->
                            setClientesToUI(lista as ArrayList<Cliente>)
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
            obterClientes()
        }

        onCreateCarregouLista = false
        super.onResume()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_clientes_acoes, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_ordernar_clientes -> {
                showDialogOrdenarClientes()
                return true
            }
            R.id.action_buscar_clientes -> {
                showDialogBuscarCliente("Buscar Clientes", "Nome")
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun showDialogBuscarCliente(titulo: String, placeholder: String) {
        bindingSearch = DialogSearchBinding.inflate(layoutInflater)

        val searchDialog = object : SearchDialog(
            requireContext(),
            titulo,
            placeholder,
            bindingSearch,
            selectedSearchText
        ) {
            override fun onItemSelected(value: String) {
                selectedSearchText = value
                obterCliente(value)
            }
        }

        searchDialog.show()
    }

    private fun obterClientes() {
        clienteRepository.obterClientes(
            { Clientes ->
                setClientesToUI(Clientes as ArrayList<Cliente>)
                hideProgressDialog()
            },
            { hideProgressDialog() }
        )
    }

    private fun obterCliente(valor: String) {
        showProgressDialog("Buscando")

        clienteRepository.obterClientesByNomeContains(
            valor,
            { clientes ->
                setClientesToUI(clientes)
                hideProgressDialog()
            },
            { hideProgressDialog() }
        )
    }

    private fun setClientesToUI(lista: List<Cliente>) {
        clientesLista = lista

        if(lista.size > 0) {
            binding.rvClientsList.visibility = View.VISIBLE
            binding.tvNoClientsAvailable.visibility = View.GONE

            binding.rvClientsList.layoutManager = LinearLayoutManager(binding.root.context)
            binding.rvClientsList.setHasFixedSize(true)

            val adapter = ClientesAdapter(binding.root.context, lista,
                exibirIconeTelefone = true,
                exibirIconeEmail = true,
                exibirIconeWhatsapp = true
            )
            binding.rvClientsList.adapter = adapter

            adapter.setOnItemClickListener(object :
                ClientesAdapter.OnItemClickListener {
                override fun onClick(cliente: Cliente, position: Int, acao: String?) {
                    val intent = Intent(binding.root.context, ClienteDetalheActivity::class.java)
                    intent.putExtra(Constants.CLIENTE_PARAM, cliente)
                    startActivity(intent)
                }
            })

        } else {
            binding.rvClientsList.visibility = View.GONE
            binding.tvNoClientsAvailable.visibility = View.VISIBLE
        }
    }

    private fun obterClientesPorOrdenacao(selectedOption: String) {
        dialogOrdenacao.dismiss()

        selectedDialogOrdenacao =
            resources.getStringArray(R.array.spinner_ordenar_opcoes).indexOfFirst { it == selectedOption }

        var listaOrdenada: ArrayList<Cliente> = ArrayList()

        when(selectedOption) {
            "Crescente (A-Z)" -> {
                //showProgressDialog("Aguarde por favor")
                listaOrdenada = ArrayList(clientesLista.sortedBy { it.nome })
            }
            "Decrescente (Z-A)" -> {
                //showProgressDialog("Aguarde por favor")
                listaOrdenada = ArrayList(clientesLista.sortedByDescending { it.nome })
            }
            "Limpar" -> {
                //showProgressDialog("Aguarde por favor")
                listaOrdenada = ArrayList(clientesLista.sortedByDescending { it.dataCriacaoTimestamp })
            } else -> {
            //Validar o que implementar
            }
        }

        (binding.rvClientsList.adapter as ClientesAdapter).updateList(listaOrdenada)
    }

    private fun showDialogOrdenarClientes() {
        val options = resources.getStringArray(R.array.spinner_ordenar_opcoes)

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Selecione uma opção de ordenação")
        dialogOrdenacao = builder.create()

        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.dialog_list, null)

        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.rvList)
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager

        val adapter = OptionsAdapter(options, ::obterClientesPorOrdenacao, selectedDialogOrdenacao)
        recyclerView.adapter = adapter

        dialogOrdenacao.setView(dialogView)
        dialogOrdenacao.show()
    }
}