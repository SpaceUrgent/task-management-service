import React, {useState, useEffect} from "react";
import AppConstants from "../../AppConstants.ts";
import {AuthClient, RegisterError} from "../api/AuthClient.ts";
import AuthLayout from "../components/AuthLayout";
import AuthForm from "../components/AuthForm";
import ValidatedInput from "../../common/components/ValidatedInput";
import {Link, useNavigate} from "react-router-dom";


const Register = () =>  {
    const authClient = AuthClient.getInstance();
    const navigate = useNavigate();

    const [email, setEmail] = useState('');
    const [emailIsValid, setEmailIsValid] = useState();
    const [showEmailError, setShowEmailError] = useState(false);

    const [firstName, setFirstName] = useState('');
    const [firstNameIsValid, setFirstNameIsValid] = useState(false);
    const [showFirstNameError, setShowFirstNameError] = useState(false);

    const [lastName, setLastName] = useState('');
    const [lastNameIsValid, setLastNameIsValid] = useState(false);
    const [showLastNameError, setShowLastNameError] = useState(false);

    const [password, setPassword] = useState('');
    const [passwordIsValid, setPasswordIsValid] = useState(false);
    const [showPasswordError, setShowPasswordError] = useState(false);

    const [confirmPassword, setConfirmPassword] = useState('');
    const [passwordMatch, setPasswordMatch] = useState(false);
    const [showConfirmPasswordError, setShowConfirmPasswordError] = useState(false);

    const [submitError, setSubmitError] = useState('');

    useEffect(() => {
        setEmailIsValid(email && email.match(AppConstants.VALID_EMAIL_REGEX))
    }, [email])

    useEffect(() => {
        const isValidName = (value) => {
            return  value && AppConstants.VALID_NAME_REGEX.test(value);
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
            await authClient.registerUser({
                email: email,
                firstName: firstName,
                lastName: lastName,
                password: password,
            });
            navigate("/login", {replace: true});
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
        <AuthLayout>
            <AuthForm
                title="Create an account"
                onSubmit={submitRegister}
            >
                <ValidatedInput
                    id="email"
                    type="email"
                    name="Email"
                    placeholder="username@domain.com"
                    onChange={(value) => setEmail(value)}
                    value={email}
                    onBlur={() => setShowEmailError(true)}
                    isValid={emailIsValid}
                    errorMessage="Please enter valid email address"
                    showError={showEmailError}
                    required={true}
                />
                <div className="row">
                    <div className="col">
                        <ValidatedInput
                            id="firstName"
                            type="text"
                            name="First Name"
                            value={firstName}
                            onChange={(value) => setFirstName(value)}
                            onBlur={() => setShowFirstNameError(true)}
                            errorMessage="Please enter valid first name"
                            isValid={firstNameIsValid}
                            showError={showFirstNameError}
                            required={true}
                        />
                        <ValidatedInput
                            id="lastName"
                            type="text"
                            name="Last Name"
                            value={lastName}
                            onChange={(value) => setLastName(value)}
                            onBlur={() => setShowLastNameError(true)}
                            errorMessage="Please enter valid last name"
                            isValid={lastNameIsValid}
                            showError={showLastNameError}
                            required={true}
                        />
                    </div>
                </div>
                <div className="row">
                    <div className="col">
                        <ValidatedInput
                            id="password"
                            type="password"
                            name="Password"
                            value={password}
                            onChange={(value) => setPassword(value)}
                            onBlur={() => setShowPasswordError(false)}
                            errorMessage="Password must contain at least 8 characters"
                            isValid={passwordIsValid}
                            showError={showPasswordError}
                            required={true}
                        />
                    </div>
                    <div className="col">
                        <ValidatedInput
                            id="confirmPassword"
                            type="password"
                            name="Confirm Password"
                            value={confirmPassword}
                            onChange={(value) => setConfirmPassword(value)}
                            onBlur={() => setShowConfirmPasswordError(true)}
                            errorMessage="Confirm doesn't match password"
                            isValid={passwordMatch && password}
                            showError={showConfirmPasswordError}
                            required={true}
                        />
                    </div>
                </div>
                <p/>
                <div className="text-center">
                    <button className="btn btn-primary mt-2 w-50">Sign Up</button>
                    <p/>
                    {submitError && <span className="text-danger span-warning small">{submitError}</span>}
                    <p className="m-1">
                        <a>Already have an account? </a>
                        <Link to={'/login'} replace={true}>Login</Link>
                    </p>
                </div>
            </AuthForm>
        </AuthLayout>
    )
}

export default Register;