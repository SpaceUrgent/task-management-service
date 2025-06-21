import { AuthClient, LoginError, RegisterError } from '../api/AuthClient.ts';
import AppConstants from '../../AppConstants.ts';

export const handleLogin = async (credentials, onSuccess, onError) => {
    const authClient = AuthClient.getInstance();
    
    try {
        await authClient.login(credentials);
        onSuccess();
    } catch (error) {
        console.error('Login error:', error);
        const errorMessage = error instanceof LoginError 
            ? error.message 
            : 'Failed to login. Please try again later';
        onError(errorMessage);
    }
};

export const handleRegister = async (userData, onSuccess, onError) => {
    const authClient = AuthClient.getInstance();
    
    try {
        await authClient.registerUser(userData);
        onSuccess();
    } catch (error) {
        console.error('Registration error:', error);
        const errorMessage = error instanceof RegisterError 
            ? error.message 
            : AppConstants.DEFAULT_REGISTER_ERROR_MESSAGE;
        onError(errorMessage);
    }
};

export const createAuthLink = (text, linkText, to) => (
    <p className="m-1">
        <span>{text} </span>
        <a href={to} className="text-decoration-none">{linkText}</a>
    </p>
); 