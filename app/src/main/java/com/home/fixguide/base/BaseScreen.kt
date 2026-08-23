package com.home.fixguide.base

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.home.fixguide.items.NoInternetIllustration

@Composable
fun BaseScreen(
    modifier: Modifier= Modifier,
    isUseSystemBarsPadding : Boolean = true,
    viewModel: BaseViewModel = hiltViewModel(),
    content: @Composable () -> Unit
){
    Box(
        modifier = modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .then(if (isUseSystemBarsPadding) Modifier.systemBarsPadding() else Modifier)
    ) {
        LaunchedEffect(Unit) {
            viewModel.initNetworkStatus()
        }
        content()

    }

    handleCommontError(viewModel)
}

@Composable
fun handleCommontError(viewModel: BaseViewModel){
    val isShow by viewModel.showCommontError.collectAsStateWithLifecycle()
    if (isShow){
        Dialog(onDismissRequest = {
            viewModel.showCommontError.value = false
        }) {
            NoInternetIllustration(modifier = Modifier.wrapContentSize()) {
                //TODO ILLU ERROR
            }
        }
    }
}
