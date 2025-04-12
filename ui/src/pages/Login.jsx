import {React, useEffect, useState} from "react";
import {AuthClient, LoginError} from "../api/AuthClient.ts";

const Login = () => {
    const VALID_EMAIL_REGEX = /^[a-z0-9-_]+@[a-z0-9]+\.[a-z0-9]+$/;

    const authClient = AuthClient.getInstance();

    const [email, setEmail] = useState("");
    const [emailIsValid, setEmailIsValid] = useState(false);
    const [emailWasFocused, setEmailWasFocused] = useState(false);

    const [password, setPassword] = useState("");
    const [passwordIsValid, setPasswordIsValid] = useState(false);
    const [passwordWasFocused, setPasswordWasFocused] = useState(false);

    const [submitError, setSubmitError] = useState("");

    useEffect(() => {
        setEmailIsValid(VALID_EMAIL_REGEX.test(email))
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
            await authClient.login({email: email, password: password})
        } catch (error) {
            const submitError = error instanceof LoginError
                ? error.message : 'Failed to login. Please try again later';
            setSubmitError(submitError);
        }
    }

    const inputsAreValid = () => {
        return emailIsValid && passwordIsValid;
    }

    return(
        <section
            className="row m-0 align-content-center justify-content-center"
            style={{
                backgroundColor: '#c8c8ff',
                height: '100vh',
                width: '100vw'
            }}
        >
            <div className="card p-2 p-3 text-center shadow p-3 mb-5 bg-body-tertiary rounded" style={{ width: '450px' }}>
                <h4 className="p-1">Sign in</h4>
                <form onSubmit={submitLogin}>
                    <div className="mb-1 text-start">
                        <label className="label form-label mb-0" htmlFor="email">Email</label>
                        <input
                            className={`form-control ${emailIsValid ? "is-valid" : emailWasFocused && "is-invalid"}`}
                            id="email"
                            type="email"
                            name="email"
                            placeholder="username@domain.com"
                            onChange={(e) => setEmail(e.target.value)}
                            value={email}
                            onBlur={() => setEmailWasFocused(true)}
                            required
                        />
                        {!emailIsValid && emailWasFocused &&
                            <span className="text-danger span-warning">Please enter valid email address</span>
                        }
                    </div>
                    <div className="col">
                        <div className="mb-1 text-start">
                            <label className="label form-label mb-0" htmlFor="password">Password</label>
                            <input
                                className={`form-control ${passwordIsValid ? "" : passwordWasFocused &&  "is-invalid"}`}
                                id="password"
                                type="password"
                                name="password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                onBlur={() => setPasswordWasFocused(true)}
                                required
                            />
                            {!passwordIsValid && passwordWasFocused &&
                                <span className="text-danger span-warning">Password is required</span>
                            }
                        </div>
                    </div>
                    <button className="btn btn-primary mt-2">Sign In</button>
                    {submitError && <span className="text-danger span-warning">{submitError}</span>}
                </form>
                <div>
                    <p className="m-1">Don't have an account? Register</p>
                </div>
            </div>
        </section>
    )
}

export default Login;