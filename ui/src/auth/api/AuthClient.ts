import axios, {AxiosResponse} from "axios";

const BASE_URL = "http://localhost:8080/api";

export class AuthClient {
    private static instance: AuthClient;
    private readonly baseUrl: string;

    private constructor(baseUrl: string) {
        this.baseUrl = baseUrl;
    }

    public static getInstance(): AuthClient {
        if (!AuthClient.instance) {
            AuthClient.instance = new AuthClient(BASE_URL);
        }
        return AuthClient.instance;
    }

    async registerUser(request: RegisterRequest): Promise<void> {
        let response: AxiosResponse<any, any>;


        try {
            const urlEncodedRequest = this.toUrlEncodedRequest(request);
            response = await axios.post(
                `${this.baseUrl}/users/register`,
                urlEncodedRequest,
                {
                    headers : { "Content-Type": "application/x-www-form-urlencoded" },
                }
            );
            console.log(response);
        } catch (error) {
            this.raiseRegisterFailed();
        }
        this.validateRegisterResponse(response);
    }

    async login(request: LoginRequest): Promise<void> {
        let response: AxiosResponse<any, any>;

        try {
            const urlEncodedRequest = this.toUrlEncodedRequest(request);
            response = await axios.post(
                `${this.baseUrl}/auth/login`,
                urlEncodedRequest,
                {
                    withCredentials: true,
                    headers : { "Content-Type": "application/x-www-form-urlencoded" }
                }
            )
        } catch (error) {
            this.raiseLoginFailed();
        }
        this.validateLoginResponse(response)
    }

    private validateRegisterResponse(response: AxiosResponse<any, any>) {
        const status = response.status;
        if (status === 201) return;
        if (status === 400 && response.data?.message) {
            throw new RegisterError(response.data.message);
        }
        this.raiseRegisterFailed();
    }

    private validateLoginResponse(response: AxiosResponse<any, any>) {
        const status = response.status;
        if (status === 200) return;
        if (response.status === 401) {
            throw new LoginError('Email or password is incorrect');
        }
        this.raiseLoginFailed();
    }

    private toUrlEncodedRequest(request: Object): string {
        const urlEncodedParams =  new URLSearchParams();
        for (const key in request) {
            urlEncodedParams.append(key, request[key]);
        }
        return urlEncodedParams.toString();
    }

    private raiseRegisterFailed() {
        throw new RegisterError('Failed to register. Please try again later');
    }

    private raiseLoginFailed() {
        throw new LoginError('Failed to login. Please try again later');
    }
}

interface RegisterRequest {
    email: string;
    firstName: string;
    lastName: string;
    password: string;
}

interface LoginRequest {
    email: string;
    password: string;
}

export class RegisterError extends Error {

    constructor(message: string) {
        super(message);
    }
}

export class LoginError extends Error {

    constructor(message: string) {
        super(message);
    }
}