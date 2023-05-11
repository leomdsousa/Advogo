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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ClienteFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class ClienteFragment : Fragment() {
    private lateinit var binding: FragmentClienteBinding
    @Inject lateinit var _clienteRepository: IClienteRepository

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = FragmentClienteBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        binding.fabClienteCadastro.setOnClickListener {
            val intent = Intent(binding.root.context, ClienteCadastroActivity::class.java)
            intent.putExtra(Constants.FROM_CLIENTE_ACTIVITY, Constants.FROM_CLIENTE_ACTIVITY)
            resultLauncher.launch(intent)
        }

        _clienteRepository.ObterClientes(
            { Clientes -> setClientesToUI(Clientes as ArrayList<Cliente>) },
            { null } //TODO("Implementar")
        )

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data!!.hasExtra(Constants.FROM_CLIENTE_ACTIVITY)) {
                    _clienteRepository.ObterClientes(
                        { lista -> setClientesToUI(lista!! as ArrayList<Cliente>) },
                        { ex -> null } //TODO("Imlementar OnFailure")
                    )
                }
            } else {
                Log.e("Cancelado", "Cancelado")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cliente, container, false)
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
                    intent.putExtra(Constants.CLIENTE_ID_PARAM, model.id)
                    startActivity(intent)
                }
            })

        } else {
            binding.rvClientsList.visibility = View.GONE
            binding.tvNoClientsAvailable.visibility = View.VISIBLE
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ClienteFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ClienteFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}