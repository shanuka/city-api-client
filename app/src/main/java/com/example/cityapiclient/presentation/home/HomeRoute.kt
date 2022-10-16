package com.example.cityapiclient.presentation.home

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cityapiclient.data.local.CurrentUser
import com.example.cityapiclient.domain.SignInObserver
import com.example.cityapiclient.presentation.layouts.AppLayoutMode
import com.example.cityapiclient.presentation.layouts.CompactLayoutWithScaffold
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLifecycleComposeApi::class)
@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    signInObserver: SignInObserver,
    appLayoutMode: AppLayoutMode
) {

    val homeUiState by viewModel.homeUiState.collectAsStateWithLifecycle()
    val signInState by signInObserver.signInState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Check for user messages to display on the screen
    signInState.userMessage?.let { userMessage ->
        LaunchedEffect(signInState.userMessage, userMessage) {
            snackbarHostState.showSnackbar(userMessage)
            signInObserver.userMessageShown()
        }
    }

    Log.d("debug", "isSigining in from composable: ${signInState.isSigningIn}")

    CompactLayoutWithScaffold(
        snackbarHostState = { SnackbarHost(hostState = snackbarHostState) },
        mainContent = {

            if (!homeUiState.isLoading) {
                when (homeUiState.currentUser) {
                    is CurrentUser.UnknownSignIn -> {
                        SignUpScreen(
                            appLayoutMode,
                            { scope.launch { signInObserver.signUp() } },
                            signInState.isSigningIn
                        )
                    }
                    is CurrentUser.SignedInUser -> {
                        Column(Modifier.padding(16.dp)) {

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                modifier = Modifier.padding(4.dp),
                                text = (homeUiState.currentUser as CurrentUser.SignedInUser).name,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                modifier = Modifier.padding(4.dp),
                                text = (homeUiState.currentUser as CurrentUser.SignedInUser).email,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                    }
                    else -> {}
                }
            }

        }, title = if (!homeUiState.isLoading) HomeAppBarTitle(homeUiState.currentUser) else ""
    )

}

@Composable
private fun HomeAppBarTitle(currentUser: CurrentUser): String =
    if (currentUser is CurrentUser.UnknownSignIn)
        "Get Started"
    else
        "Your Apps"



