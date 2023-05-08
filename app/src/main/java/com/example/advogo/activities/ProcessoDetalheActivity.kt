package com.example.advogo.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.example.advogo.R
import com.example.advogo.databinding.ActivityProcessoDetalheBinding
import com.example.advogo.models.Processo
import java.text.SimpleDateFormat
import java.util.*

class ProcessoDetalheActivity : BaseActivity() {
    private lateinit var binding: ActivityProcessoDetalheBinding
    private lateinit var processoDetalhes: Processo

    private var dataSelecionadaMilliSeconds: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProcessoDetalheBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()
        obterIntentDados()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_processo_delete, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_deletar_processo -> {
                //alertDialogDeletarProcesso(boardDetails.taskList!![taskListPosition].cards!![cardPosition].name)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun updateProcesso() {
//        val card = Card(
//            binding.btnUpdateCardDetails.text.toString(),
//            boardDetails.taskList!![taskListPosition].cards!![cardPosition].createdBy,
//            boardDetails.taskList!![taskListPosition].cards!![cardPosition].assignedTo,
//            selectedColor,
//            selectedDueDateMilliSeconds
//        )
//
//        val taskList: ArrayList<Task> = boardDetails.taskList!!
//        taskList.removeAt(taskList.size - 1)
//
//        boardDetails.taskList!![taskListPosition].cards?.set(cardPosition, card)
//
//        showProgressDialog(resources.getString(R.string.please_wait))
//        FirestoreService().addUpdateTaskList(this@CardDetailsActivity, boardDetails)
    }

    private fun deletarProcesso() {
//        val cardsList = boardDetails.taskList!![taskListPosition].cards!!
//        cardsList.removeAt(cardPosition)
//
//        val taskList: ArrayList<Task> = boardDetails.taskList!!
//        taskList.removeAt(taskList.size - 1)
//
//        taskList[taskListPosition].cards = cardsList
//        FirestoreService().addUpdateTaskList(this@CardDetailsActivity, boardDetails)
    }

    private fun advogadosDialog() {
//        val advogados =
//            boardDetails.taskList!![taskListPosition].cards!![cardPosition].assignedTo
//
//        if (cardAssignedMembersList.size > 0) {
//            for (i in membersDetailList.indices) {
//                for (j in cardAssignedMembersList) {
//                    if (membersDetailList[i].id == j) {
//                        membersDetailList[i].selected = true
//                    }
//                }
//            }
//        } else {
//            for (i in membersDetailList.indices) {
//                membersDetailList[i].selected = false
//            }
//        }
//
//        val listDialog = object : MembersListDialog(
//            this@CardDetailsActivity,
//            membersDetailList,
//            resources.getString(R.string.strSelectMember)
//        ) {
//            override fun onItemSelected(user: User, action: String) {
//                if (action == Constants.SELECT) {
//                    if (!boardDetails.taskList!![taskListPosition].cards!![cardPosition].assignedTo.contains(
//                            user.id
//                        )
//                    ) {
//                        boardDetails.taskList!![taskListPosition].cards!![cardPosition].assignedTo.add(
//                            user.id
//                        )
//                    }
//                } else {
//                    boardDetails.taskList!![taskListPosition].cards!![cardPosition].assignedTo.remove(
//                        user.id
//                    )
//
//                    for (i in membersDetailList.indices) {
//                        if (membersDetailList[i].id == user.id) {
//                            membersDetailList[i].selected = false
//                        }
//                    }
//                }
//
//                setupSelectedMembersList()
//            }
//        }
//        listDialog.show()
    }

    private fun setAdvogadosToUI() {
//        val cardAssignedMembersList =
//            boardDetails.taskList!![taskListPosition].cards!![cardPosition].assignedTo
//
//        val selectedMembersList: ArrayList<SelectedMembers> = ArrayList()
//
//        for (i in membersDetailList.indices) {
//            for (j in cardAssignedMembersList) {
//                if (membersDetailList[i].id == j) {
//                    val selectedMember = SelectedMembers(
//                        membersDetailList[i].id,
//                        membersDetailList[i].image!!
//                    )
//
//                    selectedMembersList.add(selectedMember)
//                }
//            }
//        }
//
//        if (selectedMembersList.size > 0) {
//            selectedMembersList.add(SelectedMembers("", ""))
//
//            binding.tvSelectMembers.visibility = View.GONE
//            binding.tvSelectMembers.visibility = View.VISIBLE
//
//            binding.rvSelectedMembersList.layoutManager = GridLayoutManager(this@CardDetailsActivity, 6)
//
//            val adapter = CardMembersListItemsAdapter(this@CardDetailsActivity, selectedMembersList, true)
//            binding.rvSelectedMembersList.adapter = adapter
//            adapter.setOnItemClickListener(object :
//                CardMembersListItemsAdapter.OnItemClickListener {
//                override fun onClick() {
//                    membersListDialog()
//                }
//            })
//        } else {
//            binding.tvSelectMembers.visibility = View.VISIBLE
//            binding.rvSelectedMembersList.visibility = View.GONE
//        }
    }

    private fun obterIntentDados() {
//        if (intent.hasExtra(Constants.SET_BOARD_DETAIL)) {
//            boardDetails = intent.getParcelableExtra<Board>(Constants.SET_BOARD_DETAIL)!!
//        }
//
//        if (intent.hasExtra(Constants.SET_BOARD_MEMBERS_LIST)) {
//            membersDetailList = intent.getParcelableArrayListExtra(Constants.SET_BOARD_MEMBERS_LIST)!!
//        }
//
//        if (intent.hasExtra(Constants.SET_TASK_LIST_ITEM_POSITION)) {
//            taskListPosition = intent.getIntExtra(Constants.SET_TASK_LIST_ITEM_POSITION, -1)
//        }
//
//        if (intent.hasExtra(Constants.SET_CARD_LIST_ITEM_POSITION)) {
//            cardPosition = intent.getIntExtra(Constants.SET_CARD_LIST_ITEM_POSITION, -1)
//        }
    }

    private fun atualizarProcessoSuccess() {
        //TODO("hideProgressDialog()")
        setResult(RESULT_OK)
        finish()
    }

    private fun atualizarProcessoFailure() {
        //TODO("hideProgressDialog()")
    }

    fun onDatePickerResult(year: Int, month: Int, day: Int) {
//        val sDayOfMonth = if (day < 10) "0$day" else "$day"
//        val sMonthOfYear = if ((month + 1) < 10) "0${month + 1}" else "${month + 1}"
//
//        val selectedDate = "$sDayOfMonth/$sMonthOfYear/$year"
//        binding.tvSelectDueDate.text = selectedDate
//
//        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
//        val theDate = sdf.parse(selectedDate)
//        dataSelecionadaMilliSeconds = theDate!!.time
    }

    private fun alertDialogDeletarProcesso(numeroProcesso: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.atencao))
        builder.setMessage(
            resources.getString(
                R.string.confirmacaoDeletarProcesso,
                numeroProcesso
            )
        )
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton(resources.getString(R.string.sim)) { dialogInterface, which ->
            dialogInterface.dismiss()
            deletarProcesso()
        }

        builder.setNegativeButton(resources.getString(R.string.nao)) { dialogInterface, which ->
            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarProcessoDetalheActivity)

        val actionBar = supportActionBar
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            //actionBar.title = boardDetails.taskList?.get(taskListPosition)!!.cards!![cardPosition].name
        }

        binding.toolbarProcessoDetalheActivity.setNavigationOnClickListener { onBackPressed() }
    }
}