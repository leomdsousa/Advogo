package com.example.advogo.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.advogo.R
import com.example.advogo.activities.ClienteCadastroActivity
import com.example.advogo.activities.ClienteDetalheActivity
import com.example.advogo.activities.ProcessoDetalheActivity
import com.example.advogo.adapters.ClientesAdapter
import com.example.advogo.adapters.ProcessosAdapter
import com.example.advogo.databinding.FragmentClienteBinding
import com.example.advogo.models.Cliente
import com.example.advogo.models.Processo
import com.example.advogo.repositories.IClienteRepository
import com.example.advogo.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ClienteFragment : BaseFragment() {
    private lateinit var binding: FragmentClienteBinding
    @Inject lateinit var clienteRepository: IClienteRepository

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

        clienteRepository.ObterClientes(
            { Clientes -> setClientesToUI(Clientes as ArrayList<Cliente>) },
            { null } //TODO("Implementar")
        )

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data!!.hasExtra(Constants.FROM_CLIENTE_ACTIVITY)) {
                    clienteRepository.ObterClientes(
                        { lista -> setClientesToUI(lista!! as ArrayList<Cliente>) },
                        { ex -> null } //TODO("Imlementar OnFailure")
                    )
                }
            } else {
                Log.e("Cancelado", "Cancelado")
            }
        }
    }

    fun setClientesToUI(lista: ArrayList<Cliente>) {
        //TODO("hideProgressDialog()")

        if(lista.size > 0) {
            binding.rvClientsList.visibility = View.VISIBLE
            binding.tvNoClientsAvailable.visibility = View.GONE

            binding.rvClientsList.layoutManager = LinearLayoutManager(binding.root.context)
            binding.rvClientsList.setHasFixedSize(true)

            val adapter = ClientesAdapter(binding.root.context, lista)
            binding.rvClientsList.adapter = adapter

            adapter.setOnItemClickListener(object :
                ClientesAdapter.OnItemClickListener {
                override fun onClick(model: Cliente, position: Int) {
                    val intent = Intent(binding.root.context, ClienteDetalheActivity::class.java)
                    intent.putExtra(Constants.CLIENTE_PARAM, model)
                    startActivity(intent)
                }
            })

        } else {
            binding.rvClientsList.visibility = View.GONE
            binding.tvNoClientsAvailable.visibility = View.VISIBLE
        }
    }
}