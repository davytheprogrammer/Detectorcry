package com.androidlead.loginappui.ui.screen.home

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.androidlead.loginappui.R
import com.androidlead.loginappui.ui.theme.*
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

data class EmotionalState(
    val primaryEmotion: Emotion,
    val intensity: Int, // 1-10
    val duration: Long // How long this emotion has been active in milliseconds
)

enum class Emotion {
    CALM,      // Peaceful, content state
    HAPPY,     // Showing joy, smiling
    CRYING,    // Tears, obvious distress
    DISTRESSED,// Upset but not crying
    ANGRY,     // Showing frustration or anger
    EXCITED,   // Highly energetic, positive state
    TIRED,     // Showing signs of fatigue
    HUNGRY,    // Showing feeding cues
    UNCOMFORTABLE, // Physical discomfort signs
    PLAYFUL    // Interactive, engaging behavior
}

data class BabyStatus(
    val temperature: Float,
    val heartRate: Int,
    val movement: String,
    val emotionalState: EmotionalState,
    val sleepState: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class Alert(
    val type: AlertType,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val severity: AlertSeverity = AlertSeverity.NORMAL
)

enum class AlertType {
    TEMPERATURE,
    HEART_RATE,
    MOVEMENT,
    EMOTIONAL,
    SLEEP,
    PROLONGED_DISTRESS,
    SUDDEN_CHANGE,
    IRREGULAR_PATTERN
}

enum class AlertSeverity {
    NORMAL,
    WARNING,
    CRITICAL
}

class EmotionTransitionManager {
    private var lastEmotion: Emotion? = null
    private var lastEmotionTimestamp: Long = 0
    private val minEmotionDuration = 30000L // Minimum 30 seconds for an emotion

    fun getNextValidEmotion(currentTime: Long): EmotionalState {
        val possibleEmotions = when (lastEmotion) {
            Emotion.CRYING -> listOf(
                Emotion.CRYING,
                Emotion.DISTRESSED,
                Emotion.HUNGRY,
                Emotion.UNCOMFORTABLE
            )
            Emotion.HAPPY -> listOf(
                Emotion.HAPPY,
                Emotion.PLAYFUL,
                Emotion.EXCITED,
                Emotion.CALM,
                Emotion.TIRED
            )
            Emotion.ANGRY -> listOf(
                Emotion.ANGRY,
                Emotion.CRYING,
                Emotion.DISTRESSED,
                Emotion.UNCOMFORTABLE
            )
            Emotion.CALM -> listOf(
                Emotion.CALM,
                Emotion.HAPPY,
                Emotion.TIRED,
                Emotion.HUNGRY
            )
            else -> Emotion.values().toList()
        }

        // If minimum duration hasn't passed, maintain current emotion
        if (currentTime - lastEmotionTimestamp < minEmotionDuration) {
            return EmotionalState(
                primaryEmotion = lastEmotion ?: possibleEmotions.random(),
                intensity = Random.nextInt(1, 11),
                duration = currentTime - lastEmotionTimestamp
            )
        }

        val newEmotion = possibleEmotions.random()
        lastEmotion = newEmotion
        lastEmotionTimestamp = currentTime
        return EmotionalState(
            primaryEmotion = newEmotion,
            intensity = Random.nextInt(1, 11),
            duration = 0
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedDashboard(
    modifier: Modifier = Modifier,
    onDoctorClick: () -> Unit,
    onChatClick: () -> Unit
) {
    var isMonitoring by remember { mutableStateOf(false) }
    var alerts by remember { mutableStateOf(listOf<Alert>()) }
    var currentStatus by remember { mutableStateOf<BabyStatus?>(null) }
    val scope = rememberCoroutineScope()
    var monitoringJob by remember { mutableStateOf<Job?>(null) }
    val emotionManager = remember { EmotionTransitionManager() }

    fun startMonitoring() {
        monitoringJob?.cancel()
        monitoringJob = scope.launch {
            isMonitoring = true
            while (isActive) {
                val currentTime = System.currentTimeMillis()
                val emotionalState = emotionManager.getNextValidEmotion(currentTime)

                val status = BabyStatus(
                    temperature = 36.5f + Random.nextFloat() * 2,
                    heartRate = 100 + Random.nextInt(40),
                    movement = listOf("Active", "Calm", "Restless").random(),
                    emotionalState = emotionalState,
                    sleepState = listOf("Awake", "Light Sleep", "Deep Sleep").random()
                )
                currentStatus = status

                val newAlerts = mutableListOf<Alert>()

                // Temperature alerts
                if (status.temperature > 37.5f) {
                    newAlerts.add(Alert(
                        AlertType.TEMPERATURE,
                        "High temperature detected: ${String.format("%.1f°C", status.temperature)}",
                        severity = AlertSeverity.WARNING
                    ))
                }

                // Heart rate alerts
                if (status.heartRate > 130) {
                    newAlerts.add(Alert(
                        AlertType.HEART_RATE,
                        "Elevated heart rate: ${status.heartRate} BPM",
                        severity = AlertSeverity.WARNING
                    ))
                }

                // Emotional state alerts
                when (status.emotionalState.primaryEmotion) {
                    Emotion.CRYING -> {
                        if (status.emotionalState.duration > 900000) { // 15 minutes
                            newAlerts.add(Alert(
                                AlertType.PROLONGED_DISTRESS,
                                "Baby has been crying for over 15 minutes",
                                severity = AlertSeverity.WARNING
                            ))
                        }
                    }
                    Emotion.DISTRESSED, Emotion.ANGRY -> {
                        if (status.emotionalState.intensity > 7) {
                            newAlerts.add(Alert(
                                AlertType.EMOTIONAL,
                                "Baby showing high levels of distress",
                                severity = AlertSeverity.WARNING
                            ))
                        }
                    }
                    Emotion.HUNGRY -> {
                        newAlerts.add(Alert(
                            AlertType.EMOTIONAL,
                            "Baby showing hunger cues",
                            severity = AlertSeverity.NORMAL
                        ))
                    }
                    Emotion.UNCOMFORTABLE -> {
                        newAlerts.add(Alert(
                            AlertType.EMOTIONAL,
                            "Baby appears uncomfortable",
                            severity = AlertSeverity.NORMAL
                        ))
                    }
                    else -> {}
                }

                // Sleep state alerts
                if (status.sleepState == "Light Sleep" && status.emotionalState.primaryEmotion in listOf(Emotion.DISTRESSED, Emotion.CRYING)) {
                    newAlerts.add(Alert(
                        AlertType.SLEEP,
                        "Baby showing distress during sleep",
                        severity = AlertSeverity.WARNING
                    ))
                }

                if (newAlerts.isNotEmpty()) {
                    alerts = (newAlerts + alerts).take(10)
                }

                delay(10000) // 10 seconds delay
            }
        }
    }

    fun stopMonitoring() {
        monitoringJob?.cancel()
        isMonitoring = false
    }

    Scaffold { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to PrimaryViolet,
                        1f to PrimaryGreen
                    )
                )
                .padding(paddingValues)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DetectorCry",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { if (isMonitoring) stopMonitoring() else startMonitoring() },
                        modifier = Modifier
                            .background(
                                if (isMonitoring) Color.Red else PrimaryGreen,
                                RoundedCornerShape(8.dp)
                            )
                            .size(40.dp)
                    ) {
                        Icon(
                            if (isMonitoring) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = if (isMonitoring) "Stop Monitoring" else "Start Monitoring",
                            tint = Color.White
                        )
                    }
                }
            }

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    MetricCard(
                        title = "Heart Rate",
                        value = "${currentStatus?.heartRate ?: "--"} bpm",
                        icon = Icons.Default.Favorite,
                        modifier = Modifier.width(160.dp)
                    )
                }
                item {
                    MetricCard(
                        title = "Temperature",
                        value = "${String.format("%.1f°C", currentStatus?.temperature ?: 0f)}",
                        icon = Icons.Default.Thermostat,
                        modifier = Modifier.width(160.dp)
                    )
                }
                item {
                    MetricCard(
                        title = "Emotional State",
                        value = currentStatus?.emotionalState?.primaryEmotion?.name ?: "--",
                        icon = Icons.Default.Face,
                        modifier = Modifier.width(160.dp)
                    )
                }
            }

            // Status Card
            currentStatus?.let { status ->
                StatusCard(
                    status = status,
                    modifier = Modifier.padding(16.dp)
                )
            }

            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onDoctorClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryGreenDark
                    ),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Icon(Icons.Default.LocalHospital, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Contact Doctor")
                }

                Button(
                    onClick = onChatClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryPinkDark
                    ),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Open Chat")
                }
            }

            // Alerts Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Recent Alerts",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.height(200.dp)
                    ) {
                        items(alerts) { alert ->
                            AlertCard(alert)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusCard(
    status: BabyStatus,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = PrimaryGreenDark.copy(alpha = 0.9f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Current Status",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                        .format(Date(status.timestamp)),
                    color = Color.White.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            StatusItem(
                icon = Icons.Default.Thermostat,
                label = "Temperature",
                value = String.format("%.1f°C", status.temperature),
                tint = Color.White
            )

            StatusItem(
                icon = Icons.Default.Favorite,
                label = "Heart Rate",
                value = "${status.heartRate} BPM",
                tint = Color.White
            )

            StatusItem(
                icon = Icons.Default.Face,
                label = "Emotional State",
                value = "${status.emotionalState.primaryEmotion} (Intensity: ${status.emotionalState.intensity}/10)",
                tint = Color.White
            )

            StatusItem(
                icon = Icons.Default.DirectionsRun,
                label = "Movement",
                value = status.movement,
                tint = Color.White
            )

            StatusItem(
                icon = Icons.Default.NightsStay,
                label = "Sleep State",
                value = status.sleepState,
                tint = Color.White
            )
        }
    }
}

@Composable
private fun StatusItem(
    icon: ImageVector,
    label: String,
    value: String,
    tint: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = tint.copy(alpha = 0.7f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = tint,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun AlertCard(alert: Alert) {
    val backgroundColor = when (alert.severity) {
        AlertSeverity.NORMAL -> Color(0xFF4CAF50)
        AlertSeverity.WARNING -> Color(0xFFFFA000)
        AlertSeverity.CRITICAL -> Color(0xFFE53935)
    }

    val icon = when (alert.type) {
        AlertType.TEMPERATURE -> Icons.Default.Thermostat
        AlertType.HEART_RATE -> Icons.Default.Favorite
        AlertType.MOVEMENT -> Icons.Default.DirectionsRun
        AlertType.EMOTIONAL -> Icons.Default.Face
        AlertType.SLEEP -> Icons.Default.NightsStay
        AlertType.PROLONGED_DISTRESS -> Icons.Default.Warning
        AlertType.SUDDEN_CHANGE -> Icons.Default.Timeline
        AlertType.IRREGULAR_PATTERN -> Icons.Default.ShowChart
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = alert.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
                Text(
                    text = SimpleDateFormat("HH:mm", Locale.getDefault())
                        .format(Date(alert.timestamp)),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    icon: ImageVector? = null,
    iconRes: Int? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (icon != null) {
                Icon(
                    icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else if (iconRes != null) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = title,
                    colorFilter = ColorFilter.tint(Color.White),
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

