import React, {useState, useEffect} from "react";
import './Register.css';
import AppConstants from "../AppConstants.ts";
import {AuthClient, RegisterError} from "../api/AuthClient.ts";


const Register = () =>  {
    const VALID_EMAIL_REGEX = /^[a-z0-9-_]+@[a-z0-9]+\.[a-z0-9]+$/;
    const VALID_NAME_REGEX = /^[A-Za-z'-]{2,49}$/;

    const [email, setEmail] = useState('');
    const [emailIsValid, setEmailIsValid] = useState();
    const [emailWasFocused, setEmailWasFocused] = useState(false);

    const [firstName, setFirstName] = useState('');
    const [firstNameIsValid, setFirstNameIsValid] = useState(false);
    const [firstNameWasFocused, setFirstNameWasFocused] = useState(false);

    const [lastName, setLastName] = useState('');
    const [lastNameIsValid, setLastNameIsValid] = useState(false);
    const [lastNameWasFocused, setLastNameWasFocused] = useState(false);

    const [password, setPassword] = useState('');
    const [passwordIsValid, setPasswordIsValid] = useState(false);
    const [passwordWasFocused, setPasswordWasFocused] = useState(false);

    const [confirmPassword, setConfirmPassword] = useState('');
    const [confirmPasswordWasFocused, setConfirmPasswordWasFocused] = useState(false);
    const [passwordMatch, setPasswordMatch] = useState(false);

    const [submitError, setSubmitError] = useState('');

    useEffect(() => {
        setEmailIsValid(VALID_EMAIL_REGEX.test(email))
    }, [email])

    useEffect(() => {
        const isValidName = (value) => {
            return  value && VALID_NAME_REGEX.test(value);
        }
        setFirstNameIsValid(isValidName(firstName))
        setLastNameIsValid(isValidName(lastName))
    }, [firstName, lastName])

    useEffect(() => {
        function validPassword(value) {
            return value && value.length >= 8;
        }
        setPasswordIsValid(validPassword(password));
        setPasswordMatch(password === confirmPassword);
    }, [password, confirmPassword]);

    const submitRegister = async (e) => {
        e.preventDefault();

        if (!inputsAreValid()) return;

        try {
            await AuthClient.getInstance().registerUser({
                email: email,
                firstName: firstName,
                lastName: lastName,
                password: password,
            });
        } catch (error) {
            console.error(error);
            setSubmitError(error instanceof RegisterError ? error.message : AppConstants.DEFAULT_REGISTER_ERROR_MESSAGE);
        }
    }

    const inputsAreValid = () => {
        return emailIsValid
            && firstNameIsValid
            && firstNameIsValid
            && lastNameIsValid
            && passwordIsValid
            && passwordMatch;
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
                <h4 className="p-1">Create an account</h4>
                <form onSubmit={submitRegister}>
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
                    <div className="row">
                        <div className="col">
                            <div className="mb-1 text-start">
                                <label className="label form-label mb-0" htmlFor="firstName">First Name</label>
                                <input
                                    className={`form-control mb-0 ${firstNameIsValid ? "is-valid" : firstNameWasFocused &&  "is-invalid"}`}
                                    id="firstName"
                                    type="firstName"
                                    name="firstName"
                                    value={firstName}
                                    onChange={(e) => setFirstName(e.target.value)}
                                    onBlur={() => setFirstNameWasFocused(true)}
                                    required
                                />
                                {!firstNameIsValid && firstNameWasFocused &&
                                    <span className="text-danger m-0 span-warning">Please provide a valid first name</span>
                                }
                            </div>
                        </div>
                        <div className="col">
                            <div className="mb-1 text-start">
                                <label className="label form-label mb-0" htmlFor="lastName">Last Name</label>
                                <input
                                    className={`form-control ${lastNameIsValid ? "is-valid" : lastNameWasFocused &&  "is-invalid"}`}
                                    id="lastName"
                                    type="lastName"
                                    name="lastName"
                                    value={lastName}
                                    onChange={(e) => setLastName(e.target.value)}
                                    onBlur={() => setLastNameWasFocused(true)}
                                    required
                                />
                                {!lastNameIsValid && lastNameWasFocused &&
                                    <span className="text-danger span-warning">Please provide a valid last name</span>
                                }
                            </div>
                        </div>
                    </div>
                    <div className="row">
                        <div className="col">
                            <div className="mb-1 text-start">
                                <label className="label form-label mb-0" htmlFor="password">Password</label>
                                <input
                                    className={`form-control ${passwordIsValid ? "is-valid" : passwordWasFocused &&  "is-invalid"}`}
                                    id="password"
                                    type="password"
                                    name="password"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    onBlur={() => setPasswordWasFocused(true)}
                                    required
                                />
                                {!passwordIsValid && passwordWasFocused &&
                                    <span className="text-danger span-warning">Password must contain at least 8 characters</span>
                                }
                            </div>
                        </div>
                        <div className="col">
                            <div className="mb-1 text-start">
                                <label className="label form-label mb-0" htmlFor="confirmPassword">Confirm Password</label>
                                <input
                                    className={`form-control ${passwordMatch ? confirmPassword && "is-valid" : confirmPasswordWasFocused && "is-invalid"}`}
                                    id="confirmPassword"
                                    type="password"
                                    name="confirmPassword"
                                    value={confirmPassword}
                                    onChange={(e) => setConfirmPassword(e.target.value)}
                                    onBlur={() => setConfirmPasswordWasFocused(true)}
                                    required
                                />
                                {!passwordMatch && confirmPasswordWasFocused &&
                                    <span className="text-danger span-warning">Confirm doesn't match password</span>
                                }
                            </div>
                        </div>
                    </div>
                    <button className="btn btn-primary mt-2">Sign Up</button>
                    {submitError && <span className="text-danger span-warning">{submitError}</span>}
                </form>
                <div>
                    <p className="m-1">Already have an account? Sign in</p>
                </div>
            </div>
        </section>
    )
}

export default Register;