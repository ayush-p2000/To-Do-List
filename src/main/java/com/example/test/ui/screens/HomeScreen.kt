/*The code in these page is written by Siyuan Peng*/

package com.example.test.ui.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.test.R
import com.example.test.model.entity.TodoItem
import com.example.test.ui.components.OverdueItems
import com.example.test.ui.components.SearchBar
import com.example.test.ui.components.TodoItems


@Composable
fun homePage(viewModel: HomeScreenViewModel,intent: Intent,locationViewModel: LocationViewModel) {
    var page by remember {
        mutableStateOf(Page.homePage)
    }

//    when (page) {
//        Page.homePage -> HomeScreen(onButtonClick = {page = Page.AddPage},viewModel)
//        Page.AddPage -> AddScreen(onButtonClick = {page = Page.homePage})
//    }
    var selectedItem by remember { mutableStateOf<TodoItem?>(null) }

    when (page) {
        Page.homePage -> {
            HomeScreen(
                viewModel = viewModel,
                onTodoItemClick = { todoItem ->
                    selectedItem = todoItem
                    page = Page.DetailPage
                },
                onButtonClick = { page = Page.AddPage },
                intent = intent,
                locationViewModel = locationViewModel
            )
        }
        Page.AddPage -> {
            AddScreen(viewModel = viewModel, onButtonClick = { page = Page.homePage })
        }
        Page.DetailPage -> {
            selectedItem?.let { item ->
                DetailScreen(
                    onButtonClick = { page = Page.homePage},
                    item,
                    viewModel
                )
            }
        }
    }

}



