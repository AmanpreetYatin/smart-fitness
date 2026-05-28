package com.smartfitness.app.core.utilities

object AppValidator {

    // ─────────────────────────────────────────────
    // Email Validation
    // ─────────────────────────────────────────────
    fun validateEmail(email: String): ValidationResult {
        return when {
            email.isBlank() -> ValidationResult.Error("Email cannot be empty")
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                ValidationResult.Error("Invalid email format")
            else -> ValidationResult.Success
        }
    }

    // ─────────────────────────────────────────────
    // Password Validation
    // ─────────────────────────────────────────────
    fun validatePassword(password: String): ValidationResult {
        return when {
            password.isBlank() -> ValidationResult.Error("Password cannot be empty")
            password.length < 6 -> ValidationResult.Error("Password must be at least 6 characters")
            else -> ValidationResult.Success
        }
    }

    // ─────────────────────────────────────────────
    // Name Validation
    // ─────────────────────────────────────────────
    fun validateName(name: String): ValidationResult {
        return when {
            name.isBlank() -> ValidationResult.Error("Name cannot be empty")
            name.length < 2 -> ValidationResult.Error("Name too short")
            else -> ValidationResult.Success
        }
    }

    // ─────────────────────────────────────────────
    // Generic Required Field
    // ─────────────────────────────────────────────
    fun validateRequired(value: String, fieldName: String): ValidationResult {
        return if (value.isBlank()) {
            ValidationResult.Error("$fieldName cannot be empty")
        } else {
            ValidationResult.Success
        }
    }
}

sealed class ValidationResult {
    object Success : ValidationResult()
    data class Error(val message: String) : ValidationResult()
}