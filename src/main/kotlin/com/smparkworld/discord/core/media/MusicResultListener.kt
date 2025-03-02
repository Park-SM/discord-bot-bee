package com.smparkworld.discord.core.media

import com.smparkworld.discord.core.media.model.LoadingFailureReason
import com.smparkworld.discord.core.media.model.LoadingResult

interface MusicResultListener {

    fun onLoadSuccess(result: LoadingResult)
    fun onLoadFailure(reason: LoadingFailureReason)
}