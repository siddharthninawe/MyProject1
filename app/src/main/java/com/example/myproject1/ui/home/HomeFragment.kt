package com.example.myproject1.ui.home

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myproject1.databinding.FragmentHomeBinding
import com.example.myproject1.ui.services.MusicDownloadService

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

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
    ): View {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val intent = Intent(context, MusicDownloadService::class.java)
        startService(intent)
        context?.bindService(intent, connection, Context.BIND_AUTO_CREATE)

        binding.play.setOnClickListener {

            Log.d(HomeFragment::class.java.name, "Before Bind....")
            sendIPCm(111)
        }

        binding.pause.setOnClickListener {
            Log.d(HomeFragment::class.java.name, "pause.......")
            sendIPCm(112)
        }

        binding.stop.setOnClickListener {
            sendIPCm(113)
            if (bound) {
                context?.unbindService(connection)
                bound = false
            }
            context?.stopService(intent)
        }

        return binding.root
    }

    private fun startService(intent: Intent): ComponentName? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return context?.startForegroundService(intent)
        }
        return context?.startService(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun sendIPCm(data: Int) {
        val msg: Message? = Message.obtain(null, data, 0, 0)
        try {
            messenger?.send(msg)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }
}