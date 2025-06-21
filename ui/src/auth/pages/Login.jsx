import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../common/contexts/AuthContext";
import AuthLayout from "../components/AuthLayout";
import AuthForm from "../components/AuthForm";
import { handleLogin, createAuthLink } from "../utils/authFormUtils";
import FormInput from "../../common/components/FormInput";
import { useFormValidation } from "../../common/hooks/useFormValidation";
import AppConstants from "../../AppConstants.ts";

const Login = () => {
    const { login } = useAuth();
    const navigate = useNavigate();
    const [isLoading, setIsLoading] = useState(false);
    const [submitError, setSubmitError] = useState("");

    const validationRules = {
        email: (value) => value && AppConstants.VALID_EMAIL_REGEX.test(value),
        password: (value) => !!value,
    };

    const {
        formData,
        validation,
        showErrors,
        updateField,
        showFieldError,
        isFormValid
    } = useFormValidation(validationRules);

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

        await handleLogin(
            { email: formData.email, password: formData.password },
            () => {
                login();
                navigate("/projects", { replace: true });
            },
            (error) => {
                setSubmitError(error);
                setIsLoading(false);
            }
        );
    };

    const footerContent = (
        <>
            {createAuthLink("Don't have an account? ", "Register", "/register")}
        </>
    );

    return (
        <AuthLayout>
            <AuthForm
                onSubmit={handleSubmit}
                title="Sign In"
                submitError={submitError}
                submitButtonText="Sign In"
                footerContent={footerContent}
                showSubmitButton={true}
                submitDisabled={!isFormValid() || isLoading}
            >
                <FormInput
                    id="email"
                    name="Email"
                    type="email"
                    placeholder="username@domain.com"
                    value={formData.email}
                    onChange={(value) => updateField('email', value)}
                    onBlur={() => showFieldError('email')}
                    isValid={validation.email}
                    showError={showErrors.email}
                    errorMessage="Please enter valid email address"
                    required={true}
                />
                
                <FormInput
                    id="password"
                    name="Password"
                    type="password"
                    value={formData.password}
                    onChange={(value) => updateField('password', value)}
                    onBlur={() => showFieldError('password')}
                    isValid={validation.password}
                    showError={showErrors.password}
                    errorMessage="Please enter password"
                    required={true}
                />
            </AuthForm>
        </AuthLayout>
    );
};

export default Login;