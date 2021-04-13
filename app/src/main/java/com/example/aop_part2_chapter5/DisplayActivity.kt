package com.example.aop_part2_chapter5

import android.content.Context
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.concurrent.schedule

class DisplayActivity : AppCompatActivity() {

    private val DisplayImage: ImageView by lazy {
        findViewById(R.id.DisplayImage)
    }

    private val DisplayImageBack: ImageView by lazy {
        findViewById(R.id.DisplayImageBack)
    }

    private var currentPosition: Int = 0

    lateinit var imageList: ArrayList<String>

    private lateinit var handler: Handler
    private lateinit var Myrunnable: Runnable
    private lateinit var displayTimer: Timer
    private lateinit var checkOrientationTimer: Timer
    private lateinit var checkOrientationTask: TimerTask
    private lateinit var imageDisplayTask: TimerTask

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display)

        //todo: timer, Runnable, task, handler 초기화
        initView()

        //todo: intent로 넘어온 이미지 리스트 세팅
        getImageList()

        //todo: 핸드폰 화면이 가로일 때만 전자 액자 실행
        checkOrientation()
    }


    private fun initView() {
        handler = Handler(mainLooper)

        Myrunnable = Runnable {
            showToastMessge(this@DisplayActivity)
        }
    }

    private fun createDisplayTimer() : Timer{
        val timer = Timer(false)
        return timer
    }
    private fun createCheckOrientationTimer() : Timer{
        val timer = Timer(false)
        return timer
    }

    private fun createCheckOrientationTask() : TimerTask{
        val task = object : TimerTask() {
            override fun run() {
                if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    handler.removeCallbacks(Myrunnable)
                    startDisplayImage()
                } else {
                    handler.postDelayed(Myrunnable, 1000)
                }
            }
        }
        return task
    }

    private fun createImageDisplayTask() : TimerTask{
        val task = object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    if (imageList.size > 0) {

                        DisplayImage.alpha = 0f
                        DisplayImage.setImageURI(Uri.parse(imageList[currentPosition]))
                        DisplayImage.animate()
                            .alpha(1.0f)
                            .setDuration(1000)
                            .start()

                        currentPosition =
                            if (imageList.size <= currentPosition + 1) 0 else currentPosition+1
                    }
                }
            }
        }
        return task
    }

    private fun getImageList() {
        if (intent.hasExtra("imageList")) {
            imageList = intent.extras!!.getStringArrayList("imageList") as ArrayList<String>
        }
    }

    private fun startDisplayImage() {
        displayTimer = createDisplayTimer()
        imageDisplayTask = createImageDisplayTask()
        displayTimer.schedule(imageDisplayTask, 3000, 3000)
    }

    private fun checkOrientation() {
        checkOrientationTimer = createCheckOrientationTimer()
        checkOrientationTask = createCheckOrientationTask()
        checkOrientationTimer.schedule(checkOrientationTask, 0, 5000)
    }

    private fun showToastMessge(context: Context) {
        Toast.makeText(context, "화면을 가로로 돌려주세요!", Toast.LENGTH_SHORT).show()
    }
}
