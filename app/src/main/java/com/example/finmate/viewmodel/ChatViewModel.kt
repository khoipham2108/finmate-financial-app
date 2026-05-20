package com.example.finmate.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finmate.data.model.Transaction
import com.example.finmate.data.repository.FinanceRepository
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

class ChatViewModel(
    private val repository: FinanceRepository = FinanceRepository()
) : ViewModel() {

    // Using gemini-2.5-flash as the next-generation AI architecture
    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = "AIzaSyCNSjy0QV1M8ymYoLXBL0Tx10vvPhxsQjY",
        systemInstruction = content {
            text("You are FinBot, the friendly personal finance assistant in FinMate. You dynamically adapt to the user's language. If the user inputs in English, reply professionally in English. If the user inputs in Vietnamese, reply naturally in Vietnamese. Your job is to analyze the provided historical transaction data text to give smart budget breakdowns. CRITICAL: Strictly provide factual analysis based on historical data. NEVER make financial predictive forecasts or investment predictions.")
        }
    )

    private var chatSession = generativeModel.startChat()

    private val _messages = mutableStateListOf<ChatMessage>()
    val messages: List<ChatMessage> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Lightweight Compose state for instant UI reflection
    var inputTextState by mutableStateOf("")
        private set

    fun onInputTextChanged(newValue: String) {
        inputTextState = newValue
    }

    fun sendMessage() {
        val userPrompt = inputTextState.trim()
        if (userPrompt.isEmpty()) return

        // Instant UI update
        _messages.add(ChatMessage(userPrompt, true))
        inputTextState = ""
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    // Fetch real-time transactions with safe fallback
                    val transactions = try {
                        repository.getTransactions().firstOrNull() ?: emptyList()
                    } catch (dbEx: Exception) {
                        Log.e("AndroidRuntime", "DATABASE_FETCH_FAILED", dbEx)
                        emptyList<Transaction>()
                    }

                    val promptWithContext = buildPromptWithContext(userPrompt, transactions)
                    
                    // Secure Gemini API call on IO thread
                    chatSession.sendMessage(promptWithContext)
                }

                response.text?.let {
                    _messages.add(ChatMessage(it, false))
                }
            } catch (e: Exception) {
                // Secure failure capture with full stack trace
                Log.e("AndroidRuntime", "GEMINI_CRITICAL_FAILED", e)
                _messages.add(ChatMessage("FinBot encountered a technical error. Details: ${e.localizedMessage}", false))
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun buildPromptWithContext(userPrompt: String, transactions: List<Transaction>): String {
        if (transactions.isEmpty()) return userPrompt
        
        val history = transactions.take(30).joinToString("\n") { 
            "${it.date.toDate()}: ${it.type} ${it.amount} VND, Category: ${it.category}, Note: ${it.note}"
        }
        return "Historical Transaction Data:\n$history\n\nUser Question: $userPrompt"
    }

    fun clearChat() {
        _messages.clear()
        chatSession = generativeModel.startChat()
    }
}
