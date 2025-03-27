import axios, {AxiosResponse} from "axios";

const BASE_URL = "http://localhost:9090/api";

class AuthClient {
    private static instance: AuthClient;
    private baseUrl: string;

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
            response = await axios.post(`${this.baseUrl}/users/register`, request);
        } catch (error) {
            this.raiseRegisterFailed();
        }
        this.validateRegisterResponse(response);
    }

    private validateRegisterResponse(response: AxiosResponse<any, any>) {
        const status = response.status;
        if (status === 201) return;
        if (status === 400 && response.data?.message) {
            throw new RegisterError(response.data.message);
        }
        this.raiseRegisterFailed();
    }

    private raiseRegisterFailed() {
        throw new RegisterError('Failed to register. Please try again later');
    }
}

interface RegisterRequest {
    email: string;
    firstName: string;
    lastName: string;
    password: string;
}

class RegisterError extends Error {

    constructor(message: string) {
        super(message);
    }
}

