/*The code in these page is written by Siyuan Peng*/

package com.example.test.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.test.data.KeyDAO
import com.example.test.data.SettingDAO
import com.example.test.ui.components.ExpandableItem
import com.example.test.ui.components.TitleBar


@Composable
fun SettingsPage(keyDAO: KeyDAO,settingDAO: SettingDAO) {
    val todoViewModel = remember { HomeScreenViewModel(keyDAO,settingDAO) }
    SettingsScreen(todoViewModel)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: HomeScreenViewModel) {

    val name = viewModel.userName.value
    var themeId by remember { mutableStateOf(viewModel.themeId) }
    var priorityId by remember { mutableStateOf(viewModel.priorityId) }
    var text by remember { mutableStateOf(viewModel.userName.value) }
    var selectedFiles by remember { viewModel.userAvatar}

    Scaffold(

        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Column {
                    //Text("Change the settings", fontSize = 20.sp, color= Color.Gray)
                    Text("Change your settings", fontSize = 20.sp, color=Color.Gray)
                    if (name != null) {
                        Text(name, fontWeight = FontWeight.Bold, fontSize = 32.sp )
                    }
                }


            }
        }


    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(Color(255, 247, 209))
                .verticalScroll(rememberScrollState())
        ) {
            Column {
                viewModel.selectedColor.value?.let { it1 ->
                    TitleBar(it1) {
                        IconButton(
                            onClick = { /*TODO*/
                                themeId.value?.let { it2 -> priorityId.value?.let { it3 ->
                                    if (name != null) {
                                        selectedFiles?.let { it4 ->
                                            viewModel.updateSetting(it2,
                                                it3,name, it4
                                            )
                                        }
                                    }
                                } }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = "Detail",
                                tint = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            ExpandableItem(
                title = "Your profile",
                modifier = Modifier
                    .padding(start = 20.dp,end =20.dp)
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ){

                    Text(
                        "Enter Your name",
                        modifier = Modifier.padding(start = 16.dp),
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontSize = 20.sp
                    )
                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                    ){
                        text?.let { it1 ->
                            TextField(
                                value = it1,
                                onValueChange = { newText ->
                                    text = newText
                                },
                                label = { Text("No more than 10 words") },
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                            )
                        }

                        IconButton(
                            onClick = { text?.let { it1 -> viewModel.setName(it1) } },
                            Modifier
                                .size(100.dp)
                                .padding(start = 16.dp)
                        ) {
                            Icon(
                                modifier = Modifier.size(100.dp),
                                imageVector = Icons.Rounded.Check,
                                contentDescription = "Create new task",
                                tint = Color.Black,
                            )
                        }
                    }

                }

            }

            Spacer(modifier = Modifier.height(16.dp))

            ExpandableItem(
                title = "Theme Color",
                modifier = Modifier
                    .padding(start = 20.dp,end =20.dp)
            ) {

                val selectedOption = viewModel.selectedColor.value

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            viewModel.setColor(HomeScreenViewModel.Theme.Blue)
                            themeId.value = 1
                        }
                    ){
                        RadioButton(
                            selected = selectedOption == HomeScreenViewModel.Theme.Blue,
                            onClick = {
                                viewModel.setColor(HomeScreenViewModel.Theme.Blue)
                                themeId.value = 1
                            },
                        )
                        Text(
                            text = "Blue",
                            modifier = Modifier.padding(start = 16.dp)
                        )

                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            viewModel.setColor(HomeScreenViewModel.Theme.Pink)
                            themeId.value = 2
                        }
                    ){
                        RadioButton(
                            selected = selectedOption == HomeScreenViewModel.Theme.Pink,
                            onClick = {
                                viewModel.setColor(HomeScreenViewModel.Theme.Pink)
                                themeId.value = 2
                            },
                        )
                        Text(
                            text = "Pink",
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }

            }

            Spacer(modifier = Modifier.height(16.dp))

            ExpandableItem(
                title = "Priority Color",
                modifier = Modifier
                    .padding(start = 20.dp,end =20.dp)
            ) {

                val selectedPriority = viewModel.selectedPriority.value

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            viewModel.setPriority(HomeScreenViewModel.Priority.Yellow)
                            themeId.value = 2
                        }
                    ){
                        RadioButton(
                            selected = selectedPriority == HomeScreenViewModel.Priority.Yellow,
                            onClick = {
                                viewModel.setPriority(HomeScreenViewModel.Priority.Yellow)
                                themeId.value = 2
                            },
                        )
                        Text(
                            text = "Yellow",
                            modifier = Modifier.padding(start = 16.dp)
                        )

                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            viewModel.setPriority(HomeScreenViewModel.Priority.Red)
                            themeId.value = 1
                        }
                    ){
                        RadioButton(
                            selected = selectedPriority == HomeScreenViewModel.Priority.Red,
                            onClick = {
                                viewModel.setPriority(HomeScreenViewModel.Priority.Red)
                                themeId.value = 1
                            },
                        )
                        Text(
                            text = "Red",
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }

            }
        }
    }
}


//@Composable
//@Preview
//fun SettingScreenPreview(){
//    SettingsPage()
//}
