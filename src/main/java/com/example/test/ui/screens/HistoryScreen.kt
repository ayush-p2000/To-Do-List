/*The code in these page is written by Siyuan Peng*/

package com.example.test.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.test.R
import com.example.test.model.entity.TodoItem
import com.example.test.ui.components.CompletedItems
import com.example.test.ui.components.SearchBar

//@Composable
//fun historyPage(keyDAO: KeyDAO) {
//    val todoViewModel = remember { HomeScreenViewModel(keyDAO) }
//    HistoryScreen(todoViewModel)
//}
@Composable
fun historyPage(viewModel: HomeScreenViewModel,locationViewModel: LocationViewModel) {
    var page by remember {
        mutableStateOf(PageHistory.HistoryPage)
    }

    var selectedItem by remember { mutableStateOf<TodoItem?>(null) }

    when (page) {
        PageHistory.HistoryPage -> {
            HistoryScreen(
                viewModel = viewModel,
                locationViewModel = locationViewModel,
                onTodoItemClick = { todoItem ->
                    selectedItem = todoItem // 设置选中的 TodoItem
                    page = PageHistory.DetailPage // 切换到 DetailPage
                }
            )
        }
        PageHistory.DetailPage -> {
            selectedItem?.let { item ->
                DetailScreen(
                    onButtonClick = { page = PageHistory.HistoryPage },
                    item,
                    viewModel
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(viewModel: HomeScreenViewModel,locationViewModel: LocationViewModel,onTodoItemClick: (TodoItem) -> Unit) {
    val refreshUI by viewModel.refreshUI2.collectAsState(false)
    val context = LocalContext.current

    val todoList = viewModel.todoList
    val name = viewModel.userName.value

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
                    Text("Check your history", fontSize = 20.sp, color=Color.Gray)
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


    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(Color(255, 247, 209))
        ) {
            val myValue = remember { mutableStateOf("") }

            viewModel.selectedColor.value?.let { it1 -> SearchBar(_searchText = myValue, it1,todoList) }

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth(),
                //.paint(painterResource(id = R.drawable.post1), contentScale = ContentScale.FillWidth),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ){


                todoList.sortBy { todo -> todo.time }
                items(todoList){ item->

                    if(item.doesMatchSearchText(myValue.value)&&item.hasCompleted()){
                        viewModel.selectedPriority.value?.let { it1 ->
                            viewModel.selectedColor.value?.let { it2 ->
                                CompletedItems(item, it2,
                                    it1, viewModel, locationViewModel,onTodoItemClick,context )
                            }
                        }
                    }

                }
            }
        }
    }
}

enum class PageHistory {
    HistoryPage,
    DetailPage
}
//@Composable
//@Preview
//fun HistoryScreenPreview(){
//    historyPage()
//}