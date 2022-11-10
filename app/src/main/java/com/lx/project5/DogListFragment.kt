package com.lx.project5

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.lx.api.BasicClient
import com.lx.data.DogListResponse
import com.lx.drawer.OnPetItemClickListener
import com.lx.project5.databinding.FragmentDogListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DogListFragment : Fragment() {
    var _binding: FragmentDogListBinding? = null
    val binding get() = _binding!!

    var petAdapter: PetAdapter? = null
    val petInfoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentDogListBinding.inflate(inflater, container, false)

        binding.addDogButton.setOnClickListener {
            (activity as MainActivity).onFragmentChanged(MainActivity.ScreenItem.ITEMaddDog)
        }

        initView()
        petView()

        return binding.root
    }

    // 리스트 초기화
    fun initView() {

        // 1. 리스트의 모양을 담당하는 것
        // (LinearLayoutManager : 아래쪽으로 아이템들이 보이는 것, GridLayoutManager : 격자 형태로 보이는 것)
        val layoutManager = LinearLayoutManager(context)
        binding.dogList.layoutManager = layoutManager

        // 2. 어댑터를 설정하는 것
        // 실제 데이터를 관리하고 각 아이템의 모양을 만들어주는 것
        petAdapter = PetAdapter()
        binding.dogList.adapter = petAdapter

        // 4. 아이템을 클릭했을 때 동작할 코드 넣어주기
        petAdapter?.listener = object: OnPetItemClickListener {
            override fun onItemClick(holder: PetAdapter.ViewHolder?, view: View?, position: Int) {
                petAdapter?.apply {
                    val item = items.get(position)

                    AppData.selectedItem = item

                    // 두번째 부분화면 띄워주기
                    (activity as MainActivity).onFragmentChanged(MainActivity.ScreenItem.ITEMeditDog)

                }
            }

        }

    }


    fun petView() {

        var petIndex =  (activity as MainActivity).petIndex
        if(petIndex.equals("0")){
            BasicClient.api.getPetList(
                requestCode = "1001"
            ).enqueue(object: Callback<DogListResponse> {
                override fun onResponse(call: Call<DogListResponse>, response: Response<DogListResponse>) {

                    showPetList(response)
                }

                override fun onFailure(call: Call<DogListResponse>, t: Throwable) {
                }

            })
        } else {
            BasicClient.api.getPetFilter(
                requestCode = "1001",
                petTheme = petIndex.toString()
            ).enqueue(object: Callback<DogListResponse> {
                override fun onResponse(call: Call<DogListResponse>, response: Response<DogListResponse>) {

                    showPetList(response)
                }

                override fun onFailure(call: Call<DogListResponse>, t: Throwable) {
                }

            })
        }


    }

    fun showPetList(response: Response<DogListResponse>){

        petAdapter?.apply{
            response.body()?.data?.let {
                for(item in it) {
                    this.items.add(PetData(
                        item.dogName,
                        item.dogAge,
                        item.dogGender,
                        item.dogType,
                        item.dogImage
                        )
                    )
                }
            }
            this.notifyDataSetChanged()


        }


    }


}