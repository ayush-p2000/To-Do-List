package com.example.test.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.test.data.KeyDAO
import com.example.test.data.SettingDAO
import com.example.test.model.entity.NavigationItem
import com.example.test.model.entity.ScreenID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


val navigationItems= listOf(
    NavigationItem("History", Icons.Filled.CheckCircle, Icons.Outlined.CheckCircle, ScreenID.HISTORY),
    NavigationItem("Home", Icons.Filled.Home, Icons.Outlined.Home, ScreenID.Home),
    NavigationItem("Settings", Icons.Filled.Settings,Icons.Outlined.Settings, ScreenID.SETTINGS)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainFrame(keyDAO: KeyDAO,settingDAO:SettingDAO,intent: Intent,locationViewModel: LocationViewModel){
    val context = LocalContext.current
    val homeScreenViewModel: HomeScreenViewModel = HomeScreenViewModel(keyDAO,settingDAO)

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            homeScreenViewModel.initializeData(settingDAO, context)
            homeScreenViewModel.loadDataToDatabase()
        }
    }


    var currentNavigationIndex by remember {
        mutableStateOf(ScreenID.Home)
    }

    var title by remember {
        mutableStateOf("Home")
    }

    fun updateSelectedID(id:Int,newTitle: String){
        currentNavigationIndex = id;
        title = newTitle;
    }

    Scaffold(
        bottomBar = {
            NavigationComponents(selectedScreenID = currentNavigationIndex, updateSelected = ::updateSelectedID)
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            when(currentNavigationIndex){
                ScreenID.HISTORY -> {
                    System.out.println(111)
                    historyPage(homeScreenViewModel,locationViewModel)
                }
                ScreenID.Home -> homePage(homeScreenViewModel,intent,locationViewModel)
                ScreenID.SETTINGS -> SettingsScreen(homeScreenViewModel)
            }
        }
    }
}

@Composable
fun NavigationComponents(selectedScreenID: Int,updateSelected:(ScreenID:Int,newTitle:String)->Unit){
    NavigationBar {
        navigationItems.forEach{ navigation->
            NavigationBarItem(
                label={Text(navigation.title)},
                icon={ navigation.iconStyle(selectedScreenID)},
                selected = (selectedScreenID==navigation.ScreenID),
                onClick = { updateSelected(navigation.ScreenID,navigation.title)})
        }
    }
}
