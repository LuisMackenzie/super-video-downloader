package com.mackenzie.downhub.ui.main.videohub.main

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mackenzie.downhub.R
import com.mackenzie.downhub.ui.main.videohub.common.getExternalPlayerMode
import com.mackenzie.downhub.ui.main.videohub.common.setExternalPlayerMode

@Preview(showBackground = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppBar() {
    val activity = LocalContext.current as? Activity
    TopAppBar(
        title = { Text(stringResource(R.string.app_name)) },
        actions = {
            AppBarAction(Icons.Default.Search, onClick = { /*TODO*/ })
            AppBarAction(Icons.Default.Settings, onClick = {
                onSettingsClick(activity)
            })
        },
        navigationIcon = {
            NavigationMenuButton(Icons.Default.Menu, onClick = { /*TODO*/ })
        }
    )
}

private fun onSettingsClick(activity : Activity?) {
    val playerMode = activity?.getExternalPlayerMode() ?: false
    activity?.setExternalPlayerMode(!playerMode)
    val updatedMode = activity?.getExternalPlayerMode() ?: false
    Log.e("PlayerMode", "Current mode: ${if (updatedMode) "External Player" else "Internal PLayer"}")
    if (updatedMode) {
        Toast.makeText( activity, "Se ha activado el player Externo", Toast.LENGTH_SHORT).show()
    } else {
        Toast.makeText( activity, "Se esta  usando el player interno", Toast.LENGTH_SHORT).show()
    }
}

@Composable
private fun AppBarAction(
    imageVector: ImageVector,
    onClick: () -> Unit
) {
    IconButton(onClick = {onClick()}) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            tint = contentColorFor(MaterialTheme.colorScheme.onPrimary),
            modifier = Modifier
                .fillMaxHeight()
                .padding(8.dp)
        )
    }
}

@Composable
private fun NavigationMenuButton(
    imageVector: ImageVector,
    onClick: () -> Unit
) {
    IconButton(
        onClick = { onClick() }
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            tint = contentColorFor(MaterialTheme.colorScheme.onPrimary),
            modifier = Modifier
                .fillMaxHeight()
                .padding(8.dp)
        )
    }
}