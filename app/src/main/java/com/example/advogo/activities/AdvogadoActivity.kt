package com.example.advogo.activities

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.advogo.R
import com.example.advogo.adapters.AdvogadosAdapter
import com.example.advogo.adapters.ClientesAdapter
import com.example.advogo.databinding.ActivityAdvogadoBinding
import com.example.advogo.databinding.FragmentClienteBinding
import com.example.advogo.models.Advogado
import com.example.advogo.models.Cliente
import com.example.advogo.repositories.IAdvogadoRepository
import com.example.advogo.repositories.IClienteRepository
import com.example.advogo.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AdvogadoActivity : BaseActivity() {
    private lateinit var binding: ActivityAdvogadoBinding
    @Inject lateinit var advRepository: IAdvogadoRepository

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdvogadoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar("Advogados", binding.toolbarAdvogados)

        advRepository.ObterAdvogados(
            { lista ->
                setAdvogadosToUI(lista as ArrayList<Advogado>)
                hideProgressDialog()
            },
            { hideProgressDialog() }
        )

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data!!.hasExtra(Constants.FROM_ADVOGADO_ACTIVITY)) {
                    advRepository.ObterAdvogados(
                        { lista ->
                            setAdvogadosToUI(lista as ArrayList<Advogado>)
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

    private fun setAdvogadosToUI(lista: ArrayList<Advogado>) {
        if(lista.size > 0) {
            binding.rvAdvList.visibility = View.VISIBLE
            binding.tvNoAdvAvailable.visibility = View.GONE

            binding.rvAdvList.layoutManager = LinearLayoutManager(binding.root.context)
            binding.rvAdvList.setHasFixedSize(true)

            val adapter = AdvogadosAdapter(this, lista)
            binding.rvAdvList.adapter = adapter

            adapter.setOnItemClickListener(object :
                AdvogadosAdapter.OnItemClickListener {
                override fun onClick(position: Int, advogado: Advogado, action: String?) {
                    val intent = Intent(binding.root.context, AdvogadoDetalheActivity::class.java)
                    intent.putExtra(Constants.ADV_PARAM, advogado)
                    startActivity(intent)
                }
            })

        } else {
            binding.rvAdvList.visibility = View.GONE
            binding.tvNoAdvAvailable.visibility = View.VISIBLE
        }
    }


}