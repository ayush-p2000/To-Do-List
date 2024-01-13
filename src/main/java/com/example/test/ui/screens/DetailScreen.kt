/*The code in these page is written by Siyuan Peng*/

package com.example.test.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.example.test.model.entity.TodoItem
import com.example.test.ui.components.ExpandableItem
import com.example.test.ui.components.ScrollableContent
import com.example.test.ui.theme.Blue200
import com.example.test.ui.theme.Blue700
import com.example.test.ui.theme.Pink40
import com.example.test.ui.theme.Pink80
import java.io.File


@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun DetailScreen(onButtonClick: () -> Unit, item: TodoItem?, viewModel: HomeScreenViewModel) {
    var title by remember { mutableStateOf(item?.title ?: "") }
    var description by remember { mutableStateOf(item?.details ?: "") }
    var isLocationEnabled by remember { mutableStateOf(false) }
    var isPriorityEnabled by remember { mutableStateOf(item?.priority ?: false) }
    var hasCameraPermission by remember { mutableStateOf(false) }
    var selectedFiles by remember { mutableStateOf<List<Uri>>(item?.pictures?:emptyList()) }
    var todoList by remember { mutableStateOf(listOf(ToDoItem("", true))) }
    var location by remember { mutableStateOf(item?.locationTag ?:"Tesco") }
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val roundedButtonShape = RoundedCornerShape(5.dp)
    var checkList by remember { mutableStateOf(item?.checklist ?: "") }


    fun checkCameraPermission(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            context,
            android.Manifest.permission.CAMERA
        )
    }

    fun newImageFile(): File {
        val timeMillis = System.currentTimeMillis().toString()
        val stroageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("SNAP-$timeMillis", ".jpg", stroageDir)
    }


    val imageFromGalleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedFiles = selectedFiles.toMutableList().apply {
                add(uri)
            }
        }
    }


    if (checkCameraPermission()) {
        hasCameraPermission = true
    } else {
        val requestPermissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                hasCameraPermission = true
            } else {
                Toast.makeText(context, "Camera Permission denied", Toast.LENGTH_SHORT).show()

            }
        }
        SideEffect {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }
    var cameraImageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    val imageFromCameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { isCaptured ->
        if (isCaptured) {
            // Assuming cameraImageUri stores the URI of the captured image
            cameraImageUri?.let {
                selectedFiles = selectedFiles.toMutableList().apply { add(it) }
            }
        }
    }

    if (hasCameraPermission) {
        cameraImageBitmap?.let { bitmap ->
            Image(bitmap, null)
        }
    }

    var selectedDay by remember { mutableStateOf(item?.time?.rem(100) ?: 1) }
    var selectedMonth by remember { mutableStateOf((item?.time?.div(100)?.rem(100) ?: 1)) }
    var selectedYear by remember { mutableStateOf(item?.time?.div(10000) ?: 2023) }
    var selectedHour by remember { mutableStateOf(item?.hour ?: 0) }
    var selectedMinute by remember { mutableStateOf(item?.minute ?: 0) }

    // Function to create LazyColumn for numbers
    @Composable
    fun LazyColumnForNumbers(
        start: Int,
        end: Int,
        selectedValue: Int,
        onValueChange: (Int) -> Unit
    ) {
        LazyColumn(
            modifier = Modifier
                .width(55.dp)
                .height(95.dp)
                .border(2.dp, Color(0xFF2DCDF5))
                .background(Color.White)
        ) {
            itemsIndexed((start..end).toList()) { index, value ->
                val isSelected = rememberUpdatedState(selectedValue == value)
                Text(
                    text = value.toString(),
                    modifier = Modifier
                        .clickable { onValueChange(value) }
                        .padding(8.dp)
                        .background(if (isSelected.value) Color(0xFF2DCDF5) else Color.Transparent)
                )
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(255, 247, 209))
            .height(80.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Add a clickable Icon for navigation
        Icon(
            imageVector = Icons.Default.KeyboardArrowLeft,
            contentDescription = "Back",
            tint = Color.Black,
            modifier = Modifier
                .size(30.dp)
                .clickable {
                    // Handle back button click here
                    onButtonClick()
                }

        )
    }

    ScrollableContent {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp).background(Color.White),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            selectedFiles.forEach { uri ->
                Box(
                    modifier = Modifier
                        .size(120.dp).padding(8.dp)
                        .background(Color.Gray, shape = RoundedCornerShape(5.dp))
                ) {
                    uri?.let {
                        val contentResolver = LocalContext.current.contentResolver
                        val inputStream = contentResolver.openInputStream(uri)
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(5.dp))
                        )
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancel",
                            tint = Color.White,
                            modifier = Modifier
                                .size(30.dp)
                                .clickable {
                                    // 处理删除逻辑
                                    selectedFiles = selectedFiles.filter { it != uri }
                                }
                                .padding(4.dp)
                                .align(Alignment.TopStart)
                        )
                    }
                }
            }
            if (selectedFiles.size < 3) {
                Box(
                    modifier = Modifier
                        .size(120.dp).padding(8.dp)
                        .background(Color.Gray, shape = RoundedCornerShape(5.dp))
                        .clickable {
                            imageFromGalleryLauncher.launch("image/*")
                        }
                ) {
                    Icon(
                        imageVector = Icons.TwoTone.Add,
                        contentDescription = "Add Image",
                        tint = Color.White,
                        modifier = Modifier
                            .size(100.dp).align(Alignment.Center)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(255, 247, 209))
        ) {

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                modifier = Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Add text") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        // Hide the keyboard when the "Done" action is triggered
                        keyboardController?.hide()
                    }
                ),
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .height(4 * 32.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color.White)

            )

            Spacer(modifier = Modifier.height(15.dp))

            ExpandableItem(
                icon = Icons.Outlined.DateRange,
                title = "Date",
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    LazyColumnForNumbers(
                        start = 1,
                        end = 31,
                        selectedValue = selectedDay
                    ) { selectedValue ->
                        selectedDay = selectedValue
                    }

                    // Second LazyColumn for months (1-12)
                    LazyColumnForNumbers(
                        start = 1,
                        end = 12,
                        selectedValue = selectedMonth
                    ) { selectedValue ->
                        selectedMonth = selectedValue
                    }

                    // Third LazyColumn for years (2023-2100)
                    LazyColumnForNumbers(
                        start = 2023,
                        end = 2100,
                        selectedValue = selectedYear
                    ) { selectedValue ->
                        selectedYear = selectedValue
                    }

                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            ExpandableItem(
                icon = Icons.Outlined.DateRange,
                title = "Time",
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LazyColumnForNumbers(
                        start = 0,
                        end = 24,
                        selectedValue = selectedHour
                    ) { selectedValue ->
                        selectedHour = selectedValue
                    }

                    LazyColumnForNumbers(
                        start = 0,
                        end = 60,
                        selectedValue = selectedMinute
                    ) { selectedValue ->
                        selectedMinute = selectedValue
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            ExpandableItem(
                icon = Icons.Outlined.Edit,
                title = "Checklist",
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    todoList.forEachIndexed { index, toDoItem ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextField(
                                value = toDoItem.text,
                                onValueChange = {
                                    todoList = todoList.toMutableList().apply {
                                        set(index, toDoItem.copy(text = it))
                                    }
                                    val charArray = checkList.toCharArray()
                                    charArray[index] = '0'
                                    checkList = String(charArray)
                                },
                                label = { Text("Todo Item") },
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(5.dp))
                            )

                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Delete Todo",
                                tint = Color(0xFF149EE7),
                                modifier = Modifier
                                    .clickable {
                                        todoList = todoList.toMutableList().apply {
                                            removeAt(index)
                                        }
                                        val stringBuilder = StringBuilder(checkList)
                                        stringBuilder.deleteCharAt(index)
                                        checkList = stringBuilder.toString()
                                    }
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Todo",
                        tint = Color(0xFF149EE7),
                        modifier = Modifier
                            .clickable {
                                todoList = todoList.toMutableList().apply {
                                    add(ToDoItem("", false))
                                }
                                checkList += "0"
                            }
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp)
            ) {
                Icon(Icons.Outlined.Notifications, contentDescription = "priority")
                Spacer(modifier = Modifier.width(120.dp))
                Text("Priority", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(120.dp))
                Checkbox(
                    checked = isPriorityEnabled,
                    onCheckedChange = { isChecked ->
                        isPriorityEnabled = isChecked
                    },
                    modifier = Modifier
                        .padding(15.dp)
                        .clip(RoundedCornerShape(5.dp))
                )
            }


            //Spacer(modifier = Modifier.height(16.dp))

            ExpandableItem(
                icon = Icons.Outlined.LocationOn,
                title = "Location",
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp)
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            location = "Tesco"
                        }
                    ) {
                        RadioButton(
                            selected = location == "Tesco",
                            onClick = {
                                location = "Tesco"
                            },
                        )
                        Text(
                            text = "Tesco",
                            modifier = Modifier.padding(start = 16.dp)
                        )

                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            location = "Coop"
                        }
                    ) {
                        RadioButton(
                            selected = location == "Coop",
                            onClick = {
                                location = "Coop"
                            },
                        )
                        Text(
                            text = "Coop",
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            location = "Lidl"
                        }
                    ) {
                        RadioButton(
                            selected = location == "Lidl",
                            onClick = {
                                location = "Lidl"
                            },
                        )
                        Text(
                            text = "Lidl",
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            location = "Diamond"
                        }
                    ) {
                        RadioButton(
                            selected = location == "Diamond",
                            onClick = {
                                location = "Diamond"
                            },
                        )
                        Text(
                            text = "Diamond",
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }

            }

            Spacer(modifier = Modifier.height(30.dp))


        }


        val backgroundColor = if (viewModel.selectedColor.value == HomeScreenViewModel.Theme.Pink) {
            Brush.linearGradient(listOf(Pink40, Pink80))
        } else {
            Brush.linearGradient(listOf(Blue700, Blue200))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    cameraImageBitmap = null
                    cameraImageUri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.provider",
                        newImageFile()
                    )
                    imageFromCameraLauncher.launch(cameraImageUri)
                },
                colors = ButtonDefaults.buttonColors(Color.Transparent),
                modifier = Modifier
                    .padding(start = 8.dp)
                    .clickable { /* Handle button click here */ }
                    .background(backgroundColor, shape = RoundedCornerShape(32.dp))
                    .weight(1f)
                    .height(50.dp)
            ) {
                Text("Camera", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    if (item != null) {
                        viewModel.updateTodoItem(
                            TodoItem(
                                id = item.id,
                                title = title,
                                time = selectedDay + selectedMonth * 100 + selectedYear * 10000,
                                details = description,
                                check = todoList.toMutableList(),
                                priority = isPriorityEnabled,
                                pictures = selectedFiles.toMutableList(),
                                locationTag = location,
                                checklist = checkList,
                                hour = selectedHour,
                                minute = selectedMinute
                            )
                        )
                    }
                    onButtonClick()
                },
                colors = ButtonDefaults.buttonColors(Color.Transparent),
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { /* Handle button click here */ }
                    .background(backgroundColor, shape = RoundedCornerShape(32.dp))
                    .weight(2f)
                    .height(50.dp)
            ) {
                Text("Confirm", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }

    }}


