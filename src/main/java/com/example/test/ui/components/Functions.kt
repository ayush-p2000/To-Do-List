/*The code in these page is written by Siyuan Peng*/

package com.example.test.ui.components

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import coil.compose.rememberImagePainter
import com.example.test.data.TestDatabase
import com.example.test.model.entity.TodoItem
import com.example.test.ui.screens.HomeScreenViewModel
import com.example.test.ui.screens.LocationViewModel
import com.example.test.ui.theme.Blue200
import com.example.test.ui.theme.Blue700
import com.example.test.ui.theme.DeepRed
import com.example.test.ui.theme.DeepYellow
import com.example.test.ui.theme.LightRed
import com.example.test.ui.theme.LightYellow
import com.example.test.ui.theme.Pink40
import com.example.test.ui.theme.Pink80
import com.example.test.ui.theme.deepGray
import com.example.test.ui.theme.lightGray
import kotlinx.coroutines.launch

private val REQUEST_CODE_STORAGE_PERMISSION = 100

fun getImageBitmap(uri: Uri?,context: Context): ImageBitmap? {
    var result: ImageBitmap? = null
    if (uri != null) {
        result = ImageDecoder.decodeBitmap(
            ImageDecoder.createSource(context.getContentResolver(),
                uri)).asImageBitmap()
    }
    return result
}

fun onImageSelected(uri: Uri,context: Context):Bitmap {

    val bitmap = ImageDecoder.decodeBitmap(
        ImageDecoder.createSource(context.contentResolver, uri)
    )

    return bitmap
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun rollingBroadcast(pictures: List<Uri>,context: Context) {

    val pageCount = pictures.size

    val pagerState = rememberPagerState(0)

    Column {

        HorizontalPager(
            pageCount = pictures.size,
            verticalAlignment = Alignment.CenterVertically,
            state = pagerState
        ) { index ->


            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_CODE_STORAGE_PERMISSION
                )
            }

            Image(
                //bitmap = it,
                painter =  rememberImagePainter(pictures[index]),
                contentDescription = "photo",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .size(width = 256.dp, height = 256.dp)
            )

        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            Modifier
                .height(20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pageCount) { iteration ->
                val color =
                    if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(10.dp)
                )
            }
        }
    }
}


@Composable
fun CheckTodo(item: TodoItem, viewModel: HomeScreenViewModel) {
    val checkedList = viewModel.getCheckedList(item.id.toLong())
    val booleanList = item.checklist.toCharArray()
    val booleanArray = booleanList.map { it == '1' }.toBooleanArray()

    item.check.forEachIndexed { index, checkTodo ->
        var thisItem by remember {
            mutableStateOf(booleanArray[index])
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = checkTodo.text,
                fontSize = 16.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(10.dp)
            )
            Checkbox(
                checked = thisItem,
                onCheckedChange = {
                    thisItem = !thisItem
                    // Update the checked state in the ViewModel
                    viewModel.toggleChecked(item.id.toLong(), index)
                    val charArray = item.checklist.toCharArray()
                    if(charArray[index] == '1'){
                        charArray[index] = '0'
                    }
                    else{
                        charArray[index] = '1'
                    }
                    item.checklist = String(charArray)
                }
            )
        }
    }
}

