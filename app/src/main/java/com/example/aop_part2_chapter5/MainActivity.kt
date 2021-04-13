package com.example.aop_part2_chapter5

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    //TODO: RecyclerView 참조 선언
    private val imageRecyclerView: RecyclerView by lazy {
        findViewById(R.id.imageRecyclerView)
    }

    private var uriList = ArrayList<String>()
    private var dataSet = ArrayList<RecyclerViewItem>()
    var recyclerViewAdapter: RecyclerViewAdapter? = null

    private val REQUEST_GET_IMAGE = 105 // GET_IMAGE를 위한 확인 코드
    private val PERMISSIONS_REQUEST_CODE = 100 //READ_EXTERNAL_STORAGE 권한 부여 확인 코드
    private var REQUIRED_PERMISSIONS = arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    //TODO: 사진추가버튼 클릭 이벤트
    fun addButtonClicked(v: View) {
        when (v.id) {
            R.id.addButton -> {
                //todo : 사진 추가시 갤러리 접근 권한 체크
                checkPermission()
            }
        }
    }

    //TODO: 실행버튼 클릭 이벤트
    fun executeButtonClicked(v: View) {

        when (v.id) {
            R.id.executeButton -> {
                startDigitalAlbum(dataSet)
            }
        }
    }

    private fun checkPermission() {
        //todo : 해당 Permission을 부여 받았는지 체크 / 받았으면 "GRANTED", 받지 못했으면 "DENIED"
        val permissionCheck = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        //todo : 권한을 부여받지 않았을 경우
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {

            // 교육용 UI를 띄울 필요가 있는지 체크
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                //todo: 초기에 권한을 거부하고 다시 권한을 재할당 받으려 할때 사용자에게 교육용 UI 제공
                //todo: 교육용 팝업 확인 후 권한 팝업을 띄우는 기능
                showPermissionContextPopUp()
            } else {
                //todo: 권한을 요청하는 팝업 띄우기
                ActivityCompat.requestPermissions(
                    this,
                    REQUIRED_PERMISSIONS,
                    PERMISSIONS_REQUEST_CODE
                )
            }
        } else {
            //TODO: 권한 허용, Selector 화면을 띄우는 과정, 갤러리에서 사진을 선택하는 기능
            navigatePhotos()
        }
    }

    private fun showPermissionContextPopUp() {
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다.")
            .setMessage("전자액자 앱에서 사진을 불러오기 위해 권한이 필요합니다.")
            .setPositiveButton("동의하기") {_, _ ->
                ActivityCompat.requestPermissions(
                    this,
                    REQUIRED_PERMISSIONS,
                    PERMISSIONS_REQUEST_CODE
                )
            }
            .setNegativeButton("취소하기", null)
            .create()
            .show()
    }

    private fun startDigitalAlbum(dataSet: ArrayList<RecyclerViewItem>) {
        val intent = Intent(this@MainActivity, DisplayActivity::class.java)
        //TODO: Main -> Display로 imageList를 넘겨주자

        dataSet.forEach {
            uriList.add(it.imageSource.toString())
        }

        intent.putExtra("imageList", uriList)
        startActivity(intent)
    }

    private fun navigatePhotos() {
        //TODO: SAF 사용해서 사진 가져오기
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_GET_IMAGE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //TODO: 권한 허용
                    navigatePhotos()
                } else {
                    //TODO: 권한 거부
                    Toast.makeText(this, "권한을 거부하였습니다.", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_GET_IMAGE -> {
                    //TODO: RecyclerView 에 사진 추가 작업
                    val item = RecyclerViewItem(data?.data!!)
                    dataSet.add(item)
                    if(recyclerViewAdapter == null) {
                        recyclerViewAdapter = RecyclerViewAdapter(dataSet)
                        imageRecyclerView.layoutManager =
                            GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
                        imageRecyclerView.adapter = recyclerViewAdapter
                    } else recyclerViewAdapter?.notifyDataSetChanged()
                }
                else -> {
                    Toast.makeText(this, "사진을 가져오지 못 했습니다", Toast.LENGTH_SHORT).show()
                }
            }
        } else return
    }
}