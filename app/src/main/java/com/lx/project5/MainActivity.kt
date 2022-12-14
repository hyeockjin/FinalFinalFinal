package com.lx.project5

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.lx.api.BasicClient
import com.lx.data.CareListResponse
import com.lx.data.FileUploadResponse
import com.lx.project5.databinding.ActivityMainBinding
import com.permissionx.guolindev.PermissionX
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat

import java.util.*

class MainActivity : AppCompatActivity() {
    // 주소 바꿔야 하는곳 / BasicApi, myPage
    lateinit var binding: ActivityMainBinding

    var locationClient: FusedLocationProviderClient? = null;

    lateinit var map: GoogleMap

    var myMarker: Marker? = null

    enum class ScreenItem {
        ITEM1,
        ITEM2,
        ITEM3,
        ITEMmyPage,
        ITEMcareInfo,
        ITEMaddDog,
        ITEMassess,
        ITEMcareMain,
        ITEMcareTodolist,
        ITEMcomplete,
        ITEMeditDog,
        ITEMjoin1,
        ITEMjoin2,
        ITEMlogin,
        ITEMupdate,
        ITEMpetInfo,
        ITEMpay,
        ITEMreservation,
        ITEMwrite,
        ITEMwriteList,
        ITEMend,
        ITEMdogList,
        ITEMwrite2,
        ITEMwriteSelect,
        ITEMwriteand,
        ITEMchat
    }


    val dateFormat1 = SimpleDateFormat("yyyyMMddHHmmss")
    var filename: String? = null

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

        if(currentFocus is EditText) {
            currentFocus!!.clearFocus()
        }

        return super.dispatchTouchEvent(ev)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cardView.setOnClickListener{
            onFragmentChanged(ScreenItem.ITEMcareInfo)
        }
        // 주변에 돌봄요청 버튼 눌렀을 때
        binding.writeButton2.setOnClickListener {
            onFragmentChanged(ScreenItem.ITEMwrite2)
        }

