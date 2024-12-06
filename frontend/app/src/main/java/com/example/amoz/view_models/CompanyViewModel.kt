package com.example.amoz.view_models

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.navigation.NavController
import com.example.amoz.api.enums.RoleInCompany
import com.example.amoz.api.repositories.CompanyRepository
import com.example.amoz.api.repositories.CustomerRepository
import com.example.amoz.api.repositories.EmployeeRepository
import com.example.amoz.api.requests.AddressCreateRequest
import com.example.amoz.api.requests.CompanyCreateRequest
import com.example.amoz.api.requests.CustomerB2BCreateRequest
import com.example.amoz.api.requests.CustomerB2CCreateRequest
import com.example.amoz.api.sealed.ResultState
import com.example.amoz.extensions.toMultipartBodyPart
import com.example.amoz.extensions.updateResultState
import com.example.amoz.ui.screens.Screens
import com.example.amoz.models.CustomerB2B
import com.example.amoz.models.CustomerB2C
import com.example.amoz.models.Invitation
import com.example.amoz.test_data.invitations.createMockInvitations
import com.example.amoz.ui.states.CompanyUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import okhttp3.MultipartBody
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CompanyViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val employeeRepository: EmployeeRepository,
    private val companyRepository: CompanyRepository,
    private val customerRepository: CustomerRepository
): BaseViewModel() {
    private val _companyUIState = MutableStateFlow(CompanyUIState())
    val companyUIState: StateFlow<CompanyUIState> = _companyUIState.asStateFlow()

    val ownerExceptionMessage = "Current employee is not owner"

    private val newCompanyImageUri = MutableStateFlow<Uri?>(null)

    private val _companyCreateRequestState = MutableStateFlow(CompanyCreateRequest())
    val companyCreateRequestState: StateFlow<CompanyCreateRequest> = _companyCreateRequestState.asStateFlow()

    init {
        fetchCompanyDetails()
        fetchCompanyImage()
        fetchEmployeeData()
    }

    fun createCompany(navController: NavController) {
        val companyCreateRequest = companyCreateRequestState.value
        val validationErrorMessage = companyCreateRequest.validate()
        if (validationErrorMessage == null) {
            performRepositoryAction(
                _companyUIState.value.company,
                "Could not create company",
                action = {
                    companyRepository.createCompany(companyCreateRequest)
                },
                onSuccess = { company ->
                    if (newCompanyImageUri.value != null) {
                        uploadCompanyProfilePicture()
                        navController.navigate(Screens.Company.route)
                    }
                }
            )
        } else {
            Log.w(tag, validationErrorMessage)
        }
    }

    fun updateNewCompanyImageUri(uri: Uri) {
        newCompanyImageUri.value = uri
    }

    fun updateCompanyCreateRequest(companyCreateRequest: CompanyCreateRequest) {
        _companyCreateRequestState.value = companyCreateRequest
    }

    fun uploadCompanyProfilePicture() {
        performRepositoryAction(
            binding = null,
            "Could not upload profile picture. Try again later.",
            action = {
                newCompanyImageUri.value?.let {
                    companyRepository.uploadCompanyProfilePicture(file = it.toMultipartBodyPart(context))
                }
            }
        )
    }

    fun updateCompany(
        companyCreateRequest: CompanyCreateRequest,
        roleInCompany: RoleInCompany?,
    ) {
        val validationErrorMessage = companyCreateRequest.validate()
        if (validationErrorMessage == null ) {
            if (roleInCompany == RoleInCompany.OWNER) {
                performRepositoryAction(_companyUIState.value.company, "Could not update company",
                    skipLoading = true,
                    action = {
                        companyRepository.updateCompany(companyCreateRequest)
                    })
            } else { Log.e(tag, ownerExceptionMessage) }
        } else {
            Log.w(tag, validationErrorMessage)
        }
    }

    fun updateCompanyAddress(
        request: AddressCreateRequest,
        roleInCompany: RoleInCompany?,
    ) {
        if (roleInCompany == RoleInCompany.OWNER) {
            _companyUIState.value.companyCreateRequestState.address = request
            updateCompany(_companyUIState.value.companyCreateRequestState, roleInCompany)
        }
    }

    fun fetchCompanyDetails(skipLoading: Boolean = false) {
        performRepositoryAction(
            binding = _companyUIState.value.company,
            failureMessage = "Could not fetch company details. Try again later.",
            skipLoading = skipLoading,
            action = {
                companyRepository.getUserCompany()
            }, onSuccess = { company ->
                _companyUIState.update { it.copy(companyCreateRequestState = CompanyCreateRequest(company)) }
            })
    }

    fun fetchCompanyImage(skipLoading: Boolean = false) {
        performRepositoryAction(
            binding = _companyUIState.value.companyLogo,
            failureMessage = "Could not fetch company logo. Try again later",
            skipLoading = skipLoading,
            action = { companyRepository.getCompanyProfilePicture() },
            onSuccess = {}
        )
    }

    fun fetchInvitations() {
        val mockInvitations = createMockInvitations()
        _companyUIState.update { it.copy(fetchedInvitationListState = MutableStateFlow(ResultState.Success(mockInvitations))) }

//        performRepositoryAction(
//            binding = _companyUIState.value.fetchedInvitationListState,
//            failureMessage = "Could not fetch invitations. Try again later.",
//            action = {
//                employeeRepository.fetchInvitations()
//            }
//        )
    }

    fun updateCompanyLogo(
        image: MultipartBody.Part,
        roleInCompany: RoleInCompany?,
        skipLoading: Boolean = false
    ) {
        if (roleInCompany == RoleInCompany.OWNER) {
            performRepositoryAction(null, "Could not update company logo. Try again later",
                skipLoading = skipLoading,
                action = { companyRepository.uploadCompanyProfilePicture(image) },
                onSuccess = { fetchCompanyImage() }
            )
        } else { Log.e(tag, ownerExceptionMessage) }
    }

    // -------------------- EMPLOYEES --------------------
    fun fetchEmployeeData() {
        performRepositoryAction(
            null,
            action = { employeeRepository.fetchEmployee() },
            onSuccess = { employee -> _companyUIState.update { it.copy(currentEmployee = employee) } }
        )
    }

    fun fetchEmployees() {
        performRepositoryAction(
            _companyUIState.value.employees,
            "Could not fetch employees. Try again later.",
            action = { employeeRepository.fetchEmployees() }
        )
    }

    fun getProfilePicture(employeeId: UUID) {
        val map = _companyUIState.value.employeeImages.value
        if (!map.containsKey(employeeId)) {
            map[employeeId] = MutableStateFlow(ResultState.Idle)
        }
        map[employeeId]?.let {
            performRepositoryAction(it, "Could fetch employee profile picture. Try again later.",
                action = {
                    employeeRepository.getEmployeePicture(employeeId)
                }
            )
        }
    }

    fun inviteNewEmployee(
        employeeEmail: String,
        roleInCompany: RoleInCompany?,
    ) {
        if (roleInCompany == RoleInCompany.OWNER) {
            performRepositoryAction(
                binding = null,
                failureMessage = "Could not invite new employee. Try again later.",
                action = { employeeRepository.inviteEmployeeToCompany(employeeEmail) }
            )
        } else { Log.e(tag, ownerExceptionMessage) }
    }

    fun kickEmployeeFromCompany(
        employeeId: UUID,
        roleInCompany: RoleInCompany?,
    ) {
        if (roleInCompany == RoleInCompany.OWNER) {
            performRepositoryAction(
                binding = null,
                failureMessage = "Could not kick employee. Try again later.",
                action = { employeeRepository.kickEmployeeFromCompany(employeeId) }
            )
        } else { Log.e(tag, ownerExceptionMessage) }
    }

    // -------------------- CUSTOMERS --------------------
    fun fetchCustomersB2B(skipLoading: Boolean = false) {
        performRepositoryAction(_companyUIState.value.companyB2BCustomers, "Could not fetch employees. Try again later.",
            skipLoading = skipLoading,
            action = {
                customerRepository.getAllCustomersB2B().toMutableList()
            }
        )
    }

    fun fetchCustomersB2C(skipLoading: Boolean = false) {
        performRepositoryAction(
            _companyUIState.value.companyB2CCustomers,
            "Could not fetch employees. Try again later.",
            skipLoading = skipLoading,
            action = {
                customerRepository.getAllCustomersB2C()
            }
        )
    }


    fun createB2BCustomer(
        b2bCustomerCreateRequest: CustomerB2BCreateRequest,
        onSuccessCallback: (() -> Unit)? = null
    ) {
        val validationErrorMessage = b2bCustomerCreateRequest.validate()
        if (validationErrorMessage == null) {
            performRepositoryAction(null, "Could not create company",
                skipLoading = true,
                action = {
                    customerRepository.createCustomerB2B(b2bCustomerCreateRequest)
                }, onSuccess = {
                    fetchCustomersB2C(true)
                    onSuccessCallback?.invoke()
                })
        } else {
            Log.w(tag, validationErrorMessage)
        }
    }

    fun updateB2BCustomer(
        customerId: UUID,
        b2bCustomerCreateRequest: CustomerB2BCreateRequest,
        onSuccessCallback: (() -> Unit)? = null
    ) {
        performRepositoryAction(null, "Could not update b2c customer",
            action = { customerRepository.updateCustomerB2B(customerId, b2bCustomerCreateRequest) },
            onSuccess = {
                fetchCustomersB2B(true)
                onSuccessCallback?.invoke()
            }
        )
    }

    fun createB2CCustomer(
        b2cCustomerCreateRequest: CustomerB2CCreateRequest,
        onSuccessCallback: (() -> Unit)? = null
    ) {
        val validationErrorMessage = b2cCustomerCreateRequest.validate()
        if (validationErrorMessage == null) {
            performRepositoryAction(null, "Could not create b2c customer",
                skipLoading = true,
                action = {
                    customerRepository.createCustomerB2C(b2cCustomerCreateRequest)
                },
                onSuccess = {
                    fetchCustomersB2C(true)
                    onSuccessCallback?.invoke()
                }
            )
        } else {
            Log.w(tag, validationErrorMessage)
        }
    }

    fun updateB2CCustomer(
        customerId: UUID,
        b2cCustomerCreateRequest: CustomerB2CCreateRequest,
        onSuccessCallback: (() -> Unit)? = null
    ) {
        performRepositoryAction(null, "Could not update b2c customer",
            action = { customerRepository.updateCustomerB2C(customerId, b2cCustomerCreateRequest) },
            onSuccess = {
                fetchCustomersB2C(true)
                onSuccessCallback?.invoke()
            }
        )
    }

    fun updateCustomerB2cCreateRequestState(b2cCustomer: CustomerB2C?) {
        _companyUIState.update { currState -> currState.copy(
            currentCustomerB2C = b2cCustomer,
            customerB2CCreateRequestState = b2cCustomer?.let {
                CustomerB2CCreateRequest(b2cCustomer)
            } ?: CustomerB2CCreateRequest()
        ) }
    }

    fun updateCustomerB2BCreateRequestState(b2bCustomer: CustomerB2B?) {
        _companyUIState.update { currState -> currState.copy(
            currentCustomerB2B = b2bCustomer,
            customerB2BCreateRequestState = b2bCustomer?.let {
                CustomerB2BCreateRequest(b2bCustomer)
            } ?: CustomerB2BCreateRequest()
        ) }
    }




    // -------------------- UI STATE --------------------

    fun expandEmployeeProfileBottomSheet(expand: Boolean) {
        _companyUIState.update { currState ->
            currState.copy(employeeProfileBottomSheetExpanded = expand)
        }
    }

    fun expandChangeCompanyNameBottomSheet(expand: Boolean) {
        _companyUIState.update { currState ->
            currState.copy(changeCompanyNameBottomSheetExpanded = expand)
        }
    }

    fun expandChangeCompanyLogoBottomSheet(expand: Boolean) {
        _companyUIState.update { currState ->
            currState.copy(changeCompanyLogoBottomSheetExpanded = expand)
        }
    }

    fun expandChangeCompanyAddressBottomSheet(expand: Boolean) {
        _companyUIState.update { currState ->
            currState.copy(changeCompanyAddressBottomSheetExpanded = expand)
        }
    }

    fun expandAddEmployeeBottomSheet(expand: Boolean) {
        _companyUIState.update { currState ->
            currState.copy(addEmployeeBottomSheetExpanded = expand)
        }
    }

    fun expandAddEditB2BCustomerBottomSheet(isVisible: Boolean) {
        _companyUIState.update { currState ->
            currState.copy(addB2BCustomerBottomSheetExpanded = isVisible)
        }
    }

    fun expandAddEditB2CCustomerBottomSheet(isVisible: Boolean) {
        _companyUIState.update { currState ->
            currState.copy(addB2CCustomerBottomSheetExpanded = isVisible)
        }
    }
}