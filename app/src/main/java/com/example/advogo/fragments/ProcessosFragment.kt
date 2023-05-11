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
import com.example.advogo.activities.ProcessoCadastroActivity
import com.example.advogo.activities.ProcessoDetalheActivity
import com.example.advogo.adapters.ProcessosAdapter
import com.example.advogo.databinding.FragmentProcessosBinding
import com.example.advogo.models.Processo
import com.example.advogo.repositories.IProcessoRepository
import com.example.advogo.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProcessosFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class ProcessosFragment : Fragment() {
    private lateinit var binding: FragmentProcessosBinding
    @Inject lateinit var _processoRepository: IProcessoRepository

    private lateinit var advNome: String
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = FragmentProcessosBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        binding.fabProcessoCadastro.setOnClickListener {
            val intent = Intent(binding.root.context, ProcessoCadastroActivity::class.java)
            intent.putExtra(Constants.ADV_NOME_PARAM, advNome)
            intent.putExtra(Constants.FROM_PROCESSO_ACTIVITY, Constants.FROM_PROCESSO_ACTIVITY)
            resultLauncher.launch(intent)
        }

        _processoRepository.ObterProcessos(
            { processos -> setProcessosToUI(processos as ArrayList<Processo>) },
            { null } //TODO("Implementar")
        )

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data!!.hasExtra(Constants.FROM_PROCESSO_ACTIVITY)) {
                    _processoRepository.ObterProcessos(
                        { lista -> setProcessosToUI(lista!! as ArrayList<Processo>) },
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
        return inflater.inflate(R.layout.fragment_processos, container, false)
    }

    fun setProcessosToUI(lista: ArrayList<Processo>) {
        //TODO("hideProgressDialog()")

        if(lista.size > 0) {
            binding.rvBoardsList.visibility = View.VISIBLE
            binding.tvNoBoardsAvailable.visibility = View.GONE

            binding.rvBoardsList.layoutManager = LinearLayoutManager(binding.root.context)
            binding.rvBoardsList.setHasFixedSize(true)

            val adapter = ProcessosAdapter(binding.root.context, lista)
            binding.rvBoardsList.adapter = adapter

            adapter.setOnItemClickListener(object :
                ProcessosAdapter.OnItemClickListener {
                override fun onClick(model: Processo, position: Int) {
                    val intent = Intent(binding.root.context, ProcessoDetalheActivity::class.java)
                    intent.putExtra(Constants.PROCESSO_ID_PARAM, model.id)
                    startActivity(intent)
                }
            })

        } else {
            binding.rvBoardsList.visibility = View.GONE
            binding.tvNoBoardsAvailable.visibility = View.VISIBLE
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProcessosFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProcessosFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}