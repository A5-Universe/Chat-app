package com.a5universe.chatapp.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.a5universe.chatapp.databinding.ActivityMeetingBinding
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import java.net.MalformedURLException
import java.net.URL

class MeetingActivity : AppCompatActivity() {

    lateinit var binding:ActivityMeetingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMeetingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnJoin.setOnClickListener {
            if (binding.roomCode.text.toString().isEmpty()) {
                Toast.makeText(this, "Please enter room id", Toast.LENGTH_SHORT).show()
            } else {
                // meet joining code..
                try {
                    val options = JitsiMeetConferenceOptions.Builder()
                        .setServerURL(URL("https://meet.jit.si"))
                        .setRoom(binding.roomCode.text.toString())
                        .setFeatureFlag("welcome page.enabled", false)
                        .setConfigOverride("requireDisplayName", true)
                        /*.setAudioMuted(false)
                        .setVideoMuted(false)
                        .setAudioOnly(false)
                        .setFeatureFlag("notifications.enabled", false)
                       */
                        .build()
                    JitsiMeetActivity.launch(this, options)
                } catch (e: MalformedURLException) {
                    e.printStackTrace()
                }
                // meet joining code..
            }
        }

        binding.btnShare.setOnClickListener {
            val string = binding.roomCode.text.toString()
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT, string)
            intent.type = "text/plain"
            startActivity(intent)
        }


    }
}