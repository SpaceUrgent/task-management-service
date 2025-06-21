# Auth Module

This module contains the authentication system for the task management service, including login and registration functionality.

## Structure

```
src/auth/
├── pages/           # Auth pages (Login, Register)
├── components/      # Reusable auth components
├── hooks/          # Custom hooks for form validation
├── utils/          # Utility functions for auth operations
├── api/            # API client for auth operations
└── index.js        # Main exports
```

## Components

### Pages
- **Login.jsx** - User login page
- **Register.jsx** - User registration page

### Reusable Components
- **AuthLayout.jsx** - Layout wrapper for auth pages
- **AuthForm.jsx** - Enhanced form component with error handling and loading states
- **FormField.jsx** - Reusable form field component

## Custom Hooks

### useFormValidation
A custom hook that handles form validation logic:

```javascript
const {
    formData,
    validation,
    showErrors,
    updateField,
    showFieldError,
    isFormValid
} = useFormValidation({
    email: validationRules.email,
    password: validationRules.required
});
```

## Utility Functions

### Auth Form Utils
- `handleLogin(credentials, onSuccess, onError)` - Handles login API calls
- `handleRegister(userData, onSuccess, onError)` - Handles registration API calls
- `createAuthLink(text, linkText, to)` - Creates navigation links between auth pages

## Validation Rules

Predefined validation rules available:
- `email` - Email format validation
- `password` - Password strength validation (min 8 characters)
- `confirmPassword` - Password confirmation matching
- `firstName` - First name format validation
- `lastName` - Last name format validation
- `required` - Required field validation

## Usage Example

```javascript
import { Login, Register, useFormValidation, validationRules } from '../auth';

// In a component
const { formData, validation, updateField, isFormValid } = useFormValidation({
    email: validationRules.email,
    password: validationRules.password
});
```

## Features

- **Form Validation**: Real-time validation with custom rules
- **Error Handling**: Centralized error handling for API calls
- **Loading States**: Built-in loading indicators
- **Reusable Components**: Modular design for easy maintenance
- **Type Safety**: Consistent prop interfaces
- **Accessibility**: Proper form labels and error messages 