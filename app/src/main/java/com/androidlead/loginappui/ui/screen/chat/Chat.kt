package com.androidlead.loginappui.ui.screen.chat

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.androidlead.loginappui.ui.theme.PrimaryGreen
import com.androidlead.loginappui.ui.theme.PrimaryViolet
import java.text.SimpleDateFormat
import java.util.*

data class ChatMessage(
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val status: MessageStatus = MessageStatus.SENT
)

enum class MessageStatus {
    SENT
}

@Composable
fun ChatScreen() {
    var messages by remember { mutableStateOf(listOf<ChatMessage>()) }
    var inputText by remember { mutableStateOf("") }
    var isTyping by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val suggestionChips = remember {
        listOf(
            "Is my baby sleeping enough?",
            "What's normal temperature for newborns?",
            "Baby feeding schedule",
            "Common baby rashes"
        )
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        messages = messages + ChatMessage(text, true)
        inputText = ""

        // Simulate AI response
        coroutineScope.launch {
            isTyping = true
            delay(1500) // Simulate thinking time
            isTyping = false

            val response = when {
                text.contains("sleeping", ignoreCase = true) ->
                    "Newborns typically sleep 14-17 hours a day in short periods. If your baby is sleeping more or less, we should monitor their patterns."
                text.contains("temperature", ignoreCase = true) ->
                    "Normal temperature for babies is between 97.8°F (36.5°C) and 99°F (37.2°C). Any temperature above 100.4°F (38°C) is considered a fever."
                text.contains("feeding", ignoreCase = true) ->
                    "Newborns typically need to be fed every 2-3 hours. This means feeding 8-12 times within 24 hours."
                text.contains("rash", ignoreCase = true) ->
                    "Common baby rashes include diaper rash, heat rash, and eczema. Most are harmless but should be monitored."
                else ->
                    "I understand your concern. Could you provide more details about what you're observing with your baby?"
            }

            messages = messages + ChatMessage(response, false)

            // Scroll to bottom
            coroutineScope.launch {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Chat Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = PrimaryViolet,
            tonalElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "BabyGuard AI",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    AnimatedVisibility(
                        visible = isTyping,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Text(
                            "Typing...",
                            color = Color.White.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        // Messages
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Suggestion chips at the start
            if (messages.isEmpty()) {
                item {
                    Text(
                        "Common Questions",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Row(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 4.dp)
                    ) {
                        suggestionChips.forEach { suggestion ->
                            SuggestionChip(
                                onClick = { sendMessage(suggestion) },
                                label = { Text(suggestion) },
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                    }
                }
            }

            items(messages) { message ->
                ChatBubble(message)
            }
        }

        // Input Area
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            tonalElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    placeholder = { Text("Type your message...") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                IconButton(
                    onClick = { sendMessage(inputText) },
                    enabled = inputText.isNotBlank()
                ) {
                    Icon(
                        Icons.Default.Send,
                        contentDescription = "Send",
                        tint = if (inputText.isNotBlank()) PrimaryGreen
                        else Color.Gray.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val alignment = if (message.isFromUser) Alignment.End else Alignment.Start
    val bubbleColor = if (message.isFromUser) PrimaryViolet else PrimaryGreen
    val textColor = if (message.isFromUser) Color.White else Color.White
    val bubbleShape = if (message.isFromUser)
        RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp)
    else
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Surface(
            color = bubbleColor,
            shape = bubbleShape,
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = message.content,
                    color = textColor,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Row(
            modifier = Modifier
                .padding(top = 4.dp)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = SimpleDateFormat("HH:mm", Locale.getDefault())
                    .format(Date(message.timestamp)),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            if (message.isFromUser) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Message status",
                    modifier = Modifier.size(16.dp),
                    tint = Color.Gray
                )
            }
        }
    }
}