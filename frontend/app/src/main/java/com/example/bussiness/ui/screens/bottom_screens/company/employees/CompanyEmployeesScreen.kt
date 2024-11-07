package com.example.bussiness.ui.screens.bottom_screens.company.employees

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.bussiness.R
import com.example.bussiness.ui.PrimaryFilledButton
import com.example.bussiness.ui.screens.bottom_screens.company.CompanyScreenViewModel
import com.example.bussiness.ui.theme.AmozApplicationTheme

@Composable
fun CompanyEmployeesScreen(
    navController: NavHostController,
    companyViewModel: CompanyScreenViewModel,
    paddingValues: PaddingValues,
    callSnackBar: (String, ImageVector?) -> Unit,
) {
    AmozApplicationTheme {
        val companyUiState by companyViewModel.companyUiState.collectAsState()
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            ) {
                // -------------------- Workers list --------------------
                EmployeesLazyColumn(
                    workers = companyUiState.companyWorkers,
                    callSnackBar = callSnackBar
                )
                Spacer(modifier = Modifier.height(20.dp))
                // -------------------- Add worker button --------------------
                PrimaryFilledButton(
                    onClick = { companyViewModel.expandAddWorkerBottomSheet(true) },
                    text = stringResource(id = R.string.company_add_worker_button),
                    leadingIcon = Icons.Outlined.Add
                )
            }
        }

        // -------------------- Add worker bottom sheet --------------------
        if (companyUiState.addWorkerBottomSheetExpanded) {
            AddEmployeesBottomSheet(
                onDismissRequest = {
                    companyViewModel.expandAddWorkerBottomSheet(false)
                },
                callSnackBar = callSnackBar,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEmployeesBottomSheet(
    onDismissRequest: () -> Unit,
    callSnackBar: (String, ImageVector?) -> Unit,
) {
    var workerEmail by remember { mutableStateOf("") }

    val emailPattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
    var isEmailValid by remember { mutableStateOf(false) }

    fun validateEmail(email: String) {
        isEmailValid = email.matches(emailPattern.toRegex())
    }

    val invSentSuccessful = stringResource(id = R.string.company_add_worker_invite_successful)

    ModalBottomSheet(onDismissRequest = onDismissRequest) {
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // -------------------- Title --------------------
            Text(
                text = stringResource(id = R.string.company_add_worker_invite_title),
                style = MaterialTheme.typography.headlineMedium
            )

            // -------------------- Worker's email --------------------
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(text = stringResource(id = R.string.company_add_worker_email_textfield))
                },
                value = workerEmail,
                onValueChange = {
                    workerEmail = it
                    validateEmail(workerEmail)
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Outlined.Mail, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                maxLines = 1,
                singleLine = true
            )

            // -------------------- Confirm button --------------------
            PrimaryFilledButton(
                onClick = {
                    /*TODO*/
                    /*SEND INVITATION TO PERSON VIA BACKEND*/
                    onDismissRequest()
                    callSnackBar(
                        invSentSuccessful,
                        Icons.Outlined.Done
                    )
                },
                isEnabled = isEmailValid,
                text = stringResource(id = R.string.company_add_worker_email_send_inv)
            )
        }
    }
}