@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onButtonClick:()-> Unit,viewModel: HomeScreenViewModel,onTodoItemClick: (TodoItem) -> Unit,intent: Intent,locationViewModel: LocationViewModel){
    val refreshUI by viewModel.refreshUI.collectAsState(false)

    val context = LocalContext.current
    val REQUEST_CODE_STORAGE_PERMISSION = 1001
    //var todoList = viewModel.todoList
    var todoList by remember { mutableStateOf<List<TodoItem>>(viewModel.todoList) }


    LaunchedEffect(viewModel) {

        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_CODE_STORAGE_PERMISSION
            )
        }else {
            viewModel.loadDataFromDatabase()
        }


    }



    val selectedColor = viewModel.selectedColor.value
    val name = viewModel.userName.value

    val todoListState = remember { mutableStateOf(todoList) }

    if(refreshUI){
        viewModel.resetRefreshUI()
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
                        Text("Welcome back", fontSize = 20.sp, color=Color.Gray)
                        if (name != null) {
                            Text(name, fontWeight = FontWeight.Bold, fontSize = 32.sp )
                        }
                    }
                    Box{
                        Image(
                            painter = painterResource(id = R.drawable.user),
                            contentDescription = "profile picture",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(55.dp)
                                .clip(RoundedCornerShape(50.dp))
                        )
                    }
                }
            }


        ) {
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .background(Color(255, 247, 209))
            ){
                val myValue = remember { mutableStateOf("") }

                viewModel.selectedColor.value?.let { it1 -> SearchBar(_searchText = myValue, it1,todoList) }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text(
                        "What's Next?",
                        modifier = Modifier.padding(start = 16.dp),
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontSize = 30.sp
                    )

                    var sortIndex:MutableState<Int> = mutableStateOf(0)

                    IconButton(
                        onClick = {

                            val distanceComparator = Comparator<TodoItem> { item1, item2 ->
                                val distance1 = locationViewModel.longitude?.let { it1 ->
                                    locationViewModel.latitude?.let { it2 ->
                                        viewModel.getDistance(viewModel.getLat(item1.locationTag),viewModel.getLong(item1.locationTag),
                                            it1,
                                            it2
                                        )
                                    }
                                }
                                val distance2 = locationViewModel.longitude?.let { it1 ->
                                    locationViewModel.latitude?.let { it2 ->
                                        viewModel.getDistance(viewModel.getLat(item2.locationTag),viewModel.getLong(item2.locationTag),
                                            it1,
                                            it2
                                        )
                                    }
                                }
                                distance2?.let { it1 -> distance1?.compareTo(it1) } ?: 0
                            }

                            sortIndex.value = (sortIndex.value + 1) % 4

                            val sortedList = when (sortIndex.value) {
                                0 -> todoList.sortedBy { it.time }.toMutableList()
                                1 -> todoList.sortedWith(compareBy({ !it.priority }, { it.time })).toMutableList()
                                2 -> todoList.sortedBy { it.title }.toMutableList()
                                3-> todoList.sortedWith(distanceComparator)
                                else -> todoList.toMutableList()
                            }

                            todoListState.value = sortedList

                            when(sortIndex.value){
                                0 -> Toast.makeText(context,"Sort by time!", Toast.LENGTH_SHORT).show()
                                1 -> Toast.makeText(context,"Sort by priority!", Toast.LENGTH_SHORT).show()
                                2 -> Toast.makeText(context,"Sort by title!", Toast.LENGTH_SHORT).show()
                            }

                        },
                        Modifier
                            .size(60.dp)
                            .padding(start = 30.dp)
                    ) {
                        Icon(
                            modifier = Modifier.size(35.dp),
                            imageVector = Icons.Rounded.Build,
                            contentDescription = "Sort",
                            tint = Color.Black,
                        )
                    }

                    IconButton(
                        onClick = {  viewModel.setRefreshUI()  },
                        Modifier
                            .size(60.dp)
                            .padding(end = 8.dp)
                    ) {
                        Icon(
                            modifier = Modifier.size(40.dp),
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = "Refresh",
                            tint = Color.Black,
                        )
                    }

                    IconButton(
                        onClick = {  onButtonClick()  },
                        Modifier
                            .size(60.dp)
                            .padding(end = 8.dp)
                    ) {
                        Icon(
                            modifier = Modifier.size(40.dp),
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "Create new task",
                            tint = Color.Black,
                        )
                    }
                }
                /*
                Row{
                    Text(
                        "${locationViewModel.latitude},${locationViewModel.longitude}",
                        modifier = Modifier.padding(start = 16.dp),
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontSize = 30.sp
                    )
                }*/
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ){

                    items(todoListState.value){ item->
                        if(item.doesMatchSearchText(myValue.value)&&!item.hasCompleted()){
                            if(!item.hasBeenHistory()){
                                viewModel.selectedColor.value?.let { it1 ->
                                    viewModel.selectedPriority.value?.let { it2 ->
                                        TodoItems(item,
                                            it1,
                                            it2, viewModel, locationViewModel,onTodoItemClick,context)
                                    }
                                }
                            }else{
                                viewModel.selectedPriority.value?.let { it1 ->
                                    viewModel.selectedColor.value?.let { it2 ->
                                        OverdueItems(item, it2,
                                            it1, viewModel, locationViewModel,onTodoItemClick,context )
                                    }
                                }
                            }

                        }

                    }
                }
            }
        }
    }
    else{
        viewModel.resetRefreshUI()
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
                        Text("Welcome back", fontSize = 20.sp, color=Color.Gray)
                        if (name != null) {
                            Text(name, fontWeight = FontWeight.Bold, fontSize = 32.sp )
                        }
                    }
                    Box{
                        Image(
                            painter = painterResource(id = R.drawable.user),
                            contentDescription = "profile picture",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(55.dp)
                                .clip(RoundedCornerShape(50.dp))
                        )
                    }
                }
            }


        ) {
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .background(Color(255, 247, 209))
            ){
                val myValue = remember { mutableStateOf("") }

                viewModel.selectedColor.value?.let { it1 -> SearchBar(_searchText = myValue, it1,todoList) }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text(
                        "What's Next?",
                        modifier = Modifier.padding(start = 16.dp),
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontSize = 30.sp
                    )

                    var sortIndex:MutableState<Int> = mutableStateOf(0)

                    IconButton(
                        onClick = {

                            val distanceComparator = Comparator<TodoItem> { item1, item2 ->
                                val distance1 = locationViewModel.longitude?.let { it1 ->
                                    locationViewModel.latitude?.let { it2 ->
                                        viewModel.getDistance(viewModel.getLat(item1.locationTag),viewModel.getLong(item1.locationTag),
                                            it1,
                                            it2
                                        )
                                    }
                                }
                                val distance2 = locationViewModel.longitude?.let { it1 ->
                                    locationViewModel.latitude?.let { it2 ->
                                        viewModel.getDistance(viewModel.getLat(item2.locationTag),viewModel.getLong(item2.locationTag),
                                            it1,
                                            it2
                                        )
                                    }
                                }
                                distance2?.let { it1 -> distance1?.compareTo(it1) } ?: 0
                            }

                            sortIndex.value = (sortIndex.value + 1) % 4

                            val sortedList = when (sortIndex.value) {
                                0 -> todoList.sortedBy { it.time }.toMutableList()
                                1 -> todoList.sortedWith(compareBy({ !it.priority }, { it.time })).toMutableList()
                                2 -> todoList.sortedBy { it.title }.toMutableList()
                                3-> todoList.sortedWith(distanceComparator)
                                else -> todoList.toMutableList()
                            }

                            todoListState.value = sortedList

                            when(sortIndex.value){
                                0 -> Toast.makeText(context,"Sort by time!", Toast.LENGTH_SHORT).show()
                                1 -> Toast.makeText(context,"Sort by priority!", Toast.LENGTH_SHORT).show()
                                2 -> Toast.makeText(context,"Sort by title!", Toast.LENGTH_SHORT).show()
                                3 -> Toast.makeText(context,"Sort by location!", Toast.LENGTH_SHORT).show()
                            }

                        },
                        Modifier
                            .size(60.dp)
                            .padding(start = 30.dp)
                    ) {
                        Icon(
                            modifier = Modifier.size(35.dp),
                            imageVector = Icons.Rounded.Build,
                            contentDescription = "Sort",
                            tint = Color.Black,
                        )
                    }

                    IconButton(
                        onClick = {  viewModel.setRefreshUI()  },
                        Modifier
                            .size(60.dp)
                            .padding(end = 8.dp)
                    ) {
                        Icon(
                            modifier = Modifier.size(40.dp),
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = "Refresh",
                            tint = Color.Black,
                        )
                    }

                    IconButton(
                        onClick = {  onButtonClick()  },
                        Modifier
                            .size(60.dp)
                            .padding(end = 8.dp)
                    ) {
                        Icon(
                            modifier = Modifier.size(40.dp),
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "Create new task",
                            tint = Color.Black,
                        )
                    }
                }
                /*
                Row{
                    Text(
                        "${locationViewModel.latitude},${locationViewModel.longitude}",
                        modifier = Modifier.padding(start = 16.dp),
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontSize = 30.sp
                    )
                }*/
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth(),
                    //.paint(painterResource(id = R.drawable.post1), contentScale = ContentScale.FillWidth),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ){



                    //todoList.sortBy { todo -> todo.time }
                    items(todoListState.value){ item->

                        if(item.doesMatchSearchText(myValue.value)&&!item.hasCompleted()){
                            if(!item.hasBeenHistory()){
                                viewModel.selectedColor.value?.let { it1 ->
                                    viewModel.selectedPriority.value?.let { it2 ->
                                        TodoItems(item,
                                            it1,
                                            it2, viewModel, locationViewModel,onTodoItemClick,context)
                                    }
                                }
                            }else{
                                viewModel.selectedPriority.value?.let { it1 ->
                                    viewModel.selectedColor.value?.let { it2 ->
                                        OverdueItems(item, it2,
                                            it1, viewModel, locationViewModel,onTodoItemClick,context )
                                    }
                                }
                            }

                        }

                    }
                }
            }
        }
    }
}





val imgList = listOf(
    R.drawable.miku,
    R.drawable.miku,
    R.drawable.miku
)

enum class Page {
    homePage,
    AddPage,
    DetailPage
}



//@Preview
//@Composable
//fun HomeScreenPreview(){
//    homePagePre()
//}