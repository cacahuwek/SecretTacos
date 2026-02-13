package com.cacahuwek.secretacos
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.Alignment

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val dark = isSystemInDarkTheme()

            MaterialTheme(
                colorScheme = if (dark) darkColorScheme() else lightColorScheme()
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SecretSantaApp()
                }
            }
        }
    }
}

@Composable
fun SecretSantaApp() {
    // 1) Liste en dur
    val allPeople = listOf(
        "Adrien", "Tony", "Goonito", "Awek", "Maxime", "Xoco"
    )

    // 2) État des cases (nom -> coché ?)
    val checked = remember {
        mutableStateMapOf<String, Boolean>().apply {
            allPeople.forEach { put(it, true) } // true = coché par défaut
        }
    }

    val selected = allPeople.filter { checked[it] == true }

    var result by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = 16.dp,
                top = 48.dp,   // <-- augmente ça pour descendre
                end = 16.dp,
                bottom = 16.dp
    )) {
        Text("Participants", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))

        // 3) Checklist
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // prend l'espace dispo
        ) {

            items(allPeople) { name ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically // ✅ centre sur l'axe Y
                    ) {
                        Text(name)
                        Checkbox(
                            checked = checked[name] == true,
                            onCheckedChange = { checked[name] = it }
                        )
                    }
                }
            }
        }
        Text(
            "⚠️ La liste des participants est codée en dur dans le code si besoin de remove/add modifier la liste allPeople.",
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(Modifier.height(8.dp))


        Spacer(Modifier.height(12.dp))

        // 4) Boutons
        Row {
            Button(onClick = {
                try {
                    error = null
                    result = secretSantaAssign(selected)
                } catch (e: IllegalArgumentException) {
                    error = e.message
                    result = emptyMap()
                }
            }) { Text("Générer") }

            Spacer(Modifier.width(8.dp))

            OutlinedButton(onClick = {
                // tout cocher
                allPeople.forEach { checked[it] = true }
                error = null
                result = emptyMap()
            }) { Text("Tout cocher") }

            Spacer(Modifier.width(8.dp))

            OutlinedButton(onClick = {
                // tout décocher
                allPeople.forEach { checked[it] = false }
                error = null
                result = emptyMap()
            }) { Text("Tout décocher") }
        }

        error?.let {
            Spacer(Modifier.height(10.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        if (result.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            Text("Résultat", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            result.forEach { (giver, receiver) ->
                Text("$giver → $receiver")
            }
        }
    }
}

fun secretSantaAssign(names: List<String>): Map<String, String> {
    val cleaned = names.map { it.trim() }.filter { it.isNotEmpty() }.distinct()
    require(cleaned.size >= 2) { "Il faut au moins 2 participants uniques." }

    val shuffled = cleaned.shuffled()
    return shuffled.indices.associate { i ->
        val giver = shuffled[i]
        val receiver = shuffled[(i + 1) % shuffled.size]
        giver to receiver
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SecretSantaAppPreview() {
    MaterialTheme {
        SecretSantaApp()
    }
}
