package com.diep.trackme.page.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.diep.trackme.R
import com.diep.trackme.common.BaseViewModelFactory
import kotlinx.android.synthetic.main.fragment_history.*

/**
 * A simple [Fragment] subclass.
 */
class HistoryFragment : Fragment() {
    private lateinit var viewModel: HistoryViewModel
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!, BaseViewModelFactory {
            HistoryViewModel(activity!!)
        }).get(HistoryViewModel::class.java)
        viewModel.getListRecord()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        historyAdapter = HistoryAdapter(context!!)
        record_list.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = historyAdapter
        }
        viewModel.recordList?.observe(this, Observer {
            historyAdapter.setAdapter(it)
        })
        record_btn.setOnClickListener {
            view.findNavController().navigate(R.id.action_historyFragment_to_recordFragment)
        }
    }
}
