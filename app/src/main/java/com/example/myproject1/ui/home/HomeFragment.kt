package com.example.myproject1.ui.home

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myproject1.R
import com.example.myproject1.databinding.FragmentHomeBinding
import com.example.myproject1.ui.services.MusicDownloadService

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var intent: Intent

    private var messenger: Messenger? = null
    private var bound: Boolean = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            messenger = Messenger(p1)
            bound = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            messenger = null
            bound = false
        }

    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.play.setOnClickListener {
            sendIPCmsg(111)
        }

        binding.pause.setOnClickListener {
            sendIPCmsg(112)
        }

        binding.stop.setOnClickListener {
            sendIPCmsg(113)
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        Intent(context, MusicDownloadService::class.java).also { intent ->
            context?.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (bound) {
            context?.unbindService(connection)
            bound = false
        }
        // context?.stopService(intent)
        _binding = null
    }

    private fun sendIPCmsg(data: Int) {
        val msg: Message? = Message.obtain(null, data, 0, 0)
        try {
            messenger?.send(msg)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }
}