@Composable
fun DropdownMenuDetail(item: TodoItem) {
    var expanded by remember { mutableStateOf(false) }
    var seleected by remember {
        mutableStateOf(0)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { expanded = true }
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = "Detail",
                tint = Color.Black,
                modifier = Modifier.size(56.dp)
            )
        }

    }


    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        Text(
            item.details,
            fontSize = 20.sp,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 20.dp),
            lineHeight = 36.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ExpandableItem(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    endText: String = "",
    subItemStartPadding: Int = 8,
    subItem: @Composable () -> Unit
) {
    var isShowSubItem by remember { mutableStateOf(false) }
    val arrowRotateDegrees: Float by animateFloatAsState(if (isShowSubItem) 90f else 0f)
    Column(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    isShowSubItem = !isShowSubItem
                }
        ) {
            Icon(icon, contentDescription = "title")
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Row {
                if (endText.isNotBlank()) {
                    Text(
                        text = endText,
                        modifier = modifier
                            .padding(end = 4.dp)
                            .widthIn(0.dp, 100.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Icon(
                    Icons.Filled.ArrowDropDown,
                    contentDescription = title,
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(arrowRotateDegrees),

                    )
            }
        }
        AnimatedVisibility(visible = isShowSubItem) {
            Column(modifier = Modifier.padding(start = subItemStartPadding.dp)) {
                subItem()
            }
        }
    }
}

@Composable
fun ExpandableItem(
    title: String,
    modifier: Modifier = Modifier,
    endText: String = "",
    subItemStartPadding: Int = 8,
    subItem: @Composable () -> Unit
) {
    var isShowSubItem by remember { mutableStateOf(false) }
    val arrowRotateDegrees: Float by animateFloatAsState(if (isShowSubItem) 90f else 0f)
    Column(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    isShowSubItem = !isShowSubItem
                }
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Row {
                if (endText.isNotBlank()) {
                    Text(
                        text = endText,
                        modifier = modifier
                            .padding(end = 4.dp)
                            .widthIn(0.dp, 100.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Icon(
                    Icons.Filled.ArrowDropDown,
                    contentDescription = title,
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(arrowRotateDegrees),

                    )
            }
        }
        AnimatedVisibility(visible = isShowSubItem) {
            Column(modifier = Modifier.padding(start = subItemStartPadding.dp)) {
                subItem()
            }
        }
    }
}

@Composable
fun TodoItems(
    item: TodoItem,
    theme: HomeScreenViewModel.Theme,
    priority: HomeScreenViewModel.Priority,
    viewModel : HomeScreenViewModel,
    locationViewModel: LocationViewModel,
    onTodoItemClick: (TodoItem) -> Unit,
    context: Context
) {

    val backgroundColor = if (item.priority == true) {
        if (priority == HomeScreenViewModel.Priority.Yellow) {
            Brush.linearGradient(listOf(DeepYellow, LightYellow))
        } else {
            Brush.linearGradient(listOf(DeepRed, LightRed))
        }

    } else if (theme == HomeScreenViewModel.Theme.Pink) {
        Brush.linearGradient(listOf(Pink80, Pink40))
    } else {
        Brush.linearGradient(listOf(Blue700, Blue200))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(255, 253, 243))
    ) {
        Column(
        ) {
            Row(
                modifier = Modifier
                    .background(
                        backgroundColor
                    )
                    .fillMaxWidth()
                    .height(32.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {


                IconButton(
                    onClick = { /*TODO*/
                        viewModel.updateTodoItem(
                            TodoItem(
                                id = item.id,
                                title = item.title,
                                time = item.time,
                                details = item.details,
                                check = item.check,
                                priority = item.priority,
                                pictures = item.pictures,
                                locationTag = item.locationTag,
                                checklist = item.checklist,
                                hour = item.hour,
                                minute = item.minute
                            )
                        )
                        Toast.makeText(context,"Save changes!", Toast.LENGTH_SHORT).show()
                        viewModel.setRefreshUI()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "Save",
                        tint = Color.White
                    )
                }

                IconButton(
                    onClick = { /*TODO*/
                        onTodoItemClick(item)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.List,
                        contentDescription = "Detail",
                        tint = Color.White
                    )
                }

                ConfirmButton(todoItem = item, viewModel)
            }

            Spacer(modifier = Modifier.height(8.dp))

            ExpandableItem(
                title = "Pictures",
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp)
            ) {
                Row {
                    /*
                    Image(
                        painter = painterResource(id = R.drawable.miku),
                        contentDescription = "profile picture",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(128.dp)
                            .padding(horizontal = 8.dp)
                    )
                    */

                    rollingBroadcast(pictures = item.pictures, context = context )

                    /*
                    Text(
                        item.details,
                        fontSize = 20.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 20.dp),
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 36.sp,
                        textAlign = TextAlign.Center
                    )
                    */
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .background(backgroundColor)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    item.title,
                    fontSize = 32.sp,
                    color = Color.Black,
                    modifier = Modifier
                        .padding(horizontal = 20.dp),
                    //maxLines = 1,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 40.sp,
                    //overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    item.time.toString().substring(0, 4) + "-" + item.time.toString()
                        .substring(4, 6) + "-" + item.time.toString().substring(6, 8),
                    fontSize = 16.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    item.getTime(),
                    fontSize = 16.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Column {

                Spacer(modifier = Modifier.height(8.dp))

                if (item.check.size != 0) {
                    ExpandableItem(
                        title = "Checklist",
                        modifier = Modifier
                            .padding(start = 20.dp, end = 20.dp)
                    ) {
                        CheckTodo(item,viewModel)
                    }

                    //DropdownMenuDetail(item)
                    Spacer(modifier = Modifier.height(16.dp))
                }


                ExpandableItem(
                    title = "Detail",
                    modifier = Modifier
                        .padding(start = 20.dp, end = 20.dp)
                ) {
                    Text(
                        item.details,
                        fontSize = 20.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                        lineHeight = 36.sp,
                        textAlign = TextAlign.Start
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            val distance = locationViewModel.longitude?.let {
                locationViewModel.latitude?.let { it1 ->
                    viewModel.getDistance(viewModel.getLat(item.locationTag),viewModel.getLong(item.locationTag),
                        it1,
                        it
                    )
                }
            }
            val context = LocalContext.current
            ExpandableItem(
                title = "Location",
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp)
            ) {
                Text(
                    item.locationTag+ ": \n"+viewModel.getLat(item.locationTag)+"\n"+viewModel.getLong(item.locationTag),
                    fontSize = 20.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    lineHeight = 36.sp,
                    textAlign = TextAlign.Start
                )
                Text(
                    "Distance: $distance",
                    fontSize = 20.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    lineHeight = 36.sp,
                    textAlign = TextAlign.Start
                )
                if (distance != null) {
                    if(distance<1) {
                        Toast.makeText(context,"Close!",Toast.LENGTH_SHORT).show()
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

        }
    }
}

@Composable
fun OverdueItems(
    item: TodoItem,
    theme: HomeScreenViewModel.Theme,
    priority: HomeScreenViewModel.Priority,
    viewModel : HomeScreenViewModel,
    locationViewModel: LocationViewModel,
    onTodoItemClick: (TodoItem) -> Unit,
    context: Context
) {

    val backgroundColor = Brush.linearGradient(listOf(deepGray, lightGray))


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(255, 253, 243))
        //.border(width = 1.dp, color = Color.Black)
    ) {
        Column(
            //modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier
                    .background(
                        backgroundColor
                    )
                    .fillMaxWidth()
                    .height(32.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {


                IconButton(
                    onClick = { /*TODO*/
                        viewModel.updateTodoItem(
                            TodoItem(
                                id = item.id,
                                title = item.title,
                                time = item.time,
                                details = item.details,
                                check = item.check,
                                priority = item.priority,
                                pictures = item.pictures,
                                locationTag = item.locationTag,
                                checklist = item.checklist,
                                hour = item.hour,
                                minute = item.minute
                            )
                        )
                        Toast.makeText(context,"Save changes!", Toast.LENGTH_SHORT).show()
                        viewModel.setRefreshUI()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "Save",
                        tint = Color.White
                    )
                }

                IconButton(
                    onClick = { /*TODO*/
                        onTodoItemClick(item)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.List,
                        contentDescription = "Detail",
                        tint = Color.White
                    )
                }

                ConfirmButton(todoItem = item, viewModel)
            }

            Spacer(modifier = Modifier.height(8.dp))

            ExpandableItem(
                title = "Pictures",
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp)
            ) {
                Row {
                    /*
                    Image(
                        painter = painterResource(id = R.drawable.miku),
                        contentDescription = "profile picture",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(128.dp)
                            .padding(horizontal = 8.dp)
                    )
                    */

                    rollingBroadcast(pictures = item.pictures, context = context )

                    /*
                    Text(
                        item.details,
                        fontSize = 20.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 20.dp),
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 36.sp,
                        textAlign = TextAlign.Center
                    )
                    */
                }
            }



            Spacer(modifier = Modifier.height(8.dp))


            Column(
                modifier = Modifier
                    .background(backgroundColor)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    item.title,
                    fontSize = 32.sp,
                    color = Color.Black,
                    modifier = Modifier
                        .padding(horizontal = 20.dp),
                    //maxLines = 1,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 40.sp,
                    //overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    item.time.toString().substring(0, 4) + "-" + item.time.toString()
                        .substring(4, 6) + "-" + item.time.toString().substring(6, 8),
                    fontSize = 16.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    item.getTime(),
                    fontSize = 16.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Column {

                Spacer(modifier = Modifier.height(8.dp))

                if (item.check.size != 0) {
                    ExpandableItem(
                        title = "Checklist",
                        modifier = Modifier
                            .padding(start = 20.dp, end = 20.dp)
                    ) {
                        CheckTodo(item,viewModel)
                    }

                    //DropdownMenuDetail(item)
                    Spacer(modifier = Modifier.height(16.dp))
                }


                ExpandableItem(
                    title = "Detail",
                    modifier = Modifier
                        .padding(start = 20.dp, end = 20.dp)
                ) {
                    Text(
                        item.details,
                        fontSize = 20.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                        lineHeight = 36.sp,
                        textAlign = TextAlign.Start
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            val distance = locationViewModel.longitude?.let {
                locationViewModel.latitude?.let { it1 ->
                    viewModel.getDistance(viewModel.getLat(item.locationTag),viewModel.getLong(item.locationTag),
                        it1,
                        it
                    )
                }
            }
            val context = LocalContext.current
            ExpandableItem(
                title = "Location",
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp)
            ) {
                Text(
                    item.locationTag+ ": \n"+viewModel.getLat(item.locationTag)+"\n"+viewModel.getLong(item.locationTag),
                    fontSize = 20.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    lineHeight = 36.sp,
                    textAlign = TextAlign.Start
                )
                Text(
                    "Distance: $distance",
                    fontSize = 20.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    lineHeight = 36.sp,
                    textAlign = TextAlign.Start
                )
                if (distance != null) {
                    if(distance<1) {
                        Toast.makeText(context,"Close!",Toast.LENGTH_SHORT).show()
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

        }
    }
}


@Composable
fun CompletedItems(
    item: TodoItem,
    theme: HomeScreenViewModel.Theme,
    priority: HomeScreenViewModel.Priority,
    viewModel : HomeScreenViewModel,
    locationViewModel: LocationViewModel,
    onTodoItemClick: (TodoItem) -> Unit,
    context: Context
) {

    val backgroundColor = if (item.priority == true) {
        if (priority == HomeScreenViewModel.Priority.Yellow) {
            Brush.linearGradient(listOf(DeepYellow, LightYellow))
        } else {
            Brush.linearGradient(listOf(DeepRed, LightRed))
        }

    } else if (theme == HomeScreenViewModel.Theme.Pink) {
        Brush.linearGradient(listOf(Pink80, Pink40))
    } else {
        Brush.linearGradient(listOf(Blue700, Blue200))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(255, 253, 243))
        //.border(width = 1.dp, color = Color.Black)
    ) {
        Column(
            //modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier
                    .background(
                        backgroundColor
                    )
                    .fillMaxWidth()
                    .height(32.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {

                /*
                IconButton(
                    onClick = { /*TODO*/
                        onTodoItemClick(item)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.List,
                        contentDescription = "Detail",
                        tint = Color.White
                    )
                }*/
                IconButton(
                    onClick = { /*TODO*/
                        item.checklist = item.checklist.replace(Regex("."), "0")
                        viewModel.updateTodoItem(
                            TodoItem(
                                id = item.id,
                                title = item.title,
                                time = item.time,
                                details = item.details,
                                check = item.check,
                                priority = item.priority,
                                pictures = item.pictures,
                                locationTag = item.locationTag,
                                checklist = item.checklist,
                                hour = item.hour,
                                minute = item.minute
                            )
                        )
                        Toast.makeText(context,"Reset!",Toast.LENGTH_SHORT).show()
                        viewModel.setRefreshUI2()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Save",
                        tint = Color.White
                    )
                }

                ConfirmButton(todoItem = item, viewModel)
            }

            Spacer(modifier = Modifier.height(8.dp))

            ExpandableItem(
                title = "Pictures",
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp)
            ) {
                Row {
                    /*
                    Image(
                        painter = painterResource(id = R.drawable.miku),
                        contentDescription = "profile picture",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(128.dp)
                            .padding(horizontal = 8.dp)
                    )
                    */

                    rollingBroadcast(pictures = item.pictures, context = context )

                    /*
                    Text(
                        item.details,
                        fontSize = 20.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 20.dp),
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 36.sp,
                        textAlign = TextAlign.Center
                    )
                    */
                }
            }



            Spacer(modifier = Modifier.height(8.dp))


            Column(
                modifier = Modifier
                    .background(backgroundColor)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Completed!",
                    fontSize = 32.sp,
                    color = Color.Black,
                    modifier = Modifier
                        .padding(horizontal = 20.dp),
                    //maxLines = 1,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 40.sp,
                    //overflow = TextOverflow.Ellipsis
                )
                //Spacer(modifier = Modifier.height(8.dp))
                Text(
                    item.title,
                    fontSize = 32.sp,
                    color = Color.Black,
                    modifier = Modifier
                        .padding(horizontal = 20.dp),
                    //maxLines = 1,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 40.sp,
                    //overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    item.time.toString().substring(0, 4) + "-" + item.time.toString()
                        .substring(4, 6) + "-" + item.time.toString().substring(6, 8),
                    fontSize = 16.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    item.getTime(),
                    fontSize = 16.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Column {

                Spacer(modifier = Modifier.height(8.dp))

                if (item.check.size != 0) {
                    ExpandableItem(
                        title = "Checklist",
                        modifier = Modifier
                            .padding(start = 20.dp, end = 20.dp)
                    ) {
                        CheckTodo(item,viewModel)
                    }

                    //DropdownMenuDetail(item)
                    Spacer(modifier = Modifier.height(16.dp))
                }


                ExpandableItem(
                    title = "Detail",
                    modifier = Modifier
                        .padding(start = 20.dp, end = 20.dp)
                ) {
                    Text(
                        item.details,
                        fontSize = 20.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                        lineHeight = 36.sp,
                        textAlign = TextAlign.Start
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            val distance = locationViewModel.longitude?.let {
                locationViewModel.latitude?.let { it1 ->
                    viewModel.getDistance(viewModel.getLat(item.locationTag),viewModel.getLong(item.locationTag),
                        it1,
                        it
                    )
                }
            }
            val context = LocalContext.current
            ExpandableItem(
                title = "Location",
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp)
            ) {
                Text(
                    item.locationTag+ ": \n"+viewModel.getLat(item.locationTag)+"\n"+viewModel.getLong(item.locationTag),
                    fontSize = 20.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    lineHeight = 36.sp,
                    textAlign = TextAlign.Start
                )
                Text(
                    "Distance: $distance",
                    fontSize = 20.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    lineHeight = 36.sp,
                    textAlign = TextAlign.Start
                )
                if (distance != null) {
                    if(distance<1) {
                        Toast.makeText(context,"Close!",Toast.LENGTH_SHORT).show()
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(_searchText: MutableState<String>, t: HomeScreenViewModel.Theme, todolist: List<TodoItem>) {
    var showDialog by remember { mutableStateOf(false) }

    Column {
        TitleBar(theme = t) {

            Spacer(modifier = Modifier.width(8.dp))

            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {

                OutlinedTextField(
                    value = _searchText.value,
                    onValueChange = {
                        _searchText.value = it
                    },
                    placeholder = {
                        Text(
                            "Search",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    modifier = Modifier.background(Color.Transparent),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)

                )

            }

            Spacer(modifier = Modifier.width(10.dp))

            IconButton(
                onClick = { showDialog = true}
            ) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = "Notify",
                    tint = Color.White)
            }

            if (showDialog) {
                Reminder(onDismiss = { showDialog = false },todolist)
            }

            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Composable
fun ConfirmButton(todoItem: TodoItem, viewModel : HomeScreenViewModel) {

    val context = LocalContext.current

    val showDialog = remember { mutableStateOf(false) }

    val showDialogHandler = {
        showDialog.value = true
    }

    val dismissDialogHandler = {
        showDialog.value = false
    }

    val coroutineScope = rememberCoroutineScope()
    val confirmHandler = {
        val keyDao = TestDatabase.getDB(context).keyDAO()
        coroutineScope.launch {
            keyDao.deleteKeyEntity(todoItem.toKeyEntity())
            Toast.makeText(context, "Todo Deleted!", Toast.LENGTH_SHORT).show()
        }
        showDialog.value = false
    }

    IconButton(
        onClick = showDialogHandler
    ) {
        Icon(
            imageVector = Icons.Filled.Close,
            contentDescription = "Delete",
            tint = Color.White
        )
    }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = dismissDialogHandler,
            title = { Text("Confirm Action", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to confirm?") },
            dismissButton = {
                IconButton(
                    onClick = dismissDialogHandler
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Cancel",
                        tint = Color.Black
                    )
                }
            },
            confirmButton = {
                IconButton(
                    onClick = confirmHandler
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Confirm",
                        tint = Color.Black
                    )
                }
            },
        )
    }

}



@Composable
fun Reminder(onDismiss: () -> Unit,todolist: List<TodoItem>) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            modifier = Modifier.padding(16.dp),
            shape = MaterialTheme.shapes.medium,
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Reminder")
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth(),
                    //.paint(painterResource(id = R.drawable.post1), contentScale = ContentScale.FillWidth),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ){

                    items(todolist){ item->

                        if(item.hasBeenHistory()&&!item.hasCompleted()){
                            Text(item.title+"has already been outdated", color = Color.Red, fontWeight = FontWeight.Bold,textAlign = TextAlign.Center)
                        }

                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                IconButton(
                    onClick = onDismiss
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Cancel",
                        tint = Color.Black
                    )
                }
            }
        }
    }
}



fun getUriToDrawable(resId: Int, context: Context): List<Uri> {
    val uri = Uri.parse(
        ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + context.resources.getResourcePackageName(resId)
                + '/' + context.resources.getResourceTypeName(resId)
                + '/' + context.resources.getResourceEntryName(resId)
    )
    return listOf(uri)
}
