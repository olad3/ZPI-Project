package com.example.validation

import android.util.Log
import android.util.Patterns
import com.example.amoz.R
import com.example.amoz.api.requests.AttributeCreateRequest
import com.example.amoz.app.AmozApplication
import com.example.validation.annotations.DecimalMin
import com.example.validation.annotations.Digits
import com.example.validation.annotations.Email
import com.example.validation.annotations.ListSize
import com.example.validation.annotations.Min
import com.example.validation.annotations.NoDublicateAttributes
import com.example.validation.annotations.NotBlank
import com.example.validation.annotations.NotNullable
import com.example.validation.annotations.Past
import com.example.validation.annotations.PastOrPresent
import com.example.validation.annotations.Phone
import com.example.validation.annotations.Positive
import com.example.validation.annotations.Size
import java.lang.reflect.Field
import java.math.BigDecimal
import java.time.LocalDate

object Validator {
    fun validateMin(obj: Any): String? {
        return validateTemplate(obj, Min::class.java) { field, annotation ->
            val value = field.get(obj) as? Int

            if (value != null) {
                val fieldName = annotation.nameOfField.ifBlank { field.name }

                if (value < annotation.value) {
                    return@validateTemplate "Wartość pola '${fieldName}' musi być większa " +
                            "lub równa ${annotation.value}."
                }
            }

            return@validateTemplate null
        }
    }

    fun validateListSize(obj: Any): String? {
        return validateTemplate(obj, ListSize::class.java) { field, annotation ->
            val value = field.get(obj) as? List<*>

            if (value != null) {
                val fieldName = if (annotation.nameOfField.isBlank()) {
                    field.name
                } else {
                    annotation.nameOfField
                }

                if (value.size < annotation.min) {
                    return@validateTemplate "Lista '${fieldName}' musi zawierać przynajmniej " +
                            "${annotation.min} elementów."
                }
            }

            return@validateTemplate null
        }
    }

    fun validateDigits(obj: Any): String? {
        return validateTemplate(obj, Digits::class.java) { field, annotation ->
            val value = field.get(obj) as? BigDecimal

            if (value != null) {
                val integerPart = value.toBigInteger().toString().length
                val fractionPart = value.scale()

                if (integerPart > annotation.integer || fractionPart > annotation.fraction) {
                    val fieldName = if (annotation.nameOfField.isBlank()) {
                        field.name
                    } else {
                        annotation.nameOfField
                    }

                    return@validateTemplate "Pole '${fieldName}' musi mieć wartość z maksymalnie ${annotation.integer} cyframi całkowitymi i " +
                            "${annotation.fraction} miejscami po przecinku."
                }
            }

            return@validateTemplate null
        }
    }


    fun validateDecimalMin(obj: Any): String? {
        return validateTemplate(obj, DecimalMin::class.java) { field, annotation ->
            val value = field.get(obj) as? BigDecimal

            if (value != null) {
                val minValue = BigDecimal(annotation.value)
                if ((annotation.inclusive && value <= minValue) || (!annotation.inclusive && value < minValue)) {
                    val fieldName = if (annotation.nameOfField.isBlank()) {
                        field.name
                    } else {
                        annotation.nameOfField
                    }

                    return@validateTemplate "Pole '${fieldName}' musi mieć wartość większą niż ${annotation.value}."
                }
            }
            return@validateTemplate null
        }
    }


    fun validatePastOrPresent(obj: Any): String? {
        return validateTemplate(obj, PastOrPresent::class.java) { field, annotation ->
            val value = field.get(obj)
            if (value is LocalDate) {
                val currentDate = LocalDate.now()
                if (value.isAfter(currentDate)) {
                    val fieldName = if (annotation.nameOfField.isBlank()) {
                        field.name
                    } else {
                        annotation.nameOfField
                    }

                    return@validateTemplate "Pole '${fieldName}' musi być datą z przeszłości"
                }
            }
            return@validateTemplate null
        }
    }

    fun validatePast(obj: Any): String? {
        return validateTemplate(obj, Past::class.java) { field, annotation ->
            val value = field.get(obj)
            if (value is LocalDate) {
                val currentDate = LocalDate.now()
                if (value.isAfter(currentDate) || value.isEqual(currentDate)) {
                    val fieldName = if (annotation.nameOfField.isBlank()) {
                        field.name
                    } else {
                        annotation.nameOfField
                    }

                    return@validateTemplate "Pole '${fieldName}' musi być datą z przeszłości"
                }
            }
            return@validateTemplate null
        }
    }

