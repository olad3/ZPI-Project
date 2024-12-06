package com.example.amoz.ui.screens.bottom_screens.company

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.amoz.R
import com.example.amoz.models.Invitation
import com.example.amoz.ui.components.CompanyInvitationListItem
import com.example.amoz.ui.components.CustomDialogWindow
import com.example.amoz.ui.components.PrimaryFilledButton
import com.example.amoz.ui.components.PrimaryOutlinedButton
import com.example.amoz.ui.components.ResultStateView
import com.example.amoz.ui.screens.Screens
import com.example.amoz.ui.theme.AmozApplicationTheme
import com.example.amoz.view_models.CompanyViewModel
import com.example.amoz.view_models.EmployeeViewModel

@Composable
fun NoCompanyScreen (
    navController: NavHostController,
    paddingValues: PaddingValues,
    companyViewModel: CompanyViewModel,
    employeeViewModel: EmployeeViewModel = hiltViewModel()
){
    val companyUIState by companyViewModel.companyUIState.collectAsState()
    var showAcceptDialogWindow by remember { mutableStateOf(false) }
    var selectedInvitation by remember { mutableStateOf<Invitation?>(null) }

    LaunchedEffect(Unit) {
        companyViewModel.fetchInvitations()
    }

    AmozApplicationTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            ResultStateView(
                companyUIState.fetchedInvitationListState,
                onPullToRefresh = { companyViewModel.fetchInvitations()
                }) { invitationsList ->
                var mutableInvitationsList by remember { mutableStateOf(invitationsList) }

                if (invitationsList.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(15.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(30.dp, Alignment.CenterVertically),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Spacer(modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            text = stringResource(id = R.string.company_no_company),
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground,
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        PrimaryOutlinedButton(
                            text = stringResource(id = R.string.company_register),
                            onClick = {
                                navController.navigate(Screens.CreateCompany.route)
                            }
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(15.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(vertical = 15.dp)
                    ) {
                        item {
                            Text(
                                text = stringResource(id = R.string.your_company_invites),
                                style = MaterialTheme.typography.headlineSmall,
                            )
                        }

                        items(
                            mutableInvitationsList,
                            key = { it.token }
                        ) { invitation ->
                            CompanyInvitationListItem (
                                invitation = invitation,
                                onAcceptClick = {
                                    selectedInvitation = invitation
                                    showAcceptDialogWindow = true
                                },
                                onDeclineClick = {
                                    employeeViewModel.declineInvitation(invitation.token.toString())
                                    mutableInvitationsList = invitationsList.filter { it.token != invitation.token }
                                }
                            )
                        }

                        item {
                            PrimaryFilledButton(
                                text = stringResource(id = R.string.or_company_register),
                                onClick = {
                                    navController.navigate(Screens.CreateCompany.route)
                                }
                            )
                        }
                    }
                }
            }
        }

        if (showAcceptDialogWindow) {
            CustomDialogWindow(
                title = stringResource(id = R.string.accept_invitation_window_title),
                text = stringResource(id = R.string.accept_invitation_window_text),
                onDismiss = { showAcceptDialogWindow = false },
                onAccept = {
                    employeeViewModel.acceptInvitation(selectedInvitation?.token.toString())
                    companyViewModel.fetchCompanyDetails()
                    navController.navigate(Screens.Company.route)
                    showAcceptDialogWindow = false
                    selectedInvitation = null
                },
                onReject = {
                    showAcceptDialogWindow = false
                    selectedInvitation = null
                }
            )
        }
    }
}