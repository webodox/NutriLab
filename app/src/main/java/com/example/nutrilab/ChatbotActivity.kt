package com.example.nutrilab

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class ChatbotActivity : AppCompatActivity() {

    private lateinit var chatHistory: ListView
    private lateinit var messageInput: EditText
    private lateinit var sendButton: Button
    private val messages = ArrayList<String>()
    private lateinit var adapter: ArrayAdapter<String>

    private val OPENAI_API_KEY = "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"
    private val conversationHistory = JSONArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatbot)

        chatHistory = findViewById(R.id.chatHistory)
        messageInput = findViewById(R.id.messageInput)
        sendButton = findViewById(R.id.sendButton)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, messages)
        chatHistory.adapter = adapter

        addMessage("NutriBot: Hi! I'm NutriBot, your personal nutrition assistant. I can help you with meal plans, nutrition advice, and app support. How can I help you today?")

        sendButton.setOnClickListener {
            val userMessage = messageInput.text.toString().trim()
            if (userMessage.isEmpty()) {
                Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            addMessage("You: $userMessage")
            messageInput.setText("")
            sendButton.isEnabled = false
            addMessage("NutriBot: Thinking...")
            sendToOpenAI(userMessage)
        }
    }

    private fun sendToOpenAI(userMessage: String) {
        thread {
            try {
                val messagesArray = JSONArray()

                val systemMessage = JSONObject()
                systemMessage.put("role", "system")
                systemMessage.put("content", "You are NutriBot, a helpful nutrition assistant for the NutriLab app. " +
                        "You help users with meal planning, nutrition advice, calorie tracking, and healthy eating habits. " +
                        "Keep responses concise and friendly. " +
                        "If asked to generate a meal plan, provide a simple daily meal plan with breakfast, lunch, dinner and snacks.")
                messagesArray.put(systemMessage)

                for (i in 0 until conversationHistory.length()) {
                    messagesArray.put(conversationHistory.get(i))
                }

                val userObj = JSONObject()
                userObj.put("role", "user")
                userObj.put("content", userMessage)
                messagesArray.put(userObj)
                conversationHistory.put(userObj)

                val requestBody = JSONObject()
                requestBody.put("model", "gpt-3.5-turbo")
                requestBody.put("messages", messagesArray)
                requestBody.put("max_tokens", 500)

                val url = URL("https://api.openai.com/v1/chat/completions")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("Authorization", "Bearer $OPENAI_API_KEY")
                connection.doOutput = true
                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                val writer = OutputStreamWriter(connection.outputStream)
                writer.write(requestBody.toString())
                writer.flush()

                val response = connection.inputStream.bufferedReader().readText()
                val jsonResponse = JSONObject(response)
                val botReply = jsonResponse
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")

                val botObj = JSONObject()
                botObj.put("role", "assistant")
                botObj.put("content", botReply)
                conversationHistory.put(botObj)

                runOnUiThread {
                    messages.removeAt(messages.size - 1)
                    addMessage("NutriBot: $botReply")
                    sendButton.isEnabled = true
                }

            } catch (e: Exception) {
                runOnUiThread {
                    messages.removeAt(messages.size - 1)
                    addMessage("NutriBot: Error — ${e.message}")
                    sendButton.isEnabled = true
                }
            }
        }
    }

    private fun addMessage(message: String) {
        messages.add(message)
        adapter.notifyDataSetChanged()
        chatHistory.smoothScrollToPosition(messages.size - 1)
    }
}