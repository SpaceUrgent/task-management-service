import React, {useEffect, useState} from "react";
import {AuthClient, LoginError} from "../api/AuthClient.ts";
import {useAuth} from "../../common/contexts/AuthContext";
import {Link, useNavigate} from "react-router-dom";
import AppConstants from "../../AppConstants.ts";
import ValidatedInput from "../../common/components/ValidatedInput";
import AuthLayout from "../components/AuthLayout";
import AuthForm from "../components/AuthForm";
// import {ProjectClient} from "../../api/ProjectClient.ts";

const Login = () => {

    const authClient = AuthClient.getInstance();
    const { login, logout } = useAuth();
    const navigate = useNavigate();

    const [email, setEmail] = useState("");
    const [emailIsValid, setEmailIsValid] = useState(false);
    const [showEmailError, setShowEmailError] = useState(false);

    const [password, setPassword] = useState("");
    const [passwordIsValid, setPasswordIsValid] = useState(false);
    const [showPasswordError, setShowPasswordError] = useState(false);

    const [submitError, setSubmitError] = useState("");

    useEffect(() => {
        setEmailIsValid(email && email.match(AppConstants.VALID_EMAIL_REGEX))
    }, [email]);

    useEffect(() => {
        setPasswordIsValid(!!password);
    }, [password]);

    const submitLogin = async (e) => {
        e.preventDefault();

        if (!inputsAreValid()) {
            return;
        }
        try {
            await authClient.login({email: email, password: password});
            login();
            // ProjectClient.getInstance().setOnUnauthorized(logout);
            navigate("/projects", {replace: true});
        } catch (error) {
            console.log(error);
            const submitError = error instanceof LoginError ? error.message : 'Failed to login. Please try again later';
            setSubmitError(submitError);
        }
    }

    const inputsAreValid = () => {
        return emailIsValid && passwordIsValid;
    }

    return(
        <AuthLayout>
            <AuthForm
                onSubmit={submitLogin}
                title="Sign In"
            >
                <ValidatedInput
                    id="email"
                    name="Email"
                    type="email"
                    placeholder="username@domain.com"
                    onChange={(value) => setEmail(value)}
                    isValid={emailIsValid}
                    onBlur={() => setShowEmailError(true)}
                    errorMessage="Please enter valid email address"
                    showError={showEmailError}
                    required={true}
                />
                <ValidatedInput
                    id="password"
                    name="Password"
                    type="password"
                    onChange={(value) => setPassword(value)}
                    isValid={passwordIsValid}
                    onBlur={() => setShowPasswordError(true)}
                    errorMessage="Please enter password"
                    showError={showPasswordError}
                    required={true}
                />
                <button className="btn btn-primary mt-2">Sign In</button>
                {submitError && <span className="text-danger span-warning">{submitError}</span>}
                <div>
                    <p className="m-1">
                        <a>Don't have an account? </a>
                        <Link to={'/register'} replace={true}>Register</Link>
                    </p>
                </div>
            </AuthForm>
        </AuthLayout>
    )
}

export default Login;