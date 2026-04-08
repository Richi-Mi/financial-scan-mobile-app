package com.richi_mc.myapplication.data.api.dto
import com.google.gson.annotations.SerializedName

data class ScanRequest(
    @SerializedName("user_id") val userId: String,
    @SerializedName("tipo_de_peticion") val tipoDePeticion: Int, // 0 = OCR, 1 = Voz
    @SerializedName("texto") val texto: String
)
