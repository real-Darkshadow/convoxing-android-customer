package com.convoxing.convoxing_customer.ui.chat.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.convoxing.convoxing_customer.R
import com.convoxing.convoxing_customer.data.remote.models.ChatMessage
import com.convoxing.convoxing_customer.databinding.ItemAiChatBinding
import com.convoxing.convoxing_customer.databinding.ItemUserChatBinding
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.gone
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.visible
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView
import kotlinx.coroutines.delay

class ChatAdapter(
    val activity: FragmentActivity,
    val context: Context,
    private var messages: ArrayList<ChatMessage>,
    private val onAnalysisClick: (message: ChatMessage, position: Int) -> Unit,
    private val collapseKeyboard: () -> Unit,
    private val onPlayAudio: (message: ChatMessage) -> Unit = {}
) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    private val VIEW_TYPE_AI = 0
    private val VIEW_TYPE_USER = 1
    private var isOverviewShown = false
    var isOverviewReceived = false
    private var isHistory = false

    inner class ViewHolder : RecyclerView.ViewHolder {

        lateinit var userMsgBinding: ItemUserChatBinding
        private lateinit var aiMsgBinding: ItemAiChatBinding

        constructor(binding: ItemUserChatBinding) : super(binding.root) {
            this.userMsgBinding = binding
        }

        constructor(binding: ItemAiChatBinding) : super(binding.root) {
            this.aiMsgBinding = binding
        }

        fun setUserMsg(message: ChatMessage, position: Int) {
            if (message.showAnalysisButton) {
                userMsgBinding.analysisBtn.visible()
            } else {
                userMsgBinding.analysisBtn.gone()
            }
            if (isHistory) {
                userMsgBinding.analysisBtn.visible()
            }
            userMsgBinding.apply {
                tvMsg.text = message.content?.trim()
                analysisBtn.setOnClickListener {
                    onAnalysisClick(message, position)
                }
                playAudioBtn.setOnClickListener {
                    onPlayAudio(message) // Call the callback with the message
                }
            }
        }

        fun setAiMsg(message: ChatMessage) {
            aiMsgBinding.apply {
                tvMsg.text = message.content?.trim()
                playAudioBtn.setOnClickListener {
                    onPlayAudio(message) // Call the callback with the message
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_USER -> ViewHolder(
                ItemUserChatBinding.inflate(layoutInflater, parent, false)
            )

            VIEW_TYPE_AI -> ViewHolder(
                ItemAiChatBinding.inflate(layoutInflater, parent, false)
            )

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int = messages.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            VIEW_TYPE_USER -> {
                holder.setUserMsg(messages[position], position)
                // Check if this is the first user message after an AI message
                Handler(Looper.getMainLooper()).postDelayed({
                    val firstUserMsgIndex = messages.indexOfFirst { it.role == "user" }
                    if (position == firstUserMsgIndex &&
                        position > 0 &&
                        messages[position - 1].role == "model" &&
                        !isOverviewShown
                        && !isHistory
                    ) {
                        showSelectTopicTapTarget(holder.userMsgBinding.analysisBtn)
                        isOverviewShown = true
                    }
                }, 500)
            }

            VIEW_TYPE_AI -> {
                holder.setAiMsg(messages[position])
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (messages[position].role) {
            "model" -> VIEW_TYPE_AI
            "user" -> VIEW_TYPE_USER
            else -> throw IllegalArgumentException("Unknown role at position $position")
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(
        msg: ChatMessage, isOverview: Boolean = false
    ) {
        messages.add(msg)
        if (isOverviewReceived.not()) isOverviewShown = !isOverview
        isOverviewReceived = true
        notifyDataSetChanged()
    }

    fun updateChatList(
        msg: ArrayList<ChatMessage>,
        isHistory: Boolean,
    ) {
        messages.clear()
        messages.addAll(msg)
        this.isHistory = isHistory
        notifyDataSetChanged()
    }

    fun updateUserMessage(position: Int, message: ChatMessage) {
        messages[position].userMessageId = message.userMessageId
        messages[position].sessionId = message.sessionId
        messages[position].showAnalysisButton = true
        notifyItemChanged(position)
    }

    private fun showSelectTopicTapTarget(id: ImageView) {
        collapseKeyboard()
        TapTargetView.showFor(
            activity,
            TapTarget.forView(
                id,
                "Check Analysis",
                "Tap here to check the analysis for your message!"
            )
                .outerCircleColor(R.color.light_yellow)
                .titleTextSize(20)
                .descriptionTextSize(16)
                .textColor(R.color.secondary)
                .cancelable(false)
                .transparentTarget(true)
                .targetRadius(60)
                .drawShadow(false),
            object : TapTargetView.Listener() {
                override fun onTargetClick(view: TapTargetView) {
                    super.onTargetClick(view)
                }
            }
        )
    }
}