    fun validatePositive(obj: Any): String? {
        return validateTemplate(obj, Positive::class.java) { field, annotation ->
            val value = field.get(obj) as? Number
            if (value == null || value.toDouble() <= 0) {
                val fieldName = if (annotation.nameOfField.isBlank()) {
                    field.name
                } else {
                    annotation.nameOfField
                }

                return@validateTemplate "Pole '${fieldName}' musi być nieujemne"
            }
            return@validateTemplate null
        }
    }

    fun validateNotNullable(obj: Any): String? {
        return validateTemplate(obj, NotNullable::class.java) { field, annotation ->
            val value = field.get(obj)
            if (value == null) {
                val fieldName = if (annotation.nameOfField.isBlank()) {
                    field.name
                } else {
                    annotation.nameOfField
                }

                return@validateTemplate "Pole '${fieldName}' nie może być puste"
            }
            return@validateTemplate null
        }
    }


    fun validateEmail(obj: Any): String? {
        return validateTemplate(obj, Email::class.java) { field, annotation ->
            val value = field.get(obj) as? String?
            if (value != null) {
                val fieldName = if (annotation.nameOfField.isBlank()) {
                    field.name
                } else {
                    annotation.nameOfField
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
                    return@validateTemplate "Pole '${fieldName}' nie jest adresem email"
                }
            }
            return@validateTemplate null
        }
    }

    fun validatePhoneNumber(obj: Any): String? {
        return validateTemplate(obj, Phone::class.java) { field, annotation ->
            val value = field.get(obj) as? String?
            if (value != null) {
                val fieldName = if (annotation.nameOfField.isBlank()) { field.name }
                else { annotation.nameOfField }

                if (!Patterns.PHONE.matcher(value).matches()) {
                    return@validateTemplate "${fieldName} does not match phone pattern"
                }
            }
            return@validateTemplate null
        }
    }

    fun validateSize(obj: Any): String? {
        return validateTemplate(obj, Size::class.java) { field, annotation ->
            val value = field.get(obj) as? String?
            if (value != null) {
                val fieldName = annotation.nameOfField.ifBlank { field.name }

                if (value.length > annotation.max) {
                    return@validateTemplate "Pole '${fieldName}' przekracza maksymalną długość: ${annotation.max}"
                } else if (value.length < annotation.min) {
                    return@validateTemplate "Pole '${fieldName}' nie ma minimalnej długości: ${annotation.min}"
                }
            }
            return@validateTemplate null
        }
    }

    fun validateNoDublicateAttributes(obj: Any): String? {
        return validateTemplate(obj, NoDublicateAttributes::class.java) { field, annotation ->
            val value = field.get(obj) as? List<*>

            if (value != null) {
                val fieldName = if (annotation.nameOfField.isBlank()) {
                    field.name
                } else {
                    annotation.nameOfField
                }

                val attributeRequests = value.filterIsInstance<AttributeCreateRequest>()

                val duplicateAttributeNames = attributeRequests
                    .groupBy { it.attributeName }
                    .filter { it.value.size > 1 }
                    .keys

                if (duplicateAttributeNames.isNotEmpty()) {
                    return@validateTemplate "Lista '${fieldName}' zawiera powtarzające się atrybuty: ${duplicateAttributeNames.joinToString(", ")}."
                }
            }

            return@validateTemplate null
        }
    }


    fun validateNotBlank(obj: Any): String? {
        return validateTemplate(obj, NotBlank::class.java) { field, annotation ->
            val value = field.get(obj) as? String?
            if (value.isNullOrBlank()) {
                val fieldName = annotation.nameOfField.ifBlank { field.name }

                return@validateTemplate "$fieldName should not be blank"
            }
            return@validateTemplate null
        }
    }

    private fun <T : Annotation> validateTemplate(
        obj: Any,
        annotationClass: Class<T>,
        validateField: (Field, T) -> (String?)
    ): String? {
        val validatedFields: MutableList<String?> = mutableListOf()
        val fields = obj::class.java.declaredFields
        for (field in fields) {
            val annotation = field.getAnnotation(annotationClass)
            if (annotation != null) {
                field.isAccessible = true
                Log.i("ValidationLogger", "Validating field: ${field.name}, annotation: ${annotation}")
                validatedFields.add(validateField(field, annotation))
            }
        }
        val violations = validatedFields.filterNotNull()
        return if (violations.isEmpty()) {
            null
        } else {
            violations.first()
        }
    }


}
