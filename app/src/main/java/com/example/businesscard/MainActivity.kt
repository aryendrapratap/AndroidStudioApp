package com.example.businesscard

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.*
import com.example.businesscard.ui.theme.BusinessCardTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BusinessCardTheme {
                val navController = rememberNavController()
                NavHost(navController, startDestination = "businessCard") {
                    composable("businessCard") {
                        BusinessCard(
                            imageId = R.drawable.profile_picture,
                            firstName = "Aryendra",
                            lastName = "Pratap Singh",
                            role = "Software Developer",
                            phoneNumber = "+1 (123) 456-7890",
                            twitter = "aryendraps18",
                            email = "aryendraps18@gmail.com",
                            onNavigateToSensors = { navController.navigate("sensorScreen") },
                            onNavigateToDice = { navController.navigate("DiceInterface") }
                        )
                    }
                    composable("sensorScreen") {
                        SensorScreen()
                    }
                    composable("DiceInterface") {
                        DiceInterface()
                    }
                }
            }
        }
    }
}

@Composable
fun BusinessCard(
    imageId: Int,
    firstName: String,
    lastName: String,
    role: String,
    phoneNumber: String,
    email: String,
    twitter: String,
    onNavigateToSensors: () -> Unit,
    onNavigateToDice: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Profile(imageId, "$firstName $lastName", role)
        Spacer(modifier = Modifier.height(20.dp))
        Socials(phoneNumber, email, twitter)
        Spacer(modifier = Modifier.height(30.dp))

        Button(onClick = onNavigateToSensors) {
            Text("View Sensor Data")
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(onClick = onNavigateToDice) {
            Text("Roll a Dice")
        }
    }
}

@Composable
fun Profile(imageId: Int, name: String, role: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = imageId),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Text(text = name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(text = role, fontSize = 18.sp, color = Color.Gray)
    }
}

@Composable
fun Socials(phoneNumber: String, email: String, twitter: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "📞 $phoneNumber", fontSize = 16.sp)
        Text(text = "✉️ $email", fontSize = 16.sp)
        Text(text = "🐦 @$twitter", fontSize = 16.sp)
    }
}

@Composable
fun SensorScreen() {
    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }

    var lightLevel by remember { mutableFloatStateOf(0f) }
    var proximity by remember { mutableFloatStateOf(0f) }
    val hasMagnetometer by remember {
        mutableStateOf(sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null)
    }

    val lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    val proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

    DisposableEffect(sensorManager) {
        val sensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    when (it.sensor.type) {
                        Sensor.TYPE_LIGHT -> lightLevel = it.values[0]
                        Sensor.TYPE_PROXIMITY -> proximity = it.values[0]
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        lightSensor?.let { sensorManager.registerListener(sensorListener, it, SensorManager.SENSOR_DELAY_NORMAL) }
        proximitySensor?.let { sensorManager.registerListener(sensorListener, it, SensorManager.SENSOR_DELAY_NORMAL) }

        onDispose {
            sensorManager.unregisterListener(sensorListener)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(text = "Current Light Level: $lightLevel", fontSize = 18.sp)
        Text(text = "Proximity: $proximity", fontSize = 18.sp)
        Text(text = if (hasMagnetometer) "There's a magnetometer." else "No magnetometer found.", fontSize = 18.sp)
    }
}

@Composable
fun DiceInterface() {
    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    var lightLevel by remember { mutableFloatStateOf(0f) }

    val lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

    DisposableEffect(sensorManager) {
        val sensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    if (it.sensor.type == Sensor.TYPE_LIGHT) {
                        lightLevel = it.values[0]
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        lightSensor?.let { sensorManager.registerListener(sensorListener, it, SensorManager.SENSOR_DELAY_NORMAL) }

        onDispose {
            sensorManager.unregisterListener(sensorListener)
        }
    }

    val ValueofD = (( lightLevel.toInt()) % 6 + 1)
    val DiceSideIMG = when (ValueofD) {
        1 -> R.drawable.dice_1
        2 -> R.drawable.dice_2
        3 -> R.drawable.dice_3
        4 -> R.drawable.dice_4
        5 -> R.drawable.dice_5
        else -> R.drawable.dice_6
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Light Level: $lightLevel", fontSize = 18.sp)
        Spacer(modifier = Modifier.height(20.dp))
        Image(
            painter = painterResource(id = DiceSideIMG),
            contentDescription = "Dice Image",
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}
