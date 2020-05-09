package utils

import db.ImagesEntity

interface LiveImageData {
    fun isChosen(image: ImagesEntity) : Boolean
    fun setImage(image : ImagesEntity)
    fun removeImage(image: ImagesEntity)
}