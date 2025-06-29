import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../shared/contexts/AuthContext";
import AuthLayout from "../components/AuthLayout";
import AuthForm from "../components/AuthForm";
import { handleLogin, createAuthLink } from "../utils/authFormUtils";
import {useForm} from "react-hook-form";
import ValidatedFormInput from "../../shared/components/ValidatedFormInput";
import AppConstants from "../../AppConstants.ts";

const Login = () => {
    const { login } = useAuth();
    const navigate = useNavigate();
    const [isLoading, setIsLoading] = useState(false);
    const [submitError, setSubmitError] = useState("");

    const {
        register,
        handleSubmit,
        formState: { errors, isValid, touchedFields }
    } = useForm({
        mode: "all",
        defaultValues: {
            email: "",
            password: ""
        }
    });

    const onSubmit = async (data) => {
        setIsLoading(true);
        setSubmitError("");

        await handleLogin(
            { email: data.email, password: data.password },
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
                onSubmit={handleSubmit(onSubmit)}
                title="Sign In"
                submitError={submitError}
                submitButtonText="Sign In"
                footerContent={footerContent}
                showSubmitButton={true}
                submitDisabled={!isValid || isLoading}
            >
                <ValidatedFormInput
                    label="Email"
                    name="email"
                    type="email"
                    placeholder="username@domain.com"
                    registration={register("email", {
                        required: "Please enter valid email address",
                        pattern: {
                            value: AppConstants.VALID_EMAIL_REGEX,
                            message: "Please enter valid email address"
                        }
                    })}
                    errors={errors}
                    touchedFields={touchedFields}
                />
                <ValidatedFormInput
                    name="password"
                    label="Password"
                    type="password"
                    registration={register("password", {
                        required: "Please enter password"
                    })}
                    errors={errors}
                    touchedFields={touchedFields}
                />
            </AuthForm>
        </AuthLayout>
    );
};

export default Login;