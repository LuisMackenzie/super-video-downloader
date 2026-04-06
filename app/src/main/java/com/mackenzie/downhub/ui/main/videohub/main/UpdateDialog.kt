package com.mackenzie.downhub.ui.main.videohub.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import com.mackenzie.downhub.BuildConfig
import com.mackenzie.downhub.R
import com.mackenzie.downhub.ui.main.videohub.common.Constants
import com.mackenzie.downhub.ui.main.videohub.common.removeVersionSuffix

@Preview(showBackground = true)
@Composable
fun UpdateDialog(
    latestVersion: String = "0.0.0",
    onDismissRequest: (Boolean) -> Unit = { },
    onConfirmation: () -> Unit = { },
) {
    AlertDialog(
        onDismissRequest = { onDismissRequest(false) },
        title = { Text(text = stringResource(id = R.string.dialog_update_title)) },
        text = {
            Column {
                Text(text = stringResource(id = R.string.dialog_update_subtitle))
                Row {
                    Text(text = stringResource(id = R.string.dialog_update_local_version) )
                    Text(
                        text = Constants.SPACE + BuildConfig.VERSION_NAME.removeVersionSuffix(),
                        fontFamily = FontFamily.Monospace
                    )
                }
                Column {
                    Text(text = stringResource(id = R.string.dialog_update_latest_version) )
                    Text(
                        text = latestVersion,
                        color = Color.Red,
                        fontStyle = FontStyle.Italic,
                        fontFamily = FontFamily.Monospace)
                }
            }
        },
        // TODO Fix the icon to be the update icon instead of the launcher icon
        // TODO change the PNG for a vector drawable
        icon = { Image(painter = painterResource(id = R.drawable.ic_launcher), contentDescription = "Update Icon") },
        confirmButton = {
            Button(onClick = {
                onConfirmation()
            }) {
                Text(text = stringResource(id = R.string.dialog_update_accept) + Constants.SPACE + stringResource(id = R.string.app_name))
            }
        }
    )
}