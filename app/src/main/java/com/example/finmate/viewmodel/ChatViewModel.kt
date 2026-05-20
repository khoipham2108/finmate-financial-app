package com.example.finmate.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finmate.data.model.Transaction
import com.example.finmate.data.repository.FinanceRepository
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

class ChatViewModel(
    private val repository: FinanceRepository = FinanceRepository()
) : ViewModel() {

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = "AIzaSyCNSjy0QV1M8ymYoLXBL0Tx10vvPhxsQjY",
        systemInstruction = content {
            text("You are FinBot, the friendly personal finance assistant embedded inside the FinMate app. You dynamically adapt to the user's language. If the user inputs in English, reply professionally in English. If the user inputs in Vietnamese, reply naturally in Vietnamese.. Your job is twofold: (1) Answer general personal finance, budgeting, and saving questions. (2) Analyze the user's provided transaction history text to give insights on their spending habits. CRITICAL: Strictly provide factual breakdowns based on historical or general data. NEVER make financial predictive forecasts or future investments predictions.")
        }
    )

    private val chat = generativeModel.startChat()

    private val _messages = mutableStateListOf<ChatMessage>()
    val messages: List<ChatMessage> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun sendMessage(userPrompt: String) {
        if (userPrompt.isBlank()) return

        _messages.add(ChatMessage(userPrompt, true))
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val transactions = repository.getTransactions().first()
                val contextPrompt = buildContextPrompt(userPrompt, transactions)
                
                val response = chat.sendMessage(contextPrompt)
                response.text?.let {
                    _messages.add(ChatMessage(it, false))
                }
            } catch (e: Exception) {
                _messages.add(ChatMessage("Error: ${e.localizedMessage}", false))
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun buildContextPrompt(userPrompt: String, transactions: List<Transaction>): String {
        if (transactions.isEmpty()) {
            return "CONTEXT: The user has no transaction history yet. Please friendly guide them to add their first financial record using the '+' button, and provide general saving/budgeting tips instead of analyzing database. USER PROMPT: $userPrompt"
        }

        val historyText = transactions.joinToString("\n") { 
            "- ${it.date.toDate()}: ${it.type} ${it.amount} VND, Category: ${it.category}, Note: ${it.note}"
        }

        return """
            USER_TRANSACTION_HISTORY:
            $historyText
            
            USER_PROMPT: $userPrompt
            
            INSTRUCTIONS: Analyze the prompt using the history above. Map common terms (e.g. 'xăng' to 'Transport', 'phở/ăn sáng' to 'Food'). If the prompt is about spending, give a summary based ONLY on the data provided.
        """.trimIndent()
    }

    fun clearChat() {
        _messages.clear()
        // generativeModel.startChat() doesn't have a clear history, 
        // but we can restart the chat instance if needed or just clear UI.
        // For Gemini SDK, we'd need to re-initialize the chat session to truly clear it.
    }
}
