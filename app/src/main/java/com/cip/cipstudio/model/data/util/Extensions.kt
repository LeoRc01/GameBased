package com.cip.cipstudio.model.data.util

import java.text.SimpleDateFormat
import java.util.*



fun Int.toDate() : String{
    val dateFormat = SimpleDateFormat("dd/MM/yyyy")
    val date = dateFormat.format(Date(this.toLong() * 1000))
    return date
}

