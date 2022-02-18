package net.basicmodel.entity

import java.io.Serializable

data class ResultEntity(
    var status: String = "",
    var info: String = ""
) : Serializable
