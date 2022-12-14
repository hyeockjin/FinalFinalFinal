package com.lx.project5

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.lx.api.BasicClient
import com.lx.data.AwcResponse
import com.lx.data.DogListResponse
import com.lx.project5.databinding.FragmentWriteandlistBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WriteAndListFragment : Fragment() {
    var _binding: FragmentWriteandlistBinding? = null
    val binding get() = _binding!!

    var awrdCommentAdapter: AwrdCommentAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentWriteandlistBinding.inflate(inflater, container, false)

        initView1()
        initView2()
        initList()
        initView()

        return binding.root
    }


    fun initView1(){
        BasicClient.api.getDogInfo(
            requestCode = "1001",
            dogNo = AppData.selectedWriteItem?.dogNo.toString()
        ).enqueue(object: Callback<DogListResponse> {
            override fun onResponse(call: Call<DogListResponse>, response: Response<DogListResponse>) {

                AppData.dogInfo = DogData()

                AppData.dogInfo?.dogImage = response.body()?.data?.get(0)?.dogImage
                AppData.dogInfo?.dogName = response.body()?.data?.get(0)?.dogName
                AppData.dogInfo?.apply{
                    this.dogImage?.let{
                        val uri = Uri.parse("http://192.168.0.10:8001${dogImage}")
                        Glide.with(binding.awrdImage).load(uri).into(binding.awrdImage)
                    }
                    binding.awrdDog.text = AppData.dogInfo?.dogName

                }


            }

            override fun onFailure(call: Call<DogListResponse>, t: Throwable) {
            }

        })

    }



    fun initView2(){
        binding.awrdTitle.text = AppData.selectedWriteItem?.assignTitle
        binding.awrdContent.text = AppData.selectedWriteItem?.assignContent


    }
    // ?????????
    fun initList() {

        // 1. ???????????? ????????? ???????????? ???
        // (LinearLayoutManager : ??????????????? ??????????????? ????????? ???, GridLayoutManager : ?????? ????????? ????????? ???)
        val layoutManager = LinearLayoutManager(context)
        binding.awrdRV.layoutManager = layoutManager

        // 2. ???????????? ???????????? ???
        // ?????? ???????????? ???????????? ??? ???????????? ????????? ??????????????? ???
        awrdCommentAdapter = AwrdCommentAdapter()
        binding.awrdRV.adapter = awrdCommentAdapter

        // 4. ???????????? ???????????? ??? ????????? ?????? ????????????
        awrdCommentAdapter?.listener = object: OnAwrdItemClickListener {
            override fun onItemClick(holder: AwrdCommentAdapter.ViewHolder?, view: View?, position: Int) {
                awrdCommentAdapter?.apply {
                    val item = items.get(position)

                    AppData.selectedCommentItem = item


                    (activity as MainActivity).onFragmentChanged(MainActivity.ScreenItem.ITEMcareInfo)

                }
            }

        }

    }


    fun initView() {

        var awrn = AppData.selectedWriteItem?.awrn.toString()
        Log.v("??????", "${awrn}")
        BasicClient.api.getawcList(
            requestCode = "1001",
            awrn = awrn
        ).enqueue(object: Callback<AwcResponse> {
            override fun onResponse(call: Call<AwcResponse>, response: Response<AwcResponse>) {

                addAwcList(response)
            }

            override fun onFailure(call: Call<AwcResponse>, t: Throwable) {
            }

        })

    }

    fun addAwcList(response: Response<AwcResponse>){

        awrdCommentAdapter?.apply{
            response.body()?.data?.let {
                for(item in it) {
                    this.items.add(AwcData(
                        item.awcn,
                        item.awrn,
                        item.careImage,
                        item.careName,
                        item.careNo,
                        item.comment


                    )
                    )
                }
            }
            Log.v("??????", "${response.body()?.data}")
            this.notifyDataSetChanged()

        }

    }


}