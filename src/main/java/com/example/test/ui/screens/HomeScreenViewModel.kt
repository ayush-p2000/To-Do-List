/*The code in these page is written by Siyuan Peng*/

package com.example.test.ui.screens

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.test.R
import com.example.test.data.KeyDAO
import com.example.test.data.SettingDAO
import com.example.test.model.entity.SettingEntity
import com.example.test.model.entity.TodoItem
import com.example.test.ui.components.getUriToDrawable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class HomeScreenViewModel(private val keyDAO: KeyDAO,private  val settingDAO: SettingDAO) : ViewModel() {

    var todoList = mutableListOf<TodoItem>()
    private val _checkedList = mutableMapOf<Long, BooleanArray>()


    val themeId: MutableState<Int?> = mutableStateOf(1)
    val priorityId: MutableState<Int?> = mutableStateOf(1)
    val username: MutableState<String?> = mutableStateOf("User0")
    val userAvatar: MutableState<List<Uri>?> = mutableStateOf(null)
    // Initialize the checkedList for each TodoItem
    fun initializeCheckedList(todoItems: List<TodoItem>) {
        todoItems.forEach { item ->
            _checkedList[item.id.toLong()] = BooleanArray(item.check.size) { false }
        }
    }

    // Get the checkedList for a specific TodoItem
    fun getCheckedList(itemId: Long): BooleanArray {
        return _checkedList[itemId] ?: BooleanArray(0) { false }
    }

    // Toggle the checked state at a specific index for a TodoItem
    fun toggleChecked(itemId: Long, index: Int) {
        _checkedList[itemId]?.let { checkedList ->
            if (index in checkedList.indices) {
                checkedList[index] = !checkedList[index]
            }
        }
    }

    init {
        viewModelScope.launch {
            try {
                loadDataFromDatabase()

                // Initialize the checkedList after loading data
                initializeCheckedList(todoList)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private val _refreshUI = MutableStateFlow(false)
    val refreshUI: StateFlow<Boolean> = _refreshUI

    fun setRefreshUI(){
        _refreshUI.value = true
    }

    fun resetRefreshUI(){
        _refreshUI.value = false
    }

    private val _refreshUI2 = MutableStateFlow(true)
    val refreshUI2: StateFlow<Boolean> = _refreshUI2

    private val _refreshTheme = MutableStateFlow(false)
    val refreshTheme: StateFlow<Boolean> = _refreshTheme


    interface DataLoadListener {
        fun onDataLoaded(todoList: List<TodoItem>)
    }

    var dataLoadListener: DataLoadListener? = null

    suspend fun loadDataFromDatabase() {
        try {

            val keyEntities = withContext(Dispatchers.IO) {
                keyDAO.getAllKeyEntities()

            }


            todoList.clear()
            todoList.addAll(keyEntities.map { TodoItem.fromKeyEntity(it) })

            // 数据加载完成后，调用回调通知
            dataLoadListener?.onDataLoaded(todoList)
            _refreshUI.value = true
        } catch (e: Exception) {

            e.printStackTrace()
        }
    }

    suspend fun loadDataToDatabase() {
        val allEntities = settingDAO.getAllSettingEntities()
        themeId.value = allEntities[0].themeColor
        priorityId.value = allEntities[0].priorityColor
        username.value = allEntities[0].uid
        userAvatar.value = allEntities[0].uri

    }

    suspend fun initializeData(settingDao:SettingDAO,context: Context) {
        val allEntities = settingDAO.getAllSettingEntities()


        if (allEntities.isEmpty()) {
            val initialData = SettingEntity(1,1,1,"User0",getUriToDrawable(R.drawable.miku,context))

            settingDao.insertSettingEntity(initialData)
        }
    }


    fun setRefreshUI2(){
        _refreshUI.value = false
    }

    fun resetRefreshUI2(){
        _refreshUI.value = true
    }


    fun resetRefreshTheme(){
        _refreshTheme.value = !_refreshTheme.value
    }


    fun updateTodoItem(todoItem: TodoItem) {
        viewModelScope.launch {

            withContext(Dispatchers.IO) {
                keyDAO.updateKeyEntity(todoItem.toKeyEntity())
            }
        }
        /*
        withContext(Dispatchers.IO){
            // 删除数据的逻辑
            keyDAO.updateKeyEntity(todoItem.toKeyEntity())
        }*/
    }

    fun updateSetting(themeId:Int,priorityId:Int,name: String, uri:List<Uri>) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val s = SettingEntity(1,themeId,priorityId,name,uri)
                settingDAO.updateSettingEntity(s)
            }
        }

    }

    fun getTheme(id:Int):Theme{
        if(id == 1){
            Log.d("settingssss", "$themeId")
            return Theme.Blue
        }
        else{
            Log.d("settingssss", "$themeId")
            return Theme.Pink
        }
    }

    fun getPriority(id:Int):Priority{
        if(id == 1){
            return Priority.Red
        }
        else{
            return Priority.Yellow
        }
    }


    val selectedColor: MutableState<Theme> = mutableStateOf(Theme.Blue)
    val selectedPriority: MutableState<Priority> = mutableStateOf(Priority.Yellow)
    val userName: MutableState<String?> = mutableStateOf(username.value)
    val useravator: MutableState<List<Uri>?> = mutableStateOf(userAvatar.value)

//    private fun addTodoItem(title: String, time: Int, details: String, check: MutableList<ToDoItem>, priority: Boolean) {
//        val todoItem = TodoItem(title, time, details, check, priority)
//        todoList.add(todoItem)
//    }

    fun setColor(color: Theme) {
        selectedColor.value = color
    }

    fun setPriority(color: Priority) {
        selectedPriority.value = color
    }

    fun setName(name: String) {
        userName.value = name
    }

    enum class Theme {
        Blue,
        Pink
    }

    enum class Priority {
        Yellow,
        Red
    }

    var sortIndex:MutableState<Int> = mutableStateOf(0)

    fun updateTodoList() {

        sortIndex.value = (sortIndex.value + 1) % 3

        when (sortIndex.value) {
            0 -> viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    keyDAO.getSortedByDate()
                }
            }
            1 -> viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    keyDAO.getSortedByPriority()
                }
            }
            2 -> viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    keyDAO.getSortedByTitle()
                }
            }
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            try {
                loadDataFromDatabase()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val locationMap = mapOf(
        "Tesco" to Pair(53.380557366284386, -1.476808216645995),
        "Coop" to Pair(53.384568860529846, -1.4767808571720515),
        "Lidl" to Pair(53.38305155993967, -1.466870256562744),
        "Diamond" to Pair(53.38181840322182, -1.481581562631911)
    )

    fun getLat(tag:String):Double{
        val values = locationMap[tag]

        if (values != null) {
            val (firstDouble, secondDouble) = values
            return firstDouble

        } else {
            return 0.0
        }
    }

    fun getLong(tag:String):Double{
        val values = locationMap[tag]

        if (values != null) {
            val (firstDouble, secondDouble) = values
            return secondDouble

        } else {
            return 0.0
        }
    }

    fun getDistance(lat1: Double, long1: Double, lat2: Double, long2: Double): Double{
        val R = 6371.0

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(long2 - long1)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return R * c

    }
}
