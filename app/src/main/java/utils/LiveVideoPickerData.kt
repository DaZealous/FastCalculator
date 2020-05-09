package utils

interface LiveVideoPickerData {
    fun isChosen(video : String) : Boolean
    fun setVideo(video : String)
    fun removeVideo(video : String)
    fun getVideos(): ArrayList<String>
}