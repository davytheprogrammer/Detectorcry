package com.androidlead.loginappui.ui.screen.doctor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.androidlead.loginappui.R
import com.androidlead.loginappui.ui.theme.PrimaryGreen
import com.androidlead.loginappui.ui.theme.PrimaryViolet

data class Doctor(
    val name: String,
    val specialty: String,
    val clinic: String,
    val rating: Float,
    val distance: String,
    val imageUrl: String,
    val available: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorScreen() {  // Renamed from Doctor() to avoid conflict with data class
    var searchQuery by remember { mutableStateOf("") }
    var activeSearch by remember { mutableStateOf(false) }

    val doctors = remember {
        listOf(
            Doctor(
                "Dr. Sarah Johnson",
                "Pediatrician",
                "Sunshine Children's Clinic",
                4.8f,
                "0.8 km",
                "https://placeholder.com/doctor1",
                true
            ),
            Doctor(
                "Dr. Michael Chen",
                "Pediatric Specialist",
                "KidsFirst Medical Center",
                4.9f,
                "1.2 km",
                "https://placeholder.com/doctor2",
                true
            ),
            Doctor(
                "Dr. Emma Williams",
                "Child Development Expert",
                "Growth & Care Pediatrics",
                4.7f,
                "2.1 km",
                "https://placeholder.com/doctor3",
                false
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Search Bar
        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            onSearch = { activeSearch = false },
            active = activeSearch,
            onActiveChange = { activeSearch = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("Search doctors or clinics...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            trailingIcon = if (searchQuery.isNotEmpty()) {
                {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear search")
                    }
                }
            } else null,
            colors = SearchBarDefaults.colors(
                containerColor = Color.White,
                dividerColor = Color.Transparent
            )
        ) {
            // Search suggestions can be added here
        }

        // Filters
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            var selectedFilter by remember { mutableStateOf(0) }
            val filters = listOf("Nearest", "Available Now", "Highest Rated")

            filters.forEachIndexed { index, filter ->
                if (index > 0) {
                    Spacer(modifier = Modifier.width(8.dp))
                }
                FilterChip(
                    selected = selectedFilter == index,
                    onClick = { selectedFilter = index },
                    label = { Text(filter) },
                    leadingIcon = if (index == 0) {
                        { Icon(Icons.Default.LocationOn, contentDescription = null) }
                    } else null
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(doctors) { doctor ->
                DoctorCard(doctor)
            }
        }
    }
}

@Composable
fun DoctorCard(doctor: Doctor) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Doctor Image with proper error handling
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(doctor.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Doctor's photo",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                error = painterResource(R.drawable.default_doctor_image) // Add a default image resource
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = doctor.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = doctor.specialty,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Text(
                    text = doctor.clinic,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFFB800),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = String.format("%.1f", doctor.rating),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    Text(
                        text = " • ${doctor.distance}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = if (doctor.available) "Available" else "Busy",
                    color = if (doctor.available) PrimaryGreen else Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .background(
                            if (doctor.available) PrimaryGreen.copy(alpha = 0.1f)
                            else Color.Red.copy(alpha = 0.1f),
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryViolet
                    ),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("Book")
                }
            }
        }
    }
}