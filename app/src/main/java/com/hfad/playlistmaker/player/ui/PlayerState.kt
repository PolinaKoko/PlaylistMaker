package com.hfad.playlistmaker.player.ui

sealed class PlayerState {

    object Default : PlayerState()
    object Prepared : PlayerState()
    object Playing : PlayerState()
    object Paused : PlayerState()
    object Completed : PlayerState()
    data class Error(val message: String) : PlayerState()
}