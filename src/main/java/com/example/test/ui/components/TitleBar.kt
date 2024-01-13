/*The code in these page is written by Siyuan Peng*/

package com.example.test.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.test.ui.screens.HomeScreenViewModel
import com.example.test.ui.theme.Blue200
import com.example.test.ui.theme.Blue700
import com.example.test.ui.theme.Pink40
import com.example.test.ui.theme.Pink80


@Composable
fun TitleBar(theme: HomeScreenViewModel.Theme, content: @Composable () -> Unit) {
    val backgroundColor = if(theme == HomeScreenViewModel.Theme.Pink){
        Brush.linearGradient(listOf(Pink40, Pink80))
    }
    else{
        Brush.linearGradient(listOf(Blue700, Blue200))
    }
    Row(
        modifier = Modifier
            .background(backgroundColor)
            .fillMaxWidth()
            .height(64.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ){
        content()
    }
}


@Composable
@Preview
fun TitleBarPreview(){
}