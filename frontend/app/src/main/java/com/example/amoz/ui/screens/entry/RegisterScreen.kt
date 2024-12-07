package com.example.amoz.ui.screens.entry

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.amoz.R
import com.example.amoz.api.enums.Sex
import com.example.amoz.api.requests.ContactPersonCreateRequest
import com.example.amoz.api.requests.PersonCreateRequest
import com.example.amoz.ui.components.PrimaryFilledButton
import com.example.amoz.ui.components.PrimaryOutlinedButton
import com.example.amoz.ui.components.SexDropdownMenu
import com.example.amoz.ui.components.text_fields.DateTextField
import com.example.amoz.ui.screens.Screens
import com.example.amoz.ui.theme.AmozApplicationTheme
import com.example.amoz.view_models.AuthenticationViewModel
import com.example.amoz.view_models.UserViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen (
    navController: NavHostController,
    paddingValues: PaddingValues,
    userViewModel: UserViewModel = hiltViewModel(),
    authenticationViewModel: AuthenticationViewModel = hiltViewModel(),
){
    val userUiState by userViewModel.userUiState.collectAsState()
    val context = LocalContext.current

    var personData by remember {
        mutableStateOf(
            PersonCreateRequest(
                name = "",
                surname = "",
                sex = Sex.M,
                dateOfBirth = LocalDate.now(),
            )
        )
    }

    var contactPersonData by remember {
        mutableStateOf(
            ContactPersonCreateRequest(
                contactNumber = "",
                emailAddress = "",
            )
        )
    }

    var validationMessage by remember { mutableStateOf<String?>(null) }


    AmozApplicationTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(15.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(30.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.entry_almost_ready),
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onBackground
                )

                OutlinedTextField(
                    value = personData.name,
                    onValueChange = { if (it.length <= 30) { personData = personData.copy(name = it) } },
                    label = { Text(stringResource(R.string.profile_first_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    singleLine = true,
                )

                OutlinedTextField(
                    value = personData.surname,
                    onValueChange = { if (it.length <= 30 ) {personData = personData.copy(surname = it) } },
                    label = { Text(stringResource(R.string.profile_last_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    singleLine = true,
                )

                SexDropdownMenu(
                    selectedSex = personData.sex,
                    onSexChange = { personData = personData.copy(sex = it) },
                )

                DateTextField(
                    label = stringResource(id = R.string.profile_birth_date,),
                    date = personData.dateOfBirth,
                    onDateChange = { personData = personData.copy( dateOfBirth = it as LocalDate ) },
                    showTime = false,
                    trailingIcon = Icons.Default.DateRange,
                )

                OutlinedTextField(
                    value = contactPersonData.emailAddress ?: "",
                    onValueChange = { contactPersonData = contactPersonData.copy(emailAddress = it) },
                    label = { Text(stringResource(R.string.profile_email)) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    singleLine = true,
                )

                OutlinedTextField(
                    value = contactPersonData.contactNumber,
                    onValueChange = { contactPersonData = contactPersonData.copy(contactNumber = it) },
                    label = { Text(stringResource(R.string.profile_phone_number)) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    singleLine = true,
                )

                validationMessage?.let {
                    Log.d("ValidationMessageRegisterScreen", it)
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                PrimaryFilledButton(
                    onClick = {
                        val personViolations = personData.validate()
                        val contactPersonViolations = contactPersonData.validate()

                        if (personViolations != null) {
                            validationMessage += "\n" + personViolations
                        } else if (contactPersonViolations != null) {
                            validationMessage += "\n" + contactPersonViolations
                        } else {
                            userViewModel.updateCurrentUserRegisterRequest(
                                personData,
                                contactPersonData
                            )

                            navController.navigate(Screens.RegisterImage.route)
                        }
                    },
                    text = stringResource(R.string.entry_continue),
                )

                PrimaryOutlinedButton(
                    onClick = {
                        authenticationViewModel.signOut(context as Activity) {
                            navController.navigate(Screens.Entry.route){
                                popUpTo(0)
                            }
                        }
                    },
                    text = stringResource(R.string.cancel),
                )

            }
        }
    }
}
