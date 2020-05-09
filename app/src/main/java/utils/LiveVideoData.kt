package utils

import db.VideosEntity

interface LiveVideoData {
    fun isChosen(video : VideosEntity) : Boolean
    fun setVideo(video : VideosEntity)
    fun removeVideo(video : VideosEntity)
    fun getVideos(): ArrayList<VideosEntity>
}