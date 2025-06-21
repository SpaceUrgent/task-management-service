import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import AuthLayout from "../components/AuthLayout";
import AuthForm from "../components/AuthForm";
import FormField from "../components/FormField";
import { useFormValidation, validationRules } from "../hooks/useFormValidation";
import { handleRegister, createAuthLink } from "../utils/authFormUtils";

const Register = () => {
    const navigate = useNavigate();
    const [isLoading, setIsLoading] = useState(false);
    const [submitError, setSubmitError] = useState("");

    const {
        formData,
        validation,
        showErrors,
        updateField,
        showFieldError,
        isFormValid
    } = useFormValidation({
        email: validationRules.email,
        firstName: validationRules.firstName,
        lastName: validationRules.lastName,
        password: validationRules.password,
        confirmPassword: validationRules.confirmPassword
    });

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!isFormValid()) {
            Object.keys(validation).forEach(field => {
                showFieldError(field);
            });
            return;
        }

        setIsLoading(true);
        setSubmitError("");

        await handleRegister(
            {
                email: formData.email,
                firstName: formData.firstName,
                lastName: formData.lastName,
                password: formData.password,
            },
            () => {
                navigate("/login", { replace: true });
            },
            (error) => {
                setSubmitError(error);
                setIsLoading(false);
            }
        );
    };

    const footerContent = (
        <>
            {createAuthLink("Already have an account? ", "Login", "/login")}
        </>
    );

    return (
        <AuthLayout>
            <AuthForm
                title="Create an account"
                onSubmit={handleSubmit}
                submitError={submitError}
                submitButtonText="Sign Up"
                footerContent={footerContent}
                showSubmitButton={true}
                submitDisabled={!isFormValid() || isLoading}
            >
                <FormField
                    id="email"
                    type="email"
                    name="Email"
                    placeholder="username@domain.com"
                    value={formData.email}
                    onChange={(value) => updateField('email', value)}
                    onBlur={() => showFieldError('email')}
                    isValid={validation.email}
                    errorMessage="Please enter valid email address"
                    showError={showErrors.email}
                    required={true}
                />
                
                <div className="row">
                    <div className="col">
                        <FormField
                            id="firstName"
                            type="text"
                            name="First Name"
                            value={formData.firstName}
                            onChange={(value) => updateField('firstName', value)}
                            onBlur={() => showFieldError('firstName')}
                            errorMessage="Please enter valid first name"
                            isValid={validation.firstName}
                            showError={showErrors.firstName}
                            required={true}
                        />
                    </div>
                    <div className="col">
                        <FormField
                            id="lastName"
                            type="text"
                            name="Last Name"
                            value={formData.lastName}
                            onChange={(value) => updateField('lastName', value)}
                            onBlur={() => showFieldError('lastName')}
                            errorMessage="Please enter valid last name"
                            isValid={validation.lastName}
                            showError={showErrors.lastName}
                            required={true}
                        />
                    </div>
                </div>
                
                <div className="row">
                    <div className="col">
                        <FormField
                            id="password"
                            type="password"
                            name="Password"
                            value={formData.password}
                            onChange={(value) => updateField('password', value)}
                            onBlur={() => showFieldError('password')}
                            errorMessage="Password must contain at least 8 characters"
                            isValid={validation.password}
                            showError={showErrors.password}
                            required={true}
                        />
                    </div>
                    <div className="col">
                        <FormField
                            id="confirmPassword"
                            type="password"
                            name="Confirm Password"
                            value={formData.confirmPassword}
                            onChange={(value) => updateField('confirmPassword', value)}
                            onBlur={() => showFieldError('confirmPassword')}
                            errorMessage="Passwords don't match"
                            isValid={validation.confirmPassword}
                            showError={showErrors.confirmPassword}
                            required={true}
                        />
                    </div>
                </div>
            </AuthForm>
        </AuthLayout>
    );
};

export default Register;