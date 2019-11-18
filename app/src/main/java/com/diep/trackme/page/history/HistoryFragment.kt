package com.diep.trackme.page.history


import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
    private val REQUEST_CODE = 101
    private lateinit var viewModel: HistoryViewModel
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = activity.run {
            ViewModelProviders.of(activity!!, BaseViewModelFactory {
                HistoryViewModel(context!!)
            }).get(HistoryViewModel::class.java)

        }

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
        checkPermissionForStorage()
        historyAdapter = HistoryAdapter(context!!)
        record_list.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = historyAdapter
        }
        viewModel.recordList.observe(this, Observer {
            historyAdapter.setAdapter(it)
        })
        record_btn.setOnClickListener {
            view.findNavController().navigate(R.id.action_historyFragment_to_recordFragment)
        }
    }

    private fun checkPermissionForStorage() {
        if (ContextCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity!!,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                } else {

                }
                return
            }
        }
    }

}
