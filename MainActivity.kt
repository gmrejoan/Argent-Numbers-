package com.argentnumbers.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class EmergencyService(
    val title: String,
    val subtitle: String,
    val number: String,
    val type: String
)

private val services = listOf(
    EmergencyService("National Emergency Service", "Police, fire service and ambulance assistance", "999", "Emergency"),
    EmergencyService("Fire Service Hotline", "Bangladesh Fire Service and Civil Defence", "102", "Fire"),
    EmergencyService("Government information", "Government information and services", "333", "Information"),
    EmergencyService("Child Helpline", "Support for children", "1098", "Emergency")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ArgentNumbersApp() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ArgentNumbersApp() {
    val context = LocalContext.current
    var locationText by remember { mutableStateOf("Location not enabled yet") }
    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }
    var selectedCategory by remember { mutableStateOf("All") }

    val locationPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val granted = result[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            try {
                val manager = context.getSystemService(ComponentActivity.LOCATION_SERVICE) as LocationManager
                val providers = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
                var found: Location? = null
                for (provider in providers) {
                    if (manager.isProviderEnabled(provider) &&
                        context.checkSelfPermission(
                            if (provider == LocationManager.GPS_PROVIDER) Manifest.permission.ACCESS_FINE_LOCATION
                            else Manifest.permission.ACCESS_COARSE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        val candidate = manager.getLastKnownLocation(provider)
                        if (candidate != null && (found == null || candidate.time > found.time)) found = candidate
                    }
                }
                if (found != null) {
                    latitude = found.latitude
                    longitude = found.longitude
                    locationText = "Location ready • %.4f, %.4f".format(found.latitude, found.longitude)
                } else {
                    locationText = "Permission granted. Turn on GPS and try again."
                }
            } catch (_: Exception) {
                locationText = "Could not read location. Check phone location settings."
            }
        } else {
            locationText = "Location permission not granted. You can still call hotlines."
        }
    }

    val categories = listOf("All", "Police", "Fire", "Hospital", "Electricity")
    val visibleServices = when (selectedCategory) {
        "Fire" -> services.filter { it.type == "Fire" || it.number == "999" }
        else -> services
    }

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFFB42318),
            secondary = Color(0xFF167D5A),
            background = Color(0xFFF7F8FA),
            surface = Color.White
        )
    ) {
        Scaffold(
            containerColor = Color(0xFFF7F8FA),
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text("ARGENT NUMBERS", fontWeight = FontWeight.ExtraBold, fontSize = 19.sp)
                            Text("Bangladesh Emergency Help", fontSize = 12.sp, color = Color.Gray)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFB42318)),
                        shape = RoundedCornerShape(22.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Need help now?", color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.Bold)
                            Text("For an immediate emergency, call Bangladesh National Emergency Service.", color = Color.White.copy(alpha = .9f))
                            Button(
                                onClick = { dialNumber(context, "999") },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFFB42318)),
                                modifier = Modifier.fillMaxWidth().height(54.dp)
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("CALL 999", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                item {
                    Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF167D5A))
                                Spacer(Modifier.width(8.dp))
                                Text("Nearby services", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            }
                            Text(locationText, color = Color.Gray, fontSize = 13.sp)
                            Button(
                                onClick = {
                                    locationPermission.launch(arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    ))
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.LocationOn, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Use my current location")
                            }
                            OutlinedButton(
                                onClick = {
                                    val uri = if (latitude != null && longitude != null)
                                        Uri.parse("geo:$latitude,$longitude?q=hospitals, police stations, fire stations")
                                    else Uri.parse("geo:0,0?q=government hospital near me")
                                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) { Text("Find nearby places on map") }
                        }
                    }
                }
                item {
                    Text("Emergency contacts", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text("Official hotline numbers • Verify local contacts before relying on them", color = Color.Gray, fontSize = 12.sp)
                }
                items(visibleServices) { service ->
                    ServiceCard(service = service, onCall = { dialNumber(context, service.number) })
                }
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3CD)), shape = RoundedCornerShape(14.dp)) {
                        Text(
                            "Important: Nearby station contacts and live ambulance availability are not yet connected. Use 999 for urgent assistance. Keep your location enabled for map search.",
                            modifier = Modifier.padding(14.dp),
                            color = Color(0xFF684D00),
                            fontSize = 13.sp
                        )
                    }
                }
                item {
                    Text("Argent Numbers • Prototype v0.1.0", color = Color.Gray, fontSize = 11.sp, modifier = Modifier.padding(bottom = 10.dp))
                }
            }
        }
    }
}

@Composable
private fun ServiceCard(service: EmergencyService, onCall: () -> Unit) {
    val icon = when (service.type) {
        "Fire" -> Icons.Default.LocalFireDepartment
        "Hospital" -> Icons.Default.LocalHospital
        "Police" -> Icons.Default.Policy
        "Electricity" -> Icons.Default.ElectricBolt
        else -> Icons.Default.Phone
    }
    Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier.size(46.dp).background(Color(0xFFFCE8E6), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) { Icon(icon, contentDescription = null, tint = Color(0xFFB42318)) }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(service.title, fontWeight = FontWeight.Bold)
                Text(service.subtitle, color = Color.Gray, fontSize = 12.sp)
                Text(service.number, color = Color(0xFFB42318), fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            FilledTonalButton(onClick = onCall) {
                Icon(Icons.Default.Phone, contentDescription = "Dial number")
            }
        }
    }
}

private fun dialNumber(context: android.content.Context, number: String) {
    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
    context.startActivity(intent)
}
