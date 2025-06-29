import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import AuthLayout from "../components/AuthLayout";
import AuthForm from "../components/AuthForm";
import { handleRegister, createAuthLink } from "../utils/authFormUtils";
import { useForm } from "react-hook-form";
import AppConstants from "../../AppConstants.ts";
import ValidatedFormInput from "../../shared/components/ValidatedFormInput";

const Register = () => {
    const navigate = useNavigate();
    const [isLoading, setIsLoading] = useState(false);
    const [submitError, setSubmitError] = useState("");

    const {
        register,
        handleSubmit,
        watch,
        formState: { errors, isValid, touchedFields }
    } = useForm({
        mode: "all",
        defaultValues: {
            email: "",
            firstName: "",
            lastName: "",
            password: "",
            confirmPassword: ""
        }
    });

    const onSubmit = async (data) => {
        setIsLoading(true);
        setSubmitError("");
        await handleRegister(
            {
                email: data.email,
                firstName: data.firstName,
                lastName: data.lastName,
                password: data.password,
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
                onSubmit={handleSubmit(onSubmit)}
                submitError={submitError}
                submitButtonText="Sign Up"
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

                <div className="row">
                    <div className="col">
                        <ValidatedFormInput
                            label="First Name"
                            name="firstName"
                            type="text"
                            registration={register("firstName", {
                                required: "Please enter valid first name",
                                pattern: {
                                    value: AppConstants.VALID_NAME_REGEX,
                                    message: "Please enter valid first name"
                                }
                            })}
                            errors={errors}
                            touchedFields={touchedFields}
                        />
                    </div>
                    <div className="col">
                        <ValidatedFormInput
                            label="Last Name"
                            name="lastName"
                            type="text"
                            registration={register("lastName", {
                                required: "Please enter valid last name",
                                pattern: {
                                    value: AppConstants.VALID_NAME_REGEX,
                                    message: "Please enter valid last name"
                                }
                            })}
                            errors={errors}
                            touchedFields={touchedFields}
                        />
                    </div>
                </div>
                <div className="row">
                    <div className="col">
                        <ValidatedFormInput
                            label="Password"
                            name="password"
                            type="password"
                            registration={register("password", {
                                required: "Password must contain at least 8 characters",
                                minLength: {
                                    value: 8,
                                    message: "Password must contain at least 8 characters"
                                }
                            })}
                            errors={errors}
                            touchedFields={touchedFields}
                        />
                    </div>
                    <div className="col">
                        <ValidatedFormInput
                            label="Confirm Password"
                            name="confirmPassword"
                            type="password"
                            registration={register("confirmPassword", {
                                required: "Passwords don't match",
                                validate: value => value === watch("password") || "Passwords don't match"
                            })}
                            errors={errors}
                            touchedFields={touchedFields}
                        />
                    </div>
                </div>
            </AuthForm>
        </AuthLayout>
    );
};

export default Register;