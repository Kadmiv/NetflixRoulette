package com.kadmiv.filmrepo.utils.enums

enum class ViewState(val value: Int) {
    EMPTY_STATE(-1),
    CONTENT_STATE(0),
    LOADING_STATE(1),
    ERROR_STATE(2),
    NETWORK_ERROR_STATE(3)
}