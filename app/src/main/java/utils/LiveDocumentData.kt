package utils

import db.DocumentEntity

interface LiveDocumentData {
    fun isChosen(documents : DocumentEntity) : Boolean
    fun setDocument(documents : DocumentEntity)
    fun removeDocument(documents : DocumentEntity)
}