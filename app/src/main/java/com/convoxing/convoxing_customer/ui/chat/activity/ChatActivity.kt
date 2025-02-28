package com.convoxing.convoxing_customer.ui.chat.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.convoxing.convoxing_customer.R
import com.convoxing.convoxing_customer.ui.chat.viewmodel.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatActivity : AppCompatActivity() {

    private val viewModel by viewModels<ChatViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        intent.extras?.let {
            viewModel.topicId = it.getString("topicId", "")
            viewModel.currentSessionId = it.getString("currentSessionId", "")
            viewModel.topicName = it.getString("topicName", "")
            viewModel.category = it.getString("category", "")
            viewModel.isHistory = it.getBoolean("isHistory", false)
            viewModel.isCasual = it.getBoolean("isCasual", false)
            viewModel.isOverview = it.getBoolean("isOverview", false)
        }
    }

}