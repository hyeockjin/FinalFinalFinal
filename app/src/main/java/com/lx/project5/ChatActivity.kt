package com.lx.project5

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.lx.project5.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {

    // 전달자 이름과 아이디
    private lateinit var receiverName: String
    private lateinit var receiverUid: String

    private lateinit var binding:ActivityChatBinding

    //Firebase 인증, db 연결 선언
    lateinit var mAuth:FirebaseAuth
    lateinit var mDbRef:DatabaseReference

    // 받는 쪽과 보내는 대화방 변수 선언 Firebase 안에 생성하기위해
    private lateinit var receiverRoom:String
    private lateinit var senderRoom:String

    // 메세지 타입의 데이터가 들어가는 메시지 목록List
    private lateinit var messageList: ArrayList<Message>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Firebase 인증,db ,메시지 목록 초기화
        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().reference
        messageList = ArrayList()
        val messageAdapter:ChatMessageAdapter = ChatMessageAdapter(this,messageList)

        //  채팅 RecyclerView 적용
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.chatRecyclerView.adapter = messageAdapter

        // 넘어온 데이터 변수 담기
        receiverName = intent.getStringExtra("name").toString()
        receiverUid = intent.getStringExtra("uId").toString()

        //액션바에 상대방 이름 보여주기
        supportActionBar?.title = receiverName

        // 접속자 Uid
        val senderUid = mAuth.currentUser?.uid

        // 보낸이 방 변수 정의
        senderRoom = receiverUid + senderUid
        //보낸이 방 변수 정의
        receiverRoom = senderUid + receiverUid
        // 메시지 전송 버튼 이벤트
        binding.sendButton.setOnClickListener{

            val message = binding.messageEdit.text.toString()
            val messageObject = Message(message,senderUid)

            // 데이터 저장 // 보낸쪽 채팅방과 받는쪽 채팅방 동시에 저장하게 하는 것
            mDbRef.child("chats").child(senderRoom).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    //저장성공하면
                    mDbRef.child("chat").child(receiverRoom).child("messages").push()
                        .setValue(messageObject)
                }
            // 메시지를 전송하고 입력부분을 초기화하는 부분
            binding.messageEdit.setText("")

        }
        // 메시지 가져오기
        mDbRef.child("chats").child(senderRoom).child("messages")
            .addValueEventListener(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for (postSnapshot in snapshot.children){
                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)
                    }

                    // 적용하는 부분
                    messageAdapter.notifyDataSetChanged()
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }
}