        //하단 탭의 버튼을 눌렀을때
        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.tab1 -> {
                    onFragmentChanged(ScreenItem.ITEM1)
                }
                R.id.tab2 -> {
                    onFragmentChanged(ScreenItem.ITEM3)
                }
                R.id.tab3 -> {
                    onFragmentChanged(ScreenItem.ITEMchat)
                }
                R.id.tab4 -> {
                    if(AppData.loginData?.memberId == null){
                        onFragmentChanged(ScreenItem.ITEMlogin)
                    }else if (AppData.loginData?.memberId != null){
                        onFragmentChanged(ScreenItem.ITEMmyPage)
                    }

                }
            }
            return@setOnNavigationItemSelectedListener true
        }
        //화면이 보일 때 첫 화면 보여주기
       // onFragmentChanged(ScreenItem.ITEM1)

        binding.cardView.visibility = View.GONE
        // 위험권한 요청하기
        PermissionX.init(this)
            .permissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    showToast("모든 권한 부여됨.")
                } else {
                    showToast("거부된 권한 있음.")
                }
            }

        // 지도 초기화하기
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync {
            map = it

            // 내 위치 요청하기
            requestLocation()

            // 지도 클릭 시 처리
            map.setOnMapClickListener {
                //showToast("지도 클릭됨 : ${it.latitude}, ${it.longitude}")
                //카드뷰 안보이게
                binding.cardView.visibility = View.GONE
            }

            // 보고있는 지도 영역 구분
            map.setOnCameraIdleListener {
                val bounds = map.projection.visibleRegion.latLngBounds
                //showToast("좌상단 : ${bounds.northeast}, ${bounds.southwest}")

                val zoomLevel = map.cameraPosition.zoom
                println("zoomLevel : ${zoomLevel}")
            }

            // 근처 지도 마커 활성화
            showNearLocationMarker(map)
        }

    }
    fun onFragmentChanged(index:ScreenItem) {
        when(index) {
            ScreenItem.ITEM1 -> {
                supportFragmentManager.beginTransaction().replace(R.id.container, FirstFragment()).commit()
            }
            ScreenItem.ITEM2 -> {
                supportFragmentManager.beginTransaction().replace(R.id.container, SecondFragment()).commit()
            }
            ScreenItem.ITEM3 -> {
                supportFragmentManager.beginTransaction().replace(R.id.container, CareMainFragment()).commit()
            }
            ScreenItem.ITEMmyPage -> {
                supportFragmentManager.beginTransaction().replace(R.id.container, MyPageFragment()).commit()
            }
            ScreenItem.ITEMcareInfo -> {
                supportFragmentManager.beginTransaction().replace(R.id.container, CareInfoFragment()).commit()
            }
            ScreenItem.ITEMaddDog -> {
                supportFragmentManager.beginTransaction().replace(R.id.container, AddDogFragment()).commit()
            }
            ScreenItem.ITEMassess -> {
                supportFragmentManager.beginTransaction().replace(R.id.container, AssessFragment()).commit()
            }
            ScreenItem.ITEMcareMain -> {
                supportFragmentManager.beginTransaction().replace(R.id.container, CareMainFragment()).commit()
            }
            ScreenItem.ITEMcareTodolist -> {
                supportFragmentManager.beginTransaction().replace(R.id.container, CareTodolistFragment()).commit()
            }
            ScreenItem.ITEMcomplete -> {
                supportFragmentManager.beginTransaction().replace(R.id.container, CompleteFragment()).commit()
            }
            ScreenItem.ITEMpetInfo -> {
                supportFragmentManager.beginTransaction().replace(R.id.container, PetInfoFragment()).commit()
            }
            ScreenItem.ITEMeditDog -> {
                supportFragmentManager.beginTransaction().replace(R.id.container, EditDogFragment()).commit()
            }
            ScreenItem.ITEMjoin1 -> {
                supportFragmentManager.beginTransaction().replace(R.id.container, Join1Fragment()).commit()
            }
            ScreenItem.ITEMjoin2 -> {
                supportFragmentManager.beginTransaction().replace(R.id.container, Join2Fragment()).commit()
            }
            ScreenItem.ITEMlogin -> {
                supportFragmentManager.beginTransaction().replace(R.id.container, LoginFragment()).commit()
            }

            ScreenItem.ITEMupdate -> {
                supportFragmentManager.beginTransaction().replace(R.id.container, MemberInfoUpdateFragment()).commit()
            }

            ScreenItem.ITEMpay -> {
                supportFragmentManager.beginTransaction().replace(R.id.container, LoginFragment()).commit()
            }
            ScreenItem.ITEMreservation -> {
                supportFragmentManager.beginTransaction().replace(R.id.container, ReservationFragment()).commit()
            }
            ScreenItem.ITEMwrite -> {
                supportFragmentManager.beginTransaction().replace(R.id.container, WriteFragment()).commit()
            }
            ScreenItem.ITEMwriteList -> {
                supportFragmentManager.beginTransaction().replace(R.id.container, WriteListFragment()).commit()
            }
            ScreenItem.ITEMend -> {
                supportFragmentManager.beginTransaction().replace(R.id.container, EndFragment()).commit()
            }
            ScreenItem.ITEMdogList -> {
                supportFragmentManager.beginTransaction().replace(R.id.container, DogListFragment()).commit()
            }
            ScreenItem.ITEMwrite2 -> {
                supportFragmentManager.beginTransaction().replace(R.id.container, Write2Fragment()).commit()
            }
            ScreenItem.ITEMwriteSelect -> {
                supportFragmentManager.beginTransaction().replace(R.id.container, SelectDogFragment()).commit()
            }
            ScreenItem.ITEMwriteand -> {
                supportFragmentManager.beginTransaction().replace(R.id.container, WriteAndListFragment()).commit()
            }
            ScreenItem.ITEMchat -> {
                supportFragmentManager.beginTransaction().replace(R.id.container, ChatListFragment()).commit()
            }
        }


    }
    fun requestLocation() {
        try {
            // 가장 최근에 확인된 위치 알려주기
            locationClient?.lastLocation?.addOnSuccessListener {
            }
            // 위치클라이언트 만들기
            locationClient = LocationServices.getFusedLocationProviderClient(this)

            // 내위치를 요청할 때 필요한 설정값
            val locationRequest = LocationRequest.create()
            locationRequest.run {
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                interval = 30 * 1000
            }

            // 내위치를 받았을 때 자동으로 호출되는 함수
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    super.onLocationResult(result)

                    for ((index, location) in result.locations.withIndex()) {
                        Log.v("lastkingdom","${location.latitude},${location.longitude}")
                        showCurrentLocation(location)
                    }
                }
            }
            // 내 위치 요청
            locationClient?.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())

        } catch(e:SecurityException) {
            e.printStackTrace()
        }
    }

    // 내 위치의 지도 보여주기
    fun showCurrentLocation(location: Location) {
        val curPoint = LatLng(location.latitude, location.longitude)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 17.0f))

        showMarker(curPoint)

    }

    // 근처 마커 표시
    fun showNearLocationMarker(map: GoogleMap) {
        BasicClient.api.getCareListTest(
            requestCode = "1001"
        ).enqueue(object : Callback<CareListResponse> {
            override fun onResponse(call: Call<CareListResponse>, response: Response<CareListResponse>) {
                Log.v("lastkingdom","근처 마커 활성화 요청 성공")
                val jsonArray = JSONArray(response.body()?.data)
                for (i in 0 until jsonArray.length()) {
                    Log.v("lastkingdom","근처 마커 for문 진입")
                    var latitude = response.body()?.data?.get(i)?.careX
                    var longitude = response.body()?.data?.get(i)?.careY

                    Log.v("lastkingdom","마커 위도 ${latitude.toString()}")
                    Log.v("lastkingdom","마커 위도 ${longitude.toString()}")

                    Log.v("lastkingdom","2")
                    // 1. 마커 옵션 설정 (만드는 과정)
                    var makerOptions = MarkerOptions()
                    makerOptions // LatLng에 대한 어레이를 만들어서 이용할 수도 있다.
                        .position(LatLng(latitude!!, longitude!!))
                        .title(response.body()?.data?.get(i)?.careNo.toString()) // 타이틀.

                    // 2. 마커 생성 (마커를 나타냄)
                    map.addMarker(makerOptions)

                    // 마커클릭
                    map.setOnMarkerClickListener {

                        binding.className.text = response.body()?.data?.get(i)?.careName.toString()
                        binding.classAddress.text = response.body()?.data?.get(i)?.careAddress.toString()
                        binding.classSelf.text = response.body()?.data?.get(i)?.careExperience.toString()
                        WriteSaveData.savecareNo = response.body()?.data?.get(i)?.careNo.toString()
                        binding.cardView.visibility = View.VISIBLE

                        true
                    }
                }
            }
            override fun onFailure(call: Call<CareListResponse>, t: Throwable) {
                Log.v("lastkingdom","근처 마커 활성화 요청 실패")
            }
        })
    }

    fun showMarker(curPoint: LatLng) {
        myMarker?.remove()

        MarkerOptions().also {
            it.position(curPoint)
            it.title("내위치")
            it.icon(BitmapDescriptorFactory.fromResource(R.drawable.dogicon))


            myMarker = map.addMarker(it)
            myMarker?.tag = "1001"
        }

    }

    //게시글에서 사진 찍은거 저장하기
    fun saveFile(bitmap: Bitmap) {
        filename = dateFormat1.format(Date()) + ".jpg"
        bitmap?.apply {
            openFileOutput(filename, Context.MODE_PRIVATE).use {
                this.compress(Bitmap.CompressFormat.JPEG, 100, it)
                it.close()

                showToast("이미지를 파일로 저장함 : ${filename}")

                uploadFile(filename!!)
            }
        }
    }
    fun uploadFile(filename:String){
        // 저장한 파일에 대한 정보를 filePart로 만들기
        val file = File("${filesDir}/${filename}")
        val filePart = MultipartBody.Part.createFormData(
            "photo",
            filename,
            file.asRequestBody("images/jpg".toMediaTypeOrNull())
        )
        // 추가 파라미터가 있는 경우
        val params = hashMapOf<String, String>()

        //api 에 있는 리스트 조회
        BasicClient.api.uploadFile(
            file = filePart,
            params = params
        ).enqueue(object : Callback<FileUploadResponse> {
            override fun onResponse(call: Call<FileUploadResponse>, response: Response<FileUploadResponse>) {
                response.body()?.output?.filename?.apply{
                    AppData.filepath = this
                }
            }
            override fun onFailure(call: Call<FileUploadResponse>, t: Throwable) {
            }
        })
    }

    fun showToast(message:String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    // 데이트폼
    fun nowDate():String{
        val now =  System.currentTimeMillis()
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREAN).format(now)
        return simpleDateFormat
    }
}